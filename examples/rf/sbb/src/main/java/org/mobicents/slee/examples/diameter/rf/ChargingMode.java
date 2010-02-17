package org.mobicents.slee.examples.diameter.rf;



public enum ChargingMode {
	Event, Session;

	public ChargingMode fromString(String s) {
		if (s == null || s.toLowerCase().equals("event")) {
			return Event;
		} else if (s.toLowerCase().equals("session")) {
			return Session;
		} else {
			throw new IllegalArgumentException("There is no mode for: " + s);
		}
	}
}
