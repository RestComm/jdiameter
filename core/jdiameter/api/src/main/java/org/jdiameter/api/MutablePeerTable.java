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

package org.jdiameter.api;

/**
 * This interface extends PeerTable interface and
 * append some operation for controls peer and realm table
 *
 * @author erick.svenson@yahoo.com
 * @version 1.5.1 Final
 */
public interface MutablePeerTable extends PeerTable {

  /**
   * Return peer statistics
   * @param peerHost host of peer
   * @return peer statistics
   */
  Statistic getStatistic(String peerHost);

  /**
   * Append peer table listener
   * @param listener listener instance
   */
  void setPeerTableListener(PeerTableListener listener);

  /**
   * Add new peer to peer table
   * @param peer URI of peer (host, port and other connection information)
   * for example: aaa://host.example.com:6666;transport=tcp;protocol=diameter
   * @param realmName name of realm
   * @param connecting attempt connect
   * @return peer instance
   */
  Peer addPeer(URI peer, String realmName, boolean connecting);

  /**
   * Remove peer from peer table
   * @param peerHost host of peer
   * @return removed peer instance
   */
  Peer removePeer(String peerHost);
}
