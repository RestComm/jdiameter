package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.DisconnectCauseType;

/**
 * Defines an interface representing the Disconnect-Peer-Request command.
 * 
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * 
 * <pre>
 * 5.4.1.  Disconnect-Peer-Request
 * 
 *    The Disconnect-Peer-Request (DPR), indicated by the Command-Code set
 *    to 282 and the Command Flags' 'R' bit set, is sent to a peer to
 *    inform its intentions to shutdown the transport connection.  Upon
 *    detection of a transport failure, this message MUST NOT be sent to an
 *    alternate peer.
 * 
 *    Message Format
 * 
 *       &lt;Disconnect-Peer-Request&gt;  ::= &lt; Diameter Header: 282, REQ &gt;
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  { Disconnect-Cause }
 * </pre>
 */
public interface DisconnectPeerRequest extends DiameterMessage {

	static final int commandCode = 282;

	/**
	 * Returns true if the Disconnect-Cause AVP is present in the message.
	 */
	boolean hasDisconnectCause();

	/**
	 * Returns the value of the Disconnect-Cause AVP, of type Enumerated.
	 * 
	 * @return the value of the Disconnect-Cause AVP or null if it has not been
	 *         set on this message
	 */
	DisconnectCauseType getDisconnectCause();

	/**
	 * Sets the value of the Disconnect-Cause AVP, of type Enumerated.
	 * 
	 * @throws IllegalStateException
	 *             if setDisconnectCause has already been called
	 */
	void setDisconnectCause(DisconnectCauseType disconnectCause);

}
