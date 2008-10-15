package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.jdiameter.api.Network;

public class MetaDataImpl extends org.jdiameter.client.impl.MetaDataImpl implements IMetaData {

    private final Object lock = new Object();

    public MetaDataImpl(IContainer s) {
        super(s);
    }

    public LocalPeer getLocalPeer() {
        return new MyLocalPeer();        
    }

    public StackType getStackType() {
        return StackType.TYPE_SERVER;
    }

    public void addApplicationId(ApplicationId applicationId) {
        synchronized (lock) {
            if (appIds.contains(applicationId)) return;
            appIds.add(applicationId);
        }
    }

    public void remApplicationId(ApplicationId applicationId) {
        synchronized (lock) {
            appIds.remove(applicationId);
        }
    }

    public void reload() {
        // Reload common application ids from configuration
        synchronized (lock) {
            appIds.clear();
            getLocalPeerInfo().getCommonApplications();
            // Reload ip addresses from configuration
            peer.resetAddresses();
            peer.getIPAddresses();
        }
    }

    protected class MyLocalPeer extends LocalPeer {

        protected INetwork net = null;
        protected IMutablePeerTable manager = null;
        protected ISessionFactory factory = null;
        protected Map<String, NetworkReqListener> slc = null;
        Map<Long, IMessage> peerRequests = new ConcurrentHashMap<Long, IMessage>();

        public Set<ApplicationId> getCommonApplications() {
            Set<ApplicationId> set;
            synchronized (lock) {
                set = super.getCommonApplications();
            }
            return  set;
        }

        public InetAddress[] getIPAdresses() {
            InetAddress[] addr;
            synchronized (lock) {
                addr = super.getIPAddresses();
            }
            return addr;
        }

        // Local processing message
        public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
            try {
                if (net == null || manager == null) {
                    try {
                        net = (INetwork) stack.unwrap(Network.class);
                        manager = (IMutablePeerTable) stack.unwrap(PeerTable.class);
                        factory = manager.getSessionFactory();
                        slc = manager.getSessionReqListeners();
                    } catch (Exception exc) {
                        stack.getLogger().log(Level.WARNING, "Error initialise", exc);
                    }
                }

                IMessage answer = null;
                if (message.isRequest()) {
                    message.setHopByHopIdentifier( peer.getHopByHopIdentifier() );
                    peerRequests.put(message.getHopByHopIdentifier(), message);
                    NetworkReqListener listener = net.getListener(message.getSingleApplicationId());
                    if (listener != null) {
                        // This is duplicate code from PeerImpl
                        answer = manager.isDuplicate(message);
                        if (answer != null) {
                            answer.setProxiable(message.isProxiable());
                            answer.getAvps().removeAvp(Avp.PROXY_INFO);
                            for (Avp avp : message.getAvps().getAvps(Avp.PROXY_INFO))
                                answer.getAvps().addAvp(avp);
                            answer.setHopByHopIdentifier(message.getHopByHopIdentifier());
                        } else {
                            String avpSessionId = message.getSessionId();
                            if (avpSessionId != null) {
                                NetworkReqListener sessionListener = slc.get(avpSessionId);
                                if (sessionListener != null) {
                                    answer = (IMessage) sessionListener.processRequest(message);
                                } else {
                                    try {
                                        answer = (IMessage) listener.processRequest(message);
                                        if (answer != null)
                                            manager.saveToDuplicate(message.getDuplicationKey(), answer);
                                    } catch (Exception exc) {
                                        stack.getLogger().log(Level.WARNING, "Error during processing message by listener", exc);
                                    }
                                }
                            }
                        }

                    } else {
                        stack.getLogger().log(Level.WARNING, "Can not find handler " + message.getSingleApplicationId() + " for message " + message);
                    }
                    if (answer != null)
                        peerRequests.remove( message.getHopByHopIdentifier() );
                } else {
                    answer = message;
                    message = peerRequests.get(answer.getHopByHopIdentifier());
                }
                // Process answer
                if ( message != null && !message.isTimeOut() && answer != null ) {
                    message.clearTimer();
                    message.setState(IMessage.STATE_ANSWERED);
                    message.getEventListener().receivedSuccessMessage(message, answer);
                }
                return true;
            } catch (Exception e) {
                stack.getLogger().log(Level.WARNING, "Can not processed message " + message, e);
            }
            return false;
        }
    }
}
