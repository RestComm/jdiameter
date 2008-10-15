package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 *<pre> <b>8.35. Final-Unit-Action AVP</b>
 *
 *
 *   The Final-Unit-Action AVP (AVP Code 449) is of type Enumerated and
 *   indicates to the credit-control client the action to be taken when
 *   the user's account cannot cover the service cost.
 *
 *   The Final-Unit-Action can be one of the following:
 *
 *   <b>TERMINATE                       0</b>
 *      The credit-control client MUST terminate the service session.
 *      This is the default handling, applicable whenever the credit-
 *      control client receives an unsupported Final-Unit-Action value,
 *      and it MUST be supported by all the Diameter credit-control client
 *      implementations conforming to this specification.
 *
 *   <b>REDIRECT                        1</b>
 *      The service element MUST redirect the user to the address
 *      specified in the Redirect-Server-Address AVP.  The redirect action
 *      is defined in section 5.6.2.
 *
 *   <b>RESTRICT_ACCESS                 2</b>
 *      The access device MUST restrict the user access according to the
 *      IP packet filters defined in the Restriction-Filter-Rule AVP or
 *      according to the IP packet filters identified by the Filter-Id
 *      AVP.  All the packets not matching the filters MUST be dropped
 *      (see section 5.6.3).
 *      </pre>
 * @author baranowb
 *
 */
public enum FinalUnitActionType implements Enumerated {
	TERMINATE(0),REDIRECT(1),RESTRICT_ACCESS(2);

	
	
	private int value=-1;
	
	
	private FinalUnitActionType(int value)
	{
		this.value=value;
	}
	
	
	
	
	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: "
					+ value);
		}
	}

	public FinalUnitActionType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return TERMINATE;
		case 1:
			return REDIRECT;
		case 2:
			return RESTRICT_ACCESS;
		
		

		default:
			throw new IllegalArgumentException();

		}

	}
	
	
	

	public int getValue() {
		
		return this.value;
	}

}
