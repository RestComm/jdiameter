package net.java.slee.resource.diameter.rf;

import java.io.IOException;

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

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
   * @return a DiameterActivity 
   */
  public RfMessageFactory getRfMessageFactory();

  /**
   * Create a new activity to send and receive Diameter messages.
   * 
   * @return
   * @throws CreateActivityException if the RA could not create the activity for any reason
   */
  public RfClientSession createRfClientSessionActivity() throws CreateActivityException;
  
  /**
   * Create a new activity to send and receive Diameter messages.
   * 
   * @param destinationHost a destination host to automatically put in all messages
   * @param destinationRealm a destination realm to automatically put in all messages 
   * @return
   * @throws CreateActivityException if the RA could not create the activity for any reason
   */
  public RfClientSession createRfClientSessionActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException;

  /**
   * Send an Accounting Request.
   * 
   * @param accountingRequest the Accounting-Request message to send 
   * @return
   * @throws IllegalArgumentException if accountingRequest is missing any required AVPs
   * @throws IOException if the message could not be sent 
   */
  public AccountingAnswer sendAccountingRequest(AccountingRequest accountingRequest) throws IllegalArgumentException, IOException;

  
  /**
	 * Return the number of peers this Diameter resource adaptor is connected
	 * to.
	 * 
	 * @return connected peer count
	 */
	int getPeerCount();

	/**
	 * Returns array containing identities of connected peers FIXME: baranowb; -
	 * should it be InetAddres, Port pair?
	 * 
	 * @return
	 */
	DiameterIdentity[] getConnectedPeers();
}
