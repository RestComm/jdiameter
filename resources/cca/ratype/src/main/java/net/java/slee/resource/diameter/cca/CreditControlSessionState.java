package net.java.slee.resource.diameter.cca;

import java.io.StreamCorruptedException;

public enum CreditControlSessionState {

	IDLE(0), PENDING_EVENT(1), PENDING_INITIAL(2), PENDING_UPDATE(3), PENDING_TERMINATION(
			4), PENDING_BUFFERED(5), OPEN(6), TERMINATED(7);


	private int value = -1;

	private CreditControlSessionState(int val) {
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

	public CreditControlSessionState fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 0:
			return IDLE;
		case 1:
			return PENDING_EVENT;
		case 2:
			return PENDING_INITIAL;
		case 3:
			return PENDING_UPDATE;
		case 4:
			return PENDING_TERMINATION;
		case 5:
		  return PENDING_BUFFERED;
		case 6:
			return OPEN;
		case 7:
			return TERMINATED;

		default:
			throw new IllegalArgumentException();

		}

	}
}
