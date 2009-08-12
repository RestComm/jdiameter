package net.java.slee.resource.diameter.cxdx.events;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;
import net.java.slee.resource.diameter.cxdx.events.avp.LooseRouteIndication;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * <pre>
 * <b>6.1.4 Server-Assignment-Answer (SAA) Command</b>
 * The Server-Assignment-Answer (SAA) command, indicated by the Command-Code field set to 301 and
 * the ‘R’ bit cleared in the Command Flags field, is sent by a server in response to the 
 * Server-Assignment-Request command. The Experimental-Result AVP may contain one of the values 
 * defined in section 6.2. If Result-Code or Experimental-Result does not inform about an error, 
 * the User-Data AVP shall contain the information that the S-CSCF needs to give service to the user.
 * 
 * Message Format
 * <Server-Assignment-Answer> ::=  < Diameter Header: 301, PXY, 16777216 >
 *                            < Session-Id >
 *                            { Vendor-Specific-Application-Id }
 *                            [ Result-Code ]
 *                            [ Experimental-Result ]
 *                            { Auth-Session-State }
 *                            { Origin-Host }
 *                            { Origin-Realm }
 *                            [ User-Name ]
 *                           *[ Supported-Features ]
 *                            [ User-Data ]
 *                            [ Charging-Information ]
 *                            [ Associated-Identities ]
 *                            [ Loose-Route-Indication ]
 *                           *[ SCSCF-Restoration-Info ]
 *                            [ Associated-Registered-Identities ]
 *                            [ Server-Name ]
 *                           *[ AVP ]
 *                           *[ Failed-AVP ]
 *                           *[ Proxy-Info ]
 *                           *[ Route-Record ]
 *
 *
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ServerAssignmentAnswer extends DiameterMessage {

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
   * Returns true if the User-Data AVP is present in the message.
   */
  boolean hasUserData();

  /**
   * Returns the value of the User-Data AVP, of type OctetString.
   * @return the value of the User-Data AVP or null if it has not been set on this message
   */
  String getUserData();

  /**
   * Sets the value of the User-Data AVP, of type OctetString.
   * @throws IllegalStateException if setUserData has already been called
   */
  void setUserData(String userData);

  /**
   * Returns true if the Charging-Information AVP is present in the message.
   */
  boolean hasChargingInformation();

  /**
   * Returns the value of the Charging-Information AVP, of type Grouped.
   * @return the value of the Charging-Information AVP or null if it has not been set on this message
   */
  ChargingInformation getChargingInformation();

  /**
   * Sets the value of the Charging-Information AVP, of type Grouped.
   * @throws IllegalStateException if setChargingInformation has already been called
   */
  void setChargingInformation(ChargingInformation chargingInformation);

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
   * Returns true if the Loose-Route-Indication AVP is present in the message.
   */
  boolean hasLooseRouteIndication();

  /**
   * Returns the value of the Loose-Route-Indication AVP, of type Enumerated. A
   * return value of null implies that the AVP has not been set.
   */
  LooseRouteIndication getLooseRouteIndication();

  /**
   * Sets the value of the Loose-Route-Indication AVP, of type Enumerated.
   * 
   * @throws IllegalStateException
   *             if setLooseRouteIndication has already been called
   */
  void setLooseRouteIndication(LooseRouteIndication looseRouteIndication);

  /**
   * Returns the value of the SCSCF-Restoration-Info AVP, of type Grouped.
   * 
   * @return the value of the SCSCF-Restoration-Info AVP or null if it has not been set on this message
   */
  SCSCFRestorationInfo[] getSCSCFRestorationInfos();

  /**
   * Sets the value of the SCSCF-Restoration-Info AVP, of type Grouped.
   * 
   * @throws IllegalStateException if setSCSCFRestorationInfo has already been called
   */
  void setSCSCFRestorationInfo(SCSCFRestorationInfo scscfRestorationInfo);

  /**
   * Sets the value of the SCSCF-Restoration-Info AVP, of type Grouped.
   * 
   * @throws IllegalStateException if setSCSCFRestorationInfo has already been called
   */
  void setSCSCFRestorationInfos(SCSCFRestorationInfo[] scscfRestorationInfos);

  /**
   * Returns true if the Associated-Registered-Identities AVP is present in the message.
   */
  boolean hasAssociatedRegisteredIdentities();

  /**
   * Returns the value of the Associated-Registered-Identities AVP, of type Grouped.
   * @return the value of the Associated-Registered-Identities AVP or null if it has not been set on this message
   */
  AssociatedRegisteredIdentities getAssociatedRegisteredIdentities();

  /**
   * Sets the value of the Associated-Registered-Identities AVP, of type Grouped.
   * @throws IllegalStateException if setAssociatedRegisteredIdentities has already been called
   */
  void setAssociatedRegisteredIdentities(AssociatedRegisteredIdentities associatedRegisteredIdentities);

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
