package org.mobicents.slee.examples.diameter.rf;



public enum ExampleMode {
	Server, Client;
	public ExampleMode fromString(String s) {
		if (s == null || s.toLowerCase().equals("client")) {
			return Client;
		} else if (s.toLowerCase().equals("server")) {
			return Server;
		} else {
			throw new IllegalArgumentException("There is no mode for: " + s);
		}

	}
}
