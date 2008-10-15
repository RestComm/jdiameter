package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the DeliveryReportRequested enumerated type. Author:
 * baranowb
 */
public class DeliveryReportRequested implements Enumerated,
		java.io.Serializable {
	public static final int _NO = 0;

	public static final int _YES = 1;

	public static final net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested NO = new DeliveryReportRequested(_NO);

	public static final net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested YES = new DeliveryReportRequested(_YES);

	private DeliveryReportRequested(int v) {
		value = v;
	}

	/**
	 * Return the value of this instance of this enumerated type.
	 */
	public static DeliveryReportRequested fromInt(int type) {
		switch (type) {
		case _NO:
			return NO;
		case _YES:
			return YES;

		default:
			throw new IllegalArgumentException(
					"Invalid DeliveryReportRequested value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _NO:
			return "NO";
		case _YES:
			return "YES";
		default:
			return "<Invalid Value>";
		}
	}

	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: "
					+ value);
		}
	}

	private int value = 0;

}
