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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerTableImpl implements IPeerTable {

  private static final Logger logger = LoggerFactory.getLogger(PeerTableImpl.class);
  
//  /**
//   * determines core pool size, those threads are always there, so if there is no traffic stack wont take much time to act.
//   */
//  private static final int _THREAD_POOL_CORE_SIZE = 1;
//  /**
//   * determines in seconds keep alive time for thread in pool.
//   */
//  private static final int _THREAD_POOL_KEEP_ALIVE_TIME = 60;
//  /**
//   * determines how many thread pool can have.
//   */
//  protected int maximumThreadPoolSize = 5;
//  /**
//   * determines thread priority for executor.
//   */
//  protected int threadPoolPriority = Thread.NORM_PRIORITY;
//  protected ThreadFactory threadFactory;

  // Peer table
  protected ConcurrentHashMap<URI, Peer> peerTable = new ConcurrentHashMap<URI, Peer>();
  protected boolean isStarted;
  protected long stopTimeOut;
  protected IAssembler assembler;
  protected IRouter router;
  protected MetaData metaData;
  protected IConcurrentFactory concurrentFactory;
  protected ConcurrentHashMap<String, NetworkReqListener> sessionReqListeners = new ConcurrentHashMap<String, NetworkReqListener>();

  protected PeerTableImpl() {
  }

  public PeerTableImpl(Configuration globalConfig, MetaData metaData, IRouter router, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IStatisticFactory statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser) {
    init(router, globalConfig, metaData, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
  }

  protected void init(IRouter router, Configuration globalConfig, MetaData metaData, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IStatisticFactory statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser) {
    this.router = router;
    this.metaData = metaData;
    this.concurrentFactory = concurrentFactory;
    this.stopTimeOut = globalConfig.getLongValue(StopTimeOut.ordinal(), (Long) StopTimeOut.defValue());

//    // Mobicents jDiameter Thread Pool Configuration
//    Configuration[] threadPoolConf = globalConfig.getChildren(Parameters.ThreadPool.ordinal());
//    if(threadPoolConf != null && threadPoolConf.length > 0) {
//    	Configuration tpc = threadPoolConf[0];
//    	this.maximumThreadPoolSize = tpc.getIntValue(Parameters.ThreadPoolSize.ordinal(), (Integer)Parameters.ThreadPoolSize.defValue());
//    	this.threadPoolPriority = tpc.getIntValue(Parameters.ThreadPoolPriority.ordinal(), (Integer)Parameters.ThreadPoolPriority.defValue());
//    }
//    
//    this.threadFactory = new PeerTableThreadFactory(this.threadPoolPriority);
    //this.peerTaskExecutor = Executors.newCachedThreadPool();
    
    // XXX: WHAT ABOUT THIS????
    //this.peerTaskExecutor = new ThreadPoolExecutor(_THREAD_POOL_CORE_SIZE, this.maximumThreadPoolSize, _THREAD_POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), this.threadFactory);

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
        peerConfig, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
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
    }catch(JAvpNotAllowedException j)
    {
    	throw j;
    }
    catch (Exception e) {
        logger.error("Can not send message", e);
        if (message.isRequest()) {
          peer.getStatistic().getRecordByName(IStatistic.Counters.AppGenRejectedRequest.name()).inc();
        }
        else {
          peer.getStatistic().getRecordByName(IStatistic.Counters.AppGenRejectedResponse.name()).inc();
        }
      throw new IOException(e.getMessage());
    }
  }

  public void addSessionReqListener(String sessionId, NetworkReqListener listener) {
    sessionReqListeners.put(sessionId, listener);
  }

  public Map<String, NetworkReqListener> getSessionReqListeners() {
    return sessionReqListeners;
  }

  public IPeer getPeerByName(String peerName) {
    for (Peer p : peerTable.values()) {
      if (p.getUri().getFQDN().equals(peerName)) {
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
    sessionReqListeners.remove(sessionId);
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
    if (sessionReqListeners != null) {
      sessionReqListeners.clear();
    }
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
        concurrentFactory.getThreadGroup().destroy();
      } catch (Exception e) {
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
	        // ??
	        //t.start();
	        return t;
	    }
  }
}
