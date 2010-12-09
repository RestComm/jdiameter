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
package org.jdiameter.client.impl.controller;

import static org.jdiameter.client.impl.helpers.Parameters.PeerIp;
import static org.jdiameter.client.impl.helpers.Parameters.PeerLocalPortRange;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.StopTimeOut;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Peer;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.URI;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.DictionarySingleton;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PeerTableImpl implements IPeerTable {

  private static final Logger logger = LoggerFactory.getLogger(PeerTableImpl.class);

  // Peer table
  protected ConcurrentHashMap<URI, Peer> peerTable = new ConcurrentHashMap<URI, Peer>();
  protected boolean isStarted;
  protected long stopTimeOut;
  protected IAssembler assembler;
  protected IRouter router;
  protected MetaData metaData;
  protected IConcurrentFactory concurrentFactory;
  // XXX: FT/HA // protected ConcurrentHashMap<String, NetworkReqListener> sessionReqListeners = new ConcurrentHashMap<String, NetworkReqListener>();
  protected ISessionDatasource sessionDatasource;
  
  protected final Dictionary dictionary = DictionarySingleton.getDictionary();

  protected PeerTableImpl() {
  }

  public PeerTableImpl(Configuration globalConfig, MetaData metaData, IContainer stack,IRouter router, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IStatisticFactory statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser) {
    init(stack,router, globalConfig, metaData, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
  }

  protected void init( IContainer stack,IRouter router, Configuration globalConfig, MetaData metaData, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IStatisticFactory statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser) {
    this.router = router;
    this.metaData = metaData;
    this.concurrentFactory = concurrentFactory;
    this.stopTimeOut = globalConfig.getLongValue(StopTimeOut.ordinal(), (Long) StopTimeOut.defValue());
    this.sessionDatasource = stack.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);

    Configuration[] peers = globalConfig.getChildren(Parameters.PeerTable.ordinal());
    if (peers != null && peers.length > 0) {
      for (Configuration peerConfig : peers) {
        if (peerConfig.isAttributeExist(PeerName.ordinal())) {
          String uri = peerConfig.getStringValue(PeerName.ordinal(), null);
          int rating = peerConfig.getIntValue(PeerRating.ordinal(), 0);
          String ip = peerConfig.getStringValue(PeerIp.ordinal(), null);
          String portRange = peerConfig.getStringValue(PeerLocalPortRange.ordinal(), null);
          try {
            // create predefined peer
            IPeer peer = (IPeer) createPeer(rating, uri, ip, portRange, metaData, globalConfig, peerConfig, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
            if (peer != null) {
              peer.setRealm(router.getRealmForPeer(peer.getUri().getFQDN()));
              peerTable.put(peer.getUri(), peer);
              logger.debug("Append peer {} to peer table", peer);
            }
          }
          catch (Exception e) {
            logger.warn("Can not create peer {} due to {}", uri, e);
          }
        }
      }
    }
  }

  protected Peer createPeer(int rating, String uri, String ip, String portRange, MetaData metaData, Configuration config, Configuration peerConfig, 
      IFsmFactory fsmFactory, ITransportLayerFactory transportFactory, IStatisticFactory statisticFactory, IConcurrentFactory concurrentFactory, IMessageParser parser)  
  throws InternalException, TransportException, URISyntaxException, UnknownServiceException {
    return new PeerImpl(this, rating, new URI(uri), ip, portRange, metaData.unwrap(IMetaData.class), config,
        peerConfig, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser, this.sessionDatasource);
  }

  public List<Peer> getPeerTable() {
    List<Peer> p = new ArrayList<Peer>();
    p.addAll(peerTable.values());
    return p;
  }

  public Peer getPeer(String name) {
    return getPeerByName(name);
  }

  public void sendMessage(IMessage message)
  throws IllegalDiameterStateException, RouteException, AvpDataException, IOException {
    if (!isStarted)
      throw new IllegalDiameterStateException("Stack is down");

    // Get context
    IPeer peer;
    if (message.isRequest()) {
      logger.debug("Send request {} [destHost={}; destRealm={}]", new Object[] {message, 
          message.getAvps().getAvp(Avp.DESTINATION_HOST) != null ? message.getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString() : "",
              message.getAvps().getAvp(Avp.DESTINATION_REALM) != null ? message.getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString() : ""}
      );

      // Check local request
      peer = router.getPeer(message, this);
      logger.debug( "Selected peer {} for sending message {}", new Object[] {peer, message});
      if (peer == metaData.getLocalPeer()) {
        logger.debug("Request {} will be processed by local service", message);
      }
      else {
        message.setHopByHopIdentifier(peer.getHopByHopIdentifier());
        peer.addMessage(message);
        message.setPeer(peer);
      }
    }
    else {
      peer = message.getPeer();
      if (peer == null) {
        peer = router.getPeer(message, this);
        if (peer == null) {
          throw new RouteException( "Cannot found remote context for sending message" );
        }
        message.setPeer(peer);
      }
    }

    try {
      if (!peer.sendMessage(message)) {
        throw new IOException("Can not send message");
      }
      else {
        if (message.isRequest()) {
          peer.getStatistic().getRecordByName(IStatistic.Counters.AppGenRequest.name()).inc();
        }
        else {
          peer.getStatistic().getRecordByName(IStatistic.Counters.AppGenResponse.name()).inc();
        }
      }
    }
    catch (Exception e) {
      logger.error("Can not send message", e);
      if (message.isRequest()) {
        peer.getStatistic().getRecordByName(IStatistic.Counters.AppGenRejectedRequest.name()).inc();
      }
      else {
        peer.getStatistic().getRecordByName(IStatistic.Counters.AppGenRejectedResponse.name()).inc();
      }

      if(e instanceof AvpNotAllowedException) {
        throw (AvpNotAllowedException) e;
      }
      else {
       throw new IOException(e.getMessage());
      }
    }
  }

  public void addSessionReqListener(String sessionId, NetworkReqListener listener) {
    // XXX: FT/HA // sessionReqListeners.put(sessionId, listener);
    sessionDatasource.setSessionListener(sessionId, listener);
  }

  public Map<String, NetworkReqListener> getSessionReqListeners() {
    // XXX: FT/HA // return sessionReqListeners;
    return null;
  }

  public IPeer getPeerByName(String peerName) {
    for (Peer p : peerTable.values()) {
      if (p.getUri().toString().equals(peerName) || p.getUri().getFQDN().equals(peerName)) {
        return (IPeer) p;
      }
    }
    return null;
  }

  public IPeer getPeerByUri(String peerUri) {
    URI otherUri;
    try {
      otherUri = new URI(peerUri);
    }
    catch (Exception e) {
      return null;
    }
    for (Peer p : peerTable.values()) {
      if (p.getUri().getFQDN().equals(otherUri.getFQDN())) {
        return (IPeer) p;
      }
    }
    return null;
  }

  public void removeSessionListener(String sessionId) {
    // XXX: FT/HA // sessionReqListeners.remove(sessionId);
    sessionDatasource.removeSessionListener(sessionId);
  }

  public void setAssembler(IAssembler assembler) {
    this.assembler = assembler;
  }

  // Life cycle
  public void start() throws IllegalDiameterStateException, IOException {
    for(Peer peer : peerTable.values()) {
      try {
        peer.connect();
      }
      catch (Exception e) {
        logger.debug("Can not start connect procedure to peer {}", peer, e);
      }
    }
    router.start();
    isStarted = true;
  }

  public void stopped() {
    // XXX: FT/HA // if (sessionReqListeners != null) {
    // XXX: FT/HA // sessionReqListeners.clear();
    // XXX: FT/HA // }
    for (Peer p : peerTable.values()) {
      for (IMessage m : ((IPeer) p).remAllMessage()) {
        try {
          m.runTimer();
        }
        catch(Exception e) {
          logger.debug("Can not stop timer", e);
        }
      }
    }
    if (concurrentFactory  != null) {
      try {
        concurrentFactory.getThreadGroup().interrupt();
        // Wait for some threads which may take longer...
        // FIXME: Change this once we get rid of ThreadGroup and hard interrupting threads.
        long remWaitTime = 2500;
        while(concurrentFactory.getThreadGroup().activeCount() > 0 && remWaitTime > 0) {
            long waitTime = 250;
            Thread.sleep(waitTime);
            remWaitTime -= waitTime;
        }
      }
      catch (Exception e) {
        logger.warn("Can not stop executor");
      }
    }
    router.stop();
  }

  public void stopping() {
    isStarted = false;
    for (Peer peer : peerTable.values()) {
      try {
        peer.disconnect();
      }
      catch (Exception e) {
        logger.warn("Can not stopping peer table", e);
      }
    }
  }

  public void destroy() {
    if (concurrentFactory != null) {
      try {
        concurrentFactory.getThreadGroup().destroy();
      }
      catch (IllegalThreadStateException itse) {
        logger.debug("Failure trying to destroy ThreadGroup probably due to existing active threads. Use stop() before destroy(). (nr_threads={})", concurrentFactory.getThreadGroup().activeCount());
      }
    }
    if (router != null) {
      router.destroy();
    }
    router    = null;
    peerTable = null;
    assembler = null;
  }

  // Extension interface
  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null; 
  }

  protected class PeerTableThreadFactory implements ThreadFactory {

    public final AtomicLong sequence = new AtomicLong(0);
    private int priority = Thread.NORM_PRIORITY;
    private ThreadGroup factoryThreadGroup = new ThreadGroup("JDiameterThreadGroup[" + sequence.incrementAndGet() + "]");

    public PeerTableThreadFactory(int priority) {
      super();
      this.priority = priority;
    }

    public Thread newThread(Runnable r) {
      Thread t = new Thread(this.factoryThreadGroup, r);
      t.setPriority(this.priority);
      // TODO ? t.start(); 
      return t;
    }
  }
}
