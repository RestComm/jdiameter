/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package net.java.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * Interface defining accessor methods for common avp fields in all sh message.
 * It extends {@link DiameterMessage} to provide some generic accessors.
 */
public interface DiameterShMessage extends DiameterMessage {

	/**
	 * Returns true if the Vendor-Specific-Application-Id AVP is present in the
	 * message.
	 */
	boolean hasVendorSpecificApplicationId();

	/**
	 * Returns the value of the Vendor-Specific-Application-Id AVP, of type
	 * Grouped. A return value of null implies that the AVP has not been set.
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
	 * Returns the set of Proxy-Info AVPs. The returned array contains the AVPs
	 * in the order they appear in the message. A return value of null implies
	 * that no Proxy-Info AVPs have been set. The elements in the given array
	 * are ProxyInfo objects.
	 */
	ProxyInfoAvp[] getProxyInfos();

	/**
	 * Sets a single Proxy-Info AVP in the message, of type Grouped.
	 * 
	 * @throws IllegalStateException
	 *             if setProxyInfo or setProxyInfos has already been called
	 */
	void setProxyInfo(ProxyInfoAvp proxyInfo);

	/**
	 * Sets the set of Proxy-Info AVPs, with all the values in the given array.
	 * The AVPs will be added to message in the order in which they appear in
	 * the array.
	 * 
	 * Note: the array must not be altered by the caller following this call,
	 * and getProxyInfos() is not guaranteed to return the same array instance,
	 * e.g. an "==" check would fail.
	 * 
	 * @throws IllegalStateException
	 *             if setProxyInfo or setProxyInfos has already been called
	 */
	void setProxyInfos(ProxyInfoAvp[] proxyInfos);

	/**
	 * Returns the set of Route-Record AVPs. The returned array contains the
	 * AVPs in the order they appear in the message. A return value of null
	 * implies that no Route-Record AVPs have been set. The elements in the
	 * given array are DiameterIdentity objects.
	 */
	DiameterIdentityAvp[] getRouteRecords();

	/**
	 * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
	 * 
	 * @throws IllegalStateException
	 *             if setRouteRecord or setRouteRecords has already been called
	 */
	void setRouteRecord(DiameterIdentityAvp routeRecord);

	/**
	 * Sets the set of Route-Record AVPs, with all the values in the given
	 * array. The AVPs will be added to message in the order in which they
	 * appear in the array.
	 * 
	 * Note: the array must not be altered by the caller following this call,
	 * and getRouteRecords() is not guaranteed to return the same array
	 * instance, e.g. an "==" check would fail.
	 * 
	 * @throws IllegalStateException
	 *             if setRouteRecord or setRouteRecords has already been called
	 */
	void setRouteRecords(DiameterIdentityAvp[] routeRecords);

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
	 * Returns the set of Supported-Features AVPs. The returned array contains
	 * the AVPs in the order they appear in the message. A return value of null
	 * implies that no Supported-Features AVPs have been set. The elements in
	 * the given array are SupportedFeatures objects.
	 */
	SupportedFeaturesAvp getSupportedFeatures();

}
