package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import static org.jdiameter.api.PeerState.DOWN;
import static org.jdiameter.api.PeerState.INITIAL;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.server.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

public class PeerImpl extends org.jdiameter.client.impl.controller.PeerImpl implements IPeer {

  private static final Logger logger = LoggerFactory.getLogger(org.jdiameter.server.impl.PeerImpl.class);

    // External references
    private MutablePeerTableImpl peerTable;
    protected Set<URI> predefinedPeerTable;
    protected INetwork network;
    protected IOverloadManager ovrManager;
    protected ISessionFactory sessionFactory;
    // Internal parameters and members
    protected boolean isDuplicateProtection;
    protected boolean isAttemptConnection;
    protected boolean isElection = true;
    protected Map<String, IConnection> incConnections;

    /**
     *  Create instance of class
     */
    public PeerImpl(int rating, URI remotePeer, String ip, String portRange, boolean attCnn, IConnection connection,
          MutablePeerTableImpl peerTable, IMetaData metaData, Configuration config, Configuration peerConfig,
          ISessionFactory sessionFactory, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
          IStatisticFactory statisticFactory, IConcurrentFactory concurrentFactory,
          IMessageParser parser, INetwork nWork, IOverloadManager oManager)
          throws InternalException, TransportException {
        super(peerTable, rating, remotePeer, ip, portRange, metaData, config, peerConfig, fsmFactory, trFactory, parser,
        statisticFactory, concurrentFactory, connection);
        // Create specific action context
        this.peerTable = peerTable;
        this.isDuplicateProtection = this.peerTable.isDuplicateProtection();
        this.sessionFactory = sessionFactory;
        this.isAttemptConnection = attCnn;
        this.incConnections = this.peerTable.getIncConnections();
        this.predefinedPeerTable = this.peerTable.getPredefinedPeerTable();
        this.network = nWork;
        this.ovrManager = oManager;
        // Append fsm statistic
        if (this.fsm instanceof IStateMachine) {
          this.statistic.appendCounter(((IStateMachine) fsm).getStatistic().getRecords());
        }
    }

    protected void preProcessRequest(IMessage message) {
      if (isDuplicateProtection && message.isRequest()) {
        peerTable.saveToDuplicate(message.getDuplicationKey(), message);
      }
    }

    public boolean isAttemptConnection() {
        return isAttemptConnection; 
    }

    public IContext getContext() {
        return new LocalActionConext(); 
    }

    public IConnection getConnection() {
        return connection;
    }

    public void addIncomingConnection(IConnection conn) {
        PeerState state = fsm.getState(PeerState.class);
        if (DOWN  ==  state || INITIAL == state) {
            conn.addConnectionListener(connListener);
            logger.debug("Append external connection {}", conn.getKey());
        } else {
            logger.debug("Releasing connection {}", conn.getKey());
            incConnections.remove(conn.getKey());
            try {
                conn.release();
            } catch (IOException e) {
              logger.debug("Can not close external connection", e);
            } finally {
              logger.debug("Close external connection");
            }
        }
    }

    public void setElection(boolean isElection) {
        this.isElection = isElection;
    }

    public void notifyOvrManager(IOverloadManager ovrManager) {
        ovrManager.changeNotification(0, getUri(), fsm.getQueueInfo());
    }

    public String toString() {
      return uri.toString();
    }

    protected class LocalActionConext extends ActionContext {

        public void sendCeaMessage(int resultCode, Message cer,  String errMessage) throws TransportException, OverloadException {
            logger.debug("Send CEA message");

            IMessage message = parser.createEmptyMessage(Message.CAPABILITIES_EXCHANGE_ANSWER, 0);
            message.setRequest(false);
            message.setHopByHopIdentifier(cer.getHopByHopIdentifier());
            message.setEndToEndIdentifier(cer.getEndToEndIdentifier());
            message.getAvps().addAvp(Avp.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
            message.getAvps().addAvp(Avp.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
            for (InetAddress ia : metaData.getLocalPeer().getIPAddresses()) {
                message.getAvps().addAvp(Avp.HOST_IP_ADDRESS, ia, true, false);
            }
            message.getAvps().addAvp(Avp.VENDOR_ID, metaData.getLocalPeer().getVendorId(), true, false, true);

            for (ApplicationId appId: metaData.getLocalPeer().getCommonApplications()) {
              addAppId(appId, message);
            }

            message.getAvps().addAvp(Avp.PRODUCT_NAME,  metaData.getLocalPeer().getProductName(), false);
            message.getAvps().addAvp(Avp.RESULT_CODE, resultCode, true, false, true);
            message.getAvps().addAvp(Avp.FIRMWARE_REVISION, metaData.getLocalPeer().getFirmware(), true);
            if (errMessage != null) {
                message.getAvps().addAvp(Avp.ERROR_MESSAGE, errMessage, false);
            }
            sendMessage(message);
        }

        public int processCerMessage(String key, IMessage message) {
          logger.debug("Processing CER");
          
            int resultCode = ResultCode.SUCCESS;
            try {
                if (connection == null || !connection.isConnected()) {
                  connection = incConnections.get(key);
                }
                // Process cer
                Set<ApplicationId> newAppId = getCommonApplicationIds(message);
                if (newAppId.isEmpty()) {
                    logger.debug("Processing CER failed... no common application, message AppIps {}", message.getApplicationIdAvps());
                    return ResultCode.NO_COMMON_APPLICATION;
                }              
                // Handshake
                if (!connection.getKey().equals(key)) { // received cer by other connection
                    logger.debug("CER received by other connection {}", key);
                    
                    switch(fsm.getState(PeerState.class)) {
                        case DOWN:
                            resultCode = ResultCode.SUCCESS;
                            break;
                        case INITIAL:
                            boolean isLocalWin = false;
                            if (isElection)
                                try {
                                     isLocalWin = metaData.getLocalPeer().getUri().getFQDN().compareTo(
                                            message.getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString()
                                    ) <= 0;
                                } catch(Exception exc) {
                                    isLocalWin = true;
                                }
                                
                                logger.debug("local peer is win - {}", isLocalWin);
                                
                            resultCode = 0;
                            if (isLocalWin) {
                                IConnection c = incConnections.get(key);
                                c.remConnectionListener(connListener);
                                c.disconnect();
                                incConnections.remove(key);
                            } else {
                                connection.disconnect();  // close current connection and work with other connection
                                connection.remConnectionListener(connListener);
                                connection = incConnections.remove(key);
                                resultCode = ResultCode.SUCCESS;
                            }
                            break;
                    }
                } else {
                  logger.debug("CER received by current connection");
                    if (fsm.getState(PeerState.class).equals(INITIAL)) // received cer by current connection
                        resultCode = 0; // NOP
                    incConnections.remove(key);
                }
                if (resultCode == ResultCode.SUCCESS) {
                    commonApplications.clear();
                    commonApplications.addAll(newAppId);
                    fillIPAddressTable(message);
                }
            } catch (Exception exc) {
              logger.debug("Can not process CER", exc);
            }
            logger.debug("CER result {}", resultCode);
            
            return resultCode;
        }

        public boolean isRestoreConnection() {
            return isAttemptConnection;
        }

        public String getPeerDescription() {
            return PeerImpl.this.toString();
        }

        public boolean receiveMessage(IMessage request) {
            boolean isProcessed = super.receiveMessage(request);
            if (request.isRequest()) {
              if (!isProcessed) {
              statistic.getRecordByName(IStatistic.Counters.NetGenRejectedRequest.name()).dec();
            int resultCode = ResultCode.SUCCESS;
                ApplicationId appId = request.getSingleApplicationId();
                if (appId == null) {
                    resultCode = ResultCode.NO_COMMON_APPLICATION;
                }
                else {
                    NetworkReqListener listener = network.getListener(request);
                    if (listener != null) {
                        IMessage answer = null;
                        if (isDuplicateProtection) {
                            answer = peerTable.isDuplicate(request);
                        }
                        if (answer != null) {
                            answer.setProxiable(request.isProxiable());
                            answer.getAvps().removeAvp(Avp.PROXY_INFO);
                            for (Avp avp : request.getAvps().getAvps(Avp.PROXY_INFO)) {
                                answer.getAvps().addAvp(avp);
                            }
                            answer.setHopByHopIdentifier(request.getHopByHopIdentifier());
                            statistic.getRecordByName(IStatistic.Counters.SysGenResponse.name()).inc();
                            isProcessed = true;
                        } else {
                            if (ovrManager != null && ovrManager.isParenAppOverload(request.getSingleApplicationId())) {
                          	  logger.debug("Request {} skipped, because server application has overload", request);
                          	  resultCode = ResultCode.TOO_BUSY;
                            } else {
                                try {
                                    router.registerRequestRouteInfo(request);
                                    answer = (IMessage) listener.processRequest(request);
                                    if (isDuplicateProtection && answer != null) {
                                      peerTable.saveToDuplicate(request.getDuplicationKey(), answer);
                                    }
                                    isProcessed = true;
                                } catch (Exception exc) {
                                    resultCode = ResultCode.APPLICATION_UNSUPPORTED;
                                    logger.warn("Error during processing message by listener", exc);
                                }
                            }
                        }
                        try {
                          if (answer != null && isProcessed) {
                            sendMessage(answer);
                            statistic.getRecordByName(IStatistic.Counters.AppGenResponse.name()).inc();
                          }
                        } catch (Exception e) {
                          logger.warn("Can not send answer", e);
                        }
                    } else {
                        logger.warn("Received message for unsupported Application-Id {}", appId);
                        resultCode = ResultCode.APPLICATION_UNSUPPORTED;
                    }
                }
            //
             if (!isProcessed) { // send error answer
                request.setRequest(false);
                request.setError(true);
                request.getAvps().removeAvp(Avp.RESULT_CODE);
                request.getAvps().addAvp(Avp.RESULT_CODE, resultCode, true, false, true);
                try {
                    sendMessage(request);
                    statistic.getRecordByName(IStatistic.Counters.SysGenResponse.name()).inc();
                } catch (Exception e) {
                  logger.debug("Can not send answer", e);
                }
                statistic.getRecordByName(IStatistic.Counters.NetGenRejectedRequest.name()).inc();
                } else {
                statistic.getRecordByName(IStatistic.Counters.NetGenRequest.name()).inc();

        }
              } else {


                    }
                }
            return isProcessed;
        }
    }

}
