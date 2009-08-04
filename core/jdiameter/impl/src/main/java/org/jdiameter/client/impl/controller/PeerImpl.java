package org.jdiameter.client.impl.controller;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

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
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.ExecutorFactory;
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
import org.jdiameter.client.impl.helpers.UIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerImpl implements IPeer, Comparable<Peer> {

  public static final int INT_COMMON_APP_ID = 0xffffffff;

  protected static UIDGenerator uid = new UIDGenerator();
  // Logger
  protected Logger logger = LoggerFactory.getLogger(PeerImpl.class);
  // Properties
  protected URI uri;
  protected InetAddress[] addresses;
  protected String realmName;
  protected long vendorID;
  protected String productName;
  protected int firmWare;
  protected Set<ApplicationId> commonApplications = new HashSet<ApplicationId>();
  protected long hopByHopId = uid.nextInt();
  protected int rating;
  protected boolean stopping = false;
  // Members
  protected IMetaData metaData;
  protected PeerTableImpl table;
  // Facilities
  protected IRouter router;
  protected Map<String, NetworkReqListener> slc;
  protected Map<Long, IMessage> peerRequests = new ConcurrentHashMap<Long, IMessage>();
  // FSM layer
  protected IStateMachine fsm;
  protected IMessageParser parser;
  // Feature
  protected boolean useUriAsFQDN = false; // Use URI as orign host name into CER command

  // Transport layer
  protected IConnection connection;
  protected IConnectionListener connListener = new IConnectionListener() {

    public void connectionOpened(String connKey) {
      logger.debug("Connection to {} is opened", uri);
      try {
        fsm.handleEvent( new FsmEvent(CONNECT_EVENT, connKey) );
      }
      catch (Exception e) {
        logger.warn( "Can not run start procedure", e);
      }
    }

    public void connectionClosed(String connKey, List notSended) {
      logger.debug("Connection from {} is closed", uri);
      for (IMessage request: peerRequests.values()) {
        if (request.getState() == IMessage.STATE_SENT) {
          request.setReTransmitted(true);
          request.setState(IMessage.STATE_NOT_SENT);
          try {
            peerRequests.remove(request.getHopByHopIdentifier());
            table.sendMessage(request);
          }
          catch(Throwable exc) {
            request.setReTransmitted(false);
          }
        }
      }
      try {
        fsm.handleEvent( new FsmEvent(DISCONNECT_EVENT, connKey) );
      }
      catch (Exception e) {
        logger.warn( "Can not run stopping procedure", e);
      }
    }

    public void messageReceived(String connKey, IMessage message) {
      boolean req = message.isRequest();
      try {
        int type = message.getCommandCode();
        logger.debug("Receive message type {}", type);
        switch(type) {
        case CAPABILITIES_EXCHANGE_REQUEST:
          fsm.handleEvent( new FsmEvent(req ? CER_EVENT : CEA_EVENT, message, connKey) );
          break;
        case DEVICE_WATCHDOG_REQUEST:
          fsm.handleEvent( new FsmEvent(req ? DWR_EVENT : DWA_EVENT, message, connKey) );
          break;
        case DISCONNECT_PEER_REQUEST:
          fsm.handleEvent( new FsmEvent(req ? DPR_EVENT : DPA_EVENT, message) );
          break;
        default:
          fsm.handleEvent( new FsmEvent(RECEIVE_MSG_EVENT, message) );
        }
      }
      catch (Exception e) {
        logger.debug("Error during processing incomming message", e);
        if (req) {
          try {
            message.setRequest(false);
            message.setError(true);
            message.getAvps().addAvp(Avp.RESULT_CODE, ResultCode.TOO_BUSY, true); 
            connection.sendMessage(message);
          }
          catch (Exception exc) {
            logger.debug("Can not send error answer", exc);
          }
        }
      }
    }

    public void internalError(String connKey, IMessage message, TransportException cause) {
      try {
        logger.debug("internalError ", cause);
        fsm.handleEvent( new FsmEvent(INTERNAL_ERROR, message));
      }
      catch (Exception e) {
        logger.debug( "Can not run internalError procedure", e);
      }
    }
  };

  public PeerImpl(PeerTableImpl table, int rating, URI remotePeer, String ip,  String portRange, IMetaData metaData, Configuration config,
      Configuration peerConfig, IFsmFactory fsmFactory, ITransportLayerFactory trFactory, IMessageParser parser) throws InternalException, TransportException {
    this(table, rating, remotePeer, ip,  portRange, metaData, config, peerConfig, fsmFactory, trFactory, parser, null);
  }

  protected PeerImpl(final PeerTableImpl table, int rating, URI remotePeer, String ip, String portRange, IMetaData metaData, Configuration config,
      Configuration peerConfig, IFsmFactory fsmFactory, ITransportLayerFactory trFactory, IMessageParser parser,
      IConnection connection) throws InternalException, TransportException {
    this.table = table;
    this.rating = rating;
    this.router = table.router;
    this.metaData = metaData;
    this.slc = table.getSessionReqListeners();
    this.uri = remotePeer;
    int port = remotePeer.getPort();
    InetAddress remoteAddress;
    try {
      remoteAddress = InetAddress.getByName( ip != null ? ip : remotePeer.getFQDN());
    }
    catch (UnknownHostException e) {
      throw new TransportException("Can not found host", TransportError.Internal, e);
    }
    IContext actionContext = getContext();
    this.fsm = fsmFactory.createInstanceFsm(actionContext, new ExecutorFactory() {
      public ExecutorService getExecutor() {
        return table.getPeerTaskExecutor();
      }
    }, config);
    this.fsm.addStateChangeNotification(
        new StateChangeListener() {
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
      int localPort = 0;
      if (portRange != null) {
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
          logger.debug("Can not get local address", e);
        }
        try {
          String[] rng = portRange.trim().split("-");
          int strRange = Integer.parseInt(rng[0]);
          int endRange = Integer.parseInt(rng[1]);
          localPort = strRange + new Random().nextInt(endRange - strRange + 1);
        }
        catch (Exception exc) {
          logger.debug("Can not get local port", exc);
        }
        logger.debug("Create conn with localAddress={}; localPort={}", localAddress, localPort);
      }
      this.connection = trFactory.createConnection(remoteAddress, port, localAddress, localPort,  connListener, ref);

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

  public void addPeerStateListener(final PeerStateListener listener) {
    fsm.addStateChangeNotification(new StateChangeListener() {
      public void stateChanged(Enum oldState, Enum newState) {
        listener.stateChanged((PeerState) oldState, (PeerState) newState);
      }

      public int hashCode() {
        return listener.hashCode();
      }

      public boolean equals(Object obj) {
        return listener.equals(obj);
      }
    });
  }

  public void removePeerStateListener(final PeerStateListener listener) {
    if (listener != null) {
      fsm.remStateChangeNotification(new StateChangeListener() {
        public void stateChanged(Enum oldState, Enum newState) {
          listener.stateChanged((PeerState) oldState, (PeerState) newState);
        }

        public int hashCode() {
          return listener.hashCode();
        }

        public boolean equals(Object obj) {
          return listener.equals(obj);
        }
      });
    }
  }

  private IMessage processRedirectAnswer(IMessage answer) {
    int resultCode  = 0;
    // Update redirect information
    try {
      router.updateRedirectInformation(answer);
    }
    catch (RouteException exc) {
      // Loop detected (may be stack must send error response to redirect host)
      resultCode = ResultCode.LOOP_DETECTED;
    }
    catch(Throwable exc) {
      // Incorrect redirect message
      resultCode = ResultCode.UNABLE_TO_DELIVER;
    }
    // Update destination avps
    if (resultCode == 0) {
      // Clear avps
      answer.getAvps().removeAvp(RESULT_CODE);
      // Update flags
      answer.setRequest(true);
      answer.setError(false);
      try {
        table.sendMessage(answer);
        answer = null;
      }
      catch (Exception e) {
        logger.debug("Unable to deliver due to error", e);
        resultCode = ResultCode.UNABLE_TO_DELIVER;
      }
    }
    if (resultCode != 0) {
      // Restore answer flag
      answer.setRequest(false);
      answer.setError(true);
      answer.getAvps().removeAvp(RESULT_CODE);
      answer.getAvps().addAvp(RESULT_CODE, resultCode, true, false, true);
    }
    return answer;
  }    

  public void connect() throws InternalException, IOException, IllegalDiameterStateException {
    if (getState(PeerState.class) != PeerState.DOWN) {
      throw new IllegalDiameterStateException("Invalid state:" + getState(PeerState.class));
    }
    try {
      fsm.handleEvent( new FsmEvent(EventTypes.START_EVENT) );
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

  public void disconnect() throws InternalException, IllegalDiameterStateException {
    if (getState(PeerState.class) != PeerState.DOWN) {
      stopping = true;
      try {
        fsm.handleEvent( new FsmEvent(STOP_EVENT) );
      }
      catch (OverloadException e) {
        stopping = false;
        logger.debug("Error during stopping procedure", e);
      }
    }
  }

  public <E> E getState(Class<E> enumc) {
    return fsm.getState(enumc);
  }

  public URI getUri() {
    return uri;
  }

  public InetAddress[] getIPAddresses() {
    return addresses;
  }

  public String getRealmName() {
    return realmName;
  }

  public long getVendorId() {
    return vendorID;
  }

  public String getProductName() {
    return productName;
  }

  public long getFirmware() {
    return firmWare;
  }

  public Set<ApplicationId> getCommonApplications() {
    return commonApplications;
  }

  public long getHopByHopIdentifier() {
    return hopByHopId++;
  }

  public void addMessage(IMessage message) {
    peerRequests.put(message.getHopByHopIdentifier(), message);
  }

  public void remMessage(IMessage message) {
    peerRequests.remove(message.getHopByHopIdentifier());
  }

  public IMessage[] remAllMessage() {
    IMessage[] m = peerRequests.values().toArray(new IMessage[0]);
    peerRequests.clear();
    return m;
  }

  public boolean handleMessage(EventTypes type, IMessage message, String key) throws TransportException, OverloadException, InternalException {
    return !stopping && fsm.handleEvent( new FsmEvent(type, message, key) );
  }

  public boolean sendMessage(IMessage message) throws TransportException, OverloadException, InternalException {
    return !stopping && fsm.handleEvent( new FsmEvent(EventTypes.SEND_MSG_EVENT, message) );
  }

  public boolean hasValidConnection() {
    return connection != null && connection.isConnected();
  }

  public void setRealm(String realm) {
    realmName = realm;
  }

  public void addStateChangeListener(StateChangeListener listener) {
    fsm.addStateChangeNotification(listener);
  }

  public void remStateChangeListener(StateChangeListener listener) {
    fsm.remStateChangeNotification(listener);
  }

  public void addConnectionListener(IConnectionListener listener) {
    if (connection != null) {
      connection.addConnectionListener(listener);
    }
  }

  public void remConnectionListener(IConnectionListener listener) {
    if (connection != null) {
      connection.remConnectionListener(listener);
    }
  }

  public int getRaiting() {
    return rating;
  }

  public int compareTo(Peer o) {
    return uri.compareTo(o.getUri());
  }

  public String toString() {
    return "Peer{" + "Uri=" + uri + "; State="+fsm.getState(PeerState.class).toString() + '}';
  }

  protected void fillIPAddressTable(IMessage message) {
    AvpSet avps = message.getAvps().getAvps(HOST_IP_ADDRESS);
    if (avps != null) {
      ArrayList<InetAddress> t = new ArrayList<InetAddress>();
      for (int i=0; i < avps.size(); i++) {
        try {
          t.add( avps.getAvpByIndex(i).getAddress() );
        }
        catch (AvpDataException e) {
          logger.debug("Can not get ip address from HOST_IP_ADDRESS avp");
        }
      }
      addresses = t.toArray(new InetAddress[t.size()]);
    }
  }

  protected Set<ApplicationId> getCommonApplicationIds(IMessage message) {
    Set<ApplicationId> newAppId = new HashSet<ApplicationId>();
    Set<ApplicationId> locAppId = metaData.getLocalPeer().getCommonApplications();
    Set<ApplicationId> remAppId = message.getApplicationIdAvps();
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

  protected void preProcessRequest(IMessage answer) {
  }    

  protected class ActionContext implements IContext {

    public void connect() throws InternalException, IOException, IllegalDiameterStateException {
      try {            
        connection.connect();
        logger.debug("Connected to peer {}", PeerImpl.this.getUri());
      }
      catch (TransportException e) {
        switch(e.getCode()) {
        case NetWorkError:
          throw new IOException("Can not connect to " + connection.getKey() + " - " + e.getMessage());
        case FailedSendMessage:
          throw new IllegalDiameterStateException(e);
        default:
          throw new InternalException(e);
        }
      }
    }

    public void disconnect() throws InternalException, IllegalDiameterStateException {
      if (connection != null) {
        connection.disconnect();
        logger.debug("Disconnected from peer {}", PeerImpl.this.getUri());
      }
    }

    public String getPeerDescription() {
      return PeerImpl.this.toString();
    }

    public boolean isConnected() {
      return (connection != null) && connection.isConnected();
    }

    public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
      // Check message
      if (message.isTimeOut()) {
        logger.debug( "Message {} skipped (timeout)", message);
        return false;
      }
      if (message.getState() == IMessage.STATE_SENT) {
        logger.debug( "Message {} already sent", message);
        return false;
      }
      // Remove destionation information from answer messages
      if ( !message.isRequest() ) {
        message.getAvps().removeAvp(DESTINATION_HOST);
        message.getAvps().removeAvp(DESTINATION_REALM);
      }
      // Send to network
      message.setState(IMessage.STATE_SENT);
      connection.sendMessage(message);
      logger.debug("Send message {} to peer {}", message, PeerImpl.this.getUri());
      return true;
    }

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
      message.getAvps().addAvp(PRODUCT_NAME,  metaData.getLocalPeer().getProductName(), false);
      for (ApplicationId appId: metaData.getLocalPeer().getCommonApplications()) {
        addAppId(appId, message);
      }
      message.getAvps().addAvp(FIRMWARE_REVISION, metaData.getLocalPeer().getFirmware(), true );
      message.getAvps().addAvp(ORIGIN_STATE_ID, metaData.getLocalHostStateId(), true, false, true);
      sendMessage(message);
    }

    public void sendCeaMessage(int resultCode, Message cer, String errMessage) throws TransportException, OverloadException {

    }

    public void sendDwrMessage() throws TransportException, OverloadException {
      logger.debug("Send DWR message");
      IMessage message = parser.createEmptyMessage( DEVICE_WATCHDOG_REQUEST, 0 );
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

    public void sendDwaMessage(IMessage dwr, int resultCode, String errorMessage) throws TransportException, OverloadException {
      logger.debug("Send DWA message");
      IMessage message = parser.createEmptyMessage(dwr);
      message.setRequest(false);
      message.setHopByHopIdentifier(dwr.getHopByHopIdentifier());
      message.setEndToEndIdentifier(dwr.getEndToEndIdentifier());
      // Set content
      message.getAvps().addAvp(RESULT_CODE, resultCode, true, false, true);
      if (errorMessage != null) {
        message.getAvps().addAvp(ERROR_MESSAGE, errorMessage, false);
      }
      // Remove trash avp 
      message.getAvps().removeAvp(DESTINATION_HOST);
      message.getAvps().removeAvp(DESTINATION_REALM);
      // Send
      sendMessage(message);
    }

    public boolean isRestoreConnection() {
      return true;  
    }

    public void sendDprMessage(int disconnectCause) throws TransportException, OverloadException {
      logger.debug("Send DPR message");
      IMessage message = parser.createEmptyMessage(DISCONNECT_PEER_REQUEST, 0 );
      message.setRequest(true);
      message.setHopByHopIdentifier(getHopByHopIdentifier());
      message.getAvps().addAvp(ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      message.getAvps().addAvp(ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      message.getAvps().addAvp(DISCONNECT_CAUSE, disconnectCause, true, false );
      sendMessage(message);
    }

    public void sendDpaMessage(IMessage dpr, int resultCode, String errorMessage) throws TransportException, OverloadException {
      logger.debug("Send DPA message");
      IMessage message = parser.createEmptyMessage(dpr);
      message.setRequest(false);
      message.setHopByHopIdentifier(dpr.getHopByHopIdentifier());
      message.setEndToEndIdentifier(dpr.getEndToEndIdentifier());
      message.getAvps().addAvp(RESULT_CODE,  resultCode, true, false, true);
      if (errorMessage != null) {
        message.getAvps().addAvp(ERROR_MESSAGE, errorMessage, false);
      }
      sendMessage(message);
    }

    public int processCerMessage(String key, IMessage message) {
      return 0;
    }

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
          logger.debug("Incorrect CEA message (missing mandatory AVPs)");
        }
        else {
          if (realmName == null) {
            realmName = origRealm.getOctetString();
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
          } else {
            logger.debug("CEA did not containe appId, therefore  set local appids to common-appid field");
            commonApplications.clear();
            commonApplications.addAll(metaData.getLocalPeer().getCommonApplications());
          }

          if (firmWare == 0 && frmId != null) {
            firmWare = frmId.getInteger32();
          }
        }
      }
      catch(Exception exc) {
        logger.debug("Incorrect CEA message", exc);
        rc = false;
      }
      return rc;
    }

    public boolean receiveMessage(IMessage message) {
      boolean isProcessed = false;
      if ( !message.isRequest() ) {
        IMessage request = peerRequests.remove( message.getHopByHopIdentifier() );
        if ( request != null && !request.isTimeOut()) {
          request.clearTimer();
          request.setState(IMessage.STATE_ANSWERED);
          Avp avpResCode = message.getAvps().getAvp( RESULT_CODE );
          if (isRedirectAnswer(avpResCode, message)) {
            message.setListener(request.getEventListener());
            message = processRedirectAnswer( message );
            isProcessed = message == null;
          }
          if (message != null) {
            request.getEventListener().receivedSuccessMessage( request, message );
            isProcessed = true;
          }
        }
      } else {
        // Server request
        String avpSessionId = message.getSessionId();
        if (avpSessionId != null) {
          NetworkReqListener listener = slc.get(avpSessionId);
          if (listener != null) {
            router.registerRequestRouteInfo( message );
            preProcessRequest(message);
            IMessage answer = (IMessage) listener.processRequest(message);
            if (answer != null) {
              try {
                sendMessage(answer);
              } catch (Exception e) {
                logger.warn("Can not send immediate answer {}", answer);
              }
            }
            isProcessed = true;
          }
        }
      }
      return isProcessed;
    }

    public int processDwrMessage(IMessage iMessage) {
      return ResultCode.SUCCESS;
    }

    public int processDprMessage(IMessage iMessage) {
      return ResultCode.SUCCESS;
    }

    protected void addAppId(ApplicationId appId, IMessage message) { // todo duplicate code look SessionImpl 225 line
      if (appId.getVendorId() == 0) {
        if (appId.getAuthAppId() != 0) {
          message.getAvps().addAvp( AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true );
        }
        else if (appId.getAcctAppId() != 0) {
          message.getAvps().addAvp( ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true  );
        }
      }
      else {
        message.getAvps().addAvp( SUPPORTED_VENDOR_ID, appId.getVendorId(), true, false, true);
        AvpSet vendorApp = message.getAvps().addGroupedAvp(VENDOR_SPECIFIC_APPLICATION_ID, true, false );
        vendorApp.addAvp(VENDOR_ID, appId.getVendorId(), true, false, true );
        if (appId.getAuthAppId() != 0) {
          vendorApp.addAvp( AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true );
        }
        if (appId.getAcctAppId() != 0) {
          vendorApp.addAvp( ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true );
        }
      }
    }

  }
}