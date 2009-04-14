package net.java.slee.resource.diameter.ro;

import java.io.IOException;

import net.java.slee.resource.diameter.cca.CreditControlServerSession;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;

/**
 * 
 * An RoServerSessionActivity represents a charging control session for Credit Control servers.
 * 
 * A single RoServerSessionActivity will be created for the Diameter session. All requests received for the session will be fired as events on the same RoServerSessionActivity.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoServerSession extends RoSession, CreditControlServerSession {

  /**
   * Create a Ro-specific Credit-Control-Answer message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new CreditControlAnswer
   */
  public CreditControlAnswer createRoCreditControlAnswer();

  /**
   * Sends a Credit-Control-Answer message to the peer.
   * 
   * @param cca the CreditControlAnswer to send
   * @throws IOException if an error occured sending the request to the peer
   */
  public void sendCreditControlAnswer(CreditControlAnswer cca) throws IOException;
  
}
