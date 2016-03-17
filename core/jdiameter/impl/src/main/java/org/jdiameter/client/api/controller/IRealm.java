/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.jdiameter.client.api.controller;

import org.jdiameter.api.Realm;
import org.jdiameter.server.api.agent.IAgent;
import org.jdiameter.server.api.agent.IAgentConfiguration;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IRealm extends Realm {

  /**
   * Return list of real peers
   * 
   * @return array of realm peers
   */
  public String[] getPeerNames();

  /**
   * Append new host (peer) to this realm
   * 
   * @param host
   *            name of peer host
   */
  public void addPeerName(String name);

  /**
   * Remove peer from this realm
   * 
   * @param host
   *            name of peer host
   */
  public void removePeerName(String name);

  /**
   * Checks if a peer name belongs to this realm
   * 
   * @param name name of peer host
   * @return true if the the peer belongs to this realm, false otherwise
   */
  public boolean hasPeerName(String name);

  /**
   * Get the processing agent for this realm
   * 
   * @return the agent for this realm, if any
   */
  public IAgent getAgent();

  /**
   * Get agent configuration values for this realm.
   * @return
   */
  public IAgentConfiguration getAgentConfiguration();

}
