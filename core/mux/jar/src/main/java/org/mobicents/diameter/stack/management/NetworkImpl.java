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

import java.util.HashMap;
import java.util.Map;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.PeerTable;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.server.api.agent.IAgentConfiguration;
import org.jdiameter.server.impl.MutablePeerTableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class NetworkImpl implements Network {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(NetworkImpl.class);

  private Map<String, NetworkPeer> peers = new HashMap<String, NetworkPeer>();
  private Map<String, Realm> realms = new HashMap<String, Realm>();

  public NetworkImpl() {
  }

  @Override
  public Map<String, NetworkPeer> getPeers() {
    return peers;
  }

  @Override
  public NetworkPeer getPeer(String name) {
    return peers.get(name);
  }

  @Override
  public void addPeer(NetworkPeer peer) {
    this.peers.put(peer.getName(), peer);
  }

  @Override
  public void addPeerRuntime(NetworkPeer peer, String realm) {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) DiameterConfiguration.stack.unwrap(org.jdiameter.api.Network.class);
      n.addPeer(peer.getName(), realm, peer.getAttemptConnect());
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }

  @Override
  public void removePeer(String name) {
    try {
      MutablePeerTableImpl mpt = (MutablePeerTableImpl) DiameterConfiguration.stack.unwrap(PeerTable.class);
      mpt.removePeer(name);
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }

  @Override
  public Map<String, Realm> getRealms() {
    return realms;
  }

  @Override
  public Realm getRealm(String name) {
    return realms.get(name);
  }

  @Override
  public void addRealm(Realm realm) {
    realms.put(realm.getName(), realm);
  }

  @Override
  public void addRealmRuntime(Realm realm) {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) DiameterConfiguration.stack.unwrap(org.jdiameter.api.Network.class);
      for (ApplicationIdJMX appId : realm.getApplicationIds()) {
        IAgentConfiguration agentConfiguration = null;
        if (realm instanceof IRealm) {
          agentConfiguration = ((IRealm) realm).getAgentConfiguration();
        }
        //TODO: XXX
        /*org.jdiameter.api.Realm r =*/ n.addRealm(realm.getName(), appId.asApplicationId(), LocalAction.valueOf(realm.getLocalAction()), agentConfiguration,
                                            realm.getDynamic(), realm.getExpTime());
      }
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }

  @Override
  public void removeRealm(String name) {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) DiameterConfiguration.stack.unwrap(org.jdiameter.api.Network.class);
      /*org.jdiameter.api.Realm r =*/ n.remRealm(name);
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("  ## PEERS ##\r\n");
    for (NetworkPeer peer : peers.values()) {
      buf.append(peer.toString());
    }
    buf.append("  ## REALMS ##\r\n");
    for (Realm realm : realms.values()) {
      buf.append(realm.toString());
    }

    return buf.toString();
  }
}
