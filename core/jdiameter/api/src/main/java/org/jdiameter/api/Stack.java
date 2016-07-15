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

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jdiameter.api.validation.Dictionary;

/**
 * The interface that every stack class must implement. The Java Diameter framework allows for multiple database stacks.
 * Each stack should supply a class that implements the Stack interface.
 * The StackManager will try to load as many stacks.
 * It is strongly recommended that each Stack class should be small and standalone.
 * When a Stack class is loaded, it should create an instance of itself and register it with the StackManager.
 * This means that a user can load and register a stack by calling
 * Class.forName("org.jdiameter.impl.Stack")
 *
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * <br>
 * Life cycle state machine for stack
 * <P align="center"><img src="../../../../../../images/stack_fsm.PNG" width="347" height="363"><P>
 *
 * Stack must supported following wrapper classes:
 * - Client API : PeerManager
 * - Server API : PeerManager, OverloadManager (Network, PeerManagerWrapper is optional)
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @version 1.5.1 Final
 */
public interface Stack extends Wrapper {

  /**
   * Configuration stack and allocation system resources.
   * @param config Object with configuration parameters
   * @return instance of session factory (DataSource equals)
   * @throws IllegalDiameterStateException if a stack already configured or destroyed
   * @throws InternalException if a stack can not processing initial procedure
   */
  SessionFactory init(Configuration config) throws IllegalDiameterStateException, InternalException;

  /**
   * Start activity of stack (Thread and Network connections), not
   * waiting switch ANY peer to OKEY state
   * @throws IllegalDiameterStateException if a stack is not configured or stopped
   * @throws InternalException if a stack can not processing start procedure
   */
  void start() throws IllegalDiameterStateException, InternalException;

  /**
   * Start activity of stack (Thread and Network connections),
   * waiting specified wait time switch peers to OKEY state.
   * @param  mode specified type of wait procedure
   * @param  timeout how long to wait before giving up, in units of unit
   * @param  unit a TimeUnit determining how to interpret the timeout parameter
   * @throws IllegalDiameterStateException if a stack is not configured or stopped
   * @throws InternalException if a stack can not processing start procedure
   */
  void start(Mode mode, long timeout, TimeUnit unit) throws IllegalDiameterStateException, InternalException;

  /**
   * Stop any activity of stack (Thread and Network connections),
   * waiting if necessary up to the specified wait time switch peers to DOWN state.
   * @param timeout how long to wait before giving up, in units of unit
   * @param unit a TimeUnit determining how to interpret the timeout parameter
   * @param disconnectCause the disconnect-cause to be used in the DPR message(s)
   * @throws IllegalDiameterStateException if a stack is not started
   * @throws InternalException if a stack can not processing start procedure
   */
  void stop(long timeout, TimeUnit unit, int disconnectCause) throws IllegalDiameterStateException, InternalException;

  /**
   * Destroy any resource append to this instance of stack
   */
  void destroy();

  /**
   * @return true is stack is running.
   */
  boolean isActive();

  /**
   * Return logger instance. You can set your logger handler and
   * processing logger alarms in application.
   * @return logger interface
   */
  Logger getLogger();

  /**
   * Return SessionFactory instance
   * @return SessionFactory instance
   * @throws IllegalDiameterStateException if stack is not configured
   */
  SessionFactory getSessionFactory()  throws IllegalDiameterStateException;

  /**
   * Return Dictionary instance
   * @return Dictionary instance
   * @throws IllegalDiameterStateException if stack is not configured
   */
  Dictionary getDictionary()  throws IllegalDiameterStateException;

  /**
   * @return stack meta information
   */
  MetaData getMetaData();

  /**
   * Return an existing session, if present
   *
   * @param sessionId the session identifier
   * @param clazz the class of the session object
   * @return the session object if it exists, null otherwise
   * @throws InternalException if stack is not configured
   */
  <T extends BaseSession> T getSession(String sessionId, Class<T> clazz) throws InternalException;
}
