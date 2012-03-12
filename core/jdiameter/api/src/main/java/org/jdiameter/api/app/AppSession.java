/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.api.app;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.BaseSession;
import org.jdiameter.api.Session;

import java.util.List;

/**
 * Basic class for application specific session (Sx, Rx, Gx)
 * 
 * @version 1.5.1 Final
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface AppSession extends BaseSession {

  /**
   * Return true if session has stateless FSM
   * 
   * @return true if session has stateless FSM
   */
  boolean isStateless();

  /**
   * Return current value of applicationId of application session.
   * 
   * @return applicationId of application session.
   */
  ApplicationId getSessionAppId();

  /**
   * Returns a list of child sessions
   * 
   * @return List of child delivery sessions
   */
  List<Session> getSessions();

}
