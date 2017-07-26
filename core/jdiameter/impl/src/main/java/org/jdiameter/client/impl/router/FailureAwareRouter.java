/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.client.impl.router;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.NoMorePeersAvailableException;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.RouteException;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.IRoutingAwareSessionDatasource;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extends capabilities of basic router implementation {@link RouterImpl} with extra features
 * related to failure detection and failure aware routing. Rating of a particular peer is
 * taken into consideration when deciding about an order of peers usage in case of failure
 * detection. The highest rating peers are used first, then lower priorities peers next, etc.
 * If several peers are marked with the same rating, load balancing algorithm is executed among
 * them. In case of all higher priority peers failure, lower priority peers are considered.
 * Afterwards, in case any higher priority peer becomes available again, only new sessions requests
 * should be targeted again to higher priority peers, i.e. currently handled session stays
 * assigned to a peer selected beforehand.
 */
public class FailureAwareRouter extends WeightedRoundRobinRouter {

  private static final Logger logger = LoggerFactory.getLogger(FailureAwareRouter.class);

  private IRoutingAwareSessionDatasource sessionDatasource = null;

  private int lastSelectedRating = -1;

  /**
   * Parameterized constructor. Should be called by any subclasses.
   */
  public FailureAwareRouter(IContainer container, IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config, MetaData aMetaData) {
    super(container, concurrentFactory, realmTable, config, aMetaData);

    ISessionDatasource sds = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    if (sds instanceof IRoutingAwareSessionDatasource) {
      this.sessionDatasource = (IRoutingAwareSessionDatasource) sds;
    }

    logger.debug("Constructor for FailureAwareRouter (session persistent routing {})", isSessionPersistentRoutingEnabled() ? "enabled" : "disabled");
  }

  /**
   * Narrows down the subset of peers selected by {@link RouterImpl} superclass to those with
   * the highest rating only.
   */
  protected List<IPeer> getAvailablePeers(String destRealm, String[] peers, IPeerTable manager, IMessage message) {
    List<IPeer> selectedPeers = getPeers(destRealm, peers, manager, PeerState.OKAY);

    if (logger.isDebugEnabled()) {
      logger.debug("All available peers: {}", selectedPeers);
    }
    selectedPeers = narrowToAnswerablePeers(selectedPeers, message.getSessionId());
    if (logger.isDebugEnabled()) {
      logger.debug("All answerable peers: {}", selectedPeers);
    }

    if (message.isRetransmissionSupervised()) {
      message.setNumberOfRetransAllowed(selectedPeers.size() - 1);
    }

    int maxRating = findMaximumRating(selectedPeers);
    if (maxRating >= 0 && maxRating != lastSelectedRating) {
      lastSelectedRating = maxRating;
      resetRoundRobinContext();
    }

    selectedPeers = narrowToSelectablePeersSubset(selectedPeers, maxRating, message);
    if (logger.isDebugEnabled()) {
      logger.debug("Final subset of selectable peers (max rating [{}]): {}", maxRating, selectedPeers);
    }

    return selectedPeers;
  }

  private List<IPeer> narrowToAnswerablePeers(List<IPeer> availablePeers, String sessionId) {
    List<String> unAnswerablePeers = sessionDatasource.getUnanswerablePeers(sessionId);

    if (unAnswerablePeers != null) {
      for (String peerFqdn : unAnswerablePeers) {
        for (IPeer peer : availablePeers) {
          if (peer.getUri().getFQDN().equals(peerFqdn)) {
            availablePeers.remove(peer);
            break;
          }
        }
      }
    }

    return availablePeers;
  }

  @Override
  public IPeer getPeer(IMessage message, IPeerTable manager) throws RouteException, AvpDataException {
    logger.debug("Getting a peer for message [{}]", message);
    //FIXME: add ability to send without matching realm+peer pair?, that is , route based on peer table entries?
    //that is, if msg.destHost != null > getPeer(msg.destHost).sendMessage(msg);
    String destRealm = null;
    String destHost = null;
    IRealm matchedRealm = null;
    String[] info = null;
    // Get destination information
    if (message.isRequest()) {
      Avp avpRealm = message.getAvps().getAvp(Avp.DESTINATION_REALM);
      if (avpRealm == null) {
        throw new RouteException("Destination realm avp is empty");
      }
      destRealm = avpRealm.getDiameterIdentity();

      Avp avpHost = message.getAvps().getAvp(Avp.DESTINATION_HOST);
      if (avpHost != null) {
        destHost = avpHost.getDiameterIdentity();
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Looking up peer for request: [{}], DestHost=[{}], DestRealm=[{}]", new Object[] {message, destHost, destRealm});
      }

      matchedRealm = (IRealm) this.realmTable.matchRealm(message);
    }
    else {
      //answer, search
      info = getRequestRouteInfo(message);
      if (info != null) {
        destHost = info[0];
        destRealm = info[1];
        logger.debug("Message is an answer. Host is [{}] and Realm is [{}] as per hopbyhop info from request", destHost, destRealm);
        if (destRealm == null) {
          logger.warn("Destination-Realm was null for hopbyhop id " + message.getHopByHopIdentifier());
        }
      }
      else {
        logger.debug("No Host and realm found based on hopbyhop id of the answer associated request");
      }
      //FIXME: if no info, should not send it ?
      //FIXME: add strict deff in route back table so stack does not have to lookup?
      if (logger.isDebugEnabled()) {
        logger.debug("Looking up peer for answer: [{}], DestHost=[{}], DestRealm=[{}]", new Object[] {message, destHost, destRealm});
      }
      matchedRealm = (IRealm) this.realmTable.matchRealm(message, destRealm);
    }

    //  IPeer peer = getPeerPredProcessing(message, destRealm, destHost);
    //
    //  if (peer != null) {
    //    logger.debug("Found during preprocessing...[{}]", peer);
    //    return peer;
    //  }

    // Check realm name
    //TODO: check only if it exists?
    if (matchedRealm == null) {
      throw new RouteException("Unknown realm name [" + destRealm + "]");
    }

    // THIS IS GET PEER, NOT ROUTE!!!!!!!
    // Redirect processing
    //redirectProcessing(message, destRealm, destHost);
    // Check previous context information, this takes care of most answers.
    if (message.getPeer() != null && destHost != null && destHost.equals(message.getPeer().getUri().getFQDN()) && message.getPeer().hasValidConnection()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Select previous message usage peer [{}]", message.getPeer());
      }
      return message.getPeer();
    }

    // Balancing procedure

    IPeer c = destHost != null ? manager.getPeer(destHost) : null;

    if (c != null && c.hasValidConnection()) {
      logger.debug("Found a peer using destination host avp [{}] peer is [{}] with a valid connection.", destHost, c);
      //here matchedRealm MAY
      return c;
    }
    else {
      logger.debug("Finding peer by destination host avp [host={}] did not find anything. Now going to try finding one by destination realm [{}]",
          destHost, destRealm);
      String[] peers = matchedRealm.getPeerNames();
      if (peers == null || peers.length == 0) {
        throw new RouteException("Unable to find context by route information [" + destRealm + " ," + destHost + "]");
      }

      List<IPeer> availablePeers = getAvailablePeers(destRealm, peers, manager, message);

      if (logger.isDebugEnabled()) {
        logger.debug("Performing Realm routing. Realm [{}] has the following peers available [{}] from list [{}]",
            new Object[] {destRealm, availablePeers, Arrays.asList(peers)});
      }

      // Balancing
      IPeer peer = selectPeer(availablePeers, message);
      if (peer == null) {
        throw new NoMorePeersAvailableException(
            "Unable to find a valid connection within realm [" + destRealm + "]");
      }
      else {
        if (logger.isDebugEnabled()) {
          logger.debug("Load balancing selected peer with uri [{}]", peer.getUri());
        }
      }

      return peer;
    }
  }

  private List<IPeer> getPeers(String destRealm, String[] peers, IPeerTable manager, PeerState state) {
    List<IPeer> availablePeers = new ArrayList<IPeer>(5);
    logger.debug("Looping through peers in realm [{}]", destRealm);
    for (String peerName : peers) {
      IPeer localPeer = manager.getPeer(peerName);
      if (logger.isDebugEnabled()) {
        logger.debug("Checking peer [{}] for name [{}]", new Object[]{localPeer, peerName});
      }

      if (localPeer != null && localPeer.getState(PeerState.class) == state) {
        if (localPeer.hasValidConnection()) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Found available peer to add to available peer list with uri [{}] with a valid connection",
                localPeer.getUri().toString());
          }
          availablePeers.add(localPeer);
        }
        else {
          if (logger.isDebugEnabled()) {
            logger.debug("Found a peer with uri [{}] with no valid connection", localPeer.getUri());
          }
        }
      }
    }

    return availablePeers;
  }

  /**
   * Applies load balancing algorithm and session persistence thereafter if its enabled.
   */
  public IPeer selectPeer(List<IPeer> availablePeers, IMessage message) {
    IPeer peer = null;
    if (logger.isDebugEnabled()) {
      logger.debug(super.dumpRoundRobinContext());
    }
    peer = super.selectPeer(availablePeers);

    if (peer == null) {
      return null;
    }
    if (isSessionPersistentRoutingEnabled()) {
      String sessionAssignedPeer = sessionDatasource.getSessionPeer(message.getSessionId());
      if (sessionAssignedPeer != null && !peer.getUri().getFQDN().equals(sessionAssignedPeer)) {
        if (logger.isDebugEnabled()) {
          logger.debug("Peer reselection took place from [{}] to [{}] on session [{}]", new Object[]{sessionAssignedPeer, peer.getUri().getFQDN(), message
              .getSessionId()});
        }
        sessionDatasource.setSessionPeer(message.getSessionId(), peer);
      }
      else if (sessionAssignedPeer == null) {
        sessionDatasource.setSessionPeer(message.getSessionId(), peer);
        if (logger.isDebugEnabled()) {
          logger.debug("Peer [{}] selected and assigned to session [{}]", new Object[]{peer.getUri().getFQDN(), message.getSessionId()});
        }
      }

    }

    return peer;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.impl.router.RouterImpl#isSessionAware()
   */
  @Override
  public boolean isSessionAware() {
    return isSessionPersistentRoutingEnabled();
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.impl.router.RouterImpl#getLastSelectedRating()
   */
  @Override
  protected int getLastSelectedRating() {
    return this.lastSelectedRating;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.impl.router.RouterImpl#isWrapperFor(java.lang.Class)
   */
  @Override
  public boolean isWrapperFor(Class<?> aClass) {
    if (aClass == IRoutingAwareSessionDatasource.class) {
      return isSessionAware();
    }
    else {
      return super.isWrapperFor(aClass);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.impl.router.RouterImpl#unwrap(java.lang.Class)
   */
  @Override
  public <T> T unwrap(Class<T> aClass) {
    if (aClass == IRoutingAwareSessionDatasource.class) {
      return aClass.cast(sessionDatasource);
    }
    else {
      return super.unwrap(aClass);
    }
  }

  /***********************************************************************
   * *********************** Local helper methods *************************
   ***********************************************************************/

  private List<IPeer> narrowToSelectablePeersSubset(List<IPeer> peers, int rating, IMessage message) {
    List<IPeer> peersSubset = new ArrayList<IPeer>(5);
    String sessionAssignedPeer = sessionDatasource.getSessionPeer(message.getSessionId());
    for (IPeer peer : peers) {
      if (isSessionPersistentRoutingEnabled() && sessionAssignedPeer != null) {
        if (peer.getUri().getFQDN().equals(sessionAssignedPeer)) {
          if (logger.isDebugEnabled()) {
            logger.debug("Sticky sessions are enabled and peer [{}] is assigned to the current session [{}]", new Object[]{sessionAssignedPeer, message
                .getSessionId()});
          }
          peersSubset.clear();
          peersSubset.add(peer);
          break;
        }
      }
      if (peer.getRating() == rating) {
        peersSubset.add(peer);
      }
    }

    if (logger.isDebugEnabled() && sessionAssignedPeer == null) {
      logger.debug("Sticky sessions are enabled and no peer has been yet assigned to the current session [{}]", message.getSessionId());
    }

    return peersSubset;
  }

  private int findMaximumRating(List<IPeer> peers) {
    int maxRating = -1;
    for (IPeer peer : peers) {
      maxRating = Math.max(maxRating, peer.getRating());
    }
    return maxRating;
  }

  private boolean isSessionPersistentRoutingEnabled() {
    return this.sessionDatasource != null;
  }
}
