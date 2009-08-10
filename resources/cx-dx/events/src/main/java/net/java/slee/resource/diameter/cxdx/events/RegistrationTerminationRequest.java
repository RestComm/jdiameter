package net.java.slee.resource.diameter.cxdx.events;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * <pre>
 * <b>6.1.9  Registration-Termination-Request (RTR) Command</b>
 * The Registration-Termination-Request (RTR) command, indicated by the Command-Code field set to 
 * 304 and the ‘R’ bit set in the Command Flags field, is sent by a Diameter Multimedia server to 
 * a Diameter Multimedia client in order to request the de-registration of a user.
 * 
 * Message Format
 * <Registration-Termination-Request> ::=    < Diameter Header: 304, REQ, PXY, 16777216 >
 *                                    < Session-Id >
 *                                    { Vendor-Specific-Application-Id }
 *                                    { Auth-Session-State }
 *                                    { Origin-Host }
 *                                    { Origin-Realm }
 *                                    { Destination-Host }
 *                                    { Destination-Realm }
 *                                    { User-Name }
 *                                    [ Associated-Identities ]
 *                                   *[ Supported-Features ]
 *                                   *[ Public-Identity ]
 *                                    { Deregistration-Reason }
 *                                   *[ AVP ]
 *                                   *[ Proxy-Info ]
 *                                   *[ Route-Record ]
 *
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RegistrationTerminationRequest extends DiameterMessage {

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
   * Returns true if the Associated-Identities AVP is present in the message.
   */
  boolean hasAssociatedIdentities();

  /**
   * Returns the value of the Associated-Identities AVP, of type Grouped.
   * @return the value of the Associated-Identities AVP or null if it has not been set on this message
   */
  AssociatedIdentities getAssociatedIdentities();

  /**
   * Sets the value of the Associated-Identities AVP, of type Grouped.
   * @throws IllegalStateException if setAssociatedIdentities has already been called
   */
  void setAssociatedIdentities(AssociatedIdentities associatedIdentities);

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
   * Returns the value of the Public-Identity AVP, of type UTF8String.
   * A return value of null implies that the AVP has not been set.
   */
  String[] getPublicIdentities();

  /**
   * Sets the value of the Public-Identity AVP, of type UTF8String.
   * @throws IllegalStateException if setPublicIdentity has already been called
   */
  void setPublicIdentity(String publicIdentity);

  /**
   * Sets the value of the Public-Identity AVP, of type UTF8String.
   * @throws IllegalStateException if setPublicIdentity has already been called
   */
  void setPublicIdentities(String[] publicIdentities);

  /**
   * Returns true if the Deregistration-Reason AVP is present in the message.
   */
  boolean hasDeregistrationReason();

  /**
   * Returns the value of the Deregistration-Reason AVP, of type Grouped.
   * A return value of null implies that the AVP has not been set.
   */
  DeregistrationReason getDeregistrationReason();

  /**
   * Sets the value of the Deregistration-Reason AVP, of type Grouped.
   * @throws IllegalStateException if setDeregistrationReason has already been called
   */
  void setDeregistrationReason(DeregistrationReason publicIdentity);

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
