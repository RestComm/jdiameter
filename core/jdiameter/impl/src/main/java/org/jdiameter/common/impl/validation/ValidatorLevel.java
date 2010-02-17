package org.jdiameter.common.impl.validation;

public class ValidatorLevel {

	public static final String _OFF_LEVEL = "OFF";
	public static final String _MESSAGE_LEVEL = "MESSAGE";
	public static final String _ALL_LEVEL = "ALL";
	public static final ValidatorLevel _OFF = new ValidatorLevel(_OFF_LEVEL);
	public static final ValidatorLevel _MESSAGE = new ValidatorLevel(_MESSAGE_LEVEL);
	public static final ValidatorLevel _ALL = new ValidatorLevel(_ALL_LEVEL);

	private String name = null;

	private ValidatorLevel(String name) {
		super();
		this.name = name;
	}

	public static ValidatorLevel fromString(String s) throws IllegalArgumentException {

		if (s.toUpperCase().equals(_OFF_LEVEL))
			return _OFF;
		if (s.toUpperCase().equals(_MESSAGE_LEVEL))
			return _MESSAGE;
		if (s.toUpperCase().equals(_ALL_LEVEL))
			return _ALL;
		throw new IllegalArgumentException("No level for such value: " + s);
	}

	@Override
	public String toString() {
		return "ValidatorLevel [name=" + name + "]";
	}

}
