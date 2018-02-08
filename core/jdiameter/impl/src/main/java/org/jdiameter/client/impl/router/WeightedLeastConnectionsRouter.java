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
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import org.jdiameter.server.api.IRouter;

/**
 * Weighted Least-Connections router implementation<br/><br/>
 *
 * This requires that {@link IStatistic Statistics} for the following records are enabled: Peer,AppGenRequestPerSecond,NetGenRequestPerSecond
 * In the client configuration, please use the following settings:
 *
 * <pre>
 *   ...
 *   <Parameters>
 *    <Statistics pause="5000" delay="5000" enabled="true" active_records="Peer,AppGenRequestPerSecond,NetGenRequestPerSecond"/>
 *   </Parameters>
 *   ...
 *   <Extensions>
 *   <RouterEngine value="org.jdiameter.client.impl.router.WeightedLeastConnectionsRouter" />
 *   </Extensions>
 * </pre>
 *
 * @see <a href="http://kb.linuxvirtualserver.org/wiki/Weighted_Least-Connection_Scheduling">http://kb.linuxvirtualserver.org/wiki/Weighted_Least-Connection_Scheduling</a>
 * @author <a href="mailto:n.sowen@2scale.net">Nils Sowen</a>
 */
public class WeightedLeastConnectionsRouter extends RouterImpl implements IRouter{

  private static final Logger logger = LoggerFactory.getLogger(WeightedLeastConnectionsRouter.class);

  protected WeightedLeastConnectionsRouter(IRealmTable table, Configuration config) {
    super(null, null, table, config, null);
  }

  public WeightedLeastConnectionsRouter(IContainer container, IConcurrentFactory concurrentFactory,
                                        IRealmTable realmTable, Configuration config, MetaData aMetaData) {
    super(container, concurrentFactory, realmTable, config, aMetaData);
  }

  /**
   * Return peer with least connections<br/>
   * {@url http://kb.linuxvirtualserver.org/wiki/Weighted_Least-Connection_Scheduling http://kb.linuxvirtualserver.org/wiki/Weighted_Least-Connection_Scheduling}
   *
   * <p>
   * The weighted least-connection scheduling is a superset of the least-connection scheduling,
   * in which you can assign a performance weight to each real server. The servers with a higher
   * weight value will receive a larger percentage of active connections at any one time.
   * The default server weight is one, and the IPVS Administrator or monitoring program can
   * assign any weight to real server. In the weighted least-connections scheduling, new
   * network connection is assigned to a server which has the least ratio of the current
   * active connection number to its weight.
   * <p>
   * Supposing there is a server set S = {S0, S1, ..., Sn-1},
   * W(Si) is the weight of server Si;
   * C(Si) is the current connection number of server Si;
   * CSUM = ΣC(Si) (i=0, 1, .. , n-1) is the sum of current connection numbers;
   * <p>
   * The new connection is assigned to the server j, in which
   * (C(Sm) / CSUM)/ W(Sm) = min { (C(Si) / CSUM) / W(Si)}  (i=0, 1, . , n-1),
   * where W(Si) isn't zero
   * Since the CSUM is a constant in this lookup, there is no need to divide by CSUM,
   * the condition can be optimized as
   * C(Sm) / W(Sm) = min { C(Si) / W(Si)}  (i=0, 1, . , n-1), where W(Si) isn't zero
   * <p>
   * Since division operation eats much more CPU cycles than multiply operation, and Linux
   * does not allow float mode inside the kernel, the condition C(Sm)/W(Sm) > C(Si)/W(Si)
   * can be optimized as C(Sm)*W(Si) > C(Si)*W(Sm). The scheduling should guarantee
   * that a server will not be scheduled when its weight is zero. Therefore, the pseudo
   * code of weighted least-connection scheduling algorithm is
   * <p>
   * <pre>
   * {@code
   *   for (m = 0; m < n; m++) {
   *   if (W(Sm) > 0) {
   *     for (i = m+1; i < n; i++) {
   *     if (C(Sm)*W(Si) > C(Si)*W(Sm))
   *       m = i;
   *     }
   *     return Sm;
   *   }
   *   }
   *   return NULL;
   * }
   * </pre>
   * <p>
   * The weighted least-connection scheduling algorithm requires additional division than
   * the least-connection scheduling. In a hope to minimize the overhead of scheduling when
   * servers have the same processing capacity, both the least-connection scheduling and the
   * weighted least-connection scheduling algorithms are implemented.
   *
   * @see <a href="http://kb.linuxvirtualserver.org/wiki/Weighted_Least-Connection_Scheduling">http://kb.linuxvirtualserver.org/wiki/Weighted_Least-Connection_Scheduling</a>
   * @param availablePeers list of peers that are in {@link PeerState#OKAY OKAY} state
   * @return the selected peer according to algorithm
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

    for (int m = 0; m < peerSize; peerSize++) {
      IPeer peerM = availablePeers.get(m);
      if (peerM.getRating() > 0) {
        for (int i = m + 1; i < peerSize; i++) {
          IPeer peerI = availablePeers.get(i);
          if (getNumConnections(peerM) * peerI.getRating() > getNumConnections(peerI) * peerM.getRating()) {
            m = i;
          }
        }
        return availablePeers.get(m);
      }
    }

    // Return first peer if anything did go wrong
    return availablePeers.iterator().next();
  }

  /**
   * Since num connections is not available, determine throughput by reading statistics
   * and assume the load of the peer
   *
   * @param peer
   * @return throughput indicator
   */
  protected long getNumConnections(IPeer peer) {
    if (peer == null) {
      return 0;
    }
    IStatistic stats = peer.getStatistic();

    // If no statistics are available, return zero
    if (!stats.isEnabled()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Statistics for peer are disabled. Please enable statistics in client config");
      }
      return 0;
    }

    // Requests per second initiated by Local Peer + Request initiated by Remote peer
    String uri = peer.getUri() == null ? "local" : peer.getUri().toString();
    long requests = getRecord(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+uri, stats)
        + getRecord(IStatisticRecord.Counters.NetGenRequestPerSecond.name()+'.'+uri, stats);

    // There are likely more requests than responses active
    long connections = Math.max(0, requests);

    if (logger.isTraceEnabled()) {
      logger.trace("Active connections for {}: {}", peer, connections);
    }

    return connections;
  }

  /**
   * Return statistics record value from given {@link IStatistic}
   *
   * @param record key to retrieve
   * @param stats  statistic object
   * @return
   */
  protected long getRecord(String record, IStatistic stats) {
    if (record == null || stats == null) {
      return 0;
    }
    IStatisticRecord statsRecord = stats.getRecordByName(record);
    if (statsRecord == null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Warning: no record for {}, available: {}", record, Arrays.toString(stats.getRecords()));
      }
      return 0;
    }
    return statsRecord.getValueAsLong();
  }
}
