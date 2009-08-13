package net.java.slee.resource.diameter.cxdx.events;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.cxdx.events.avp.MultipleRegistrationIndication;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.ServerAssignmentType;
import net.java.slee.resource.diameter.cxdx.events.avp.UserDataAlreadyAvailable;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * <pre>
 * <b>6.1.3 Server-Assignment-Request (SAR) Command</b>
 * The Server-Assignment-Request (SAR) command, indicated by the Command-Code field set to 301 and
 * the ‘R’ bit set in the Command Flags field, is sent by a Diameter Multimedia client to a 
 * Diameter Multimedia server in order to request it to store the name of the server that is 
 * currently serving the user.
 * 
 * Message Format
 * <Server-Assignment-Request> ::= < Diameter Header: 301, REQ, PXY, 16777216 >
 *                             < Session-Id >
 *                             { Vendor-Specific-Application-Id }
 *                             { Auth-Session-State }
 *                             { Origin-Host }
 *                             { Origin-Realm }
 *                             [ Destination-Host ]
 *                             { Destination-Realm }
 *                             [ User-Name ]
 *                            *[ Supported-Features ]
 *                            *[ Public-Identity ]
 *                             [ Wildcarded-PSI ]
 *                             [ Wildcarded-IMPU ]
 *                             { Server-Name }
 *                             { Server-Assignment-Type }
 *                             { User-Data-Already-Available }
 *                             [ SCSCF-Restoration-Info ]
 *                             [ Multiple-Registration-Indication ]
 *                            *[ AVP ]
 *                            *[ Proxy-Info ]
 *                            *[ Route-Record ]
 *
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ServerAssignmentRequest extends DiameterMessage {

  public static final int COMMAND_CODE = 301;

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
   * Sets the set of Public-Identity AVPs, with all the values in the given
   * array. The AVPs will be added to message in the order in which they
   * appear in the array.
   * 
   * Note: the array must not be altered by the caller following this call,
   * and getPublicIdentities() is not guaranteed to return the same array
   * instance, e.g. an "==" check would fail.
   * 
   * @throws IllegalStateException
   *             if setPublicIdentity or setPublicIdentities has already
   *             been called
   */
  void setPublicIdentities(String[] publicIdentities);

  /**
   * Returns true if the Wildcarded-PSI AVP is present in the message.
   */
  boolean hasWildcardedPSI();

  /**
   * Returns the value of the Wildcarded-PSI AVP, of type UTF8String. A 
   * return value of null implies that the AVP has not been set.
   */
  String getWildcardedPSI();

  /**
   * Sets the value of the Wildcarded-PSI AVP, of type UTF8String.
   */
  void setWildcardedPSI(String wildcardedPSI);

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
   * Returns true if the Server-Assignment-Type AVP is present in the message.
   */
  boolean hasServerAssignmentType();

  /**
   * Returns the value of the Server-Assignment-Type AVP, of type Enumerated.
   * 
   * @return the value of the Server-Assignment-Type AVP or null if it has not been set on this message
   */
  ServerAssignmentType getServerAssignmentType();

  /**
   * Sets the value of the Server-Assignment-Type AVP, of type Enumerated.
   * 
   * @throws IllegalStateException if setServerAssignmentType has already been called
   */
  void setServerAssignmentType(ServerAssignmentType serverAssignmentType);

  /**
   * Returns true if the User-Data-Already-Available AVP is present in the message.
   */
  boolean hasUserDataAlreadyAvailable();

  /**
   * Returns the value of the User-Data-Already-Available AVP, of type Enumerated.
   * 
   * @return the value of the User-Data-Already-Available AVP or null if it has not been set on this message
   */
  UserDataAlreadyAvailable getUserDataAlreadyAvailable();

  /**
   * Sets the value of the User-Data-Already-Available AVP, of type Enumerated.
   * 
   * @throws IllegalStateException if setUserDataAlreadyAvailable has already been called
   */
  void setUserDataAlreadyAvailable(UserDataAlreadyAvailable userDataAlreadyAvailable);

  /**
   * Returns true if the SCSCF-Restoration-Info AVP is present in the message.
   */
  boolean hasSCSCFRestorationInfo();

  /**
   * Returns the value of the SCSCF-Restoration-Info AVP, of type Grouped.
   * 
   * @return the value of the SCSCF-Restoration-Info AVP or null if it has not been set on this message
   */
  SCSCFRestorationInfo getSCSCFRestorationInfo();

  /**
   * Sets the value of the SCSCF-Restoration-Info AVP, of type Grouped.
   * 
   * @throws IllegalStateException if setSCSCFRestorationInfo has already been called
   */
  void setSCSCFRestorationInfo(SCSCFRestorationInfo scscfRestorationInfo);

  /**
   * Returns true if the Multiple-Registration-Indication AVP is present in the message.
   */
  boolean hasMultipleRegistrationIndication();

  /**
   * Returns the value of the Multiple-Registration-Indication AVP, of type Enumerated.
   * 
   * @return the value of the Multiple-Registration-Indication AVP or null if it has not been set on this message
   */
  MultipleRegistrationIndication getMultipleRegistrationIndication();

  /**
   * Sets the value of the Multiple-Registration-Indication AVP, of type Enumerated.
   * 
   * @throws IllegalStateException if setMultipleRegistrationIndication has already been called
   */
  void setMultipleRegistrationIndication(MultipleRegistrationIndication multipleRegistrationIndication);

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
