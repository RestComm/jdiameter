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

import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.IRoutingAwareSessionDatasource;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    if(logger.isDebugEnabled()) {
      logger.debug("Constructor for FailureAwareRouter (session persistent routing {})", isSessionPersistentRoutingEnabled() ? "enabled" : "disabled");
    }
  }

  /**
   * Narrows down the subset of peers selected by {@link RouterImpl} superclass to those with
   * the highest rating only.
   *
   * @see org.jdiameter.client.impl.router.RouterImpl#getAvailablePeers(java.lang.String, java.lang.String[], org.jdiameter.client.api.controller.IPeerTable)
   */
  @Override
  protected List<IPeer> getAvailablePeers(String destRealm, String[] peers, IPeerTable manager) {
    List<IPeer> selectedPeers = super.getAvailablePeers(destRealm, peers, manager);

    int maxRating = findMaximumRating(selectedPeers);
    if (maxRating >= 0 && maxRating != lastSelectedRating) {
      lastSelectedRating = maxRating;
      resetRoundRobinContext();
    }

    selectedPeers = narrowToRatingBasedSubset(selectedPeers, maxRating);
    if(logger.isDebugEnabled()) {
      logger.debug("Final subset of peers with rating [{}]: {}", maxRating, selectedPeers);
    }

    return selectedPeers;
  }

  /**
   * Applies load balancing algorithm and session persistence thereafter if its enabled.
   *
   * @see org.jdiameter.client.impl.router.RouterImpl#selectPeer(java.util.List, org.jdiameter.client.api.IMessage)
   * @see org.jdiameter.common.impl.data.RoutingAwareDataSource
   */
  @Override
  public IPeer selectPeer(List<IPeer> availablePeers, IMessage message) {
    IPeer peer = null;
    String sessionAssignedPeer = null;

    if (isSessionPersistentRoutingEnabled()) {
      sessionAssignedPeer = sessionDatasource.getSessionPeer(message.getSessionId());
      if (sessionAssignedPeer != null) {
        if(logger.isDebugEnabled()) {
          logger.debug("Sticky sessions are enabled and peer [{}] is assigned to the current session [{}]",
                  new Object[]{sessionAssignedPeer, message.getSessionId()});
        }
        peer = findPeer(sessionAssignedPeer, availablePeers);
        if (peer != null) {
          return peer;
        }
        else {
          if(logger.isDebugEnabled()) {
            logger.debug("Peer [{}] assigned to session [{}] so far is not available anymore", sessionAssignedPeer, message.getSessionId());
          }
        }
      } else {
        if(logger.isDebugEnabled()) {
          logger.debug("Sticky sessions are enabled and no peer has been yet assigned to the current session [{}]", message.getSessionId());
        }
      }
    }

    if(logger.isDebugEnabled()) {
      logger.debug(super.dumpRoundRobinContext());
    }
    peer = super.selectPeer(availablePeers);

    if (isSessionPersistentRoutingEnabled()) {
      if (sessionAssignedPeer != null) {
        if(logger.isDebugEnabled()) {
          logger.debug("Peer reselection took place from [{}] to [{}] on session [{}]",
                  new Object[]{sessionAssignedPeer, peer.getUri().getFQDN(), message.getSessionId()});
        }
        sessionDatasource.setSessionPeer(message.getSessionId(), peer);
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

  private List<IPeer> narrowToRatingBasedSubset(List<IPeer> peers, int rating) {
    List<IPeer> peersSubset = new ArrayList<IPeer>(5);
    for (IPeer peer : peers) {
      if (peer.getRating() == rating) {
        peersSubset.add(peer);
      }
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

  private IPeer findPeer(String fqdn, List<IPeer> peers) {
    for (IPeer peer : peers) {
      if (peer.getUri().getFQDN().equals(fqdn)) {
        return peer;
      }
    }
    return null;
  }

  private boolean isSessionPersistentRoutingEnabled() {
    return this.sessionDatasource != null;
  }
}
