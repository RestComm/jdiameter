package net.java.slee.resource.diameter.cca;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;

/**
 * A CreditControlClientSession represents a charging control session for
 * Credit Control clients.
 * 
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlClientSession extends CreditControlSession {

  /**
   * Create a Credit-Control-Request message pre-populated with the AVPs
   * appropriate for this session.
   * 
   * @return a new CreditControlRequest
   */
  CreditControlRequest createCreditControlRequest();

  /**
   * Send an event Credit-Control-Request. An event containing the answer will
   * be fired on this activity.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException
   */
  void sendCreditControlRequest(CreditControlRequest ccr) throws IOException;
  
  /**
   * Send an initial Credit-Control-Request. An event containing the answer
   * will be fired on this activity.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException
   */
  void sendInitialCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send a Re-Auth-Answer message.
   * 
   * @param rar the CreditControlRequest to send
   * @throws IOException
   */
  void sendReAuthAnswer(ReAuthAnswer rar) throws IOException;

  /**
   * Send an update (intermediate) Credit-Control-Request. An event containing
   * the answer will be fired on this activity.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException
   */
  void sendUpdateCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send a termination Credit-Control-Request. An event containing the answer
   * will be fired on this activity. The activity will end when the event is
   * fired.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException 
   */
  void sendTerminationCreditControlRequest(CreditControlRequest ccr) throws IOException;
  
}
