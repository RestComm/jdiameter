/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.controller.PeerTableImpl;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.*;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.server.api.*;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.jdiameter.server.api.io.ITransportLayerFactory;
import org.jdiameter.server.impl.helpers.EmptyConfiguration;
import static org.jdiameter.server.impl.helpers.Parameters.*;
import org.jdiameter.server.impl.helpers.StatisticAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MutablePeerTableImpl extends PeerTableImpl implements IMutablePeerTable, ConfigurationListener {

  private static final Logger logger = LoggerFactory.getLogger(MutablePeerTableImpl.class);

  private static final int MAX_DUPLICATE_ANSWERS = 5000;
  private static final int CONN_INVALIDATE_PERIOD = 60000;

  protected Configuration config;
  protected ISessionFactory sessionFactory;
  protected IFsmFactory fsmFactory;
  protected ITransportLayerFactory transportFactory;
  protected IMessageParser parser;
  protected org.jdiameter.server.api.IRouter router;

  // Duplicate handling -------------------------------------------------------
  protected boolean duplicateProtection = false;
  protected long duplicateTimer;
  protected ScheduledExecutorService duplicationScheduler = null;
  @SuppressWarnings("unchecked")
  protected ScheduledFuture duplicationHandler = null;
  protected ConcurrentHashMap<String, StorageEntry> storageAnswers = new ConcurrentHashMap<String, StorageEntry>();

  protected boolean isAcceptUndefinedPeer  = false;

  // Connections handling -----------------------------------------------------
  private ConcurrentHashMap<String, IConnection> incConnections;
  private ScheduledExecutorService connScheduler;
  @SuppressWarnings("unchecked")
  private ScheduledFuture connHandler;

  // Network management -------------------------------------------------------
  protected INetworkGuard networkGuard;
  protected INetwork network;
  protected Set<URI> predefinedPeerTable;

  // Overload handling --------------------------------------------------------
  protected IOverloadManager ovrManager;
  protected ScheduledExecutorService overloadScheduler = null;
  @SuppressWarnings("unchecked")
  protected ScheduledFuture overloadHandler = null;
  protected PeerTableListener peerTableListener = null;
  protected IStatisticFactory statisticFactory;

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

  public MutablePeerTableImpl(Configuration config, MetaData metaData,IContainer stack, org.jdiameter.server.api.IRouter router,
      ISessionFactory sessionFactory, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
      IMessageParser parser, INetwork network, IOverloadManager ovrManager,
      IStatisticFactory statisticFactory, IConcurrentFactory concurrentFactory) {
    this.metaData = metaData;
    this.config = config;
    this.router = router;
    this.sessionFactory = sessionFactory;
    this.statisticFactory = statisticFactory;
    this.concurrentFactory = concurrentFactory;
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

    init(stack,router, config, metaData, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
  }

  @Override
  protected Peer createPeer(int rating, String uri, String ip, String portRange, MetaData metaData, Configuration globalConfig,
      Configuration peerConfig, org.jdiameter.client.api.fsm.IFsmFactory fsmFactory,
      org.jdiameter.client.api.io.ITransportLayerFactory transportFactory,
      IStatisticFactory statisticFactory, IConcurrentFactory concurrentFactory,
      IMessageParser parser) throws InternalException, TransportException, URISyntaxException, UnknownServiceException {
    if (predefinedPeerTable == null) {
      predefinedPeerTable = new CopyOnWriteArraySet<URI>();
    }
    predefinedPeerTable.add(new URI(uri));
    if (peerConfig.getBooleanValue(PeerAttemptConnection.ordinal(), false)) {
      return newPeerInstance(rating, new URI(uri), ip, portRange, true, null,
          metaData, globalConfig, peerConfig, (IFsmFactory) fsmFactory,
          (ITransportLayerFactory) transportFactory, parser, statisticFactory, concurrentFactory);
    }
    else {
      return null;
    }
  }

  protected IPeer newPeerInstance(int rating, URI uri, String ip, String portRange, boolean attCnn, IConnection connection,
      MetaData metaData, Configuration globalConfig, Configuration peerConfig, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IMessageParser parser,
      IStatisticFactory statisticFactory, IConcurrentFactory concurrentFactory) throws URISyntaxException, UnknownServiceException, InternalException, TransportException {
    return new org.jdiameter.server.impl.PeerImpl(
        rating, uri, ip, portRange, attCnn, connection,
        this, (org.jdiameter.server.api.IMetaData) metaData, globalConfig, peerConfig, sessionFactory,
        fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser, network, ovrManager, sessionDatasource
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
    router.start();
    // Start overload manager
    overloadScheduler = concurrentFactory.getScheduledExecutorService(PeerOverloadTimer.name());
    Executors.newScheduledThreadPool(1);
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
      duplicationScheduler = concurrentFactory.getScheduledExecutorService(DuplicationMessageTimer.name());
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
      duplicationHandler = duplicationScheduler.scheduleAtFixedRate(duplicateTask, duplicateTimer, duplicateTimer, TimeUnit.MILLISECONDS);
    }
    //
    connScheduler = concurrentFactory.getScheduledExecutorService(ConnectionTimer.name());
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
      networkGuard = createNetworkGuard(transportFactory);
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

  private INetworkGuard createNetworkGuard(final ITransportLayerFactory transportFactory) throws TransportException {
    return transportFactory.createNetworkGuard(
        metaData.getLocalPeer().getIPAddresses()[0],
        metaData.getLocalPeer().getUri().getPort(),
        new INetworkConnectionListener() {
          public void newNetworkConnection(final IConnection connection) {
            synchronized (regLock) {
              final IConnectionListener listener = new IConnectionListener() {

                public void connectionOpened(String connKey) {
                  logger.debug("Connection {} opened", connKey);
                }

                @SuppressWarnings("unchecked")
                public void connectionClosed(String connKey, List notSended) {
                  logger.debug("Connection {} closed", connKey);
                  unregister(true);
                }

                public void messageReceived(String connKey, IMessage message) {
                  logger.debug("Message {} received to peer {}", new Object[]{message, connKey});
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
                      logger.debug("Add {} connection to peer {}", new Object[]{connection, peer});
                      peer.addIncomingConnection(connection);
                      try {
                        logger.debug("Handle {} message on peer {}", new Object[]{message, peer});
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

                          peer = newPeerInstance(0, uri, connection.getRemoteAddress().getHostAddress(), null, false, connection,
                              metaData, config, null, fsmFactory, transportFactory, parser, statisticFactory, concurrentFactory);
                          logger.debug("Add {} peer {}", peer);
                          appendPeerToPeerTable(peer);
                          logger.debug("Handle {} message on peer {}", new Object[]{message, peer});
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
      concurrentFactory.shutdownNow(overloadScheduler);
      overloadScheduler = null;
      overloadHandler.cancel(true);
      overloadHandler = null;            
    }
    //
    if (duplicationScheduler != null) {
      concurrentFactory.shutdownNow(duplicationScheduler);
      duplicationScheduler = null;
    }
    if (duplicationHandler != null) {
      duplicationHandler.cancel(true);
      duplicationHandler = null;
    }
    //
    if (connScheduler != null) {
      concurrentFactory.shutdownNow(connScheduler);
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
      IPeer peer = (IPeer) createPeer(0, peerURI.toString(), null, null, metaData, config, peerConfig, fsmFactory, 
          transportFactory, statisticFactory, concurrentFactory, parser);
      if (peer == null) return null;
      peer.setRealm(realm);
      appendPeerToPeerTable(peer);
      boolean found = false;
      for (Realm r : router.getRealms()) {
        if (r.getName().equals(realm)) {
          r.addPeerName(peerURI.toString());
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
        return StatisticAdaptor.adapt(((IPeer) p).getStatistic());
      }
    }
    return null;
  }

  public IMessage isDuplicate(IMessage request) {
    String key = request.getDuplicationKey();
    if (key != null && storageAnswers != null) {
      StorageEntry entry = storageAnswers.get(key);
      return entry != null ? (IMessage) entry.getMessage().clone() : null;
    }
    return null;
  }

  public void saveToDuplicate(String key, IMessage answer) {
    if (storageAnswers != null && storageAnswers.size() < MAX_DUPLICATE_ANSWERS) {
      if (key != null) {
        storageAnswers.put(key, new StorageEntry((IMessage) answer.clone()));
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
