package org.mobicents.diameter.stack.management;

import java.io.Serializable;
import java.util.Collection;

public interface Realm extends Serializable {

  public Collection<ApplicationIdJMX> getApplicationIds();

  public void addApplicationId(ApplicationIdJMX applicationId);

  public void removeApplicationId(ApplicationIdJMX applicationId);

  public String getName();

  public void setName(String name);

  public Collection<String> getPeers();

  public void setPeers(Collection<String> peers);

  public void addPeer(String peer);

  public void removePeer(String peer);

  public String getLocalAction();

  public void setLocalAction(String localAction);

  public Boolean getDynamic();

  public void setDynamic(Boolean dynamic);

  public Long getExpTime();

  public void setExpTime(Long expTime);

}
