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

import org.jdiameter.api.app.AppSession;

/**
 * This class created session instance (Factory)
 *
 * @author erick.svenson@yahoo.com
 * @version 1.5.1 Final
 */
public interface SessionFactory {

  /**
   * Generates new session id which can be used as argument in
   * {@link #getNewSession(String)} or
   * {@link #getNewAppSession(String, ApplicationId, Class)}.
   *
   * @return new id for session.
   * @since 1.5.4.1-build416
   */
  String getSessionId();

  /**
   * See {@link #getSessionId()}. This method has similar semantics, it adds
   * custom part to id.
   *
   * @param customPart
   * @return
   * @since 1.5.4.1-build416
   */
  String getSessionId(String customPart);

  /**
   * Create new raw session instance
   *
   * @return session instance of session
   * @throws InternalException if a error occurs
   */
  RawSession getNewRawSession() throws InternalException;

  /**
   * Create new session with new session id
   *
   * @return session instance of session
   * @throws InternalException if a error occurs
   */
  Session getNewSession() throws InternalException;


  /**
   * Create new session with predefined sessionId
   * You can create special sessions to work on distributed systems
   *
   * @param sessionId  instance of session
   * @return session instance of session
   * @throws InternalException if a error occurs
   */
  Session getNewSession(String sessionId) throws InternalException;

  /**
   * Create new vendor specific application session
   * Use this method for create specific application sessions
   * Example: ClientShSession session = factory.getNewSession(appId, ClientShSession.class)
   *
   * @param applicationId predefined application id
   * @param userSession   A Class defining an interface that the result must implement.
   * @return session instance
   * @throws InternalException if a error occurs
   */

  <T extends AppSession> T getNewAppSession(ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException;

  /**
   * Create new vendor specific application session with predefined sessionId, origination host/realm names.
   * You can create special sessions to work on distributed systems
   * Use this method for create specific application sessions
   * Example: ClientShSession session = factory.getNewSession(appId, ClientShSession.class)
   *
   * @param sessionId instance of session
   * @param applicationId predefined application id
   * @param userSession A Class defining an interface that the result must implement.
   * @return session instance
   * @throws InternalException if a error occurs
   */

  <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException;
}
