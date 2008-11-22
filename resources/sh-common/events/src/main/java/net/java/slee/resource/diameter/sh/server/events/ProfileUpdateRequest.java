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
package net.java.slee.resource.diameter.sh.server.events;


import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.DiameterShMessage;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

/**
 * Defines an interface representing the Profile-Update-Request command.
 *
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0) specification:
 * <pre>
 * 6.1.3        Profile-Update-Request (PUR) Command
 * 
 * The Profile-Update-Request (PUR) command, indicated by the Command-Code field
 * set to 307 and the 'R' bit set in the Command Flags field, is sent by a
 * Diameter client to a Diameter server in order to update user data in the
 * server. 
 * 
 * Message Format
 * &lt; Profile-Update-Request &gt; ::=      &lt; Diameter Header: 307, REQ, PXY, 16777217 &gt;
 *                                     &lt; Session-Id &gt;
 *                                     { Vendor-Specific-Application-Id }
 *                                     { Auth-Session-State }
 *                                     { Origin-Host }
 *                                     { Origin-Realm }
 *                                     [ Destination-Host ]
 *                                     { Destination-Realm }
 *                                     *[ Supported-Features ]
 *                                     { User-Identity }
 *                                     { Data-Reference }
 *                                     { User-Data }
 *                                     *[ AVP ]
 *                                     *[ Proxy-Info ]
 *                                     *[ Route-Record ]
 * </pre>
 */
public interface ProfileUpdateRequest extends DiameterShMessage {

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
     * Returns true if the Destination-Host AVP is present in the message.
     */
    boolean hasDestinationHost();

    /**
     * Returns the value of the Destination-Host AVP, of type DiameterIdentity.
     * @return the value of the Destination-Host AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getDestinationHost();

    /**
     * Sets the value of the Destination-Host AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setDestinationHost has already been called
     */
    void setDestinationHost(DiameterIdentityAvp destinationHost);

    /**
     * Returns true if the Destination-Realm AVP is present in the message.
     */
    boolean hasDestinationRealm();

    /**
     * Returns the value of the Destination-Realm AVP, of type DiameterIdentity.
     * @return the value of the Destination-Realm AVP or null if it has not been set on this message
     */
    DiameterIdentityAvp getDestinationRealm();

    /**
     * Sets the value of the Destination-Realm AVP, of type DiameterIdentity.
     * @throws IllegalStateException if setDestinationRealm has already been called
     */
    void setDestinationRealm(DiameterIdentityAvp destinationRealm);

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
     * Returns true if the User-Identity AVP is present in the message.
     */
    boolean hasUserIdentity();

    /**
     * Returns the value of the User-Identity AVP, of type Grouped.
     * @return the value of the User-Identity AVP or null if it has not been set on this message
     */
    UserIdentityAvp getUserIdentity();

    /**
     * Sets the value of the User-Identity AVP, of type Grouped.
     * @throws IllegalStateException if setUserIdentity has already been called
     */
    void setUserIdentity(UserIdentityAvp userIdentity);

    /**
     * Returns true if the Data-Reference AVP is present in the message.
     */
    boolean hasDataReference();

    /**
     * Returns the value of the Data-Reference AVP, of type Enumerated.
     * @return the value of the Data-Reference AVP or null if it has not been set on this message
     */
    DataReferenceType getDataReference();

    /**
     * Sets the value of the Data-Reference AVP, of type Enumerated.
     * @throws IllegalStateException if setDataReference has already been called
     */
    void setDataReference(DataReferenceType dataReference);

    /**
     * Returns true if the User-Data AVP is present in the message.
     */
    boolean hasUserData();

    /**
     * Returns the value of the User-Data AVP, of type UserData.
     * @return the value of the User-Data AVP or null if it has not been set on this message
     */
    String getUserData();

    /**
     * Sets the value of the User-Data AVP, of type UserData.
     * @throws IllegalStateException if setUserData has already been called
     */
    void setUserData(byte[] userData);

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
