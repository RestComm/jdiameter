package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the ClassIdentifier enumerated type. Author: Open
 * Cloud See Also:Serialized Form
 */
public class ClassIdentifier implements Enumerated, java.io.Serializable {
	public static final int _ADVERTISEMENT = 1;

	public static final int _AUTO = 3;

	public static final int _INFORMATIONAL = 2;

	public static final int _PERSONAL = 0;

	public static final net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier ADVERTISEMENT = new ClassIdentifier(_ADVERTISEMENT);

	public static final net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier AUTO = new ClassIdentifier(_AUTO);

	public static final net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier INFORMATIONAL = new ClassIdentifier(_INFORMATIONAL);

	public static final net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier PERSONAL = new ClassIdentifier(_PERSONAL);

	private ClassIdentifier(int v)
	{
		value=v;
	}
	
	/**
	 * Return the value of this instance of this enumerated type.
	 */
	public static ClassIdentifier fromInt(int type) {
		switch (type) {
		case _ADVERTISEMENT:
			return ADVERTISEMENT;

		case _AUTO:
			return AUTO;

		case _INFORMATIONAL:
			return INFORMATIONAL;

		case _PERSONAL:
			return PERSONAL;
		default:
			throw new IllegalArgumentException(
					"Invalid DisconnectCause value: " + type);
		}
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		switch (value) {
		case _ADVERTISEMENT:
			return "ADVERTISEMENT";

		case _AUTO:
			return "AUTO";

		case _INFORMATIONAL:
			return "INFORMATIONAL";

		case _PERSONAL:
			return "PERSONAL";
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
