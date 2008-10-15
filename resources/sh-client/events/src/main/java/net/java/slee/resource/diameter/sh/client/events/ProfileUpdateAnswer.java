/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.sh.client.events;


import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * Defines an interface representing the Profile-Update-Answer command.
 *
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0) specification:
 * <pre>
 * 6.1.4        Profile-Update-Answer (PUA) Command
 * 
 * The Profile-Update-Answer (PUA) command, indicated by the Command-Code field
 * set to 307 and the 'R' bit cleared in the Command Flags field, is sent by a
 * server in response to the Profile-Update-Request command. The
 * Experimental-Result AVP may contain one of the values defined in section 6.2 or
 * in 3GPP TS 29.229 [6].
 * 
 * Message Format
 * &lt; Profile-Update-Answer &gt; ::=   &lt; Diameter Header: 307, PXY, 16777217 &gt;
 *                                 &lt; Session-Id &gt;
 *                                 { Vendor-Specific-Application-Id }
 *                                 [ Result-Code ]
 *                                 [ Experimental-Result ]
 *                                 { Auth-Session-State }
 *                                 { Origin-Host }
 *                                 { Origin-Realm }
 *                                 *[ Supported-Features ]
 *                                 *[ AVP ]
 *                                 *[ Failed-AVP ]
 *                                 *[ Proxy-Info ]
 *                                 *[ Route-Record ]
 * </pre>
 */
public interface ProfileUpdateAnswer extends DiameterShMessage {

    int commandCode = 307;

    /**
     * Returns true if the Session-Id AVP is present in the message.
     */
    boolean hasSessionId();

    /**
     * Returns the value of the Session-Id AVP, of type UTF8String.
     * @return the value of the Session-Id AVP or null if it has not been set on this message
     */
    String getSessionId();

    /**
     * Sets the value of the Session-Id AVP, of type UTF8String.
     * @throws IllegalStateException if setSessionId has already been called
     */
    void setSessionId(String sessionId);

    /**
     * Returns true if the Vendor-Specific-Application-Id AVP is present in the message.
     */
    boolean hasVendorSpecificApplicationId();

    /**
     * Returns the value of the Vendor-Specific-Application-Id AVP, of type Grouped.
     * @return the value of the Vendor-Specific-Application-Id AVP or null if it has not been set on this message
     */
    VendorSpecificApplicationIdAvp getVendorSpecificApplicationId();

    /**
     * Sets the value of the Vendor-Specific-Application-Id AVP, of type Grouped.
     * @throws IllegalStateException if setVendorSpecificApplicationId has already been called
     */
    void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId);

    /**
     * Returns true if the Result-Code AVP is present in the message.
     */
    boolean hasResultCode();

    /**
     * Returns the value of the Result-Code AVP, of type Unsigned32.
     * Use {@link #hasResultCode()} to check the existence of this AVP.  
     * @return the value of the Result-Code AVP
     * @throws IllegalStateException if the Result-Code AVP has not been set on this message
     */
    long getResultCode();

    /**
     * Sets the value of the Result-Code AVP, of type Unsigned32.
     * @throws IllegalStateException if setResultCode has already been called
     */
    void setResultCode(long resultCode);

    /**
     * Returns true if the Experimental-Result AVP is present in the message.
     */
    boolean hasExperimentalResult();

    /**
     * Returns the value of the Experimental-Result AVP, of type Grouped.
     * @return the value of the Experimental-Result AVP or null if it has not been set on this message
     */
    ExperimentalResultAvp getExperimentalResult();

    /**
     * Sets the value of the Experimental-Result AVP, of type Grouped.
     * @throws IllegalStateException if setExperimentalResult has already been called
     */
    void setExperimentalResult(ExperimentalResultAvp experimentalResult);

    /**
     * Returns true if the Auth-Session-State AVP is present in the message.
     */
    boolean hasAuthSessionState();

    /**
     * Returns the value of the Auth-Session-State AVP, of type Enumerated.
     * @return the value of the Auth-Session-State AVP or null if it has not been set on this message
     */
    AuthSessionStateType getAuthSessionState();

    /**
     * Sets the value of the Auth-Session-State AVP, of type Enumerated.
     * @throws IllegalStateException if setAuthSessionState has already been called
     */
    void setAuthSessionState(AuthSessionStateType authSessionState);

    /**
     * Returns true if the Origin-Host AVP is present in the message.
     */
    boolean hasOriginHost();

    /**
     * Returns the value of the Origin-Host AVP, of type DiameterIdentity.
     * @return the value of the Origin-Host AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getOriginHost();

    /**
     * Sets the value of the Origin-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setOriginHost has already been called
     */
    void setOriginHost(DiameterIdentityAvp originHost);

    /**
     * Returns true if the Origin-Realm AVP is present in the message.
     */
    boolean hasOriginRealm();

    /**
     * Returns the value of the Origin-Realm AVP, of type DiameterIdentity.
     * @return the value of the Origin-Realm AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getOriginRealm();

    /**
     * Sets the value of the Origin-Realm AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setOriginRealm has already been called
     */
    void setOriginRealm(DiameterIdentityAvp originRealm);

    /**
     * Returns the set of Supported-Features AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Supported-Features AVPs have been set.
     * The elements in the given array are SupportedFeatures objects.
     */
    SupportedFeaturesAvp[] getSupportedFeatureses();

    /**
     * Sets a single Supported-Features AVP in the message, of type Grouped.
     * @throws IllegalStateException if setSupportedFeatures or setSupportedFeatureses
     *  has already been called
     */
    void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures);

    /**
     * Sets the set of Supported-Features AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getSupportedFeatureses() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setSupportedFeatures or setSupportedFeatureses
     *  has already been called
     */
    void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses);

    /**
     * Returns the set of Failed-AVP AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Failed-AVP AVPs have been set.
     * The elements in the given array are FailedAvp objects.
     */
    FailedAvp[] getFailedAvps();

    /**
     * Sets a single Failed-AVP AVP in the message, of type Grouped.
     * @throws IllegalStateException if setFailedAvp or setFailedAvps
     *  has already been called
     */
    void setFailedAvp(FailedAvp failedAvp);

    /**
     * Sets the set of Failed-AVP AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getFailedAvps() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setFailedAvp or setFailedAvps
     *  has already been called
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
    DiameterIdentityAvp[] getRouteRecords();

    /**
     * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
     * @throws IllegalStateException if setRouteRecord or setRouteRecords
     *  has already been called
     */
    void setRouteRecord(DiameterIdentityAvp routeRecord);

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
    void setRouteRecords(DiameterIdentityAvp[] routeRecords);

    /**
     * Returns the set of extension AVPs. The returned array contains the extension AVPs
     * in the order they appear in the message.
     * A return value of null implies that no extensions AVPs have been set.
     */
    DiameterAvp[] getExtensionAvps();

    /**
     * Sets the set of extension AVPs with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getExtensionAvps() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws AvpNotAllowedException if an AVP is encountered of a type already known to this class
     *   (i.e. an AVP for which get/set methods already appear in this class)
     * @throws IllegalStateException if setExtensionAvps has already been called
     */
    void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException;

}
