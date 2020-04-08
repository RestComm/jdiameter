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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.PeerState;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Weighted round-robin router implementation
 *
 * @author <a href="mailto:n.sowen@2scale.net">Nils Sowen</a>
 * @see
 *      <a href=
 *      "http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling">http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling</a>
 */
public class WeightedRoundRobinResubmittingRouter extends RouterImpl implements IRouter {

  private static final Logger logger = LoggerFactory.getLogger(WeightedRoundRobinResubmittingRouter.class);

  private static final int ATTEMPTED_PEER_RETENTION_PERIOD_MS = 30000;

  private int lastSelectedPeer = -1;
  private int currentWeight = 0;
  private Map<MessageKey, Set<IPeer>> attemptedPeers = new ConcurrentHashMap<MessageKey, Set<IPeer>>();

  protected WeightedRoundRobinResubmittingRouter(IRealmTable table, Configuration config) {
    super(null, null, table, config, null);
  }

  public WeightedRoundRobinResubmittingRouter(IContainer container, IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config,
      MetaData aMetaData) {
    super(container, concurrentFactory, realmTable, config, aMetaData);
  }

  /**
   * Select peer by weighted round-robin scheduling
   * As documented in http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling
   *
   * <p>
   * The weighted round-robin scheduling is designed to better handle servers
   * with different processing capacities. Each server can be assigned a weight,
   * an integer value that indicates the processing capacity. Servers with higher
   * weights receive new connections first than those with less weights, and servers
   * with higher weights get more connections than those with less weights and servers
   * with equal weights get equal connections. The pseudo code of weighted round-robin
   * scheduling is as follows:
   * <p>
   * Supposing that there is a server set S = {S0, S1, …, Sn-1};
   * W(Si) indicates the weight of Si;
   * i indicates the server selected last time, and i is initialized with -1;
   * cw is the current weight in scheduling, and cw is initialized with zero;
   * max(S) is the maximum weight of all the servers in S;
   * gcd(S) is the greatest common divisor of all server weights in S;
   * <p>
   *
   * <pre>
   * {@code
   *   while (true) {
   *     i = (i + 1) mod n;
   *     if (i == 0) {
   *       cw = cw - gcd(S);
   *       if (cw <= 0) {
   *         cw = max(S);
   *         if (cw == 0)
   *           return NULL;
   *       }
   *     }
   *     if (W(Si) >= cw)
   *       return Si;
   *   }
   * }
   * </pre>
   * <p>
   * For example, the real servers, A, B and C, have the weights, 4, 3, 2 respectively,
   * a scheduling sequence will be AABABCABC in a scheduling period (mod sum(Wi)).
   * <p>
   * In an optimized implementation of the weighted round-robin scheduling, a scheduling sequence
   * will be generated according to the server weights after the rules of IPVS are modified.
   * The network connections are directed to the different real servers based on the scheduling
   * sequence in a round-robin manner.
   * <p>
   * The weighted round-robin scheduling is better than the round-robin scheduling, when the
   * processing capacity of real servers are different. However, it may lead to dynamic load
   * imbalance among the real servers if the load of the requests vary highly. In short, there
   * is the possibility that a majority of requests requiring large responses may be directed
   * to the same real server.
   * <p>
   * Actually, the round-robin scheduling is a special instance of the weighted round-robin
   * scheduling, in which all the weights are equal.
   * <p>
   * This method is internally synchronized due to concurrent modifications to lastSelectedPeer and currentWeight.
   * Please consider this when relying on heavy throughput.
   * <p>
   * Please note: if the list of availablePeers changes between calls (e.g. if a peer becomes active or inactive),
   * the balancing algorithm is disturbed and might be distributed uneven.
   * This is likely to happen if peers are flapping.
   *
   * @param availablePeers
   *          list of peers that are in {@link PeerState#OKAY OKAY} state
   * @return the selected peer according to algorithm
   * @see
   *      <a href=
   *      "http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling">http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling</a>
   */
  @Override
  public IPeer selectPeer(List<IPeer> availablePeers) {
    return selectPeer(null, availablePeers);
  }

  /**
   * Select peer by weighted round-robin scheduling
   *
   * This method ensures that, when the <code>message</code> is passed, that
   * the same peer that responded with the Busy or Unable To Deliver Answer is not selected for
   * any subsequent submissions of the same request.
   *
   * @param message
   *          The message to be re-attempted due to a Busy or Unable To Deliver Answer
   * @param availablePeers
   *          list of peers that are in {@link PeerState#OKAY OKAY} state
   * @return the selected peer according to algorithm, ensuring that if the <code>message</code> is passed, that
   *         the same peer that responded with the Busy or Unable To Deliver Answer is not selected a second time
   *
   */
  @Override
  public IPeer selectPeer(IMessage message, List<IPeer> availablePeers) {
    IPeer selectedPeer = null;
    int peerSize = availablePeers != null ? availablePeers.size() : 0;

    // Return none if empty, or first if only one member found
    if (peerSize <= 0) {
      return null;
    }

    if (message != null && message.getPeer() != null) {
      addAttemptedPeer(message, message.getPeer());

      long numberOfAttempts = 0;
      MessageKey messageKey = getMessageKey(message);
      Set<IPeer> peerSet = attemptedPeers.get(messageKey);
      if (peerSet != null) {
        numberOfAttempts = peerSet.size();
      }
      if (logger.isTraceEnabled()) {
        logger.trace("Selecting subsequent peer for {} [numberOfAttempts={}]", messageKey, numberOfAttempts);
      }

      if (numberOfAttempts == peerSize) {
        if (logger.isDebugEnabled()) {
          logger.debug("All peers exhausted for {}, giving up...", messageKey);
        }
        removeAttemptedPeers(messageKey);
        return null;
      }
    }

    if (peerSize == 1) {
      return availablePeers.iterator().next();
    }

    // Find maximum weight and greatest common divisor of weight across all peers
    int maxWeight = 0;
    Integer gcd = null;
    for (IPeer peer : availablePeers) {
      maxWeight = Math.max(maxWeight, peer.getRating());
      gcd = (gcd == null) ? peer.getRating() : gcd(gcd, peer.getRating());
    }

    // Find best matching candidate. Synchronized here due to consistent changes on member variables
    synchronized (this) {
      for (;;) {
        lastSelectedPeer = (lastSelectedPeer + 1) % peerSize;
        if (lastSelectedPeer == 0) {
          currentWeight = currentWeight - gcd;
          if (currentWeight <= 0) {
            currentWeight = maxWeight;
          }
        }
        if (peerSize <= lastSelectedPeer) {
          lastSelectedPeer = -1; // safety first, restart if peer size has accidentally changed.
          continue;
        }
        IPeer candidate = availablePeers.get(lastSelectedPeer);
        if (candidate.getRating() >= currentWeight) {
          if (message != null && message.getPeer() != null) {
            if (isPeerPreviouslyAttempted(lastSelectedPeer, availablePeers, message)) {
              continue;
            }
          }

          selectedPeer = availablePeers.get(lastSelectedPeer);

          if (logger.isTraceEnabled()) {
            logger.trace("Selected Peer [uri={}, realmName={}, rating={}]", selectedPeer.getUri(), selectedPeer.getRealmName(), selectedPeer.getRating());
          }
          return selectedPeer;
        }
      }
    }
  }

  private MessageKey getMessageKey(final IMessage message) {
    return new MessageKey(message.getSessionId(), message.getEndToEndIdentifier());
  }

  private boolean isPeerPreviouslyAttempted(int selectedPeerIndex, List<IPeer> availablePeers, IMessage message) {
    boolean isPeerPreviouslyAttempted = false;
    final MessageKey messageKey = getMessageKey(message);
    if (logger.isTraceEnabled()) {
      logger.trace("Checking whether selected Peer [id={}] has already had {} sent to it", selectedPeerIndex, messageKey);
    }

    if (attemptedPeers.containsKey(messageKey)) {
      IPeer candidate = availablePeers.get(selectedPeerIndex);
      if (attemptedPeers.get(messageKey).contains(candidate)) {
        if (logger.isTraceEnabled()) {
          logger.trace("Peer [uri={}, realmName={}, rating={}] has been tried before, try next peer", candidate.getUri(), candidate.getRealmName(),
              candidate.getRating());
        }
        isPeerPreviouslyAttempted = true;
      }
    }

    return isPeerPreviouslyAttempted;
  }

  private synchronized void addAttemptedPeer(final IMessage message, IPeer peer) {
    final MessageKey messageKey = getMessageKey(message);
    if (attemptedPeers.containsKey(messageKey)) {
      attemptedPeers.get(messageKey).add(peer);
    }
    else {
      Set<IPeer> peerSet = new HashSet<IPeer>();
      peerSet.add(peer);
      attemptedPeers.put(messageKey, peerSet);

      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          removeAttemptedPeers(messageKey);
        }
      }, ATTEMPTED_PEER_RETENTION_PERIOD_MS);
    }
  }

  private void removeAttemptedPeers(MessageKey messageKey) {
    if (logger.isDebugEnabled()) {
      logger.debug("Removing attemptedPeers for {} (currently [attemptedPeers.size()={}])  ", messageKey, attemptedPeers.size());
    }
    Set<IPeer> peerSet = attemptedPeers.remove(messageKey);
    if (peerSet != null) {
      if (logger.isTraceEnabled()) {
        logger.trace("peerSet with [size={}] has been removed for {}", peerSet.size(), messageKey);
      }
    }
    else {
      if (logger.isWarnEnabled()) {
        logger.warn("No peers removed from attemptedPeers for {}!", messageKey);
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Done removing attemptedPeers for {} (now [attemptedPeers.size()={}])  ", messageKey, attemptedPeers.size());
    }
  }

  /**
   * Return greatest common divisor for two integers
   * https://en.wikipedia.org/wiki/Greatest_common_divisor#Using_Euclid.27s_algorithm
   *
   * @param a
   * @param b
   * @return greatest common divisor
   */
  protected int gcd(int a, int b) {
    return (b == 0) ? a : gcd(b, a % b);
  }

  /**
   * Defines a class which can be used to uniquely define any single message within any given session.
   *
   */
  private class MessageKey {
    private String sessionId;
    private long endToEndId;

    MessageKey(String sessionId, long endToEndId) {
      super();
      this.sessionId = sessionId;
      this.endToEndId = endToEndId;
    }

    @Override
    public String toString() {
      return "MessageKey [sessionId=" + sessionId + ", endToEndId=" + endToEndId + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + (int) (endToEndId ^ (endToEndId >>> 32));
      result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      MessageKey other = (MessageKey) obj;
      if (!getOuterType().equals(other.getOuterType())) {
        return false;
      }
      if (endToEndId != other.endToEndId) {
        return false;
      }
      if (sessionId == null) {
        if (other.sessionId != null) {
          return false;
        }
      }
      else if (!sessionId.equals(other.sessionId)) {
        return false;
      }
      return true;
    }

    private WeightedRoundRobinResubmittingRouter getOuterType() {
      return WeightedRoundRobinResubmittingRouter.this;
    }
  }
}
