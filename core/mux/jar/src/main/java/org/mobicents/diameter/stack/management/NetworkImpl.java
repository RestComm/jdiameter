package org.mobicents.diameter.stack.management;

import java.util.HashMap;
import java.util.Map;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.PeerTable;
import org.jdiameter.server.impl.MutablePeerTableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkImpl implements Network {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(NetworkImpl.class);

  private Map<String, NetworkPeer> peers = new HashMap<String, NetworkPeer>();
  private Map<String, Realm> realms = new HashMap<String, Realm>();

  public NetworkImpl() {
  }

  public Map<String, NetworkPeer> getPeers() {
    return peers;
  }

  public NetworkPeer getPeer(String name) {
    return peers.get(name);
  }

  public void addPeer(NetworkPeer peer) {
    this.peers.put(peer.getName(), peer);
  }

  public void addPeerRuntime(NetworkPeer peer, String realm) {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) DiameterConfiguration.stack.unwrap(org.jdiameter.api.Network.class);
      n.addPeer(peer.getName(), realm, peer.getAttemptConnect());
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }
  
  public void removePeer(String name) {
    try {
      MutablePeerTableImpl mpt = (MutablePeerTableImpl) DiameterConfiguration.stack.unwrap(PeerTable.class);
      mpt.removePeer(name);
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }

  public Map<String, Realm> getRealms() {
    return realms;
  }

  public Realm getRealm(String name) {
    return realms.get(name);
  }

  public void addRealm(Realm realm) {
    realms.put(realm.getName(), realm);
  }

  public void addRealmRuntime(Realm realm) {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) DiameterConfiguration.stack.unwrap(org.jdiameter.api.Network.class);
      for(ApplicationIdJMX appId : realm.getApplicationIds()) {
        /*org.jdiameter.api.Realm r =*/ n.addRealm(realm.getName(), appId.asApplicationId(), LocalAction.valueOf(realm.getLocalAction()), realm.getDynamic(), realm.getExpTime());
      }
    }
    catch (InternalException e) {
      logger.error("Failed to unwrap class.", e);
    }
  }

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
    for(NetworkPeer peer : peers.values()) {
      buf.append(peer.toString());
    }
    buf.append("  ## REALMS ##\r\n");
      for(Realm realm : realms.values()) {
        buf.append(realm.toString());
    }

    return buf.toString();
  }
}
