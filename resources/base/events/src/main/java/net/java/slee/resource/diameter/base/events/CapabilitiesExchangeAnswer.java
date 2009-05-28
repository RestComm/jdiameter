package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.FailedAvp;

/**
 * Defines an interface representing the Capabilities-Exchange-Answer command.
 * 
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * 
 * <pre>
 * 5.3.2.  Capabilities-Exchange-Answer
 * 
 *    The Capabilities-Exchange-Answer (CEA), indicated by the Command-Code
 *    set to 257 and the Command Flags' 'R' bit cleared, is sent in
 *    response to a CER message.
 * 
 *    When Diameter is run over SCTP [SCTP], which allows connections to
 *    span multiple interfaces, hence, multiple IP addresses, the
 *    Capabilities-Exchange-Answer message MUST contain one Host-IP-Address
 *    AVP for each potential IP address that MAY be locally used when
 *    transmitting Diameter messages.
 * 
 *    Message Format
 * 
 *       &lt;Capabilities-Exchange-Answer&gt; ::= &lt; Diameter Header: 257 &gt;
 *                 { Result-Code }
 *                 { Origin-Host }
 *                 { Origin-Realm }
 *              1* { Host-IP-Address }
 *                 { Vendor-Id }
 *                 { Product-Name }
 *                 [ Origin-State-Id ]
 *                 [ Error-Message ]
 *               * [ Failed-AVP ]
 *               * [ Supported-Vendor-Id ]
 *               * [ Auth-Application-Id ]
 *               * [ Inband-Security-Id ]
 *               * [ Acct-Application-Id ]
 *               * [ Vendor-Specific-Application-Id ]
 *                 [ Firmware-Revision ]
 *               * [ AVP ]
 * </pre>
 */
public interface CapabilitiesExchangeAnswer extends DiameterMessage,CapabilitiesExchangeMessage {

	static final int commandCode = 257;

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
	 * Returns true if the Error-Message AVP is present in the message.
	 */
	boolean hasErrorMessage();

	/**
	 * Returns the value of the Error-Message AVP, of type UTF8String.
	 * 
	 * @return the value of the Error-Message AVP or null if it has not been set
	 *         on this message
	 */
	String getErrorMessage();

	/**
	 * Sets the value of the Error-Message AVP, of type UTF8String.
	 * 
	 * @throws IllegalStateException
	 *             if setErrorMessage has already been called
	 */
	void setErrorMessage(String errorMessage);

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
	 * Gets a single Failed-AVP AVP in the message, of type Grouped.
	 * 
	 */
	FailedAvp getFailedAvp();

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
	 * Returns true if Failed-AVP AVP is present
	 * @return
	 */
	boolean hasFailedAvp();
	
}
