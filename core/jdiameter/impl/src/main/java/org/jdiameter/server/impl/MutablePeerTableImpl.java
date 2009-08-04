package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

  private ConcurrentHashMap<String, IConnection> incConnections;
  private ScheduledExecutorService connScheduler;
  private ScheduledFuture connHandler;

  protected INetWorkGuard networkGuard;
  protected INetwork network;
  protected Set<URI> predefinedPeerTable;

  protected IOverloadManager ovrManager;
  protected ScheduledExecutorService overloadScheduler = null;
  protected ScheduledFuture overloadHandler = null;
  protected PeerTableListener peerTableListener = null;

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
    if (this.duplicateProtection) {
      this.duplicateTimer = config.getLongValue(DuplicateTimer.ordinal(), (Long) DuplicateTimer.defValue());
    }
    if (predefinedPeerTable == null) {
      predefinedPeerTable = new CopyOnWriteArraySet<URI>();
    }
    if (config instanceof MutableConfiguration) {
      ((MutableConfiguration) config).addChangeListener(this);
    }

    init(router, config, metaData, fsmFactory, transportFactory, parser);
  }

  protected Peer createPeer(int rating, String uri, String ip, String portRange, MetaData metaData, Configuration globalConfig, Configuration peerConfig, IFsmFactory fsmFactory,
      org.jdiameter.client.api.io.ITransportLayerFactory transportFactory, IMessageParser parser) throws InternalException, TransportException, URISyntaxException, UnknownServiceException {
    if (predefinedPeerTable == null) {
      predefinedPeerTable = new CopyOnWriteArraySet<URI>();
    }
    predefinedPeerTable.add(new URI(uri));
    if (peerConfig.getBooleanValue(PeerAttemptConnection.ordinal(), false)) {
      return newPeerInstance(rating, new URI(uri), ip, portRange, metaData, globalConfig, peerConfig, fsmFactory, (org.jdiameter.server.api.io.ITransportLayerFactory)transportFactory, parser, true, null);
    }
    else {
      return null;
    }
  }

  protected IPeer newPeerInstance(int rating, URI uri, String ip, String portRange, MetaData metaData, Configuration globalConfig, Configuration peerConfig, IFsmFactory fsmFactory,
      org.jdiameter.server.api.io.ITransportLayerFactory transportFactory, IMessageParser parser,
      boolean attCnn, IConnection connection ) throws URISyntaxException, UnknownServiceException, InternalException, TransportException {
    return new org.jdiameter.server.impl.PeerImpl(this, rating, uri, ip, portRange, (org.jdiameter.server.api.IMetaData) metaData,
        globalConfig, peerConfig, sessionFactory, fsmFactory, transportFactory, parser, network, ovrManager, attCnn, connection
    );
  }

  public void setPeerTableListener(PeerTableListener peerTableListener) {
    this.peerTableListener = peerTableListener;
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

  public void start() throws IllegalDiameterStateException, IOException { // TODO: use parent method
    if (peerTaskExecutor.isShutdown()) {
      peerTaskExecutor = Executors.newCachedThreadPool();
    }
    router.start();
    // Start overload manager
    overloadScheduler = Executors.newScheduledThreadPool(1);
    Runnable overloadTask = new Runnable() {
      public void run() {
        if (ovrManager != null) {
          for (Peer p : peerTable.values()) {
            ((IPeer) p).notifyOvrManager(ovrManager);
          }
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
            if (s != null && s.getTime() + duplicateTimer <= now) {
              storageAnswers.remove(s.getDuplicationKey());
            }
          }
        }
      };
      duplicationHandler = dupliocationScheduler.scheduleAtFixedRate(duplicateTask, duplicateTimer, duplicateTimer, TimeUnit.MILLISECONDS);
    }
    //
    connScheduler = Executors.newScheduledThreadPool(1);
    Runnable connectionCheckTask = new Runnable() {
      public void run() {
        Map<String, IConnection> connections = getIncConnections();
        for (IConnection connection : connections.values()) {
          if (System.currentTimeMillis() - connection.getCreatedTime() <= CONN_INVALIDATE_PERIOD) {
            logger.debug("External connection released by timeout {}", connection);
            try {
              connection.remAllConnectionListener();
              connection.release();
            }
            catch (IOException e) {
              logger.debug("Can not release connection", e);
            }
            connections.remove(connection.getKey());
          }
        }
      }
    };
    connHandler = connScheduler.scheduleAtFixedRate(connectionCheckTask, CONN_INVALIDATE_PERIOD, CONN_INVALIDATE_PERIOD, TimeUnit.MILLISECONDS);
    // Start server socket
    try {
      networkGuard = createNetWorkGuard(transportFactory);
    }
    catch (TransportException e) {
      logger.debug("Can not create server socket", e);
    }
    // Connect to predefined peers
    for (Peer p : peerTable.values()) {
      try {
        p.connect();
      }
      catch (Exception e) {
        logger.warn("Can not start connect procedure for peer {}", p, e);
      }
    }
    isStarted = true;
  }

  public Set<URI> getPredefinedPeerTable() {
    return predefinedPeerTable;
  }

  public ConcurrentHashMap<String, IConnection> getIncConnections() {
    if (incConnections == null) {
      incConnections = new ConcurrentHashMap<String, IConnection>();
    }

    return incConnections;
  }

  private final Object regLock = new Object();

  private INetWorkGuard createNetWorkGuard(final ITransportLayerFactory transportFactory) throws TransportException {
    return transportFactory.createNetWorkGuard(
        metaData.getLocalPeer().getIPAddresses()[0],
        metaData.getLocalPeer().getUri().getPort(),
        new INetWorkConnectionListener() {
          public void newNetWorkConnection(final IConnection connection) {
            synchronized (regLock) {
              final IConnectionListener listener = new IConnectionListener() {

                public void connectionOpened(String connKey) {
                  logger.debug("Connection {} opened", connKey);
                }

                public void connectionClosed(String connKey, List notSended) {
                  logger.debug("Connection {} closed", connKey);
                  unregister(true);
                }

                public void messageReceived(String connKey, IMessage message) {
                  if (message.isRequest() && message.getCommandCode() == Message.CAPABILITIES_EXCHANGE_REQUEST) {
                    connection.remConnectionListener(this);
                    IPeer peer = null;
                    String host;
                    try {
                      host = message.getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString();
                    }
                    catch (AvpDataException e) {
                      logger.warn("Can not find ORIG_HOST avp in CER", e);
                      unregister(true);
                      return;
                    }
                    boolean foundInpredefinedTable = false;
                    // find into predefined table
                    for (URI uri : predefinedPeerTable) {
                      if (uri.getFQDN().equals(host)) {
                        peer = (IPeer) peerTable.get(uri);
                        foundInpredefinedTable = true; // found but not init
                        break;
                      }
                    }
                    // find in peertable for peer already connected to server but not removed
                    if (peer == null) {
                      for (URI uri : peerTable.keySet()) {
                        if (uri.getFQDN().equals(host)) {
                          peer = (IPeer) peerTable.get(uri);
                          break;
                        }
                      }
                    }

                    if (peer != null) {
                      peer.addIncomingConnection(connection);
                      try {
                        peer.handleMessage(message.isRequest() ? EventTypes.CER_EVENT : EventTypes.CER_EVENT, message, connKey);
                      }
                      catch (Exception e) {
                        logger.debug("Can not process CER message", e);
                      }
                    }
                    else {
                      if (isAcceptUndefinedPeer || foundInpredefinedTable) {
                        try {
                          int port = connection.getRemotePort();

                          boolean hostAsUri = config.getBooleanValue(UseUriAsFqdn.ordinal(), (Boolean) UseUriAsFqdn.defValue());
                          URI uri;
                          if (hostAsUri || host.startsWith("aaa://")) {
                            uri = new URI(host);
                          }
                          else {
                            uri = new URI("aaa://" + host + ":" + port);
                          }

                          peer = newPeerInstance(0, uri, connection.getRemoteAddress().getHostAddress(), null, metaData,
                              config, null, fsmFactory, transportFactory, parser, false, connection);
                          appendPeerToPeerTable(peer);
                          peer.handleMessage(message.isRequest() ? EventTypes.CER_EVENT : EventTypes.CER_EVENT, message, connKey);
                        }
                        catch (Exception e) {
                          logger.warn("Can not create peer", e);
                          unregister(true);
                        }
                      }
                      else {
                        logger.info("Skip anonymous connection {}", connection.toString());                              
                        unregister(true);
                      }
                    }
                  }
                  else {
                    logger.debug("Unknown message {} by connection {}", new Object[]{message, connKey});
                    unregister(true);
                  }
                }

                public void internalError(String connKey, IMessage message, TransportException cause) {
                  logger.debug("Connection {} internalError {}", new Object[]{connKey, cause});
                  unregister(true);
                }

                public void unregister(boolean release) {
                  getIncConnections().remove(connection.getKey());
                  connection.remConnectionListener(this);
                  if (release && connection.isConnected()) {
                    try {
                      connection.release();
                    }
                    catch (IOException e) {
                      logger.debug("Can not release connection {}", connection);
                    }
                  }
                }
              };
              getIncConnections().put(connection.getKey(), connection);
              connection.addConnectionListener(listener);
            }
          }
        }
    );
  }

  private void appendPeerToPeerTable(IPeer peer) {
    peerTable.put(peer.getUri(), peer);
    if (peerTableListener != null) {
      peerTableListener.peerAccepted(peer);
    }
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
      Configuration[] peers = config.getChildren(PeerTable.ordinal());
      // find peer config
      for (Configuration c : peers)
        if (peerURI.getFQDN().equals(c.getStringValue(PeerName.ordinal(), ""))) {
          peerConfig = c;
          break;
        }
      if (peerConfig == null) {
        peerConfig = new EmptyConfiguration(false).add(PeerAttemptConnection, connecting);
      }
      IPeer peer = (IPeer) createPeer(0, peerURI.getFQDN(), null, null, metaData, config, peerConfig, fsmFactory, transportFactory, parser);
      if (peer == null) return null;
      peer.setRealm(realm);
      appendPeerToPeerTable(peer);
      boolean found = false;
      for (Realm r : router.getRealms()) {
        if (r.getName().equals(realm)) {
          r.addPeerName(peerURI.getFQDN());
          found = true;
          break;
        }
      }
      if (!found) {
        throw new IllegalArgumentException("Incorrect realm name");
      }
      if (connecting) {
        peer.connect();
      }
      return peer;
    }
    catch(Exception e) {
      logger.debug("Can not add peer", e);
      return null;
    }
  }

  public Set<Realm> getAllRealms() {
    return router.getRealms();
  }

  public Peer removePeer(String host) {
    try {
      URI peerUri = null;
      for (URI u : peerTable.keySet()) {
        if (u.getFQDN().equals(host)) {
          peerUri = u;
          peerTable.get(u).disconnect();
        }
      }
      if (peerUri != null) {
        predefinedPeerTable.remove(peerUri);
        Peer removedPeer = peerTable.remove(peerUri);
        if (peerTableListener != null) {
          peerTableListener.peerRemoved(removedPeer);
        }

        return removedPeer;
      }
      else {
        return null;
      }       
    } catch (Exception e) {
      logger.debug("Can not remove peer", e);
      return null;
    }
  }

  public Statistic getStatistic(String name) {
    for (Peer p : peerTable.values()) {
      if (p.getUri().getFQDN().equals(name)) {
        return ((IPeer) p).getStatistic();
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
      if (key != null) {
        storageAnswers.put(key, new StorageEntry(answer));
      }
    } 
  }

  public ISessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    boolean isWrapp = super.isWrapperFor(aClass);

    return aClass == MutablePeerTable.class || aClass == Network.class || isWrapp;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    if (aClass == MutablePeerTable.class) {
      return (T) assembler.getComponentInstance(aClass);
    }
    if (aClass == Network.class) {
      return (T) assembler.getComponentInstance(aClass);
    }
    return null;
  }
}
