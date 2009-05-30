package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

/**
 * Defines an interface representing the Accounting-Request command.
 * 
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * 
 * <pre>
 * 9.7.1.  Accounting-Request
 * 
 *    The Accounting-Request (ACR) command, indicated by the Command-Code
 *    field set to 271 and the Command Flags' 'R' bit set, is sent by a
 *    Diameter node, acting as a client, in order to exchange accounting
 *    information with a peer.
 * 
 *    One of Acct-Application-Id and Vendor-Specific-Application-Id AVPs
 *    MUST be present.  If the Vendor-Specific-Application-Id grouped AVP
 *    is present, it must have an Acct-Application-Id inside.
 * 
 *    The AVP listed below SHOULD include service specific accounting AVPs,
 *    as described in Section 9.3.
 * 
 *    Message Format
 * 
 *       &lt;Accounting-Request&gt; ::= &lt; Diameter Header: 271, REQ, PXY &gt;
 *                 &lt; Session-Id &gt;
 *                 { Origin-Host }
 *                 { Origin-Realm }
 *                 { Destination-Realm }
 *                 { Accounting-Record-Type }
 *                 { Accounting-Record-Number }
 *                 [ Acct-Application-Id ]
 *                 [ Vendor-Specific-Application-Id ]
 *                 [ User-Name ]
 *                 [ Accounting-Sub-Session-Id ]
 *                 [ Accounting-Session-Id ]
 *                 [ Acct-Multi-Session-Id ]
 *                 [ Acct-Interim-Interval ]
 *                 [ Accounting-Realtime-Required ]
 *                 [ Origin-State-Id ]
 *                 [ Event-Timestamp ]
 *               * [ Proxy-Info ]
 *               * [ Route-Record ]
 *               * [ AVP ]
 * </pre>
 */
public interface AccountingRequest extends DiameterMessage, AccountingMessage {

	static final int commandCode = 271;

	/**
	 * Returns true if the Destination-Realm AVP is present in the message.
	 */
	boolean hasDestinationRealm();

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
