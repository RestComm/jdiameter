 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
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
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
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
  protected ConcurrentHashMap<String, Peer> peerTable = new ConcurrentHashMap<String, Peer>();
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

  public PeerTableImpl(Configuration globalConfig, MetaData metaData, IContainer stack, IRouter router, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IStatisticManager statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser) {
    init(stack, router, globalConfig, metaData, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser);
  }

  protected void init( IContainer stack, IRouter router, Configuration globalConfig, MetaData metaData, IFsmFactory fsmFactory,
      ITransportLayerFactory transportFactory, IStatisticManager statisticFactory,
      IConcurrentFactory concurrentFactory, IMessageParser parser) {
    logger.debug("Initializing Peer Table.");
    this.router = router;
    this.metaData = metaData;
    this.concurrentFactory = concurrentFactory;
    this.stopTimeOut = globalConfig.getLongValue(StopTimeOut.ordinal(), (Long) StopTimeOut.defValue());
    this.sessionDatasource = stack.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);

    logger.debug("Populating peerTable from configuration");
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
            IPeer peer = (IPeer) createPeer(rating, uri, ip, portRange, metaData, globalConfig, peerConfig, fsmFactory, transportFactory, statisticFactory,
                concurrentFactory, parser);
            if (peer != null) {
              //NOTE: this depends on conf, in normal case realm is younger part of FQDN, but in some cases
              //conf peers may contain IPs only... sucks.
              peer.setRealm(router.getRealmTable().getRealmForPeer(peer.getUri().getFQDN()));
              peerTable.put(peer.getUri().getFQDN(), peer);
              logger.debug("Appended peer [{}] to peer table", peer);
            }
          }
          catch (Exception e) {
            logger.warn("Unable to create peer [" + uri + "]", e);
          }
        }
      }
    }
  }

  protected Peer createPeer(int rating, String uri, String ip, String portRange, MetaData metaData, Configuration config, Configuration peerConfig,
      IFsmFactory fsmFactory, ITransportLayerFactory transportFactory, IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory,
      IMessageParser parser)
          throws InternalException, TransportException, URISyntaxException, UnknownServiceException {
    return new PeerImpl(this, rating, new URI(uri), ip, portRange, metaData.unwrap(IMetaData.class), config,
        peerConfig, fsmFactory, transportFactory, statisticFactory, concurrentFactory, parser, this.sessionDatasource);
  }

  @Override
  public List<Peer> getPeerTable() {
    return new ArrayList<Peer>(peerTable.values());
  }

  @Override
  public void sendMessage(IMessage message) throws IllegalDiameterStateException, RouteException, AvpDataException, IOException {
    if (!isStarted) {
      throw new IllegalDiameterStateException("Stack is down");
    }

    // Get context
    IPeer peer;
    if (message.isRequest()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Send request {} [destHost={}; destRealm={}]", new Object[] {message,
            message.getAvps().getAvp(Avp.DESTINATION_HOST) != null ? message.getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString() : "",
                message.getAvps().getAvp(Avp.DESTINATION_REALM) != null ? message.getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString() : ""});
      }

      // Check local request
      if (router.updateRoute(message)) {
        if (logger.isDebugEnabled()) {
          logger.debug("Updated route on message {} [destHost={}; destRealm={}]", new Object[] {message,
              message.getAvps().getAvp(Avp.DESTINATION_HOST) != null ? message.getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString() : "",
                  message.getAvps().getAvp(Avp.DESTINATION_REALM) != null ? message.getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString() : ""});
        }
      }
      peer = router.getPeer(message, this);
      logger.debug("Selected peer [{}] for sending message [{}]", peer, message);
      if (peer == metaData.getLocalPeer()) {
        logger.debug("Request [{}] will be processed by local service", message);
      }
      else {
        message.setHopByHopIdentifier(peer.getHopByHopIdentifier());
        peer.addMessage(message);
        message.setPeer(peer);
      }
    }
    else {
      logger.debug("Message is an answer");
      peer = message.getPeer();
      if (peer == null || !peer.hasValidConnection()) {
        logger.debug("Peer is null [{}] or with invalid connection so we will use router.getPeer to find a peer", peer == null);
        peer = router.getPeer(message, this);
        if (peer == null) {
          throw new RouteException( "Cannot found remote context for sending message" );
        }
        logger.debug("Found a peer [{}] and setting it as the peer in the message", peer);
        message.setPeer(peer);
      }
    }

    try {
      logger.debug("Calling sendMessage on peer [{}]", peer);
      if (!peer.sendMessage(message)) {
        throw new IOException("Can not send message");
      }
      else {
        logger.debug("Message was submitted to be sent, now adding statistics");
        if (message.isRequest()) {
          if (peer.getStatistic().isEnabled()) {
            peer.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequest.name()).inc();
          }
        }
        else {
          if (peer.getStatistic().isEnabled()) {
            peer.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenResponse.name()).inc();
          }
        }
      }
    }
    catch (Exception e) {
      logger.error("Can not send message", e);
      if (message.isRequest()) {
        if (peer.getStatistic().isEnabled()) {
          peer.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRejectedRequest.name()).inc();
        }
      }
      else {
        if (peer.getStatistic().isEnabled()) {
          peer.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRejectedResponse.name()).inc();
        }
      }

      if (e instanceof AvpNotAllowedException) {
        throw (AvpNotAllowedException) e;
      }
      else {
        throw new IOException(e.getMessage());
      }
    }
  }

  @Override
  public void addSessionReqListener(String sessionId, NetworkReqListener listener) {
    // XXX: FT/HA // sessionReqListeners.put(sessionId, listener);
    logger.debug("Adding sessionId [{}] to sessionDatasource", sessionId);
    sessionDatasource.setSessionListener(sessionId, listener);
  }

  @Override
  public Map<String, NetworkReqListener> getSessionReqListeners() {
    // XXX: FT/HA // return sessionReqListeners;
    return null;
  }

  @Override
  public IPeer getPeer(String fqdn) {
    logger.debug("In getPeer for peer with FQDN [{}]. Going to find a matching entry in peerTable", fqdn);
    IPeer peer = (IPeer) peerTable.get(fqdn);
    if (peer == null) {
      logger.debug("No peer found in getPeer for peer [{}] will return null", fqdn);
      return null;
    }

    logger.debug("Found matching peer [{}]. Is connection open ? {}.", peer.getUri(), peer.hasValidConnection());
    return peer;
  }

  @Override
  public void removeSessionListener(String sessionId) {
    // XXX: FT/HA // sessionReqListeners.remove(sessionId);
    sessionDatasource.removeSessionListener(sessionId);
  }

  @Override
  public void setAssembler(IAssembler assembler) {
    this.assembler = assembler;
  }

  // Life cycle
  @Override
  public void start() throws IllegalDiameterStateException, IOException {
    logger.debug("Starting PeerTable. Going to call connect on all peers in the peerTable");
    for (Peer peer : peerTable.values()) {
      try {
        peer.connect();
      }
      catch (Exception e) {
        logger.warn("Can not start connect procedure to peer [" + peer + "]", e);
      }
    }
    logger.debug("Calling start on the router");
    router.start();
    isStarted = true;
  }

  @Override
  public void stopped() {
    logger.debug("Calling stopped() on PeerTableImpl");
    // XXX: FT/HA // if (sessionReqListeners != null) {
    // XXX: FT/HA // sessionReqListeners.clear();
    // XXX: FT/HA // }
    for (Peer p : peerTable.values()) {
      for (IMessage m : ((IPeer) p).remAllMessage()) {
        try {
          m.runTimer();
        }
        catch (Exception e) {
          logger.debug("Unable to stop timer on message", e);
        }
      }
    }
    if (concurrentFactory  != null) {
      try {
        // Wait for some threads which may take longer...
        // FIXME: Change this once we get rid of ThreadGroup and hard interrupting threads.
        // boolean interrupted = false;
        long remWaitTime = 2000;
        logger.debug("Stopping thread group and waiting a max of {}ms for all threads to finish", remWaitTime);
        while (concurrentFactory.getThreadGroup().activeCount() > 0 && remWaitTime > 0) {
          long waitTime = 250;
          Thread.sleep(waitTime);
          remWaitTime -= waitTime;
          logger.debug("Waited {}ms. Time remaining to wait: {}ms. {} Thread still active.",
              new Object[]{waitTime, remWaitTime, concurrentFactory.getThreadGroup().activeCount()});
          // it did not terminated, let's interrupt
          // FIXME: remove ASAP, this is very bad, it kills threads in middle of op,
          //        killing FSM of peer for instance, after that its not usable.
          // if (remWaitTime <= 0 && !interrupted) {
          //   interrupted = true;
          //   remWaitTime = 2000;
          //   logger.debug("Stopping thread group did not work. Interrupting and waiting a max of {}ms for all threads to finish", remWaitTime);
          //   concurrentFactory.getThreadGroup().interrupt();
          // }
        }
      }
      catch (Exception e) {
        logger.warn("Unable to stop executor");
      }
    }
    router.stop();
  }

  @Override
  public void stopping(int disconnectCause) {
    logger.debug("In stopping. Going to disconnect all peers in peer table");
    isStarted = false;
    for (Peer peer : peerTable.values()) {
      try {
        peer.disconnect(disconnectCause);
      }
      catch (Exception e) {
        logger.warn("Failure disconnecting peer [" + peer.getUri().toString() + "]", e);
      }
    }
  }

  @Override
  public void destroy() {
    logger.debug("In destroy. Going to destroy concurrentFactory's thread group");
    if (concurrentFactory != null) {
      try {
        // concurrentFactory.getThreadGroup().interrupt();
        // concurrentFactory.getThreadGroup().stop(); //had to add it to make testStartStopStart pass....
        // concurrentFactory.getThreadGroup().destroy();
      }
      catch (IllegalThreadStateException itse) {
        if (logger.isDebugEnabled()) {
          logger.debug("Failure trying to destroy ThreadGroup probably due to existing active threads. Use stop() before destroy(). (nr_threads={})",
              concurrentFactory.getThreadGroup().activeCount());
        }
      }
      catch (ThreadDeath td) {
        // The class ThreadDeath is specifically a subclass of Error rather than Exception, even though it is a
        // "normal occurrence", because many applications catch all occurrences of Exception and then discard the
        // exception.  ....
      }
    }
    if (router != null) {
      logger.debug("Calling destroy on router");
      router.destroy();
    }
    router    = null;
    peerTable = null;
    assembler = null;
  }

  // Extension interface
  @Override
  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  @Override
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

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(this.factoryThreadGroup, r);
      if (logger.isDebugEnabled()) {
        logger.debug("Creating new thread in thread group JDiameterThreadGroup. Thread name is [{}]", t.getName());
      }
      t.setPriority(this.priority);
      // TODO ? t.start();
      return t;
    }
  }
}
