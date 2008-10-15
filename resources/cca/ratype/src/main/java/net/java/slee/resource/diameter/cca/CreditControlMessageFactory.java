package net.java.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;

/**
 * Factory to support the creation of Diameter Credit Control messages.
 * 
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlMessageFactory {

  /**
   * Get the Diameter Base protocol message factory.
   * 
   * @return
   */
  DiameterMessageFactory getBaseMessageFactory();

  /**
   * Create a CreditControlRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new CreditControlRequest
   */
  CreditControlRequest createCreditControlRequest();

  /**
   * Create a CreditControlRequest instance, populating it with the internal
   * AVPs not known or needed by the application. Use the session ID provided
   * to find the Diameter session. This should be used when the requests are
   * being made synchronously and there's no CreditControlClientSessionActivity
   * available.
   * 
   * @param sessionId the Session-Id AVP returned in the Answer to a previous sync call
   * @return a new CreditControlRequest
   * @throws IllegalArgumentException if sessionId is not a SessionID AVP
   */
  CreditControlRequest createCreditControlRequest(String sessionId) throws IllegalArgumentException;
  
}
