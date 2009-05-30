package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;

/**
 * Defines an interface representing the Session-Termination-Request command.
 * 
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * 
 * <pre>
 * 8.4.1.  Session-Termination-Request
 * 
 *    The Session-Termination-Request (STR), indicated by the Command-Code
 *    set to 275 and the Command Flags' 'R' bit set, is sent by the access
 *    device to inform the Diameter Server that an authenticated and/or
 *    authorized session is being terminated.
 * 
 *    Message Format
 * 
 *       &lt;Session-Termination-Request&gt; ::= &lt; Diameter Header: 275, REQ, PXY &gt;
 *                 &lt; Session-Id &gt;
 *                 { Origin-Host }
 *                 { Origin-Realm }
 *                 { Destination-Realm }
 *                 { Auth-Application-Id }
 *                 { Termination-Cause }
 *                 [ User-Name ]
 *                 [ Destination-Host ]
 *               * [ Class ]
 *                 [ Origin-State-Id ]
 *               * [ Proxy-Info ]
 *               * [ Route-Record ]
 *               * [ AVP ]
 * </pre>
 */
public interface SessionTerminationRequest extends DiameterMessage {

	static final int commandCode = 275;

	/**
	 * Returns true if the Destination-Realm AVP is present in the message.
	 */
	boolean hasDestinationRealm();

	/**
	 * Returns true if the Auth-Application-Id AVP is present in the message.
	 */
	boolean hasAuthApplicationId();

	/**
	 * Returns the value of the Auth-Application-Id AVP, of type Unsigned32. Use
	 * {@link #hasAuthApplicationId()} to check the existence of this AVP.
	 * 
	 * @return the value of the Auth-Application-Id AVP
	 * @throws IllegalStateException
	 *             if the Auth-Application-Id AVP has not been set on this
	 *             message
	 */
	long getAuthApplicationId();

	/**
	 * Sets the value of the Auth-Application-Id AVP, of type Unsigned32.
	 * 
	 * @throws IllegalStateException
	 *             if setAuthApplicationId has already been called
	 */
	void setAuthApplicationId(long authApplicationId);

	/**
	 * Returns true if the Termination-Cause AVP is present in the message.
	 */
	boolean hasTerminationCause();

	/**
	 * Returns the value of the Termination-Cause AVP, of type Enumerated.
	 * 
	 * @return the value of the Termination-Cause AVP or null if it has not been
	 *         set on this message
	 */
	TerminationCauseType getTerminationCause();

	/**
	 * Sets the value of the Termination-Cause AVP, of type Enumerated.
	 * 
	 * @throws IllegalStateException
	 *             if setTerminationCause has already been called
	 */
	void setTerminationCause(TerminationCauseType terminationCause);

	/**
	 * Returns true if the Destination-Host AVP is present in the message.
	 */
	boolean hasDestinationHost();

	/**
	 * Returns the value of the Destination-Host AVP, of type DiameterIdentity.
	 * 
	 * @return the value of the Destination-Host AVP or null if it has not been
	 *         set on this message
	 */
	DiameterIdentity getDestinationHost();

	/**
	 * Sets the value of the Destination-Host AVP, of type DiameterIdentity.
	 * 
	 * @throws IllegalStateException
	 *             if setDestinationHost has already been called
	 */
	void setDestinationHost(DiameterIdentity destinationHost);

	/**
	 * Returns the set of Class AVPs. The returned array contains the AVPs in
	 * the order they appear in the message. A return value of null implies that
	 * no Class AVPs have been set. The elements in the given array are byte[]
	 * objects.
	 */
	byte[][] getClassAvps();

	/**
	 * Sets a single Class AVP in the message, of type OctetString.
	 * 
	 * @throws IllegalStateException
	 *             if setClassAvp or setClassAvps has already been called
	 */
	void setClassAvp(byte[] classAvp);

	/**
	 * Sets the set of Class AVPs, with all the values in the given array. The
	 * AVPs will be added to message in the order in which they appear in the
	 * array.
	 * 
	 * Note: the array must not be altered by the caller following this call,
	 * and getClassAvps() is not guaranteed to return the same array instance,
	 * e.g. an "==" check would fail.
	 * 
	 * @throws IllegalStateException
	 *             if setClassAvp or setClassAvps has already been called
	 */
	void setClassAvps(byte[][] classAvps);

	/**
	 * Returns true if the Origin-State-Id AVP is present in the message.
	 */
	boolean hasOriginStateId();

	/**
	 * Returns the value of the Origin-State-Id AVP, of type Unsigned32. Use
	 * {@link #hasOriginStateId()} to check the existence of this AVP.
	 * 
	 * @return the value of the Origin-State-Id AVP
	 * @throws IllegalStateException
	 *             if the Origin-State-Id AVP has not been set on this message
	 */
	long getOriginStateId();

	/**
	 * Sets the value of the Origin-State-Id AVP, of type Unsigned32.
	 * 
	 * @throws IllegalStateException
	 *             if setOriginStateId has already been called
	 */
	void setOriginStateId(long originStateId);

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
   * Returns true if the Proxy-Info AVP is present in the message.
   */
  boolean hasProxyInfo();

	/**
	 * Returns the set of Route-Record AVPs. The returned array contains the
	 * AVPs in the order they appear in the message. A return value of null
	 * implies that no Route-Record AVPs have been set. The elements in the
	 * given array are DiameterIdentity objects.
	 */
	DiameterIdentity[] getRouteRecords();

	/**
	 * Sets a single Route-Record AVP in the message, of type DiameterIdentity.
	 * 
	 * @throws IllegalStateException
	 *             if setRouteRecord or setRouteRecords has already been called
	 */
	void setRouteRecord(DiameterIdentity routeRecord);

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
	void setRouteRecords(DiameterIdentity[] routeRecords);

}
