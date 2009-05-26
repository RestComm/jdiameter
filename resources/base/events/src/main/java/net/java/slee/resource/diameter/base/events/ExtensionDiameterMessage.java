package net.java.slee.resource.diameter.base.events;



import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;




/**
 * Defines an interface representing the Extension-Diameter-Message command.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 9.7.0.  Extension-Diameter-Message
 * 
 *     An implementation of DiameterMessage for extension messages--those not defined by the
 *     Diameter RA being used.
 * 
 *     It follows the same pattern as the standard message types, but with the DiameterCommand supplied
 *     by the user.
 * 
 *     The AVPs are exposed as the set of 'extension AVP's', the same way as exposed for messages
 *     which define a "* [ AVP ]" line in the BNF definition of the message.
 * 
 *     Message Format
 * 
 *       &lt;Extension-Diameter-Message&gt; ::= &lt; Diameter Header: 0, PXY &gt;
 *                  &lt; Session-Id &gt;
 *                  { Origin-Host }
 *                  { Origin-Realm }
 *                  { Destination-Host }
 *                  { Destination-Realm }
 *                * [ AVP ]
 * </pre>
 */
public interface ExtensionDiameterMessage extends DiameterMessage {

	//FIXME: baranowb - get code
    int commandCode = -2;

    /**
     * Returns true if the Destination-Realm AVP is present in the message.
     */
    boolean hasDestinationRealm();
    /**
     * Returns true if the Destination-Host AVP is present in the message.
     */
    boolean hasDestinationHost();
}
