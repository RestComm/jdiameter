package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 *<pre> <b>8.40. Multiple-Services-Indicator AVP</b>
 *
 *
 *   The Multiple-Services-Indicator AVP (AVP Code 455) is of type
 *   Enumerated and indicates whether the Diameter credit-control client
 *   is capable of handling multiple services independently within a
 *   (sub-) session.  The absence of this AVP means that independent
 *   credit-control of multiple services is not supported.
 *
 *   A server not implementing the independent credit-control of multiple
 *   services MUST treat the Multiple-Services-Indicator AVP as an invalid
 *   AVP.
 *
 *   The following values are defined for the Multiple-Services-Indicator
 *   AVP:
 *
 *   MULTIPLE_SERVICES_NOT_SUPPORTED 0
 *      Client does not support independent credit-control of multiple
 *      services within a (sub-)session.
 *
 *   MULTIPLE_SERVICES_SUPPORTED     1
 *      Client supports independent credit-control of multiple services
 *      within a (sub-)session.
 *      
 *      </pre>
 * @author baranowb
 *
 */
public enum MultipleServicesIndicatorType implements Enumerated {

	MULTIPLE_SERVICES_NOT_SUPPORTED(0), MULTIPLE_SERVICES_SUPPORTED(1);

	private int value = -1;

	private MultipleServicesIndicatorType(int value) {
		this.value = value;
	}

	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: "
					+ value);
		}
	}

	public MultipleServicesIndicatorType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return MULTIPLE_SERVICES_NOT_SUPPORTED;
		case 1:
			return MULTIPLE_SERVICES_SUPPORTED;

		default:
			throw new IllegalArgumentException();

		}

	}

	public int getValue() {

		return this.value;
	}

}
