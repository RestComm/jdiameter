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
 * The session delivery objects are responsible for delivering all incoming Message to a specific session.
 * It determines the Diameter Session object that the message belongs to by querying the message's session id AVP.
 * The delivery object searches the local session database for a matching session. If no matching session is found,
 * the delivery object will lookup a matching session factory object that has an application id matching the
 * application id of the message. If there is a registered session factory, then the delivery object will ask the
 * factory to create a new session and delivery the message to the newly created session. If non of these lookup's
 * are successful, the session delivery object will silently discard the message.
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * Serializable interface allows use this class in SLEE Event objects
 *
 * @version 1.5.1 Final
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface BaseSession {

  /**
   * Returns the time when this session was created (milliseconds)
   * Start point of time January 1, 1970 GMT.
   * @return long specifying when this session was created
   */
  long getCreationTime();

  /**
   * Returns the last time an event occurred on this session (milliseconds)
   * Start point of time January 1, 1970 GMT.
   * @return long specifying when last time an event occurred on this session
   */
  long getLastAccessedTime();

  /**
   * Return true if session is not released
   * @return true if session is not released
   */
  boolean isValid();

  /**
   * Release all resources append to session
   */
  void release();

  /**
   * Indicates if this is an App Session or a raw/base session
   * @return
   */
  boolean isAppSession();

  /**
   * Indicates if the session is replicable
   *
   * @return
   */
  boolean isReplicable();

  /**
   * @return session-id as String (Session-Id AVP)
   */
  String getSessionId();

  String IDLE_SESSION_TIMER_NAME = "IDLE_SESSION_TIMER";
}
