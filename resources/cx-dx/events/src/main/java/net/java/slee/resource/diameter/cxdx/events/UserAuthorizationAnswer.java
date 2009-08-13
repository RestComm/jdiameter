package net.java.slee.resource.diameter.cxdx.events;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.ro.events.avp.ServerCapabilities;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * <pre>
 * <b>6.1.2 User-Authorization-Answer (UAA) Command</b>
 * The User-Authorization-Answer (UAA) command, indicated by the Command-Code field set to 300 and
 * the ‘R’ bit cleared in the Command Flags field, is sent by a server in response to the 
 * User-Authorization-Request command. The Experimental-Result AVP may contain one of the values 
 * defined in section 6.2.
 * 
 * Message Format
 * < User-Authorization-Answer> ::=      < Diameter Header: 300, PXY, 16777216 >
 *                              < Session-Id >
 *                              { Vendor-Specific-Application-Id }
 *                              [ Result-Code ]
 *                              [ Experimental-Result ]
 *                              { Auth-Session-State }
 *                              { Origin-Host }
 *                              { Origin-Realm }
 *                             *[ Supported-Features ]
 *                              [ Server-Name ]
 *                              [ Server-Capabilities ]
 *                              [ Wildcarded-IMPU ]
 *                             *[ AVP ]
 *                             *[ Failed-AVP ]
 *                             *[ Proxy-Info ]
 *                             *[ Route-Record ]
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface UserAuthorizationAnswer extends DiameterMessage {

  public static final int COMMAND_CODE = 300;

  /**
   * Returns true if the Vendor-Specific-Application-Id AVP is present in the
   * message.
   */
  boolean hasVendorSpecificApplicationId();

  /**
   * Returns the value of the Vendor-Specific-Application-Id AVP, of type
   * Grouped.
   * 
   * @return the value of the Vendor-Specific-Application-Id AVP or null if it
   *         has not been set on this message
   */
  VendorSpecificApplicationIdAvp getVendorSpecificApplicationId();

  /**
   * Sets the value of the Vendor-Specific-Application-Id AVP, of type
   * Grouped.
   * 
   * @throws IllegalStateException
   *             if setVendorSpecificApplicationId has already been called
   */
  void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId);

  /**
   * Returns true if the Result-Code AVP is present in the message.
   * 
   * @return
   */
  boolean hasResultCode();

  /**
   * Returns the value of the Result-Code AVP, of type Unsigned32.
   * 
   * @return
   */
  long getResultCode();

  /**
   * Sets the value of the Result-Code AVP, of type Unsigned32.
   * 
   * @param resultCode
   * @throws IllegalStateException 
   */
  void setResultCode(long resultCode) throws IllegalStateException;

  /**
   * Returns true if the Experimental-Result AVP is present in the message.
   */
  boolean hasExperimentalResult();

  /**
   * Returns the value of the Experimental-Result AVP, of type Grouped.
   * 
   * @return the value of the Experimental-Result AVP or null if it has not
   *         been set on this message
   */
  ExperimentalResultAvp getExperimentalResult();

  /**
   * Sets the value of the Experimental-Result AVP, of type Grouped.
   * 
   * @throws IllegalStateException
   *             if setExperimentalResult has already been called
   */
  void setExperimentalResult(ExperimentalResultAvp experimentalResult);

  /**
   * Returns true if the Auth-Session-State AVP is present in the message.
   */
  boolean hasAuthSessionState();

  /**
   * Returns the value of the Auth-Session-State AVP, of type Enumerated. A
   * return value of null implies that the AVP has not been set.
   */
  AuthSessionStateType getAuthSessionState();

  /**
   * Sets the value of the Auth-Session-State AVP, of type Enumerated.
   * 
   * @throws IllegalStateException
   *             if setAuthSessionState has already been called
   */
  void setAuthSessionState(AuthSessionStateType authSessionState);

  /**
   * Returns the set of Supported-Features AVPs. The returned array contains
   * the AVPs in the order they appear in the message. A return value of null
   * implies that no Supported-Features AVPs have been set. The elements in
   * the given array are SupportedFeatures objects.
   */
  SupportedFeaturesAvp[] getSupportedFeatureses();

  /**
   * Sets a single Supported-Features AVP in the message, of type Grouped.
   * 
   * @throws IllegalStateException
   *             if setSupportedFeatures or setSupportedFeatureses has already
   *             been called
   */
  void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures);

  /**
   * Sets the set of Supported-Features AVPs, with all the values in the given
   * array. The AVPs will be added to message in the order in which they
   * appear in the array.
   * 
   * Note: the array must not be altered by the caller following this call,
   * and getSupportedFeatureses() is not guaranteed to return the same array
   * instance, e.g. an "==" check would fail.
   * 
   * @throws IllegalStateException
   *             if setSupportedFeatures or setSupportedFeatureses has already
   *             been called
   */
  void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses);

  /**
   * Returns true if the Server-Name AVP is present in the message.
   */
  boolean hasServerName();

  /**
   * Returns the value of the Server-Name AVP, of type UTF8String.
   * 
   * @return the value of the Server-Name AVP or null if it has not been set
   *         on this message
   */
  String getServerName();

  /**
   * Sets the value of the Server-Name AVP, of type UTF8String.
   * 
   * @throws IllegalStateException
   *             if setServerName has already been called
   */
  void setServerName(String serverName);

  /**
   * Returns true if the Server-Capabilities AVP is present in the message.
   */
  boolean hasServerCapabilities();

  /**
   * Returns the value of the Server-Capabilities AVP, of type Grouped. A 
   * return value of null implies that the AVP has not been set.
   */
  ServerCapabilities getServerCapabilities();

  /**
   * Sets the value of the Server-Capabilities AVP, of type Grouped.
   */
  void setServerCapabilities(ServerCapabilities serverCapabilities);

  /**
   * Returns true if the Wildcarded-IMPU AVP is present in the message.
   */
  boolean hasWildcardedIMPU();

  /**
   * Returns the value of the Wildcarded-IMPU AVP, of type UTF8String. A 
   * return value of null implies that the AVP has not been set.
   */
  String getWildcardedIMPU();

  /**
   * Sets the value of the Wildcarded-IMPU AVP, of type UTF8String.
   */
  void setWildcardedIMPU(String wildcardedIMPU);

  /**
   * Returns the set of Failed-AVP AVPs. The returned array contains the AVPs
   * in the order they appear in the message. A return value of null implies
   * that no Failed-AVP AVPs have been set. The elements in the given array
   * are FailedAvp objects.
   */
  FailedAvp[] getFailedAvps();

  /**
   * Sets a single Failed-AVP AVP in the message, of type Grouped.
   * 
   * @throws IllegalStateException
   *             if setFailedAvp or setFailedAvps has already been called
   */
  void setFailedAvp(FailedAvp failedAvp);

  /**
   * Sets the set of Failed-AVP AVPs, with all the values in the given array.
   * The AVPs will be added to message in the order in which they appear in
   * the array.
   * 
   * Note: the array must not be altered by the caller following this call,
   * and getFailedAvps() is not guaranteed to return the same array instance,
   * e.g. an "==" check would fail.
   * 
   * @throws IllegalStateException
   *             if setFailedAvp or setFailedAvps has already been called
   */
  void setFailedAvps(FailedAvp[] failedAvps);

  /**
   * Returns the set of Proxy-Info AVPs. The returned array contains
   * the AVPs in the order they appear in the message.
   * A return value of null implies that no Proxy-Info AVPs have been set.
   * The elements in the given array are ProxyInfo objects.
   */
  ProxyInfoAvp[] getProxyInfos();

  /**
   * Sets a single Proxy-Info AVP in the message, of type Grouped.
   * @throws IllegalStateException if setProxyInfo or setProxyInfos
   *  has already been called
   */
  void setProxyInfo(ProxyInfoAvp proxyInfo);

  /**
   * Sets the set of Proxy-Info AVPs, with all the values in the given array.
   * The AVPs will be added to message in the order in which they appear in the array.
   *
   * Note: the array must not be altered by the caller following this call, and
   * getProxyInfos() is not guaranteed to return the same array instance,
   * e.g. an "==" check would fail.
   *
   * @throws IllegalStateException if setProxyInfo or setProxyInfos
   *  has already been called
   */
  void setProxyInfos(ProxyInfoAvp[] proxyInfos);

  /**
   * Returns the set of Route-Record AVPs. The returned array contains
   * the AVPs in the order they appear in the message.
   * A return value of null implies that no Route-Record AVPs have been set.
   * The elements in the given array are DiameterIdentity objects.
   */
  DiameterIdentity[] getRouteRecords();

  /**
   * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
   * @throws IllegalStateException if setRouteRecord or setRouteRecords
   *  has already been called
   */
  void setRouteRecord(DiameterIdentity routeRecord);

  /**
   * Sets the set of Route-Record AVPs, with all the values in the given array.
   * The AVPs will be added to message in the order in which they appear in the array.
   *
   * Note: the array must not be altered by the caller following this call, and
   * getRouteRecords() is not guaranteed to return the same array instance,
   * e.g. an "==" check would fail.
   *
   * @throws IllegalStateException if setRouteRecord or setRouteRecords
   *  has already been called
   */
  void setRouteRecords(DiameterIdentity[] routeRecords);

}
