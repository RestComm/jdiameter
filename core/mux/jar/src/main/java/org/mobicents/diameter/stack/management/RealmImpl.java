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

package org.mobicents.diameter.stack.management;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.server.api.agent.IAgentConfiguration;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RealmImpl implements Realm {

  private static final long serialVersionUID = 1L;

  private Collection<ApplicationIdJMX> applicationIds;

  private String name;
  private Collection<String> peers;
  private String localAction;
  private Boolean dynamic;
  private Long expTime;
  // make this a string ?
  private IAgentConfiguration agentConfiguration;

  private String originalName;

  public RealmImpl(Collection<ApplicationIdJMX> applicationIds, String name, Collection<String> peers, String localAction,
      IAgentConfiguration agentConfiguration, Boolean dynamic, Long expTime) {
    this.applicationIds = applicationIds;
    this.name = name;
    this.peers = peers;
    this.localAction = localAction;
    this.dynamic = dynamic;
    this.expTime = expTime;
  }

  @Override
  public Collection<ApplicationIdJMX> getApplicationIds() {
    return applicationIds;
  }

  @Override
  public void addApplicationId(ApplicationIdJMX applicationId) {
    this.applicationIds.add(applicationId);
  }

  @Override
  public void removeApplicationId(ApplicationIdJMX applicationId) {
    this.applicationIds.remove(applicationId);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    if (!this.name.equals(name)) {
      this.originalName = this.name;
      this.name = name;
    }
  }

  @Override
  public Collection<String> getPeers() {
    return peers;
  }

  @Override
  public void setPeers(Collection<String> peers) {
    this.peers = peers;
  }

  @Override
  public void addPeer(String peer) {
    this.peers.add(peer);
  }

  @Override
  public void removePeer(String peer) {
    this.peers.remove(peer);
  }

  @Override
  public String getLocalAction() {
    return localAction;
  }

  @Override
  public void setLocalAction(String localAction) {
    this.localAction = localAction;
  }

  @Override
  public Boolean getDynamic() {
    return dynamic;
  }

  @Override
  public void setDynamic(Boolean dynamic) {
    this.dynamic = dynamic;
  }

  @Override
  public Long getExpTime() {
    return expTime;
  }

  @Override
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
      Field[] fieldlist = cls.getDeclaredFields();
      for (int i = 0; i < fieldlist.length; i++) {
        Field fld = fieldlist[i];
        if (!Modifier.isStatic(fld.getModifiers())) {
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
      for (ApplicationIdJMX appId : this.applicationIds) {
        //TODO: XXX
        org.jdiameter.api.Realm r =
            n.addRealm(this.name, appId.asApplicationId(), LocalAction.valueOf(this.localAction), this.agentConfiguration, this.dynamic, this.expTime);
        for (String host : this.peers) {
          ((IRealm) r).addPeerName(host);
        }
      }
      if (this.originalName != null) {
        n.remRealm(this.originalName);
        this.originalName = null;
      }
    }
    catch (InternalException e) {
      // ignore
    }
  }
}
