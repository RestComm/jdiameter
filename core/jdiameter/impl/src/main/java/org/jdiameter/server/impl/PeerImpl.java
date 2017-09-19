 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.server.impl;

import static org.jdiameter.api.PeerState.DOWN;
import static org.jdiameter.api.PeerState.INITIAL;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.jdiameter.server.api.IFsmFactory;
import org.jdiameter.server.api.INetwork;
import org.jdiameter.server.api.IOverloadManager;
import org.jdiameter.server.api.IPeer;
import org.jdiameter.server.api.IStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class PeerImpl extends org.jdiameter.client.impl.controller.PeerImpl implements IPeer {

  private static final Logger logger = LoggerFactory.getLogger(org.jdiameter.server.impl.PeerImpl.class);

  // External references
  private MutablePeerTableImpl peerTable;
  protected Set<String> predefinedPeerTable;
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
      IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory,
      IMessageParser parser, INetwork nWork, IOverloadManager oManager, final ISessionDatasource sessionDataSource)
          throws InternalException, TransportException {
    super(peerTable, rating, remotePeer, ip, portRange, metaData, config, peerConfig, fsmFactory, trFactory, parser,
        statisticFactory, concurrentFactory, connection, sessionDataSource);
    // Create specific action context
    this.peerTable = peerTable;
    this.isDuplicateProtection = this.peerTable.isDuplicateProtection();
    this.sessionFactory = sessionFactory;
    this.isAttemptConnection = attCnn;
    this.incConnections = this.peerTable.getIncConnections();
    this.predefinedPeerTable = this.peerTable.getPredefinedPeerTable();
    this.network = nWork;
    this.ovrManager = oManager;
  }

  @Override
  protected void createPeerStatistics() {
    super.createPeerStatistics();

    // Append fsm statistic
    if (this.fsm instanceof IStateMachine) {
      StatisticRecord[] records = ((IStateMachine) fsm).getStatistic().getRecords();
      IStatisticRecord[] recordsArray = new IStatisticRecord[records.length];
      int count = 0;
      for (StatisticRecord st: records) {
        recordsArray[count++] = (IStatisticRecord) st;
      }
      this.statistic.appendCounter(recordsArray);
    }
  }

  protected void preProcessRequest(IMessage message) {
    // ammendonca: this is non-sense, we don't want to save requests
    //if (isDuplicateProtection && message.isRequest()) {
    //  peerTable.saveToDuplicate(message.getDuplicationKey(), message);
    //}
  }

  @Override
  public boolean isAttemptConnection() {
    return isAttemptConnection;
  }

  @Override
  public IContext getContext() {
    return new LocalActionConext();
  }

  @Override
  public IConnection getConnection() {
    return connection;
  }

  @Override
  public void addIncomingConnection(IConnection conn) {
    PeerState state = fsm.getState(PeerState.class);
    if (DOWN  ==  state || INITIAL == state) {
      conn.addConnectionListener(connListener);
      // ammendonca: if we are receiving a new connection in such state, we may want to make it primary, right?
      this.connection = conn;
      logger.debug("Append external connection [{}]", conn.getKey());
    }
    else {
      logger.debug("Releasing connection [{}]", conn.getKey());
      incConnections.remove(conn.getKey());
      try {
        conn.release();
      }
      catch (IOException e) {
        logger.debug("Can not close external connection", e);
      }
      finally {
        logger.debug("Close external connection");
      }
    }
  }

  @Override
  public void setElection(boolean isElection) {
    this.isElection = isElection;
  }

  @Override
  public void notifyOvrManager(IOverloadManager ovrManager) {
    ovrManager.changeNotification(0, getUri(), fsm.getQueueInfo());
  }

  @Override
  public String toString() {
    if (fsm != null) {
      return "SPeer{" + "Uri=" + uri + "; State=" + fsm.getState(PeerState.class) + "; con=" + connection + "; incCon" + incConnections + " }";
    }
    return "SPeer{" + "Uri=" + uri + "; State=" + fsm + "; con=" + connection + "; incCon" + incConnections + " }";
  }

  protected class LocalActionConext extends ActionContext {

    @Override
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

    @Override
    public int processCerMessage(String key, IMessage message) {
      logger.debug("Processing CER");

      int resultCode = ResultCode.SUCCESS;
      try {
        if (connection == null || !connection.isConnected()) {
          if (logger.isDebugEnabled()) {
            logger.debug("Connection is null or not connected. Looking for one in incConnections with key [{}]. Here are the incConnections :", key);
            for (String c : incConnections.keySet()) {
              logger.debug(c);
            }
          }
          connection = incConnections.get(key);
        }
        // Process cer
        Set<ApplicationId> newAppId = getCommonApplicationIds(message);
        if (newAppId.isEmpty()) {
          if(logger.isWarnEnabled()) {
            logger.warn("Processing CER failed, no common application. Message AppIds [{}]", message.getApplicationIdAvps());
          }
          return ResultCode.NO_COMMON_APPLICATION;
        }
        // Handshake
        if (!connection.getKey().equals(key)) { // received cer by other connection
          logger.debug("CER received by other connection [{}]", key);

          switch (fsm.getState(PeerState.class)) {
            case DOWN:
              resultCode = ResultCode.SUCCESS;
              break;
            case INITIAL:
              boolean isLocalWin = false;
              if (isElection) {
                try {
                  // ammendonca: can't understand why it checks for <= 0 ... using equals
                  //isLocalWin = metaData.getLocalPeer().getUri().getFQDN().compareTo(
                  //    message.getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString()) <= 0;
                  isLocalWin = metaData.getLocalPeer().getUri().getFQDN().equals(
                      message.getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString());
                }
                catch (Exception exc) {
                  isLocalWin = true;
                }
              }

              logger.debug("local peer is win - [{}]", isLocalWin);

              resultCode = 0;
              if (isLocalWin) {
                IConnection c = incConnections.get(key);
                c.remConnectionListener(connListener);
                c.disconnect();
                incConnections.remove(key);
              }
              else {
                connection.disconnect();  // close current connection and work with other connection
                connection.remConnectionListener(connListener);
                connection = incConnections.remove(key);
                resultCode = ResultCode.SUCCESS;
              }
              break;
          }
        }
        else {
          if (logger.isDebugEnabled()) {
            logger.debug("CER received by current connection, key: [{}] PeerState: [{}] ", key, fsm.getState(PeerState.class));
          }
          if (fsm.getState(PeerState.class).equals(INITIAL)) { // received cer by current connection
            resultCode = 0; // NOP
          }

          incConnections.remove(key);
        }
        if (resultCode == ResultCode.SUCCESS) {
          commonApplications.clear();
          commonApplications.addAll(newAppId);
          fillIPAddressTable(message);
        }
      }
      catch (Exception exc) {
        logger.debug("Can not process CER", exc);
      }
      logger.debug("CER result [{}]", resultCode);

      return resultCode;
    }

    @Override
    public boolean isRestoreConnection() {
      return isAttemptConnection;
    }

    @Override
    public String getPeerDescription() {
      return PeerImpl.this.toString();
    }

    @Override
    public boolean receiveMessage(IMessage message) {
      logger.debug("Receiving message in server.");
      boolean isProcessed = false;

      // we set the peer in the message so we can later reply directly
      message.setPeer(PeerImpl.this);

      if (message.isRequest()) {
        IRequest req = message;
        Avp destRealmAvp = req.getAvps().getAvp(Avp.DESTINATION_REALM);
        String destRealm = null;
        if (destRealmAvp == null) {
          // TODO: add that missing avp in "Failed-AVP" avp...
          sendErrorAnswer(message, "Missing Destination-Realm AVP", ResultCode.MISSING_AVP);
          return true;
        }
        else {
          try {
            destRealm = destRealmAvp.getDiameterIdentity();
          }
          catch (AvpDataException ade) {
            sendErrorAnswer(message, "Failed to parse Destination-Realm AVP", ResultCode.INVALID_AVP_VALUE, destRealmAvp);
            return true;
          }
        }
        IRealmTable realmTable = router.getRealmTable();

        if (!realmTable.realmExists(destRealm)) {
          // send no such realm answer.
          logger.warn("Received a request for an unrecognized realm: [{}]. Answering with 3003 (DIAMETER_REALM_NOT_SERVED) Result-Code.", destRealm);
          sendErrorAnswer(message, null, ResultCode.REALM_NOT_SERVED);
          return true;
        }
        ApplicationId appId = message.getSingleApplicationId();
        if (appId == null) {
          logger.warn("Receive a message with no Application Id. Answering with 5005 (MISSING_AVP) Result-Code.");
          sendErrorAnswer(message, "Missing Application-Id", ResultCode.MISSING_AVP);
          // TODO: add Auth-Application-Id, Acc-Application-Id and Vendor-Specific-Application-Id, can be empty
          return true;
        }

        // check condition for local processing.
        Avp destHostAvp = req.getAvps().getAvp(Avp.DESTINATION_HOST);

        // 6.1.4.  Processing Local Requests
        // A request is known to be for local consumption when one of the
        // following conditions occur:
        //  -  The Destination-Host AVP contains the local host's identity,
        //  -  The Destination-Host AVP is not present, the Destination-Realm AVP
        //     contains a realm the server is configured to process locally, and
        //     the Diameter application is locally supported, or
        //  -  Both the Destination-Host and the Destination-Realm are not
        //     present.
        if (destHostAvp != null) {
          try {
            String destHost = destHostAvp.getDiameterIdentity();
            //FIXME: add check with DNS/names to check 127 vs localhost
            if (destHost.equals(metaData.getLocalPeer().getUri().getFQDN())) {

              // this is for handling possible REDIRECT, destRealm != local.realm
              LocalAction action = null;
              IRealm matched = null;

              matched = (IRealm) realmTable.matchRealm(req);
              if (matched != null) {
                action = matched.getLocalAction();
              }
              else {
                // We don't support it locally, its not defined as remote, so send no such realm answer.
                sendErrorAnswer(message, null, ResultCode.APPLICATION_UNSUPPORTED);
                // or REALM_NOT_SERVED ?
                return true;
              }

              switch (action) {
                case LOCAL: // always call listener - this covers realms
                  // configured as localy processed and
                  // LocalPeer.realm
                  isProcessed = consumeMessage(message);
                  break;
                case PROXY:
                  //TODO: change this its almost the same as above, make it sync, so no router code involved
                  if (handleByAgent(message, isProcessed, req, matched)) {
                    isProcessed = true;
                  }
                  break;
                case RELAY: // might be complicated, lets make it listener
                  // now
                  isProcessed = consumeMessage(message); //if its not redirected its
                  break;
                case REDIRECT:
                  //TODO: change this its almost the same as above, make it sync, so no router code involved
                  if (handleByAgent(message, isProcessed, req, matched)) {
                    isProcessed = true;
                  }
                  break;
              }
            }
            else {
              //NOTE: this check should be improved, it checks if there is connection to peer, otherwise we cant serve it.
              //possibly also match realm.
              IPeer p = (IPeer) peerTable.getPeer(destHost);
              if (p != null && p.hasValidConnection()) {
                isProcessed = consumeMessage(message);
              }
              else {
                // RFC 3588 // 6.1
                //   4. If none of the above is successful, an answer is returned with the
                //   Result-Code set to DIAMETER_UNABLE_TO_DELIVER, with the E-bit set.
                logger.warn("Received message for unknown peer [{}]. Answering with 3002 (UNABLE_TO_DELIVER) Result-Code.", destHost);
                sendErrorAnswer(req, "No connection to peer", ResultCode.UNABLE_TO_DELIVER);
                isProcessed = true;
              }
            }
          }
          catch (AvpDataException ade) {
            logger.warn("Received message with present but unparsable Destination-Host. Answering with 5004 (INVALID_AVP_VALUE) Result-Code.");
            sendErrorAnswer(message, "Failed to parse Destination-Host AVP", ResultCode.INVALID_AVP_VALUE, destHostAvp);
            return true;
          }
        }
        else {
          // we have to match realms :) this MUST include local realm
          LocalAction action = null;
          IRealm matched = null;

          matched = (IRealm) realmTable.matchRealm(req);
          if (matched != null) {
            action = matched.getLocalAction();
          }
          else {
            // We don't support it locally, its not defined as remote, so send no such realm answer.
            sendErrorAnswer(message, null, ResultCode.APPLICATION_UNSUPPORTED);
            // or REALM_NOT_SERVED ?
            return true;
          }

          switch (action) {
            case LOCAL: // always call listener - this covers realms
              // configured as locally processed and LocalPeer.realm
              isProcessed = consumeMessage(message);
              break;
            case PROXY:
              //TODO: change this its almost the same as above, make it sync, so no router code involved
              if ( handleByAgent(message, isProcessed, req, matched)) {
                isProcessed = true;
              }
              break;
            case RELAY: // might be complicated, lets make it listener
              // now
              isProcessed = consumeMessage(message);
              break;
            case REDIRECT:
              //TODO: change this its almost the same as above, make it sync, so no router code involved
              if (handleByAgent(message, isProcessed, req, matched)) {
                isProcessed = true;
              }
              break;
          }
        }
      }
      else {
        // answer, let client do its work
        isProcessed = super.receiveMessage(message);
      }

      return isProcessed;
    }

    /**
     * @param message
     * @param isProcessed
     * @param req
     * @param matched
     * @return
     */
    private boolean handleByAgent(IMessage message, boolean isProcessed, IRequest req, IRealm matched) {
      if (ovrManager != null && ovrManager.isParenAppOverload(message.getSingleApplicationId())) {
        logger.debug("Request [{}] skipped, because server application is overloaded", message);
        sendErrorAnswer(message, "Overloaded", ResultCode.TOO_BUSY);
        return true;
      }
      else {
        try {
          router.registerRequestRouteInfo(message);
          IMessage answer = (IMessage) matched.getAgent().processRequest(req, matched);
          if (isDuplicateProtection && answer != null) {
            peerTable.saveToDuplicate(message.getDuplicationKey(), answer);
          }
          isProcessed = true;
          if (answer != null) {
            sendMessage(answer);
          }
          if (statistic.isEnabled()) {
            statistic.getRecordByName(IStatisticRecord.Counters.SysGenResponse.name()).inc();
          }
        }
        catch (Exception exc) {
          // TODO: check this!!
          logger.warn("Error during processing message by " + matched.getAgent().getClass(), exc);
          sendErrorAnswer(message, "Unable to process", ResultCode.UNABLE_TO_COMPLY);
          return true;
        }
      }
      if (isProcessed) {
        // NOTE: done to inc stat which informs on net work request consumption :)
        if (statistic.isEnabled()) {
          statistic.getRecordByName(IStatisticRecord.Counters.NetGenRequest.name()).inc();
        }
      }
      return isProcessed;
    }

    /**
     * @param message
     * @return
     */
    private boolean consumeMessage(IMessage message) {
      // now its safe to call stupid client code....
      logger.debug("In Server consumeMessage. Going to call parents class receiveMessage");
      boolean isProcessed = super.receiveMessage(message);
      logger.debug("Did client PeerImpl process the message? [{}]", isProcessed);
      IMessage answer = null;
      // this will process if session exists.
      if (!isProcessed) {
        if (statistic.isEnabled()) {
          // Decrement what we have incremented in super.receiveMessage(message) since it wasn't processed
          statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedRequest.name()).dec();
        }

        NetworkReqListener listener = network.getListener(message);
        if (listener != null) {
          if (logger.isDebugEnabled()) {
            logger.debug("We have found an application that is a listener for this message. It is [{}]", listener.getClass().getName());
          }
          if (isDuplicateProtection) {
            logger.debug("Checking if it's a duplicate, since duplicate protection is ENABLED.");
            answer = peerTable.isDuplicate(message);
          }
          if (answer != null) {
            logger.debug("This message was detected as being a duplicate");
            answer.setProxiable(message.isProxiable());
            answer.getAvps().removeAvp(Avp.PROXY_INFO);
            for (Avp avp : message.getAvps().getAvps(Avp.PROXY_INFO)) {
              answer.getAvps().addAvp(avp);
            }
            answer.setHopByHopIdentifier(message.getHopByHopIdentifier());

            isProcessed = true;
            try {
              sendMessage(answer);
              if (statistic.isEnabled()) {
                statistic.getRecordByName(IStatisticRecord.Counters.SysGenResponse.name()).inc();
              }
            }
            catch (Exception e) {
              // TODO: check this!!
              logger.warn("Error during processing message by duplicate protection", e);
              sendErrorAnswer(message, "Unable to process", ResultCode.UNABLE_TO_COMPLY);
              return true;
            }
          }
          else {
            if (ovrManager != null && ovrManager.isParenAppOverload(message.getSingleApplicationId())) {
              logger.debug("Request [{}] skipped, because server application is overloaded", message);
              sendErrorAnswer(message, "Overloaded", ResultCode.TOO_BUSY);
              return true;
            }
            else {
              try {
                router.registerRequestRouteInfo(message);
                answer = (IMessage) listener.processRequest(message);
                if (isDuplicateProtection && answer != null) {
                  peerTable.saveToDuplicate(message.getDuplicationKey(), answer);
                }
                isProcessed = true;
                if (isProcessed && answer != null) {
                  // we use the peer from the request to reply directly
                  answer.setPeer(message.getPeer());
                  sendMessage(answer);
                }
                if (statistic.isEnabled()) {
                  statistic.getRecordByName(IStatisticRecord.Counters.AppGenResponse.name()).inc();
                }
              }
              catch (Exception exc) {
                // TODO: check this!!
                logger.warn("Error during processing message by listener", exc);
                sendErrorAnswer(message, "Unable to process", ResultCode.UNABLE_TO_COMPLY);
                return true;
              }
            }
          }
        }
        else {
          // NOTE: no listener defined for messages apps, response with "bad peer" stuff.
          logger.warn("Received message for unsupported Application-Id [{}]", message.getSingleApplicationId());
          sendErrorAnswer(message, "Unsupported Application-Id", ResultCode.APPLICATION_UNSUPPORTED);
          return true;
        }
      }

      if (isProcessed) {
        // NOTE: done to inc stat which informs on net work request consumption :)...
        if (statistic.isEnabled()) {
          statistic.getRecordByName(IStatisticRecord.Counters.NetGenRequest.name()).inc();
        }
      }
      return isProcessed;
    }

    @Override
    public String toString() {
      return new StringBuffer("LocalActionConext [isRestoreConnection()=").append(isRestoreConnection()).append(", getPeerDescription()=").
          append(getPeerDescription()).append(", isConnected()=").append(isConnected()).append(", LocalPeer=").append(metaData.getLocalPeer().getUri()).
          append(" ]").toString();
    }

  }

}
