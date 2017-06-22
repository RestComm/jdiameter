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
 */

package org.jdiameter.common.api.data;

import org.jdiameter.api.SessionPersistenceStorage;
import org.jdiameter.client.api.controller.IPeer;

import java.util.List;

/**
 * Extends basic session storage with capabilities of CRUD operations
 * for session persistence records which bind sessions with peers that
 * are processing those sessions.
 */
public interface IRoutingAwareSessionDatasource extends ISessionDatasource, SessionPersistenceStorage {

  /**
   * Gets a name of the peer that is currently assigned to a given session.
   *
   * @param sessionId session identifier used as mapping key in session storage
   * @return peer name
   */
  String getSessionPeer(String sessionId);

  /**
   * Binds a particular session with a given peer.
   *
   * @param sessionId session identifier used as mapping key in session storage
   * @param peer      object to bind
   */
  void setSessionPeer(String sessionId, IPeer peer);

  /**
   * Unbinds a particular session from a given peer.
   *
   * @param sessionId session identifier used as mapping key in session storage
   * @return peer name that has just been unbound
   */
  String removeSessionPeer(String sessionId);

  /**
   * @param sessionId session identifier used as mapping key in session storage
   */
  void clearUnanswerablePeers(String sessionId);

  /**
   * @param sessionId session identifier used as mapping key in session storage
   * @return list of peers that did not answer for request within Tx timer value period
   */
  List<String> getUnanswerablePeers(String sessionId);
}
