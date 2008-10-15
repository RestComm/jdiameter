package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 *<pre> <b>8.15. Direct-Debiting-Failure-Handling AVP</b>
 *
 *
 *   The Direct-Debiting-Failure-Handling AVP (AVP Code 428) is of type
 *   Enumerated.  The credit-control client uses information in this AVP
 *   to decide what to do if sending credit-control messages (Requested-
 *   Action AVP set to DIRECT_DEBITING) to the credit-control server has
 *   been, for instance, temporarily prevented due to a network problem.
 *
 *   <b>TERMINATE_OR_BUFFER             0</b>
 *      When the Direct-Debiting-Failure-Handling AVP is set to
 *      TERMINATE_OR_BUFFER, the service MUST be granted for as long as
 *      there is a connection to the credit-control server.  If the
 *      credit-control client does not receive any Credit-Control-Answer
 *      message within the Tx timer (as defined in section 13) the
 *      credit-control request is regarded as failed.  The client SHOULD
 *      terminate the service if it can determine from the failed answer
 *      that units have not been debited.  Otherwise the credit-control
 *      client SHOULD grant the service, store the request in application
 *      level non-volatile storage, and try to re-send the request.  These
 *      requests MUST be marked as possible duplicates by setting the T-
 *      flag in the command header as described in [DIAMBASE] section 3.
 *
 *      This is the default behavior if the AVP isn't included in the
 *      reply from the authorization server.
 *
 *   <b>CONTINUE                          1</b>
 *      When the Direct-Debiting-Failure-Handling AVP is set to CONTINUE,
 *      the service SHOULD be granted, even if credit-control messages
 *      can't be delivered, and the request should be deleted.
 *      </pre>
 * @author baranowb
 * 
 */
public enum DirectDebitingFailureHandlingType implements Enumerated {
	TERMINATE_OR_BUFFER(0), CONTINUE(1);

	private int value = -1;

	private DirectDebitingFailureHandlingType(int val) {
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

	public DirectDebitingFailureHandlingType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return TERMINATE_OR_BUFFER;
		case 1:
			return CONTINUE;

		default:
			throw new IllegalArgumentException();

		}

	}
}
