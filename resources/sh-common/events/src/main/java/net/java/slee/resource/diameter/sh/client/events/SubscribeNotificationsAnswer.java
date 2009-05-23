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

import java.util.Date;

import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;

/**
 * Defines an interface representing the Subscribe-Notifications-Answer command.
 * 
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0)
 * specification:
 * 
 * <pre>
 * 6.1.6        Subscribe-Notifications-Answer (SNA) Command
 * 
 * The Subscribe-Notifications-Answer command, indicated by the Command-Code field
 * set to 308 and the 'R' bit cleared in the Command Flags field, is sent by a
 * server in response to the Subscribe-Notifications-Request command. The
 * Result-Code or Experimental-Result AVP may contain one of the values defined in
 * section 6.2 or in 3GPP TS 29.229 [6].
 * 
 * Message Format
 * &lt; Subscribe-Notifications-Answer &gt; ::=      &lt; Diameter Header: 308, PXY, 16777217 &gt;
 *                                             &lt; Session-Id &gt;
 *                                             { Vendor-Specific-Application-Id }
 *                                             { Auth-Session-State }
 *                                             [ Result-Code ]
 *                                             [ Experimental-Result ]
 *                                             { Origin-Host }
 *                                             { Origin-Realm }
 *                                             *[ Supported-Features ]
 *                                             [ Expiry-Time ]
 *                                             *[ AVP ]
 *                                             *[ Failed-AVP ]
 *                                             *[ Proxy-Info ]
 *                                             *[ Route-Record ]
 * </pre>
 */
public interface SubscribeNotificationsAnswer extends DiameterShMessage {

	static final int commandCode = 308;

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
	 * Returns true if the Expiry-Time AVP is present in the message.
	 */
	boolean hasExpiryTime();

	/**
	 * Returns the value of the Expiry-Time AVP, of type Time.
	 * 
	 * @return the value of the Expiry-Time AVP or null if it has not been set
	 *         on this message
	 */
	Date getExpiryTime();

	/**
	 * Sets the value of the Expiry-Time AVP, of type Time.
	 * 
	 * @throws IllegalStateException
	 *             if setExpiryTime has already been called
	 */
	void setExpiryTime(Date expiryTime);

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
