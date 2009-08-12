package net.java.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;

/**
 * 
 * Factory to support the creation of Diameter Credit Control messages.
 *
 * <br>Super project:  mobicents
 * <br>10:57:36 AM Dec 30, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface CreditControlMessageFactory {

  public static final int _CCA_VENDOR = 0;
  public static final int _CCA_AUTH_APP_ID = 4;

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


  /**
   * Create a CreditControlAnswer instance, populating it with the internal
   * AVPs not known or needed by the application. 
   * 
   * @param request - request that has come, can be null, in which case it is ignored. Some AVP values can be taken from request, but its up to impl.
   * @return a new CreditControlRequest
   */
  CreditControlAnswer createCreditControlAnswer(CreditControlRequest request);

//  /**
//   * Create a CreditControlAnswer instance, populating it with the internal
//   * AVPs not known or needed by the application. Use the session ID provided
//   * to find the Diameter session. This should be used when the requests are
//   * being made synchronously and there's no CreditControlClientSessionActivity
//   * available.
//   * 
//   * @param sessionId the Session-Id AVP returned in the Answer to a previous sync call
//   * @return a new CreditControlRequest
//   * @throws IllegalArgumentException if sessionId is not a SessionID AVP
//   */
//  CreditControlAnswer createCreditControlAnswer(String sessionId) throws IllegalArgumentException;



}
