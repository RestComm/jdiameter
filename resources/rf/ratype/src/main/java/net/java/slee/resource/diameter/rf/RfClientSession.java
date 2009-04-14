package net.java.slee.resource.diameter.rf;

import java.io.IOException;

import net.java.slee.resource.diameter.base.AccountingClientSessionActivity;
import net.java.slee.resource.diameter.base.events.AccountingRequest;

/**
 * 
 * An RfClientSessionActivity represents an offline charging session for accounting clients.
 * 
 * All requests for the session must be sent via the same RfClientSessionActivity.
 * 
 * All responses related to the session will be received as events fired on the same RfClientSessionActivity.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RfClientSession extends RfSession, AccountingClientSessionActivity {

  /**
   * Send an Accounting Request.
   * 
   * @param accountingRequest request message to send
   * @throws IOException if the message could not be sent 
   * @throws IllegalArgumentException if accountingRequest is missing any required AVPs
   */
  public void sendAccountingRequest(AccountingRequest accountingRequest) throws IOException, IllegalArgumentException;
}
