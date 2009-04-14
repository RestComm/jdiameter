package net.java.slee.resource.diameter.rf;

/**
 * 
 * RfSession.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RfSession {

  /**
   * Return a message factory to be used to create concrete implementations of credit control messages.
   * 
   * @return
   */
  public RfMessageFactory getRfMessageFactory();

  /**
	 * Returns the session ID of the credit control session, which uniquely
	 * identifies the session.
	 * 
	 * @return 
	 */
	public String getSessionId();
	
}
