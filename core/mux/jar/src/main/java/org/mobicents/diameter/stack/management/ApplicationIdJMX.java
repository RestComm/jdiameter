package org.mobicents.diameter.stack.management;

import java.io.Serializable;

import org.jdiameter.api.ApplicationId;

public class ApplicationIdJMX implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long vendorId = null;
  private Long authApplicationId = null;
  private Long acctApplicationId = null;

  private ApplicationIdJMX(Long vendorId, Long authApplicationId, Long acctApplicationId) {
    this.vendorId = vendorId;
    this.authApplicationId = authApplicationId;
    this.acctApplicationId = acctApplicationId;
  }

  public static ApplicationIdJMX createAcctApplicationId(long vendorId, long applicationId) {
    return new ApplicationIdJMX(vendorId, null, applicationId);
  }

  public static ApplicationIdJMX createAuthApplicationId(long vendorId, long applicationId) {
    return new ApplicationIdJMX(vendorId, applicationId, null);
  }

  public static ApplicationIdJMX createAcctApplicationId(long applicationId) {
    return createAcctApplicationId(0L, applicationId);
  }

  public static ApplicationIdJMX createAuthApplicationId(long applicationId) {
    return createAuthApplicationId(0L, applicationId);
  }

  public Long getAcctApplicationId() {
    return acctApplicationId;
  }

  public Long getAuthApplicationId() {
    return authApplicationId;
  }

  public Long getVendorId() {
    return vendorId;
  }

  public ApplicationId asApplicationId() {
    return authApplicationId != null ? ApplicationId.createByAuthAppId(vendorId, authApplicationId) : ApplicationId.createByAccAppId(vendorId, acctApplicationId);
  }

  public static ApplicationIdJMX fromApplicationId(ApplicationId appId) {
    if(appId.getAuthAppId() != 0) {
      return new ApplicationIdJMX(appId.getVendorId(), appId.getAuthAppId(), null);
    }
    else {
      return new ApplicationIdJMX(appId.getVendorId(), null, appId.getAcctAppId());      
    }
  }

  @Override
  public String toString() {
    return "ApplicationID[vendor=" + vendorId + "; Auth=" + authApplicationId + "; Acct=" + acctApplicationId + "]";
  }
}
