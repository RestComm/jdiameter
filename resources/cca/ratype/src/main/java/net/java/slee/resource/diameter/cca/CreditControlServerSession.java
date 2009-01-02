package net.java.slee.resource.diameter.cca;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;

/**
 * 
 * A CreditControlServerSession represents a charging control session for Credit Control servers.
 *
 * <br>Super project:  mobicents
 * <br>10:59:47 AM Dec 30, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
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
   * @throws IOException
   */
  void sendReAuthRequest(ReAuthRequest rar) throws IOException;

}
