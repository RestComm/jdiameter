/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, TeleStax Inc. and individual contributors
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

package org.jdiameter.client.impl.controller;

import static org.jdiameter.api.Avp.ACCT_APPLICATION_ID;
import static org.jdiameter.api.Avp.AUTH_APPLICATION_ID;
import static org.jdiameter.api.Avp.DESTINATION_HOST;
import static org.jdiameter.api.Avp.DESTINATION_REALM;
import static org.jdiameter.api.Avp.DISCONNECT_CAUSE;
import static org.jdiameter.api.Avp.ERROR_MESSAGE;
import static org.jdiameter.api.Avp.FIRMWARE_REVISION;
import static org.jdiameter.api.Avp.HOST_IP_ADDRESS;
import static org.jdiameter.api.Avp.ORIGIN_HOST;
import static org.jdiameter.api.Avp.ORIGIN_REALM;
import static org.jdiameter.api.Avp.ORIGIN_STATE_ID;
import static org.jdiameter.api.Avp.PRODUCT_NAME;
import static org.jdiameter.api.Avp.RESULT_CODE;
import static org.jdiameter.api.Avp.SUPPORTED_VENDOR_ID;
import static org.jdiameter.api.Avp.VENDOR_ID;
import static org.jdiameter.api.Avp.VENDOR_SPECIFIC_APPLICATION_ID;
import static org.jdiameter.api.Message.CAPABILITIES_EXCHANGE_REQUEST;
import static org.jdiameter.api.Message.DEVICE_WATCHDOG_REQUEST;
import static org.jdiameter.api.Message.DISCONNECT_PEER_REQUEST;
import static org.jdiameter.client.api.fsm.EventTypes.CEA_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.CER_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.CONNECT_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.DISCONNECT_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.DPA_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.DPR_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.DWA_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.DWR_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.INTERNAL_ERROR;
import static org.jdiameter.client.api.fsm.EventTypes.RECEIVE_MSG_EVENT;
import static org.jdiameter.client.api.fsm.EventTypes.STOP_EVENT;
import static org.jdiameter.client.impl.helpers.Parameters.SecurityRef;
import static org.jdiameter.client.impl.helpers.Parameters.UseUriAsFqdn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerStateListener;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.URI;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.FsmEvent;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.fsm.IStateMachine;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.AbstractStateChangeListener;
import org.jdiameter.client.impl.DictionarySingleton;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.jdiameter.common.impl.controller.AbstractPeer;
import org.jdiameter.server.impl.MutablePeerTableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Peer implementation
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PeerImpl extends AbstractPeer implements IPeer {

  private static final Logger logger = LoggerFactory.getLogger(PeerImpl.class);

  // Properties
  protected InetAddress[] addresses;
  protected String realmName;
  protected long vendorID;
  protected String productName;
  protected int firmWare;
  protected Set<ApplicationId> commonApplications = new HashSet<ApplicationId>();
  protected AtomicLong hopByHopId = new AtomicLong(uid.nextInt());
  protected int rating;
  protected boolean stopping = false;
  // Members
  protected IMetaData metaData;
  protected PeerTableImpl table;
  // Facilities
  protected IRouter router;
  // XXX: FT/HA // protected Map<String, NetworkReqListener> slc;
  protected final Map<Long, IMessage> peerRequests = new ConcurrentHashMap<Long, IMessage>();
  protected final Dictionary dictionary = DictionarySingleton.getDictionary();
  // FSM layer
  protected IStateMachine fsm;
  protected IMessageParser parser;
  // Feature
  protected boolean useUriAsFQDN = false; // Use URI as origin host name into CER command

  //session store and data
  protected ISessionDatasource sessionDataSource;

  // Transport layer
  protected IConnection connection;
  protected IConnectionListener connListener = new IConnectionListener() {

    @Override
    public void connectionOpened(String connKey) {
      logger.debug("Connection to {} is open", uri);
      try {
        fsm.handleEvent(new FsmEvent(CONNECT_EVENT, connKey));
      }
      catch (Exception e) {
        logger.warn("Unable to run start procedure", e);
      }
    }

    @Override
    public void connectionClosed(String connKey, List notSent) {
      logger.debug("Connection from {} is closed", uri);
      for (IMessage request : peerRequests.values()) {
        if (request.getState() == IMessage.STATE_SENT) {
          request.setReTransmitted(true);
          request.setState(IMessage.STATE_NOT_SENT);
          try {
            peerRequests.remove(request.getHopByHopIdentifier());
            table.sendMessage(request);
          }
          catch (Throwable exc) {
            request.setReTransmitted(false);
          }
        }
      }
      try {
        fsm.handleEvent(new FsmEvent(DISCONNECT_EVENT, connKey));
      }
      catch (Exception e) {
        logger.warn("Unable to run stopping procedure", e);
      }
    }

    @Override
    public void messageReceived(String connKey, IMessage message) {
      boolean req = message.isRequest();
      try {
        int type = message.getCommandCode();
        logger.debug("Receive message type [{}] to peer [{}]", new Object[] {type, connKey});
        switch (type) {
          case CAPABILITIES_EXCHANGE_REQUEST:
            fsm.handleEvent(new FsmEvent(req ? CER_EVENT : CEA_EVENT, message, connKey));
            break;
          case DEVICE_WATCHDOG_REQUEST:
            fsm.handleEvent(new FsmEvent(req ? DWR_EVENT : DWA_EVENT, message, connKey));
            break;
          case DISCONNECT_PEER_REQUEST:
            fsm.handleEvent(new FsmEvent(req ? DPR_EVENT : DPA_EVENT, message));
            break;
          default:
            fsm.handleEvent(new FsmEvent(RECEIVE_MSG_EVENT, message));
            break;
        }
      }
      catch (Exception e) {
        logger.warn("Error while processing incoming message", e);
        if (req) {
          try {
            message.setRequest(false);
            message.setError(true);
            message.getAvps().addAvp(Avp.RESULT_CODE, ResultCode.TOO_BUSY, true);
            connection.sendMessage(message);
          }
          catch (Exception exc) {
            logger.warn("Unable to send error answer", exc);
          }
        }
      }
    }

    @Override
    public void internalError(String connKey, IMessage message, TransportException cause) {
      try {
        logger.debug("internalError ", cause);
        fsm.handleEvent(new FsmEvent(INTERNAL_ERROR, message));
      }
      catch (Exception e) {
        logger.debug("Unable to run internalError procedure", e);
      }
    }
  };

  public PeerImpl(final PeerTableImpl table, int rating, URI remotePeer, String ip,  String portRange, IMetaData metaData, Configuration config,
      Configuration peerConfig, IFsmFactory fsmFactory, ITransportLayerFactory trFactory, IStatisticManager statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser, final ISessionDatasource sessionDataSource) throws InternalException, TransportException {
    this(table, rating, remotePeer, ip, portRange, metaData, config, peerConfig, fsmFactory, trFactory, parser, statisticFactory, concurrentFactory, null,
        sessionDataSource);
  }

  protected PeerImpl(final PeerTableImpl table, int rating, URI remotePeer, String ip, String portRange, IMetaData metaData,
      Configuration config, Configuration peerConfig, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
      IMessageParser parser, IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory,
      IConnection connection, final ISessionDatasource sessionDataSource) throws InternalException, TransportException {
    super(remotePeer, statisticFactory);
    this.table = table;
    this.rating = rating;
    this.router = table.router;
    this.metaData = metaData;
    // XXX: FT/HA // this.slc = table.getSessionReqListeners();
    this.sessionDataSource = sessionDataSource;

    int port = remotePeer.getPort();
    InetAddress remoteAddress;
    try {
      remoteAddress = InetAddress.getByName(ip != null ? ip : remotePeer.getFQDN());
    }
    catch (UnknownHostException e) {
      throw new TransportException("Unable to retrieve host", TransportError.Internal, e);
    }
    IContext actionContext = getContext();
    this.fsm = fsmFactory.createInstanceFsm(actionContext, concurrentFactory, config);
    this.fsm.addStateChangeNotification(
        new AbstractStateChangeListener() {
          @Override
          public void stateChanged(Enum oldState, Enum newState) {
            PeerState s = (PeerState) newState;
            if (PeerState.DOWN.equals(s)) {
              stopping = false;
            }
          }
        }
    );
    if (connection == null) {
      String ref = peerConfig.getStringValue(SecurityRef.ordinal(), null);
      InetAddress localAddress = null;
      try {
        Peer local = metaData.getLocalPeer();
        if (local.getIPAddresses() != null && local.getIPAddresses().length > 0) {
          localAddress = local.getIPAddresses()[0];
        }
        else {
          localAddress = InetAddress.getByName(metaData.getLocalPeer().getUri().getFQDN());
        }
      }
      catch (Exception e) {
        logger.warn("Unable to get local address", e);
      }
      int localPort = 0;
      if (portRange != null) {
        try {
          String[] rng = portRange.trim().split("-");
          int startRange = Integer.parseInt(rng[0]);
          int endRange = Integer.parseInt(rng[1]);
          boolean portNotAvailable = false;
          int limit = 0;
          int maxTries = endRange - startRange + 1;
          logger.debug("Selecting local port randomly from range '{}-{}'. Doing {} tries (some ports may not be tested, others tested more than once).",
              new Object[]{startRange, endRange, maxTries});

          do {
            portNotAvailable = false;
            limit++;
            localPort = startRange + new Random().nextInt(endRange - startRange + 1);
            logger.trace("Checking if port '{}' is available.", localPort);
            //check if port is open
            ServerSocket socket = null;
            try {
              socket = new ServerSocket(localPort);
              socket.setReuseAddress(true);
            }
            catch (IOException e) {
              logger.trace("The port '{}' is NOT available.", localPort);
              portNotAvailable = true;
            }
            finally {
              // Clean up
              if (socket != null) {
                logger.trace("The port '{}' is available and will be used.", localPort);
                socket.close();
              }
            }
          } while (portNotAvailable && (limit < maxTries));
          if (portNotAvailable) {
            logger.warn("Unable to find available port in port range.");
          }
        }
        catch (Exception exc) {
          logger.warn("Unable to get local port.", exc);
        }
        logger.debug("Create connection with localAddress=[{}]; localPort=[{}]", localAddress, localPort);
      }
      this.connection = trFactory.createConnection(remoteAddress, concurrentFactory, port, localAddress, localPort, connListener, ref);
    }
    else {
      this.connection = connection;
      this.connection.addConnectionListener(connListener);
    }
    this.parser = parser;
    this.addresses = new InetAddress[] {remoteAddress};
    this.useUriAsFQDN = config.getBooleanValue(UseUriAsFqdn.ordinal(), (Boolean) UseUriAsFqdn.defValue());
  }

  public IContext getContext() {
    return new ActionContext();
  }

  private boolean isRedirectAnswer(Avp avpResCode, IMessage answer) {
    try {
      return (answer.getFlags() & 0x20) != 0 && avpResCode != null && avpResCode.getInteger32() == ResultCode.REDIRECT_INDICATION;
    }
    catch (AvpDataException e) {
      return false;
    }
  }

  @Override
  public IStatistic getStatistic() {
    return statistic;
  }

  @Override
  public void addPeerStateListener(final PeerStateListener listener) {
    fsm.addStateChangeNotification(new AbstractStateChangeListener() {

      @Override
      public void stateChanged(Enum oldState, Enum newState) {
        listener.stateChanged((PeerState) oldState, (PeerState) newState);
      }

      @Override
      public int hashCode() {
        return listener.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
        return listener.equals(obj);
      }
    });
  }

  @Override
  public void removePeerStateListener(final PeerStateListener listener) {
    //FIXME: fix this... cmon
    if (listener != null) {
      fsm.remStateChangeNotification(new AbstractStateChangeListener() {
        @Override
        public void stateChanged(Enum oldState, Enum newState) {
          listener.stateChanged((PeerState) oldState, (PeerState) newState);
        }

        @Override
        public int hashCode() {
          return listener.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
          return listener.equals(obj);
        }
      });
    }
  }

  private IMessage processRedirectAnswer(IMessage request, IMessage answer) {
    int resultCode  = ResultCode.SUCCESS;

    try {
      //it will try to find next hope and send it...
      router.processRedirectAnswer(request, answer, table);
      return null;
    }
    catch (RouteException exc) {
      // Loop detected (may be stack must send error response to redirect host)
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to process redirect!", exc);
      }
      resultCode = ResultCode.LOOP_DETECTED;

    }
    catch (Throwable exc) {
      // Incorrect redirect message
      logger.debug("Failed to process redirect!", exc);
      resultCode = ResultCode.UNABLE_TO_DELIVER;
    }
    //why, oh why, peer works as router?....
    //    // Update destination avps
    //    if (resultCode == ResultCode.SUCCESS) {
    //      // Clear avps
    //      answer.getAvps().removeAvp(RESULT_CODE);
    //      // Update flags
    //      answer.setRequest(true);
    //      answer.setError(false);
    //      try {
    //        table.sendMessage(answer);
    //        answer = null;
    //      }
    //      catch (Exception e) {
    //        logger.warn("Unable to deliver due to error", e);
    //        resultCode = ResultCode.UNABLE_TO_DELIVER;
    //      }
    //    }
    if (resultCode != ResultCode.SUCCESS) {
      // Restore answer flag
      answer.setRequest(false);
      answer.setError(true);
      answer.getAvps().removeAvp(RESULT_CODE);
      answer.getAvps().addAvp(RESULT_CODE, resultCode, true, false, true);
    }
    return answer;
  }

  @Override
  public void connect() throws InternalException, IOException, IllegalDiameterStateException {
    if (getState(PeerState.class) != PeerState.DOWN) {
      throw new IllegalDiameterStateException("Invalid state:" + getState(PeerState.class));
    }
    try {
      fsm.handleEvent(new FsmEvent(EventTypes.START_EVENT));
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

  @Override
  public void disconnect(int disconnectCause) throws InternalException, IllegalDiameterStateException {
    super.disconnect(disconnectCause);
    if (getState(PeerState.class) != PeerState.DOWN) {
      stopping = true;
      try {
        FsmEvent event = new FsmEvent(STOP_EVENT);
        event.setData(disconnectCause);
        fsm.handleEvent(event);
      }
      catch (OverloadException e) {
        stopping = false;
        logger.warn("Error during stopping procedure", e);
      }
    }
  }

  @Override
  public <E> E getState(Class<E> enumc) {
    return fsm.getState(enumc);
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public InetAddress[] getIPAddresses() {
    return addresses;
  }

  @Override
  public String getRealmName() {
    return realmName;
  }

  @Override
  public long getVendorId() {
    return vendorID;
  }

  @Override
  public String getProductName() {
    return productName;
  }

  @Override
  public long getFirmware() {
    return firmWare;
  }

  @Override
  public Set<ApplicationId> getCommonApplications() {
    return commonApplications;
  }

  @Override
  public long getHopByHopIdentifier() {
    return hopByHopId.incrementAndGet();
  }

  @Override
  public void addMessage(IMessage message) {
    peerRequests.put(message.getHopByHopIdentifier(), message);
  }

  @Override
  public void remMessage(IMessage message) {
    peerRequests.remove(message.getHopByHopIdentifier());
  }

  @Override
  public IMessage[] remAllMessage() {
    IMessage[] m = peerRequests.values().toArray(new IMessage[peerRequests.size()]);
    peerRequests.clear();
    return m;
  }

  @Override
  public boolean handleMessage(EventTypes type, IMessage message, String key) throws TransportException, OverloadException, InternalException {
    return !stopping && fsm.handleEvent(new FsmEvent(type, message, key));
  }

  @Override
  public boolean sendMessage(IMessage message) throws TransportException, OverloadException, InternalException {
    if (dictionary != null && dictionary.isEnabled()) {
      logger.debug("Message validation is ENABLED. Going to validate message before sending.");
      dictionary.validate(message, false);
    }
    return !stopping && fsm.handleEvent(new FsmEvent(EventTypes.SEND_MSG_EVENT, message));
  }

  @Override
  public boolean hasValidConnection() {
    return connection != null && connection.isConnected();
  }

  @Override
  public void setRealm(String realm) {
    realmName = realm;
  }

  @Override
  public void addStateChangeListener(StateChangeListener listener) {
    fsm.addStateChangeNotification(listener);
  }

  @Override
  public void remStateChangeListener(StateChangeListener listener) {
    fsm.remStateChangeNotification(listener);
  }

  @Override
  public void addConnectionListener(IConnectionListener listener) {
    if (connection != null) {
      connection.addConnectionListener(listener);
    }
  }

  @Override
  public void remConnectionListener(IConnectionListener listener) {
    if (connection != null) {
      connection.remConnectionListener(listener);
    }
  }

  @Override
  public int getRating() {
    return rating;
  }

  @Override
  public boolean isConnected() {
    return getState(PeerState.class) == PeerState.OKAY;
  }

  @Override
  public String toString() {
    return "CPeer{" + "Uri=" + uri + "; State=" + (fsm != null ? fsm.getState(PeerState.class) : "n/a") + "; Con=" + connection + "}";
  }

  protected void fillIPAddressTable(IMessage message) {
    AvpSet avps = message.getAvps().getAvps(HOST_IP_ADDRESS);
    if (avps != null) {
      ArrayList<InetAddress> t = new ArrayList<InetAddress>();
      for (int i = 0; i < avps.size(); i++) {
        try {
          t.add(avps.getAvpByIndex(i).getAddress());
        }
        catch (AvpDataException e) {
          logger.warn("Unable to retrieve IP Address from Host-IP-Address AVP");
        }
      }
      addresses = t.toArray(new InetAddress[t.size()]);
    }
  }

  protected Set<ApplicationId> getCommonApplicationIds(IMessage message) {
    //TODO: fix this, its not correct lookup. It should check realm!
    //it does not include application Ids for which listeners register - and on this  basis it consume message!
    Set<ApplicationId> newAppId = new HashSet<ApplicationId>();
    Set<ApplicationId> locAppId = metaData.getLocalPeer().getCommonApplications();
    List<ApplicationId> remAppId = message.getApplicationIdAvps();
    logger.debug("Checking common applications. Remote applications: {}. Local applications: {}", remAppId, locAppId);
    // check common application
    for (ApplicationId l : locAppId) {
      for (ApplicationId r : remAppId) {
        if (l.equals(r)) {
          newAppId.add(l);
        }
        else if (r.getAcctAppId() == INT_COMMON_APP_ID || r.getAuthAppId() == INT_COMMON_APP_ID ||
            l.getAcctAppId() == INT_COMMON_APP_ID || l.getAuthAppId() == INT_COMMON_APP_ID) {
          newAppId.add(r);
        }
      }
    }
    return newAppId;
  }

  protected void sendErrorAnswer(IRequest request, String errorMessage, int resultCode, Avp ...avpsToAdd) {
    logger.debug("Could not process request. Result Code = [{}], Error Message: [{}]", resultCode, errorMessage);
    request.setRequest(false);
    // Not setting error flag, depends on error code. Will be set @ PeerImpl.ActionContext.sendMessage(IMessage)
    // request.setError(true);
    request.getAvps().addAvp(RESULT_CODE, resultCode, true, false, true);

    //add before removal actions
    if (avpsToAdd != null) {
      for (Avp a : avpsToAdd) {
        request.getAvps().addAvp(a);
      }
    }

    request.getAvps().removeAvp(ORIGIN_HOST);
    request.getAvps().removeAvp(ORIGIN_REALM);
    request.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
    request.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
    if (errorMessage != null) {
      request.getAvps().addAvp(ERROR_MESSAGE, errorMessage, false);
    }
    // Remove trash avp
    request.getAvps().removeAvp(DESTINATION_HOST);
    request.getAvps().removeAvp(DESTINATION_REALM);
    try {
      logger.debug("Sending response indicating we could not process request");
      sendMessage((IMessage) request);
      if (statistic.isEnabled()) {
        statistic.getRecordByName(IStatisticRecord.Counters.SysGenResponse.name()).inc();
      }
    }
    catch (Exception e) {
      logger.debug("Unable to send answer", e);
    }
    if (statistic.isEnabled()) {
      statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedRequest.name()).inc();
    }
  }


  protected class ActionContext implements IContext {

    @Override
    public String toString() {
      return new StringBuilder("ActionContext [getPeerDescription()=").append(getPeerDescription()).append(", isConnected()=").append(isConnected()).
          append(", isRestoreConnection()=").append(isRestoreConnection()).append("]").toString();
    }

    @Override
    public void connect() throws InternalException, IOException, IllegalDiameterStateException {
      try {
        connection.connect();
        if (logger.isDebugEnabled()) {
          logger.debug("Connected to peer {}", getUri());
        }
      }
      catch (TransportException e) {
        logger.debug("Failure establishing connection.", e);
        switch (e.getCode()) {
          case NetWorkError:
            throw new IOException("Unable to connect to " + connection.getKey() + " - " + e.getMessage());
          case FailedSendMessage:
            throw new IllegalDiameterStateException(e);
          default:
            throw new InternalException(e);
        }
      }
    }

    @Override
    public void disconnect() throws InternalException, IllegalDiameterStateException {
      if (connection != null) {
        connection.disconnect();
        if (logger.isDebugEnabled()) {
          logger.debug("Disconnected from peer {}", getUri());
        }
      }
    }

    @Override
    public String getPeerDescription() {
      return uri.toString();
    }

    @Override
    public boolean isConnected() {
      return (connection != null) && connection.isConnected();
    }

    @Override
    public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
      // Check message
      if (message.isTimeOut()) {
        logger.debug("Message {} skipped (timeout)", message);
        return false;
      }
      if (message.getState() == IMessage.STATE_SENT) {
        logger.debug("Message {} already sent", message);
        return false;
      }
      // Remove destination information from answer messages
      if (!message.isRequest()) {
        try {
          long resultCode = message.getResultCode().getUnsigned32();
          message.setError(resultCode >= 3000 && resultCode < 4000);
        }
        catch (Exception e) {
          logger.debug("Unable to retrieve Result-Code from answer. Not setting ERROR bit.");
          // ignore. should not happen
        }
        // 6.2.  Diameter Answer Processing answers and Error messages DONT have those.... pffff.
        message.getAvps().removeAvp(DESTINATION_HOST);
        message.getAvps().removeAvp(DESTINATION_REALM);

        int commandCode = message.getCommandCode();
        // We don't want this for CEx/DWx/DPx
        if (commandCode != 257 && commandCode != 280 && commandCode != 282) {
          if (table instanceof MutablePeerTableImpl) { // available only to server, client skip this step
            MutablePeerTableImpl peerTable = (MutablePeerTableImpl) table;
            if (peerTable.isDuplicateProtection()) {
              String[] originInfo = router.getRequestRouteInfo(message);
              if (originInfo != null) {
                // message.getDuplicationKey() doesn't work because it's answer
                peerTable.saveToDuplicate(message.getDuplicationKey(originInfo[0], message.getEndToEndIdentifier()), message);
              }
            }
          }
        }
      }
      // PCB added this
      router.garbageCollectRequestRouteInfo(message);

      // Send to network
      message.setState(IMessage.STATE_SENT);
      logger.debug("Calling connection to send message [{}] to peer [{}] over the network", message, getUri());
      connection.sendMessage(message);
      logger.debug("Connection sent message [{}] to peer [{}] over the network", message, getUri());

      return true;
    }

    @Override
    public void sendCerMessage() throws TransportException, OverloadException {
      logger.debug("Send CER message");
      IMessage message = parser.createEmptyMessage(CAPABILITIES_EXCHANGE_REQUEST, 0);
      message.setRequest(true);
      message.setHopByHopIdentifier(getHopByHopIdentifier());

      if (useUriAsFQDN) {
        message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().toString(), true, false, true);
      }
      else {
        message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      }

      message.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      for (InetAddress ia : metaData.getLocalPeer().getIPAddresses()) {
        message.getAvps().addAvp(HOST_IP_ADDRESS, ia, true, false);
      }
      message.getAvps().addAvp(VENDOR_ID, metaData.getLocalPeer().getVendorId(), true, false, true);
      message.getAvps().addAvp(PRODUCT_NAME, metaData.getLocalPeer().getProductName(), false);
      for (ApplicationId appId : metaData.getLocalPeer().getCommonApplications()) {
        addAppId(appId, message);
      }
      message.getAvps().addAvp(FIRMWARE_REVISION, metaData.getLocalPeer().getFirmware(), true);
      message.getAvps().addAvp(ORIGIN_STATE_ID, metaData.getLocalHostStateId(), true, false, true);
      sendMessage(message);
    }

    @Override
    public void sendCeaMessage(int resultCode, Message cer, String errMessage) throws TransportException, OverloadException {

    }

    @Override
    public void sendDwrMessage() throws TransportException, OverloadException {
      logger.debug("Send DWR message");
      IMessage message = parser.createEmptyMessage(DEVICE_WATCHDOG_REQUEST, 0);
      message.setRequest(true);
      message.setHopByHopIdentifier(getHopByHopIdentifier());
      // Set content
      message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      message.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      message.getAvps().addAvp(ORIGIN_STATE_ID, metaData.getLocalHostStateId(), true, false, true);
      // Remove trash avp
      message.getAvps().removeAvp(DESTINATION_HOST);
      message.getAvps().removeAvp(DESTINATION_REALM);
      // Send
      sendMessage(message);
    }

    @Override
    public void sendDwaMessage(IMessage dwr, int resultCode, String errorMessage) throws TransportException, OverloadException {
      logger.debug("Send DWA message");
      IMessage message = parser.createEmptyMessage(dwr);
      message.setRequest(false);
      message.setHopByHopIdentifier(dwr.getHopByHopIdentifier());
      message.setEndToEndIdentifier(dwr.getEndToEndIdentifier());
      // Set content
      message.getAvps().addAvp(RESULT_CODE, resultCode, true, false, true);
      message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      message.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      if (errorMessage != null) {
        message.getAvps().addAvp(ERROR_MESSAGE, errorMessage, false);
      }
      // Remove trash avp
      message.getAvps().removeAvp(DESTINATION_HOST);
      message.getAvps().removeAvp(DESTINATION_REALM);
      // Send
      sendMessage(message);
    }

    @Override
    public boolean isRestoreConnection() {
      return true;
    }

    @Override
    public void sendDprMessage(int disconnectCause) throws TransportException, OverloadException {
      logger.debug("Send DPR message with Disconnect-Cause [{}]", disconnectCause);
      IMessage message = parser.createEmptyMessage(DISCONNECT_PEER_REQUEST, 0);
      message.setRequest(true);
      message.setHopByHopIdentifier(getHopByHopIdentifier());
      message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      message.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      message.getAvps().addAvp(DISCONNECT_CAUSE, disconnectCause, true, false);
      sendMessage(message);
    }

    @Override
    public void sendDpaMessage(IMessage dpr, int resultCode, String errorMessage) throws TransportException, OverloadException {
      logger.debug("Send DPA message");
      IMessage message = parser.createEmptyMessage(dpr);
      message.setRequest(false);
      message.setHopByHopIdentifier(dpr.getHopByHopIdentifier());
      message.setEndToEndIdentifier(dpr.getEndToEndIdentifier());
      message.getAvps().addAvp(RESULT_CODE,  resultCode, true, false, true);
      message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      message.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      if (errorMessage != null) {
        message.getAvps().addAvp(ERROR_MESSAGE, errorMessage, false);
      }
      sendMessage(message);
    }

    @Override
    public int processCerMessage(String key, IMessage message) {
      return 0;
    }

    @Override
    public boolean processCeaMessage(String key, IMessage message) {
      boolean rc = true;
      try {
        Avp origHost  = message.getAvps().getAvp(ORIGIN_HOST);
        Avp origRealm = message.getAvps().getAvp(ORIGIN_REALM);
        Avp vendorId  = message.getAvps().getAvp(VENDOR_ID);
        Avp prdName   = message.getAvps().getAvp(PRODUCT_NAME);
        Avp resCode = message.getAvps().getAvp(RESULT_CODE);
        Avp frmId     = message.getAvps().getAvp(FIRMWARE_REVISION);
        if (origHost == null || origRealm == null || vendorId == null) {
          logger.warn("Incorrect CEA message (missing mandatory AVPs)");
        }
        else {
          if (realmName == null) {
            realmName = origRealm.getDiameterIdentity();
          }
          if (vendorID == 0) {
            vendorID = vendorId.getUnsigned32();
          }
          fillIPAddressTable(message);
          if (productName == null && prdName != null) {
            productName = prdName.getUTF8String();
          }
          if (resCode != null) {
            int mrc = resCode.getInteger32();
            if (mrc != ResultCode.SUCCESS) {
              logger.debug("Result code value {}", mrc);
              return false;
            }
          }
          Set<ApplicationId> cai = getCommonApplicationIds(message);
          if (cai.size() > 0) {
            commonApplications.clear();
            commonApplications.addAll(cai);
          }
          else {
            logger.debug("CEA did not contained appId, therefore set local appids to common-appid field");
            commonApplications.clear();
            commonApplications.addAll(metaData.getLocalPeer().getCommonApplications());
          }

          if (firmWare == 0 && frmId != null) {
            firmWare = frmId.getInteger32();
          }
        }
      }
      catch (Exception exc) {
        logger.debug("Incorrect CEA message", exc);
        rc = false;
      }
      return rc;
    }

    @Override
    public boolean receiveMessage(IMessage message) {
      logger.debug("Receiving message in client.");
      boolean isProcessed = false;

      // TODO: this might not be proper, since there might be no session
      // present in case of stateless traffic
      if (message.isRequest()) {
        logger.debug("Message is a request");
        // checks in server side make sure we are legit, now lets check if there is session present
        String avpSessionId = message.getSessionId();
        if (avpSessionId != null) {
          // XXX: FT/HA // NetworkReqListener listener = slc.get(avpSessionId);
          NetworkReqListener listener = sessionDataSource.getSessionListener(avpSessionId);
          if (listener != null) {
            router.registerRequestRouteInfo(message);

            IMessage answer = (IMessage) listener.processRequest(message);
            if (answer != null) {
              try {
                sendMessage(answer);
                if (statistic.isEnabled()) {
                  statistic.getRecordByName(IStatisticRecord.Counters.AppGenResponse.name()).inc();
                }
              }
              catch (Exception e) {
                logger.warn("Unable to send immediate answer {}", answer);
              }
            }

            if (statistic.isEnabled()) {
              statistic.getRecordByName(IStatisticRecord.Counters.NetGenRequest.name()).inc();
            }
            isProcessed = true;
          }
          else {
            if (statistic.isEnabled()) {
              statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedRequest.name()).inc();
            }
          }
        }
      }
      else {
        logger.debug("Message is an answer");

        //TODO: check REALMs here?
        IMessage request = peerRequests.remove(message.getHopByHopIdentifier());
        if (request != null && !request.isTimeOut()) {
          request.clearTimer();
          request.setState(IMessage.STATE_ANSWERED);
          Avp avpResCode = message.getAvps().getAvp(RESULT_CODE);
          if (isRedirectAnswer(avpResCode, message)) {
            message.setListener(request.getEventListener());
            message = processRedirectAnswer(request, message);
            //if return value is not null, there was some error, lets try to invoke listener if it exists...
            isProcessed = message == null;
          }

          if (message != null) {
            if (request.getEventListener() != null) {
              request.getEventListener().receivedSuccessMessage(request, message);
            }
            else {
              logger.debug("Unable to call answer listener for request {} because listener is not set", message);
              if (statistic.isEnabled()) {
                statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedResponse.name()).inc();
              }
            }

            isProcessed = true;
            if (statistic.isEnabled()) {
              statistic.getRecordByName(IStatisticRecord.Counters.NetGenResponse.name()).inc();
            }
          }
          else {
            if (statistic.isEnabled()) {
              statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedResponse.name()).inc();
            }
          }
        }
        else {
          if (statistic.isEnabled()) {
            statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedResponse.name()).inc();
          }
        }
      }
      return isProcessed;
    }

    @Override
    public int processDwrMessage(IMessage iMessage) {
      return ResultCode.SUCCESS;
    }

    @Override
    public int processDprMessage(IMessage iMessage) {
      return ResultCode.SUCCESS;
    }

    protected void addAppId(ApplicationId appId, IMessage message) {
      if (appId.getVendorId() == 0) {
        if (appId.getAuthAppId() != 0) {
          message.getAvps().addAvp(AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
        }
        else if (appId.getAcctAppId() != 0) {
          message.getAvps().addAvp(ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
        }
      }
      else {
        // Avoid duplicates
        boolean vendorIdPresent = false;
        for (Avp avp : message.getAvps().getAvps(SUPPORTED_VENDOR_ID)) {
          try {
            if (avp.getUnsigned32() == appId.getVendorId()) {
              vendorIdPresent = true;
              break;
            }
          }
          catch (Exception e) {
            logger.debug("Failed to read Supported-Vendor-Id.", e);
          }
        }
        if (!vendorIdPresent) {
          message.getAvps().addAvp(SUPPORTED_VENDOR_ID, appId.getVendorId(), true, false, true);
        }
        AvpSet vendorApp = message.getAvps().addGroupedAvp(VENDOR_SPECIFIC_APPLICATION_ID, true, false);
        vendorApp.addAvp(VENDOR_ID, appId.getVendorId(), true, false, true);
        if (appId.getAuthAppId() != 0) {
          vendorApp.addAvp(AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
        }
        if (appId.getAcctAppId() != 0) {
          vendorApp.addAvp(ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
        }
      }
    }

    /* (non-Javadoc)
     * @see org.jdiameter.client.api.fsm.IContext#removePeerStatistics()
     */
    @Override
    public void removeStatistics() {
      removePeerStatistics();
    }

    /* (non-Javadoc)
     * @see org.jdiameter.client.api.fsm.IContext#createPeerStatistics()
     */
    @Override
    public void createStatistics() {
      createPeerStatistics();
    }
  }

}