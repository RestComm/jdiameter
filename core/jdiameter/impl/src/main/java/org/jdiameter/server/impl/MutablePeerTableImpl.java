package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.controller.PeerTableImpl;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;
import org.jdiameter.server.api.IOverloadManager;
import org.jdiameter.server.api.IPeer;
import org.jdiameter.server.api.io.INetWorkConnectionListener;
import org.jdiameter.server.api.io.INetWorkGuard;
import org.jdiameter.server.api.io.ITransportLayerFactory;
import org.jdiameter.server.impl.helpers.EmptyConfiguration;
import static org.jdiameter.server.impl.helpers.Parameters.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;

import org.jdiameter.api.Network;

public class MutablePeerTableImpl extends PeerTableImpl implements IMutablePeerTable, ConfigurationListener {

    private static final int MAX_DUPLICATE_ANSWERS = 5000;
    private static final int CONN_INVALIDATE_PERIOD = 60000;

    protected Configuration config;
    protected ISessionFactory sessionFactory;
    protected IFsmFactory fsmFactory;
    protected ITransportLayerFactory transportFactory;
    protected IMessageParser parser;
    protected org.jdiameter.server.api.IRouter router;

    protected boolean duplicateProtection = false;
    protected long duplicateTimer;
    protected ScheduledExecutorService dupliocationScheduler = null;
    protected ScheduledFuture duplicationHandler = null;
    protected ConcurrentHashMap<String, StorageEntry> storageAnswers = new ConcurrentHashMap<String, StorageEntry>();

    protected boolean isAcceptUndefinedPeer  = false;

    protected ConcurrentHashMap<String, PeerImpl.Entry> incConnections = new ConcurrentHashMap<String, PeerImpl.Entry>();
    private ScheduledExecutorService connScheduler;
    private ScheduledFuture connHandler;

    protected INetWorkGuard networkGuard;
    protected INetwork network;
    protected Set<URI> predefinedPeerTable;

    protected IOverloadManager ovrManager;
    protected ScheduledExecutorService overloadScheduler = null;
    protected ScheduledFuture overloadHandler = null;



    protected class StorageEntry {

        private String duplicationKey;
        private long time = System.currentTimeMillis();
        private IMessage answer;

        public StorageEntry(IMessage message) {
            answer = message;
            duplicationKey = message.getDuplicationKey();
        }

        public IMessage getMessage() {
            return answer;
        }

        public long getTime() {
            return time;
        }

        public String getDuplicationKey() {
            return duplicationKey;
        }
    }    

    public MutablePeerTableImpl(Configuration config, MetaData metaData, org.jdiameter.server.api.IRouter router,
                                  ISessionFactory sessionFactory, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
                                  IMessageParser parser, INetwork network, IOverloadManager ovrManager) {
        this.metaData = metaData;
        this.config = config;
        this.router = router;
        this.sessionFactory = sessionFactory;
        this.fsmFactory = fsmFactory;
        this.transportFactory = trFactory;
        this.parser = parser;
        this.network = network;
        this.ovrManager = ovrManager;
        this.network.setPeerManager(this);
        this.isAcceptUndefinedPeer = config.getBooleanValue(AcceptUndefinedPeer.ordinal(), false);
        this.duplicateProtection = config.getBooleanValue(DuplicateProtection.ordinal(), (Boolean) DuplicateProtection.defValue());
        if (this.duplicateProtection)
            this.duplicateTimer = config.getLongValue(DuplicateTimer.ordinal(), (Long) DuplicateTimer.defValue());
        if (predefinedPeerTable == null)
            predefinedPeerTable = new CopyOnWriteArraySet<URI>();

        if (config instanceof MutableConfiguration)
            ((MutableConfiguration)config).addChangeListener(this);

        init(router, config, metaData, fsmFactory, transportFactory, parser);
    }

    protected Peer createPeer(int rating, String uri, MetaData metaData, Configuration globalConfig, Configuration peerConfig, IFsmFactory fsmFactory,
                              org.jdiameter.client.api.io.ITransportLayerFactory transportFactory, IMessageParser parser) throws InternalException, TransportException, URISyntaxException, UnknownServiceException {
        if (predefinedPeerTable == null)
            predefinedPeerTable = new CopyOnWriteArraySet<URI>();
        predefinedPeerTable.add(new URI(uri));
        if (peerConfig.getBooleanValue(PeerAttemptConnection.ordinal(), false))
            return new org.jdiameter.server.impl.PeerImpl(
                this, rating, new URI(uri), metaData.unwrap(IMetaData.class),
                globalConfig, peerConfig, sessionFactory, fsmFactory, transportFactory, parser, network, ovrManager, true, null
            );
        else
            return null;
    }

    public void setPeerTableListener(PeerTableListener peerTableListener) {
        //To do
    }

    public boolean elementChanged(int i, Object data) {
        Configuration newConf = (Configuration) data;
        stopTimeOut = newConf.getLongValue(StopTimeOut.ordinal(), (Long) StopTimeOut.defValue());
        duplicateTimer = newConf.getLongValue(DuplicateTimer.ordinal(), (Long) DuplicateTimer.defValue());
        isAcceptUndefinedPeer = newConf.getBooleanValue(AcceptUndefinedPeer.ordinal(), false);
        return true;
    }

    public boolean isDuplicateProtection() {
        return duplicateProtection;
    }

    public void start() throws IllegalDiameterStateException, IOException {
        router.start();
        // Start overload manager
        overloadScheduler = Executors.newScheduledThreadPool(1);
        Runnable overloadTask = new Runnable() {
                public void run() {
                    if (ovrManager != null)
                        for (Peer p : peerTable.values()) {
                            ((IPeer) p).notifyOvrManager(ovrManager);
                        }
                }
        };
        overloadHandler = overloadScheduler.scheduleAtFixedRate(overloadTask, 0, 1, TimeUnit.SECONDS);
        // Start duplication protection procedure
        if (duplicateProtection) {
            dupliocationScheduler = Executors.newScheduledThreadPool(1);
            Runnable duplicateTask = new Runnable() {
                public void run() {
                    long now = System.currentTimeMillis();
                    for (StorageEntry s : storageAnswers.values()) {
                        if (s != null && s.getTime() + duplicateTimer <= now)
                            storageAnswers.remove(s.getDuplicationKey());
                    }
                }
            };
            duplicationHandler = dupliocationScheduler.scheduleAtFixedRate(duplicateTask, duplicateTimer, duplicateTimer, TimeUnit.MILLISECONDS);
        }
        //
        connScheduler = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            public void run() {
                for (PeerImpl.Entry cEntry :incConnections.values()) {
                    if (cEntry != null && (System.currentTimeMillis() - cEntry.getCreatedTime() <= CONN_INVALIDATE_PERIOD)) {
                        logger.log(Level.FINE, "External connection released " + cEntry.getConnection());
                        cEntry.getConnection().disconnect();
                        incConnections.remove(cEntry.getConnection().getKey());
                    }
                }
            }
        };
        connHandler = connScheduler.scheduleAtFixedRate(task, CONN_INVALIDATE_PERIOD, CONN_INVALIDATE_PERIOD, TimeUnit.MILLISECONDS);
        // Start server socket
        try {
            networkGuard = createNetWorkGuard(transportFactory);
        } catch (TransportException e) {
            logger.log(Level.WARNING, "Can not create server socket", e);
        }
        // Connect to predefined peers
        for (Peer p : peerTable.values())
            try {
                p.connect();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Can not start connect procedure for peer" + p, e);
            }
        isStarted = true;
    }

    public Set<URI> getPredefinedPeerTable() {
        return predefinedPeerTable;
    }

    public ConcurrentHashMap<String, PeerImpl.Entry> getIncConnections() {
        if (incConnections == null)
            incConnections = new ConcurrentHashMap<String, PeerImpl.Entry>();
        return incConnections;
    }

    protected ConcurrentHashMap<String, String> alias = new ConcurrentHashMap<String, String>();

    private final Object regLock = new Object();
    // todo change (hack method)
    public void updatePeerTableEntry(String oldName, URI oldUri, String newName, URI newUri) {
        synchronized( regLock ) {
            alias.replace(oldName, newName);
            if(!peerTable.containsKey(newUri) && peerTable.containsKey(oldUri)) {
              peerTable.put(newUri, peerTable.remove(oldUri));
            }
        }
    }

    private INetWorkGuard createNetWorkGuard(final ITransportLayerFactory transportFactory) throws TransportException {
        return transportFactory.createNetWorkGuard(
            metaData.getLocalPeer().getIPAddresses()[0],
            metaData.getLocalPeer().getUri().getPort(),
            new INetWorkConnectionListener() {
                public void newNetWorkConnection(IConnection connection) {
                    synchronized( regLock ) {
                        IPeer p = null;
                        String host = connection.getRemoteAddress().getHostName();
                        if (alias.containsKey(host))
                            host = alias.get(host);
                        // find into predefined table
                        for (URI u : predefinedPeerTable) {
                            if (u.getFQDN().equals(host)) {
                                p = (IPeer) peerTable.get(u);
                                break;
                            }
                        }
                        // find in peertable for peer already connected to server but not removed
                        if (p == null)
                            for (URI u : peerTable.keySet()) {
                                if (u.getFQDN().equals(host)) {
                                    p = (IPeer) peerTable.get(u);
                                    break;
                                }
                            }
                        if (p != null) {
                            p.addIncomingConnection(connection);
                        } else {
                            if (isAcceptUndefinedPeer) {
                                alias.put(host, host);
                                try {
                                    int port = connection.getRemotePort();
                                    p = new org.jdiameter.server.impl.PeerImpl(
                                        MutablePeerTableImpl.this, 0, new URI("aaa://"+host+":"+port), metaData.unwrap(IMetaData.class),
                                        config, null, sessionFactory, fsmFactory, transportFactory, parser, network, ovrManager, false, connection
                                    );
                                    peerTable.put(p.getUri(), p);
                                } catch (Exception e) {
                                    logger.log(Level.WARNING, "Can not create peer", e);
                                }
                            } else {
                                logger.info("Skip anonymous connection " + connection.toString());
                                try {
                                    connection.disconnect();
                                    connection.release();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
        );
    }

    public void stopping() {
        super.stopping();
        if (networkGuard != null) {
            networkGuard.destroy();
            networkGuard = null;
        }
        //
        if (overloadScheduler != null) {
            Executors.unconfigurableScheduledExecutorService(overloadScheduler);
            overloadScheduler = null;
            overloadHandler.cancel(true);
            overloadHandler = null;            
        }
        //
        if (dupliocationScheduler != null) {
            Executors.unconfigurableScheduledExecutorService(dupliocationScheduler);
            dupliocationScheduler = null;
        }
        if (duplicationHandler != null) {
            duplicationHandler.cancel(true);
            duplicationHandler = null;
        }
        //
        if (connScheduler != null) {
            Executors.unconfigurableScheduledExecutorService(connScheduler);
            connScheduler = null;
        }
        if (connHandler != null) {
            connHandler.cancel(true);
            connHandler = null;
        }
        //
        storageAnswers.clear();
    }



    public Peer addPeer(URI peerURI, String realm, boolean connecting) {
        try {
            Configuration peerConfig = null;
            Configuration[] peers = config.getChildren( PeerTable.ordinal() );
            // find peer config
            for (Configuration c : peers)
                if (peerURI.getFQDN().equals(c.getStringValue(PeerName.ordinal(), ""))) {
                    peerConfig = c;
                    break;
                }
            if (peerConfig == null) {
                peerConfig = new EmptyConfiguration(false).add(PeerAttemptConnection, connecting);
            }
            IPeer peer = (IPeer) createPeer(0, peerURI.getFQDN(), metaData, config, peerConfig, fsmFactory, transportFactory, parser);
            if (peer == null) return null;
            peer.setRealm(realm);
            peerTable.put(peer.getUri(), peer);
            boolean found = false;
            for (Realm r : router.getRealms()) {
                if (r.getName().equals(realm)) {
                    r.addPeerName(peerURI.getFQDN());
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new IllegalArgumentException("Incorrect realm name");
            if (connecting)
                peer.connect();
            return peer;
        } catch(Exception e) {
            logger.log(Level.INFO, "Can not add peer", e);
            return null;
        }
    }

    public Set<Realm> getAllRealms() {
        return router.getRealms();
    }

    public Peer removePeer(String uri) {
        try {
            URI peerUri = new URI(uri);
            if (peerTable.containsKey(peerUri)) {
                peerTable.get(peerUri).disconnect();
            }
            predefinedPeerTable.remove(peerUri);
            return peerTable.remove(peerUri);
        } catch (Exception e) {
            logger.log(Level.INFO, "Can not remove peer", e);
            return null;
        }
    }



    public Statistic getStatistic(String name) {
        for (Peer p : peerTable.values()) {
            if (p.getUri().getFQDN().equals(name)) {
                return ((IPeer)p).getStatistic();
            }
        }
        return null;
    }

    public IMessage isDuplicate(IMessage request) {
        String key = request.getDuplicationKey();
        if (key != null && storageAnswers != null) {
            StorageEntry entry = storageAnswers.get(key);
            return entry != null ? entry.getMessage() : null;
        }
        return null;
    }

    public void saveToDuplicate(String key, IMessage answer) {
        if (storageAnswers != null && storageAnswers.size() < MAX_DUPLICATE_ANSWERS) {
            if (key != null)
                storageAnswers.put(key, new StorageEntry(answer));
        } 
    }

    public ISessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        boolean isWrapp = super.isWrapperFor(aClass);
        if (aClass == MutablePeerTable.class)
            return true;
        else if (aClass == Network.class)
            return true;
        else
            return isWrapp;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        if (aClass == MutablePeerTable.class)
            return (T) assembler.getComponentInstance(aClass);
        if (aClass == Network.class)
            return (T) assembler.getComponentInstance(aClass);
        return null;
    }
}
