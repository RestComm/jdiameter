package net.java.slee.resource.diameter.cxdx.events;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * <pre>
 * <b>6.1.7  Multimedia-Auth-Request (MAR) Command</b>
 * The Multimedia-Auth-Request (MAR) command, indicated by the Command-Code field set to 303 and
 * the ‘R’ bit set in the Command Flags field, is sent by a Diameter Multimedia client to a 
 * Diameter Multimedia server in order to request security information.
 * 
 * Message Format
 * < Multimedia-Auth-Request > ::=  < Diameter Header: 303, REQ, PXY, 16777216 >
 *                             < Session-Id >
 *                             { Vendor-Specific-Application-Id }
 *                             { Auth-Session-State }
 *                             { Origin-Host }
 *                             { Origin-Realm }
 *                             { Destination-Realm }
 *                             [ Destination-Host ]
 *                             { User-Name }
 *                            *[ Supported-Features ]
 *                             { Public-Identity }
 *                             { SIP-Auth-Data-Item }
 *                             { SIP-Number-Auth-Items }
 *                             { Server-Name }
 *                            *[ AVP ]
 *                            *[ Proxy-Info ]
 *                            *[ Route-Record ]
 *
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface MultimediaAuthenticationRequest extends DiameterMessage {

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
   * Returns true if the User-Name AVP is present in the message.
   */
  boolean hasUserName();

  /**
   * Returns the value of the User-Name AVP, of type UTF8String.
   * @return the value of the User-Name AVP or null if it has not been set on this message
   */
  String getUserName();

  /**
   * Sets the value of the User-Name AVP, of type UTF8String.
   * @throws IllegalStateException if setUserName has already been called
   */
  void setUserName(String userName);

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
   * Returns the set of Supported-Features AVPs. The returned array contains
   * the AVPs in the order they appear in the message. A return value of null
   * implies that no Supported-Features AVPs have been set. The elements in
   * the given array are SupportedFeatures objects.
   */
  SupportedFeaturesAvp[] getSupportedFeatureses();

  /**
   * Returns true if the Public-Identity AVP is present in the message.
   */
  boolean hasPublicIdentity();

  /**
   * Returns the value of the Public-Identity AVP, of type UTF8String.
   * A return value of null implies that the AVP has not been set.
   */
  String getPublicIdentity();

  /**
   * Sets the value of the Public-Identity AVP, of type UTF8String.
   * @throws IllegalStateException if setPublicIdentity has already been called
   */
  void setPublicIdentity(String publicIdentity);

  /**
   * Returns true if the SIP-Auth-Data-Item AVP is present in the message.
   */
  boolean hasSIPAuthDataItem();

  /**
   * Returns the value of the SIP-Auth-Data-Item AVP, of type Grouped.
   * A return value of null implies that the AVP has not been set.
   */
  SIPAuthDataItem getSIPAuthDataItem();

  /**
   * Sets the value of the SIP-Auth-Data-Item AVP, of type Grouped.
   * @throws IllegalStateException if setSIPAuthDataItem has already been called
   */
  void setSIPAuthDataItem(SIPAuthDataItem sipAuthDataItem);

  /**
   * Returns true if the SIP-Number-Auth-Items AVP is present in the message.
   */
  boolean hasSIPNumberAuthItems();

  /**
   * Returns the value of the SIP-Number-Auth-Items AVP, of type Unsigned32.
   * A return value of null implies that the AVP has not been set.
   */
  SIPAuthDataItem getSIPNumberAuthItems();

  /**
   * Sets the value of the SIP-Number-Auth-Items AVP, of type Unsigned32.
   * @throws IllegalStateException if setSIPNumberAuthItems has already been called
   */
  void setSIPNumberAuthItems(long sipNumberAuthItems);

  /**
   * Returns true if the Server-Name AVP is present in the message.
   */
  boolean hasServerName();

  /**
   * Returns the value of the Server-Name AVP, of type UTF8String.
   * A return value of null implies that the AVP has not been set.
   */
  String getServerName();

  /**
   * Sets the value of the Server-Name AVP, of type UTF8String.
   * @throws IllegalStateException if setServerName has already been called
   */
  void setServerName(String serverName);

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
