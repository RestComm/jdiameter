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

import java.io.Serializable;
import java.util.Map;

public interface Network extends Serializable {

  public Map<String, NetworkPeer> getPeers();

  public NetworkPeer getPeer(String name);

  public void addPeer(NetworkPeer peer);

  public void addPeerRuntime(NetworkPeer peer, String realm);

  public void removePeer(String name);

  public Map<String, Realm> getRealms();

  public Realm getRealm(String name);

  public void addRealm(Realm realm);

  public void addRealmRuntime(Realm realm);

  public void removeRealm(String name);
}
