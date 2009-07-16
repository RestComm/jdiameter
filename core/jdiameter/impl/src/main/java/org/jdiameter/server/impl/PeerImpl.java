package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import static org.jdiameter.api.PeerState.DOWN;
import static org.jdiameter.api.PeerState.INITIAL;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.server.api.*;
import static org.jdiameter.server.impl.helpers.StatisticTypes.REQ_MESS_COUNTER;
import static org.jdiameter.server.impl.helpers.StatisticTypes.RESP_MESS_COUNTER;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;
import java.util.Map;
import java.util.logging.Level;

public class PeerImpl extends org.jdiameter.client.impl.controller.PeerImpl implements IPeer {

    // External references
    private MutablePeerTableImpl manager;
    protected Set<URI> predefinedPeerTable;
    protected INetwork network;
    protected IOverloadManager ovrManager;
    protected ISessionFactory factory;
    // Internal parameters and members
    protected boolean isDuplicateProtection;
    protected boolean isAttemptConnection;
    protected boolean isElection = true;
    protected Map<String, IConnection> incConnections;
    // Statistics
    protected IStatisticRecord reqStat = new StatisticRecordImpl("ReqCounter", "Request message counter", REQ_MESS_COUNTER);
    protected IStatisticRecord respStat = new StatisticRecordImpl("RespCounter", "Response message counter", RESP_MESS_COUNTER);
    protected IStatistic statistic = new StatisticImpl("Peer" , "Peer statistic", reqStat, respStat);

    /**
     *  Create instance of class
     *
     */
    public PeerImpl(MutablePeerTableImpl mgr, int rating, URI remotePeer, String ip, String portRange, IMetaData metaData, Configuration config,
                     Configuration peerConfig, ISessionFactory sessionFactory, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
                    IMessageParser parser, INetwork nWork, IOverloadManager oManager, boolean attCnn, IConnection connection) throws InternalException, TransportException {
        super(mgr, rating, remotePeer, ip, portRange, metaData, config, peerConfig, fsmFactory, trFactory, parser, connection);
        // Create specific action context
        manager = mgr;
        isDuplicateProtection = manager.isDuplicateProtection();
        factory = sessionFactory;
        isAttemptConnection = attCnn; 
        incConnections = manager.getIncConnections();
        predefinedPeerTable = manager.getPredefinedPeerTable();
        network = nWork;
        ovrManager = oManager;
        // Append fsm statistic
        if (fsm instanceof IStateMachine)
            statistic.appendCounter(((IStateMachine)fsm).getStatistic().getRecords());
    }

    public Statistic getStatistic() {
        return statistic;
    }

    protected void preProcessRequest(IMessage message) {
        if ( isDuplicateProtection && message.isRequest() )
                manager.saveToDuplicate(message.getDuplicationKey(), message);
    }

    public boolean isAttemptConnection() {
        return isAttemptConnection; 
    }

    public IContext getContext() {
        return new LocalActionConext(); 
    }

    public IConnection getConnection() {
        return conn;
    }

    public void addIncomingConnection(IConnection conn) {
        PeerState state = fsm.getState(PeerState.class);
        if (DOWN  ==  state || INITIAL == state) {
            conn.addConnectionListener(connListener);
            if(logger.isLoggable(Level.FINE)) {
              logger.log(Level.FINE,"Append external connection " + conn.getKey());
            }
        } else {
            if(logger.isLoggable(Level.FINE)) {
              logger.log(Level.FINE, "Releasing connection " + conn.getKey());
            }
            incConnections.remove(conn.getKey());
            try {
                conn.release();
            } catch (IOException e) {
            	if(logger.isLoggable(Level.SEVERE))
            	{
            		logger.log(Level.SEVERE, "Can not close external connection", e);
            	}
            } finally {
            	if(logger.isLoggable(Level.INFO))
            	{
            		logger.log(Level.INFO, "Close external connection");
            	}
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
        return "Peer{" +
                "Uri=" + uri + "; State="+fsm.getState(PeerState.class).toString() + "}";
    }

    protected class LocalActionConext extends ActionContext {

        public void sendCeaMessage(int resultCode, Message cer,  String errMessage) throws TransportException, OverloadException {
        	if(logger.isLoggable(Level.FINEST))
        	{
        		logger.log(Level.FINEST, "Send CEA message");
        	}
            IMessage message = parser.createEmptyMessage( Message.CAPABILITIES_EXCHANGE_ANSWER, 0 );
            message.setRequest(false);
            message.setHopByHopIdentifier(cer.getHopByHopIdentifier());
            message.setEndToEndIdentifier(cer.getEndToEndIdentifier());
            message.getAvps().addAvp( Avp.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
            message.getAvps().addAvp( Avp.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
            for (InetAddress ia : metaData.getLocalPeer().getIPAddresses())
                message.getAvps().addAvp( Avp.HOST_IP_ADDRESS, ia, true, false);
            message.getAvps().addAvp( Avp.VENDOR_ID, metaData.getLocalPeer().getVendorId(), true, false, true );

            for (ApplicationId appId: metaData.getLocalPeer().getCommonApplications()) addAppId(appId, message);

            message.getAvps().addAvp( Avp.PRODUCT_NAME,  metaData.getLocalPeer().getProductName(), false);
            message.getAvps().addAvp( Avp.RESULT_CODE, resultCode, true, false, true);
            message.getAvps().addAvp( Avp.FIRMWARE_REVISION, metaData.getLocalPeer().getFirmware(), true );
            if (errMessage != null)
                message.getAvps().addAvp( Avp.ERROR_MESSAGE, errMessage, false);
            sendMessage(message);
        }

        public int processCerMessage(String key, IMessage message) {
        	if(logger.isLoggable(Level.FINE))
        	{
        		logger.fine("Processing CER");
        	}
            int resultCode = ResultCode.SUCCESS;
            try {
                if (conn == null || !conn.isConnected()) {
                    conn = incConnections.get(key);
                }
                // Process cer
                Set<ApplicationId> newAppId = getCommonApplicationIds(message);
                if (newAppId.isEmpty()) {
                	if(logger.isLoggable(Level.FINE))
                	{
                		logger.fine("Processing CER failed... no common application, message AppIps:" + message.getApplicationIdAvps());
                	}
                    return ResultCode.NO_COMMON_APPLICATION;
                }              
                // Handshake
                if ( !conn.getKey().equals(key) ) { // received cer by other connection
                	if(logger.isLoggable(Level.FINE))
                	{
                		logger.fine("CER received by other connection " + key);
                	}
                    switch( fsm.getState(PeerState.class) ) {
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
                                if(logger.isLoggable(Level.FINE))
                            	{
                                	logger.log(Level.FINE, "local peer is win - " + isLocalWin);
                            	}
                            resultCode = 0;
                            if ( isLocalWin ) {
                                IConnection c = incConnections.get(key);
                                c.remConnectionListener(connListener);
                                c.disconnect();
                                incConnections.remove(key);
                            } else {
                                conn.disconnect();  // close current connection and work with other connection
                                conn.remConnectionListener(connListener);
                                conn = incConnections.remove(key);
                                resultCode = ResultCode.SUCCESS;
                            }
                            break;
                    }
                } else {
                	if(logger.isLoggable(Level.FINE))
                	{
                		logger.fine("CER received by current connection");
                	}
                    if ( fsm.getState(PeerState.class).equals(INITIAL)) // received cer by current connection
                        resultCode = 0; // NOP
                    incConnections.remove(key);
                }
                if (resultCode == ResultCode.SUCCESS) {
                    commonApplications.clear();
                    commonApplications.addAll(newAppId);
                    fillIPAddressTable(message);
                }
            } catch(Exception exc) {
            	if(logger.isLoggable(Level.SEVERE))
            	{
            		logger.log(Level.SEVERE, "Can not process CER",exc);
            	}
            }
            if(logger.isLoggable(Level.FINE))
        	{
            	logger.fine("CER result " + resultCode);
        	}
            return resultCode;
        }

        public boolean isRestoreConnection() {
            return isAttemptConnection;
        }

        public String getPeerDescription() {
            return PeerImpl.this.toString();
        }

        public boolean receiveMessage(IMessage request) {
            if (request.isRequest()) reqStat.inc();
            boolean isProcessed = super.receiveMessage(request);
            int resultCode = ResultCode.SUCCESS;
            if (request.isRequest() && !isProcessed) {
                ApplicationId appId = request.getSingleApplicationId();
                if (appId == null) {
                    resultCode = ResultCode.NO_COMMON_APPLICATION;
                } else {
                    NetworkReqListener listener = network.getListener(request);
                    if (listener != null) {
                        IMessage answer = null;
                        if (isDuplicateProtection)
                            answer = manager.isDuplicate(request);
                        if (answer != null) {
                            answer.setProxiable(request.isProxiable());
                            answer.getAvps().removeAvp(Avp.PROXY_INFO);
                            for (Avp avp : request.getAvps().getAvps(Avp.PROXY_INFO))
                                answer.getAvps().addAvp(avp);
                            answer.setHopByHopIdentifier(request.getHopByHopIdentifier());
                        } else {
                            if (ovrManager != null && ovrManager.isParenAppOverload(request.getSingleApplicationId())) {
                            	if(logger.isLoggable(Level.INFO))
                            	{
                            		logger.log(Level.INFO, "Request " + request + " skipped, because server application has overload");
                            	}
                                answer = (IMessage)request.createAnswer(ResultCode.TOO_BUSY);
                            } else {
                                try {
                                    router.registerRequestRouteInfo( request );
                                    answer = (IMessage) listener.processRequest(request);
                                    if (isDuplicateProtection && answer != null)
                                        manager.saveToDuplicate(request.getDuplicationKey(), answer);
                                    isProcessed = true;
                                } catch (Exception exc) {
                                    resultCode = ResultCode.APPLICATION_UNSUPPORTED;
                                    if(logger.isLoggable(Level.SEVERE))
                                	{
                                    	logger.log(Level.SEVERE, "Error during processing message by listener", exc);
                                	}
                                }
                            }
                        }
                        try {
                            if (answer != null)
                                sendMessage(answer);
                        } catch (Exception e) {
                        	if(logger.isLoggable(Level.SEVERE))
                        	{
                        		logger.log(Level.SEVERE, "Can not send answer", e);
                        	}
                        }
                    } else {
                    	if(logger.isLoggable(Level.WARNING))
                    	{
                    		logger.log(Level.WARNING, "Received message for unsupported Application-Id: " + appId);
                    	}
                        resultCode = ResultCode.APPLICATION_UNSUPPORTED;
                    }
                }
            }
            //
            if (resultCode != ResultCode.SUCCESS) {
                request.setRequest(false);
                request.setError(true);
                request.getAvps().removeAvp(Avp.RESULT_CODE);
                request.getAvps().addAvp(Avp.RESULT_CODE, resultCode, true, false, true);
                try {
                    sendMessage(request);
                } catch (Exception e) {
                	if(logger.isLoggable(Level.SEVERE))
                	{
                		logger.log(Level.SEVERE, "Can not send answer", e);
                	}
                }
            }
            return isProcessed;
        }


        public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
            boolean rc = super.sendMessage(message);
            if (rc) {
                if (statistic.isEnable()) {
                    int cc = message.getCommandCode();
                    if (cc != IMessage.CAPABILITIES_EXCHANGE_REQUEST &&
                        cc != IMessage.DEVICE_WATCHDOG_REQUEST &&
                        cc != IMessage.DISCONNECT_PEER_REQUEST) {
                        if (message.isRequest())
                            reqStat.inc();
                        else
                            respStat.inc();
                    }
                }
            }
            return rc;
        }
    }

}
