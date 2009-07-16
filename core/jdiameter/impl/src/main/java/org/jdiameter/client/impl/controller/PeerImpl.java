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

import org.jdiameter.api.*;
import static org.jdiameter.api.Avp.*;
import static org.jdiameter.api.Message.*;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.fsm.*;
import static org.jdiameter.client.api.fsm.EventTypes.*;
import org.jdiameter.client.api.io.*;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.helpers.Loggers;
import static org.jdiameter.client.impl.helpers.Parameters.UseUriAsFqdn;
import static org.jdiameter.client.impl.helpers.Parameters.SecurityRef;
import org.jdiameter.client.impl.helpers.UIDGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import static java.util.logging.Level.*;
import java.util.logging.Logger;

public class PeerImpl implements IPeer, Comparable<Peer> {

    public static final int INT_COMMON_APP_ID = 0xffffffff;

    protected static UIDGenerator uid = new UIDGenerator();
    // Logger
    protected Logger logger = Logger.getLogger(Loggers.Peer.fullName());
    // Properties
    protected URI uri;
    protected InetAddress[] addresses;
    protected String realmName;
    protected long vendorID;
    protected String productName;
    protected int firmWare;
    protected Set<ApplicationId> commonApplications = new HashSet<ApplicationId>();
    protected int hopByHopId = uid.nextInt();
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
    protected StateChangeListener fsmListener;
    protected IMessageParser parser;
    // Feature
    protected boolean useUriAsFQDN = false; // Use URI as orign host name into CER command

    // Transport layer
    protected IConnection conn;
    protected IConnectionListener connListener = new IConnectionListener() {
        
        public void connectionOpened(String connKey) {
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Connection to " + uri + " is opened");
        	}
            try {
                fsm.handleEvent( new FsmEvent(CONNECT_EVENT, connKey) );
            } catch (Exception e) {
            	if(logger.isLoggable(SEVERE))
            	{
            		logger.log(SEVERE, "Can not run start procedure", e);
            	}
            }
        }

        public void connectionClosed(String connKey, List notSended) {
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Connection from " + uri + " is close");
        	}
            for (IMessage request: peerRequests.values()) {
                if (request.getState() == IMessage.STATE_SENT) {
                    request.setReTransmitted(true);
                    request.setState(IMessage.STATE_NOT_SENT);
                    try {
                        peerRequests.remove(request.getHopByHopIdentifier());
                        table.sendMessage(request);
                    } catch(Throwable exc) {
                        request.setReTransmitted(false);
                    }
                }
            }
            try {
                fsm.handleEvent( new FsmEvent(DISCONNECT_EVENT, connKey) );
            } catch (Exception e) {
            	if(logger.isLoggable(SEVERE))
            	{
            		logger.log(SEVERE, "Can not run stopping procedure", e);
            	}
            }
        }

        public void messageReceived(String connKey, IMessage message) {
            boolean req = message.isRequest();
            try {
                int type = message.getCommandCode();
            	if(logger.isLoggable(FINEST))
            	{
            		logger.log(FINEST, "Receive message type:" + type);
            	}
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
            } catch (Exception e) {
            	if(logger.isLoggable(SEVERE))
            	{
            		logger.log(SEVERE, "Error during processing incomming message", e);
            	}
                if (req) {
                    try {
                        message.setRequest(false);
                        message.setError(true);
                        message.getAvps().addAvp(Avp.RESULT_CODE, ResultCode.TOO_BUSY, true); 
                        conn.sendMessage(message);
                    } catch (Exception exc) {
                    	if(logger.isLoggable(SEVERE))
                    	{
                    		logger.log(SEVERE, "Can not send error answer", exc);
                    	}
                    }
                }
            }
        }

        public void internalError(String connKey, IMessage message, TransportException cause) {
            try {
            	if(logger.isLoggable(SEVERE))
            	{
            		logger.log(SEVERE, "internalError ", cause);
            	}
                fsm.handleEvent( new FsmEvent(INTERNAL_ERROR, message));
            } catch (Exception e) {
            	if(logger.isLoggable(SEVERE))
            	{
            		logger.log(SEVERE, "Can not run internalError procedure", e);
            	}
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
        } catch (UnknownHostException e) {
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
                    if (PeerState.DOWN.equals(s))
                        stopping = false;
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
                  } else {
                      localAddress = InetAddress.getByName(metaData.getLocalPeer().getUri().getFQDN());
                  }
                } catch (Exception exc) {
                	if(logger.isLoggable(WARNING))
                	{
                		logger.log(Level.WARNING, "Can not get local address", exc);
                	}
                }
                try {
                    String[] rng = portRange.trim().split("-");
                    int strRange = Integer.parseInt(rng[0]);
                    int endRange = Integer.parseInt(rng[1]);
                    localPort = strRange + new Random().nextInt(endRange - strRange + 1);
                } catch (Exception exc) {
                	if(logger.isLoggable(WARNING))
                	{
                		logger.log(Level.WARNING, "Can not get local port", exc);
                	}
                }
            }
            this.conn = trFactory.createConnection(remoteAddress, port, localAddress, localPort,  connListener, ref);

        } else {
            this.conn = connection;
            this.conn.addConnectionListener(connListener);
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
        } catch (AvpDataException e) {
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
        } catch (RouteException exc) {
            // Loop detected (may be stack must send error response to redirect host)
            resultCode = ResultCode.LOOP_DETECTED;
        } catch(Throwable exc) {
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
            } catch (Exception e) {
            	if(logger.isLoggable(WARNING))
            	{
            		logger.log(WARNING,"Unable to deliver due to some error: ",e);
            		
            	}
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
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public void disconnect() throws InternalException, IllegalDiameterStateException {
      if (getState(PeerState.class) != PeerState.DOWN) {
        stopping = true;
        try {
            fsm.handleEvent( new FsmEvent(STOP_EVENT) );
        } catch (OverloadException e) {
            stopping = false;
            if(logger.isLoggable(WARNING))
        	{
            	logger.log(WARNING, "Error during stopping procedure", e);
        	}
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

    public int getHopByHopIdentifier() {
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
        return conn != null && conn.isConnected();
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
        if (conn != null)
            conn.addConnectionListener(listener);
    }

    public void remConnectionListener(IConnectionListener listener) {
        if (conn != null)
            conn.remConnectionListener(listener); 
    }

    public int getRaiting() {
        return rating;
    }

    public int compareTo(Peer o) {
        return uri.compareTo(o.getUri());
    }

    public String toString() {
        return "Peer{" +
                "Uri=" + uri + "; State="+fsm.getState(PeerState.class).toString() +
                '}';
    }

    protected void fillIPAddressTable(IMessage message) {
        AvpSet avps = message.getAvps().getAvps(HOST_IP_ADDRESS);
        if (avps != null) {
            ArrayList<InetAddress> t = new ArrayList<InetAddress>();
            for (int i=0; i < avps.size(); i++)
                try {
                    t.add( avps.getAvpByIndex(i).getAddress() );
                } catch (AvpDataException e) {
                	if(logger.isLoggable(FINEST))
                	{
                		logger.log(FINEST, "Can not get ip address from HOST_IP_ADDRESS avp");
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
                if (l.equals(r))
                    newAppId.add(l);
                else
                if (r.getAcctAppId() == INT_COMMON_APP_ID || r.getAuthAppId() == INT_COMMON_APP_ID || 
                    l.getAcctAppId() == INT_COMMON_APP_ID || l.getAuthAppId() == INT_COMMON_APP_ID)
                    newAppId.add(r);
            }
        }
        return newAppId;
    }


    protected void preProcessRequest(IMessage answer) {
    }    

    protected class ActionContext implements IContext {

        public void connect() throws InternalException, IOException, IllegalDiameterStateException {
            try {            
                conn.connect();
                if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Connected to peer " + PeerImpl.this.getUri());
                }
            } catch (TransportException e) {
                switch(e.getCode()) {
                    case NetWorkError:
                        throw new IOException("Can not connect to " + conn.getKey() + " - " + e.getMessage());
                    case FailedSendMessage:
                        throw new IllegalDiameterStateException(e);
                    default:
                        throw new InternalException(e);
                }
            }
        }

        public void disconnect() throws InternalException, IllegalDiameterStateException {
            if (conn != null) {
                conn.disconnect();
                if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Disconnected from peer " + PeerImpl.this.getUri());
                }
            }
        }

        public String getPeerDescription() {
            return PeerImpl.this.toString();
        }

        public boolean isConnected() {
            return (conn != null) && conn.isConnected();
        }

        public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
            // Check message
            if (message.isTimeOut()) {
            	if(logger.isLoggable(INFO))
            	{
            		logger.log(INFO, "Message: " + message + " skipped (timeout)");
            	}
                return false;
            }
            if (message.getState() == IMessage.STATE_SENT) {
            	if(logger.isLoggable(FINE))
            	{
            		logger.log(Level.FINE, "Message: " + message + " already send");
            	}
                return false;
            }
            // Remove destionation information from answer messages
            if ( !message.isRequest() ) {
                message.getAvps().removeAvp(DESTINATION_HOST);
                message.getAvps().removeAvp(DESTINATION_REALM);
            }
            // Send to network
            message.setState(IMessage.STATE_SENT);
            conn.sendMessage(message);
            if (logger.isLoggable(Level.FINE)) {
              logger.fine("Send message " + message + " to peer " + PeerImpl.this.getUri());
            }
            return true;
        }

        public void sendCerMessage() throws TransportException, OverloadException {
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Send CER message");
        	}
            IMessage message = parser.createEmptyMessage( CAPABILITIES_EXCHANGE_REQUEST, 0 );
            message.setRequest(true);
            message.setHopByHopIdentifier(getHopByHopIdentifier());

            if (useUriAsFQDN) {
                message.getAvps().addAvp( ORIGIN_HOST, metaData.getLocalPeer().getUri().toString(), true, false, true);
            }
            else {
                message.getAvps().addAvp( ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
            }

            message.getAvps().addAvp( ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
            for (InetAddress ia : metaData.getLocalPeer().getIPAddresses()) {
                message.getAvps().addAvp( HOST_IP_ADDRESS, ia, true, false);
            }
            message.getAvps().addAvp( VENDOR_ID, metaData.getLocalPeer().getVendorId(), true, false, true);
            message.getAvps().addAvp( PRODUCT_NAME,  metaData.getLocalPeer().getProductName(), false);
            for (ApplicationId appId: metaData.getLocalPeer().getCommonApplications()) addAppId(appId, message);
            message.getAvps().addAvp( FIRMWARE_REVISION, metaData.getLocalPeer().getFirmware(), true );
            message.getAvps().addAvp( ORIGIN_STATE_ID, metaData.getLocalHostStateId(), true, false, true);
            sendMessage(message);
        }

        public void sendCeaMessage(int resultCode, Message cer, String errMessage) throws TransportException, OverloadException {

        }

        public void sendDwrMessage() throws TransportException, OverloadException {
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Send DWR message");
        	}
            IMessage message = parser.createEmptyMessage( DEVICE_WATCHDOG_REQUEST, 0 );
            message.setRequest(true);
            message.setHopByHopIdentifier(getHopByHopIdentifier());
            // Set content
            message.getAvps().addAvp( ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
            message.getAvps().addAvp( ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
            message.getAvps().addAvp( ORIGIN_STATE_ID, metaData.getLocalHostStateId(), true, false, true);
            // Remove trash avp
            message.getAvps().removeAvp(DESTINATION_HOST);
            message.getAvps().removeAvp(DESTINATION_REALM);
            // Send
            sendMessage(message);
        }

        public void sendDwaMessage(IMessage dwr, int resultCode, String errorMessage) throws TransportException, OverloadException {
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Send DWA message");
        	}
            IMessage message = parser.createEmptyMessage(dwr);
            message.setRequest(false);
            message.setHopByHopIdentifier(dwr.getHopByHopIdentifier());
            message.setEndToEndIdentifier(dwr.getEndToEndIdentifier());
            // Set content
            message.getAvps().addAvp( RESULT_CODE, resultCode, true, false, true);
            if (errorMessage != null)
                message.getAvps().addAvp( ERROR_MESSAGE, errorMessage, false);
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
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Send DPR message");
        	}
            IMessage message = parser.createEmptyMessage( DISCONNECT_PEER_REQUEST, 0 );
            message.setRequest(true);
            message.setHopByHopIdentifier(getHopByHopIdentifier());
            message.getAvps().addAvp( ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
            message.getAvps().addAvp( ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
            message.getAvps().addAvp( DISCONNECT_CAUSE, disconnectCause, true, false );
            sendMessage(message);
        }

        public void sendDpaMessage(IMessage dpr, int resultCode, String errorMessage) throws TransportException, OverloadException {
        	if(logger.isLoggable(FINEST))
        	{
        		logger.log(FINEST, "Send DPA message");
        	}
            IMessage message = parser.createEmptyMessage(dpr);
            message.setRequest(false);
            message.setHopByHopIdentifier(dpr.getHopByHopIdentifier());
            message.setEndToEndIdentifier(dpr.getEndToEndIdentifier());
            message.getAvps().addAvp( RESULT_CODE,  resultCode, true, false, true);
            if (errorMessage != null)
                message.getAvps().addAvp( ERROR_MESSAGE, errorMessage, false);
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
                	if(logger.isLoggable(SEVERE))
                	{
                		logger.log(SEVERE, "Incorrect CEA message ( please set all mandatory Avps )");
                	}
                } else {
                    if (realmName == null)
                        realmName = origRealm.getOctetString();
                    if (vendorID == 0)
                        vendorID = vendorId.getUnsigned32();
                    fillIPAddressTable(message);
                    if (productName == null && prdName != null)
                        productName = prdName.getUTF8String();
                    if (resCode != null) {
                        int mrc = resCode.getInteger32();
                        if (mrc != ResultCode.SUCCESS) {
                            logger.warning("Result code value:" + mrc);
                            return false;
                        }
                    }
                    Set<ApplicationId> cai = getCommonApplicationIds(message);
                    if (cai.size() > 0) {
                        commonApplications.clear();
                        commonApplications.addAll(cai);
                    } else {
                    	if(logger.isLoggable(WARNING))
                    	{
                    		logger.log(WARNING, "CEA did not containe appId, therefore  set local appids to common-appid field");
                    	}
                        commonApplications.clear();
                        commonApplications.addAll(metaData.getLocalPeer().getCommonApplications());
                    }

                    if (firmWare == 0 && frmId != null)
                        firmWare = frmId.getInteger32();
                }
            } catch(Exception exc) {
            	if(logger.isLoggable(SEVERE))
            	{
            		logger.log(SEVERE, "Incorrect CEA message", exc);
            	}
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
                        if (answer != null)
                            try {
                                sendMessage(answer);
                            } catch (Exception e) {
                            	if(logger.isLoggable(SEVERE))
                            	{
                            		logger.severe("Can not send immediate answer " + answer);
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
                } else
                if (appId.getAcctAppId() != 0) {
                    message.getAvps().addAvp( ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true  );
                }
            } else {
                message.getAvps().addAvp( SUPPORTED_VENDOR_ID, appId.getVendorId(), true, false, true);
                AvpSet vendorApp = message.getAvps().addGroupedAvp(VENDOR_SPECIFIC_APPLICATION_ID, true, false );
                vendorApp.addAvp(VENDOR_ID, appId.getVendorId(), true, false, true );
                if (appId.getAuthAppId() != 0)
                    vendorApp.addAvp( AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true );
                if (appId.getAcctAppId() != 0)
                    vendorApp.addAvp( ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true );
            }
        }

    }
}