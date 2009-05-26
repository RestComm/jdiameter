package net.java.slee.resource.diameter.ro;

import java.io.IOException;

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;

/**
 * 
 * The SBB interface for the Diameter Ro Resource Adaptor.
 * 
 * This API can be used in either an asynchronous or synchronous manner.
 * 
 * To send messages asynchronously, create a RoClientSessionActivity using one of the createRoClientSessionActivity() methods.
 * 
 * To send messages synchronously, use the following methods:
 * <ul>eventCreditControlRequest(CreditControlRequest)</ul>
 * <ul>initialCreditControlRequest(CreditControlRequest)</ul>
 * <ul>updateCreditControlRequest(CreditControlRequest)</ul>
 * <ul>terminationCreditControlRequest(CreditControlRequest)</ul>
 * 
 * The Credit-Control-Request messages must be created using the RoMessageFactory returned from getRoMessageFactory().
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoProvider {

  /**
   * Return a message factory to be used to create concrete implementations of credit control messages.
   * 
   * @return
   */
  public RoMessageFactory getRoMessageFactory();

  /**
   * Return a avp factory to be used to create concrete implementations of credit control AVPs.
   * 
   * @return
   */
  public RoAvpFactory getRoAvpFactory();

  /**
   * Create a new activity to send and receive Diameter messages.
   * 
   * @return a DiameterActivity
   * @throws CreateActivityException if the RA could not create the activity for any reason
   */
  public RoClientSession createRoClientSessionActivity() throws CreateActivityException;

  /**
   * Create a new activity to send and receive Diameter messages.
   * 
   * @param destinationHost a destination host to automatically put in all messages
   * @param destinationRealm a destination realm to automatically put in all messages 
   * @return a DiameterActivity 
   * @throws CreateActivityException if the RA could not create the activity for any reason
   */
  public RoClientSession createRoClientSessionActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException;

  /**
   * Send a Credit-Control-Request message to the appropriate peers, and block until the response is received then return it.
   * 
   * @param ccr the CreditControlRequest to send
   * @return the answer received 
   * @throws IOException if an error occured sending the request to the peer
   */
  public CreditControlAnswer eventCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send an initial Credit-Control-Request that will start a credit control session, and block until the answer is received.
   * 
   * @param ccr
   * @return
   * @throws IOException if an error occured sending the request to the peer
   */
  public CreditControlAnswer initialCreditControlRequest(CreditControlRequest ccr) throws IOException;

  /**
   * Send an update (intermediate) Credit-Control-Request and block until the answer is received.
   * 
   * @param ccr
   * @return
   * @throws IOException if an error occured sending the request to the peer
   * @throws IllegalArgumentException if the CreditControlRequest does not contain CC-Request-Number and Session-Id AVPs
   */
  public CreditControlAnswer updateCreditControlRequest(CreditControlRequest ccr) throws IOException, IllegalArgumentException ;

  /**
   * Send a termination Credit-Control-Request and block until the answer is received.
   * 
   * @param ccr the CreditControlRequest to send
   * @return the answer received 
   * @throws IOException if an error occured sending the request to the peer
   * @throws IllegalArgumentException if the CreditControlRequest does not contain CC-Request-Number and Session-Id AVPs
   */
  public CreditControlAnswer terminationCreditControlRequest(CreditControlRequest ccr) throws IOException, IllegalArgumentException ;

  /**
   * Marshal a Credit-Control-Request into a byte array that can be serialized (e.g., stored in a CMP field).
   * 
   * @param ccr the Credit-Control-Request to marshal 
   * @return a byte array with the marshalled data
   */
  public byte[] marshalRoCreditControlRequest(CreditControlRequest ccr);

  /**
   * Marshal a Credit-Control-Answer into a byte array that can be serialized (e.g., stored in a CMP field).
   * 
   * @param cca the Credit-Control-Answer to marshal 
   * @return a byte array with the marshalled data
   */
  public byte[] marshalRoCreditControlAnswer(CreditControlAnswer cca);

  /**
   * Unmarshal a Credit-Control-Request from a byte array.
   * 
   * @param b the byte array to unmarshal
   * @return a Credit-Control-Request constructed from the data in the byte array 
   */
  public CreditControlRequest unmarshalRoCreditControlRequest(byte[] b) throws IOException, AvpNotAllowedException;

  /**
   * Unmarshal a Credit-Control-Answer from a byte array.
   * 
   * @param b the byte array to unmarshal
   * @return a Credit-Control-Answer constructed from the data in the byte array 
   */
  public CreditControlAnswer unmarshalRoCreditControlAnswer(byte[] b) throws IOException, AvpNotAllowedException;

}
