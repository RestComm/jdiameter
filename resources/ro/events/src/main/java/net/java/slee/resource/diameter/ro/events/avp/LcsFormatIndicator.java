package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the LcsFormatIndicator enumerated type. Author: Open
 * Cloud See Also:Serialized Form
 */
public class LcsFormatIndicator implements Enumerated, java.io.Serializable {
	public static final int _EMAIL_ADDRESS = 1;

	public static final int _LOGICAL_NAME = 0;

	public static final int _MSISDN = 2;

	public static final int _SIP_URL = 4;

	public static final int _URL = 3;

	public static final net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator EMAIL_ADDRESS = new LcsFormatIndicator(
			_EMAIL_ADDRESS);

	public static final net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator LOGICAL_NAME = new LcsFormatIndicator(
			_LOGICAL_NAME);

	public static final net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator MSISDN = new LcsFormatIndicator(
			_MSISDN);

	public static final net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator SIP_URL = new LcsFormatIndicator(
			_SIP_URL);

	public static final net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator URL = new LcsFormatIndicator(
			_URL);

	private LcsFormatIndicator(int v) {
		value = v;
	}

	/**
	 * Return the value of this instance of this enumerated type.
	 */
	public static LcsFormatIndicator fromInt(int type) {
		switch (type) {
		case _EMAIL_ADDRESS:
			return EMAIL_ADDRESS;

		case _LOGICAL_NAME:
			return LOGICAL_NAME;

		case _MSISDN:
			return MSISDN;

		case _SIP_URL:
			return SIP_URL;

		case _URL:
			return URL;
		default:
			throw new IllegalArgumentException(
					"Invalid LcsFormatIndicator value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _EMAIL_ADDRESS:
			return "EMAIL_ADDRESS";

		case _LOGICAL_NAME:
			return "LOGICAL_NAME";

		case _MSISDN:
			return "MSISDN";

		case _SIP_URL:
			return "SIP_URL";

		case _URL:
			return "URL";
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
