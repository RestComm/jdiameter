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

package org.jdiameter.client.impl.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.LocalAction;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.server.api.agent.IAgent;
import org.jdiameter.server.api.agent.IAgentConfiguration;

/**
 * The Realm class implements rows in the Diameter Realm routing table.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RealmImpl implements IRealm {

  protected String name;
  protected ApplicationId appId;
  protected LocalAction action;
  protected boolean dynamic;
  protected long expirationTime;
  protected Collection<String> hosts = new ConcurrentLinkedQueue<String>();
  protected IAgent agent;
  protected IAgentConfiguration agentConfiguration;
  public RealmImpl(String name, ApplicationId applicationId, LocalAction localAction,
      IAgent agent, IAgentConfiguration agentConfiguration, boolean dynamic, long expirationTime, String... hosts) {
    this.hosts.addAll(Arrays.asList(hosts));
    this.name = name;
    this.appId = applicationId;
    this.action = localAction;
    this.dynamic = dynamic;
    this.expirationTime = expirationTime;
    this.agent = agent;
    this.agentConfiguration = agentConfiguration;
  }

  /**
   * Return name of this realm
   *
   * @return name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Return applicationId associated with this realm
   *
   * @return applicationId
   */
  @Override
  public ApplicationId getApplicationId() {
    return appId;
  }

  /**
   * Return realm local action for this realm
   *
   * @return realm local action
   */
  @Override
  public LocalAction getLocalAction() {
    return action;
  }

  /**
   * Return list of real peers
   *
   * @return array of realm peers
   */
  @Override
  public String[] getPeerNames() {
    return hosts.toArray(new String[hosts.size()]);
  }

  /**
   * Append new host (peer) to this realm
   *
   * @param host
   *          name of peer host
   */
  @Override
  public void addPeerName(String name) {
    if (!hasPeerName(name)) {
      hosts.add(name);
    }
  }

  /**
   * Remove peer from this realm
   *
   * @param host
   *          name of peer host
   */
  @Override
  public void removePeerName(String s) {
    hosts.remove(name);
  }

  /**
   * Return true if this realm is dynamic updated
   *
   * @return true if this realm is dynamic updated
   */
  @Override
  public boolean isDynamic() {
    return dynamic;
  }

  /**
   * Return expiration time for this realm in milisec
   *
   * @return expiration time
   */
  @Override
  public long getExpirationTime() {
    return expirationTime;
  }

  @Override
  public boolean hasPeerName(String name) {
    return this.hosts.contains(name);
  }

  @Override
  public IAgent getAgent() {
    return agent;
  }

  @Override
  public IAgentConfiguration getAgentConfiguration() {
    return this.agentConfiguration;
  }

  @Override
  public boolean isLocal() {
    return false;
  }

  @Override
  public String toString() {
    return "RealmImpl [name=" + name + ", appId=" + appId + ", action=" + action + ", dynamic=" + dynamic +
        ", expirationTime=" + expirationTime + ", hosts=" + hosts + ", agent=" + agent + "]";
  }

}
