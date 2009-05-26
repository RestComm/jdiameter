package net.java.slee.resource.diameter.cca;

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

/**
 * 
 * Provider to create Credit-Control sessions and obtain Messages/AVP Factories.
 *
 * <br>Super project:  mobicents
 * <br>10:58:30 AM Dec 30, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface CreditControlProvider {

  /**
   * Create a new client session to send and receive Diameter messages.
   * All messages sent on an activity created by this method must contain valid
   * routing AVPs (one or both of Destination-Realm and Destination-Host as
   * defined by RFC3588).
   * 
   * @return a instance of a CreditControlClientSession to send credit control messages
   */
  public CreditControlClientSession createClientSession()throws CreateActivityException;

  /**
   * Create a new client session to send and receive Diameter messages.
   * Messages sent on an activity created by this method will automatically
   * have the Destination-Host and Destination-Realm AVPs set to the provided
   * values.
   * 
   * @param destinationHost a destination host to automatically put in all messages, may be null if not needed
   * @param destinationRealm a destination realm to automatically put in all messages
   * @return a instance of a CreditControlClientSession to send credit control messages
   * @throws CreateActivityException 
   */
  public CreditControlClientSession createClientSession(
      DiameterIdentity destinationHost,
      DiameterIdentity destinationRealm) throws CreateActivityException;

  /**
   * Return a message factory to be used to create credit control messages
   * 
   * @return a CreditControlMessageFactory implementation
   */
  public CreditControlMessageFactory getCreditControlMessageFactory();

  /**
   * Return a AVP factory to be used to create credit control AVPs
   * 
   * @return a CreditControlAVPFactory implementation
   */
  public CreditControlAVPFactory getCreditControlAVPFactory();

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
