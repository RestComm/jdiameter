package org.mobicents.diameter.stack.management;

import static org.jdiameter.client.impl.helpers.Parameters.PeerIp;
import static org.jdiameter.client.impl.helpers.Parameters.PeerLocalPortRange;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.server.impl.helpers.XMLConfiguration;

public class NetworkPeerImpl implements NetworkPeer {

  private static final long serialVersionUID = 1L;

  // Mandatory
  private String name;
  private Boolean attemptConnect;
  private Integer rating;

  // Optional
  private String ip;
  private Integer portRangeLow;
  private Integer portRangeHigh;
  private String securityRef;

  // Helpers
  private final String DEFAULT_STRING = "default_string";

  private HashMap<String, DiameterStatistic> statistics;

  public NetworkPeerImpl(String name, Boolean attemptConnect, Integer rating) {
    this.name = name;
    this.attemptConnect = attemptConnect;
    this.rating = rating;
  }

  public NetworkPeerImpl(String name, Boolean attemptConnect, Integer rating, String ip, Integer portRangeLow, Integer portRangeHigh, String securityRef) {
    this(name, attemptConnect, rating);
    this.ip = ip;
    this.portRangeLow = portRangeLow;
    this.portRangeHigh = portRangeHigh;
    this.securityRef = securityRef;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if(!this.name.equals(name)) {
      EmptyConfiguration config = getPeerConfiguration(name);
      if (config != null) {
        config.add(PeerName, name);
        this.name = name;
      }
    }
  }

  public Boolean getAttemptConnect() {
    return attemptConnect;
  }

  public void setAttemptConnect(Boolean attemptConnect) {
    this.attemptConnect = attemptConnect;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    EmptyConfiguration config = getPeerConfiguration(name);
    if (config != null) {
      config.add(PeerRating, rating);
      this.rating = rating;
    }
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    // TODO: Verify IP Address
    EmptyConfiguration config = getPeerConfiguration(name);
    if (config != null) {
      config.add(PeerIp, ip);
      this.ip = ip;
    }
  }

  public Integer getPortRangeLow() {
    return portRangeLow;
  }

  public Integer getPortRangeHigh() {
    return portRangeHigh;
  }

  public void setPortRange(Integer portRangeLow, Integer portRangeHigh) {
    EmptyConfiguration config = getPeerConfiguration(name);
    if (config != null) {
      config.add(PeerLocalPortRange, portRangeLow + "-" + portRangeHigh);
      this.portRangeLow = portRangeLow;
      this.portRangeHigh = portRangeHigh;
    }
  }

  public String getSecurityRef() {
    return securityRef;
  }

  public void setSecurityRef(String securityRef) {
    this.securityRef = securityRef;
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
  
  private EmptyConfiguration getPeerConfiguration(String name) {
    XMLConfiguration configuration = (XMLConfiguration) DiameterConfiguration.stack.getMetaData().getConfiguration();
    Configuration[] peerTable = configuration.getChildren(PeerTable.ordinal());
    
    for(Configuration curPeer : peerTable) {
      if(curPeer.getStringValue(PeerName.ordinal(), DEFAULT_STRING).equals(name)) {
        return (EmptyConfiguration) curPeer;
      }
    }
    
    return null;
  }
  
  public HashMap<String, DiameterStatistic> getStatistics() {
    return statistics;
  }
  
  public void setStatistics(HashMap<String, DiameterStatistic> statistics) {
    this.statistics = statistics;
  }
}
