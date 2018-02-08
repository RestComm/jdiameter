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
import org.jdiameter.api.PeerState;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

import org.jdiameter.server.api.IRouter;

import java.util.List;

/**
 * Weighted round-robin router implementation
 *
 * @see <a href="http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling">http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling</a>
 * @author <a href="mailto:n.sowen@2scale.net">Nils Sowen</a>
 */
public class WeightedRoundRobinRouter extends RouterImpl implements IRouter{

  private int lastSelectedPeer = -1;
  private int currentWeight = 0;

  protected WeightedRoundRobinRouter(IRealmTable table, Configuration config) {
    super(null, null, table, config, null);
  }

  public WeightedRoundRobinRouter(IContainer container, IConcurrentFactory concurrentFactory,
                                  IRealmTable realmTable, Configuration config, MetaData aMetaData) {
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
   * <pre>
   * {@code
   *   while (true) {
   *   i = (i + 1) mod n;
   *   if (i == 0) {
   *     cw = cw - gcd(S);
   *     if (cw <= 0) {
   *       cw = max(S);
   *       if (cw == 0)
   *       return NULL;
   *     }
   *   }
   *   if (W(Si) >= cw)
   *     return Si;
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
   *
   * Please note: if the list of availablePeers changes between calls (e.g. if a peer becomes active or inactive),
   * the balancing algorithm is disturbed and might be distributed uneven.
   * This is likely to happen if peers are flapping.
   *
   * @param availablePeers list of peers that are in {@link PeerState#OKAY OKAY} state
   * @return the selected peer according to algorithm
   * @see <a href="http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling">http://kb.linuxvirtualserver.org/wiki/Weighted_Round-Robin_Scheduling</a>
   */
  @Override
  public IPeer selectPeer(List<IPeer> availablePeers) {

    int peerSize = availablePeers != null ? availablePeers.size() : 0;

    // Return none if empty, or first if only one member found
    if (peerSize <= 0) {
      return null;
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
      for ( ;; ) {
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
          return availablePeers.get(lastSelectedPeer);
        }
      }
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
}
