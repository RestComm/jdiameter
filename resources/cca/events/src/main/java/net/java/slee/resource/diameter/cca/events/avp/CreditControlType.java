package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 *<pre> <b>8.13. Credit-Control AVP</b>
 *
 *
 *   The Credit-Control AVP (AVP Code 426) is of type Enumerated and MUST
 *   be included in AA requests when the service element has credit-
 *   control capabilities.
 *
 *   <b>CREDIT_AUTHORIZATION            0</b>
 *      If the home Diameter AAA server determines that the user has
 *      prepaid subscription, this value indicates that the credit-control
 *      server MUST be contacted to perform the first interrogation.  The
 *      value of the Credit-Control AVP MUST always be set to 0 in an AA
 *      request sent to perform the first interrogation and to initiate a
 *      new credit-control session.
 *
 *   <b>RE_AUTHORIZATION                1</b>
 *      This value indicates to the Diameter AAA server that a credit-
 *      control session is ongoing for the subscriber and that the
 *      credit-control server MUST not be contacted.  The Credit-Control
 *      AVP set to the value of 1 is to be used only when the first
 *      interrogation has been successfully performed and the credit-
 *      control session is ongoing (i.e., re-authorization triggered by
 *      Authorization-Lifetime).  This value MUST NOT be used in an AA
 *      request sent to perform the first interrogation.
 *
 *	<pre>
 * @author baranowb
 *
 */
public enum CreditControlType implements Enumerated {
	CREDIT_AUTHORIZATION(0), RE_AUTHORIZATION(1);

	private int value = -1;

	private CreditControlType(int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}

	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: "
					+ value);
		}
	}

	public CreditControlType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return CREDIT_AUTHORIZATION;
		case 1:
			return RE_AUTHORIZATION;

		default:
			throw new IllegalArgumentException();

		}

	}
}
