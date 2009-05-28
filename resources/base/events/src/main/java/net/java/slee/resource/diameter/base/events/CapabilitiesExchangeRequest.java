package net.java.slee.resource.diameter.base.events;



/**
 * Defines an interface representing the Capabilities-Exchange-Request command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 5.3.1.  Capabilities-Exchange-Request
 * 
 *    The Capabilities-Exchange-Request (CER), indicated by the Command-
 *    Code set to 257 and the Command Flags' 'R' bit set, is sent to
 *    exchange local capabilities.  Upon detection of a transport failure,
 *    this message MUST NOT be sent to an alternate peer.
 * 
 *    When Diameter is run over SCTP [SCTP], which allows for connections
 *    to span multiple interfaces and multiple IP addresses, the
 *    Capabilities-Exchange-Request message MUST contain one Host-IP-
 *    Address AVP for each potential IP address that MAY be locally used
 *    when transmitting Diameter messages.
 * 
 *    Message Format
 * 
 *       &lt;Capabilities-Exchange-Request&gt; ::= &lt; Diameter Header: 257, REQ &gt;
 *                 { Origin-Host }
 *                 { Origin-Realm }
 *              1* { Host-IP-Address }
 *                 { Vendor-Id }
 *                 { Product-Name }
 *                 [ Origin-State-Id ]
 *               * [ Supported-Vendor-Id ]
 *               * [ Auth-Application-Id ]
 *               * [ Inband-Security-Id ]
 *               * [ Acct-Application-Id ]
 *               * [ Vendor-Specific-Application-Id ]
 *                 [ Firmware-Revision ]
 *               * [ AVP ]
 * </pre>
 */
public interface CapabilitiesExchangeRequest extends CapabilitiesExchangeMessage {

	static final int commandCode = 257;


}
