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
    catch (Exception e) {
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
