package net.java.slee.resource.diameter.ro;

public interface RoProvider {

	public RoAvpFactory getRoAVPFactory();
	public RoMessageFactory getRoMessageFactory();
}
