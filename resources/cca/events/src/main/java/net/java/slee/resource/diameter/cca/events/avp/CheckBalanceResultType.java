package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre><b>8.6. Check-Balance-Result AVP</b>
 *   The Check Balance Result AVP (AVP Code 422) is of type Enumerated and
 *   contains the result of the balance check.  This AVP is applicable
 *   only when the Requested-Action AVP indicates CHECK_BALANCE in the
 *   Credit-Control-Request command.
 *
 *   The following values are defined for the Check-Balance-Result AVP.
 *
 *   ENOUGH_CREDIT                   0
 *      There is enough credit in the account to cover the requested
 *      service.
 *
 *   NO_CREDIT                       1
 *      There isn't enough credit in the account to cover the requested
 *      service.</pre>
 * @author baranowb
 *
 */
public enum CheckBalanceResultType implements Enumerated {

	
	ENOUGH_CREDIT(0),NO_CREDIT(1);
	
	
	
	
	
	
	
	
	private int value = -1;

	private CheckBalanceResultType(int val) {
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

	public static CheckBalanceResultType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return ENOUGH_CREDIT;
		case 1:
			return NO_CREDIT;

		default:
			throw new IllegalArgumentException();

		}

	}
	
	
	
	
}
