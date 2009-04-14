package net.java.slee.resource.diameter.ro;

/**
 * 
 * RoSession.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoSession {

  /**
   * Return a message factory to be used to create concrete implementations of credit control messages.
   * 
   * @return
   */
  public RoMessageFactory getRoMessageFactory();

  /**
   * Returns the session ID of the credit control session, which uniquely identifies the session.
   * 
   * @return the session ID
   */
  public String getSessionId();
}
