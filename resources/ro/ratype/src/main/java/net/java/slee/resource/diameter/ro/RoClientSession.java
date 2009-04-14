package net.java.slee.resource.diameter.ro;

import java.io.IOException;

import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;

/**
 * 
 * An RoClientSessionActivity represents a charging control session for Credit Control clients.
 * 
 * All requests for the session must be sent via the same RoClientSessionActivity.
 * 
 * All responses related to the session will be received as events fired on the same RoClientSessionActivity.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoClientSession extends RoSession, CreditControlClientSession {

  /**
   * Send an event Credit-Control-Request.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException if an error occured sending the request to the peer
   */
  public void sendEventCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send an initial Credit-Control-Request.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException if an error occured sending the request to the peer
   */
  public void sendInitialCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send an update (intermediate) Credit-Control-Request.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException if an error occured sending the request to the peer
   */
  public void sendUpdateCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send a termination Credit-Control-Request.
   * 
   * @param ccr the CreditControlRequest to send
   * @throws IOException if an error occured sending the request to the peer
   */
  public void sendTerminationCreditControlRequest(CreditControlRequest ccr) throws IOException;

}
