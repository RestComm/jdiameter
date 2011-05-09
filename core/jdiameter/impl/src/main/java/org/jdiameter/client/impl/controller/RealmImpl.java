/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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
package org.jdiameter.client.impl.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.LocalAction;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.server.api.agent.IAgent;

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

  public RealmImpl(String name, ApplicationId applicationId, LocalAction localAction, 
      IAgent agent, boolean dynamic, long expirationTime, String... hosts) {
    this.hosts.addAll(Arrays.asList(hosts));
    this.name = name;
    this.appId = applicationId;
    this.action = localAction;
    this.dynamic = dynamic;
    this.expirationTime = expirationTime;
    this.agent = agent;
  }

  /**
   * Return name of this realm
   * 
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Return applicationId associated with this realm
   * 
   * @return applicationId
   */
  public ApplicationId getApplicationId() {
    return appId;
  }

  /**
   * Return realm local action for this realm
   * 
   * @return realm local action
   */
  public LocalAction getLocalAction() {
    return action;
  }

  /**
   * Return list of real peers
   * 
   * @return array of realm peers
   */
  public String[] getPeerNames() {
    return hosts.toArray(new String[hosts.size()]);
  }

  /**
   * Append new host (peer) to this realm
   * 
   * @param host
   *          name of peer host
   */
  public void addPeerName(String name) {
    hosts.add(name);
  }

  /**
   * Remove peer from this realm
   * 
   * @param host
   *          name of peer host
   */
  public void removePeerName(String s) {
    hosts.remove(name);
  }

  /**
   * Return true if this realm is dynamic updated
   * 
   * @return true if this realm is dynamic updated
   */
  public boolean isDynamic() {
    return dynamic;
  }

  /**
   * Return expiration time for this realm in milisec
   * 
   * @return expiration time
   */
  public long getExpirationTime() {
    return expirationTime;
  }

  public boolean hasPeerName(String name) {
    return this.hosts.contains(name);
  }

  public IAgent getAgent() {
    return agent;
  }

  public boolean isLocal() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String toString() {
    return "RealmImpl [name=" + name + ", appId=" + appId + ", action=" + action + ", dynamic=" + dynamic + 
        ", expirationTime=" + expirationTime + ", hosts=" + hosts + ", agent=" + agent + "]";
  }

}
