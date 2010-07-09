/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.api.app;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.BaseSession;
import org.jdiameter.api.Session;
//import org.jdiameter.client.api.IContainer;

import java.io.Serializable;
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
public interface AppSession extends Serializable,BaseSession {

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
