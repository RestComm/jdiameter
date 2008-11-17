package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;
/**
 * <pre><b>8.14. Credit-Control-Failure-Handling AVP</b>
 *
 *
 *   The Credit-Control-Failure-Handling AVP (AVP Code 427) is of type
 *   Enumerated.  The credit-control client uses information in this AVP
 *   to decide what to do if sending credit-control messages to the
 *   credit-control server has been, for instance, temporarily prevented
 *   due to a network problem.  Depending on the service logic, the
 *   credit-control server can order the client to terminate the service
 *   immediately when there is a reason to believe that the service cannot
 *   be charged, or to try failover to an alternative server, if possible.
 *   Then the server could either terminate or grant the service, should
 *   the alternative connection also fail.
 *
 *   <b>TERMINATE                       0</b>
 *      When the Credit-Control-Failure-Handling AVP is set to TERMINATE,
 *      the service MUST only be granted for as long as there is a
 *      connection to the credit-control server.  If the credit-control
 *      client does not receive any Credit-Control-Answer message within
 *      the Tx timer (as defined in section 13), the credit-control
 *      request is regarded as failed, and the end user's service session
 *      is terminated.
 *
 *      This is the default behavior if the AVP isn't included in the
 *      reply from the authorization or credit-control server.
 *
 *   <b>CONTINUE                       1</b>
 *      When the Credit-Control-Failure-Handling AVP is set to CONTINUE,
 *      the credit-control client SHOULD re-send the request to an
 *      alternative server in the case of transport or temporary failures,
 *      provided that a failover procedure is supported in the credit-
 *      control server and the credit-control client, and that an
 *      alternative server is available.  Otherwise, the service SHOULD be
 *      granted, even if credit-control messages can't be delivered.
 *
 *   <b>RETRY_AND_TERMINATE            2</b>
 *      When the Credit-Control-Failure-Handling AVP is set to
 *      RETRY_AND_TERMINATE, the credit-control client SHOULD re-send the
 *      request to an alternative server in the case of transport or
 *      temporary failures, provided that a failover procedure is
 *      supported in the credit-control server and the credit-control
 *      client, and that an alternative server is available.  Otherwise,
 *      the service SHOULD not be granted when the credit-control messages
 *      can't be delivered.
 *      <pre>
 * @author baranowb
 *
 */
public enum CreditControlFailureHandlingType implements Enumerated {
	TERMINATE(0), CONTINUE(1),RETRY_AND_TERMINATE(2);

	private int value = -1;

	private CreditControlFailureHandlingType(int val) {
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

	public static CreditControlFailureHandlingType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return TERMINATE;
		case 1:
			return CONTINUE;
		case 2:
			return RETRY_AND_TERMINATE;
		default:
			throw new IllegalArgumentException();

		}

	}
}
