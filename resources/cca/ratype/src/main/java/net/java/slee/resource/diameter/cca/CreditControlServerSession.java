package net.java.slee.resource.diameter.cca;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;

/**
 * A CreditControlServerSession represents a charging control session for
 * Credit Control servers.
 * 
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlServerSession extends CreditControlSession {

  /**
   * Create a Credit-Control-Answer message pre-populated with the AVPs
   * appropriate for this session.
   * 
   * @return a new CreditControlAnswer
   */
  CreditControlAnswer createCreditControlAnswer();

  /**
   * Send a Credit-Control-Answer message to the CC client.
   * 
   * @param cca the CreditControlAnswer to send
   * @throws IOException
   */
  void sendCreditControlAnswer(CreditControlAnswer cca) throws IOException;
  
  /**
   * Send a Re-Auth-Request message to the CC client.
   * 
   * @param rar the ReAuthRequest to send
   * @throws IOException
   */
  void sendReAuthRequest(ReAuthRequest rar);
  
}
