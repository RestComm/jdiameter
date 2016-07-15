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

package org.jdiameter.client.api.controller;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.common.api.statistic.IStatistic;

/**
 * This interface provide additional methods for Peer interface
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IPeer extends Peer {

  /**
   * Return rating of peer
   *
   * @return int value
   */
  int getRating();

  /**
   * Return new hop by hop id for new message
   *
   * @return new hop by hop id
   */
  long getHopByHopIdentifier();

  /**
   * Append request to peer request storage map
   *
   * @param message request instance
   */
  void addMessage(IMessage message);

  /**
   * Remove request from request storage map
   *
   * @param message request instance
   */
  void remMessage(IMessage message);

  /**
   * Clear request storage map
   */
  IMessage[] remAllMessage();

  /**
   * Put message to peer fsm
   *
   * @param message request instance
   * @return true if message will be set to FSM
   * @throws TransportException
   * @throws OverloadException
   */
  boolean handleMessage(EventTypes type, IMessage message, String key) throws TransportException, OverloadException, InternalException;

  /**
   * Send message to diameter network
   *
   * @param message request instance
   * @return true if message will be set to FSM
   * @throws TransportException
   * @throws OverloadException
   */
  boolean sendMessage(IMessage message) throws TransportException, OverloadException, InternalException;

  /**
   * Return true if peer has valid connection
   *
   * @return true if peer has valid connection
   */
  boolean hasValidConnection();

  /**
   * Attach peer to realm
   *
   * @param realm realm name
   */
  void setRealm(String realm);

  /**
   * Add state change listener
   *
   * @param listener listener instance
   */
  void addStateChangeListener(StateChangeListener listener);

  /**
   * Remove state change listener
   *
   * @param listener listener instance
   */
  void remStateChangeListener(StateChangeListener listener);

  /**
   * Add connection state change listener
   *
   * @param listener listener instance
   */
  void addConnectionListener(IConnectionListener listener);

  /**
   * Remove connection state change listener
   *
   * @param listener listener instance
   */
  void remConnectionListener(IConnectionListener listener);

  /**
   * Return peer statistic
   *
   * @return peer statistic
   */
  IStatistic getStatistic();

  /**
   * Return if peer is connected
   *
   * @return is peer connected
   */
  boolean isConnected();
}
