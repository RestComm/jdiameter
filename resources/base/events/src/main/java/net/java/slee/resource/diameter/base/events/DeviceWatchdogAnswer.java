package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.FailedAvp;

/**
 * Defines an interface representing the Device-Watchdog-Answer command.
 * 
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * 
 * <pre>
 * 5.5.2.  Device-Watchdog-Answer
 * 
 *    The Device-Watchdog-Answer (DWA), indicated by the Command-Code set
 *    to 280 and the Command Flags' 'R' bit cleared, is sent as a response
 *    to the Device-Watchdog-Request message.
 * 
 *    Message Format
 * 
 *       &lt;Device-Watchdog-Answer&gt;  ::= &lt; Diameter Header: 280 &gt;
 *                  { Result-Code }
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  [ Error-Message ]
 *                * [ Failed-AVP ]
 *                  [ Origin-State-Id ]
 * </pre>
 */
public interface DeviceWatchdogAnswer extends DiameterMessage {

	static final int commandCode = 280;

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

}
