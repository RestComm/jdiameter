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

package org.mobicents.diameter.stack.management;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;

public class RealmImpl implements Realm {

  private static final long serialVersionUID = 1L;

  private Collection<ApplicationIdJMX> applicationIds;

  private String name;
  private Collection<String> peers;
  private String localAction;
  private Boolean dynamic;
  private Long expTime;

  private String originalName;
  
  public RealmImpl(Collection<ApplicationIdJMX> applicationIds, String name, Collection<String> peers, String localAction, Boolean dynamic, Long expTime) {
    this.applicationIds = applicationIds;
    this.name = name;
    this.peers = peers;
    this.localAction = localAction;
    this.dynamic = dynamic;
    this.expTime = expTime;
  }

  public Collection<ApplicationIdJMX> getApplicationIds() {
    return applicationIds;
  }

  public void addApplicationId(ApplicationIdJMX applicationId) {
    this.applicationIds.add(applicationId);
  }

  public void removeApplicationId(ApplicationIdJMX applicationId) {
    this.applicationIds.remove(applicationId);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if(!this.name.equals(name)) {
      this.originalName = this.name;
      this.name = name;
    }
  }

  public Collection<String> getPeers() {
    return peers;
  }

  public void setPeers(Collection<String> peers) {
    this.peers = peers;
  }

  public void addPeer(String peer) {
    this.peers.add(peer);
  }

  public void removePeer(String peer) {
    this.peers.remove(peer);
  }

  public String getLocalAction() {
    return localAction;
  }

  public void setLocalAction(String localAction) {
    this.localAction = localAction;
  }

  public Boolean getDynamic() {
    return dynamic;
  }

  public void setDynamic(Boolean dynamic) {
    this.dynamic = dynamic;
  }

  public Long getExpTime() {
    return expTime;
  }

  public void setExpTime(Long expTime) {
    this.expTime = expTime;
  }

  @Override
  public String toString() {
    String dotsString = " .............................................................";
    Class<?> cls;
    StringBuffer toStringBuffer = new StringBuffer();
    try {
      cls = Class.forName(this.getClass().getName());
      Field fieldlist[] = cls.getDeclaredFields();
      for (int i = 0; i < fieldlist.length; i++) {
        Field fld = fieldlist[i];
        if(!Modifier.isStatic(fld.getModifiers())) {
          toStringBuffer.append(fld.getName());
          int dots = 60 - fld.getName().length();
          toStringBuffer.append(dotsString, 0, dots);
          toStringBuffer.append(" ").append(fld.get(this)).append("\r\n");
        }
        //System.out.println("decl class = " + fld.getDeclaringClass());
        //System.out.println("type = " + fld.getType());
        //int mod = fld.getModifiers();
        //System.out.println("modifiers = " + Modifier.toString(mod));
        //System.out.println("-----");
      }
    }
    catch (ClassNotFoundException e) {
      // ignore
    }
    catch (IllegalArgumentException e) {
      // ignore
    }
    catch (IllegalAccessException e) {
      // ignore
    }

    return toStringBuffer.toString();
  }
  
  public void updateRealm() {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) DiameterConfiguration.stack.unwrap(org.jdiameter.api.Network.class);
      for(ApplicationIdJMX appId : this.applicationIds) {
        org.jdiameter.api.Realm r = n.addRealm(this.name, appId.asApplicationId(), LocalAction.valueOf(this.localAction), this.dynamic, this.expTime);
        for(String host : this.peers) {
          r.addPeerName(host);
        }
      }
      if(this.originalName != null) {
        n.remRealm(this.originalName);
        this.originalName = null;
      }
    }
    catch (InternalException e) {
      // ignore
    }
  }
}
