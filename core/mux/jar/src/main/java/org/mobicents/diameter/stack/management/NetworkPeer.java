package org.mobicents.diameter.stack.management;

import java.io.Serializable;
import java.util.HashMap;

public interface NetworkPeer extends Serializable {

  public String getName();

  public void setName(String name);

  public Boolean getAttemptConnect();

  public void setAttemptConnect(Boolean attemptConnect);

  public Integer getRating();

  public void setRating(Integer rating);

  public String getIp();

  public void setIp(String ip);

  public Integer getPortRangeLow();

  public Integer getPortRangeHigh();

  public void setPortRange(Integer portRangeLow, Integer portRangeHigh);

  public String getSecurityRef();

  public void setSecurityRef(String securityRef);

  public HashMap<String, DiameterStatistic> getStatistics();

  public void setStatistics(HashMap<String, DiameterStatistic> statistics);
}
