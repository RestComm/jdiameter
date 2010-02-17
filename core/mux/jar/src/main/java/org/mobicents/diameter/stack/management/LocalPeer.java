package org.mobicents.diameter.stack.management;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public interface LocalPeer extends Serializable {

  public String getUri();

  public Collection<String> getIpAddresses();

  public String getRealm();

  public Long getVendorId();

  public String getProductName();

  public Long getFirmwareRev();

  public Collection<ApplicationIdJMX> getDefaultApplications();

  public void setUri(String uri);

  public void addIpAddress(String ipAddress);

  public void removeIpAddress(String ipAddress);

  public void setRealm(String realm);

  public void setVendorId(Long vendorId);

  public void setProductName(String productName);

  public void setFirmwareRev(Long firmwareRev);

  public void addDefaultApplication(ApplicationIdJMX defaultApplication);

  public void removeDefaultApplication(ApplicationIdJMX defaultApplication);

  public HashMap<String, DiameterStatistic> getStatistics();
  
  public void setStatistics(HashMap<String, DiameterStatistic> statistics);
}
