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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

/**
 * Peer contains everything that is worth knowing about a peer and
 * define some operation for working with this peer.
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @version 1.5.1 Final
 */
public interface Peer {

  /**
   *  Establishes a connection towards a remote peer.
   * @throws IllegalDiameterStateException
   */
  void connect() throws InternalException, IOException, IllegalDiameterStateException;

  /**
   * Close the connection to the peer.
   * @throws IllegalDiameterStateException
   */
  void disconnect(int disconnectCause) throws InternalException, IllegalDiameterStateException;

  /**
   * All implementations must support PeerState interface as argument
   * @return state of peer
   */
  <E> E getState(Class<E> enumc);

  /**
   * @return URI of peer
   */
  URI getUri();

  /**
   * @return array of peer ip addresses
   */
  InetAddress[] getIPAddresses();

  /**
   * @return Realm name of peer
   */
  String getRealmName();

  /**
   * @return vendor id of peer stack implementation
   */
  long getVendorId();

  /**
   * @return product name of peer stack implementation
   */
  String getProductName();

  /**
   * @return firmware version  of peer stack implementation
   */
  long getFirmware();

  /**
   * @return set of common Application-id of peer;
   */
  Set<ApplicationId> getCommonApplications();

  /**
   * Append peer state change listener to the peer manager

   * @param listener listener instance
   */
  void addPeerStateListener(PeerStateListener listener);

  /**
   * Remove peer state change listener from the peer manager
   * @param listener listener instance
   */
  void removePeerStateListener(PeerStateListener listener);
}
