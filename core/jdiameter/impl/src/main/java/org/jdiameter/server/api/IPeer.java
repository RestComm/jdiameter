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

package org.jdiameter.server.api;

import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.io.IConnection;

/**
 * This interface describe extends methods of base class
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IPeer extends org.jdiameter.client.api.controller.IPeer {

  /**
   * Return true if peer must start reconnect procedure
   * 
   * @return true if peer must start reconnect procedure
   */
  boolean isAttemptConnection();

  /**
   * Return action context
   * 
   * @return action context
   */
  IContext getContext();

  /**
   * Return peer connection
   * 
   * @return peer connection
   */
  IConnection getConnection();

  /**
   * Add new network connection (wait CER/CEA)
   * 
   * @param conn new network connection
   */
  void addIncomingConnection(IConnection conn);

  /**
   * Set result of election
   * 
   * @param isElection result of election
   */
  void setElection(boolean isElection);

  /**
   * Set overload manager
   * 
   * @param ovrManager overload manager
   */
  void notifyOvrManager(IOverloadManager ovrManager);
}
