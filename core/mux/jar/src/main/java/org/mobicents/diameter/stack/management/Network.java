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
