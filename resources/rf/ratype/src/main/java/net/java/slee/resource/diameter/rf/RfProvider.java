package net.java.slee.resource.diameter.rf;

import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

/**
 * 
 * The SBB interface for the Diameter Rf Resource Adaptor.
 * 
 * This API can be used in either an asynchronous or synchronous manner.
 * 
 * To send messages asynchronously, create a RfClientSessionActivity using one of the createRfClientSessionActivity() methods.
 * 
 * To send messages synchronously, use the accountingRequest(AccountingRequest) method.
 * 
 * The Accounting-Request messages must be created using the RfMessageFactory returned from getRfMessageFactory().
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RfProvider {

  /**
   * Return a message factory to be used to create concrete implementations of accounting messages and AVPs.
   * 
   * @return
   */
  public RfMessageFactory getRfMessageFactory();

  /**
   * Create a new activity to send and receive Diameter messages.
   * 
   * @return
   */
  public RfClientSession createRfClientSessionActivity();
  
  /**
   * Create a new activity to send and receive Diameter messages.
   * 
   * @param destinationHost
   * @param destinationRealm
   * @return
   */
  public RfClientSession createRfClientSessionActivity(DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm);

  /**
   * Send an Accounting Request.
   * 
   * @param accountingRequest
   * @return
   */
  public AccountingAnswer sendAccountingRequest(AccountingRequest accountingRequest);

}
