package net.java.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;




/**
 * Defines an interface representing the Device-Watchdog-Request command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 5.5.1.  Device-Watchdog-Request
 * 
 *    The Device-Watchdog-Request (DWR), indicated by the Command-Code set
 *    to 280 and the Command Flags' 'R' bit set, is sent to a peer when no
 *    traffic has been exchanged between two peers (see Section 5.5.3).
 *    Upon detection of a transport failure, this message MUST NOT be sent
 *    to an alternate peer.
 * 
 *    Message Format
 * 
 *       &lt;Device-Watchdog-Request&gt;  ::= &lt; Diameter Header: 280, REQ &gt;
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  [ Origin-State-Id ]
 * </pre>
 */
public interface DeviceWatchdogRequest extends DiameterMessage {

	static final  int commandCode = 280;



    /**
     * Returns true if the Origin-State-Id AVP is present in the message.
     */
    boolean hasOriginStateId();

    /**
     * Returns the value of the Origin-State-Id AVP, of type Unsigned32.
     * Use {@link #hasOriginStateId()} to check the existence of this AVP.  
     * @return the value of the Origin-State-Id AVP
     * @throws IllegalStateException if the Origin-State-Id AVP has not been set on this message
     */
    long getOriginStateId();

    /**
     * Sets the value of the Origin-State-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setOriginStateId has already been called
     */
    void setOriginStateId(long originStateId);

}
