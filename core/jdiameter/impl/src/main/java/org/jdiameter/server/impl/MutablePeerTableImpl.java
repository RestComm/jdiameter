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

package org.jdiameter.server.impl;

import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;
import static org.jdiameter.client.impl.helpers.Parameters.StopTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.UseUriAsFqdn;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.ConnectionTimer;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.DuplicationMessageTimer;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.PeerOverloadTimer;
import static org.jdiameter.server.impl.helpers.Parameters.AcceptUndefinedPeer;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateProtection;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateSize;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateTimer;
import static org.jdiameter.server.impl.helpers.Parameters.PeerAttemptConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.ConfigurationListener;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.api.MutablePeerTable;
import org.jdiameter.api.Network;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerTableListener;
import org.jdiameter.api.Realm;
import org.jdiameter.api.Statistic;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.StackState;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.controller.PeerTableImpl;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.server.api.IFsmFactory;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;
import org.jdiameter.server.api.IOverloadManager;
import org.jdiameter.server.api.IPeer;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.jdiameter.server.api.io.ITransportLayerFactory;
import org.jdiameter.server.impl.helpers.EmptyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MutablePeerTableImpl extends PeerTableImpl implements IMutablePeerTable, ConfigurationListener {

  private static final Logger logger = LoggerFactory.getLogger(MutablePeerTableImpl.class);

  private static final int CONN_INVALIDATE_PERIOD = 60000;
  private static final int MAX_PEER_TABLE_SIZE = 10000;

  protected Configuration config;
  protected ISessionFactory sessionFactory;
  protected IFsmFactory fsmFactory;
  protected ITransportLayerFactory transportFactory;
  protected IMessageParser parser;
  protected org.jdiameter.server.api.IRouter router;

  // Duplicate handling -------------------------------------------------------
  protected boolean duplicateProtection = false;
  protected int duplicateSize;
  protected long duplicateTimer;
  protected ScheduledExecutorService duplicationScheduler = null;
  protected ScheduledFuture duplicationHandler = null;
  protected ConcurrentHashMap<String, StorageEntry> storageAnswers = new ConcurrentHashMap<String, StorageEntry>();

  protected boolean isAcceptUndefinedPeer  = false;

  // Connections handling -----------------------------------------------------
  private ConcurrentHashMap<String, IConnection> incConnections;
  private ScheduledExecutorService connScheduler;
  private ScheduledFuture connHandler;

  // Network management -------------------------------------------------------
  protected INetworkGuard networkGuard;
  protected INetwork network;
  protected Set<String> predefinedPeerTable;

  // Overload handling --------------------------------------------------------
  protected IOverloadManager ovrManager;
  protected ScheduledExecutorService overloadScheduler = null;
  protected ScheduledFuture overloadHandler = null;
  protected PeerTableListener peerTableListener = null;
  protected IStatisticManager statisticFactory;

  private IContainer stack;

  protected class StorageEntry {

    private String duplicationKey;
    private long time = System.currentTimeMillis();
    private IMessage answer;

    public StorageEntry(IMessage message) {
      answer = message;
      // duplicationKey = message.getDuplicationKey(); doesn't work because it's answer
      String[] originInfo = router.getRequestRouteInfo(answer);
      duplicationKey = message.getDuplicationKey(originInfo[0], message.getEndToEndIdentifier());
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

  public MutablePeerTableImpl(Configuration config, MetaData metaData, IContainer stack, org.jdiameter.server.api.IRouter router,
      ISessionFactory sessionFactory, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
      IMessageParser parser, INetwork network, IOverloadManager ovrManager,
      IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory) {
    logger.debug("MutablePeerTableImpl is being created");
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
    this.stack = stack;
    this.isAcceptUndefinedPeer = config.getBooleanValue(AcceptUndefinedPeer.ordinal(), false);
    this.duplicateProtection = config.getBooleanValue(DuplicateProtection.ordinal(), (Boolean) DuplicateProtection.defValue());
    if (this.duplicateProtection) {
      this.duplicateTimer = config.getLongValue(DuplicateTimer.ordinal(), (Long) DuplicateTimer.defValue());
      this.duplicateSize = config.getIntValue(DuplicateSize.ordinal(), (Integer) DuplicateSize.defValue());
    }
    logger.debug("Duplicate Protection Configuration: Enabled? {}, Timer: {}, Size: {}",
        new Object[]{this.duplicateProtection, this.duplicateTimer, this.duplicateSize});
    if (predefinedPeerTable == null) {
      predefinedPeerTable = new CopyOnWriteArraySet<String>();
    }
    if (config instanceof MutableConfiguration) {
      ((MutableConfiguration) config).addChangeListener(this);
    }

    logger.debug("MutablePeerTableImpl is starting initialisation by calling init on super class");
    init(stack, router, config, metaData, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
    logger.debug("MutablePeerTableImpl has finished initialisation");
  }

  @Override
  protected Peer createPeer(int rating, String uri, String ip, String portRange, MetaData metaData, Configuration globalConfig,
      Configuration peerConfig, org.jdiameter.client.api.fsm.IFsmFactory fsmFactory,
      org.jdiameter.client.api.io.ITransportLayerFactory transportFactory,
      IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory,
      IMessageParser parser) throws InternalException, TransportException, URISyntaxException, UnknownServiceException {
    logger.debug("Creating Peer for URI [{}]", uri);
    if (predefinedPeerTable == null) {
      logger.debug("Creating new empty predefined peer table");
      predefinedPeerTable = new CopyOnWriteArraySet<String>();
    }
    logger.debug("Adding URI [{}] to predefinedPeerTable", uri);
    predefinedPeerTable.add(new URI(uri).getFQDN());
    if (peerConfig.getBooleanValue(PeerAttemptConnection.ordinal(), false)) {
      logger.debug("Peer at URI [{}] is configured to attempt a connection (acting as a client) and a new peer instance will be created and returned", uri);
      return newPeerInstance(rating, new URI(uri), ip, portRange, true, null,
          metaData, globalConfig, peerConfig, (IFsmFactory) fsmFactory,
          (ITransportLayerFactory) transportFactory, parser, statisticFactory, concurrentFactory);
    }
    else {
      logger.debug("Peer at URI [{}] is configured to NOT attempt a connection (i.e. acting as a server) and null will be returned", uri);
      return null;
    }
  }

  protected IPeer newPeerInstance(int rating, URI uri, String ip, String portRange, boolean attCnn, IConnection connection,
      MetaData metaData, Configuration globalConfig, Configuration peerConfig, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IMessageParser parser,
      IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory)
          throws URISyntaxException, UnknownServiceException, InternalException, TransportException {
    logger.debug("Creating and returning a new Peer Instance for URI [{}].", uri);
    return new org.jdiameter.server.impl.PeerImpl(
            rating, uri, ip, portRange, attCnn, connection,
            this, (org.jdiameter.server.api.IMetaData) metaData, globalConfig, peerConfig, sessionFactory,
            fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser, network, ovrManager, sessionDatasource
            );
  }

  @Override
  public void setPeerTableListener(PeerTableListener peerTableListener) {
    this.peerTableListener = peerTableListener;
  }

  @Override
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

  @Override
  public void start() throws IllegalDiameterStateException, IOException { // TODO: use parent method
    logger.debug("Starting MutablePeerTableImpl. Starting router, overload scheduler, connection check timer, etc.");
    router.start();
    // Start overload manager
    overloadScheduler = concurrentFactory.getScheduledExecutorService(PeerOverloadTimer.name());

    Runnable overloadTask = new Runnable() {
      @Override
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
        @Override
        public void run() {
          long now = System.currentTimeMillis();
          if (logger.isDebugEnabled()) {
            logger.debug("Running Duplicate Cleaning Task. Duplicate Storage size is: {}. Removing entries with time <= '{}'",
                storageAnswers.size(), now - duplicateTimer);
          }
          for (StorageEntry s : storageAnswers.values()) {
            if (s != null && s.getTime() + duplicateTimer <= now) {
              if (logger.isTraceEnabled()) {
                logger.trace("Duplicate Cleaning Task - Removing Entry with key '{}' and time '{}'", s.getDuplicationKey(), s.getTime());
              }
              storageAnswers.remove(s.getDuplicationKey());
            }
            else {
              if (logger.isTraceEnabled()) {
                logger.trace("Duplicate Cleaning Task - Skipping Entry with key '{}' and time '{}'", s.getDuplicationKey(), s.getTime());
              }
            }
          }
          if (logger.isDebugEnabled()) {
            logger.debug("Completed Duplicate Cleaning Task. New Duplicate Storage size is: {}. Total task runtime: {}ms",
                storageAnswers.size(), System.currentTimeMillis() - now);
          }
        }
      };
      duplicationHandler = duplicationScheduler.scheduleAtFixedRate(duplicateTask, duplicateTimer, duplicateTimer, TimeUnit.MILLISECONDS);
    }
    //
    connScheduler = concurrentFactory.getScheduledExecutorService(ConnectionTimer.name());
    Runnable connectionCheckTask = new Runnable() {
      @Override
      public void run() {
        Map<String, IConnection> connections = getIncConnections();
        for (IConnection connection : connections.values()) {
          if (System.currentTimeMillis() - connection.getCreatedTime() >= CONN_INVALIDATE_PERIOD) {
            logger.debug("External connection released by timeout [{}]", connection.getKey());
            try {
              connection.remAllConnectionListener();
              connection.release();
            }
            catch (IOException e) {
              logger.debug("Unable to release connection", e);
            }
            connections.remove(connection.getKey());
          }
        }
      }
    };
    connHandler = connScheduler.scheduleAtFixedRate(connectionCheckTask, CONN_INVALIDATE_PERIOD, CONN_INVALIDATE_PERIOD, TimeUnit.MILLISECONDS);
    // Start server socket
    try {
      logger.debug("Creating network guard");
      networkGuard = createNetworkGuard(transportFactory);
    }
    catch (TransportException e) {
      // We want the root cause, that's what matters to us...
      Throwable t = e;
      while (t.getCause() != null) {
        t = t.getCause();
      }
      Peer p = stack.getMetaData().getLocalPeer();
      String ips = "";
      for (InetAddress ip : p.getIPAddresses()) {
        ips += " " + ip.getHostAddress() + ":" + p.getUri().getPort();
      }
      logger.error("Unable to create server socket for LocalPeer '{}' at{} ({}).", new Object[]{p.getUri().getFQDN(), ips, t.getMessage()});
      logger.debug("Unable to create server socket", e);
    }
    // Connect to predefined peers
    for (Peer p : peerTable.values()) {
      try {
        if (((IPeer) p).isAttemptConnection()) {
          p.connect();
        }
      }
      catch (Exception e) {
        logger.warn("Unable to start connect procedure for peer [" + p + "]", e);
      }
    }
    isStarted = true;
  }

  public Set<String> getPredefinedPeerTable() {
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
        metaData.getLocalPeer().getIPAddresses(),
        metaData.getLocalPeer().getUri().getPort(),
        new INetworkConnectionListener() {
          @Override
          public void newNetworkConnection(final IConnection connection) {
            //PCB added logging
            logger.debug("newNetworkConnection. connection [{}]", connection.getKey());
            synchronized (regLock) {
              final IConnectionListener listener = new IConnectionListener() {
                @Override
                public void connectionOpened(String connKey) {
                  logger.debug("Connection [{}] opened", connKey);
                }

                @Override
                @SuppressWarnings("unchecked")
                public void connectionClosed(String connKey, List notSended) {
                  logger.debug("Connection [{}] closed", connKey);
                  unregister(true);
                }

                @Override
                public void messageReceived(String connKey, IMessage message) {
                  logger.debug("Message [{}] received to peer [{}]", message, connKey);
                  if (message.isRequest() && message.getCommandCode() == Message.CAPABILITIES_EXCHANGE_REQUEST) {
                    connection.remConnectionListener(this);
                    IPeer peer = null;
                    String host;
                    try {
                      host = message.getAvps().getAvp(Avp.ORIGIN_HOST).getDiameterIdentity();
                      logger.debug("Origin-Host in new received message is [{}]", host);
                    }
                    catch (AvpDataException e) {
                      logger.warn("Unable to retrieve find Origin-Host AVP in CER", e);
                      unregister(true);
                      return;
                    }
                    String realm;
                    try {
                      realm = message.getAvps().getAvp(Avp.ORIGIN_REALM).getDiameterIdentity();
                      logger.debug("Origin-Realm in new received message is [{}]", realm);
                    } catch (AvpDataException e) {
                      logger.warn("Unable to retrieve find Origin-Realm AVP in CER", e);
                      unregister(true);
                      return;
                    }

                    boolean foundInPredefinedTable = false;
                    // find into predefined table
                    for (String fqdn : predefinedPeerTable) {
                      if (logger.isDebugEnabled()) {
                        logger.debug("Checking against entry in predefinedPeerTable with FQDN [{}]", fqdn);
                      }
                      if (fqdn.equals(host)) {
                        if (logger.isDebugEnabled()) {
                          logger.debug("{} == {}", fqdn, host);
                        }
                        peer = (IPeer) peerTable.get(fqdn);
                        foundInPredefinedTable = true; // found but not init
                        break;
                      }
                      else {
                        if (logger.isDebugEnabled()) {
                          logger.debug("{} != {}", fqdn, host);
                        }
                      }
                    }
                    // find in peer table for peer already connected to server but not removed
                    if (peer == null) {
                      logger.debug("Peer with FQDN [{}] was not found in predefined peer table. Checking at (previously) connected peers table", host);
                      peer = (IPeer) peerTable.get(host);
                      if (peer != null) {
                        logger.debug("Got peer for FQDN [{}]. Is connection open ? {}.", host, peer.hasValidConnection());
                      }
                      else {
                        logger.debug("Still haven't found peer for FQDN [{}]", host);
                      }
                    }

                    if (peer != null) {
                      //FIXME: define procedure when 'peer.getRealm() != realm'
                      logger.debug("Add [{}] connection to peer [{}]", connection, peer);
                      peer.addIncomingConnection(connection);
                      try {
                        logger.debug("Handle [{}] message on peer [{}]", message, peer);
                        peer.handleMessage(message.isRequest() ? EventTypes.CER_EVENT : EventTypes.CER_EVENT, message, connKey);
                      }
                      catch (Exception e) {
                        logger.debug("Unable to process CER message", e);
                      }
                    }
                    else {
                      if (isAcceptUndefinedPeer || foundInPredefinedTable) {
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
                          logger.debug("Created new peer instance [{}] and adding to peer table", peer);
                          peer.setRealm(realm);

                          Collection<Realm> realms = router.getRealmTable().getRealms(realm);
                          for (Realm r : realms) {
                            if (r.getName().equals(realm)) {
                              logger.debug("Found the realm [{}] for the new peer [{}], adding it to it", realm, peer);
                              ((IRealm) r).addPeerName(host);
                            }
                          }

                          appendPeerToPeerTable(peer);
                          logger.debug("Handle [{}] message on peer [{}]", message, peer);
                          peer.handleMessage(message.isRequest() ? EventTypes.CER_EVENT : EventTypes.CER_EVENT, message, connKey);
                        }
                        catch (Exception e) {
                          logger.warn("Unable to create peer", e);
                          unregister(true);
                        }
                      }
                      else {
                        logger.info("Skip anonymous connection [{}]", connection);
                        unregister(true);
                      }
                    }
                  }
                  else {
                    logger.debug("Unknown message [{}] by connection [{}]", message, connKey);
                    unregister(true);
                  }
                }

                @Override
                public void internalError(String connKey, IMessage message, TransportException cause) {
                  logger.debug("Connection [{}] internalError [{}]", connKey, cause);
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
                      logger.debug("Unable to release connection [{}]", connection);
                    }
                  }
                }
              };
              //PCB added logging
              String connKey = connection.getKey();
              getIncConnections().put(connection.getKey(), connection);
              logger.debug("Inserted connection [{}] into IncConnections", connKey);

              connection.addConnectionListener(listener);
              logger.debug("Added listener [{}] to connection [{}]", listener, connKey);
            }
          }
        }
        );
  }

  private void appendPeerToPeerTable(IPeer peer) {
    logger.debug("Adding Peer[{}] to PeerTable with size {}", peer, peerTable.size());

    // Cleaning up if we are at max capacity...
    if (peerTable.size() == MAX_PEER_TABLE_SIZE) {
      for (String k : peerTable.keySet()) {
        Peer p = peerTable.get(k);
        if (p != null && p.getState(PeerState.class) == PeerState.DOWN) {
          peerTable.remove(k, p);
        }
      }
    }

    peerTable.put(peer.getUri().getFQDN(), peer);
    if (peerTableListener != null) {
      peerTableListener.peerAccepted(peer);
    }
  }

  @Override
  public void stopping(int disconnectCause) {
    super.stopping(disconnectCause);
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
    //remove incoming data
    storageAnswers.clear();

    // Clear dynamic peers from peertable
    Iterator<String> it = super.peerTable.keySet().iterator();
    while (it.hasNext()) {
      String fqdn = it.next();
      if (this.predefinedPeerTable.contains(fqdn)) {
        continue;
      }
      else {
        it.remove();
      }
    }

  }

  @Override
  public Peer addPeer(URI peerURI, String realm, boolean connecting) {
    //TODO: add sKey here, now it adds peer to all realms.
    //TODO: better, separate addPeer from realm!
    try {
      Configuration peerConfig = null;
      Configuration[] peers = config.getChildren(PeerTable.ordinal());
      // find peer config
      for (Configuration c : peers) {
        if (peerURI.getFQDN().equals(c.getStringValue(PeerName.ordinal(), ""))) {
          peerConfig = c;
          break;
        }
      }
      if (peerConfig == null) {
        peerConfig = new EmptyConfiguration(false).add(PeerAttemptConnection, connecting);
      }
      IPeer peer = (IPeer) createPeer(0, peerURI.toString(), null, null, metaData, config, peerConfig, fsmFactory,
          transportFactory, statisticFactory, concurrentFactory, parser);
      if (peer == null) {
        return null;
      }
      peer.setRealm(realm);
      appendPeerToPeerTable(peer);
      boolean found = false;

      Collection<Realm> realms =  this.router.getRealmTable().getRealms(realm);
      for (Realm r : realms) {
        if (r.getName().equals(realm)) {
          ((IRealm) r).addPeerName(peerURI.toString());
          found = true;
          break;
        }
      }
      if (!found) {
        throw new IllegalArgumentException("Incorrect realm name");
      }
      if (StackState.STARTED.equals(stack.getState()) && connecting) {
        peer.connect();
      }
      return peer;
    }
    catch (Exception e) {
      logger.debug("Unable to add peer", e);
      return null;
    }
  }

  public Set<Realm> getAllRealms() {
    return new HashSet<Realm>(router.getRealmTable().getRealms());
  }

  @Override
  public Peer removePeer(String host) {
    try {
      String fqdn = null;
      for (String f : peerTable.keySet()) {
        if (f.equals(host)) {
          fqdn = f;
          peerTable.get(fqdn).disconnect(DisconnectCause.BUSY);
        }
      }
      if (fqdn != null) {
        predefinedPeerTable.remove(fqdn);
        Peer removedPeer = peerTable.remove(fqdn);
        if (peerTableListener != null) {
          peerTableListener.peerRemoved(removedPeer);
        }

        return removedPeer;
      }
      else {
        return null;
      }
    }
    catch (Exception e) {
      logger.debug("Unable to remove peer", e);
      return null;
    }
  }

  @Override
  public Statistic getStatistic(String name) {
    for (Peer p : peerTable.values()) {
      if (p.getUri().getFQDN().equals(name)) {
        return ((IPeer) p).getStatistic();
      }
    }
    return null;
  }

  @Override
  public IMessage isDuplicate(IMessage request) {
    String key = request.getDuplicationKey();
    if (key != null && storageAnswers != null) {
      StorageEntry entry = storageAnswers.get(key);
      return entry != null ? (IMessage) entry.getMessage().clone() : null;
    }
    return null;
  }

  @Override
  public void saveToDuplicate(String key, IMessage answer) {
    if (storageAnswers != null && storageAnswers.size() < duplicateSize) {
      if (key != null) {
        StorageEntry se = new StorageEntry((IMessage) answer.clone());
        if (logger.isTraceEnabled()) {
          logger.trace("Duplicate Protection - Inserting Entry with key '{}' and time '{}'", key, se.getTime());
        }
        storageAnswers.put(key, se);
      }
    }
  }

  @Override
  public ISessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    boolean isWrapp = super.isWrapperFor(aClass);

    return aClass == MutablePeerTable.class || aClass == Network.class || isWrapp;
  }

  @Override
  public <T> T unwrap(Class<T> aClass) throws InternalException {
    if (aClass == MutablePeerTable.class) {
      return assembler.getComponentInstance(aClass);
    }
    if (aClass == Network.class) {
      return assembler.getComponentInstance(aClass);
    }
    return null;
  }
}
