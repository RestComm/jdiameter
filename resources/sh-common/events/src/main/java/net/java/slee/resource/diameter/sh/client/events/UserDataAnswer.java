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

import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

/**
 * Defines an interface representing the User-Data-Answer command.
 * 
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0)
 * specification:
 * 
 * <pre>
 * 6.1.2        User-Data-Answer (UDA) Command
 * 
 * The User-Data-Answer (UDA) command, indicated by the Command-Code field set 
 * to 306 and the 'R' bit cleared in the Command Flags field, is sent by a server in
 * response to the User-Data-Request command. The Experimental-Result AVP may
 * contain one of the values defined in section 6.2 or in 3GPP TS 29.229 [6].
 * 
 * Message Format
 * &lt; User-Data-Answer &gt; ::=        &lt; Diameter Header: 306, PXY, 16777217 &gt;
 *                                 &lt; Session-Id &gt;
 *                                 { Vendor-Specific-Application-Id }
 *                                 [ Result-Code ]
 *                                 [ Experimental-Result ]
 *                                 { Auth-Session-State }
 *                                 { Origin-Host }
 *                                 { Origin-Realm }
 *                                 *[ Supported-Features ]
 *                                 [ User-Data ]
 *                                 *[ AVP ]
 *                                 *[ Failed-AVP ]
 *                                 *[ Proxy-Info ]
 *                                 *[ Route-Record ]
 * </pre>
 */
public interface UserDataAnswer extends DiameterShMessage {

	static final int commandCode = 306;

	/**
	 * Returns true if the Result-Code AVP is present in the message.
	 */
	boolean hasResultCode();

	/**
	 * Returns the value of the Result-Code AVP, of type Unsigned32. Use
	 * {@link #hasResultCode()} to check the existence of this AVP.
	 * 
	 * @return the value of the Result-Code AVP
	 * @throws IllegalStateException
	 *             if the Result-Code AVP has not been set on this message
	 */
	long getResultCode();

	/**
	 * Sets the value of the Result-Code AVP, of type Unsigned32.
	 * 
	 * @throws IllegalStateException
	 *             if setResultCode has already been called
	 */
	void setResultCode(long resultCode);

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
	 * Returns the value of the User-Data AVP, of type UserData.
	 * 
	 * @return the value of the User-Data AVP or null if it has not been set on
	 *         this message
	 */
	String getUserData();

	/**
	 * Sets the value of the User-Data AVP, of type UserData.
	 * 
	 * @throws IllegalStateException
	 *             if setUserData has already been called
	 */
	void setUserData(String userData);

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

}
