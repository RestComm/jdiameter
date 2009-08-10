package net.java.slee.resource.diameter.cxdx;

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

/**
 * 
 * Provider to create CxDx sessions and obtain Messages/AVP Factories.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:emmartins@gmail.com"> Eduardo Martins </a>
 */
public interface CxDxProvider {

  /**
   * Create a new client session to send and receive Diameter messages.
   * All messages sent on an activity created by this method must contain valid
   * routing AVPs (one or both of Destination-Realm and Destination-Host as
   * defined by RFC3588).
   * 
   * @return a instance of a CreditControlClientSession to send credit control messages
   */
  CxDxClientSession createClientSession()throws CreateActivityException;

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
  CxDxClientSession createClientSession(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException;

  /**
   * Create a new server session to send and receive Diameter messages.
   * All messages sent on an activity created by this method must contain valid
   * routing AVPs (one or both of Destination-Realm and Destination-Host as
   * defined by RFC3588).
   * 
   * @return a instance of a CreditControlClientSession to send credit control messages
   */
  CxDxServerSession createServerSession()throws CreateActivityException;

  /**
   * Create a new server session to send and receive Diameter messages.
   * Messages sent on an activity created by this method will automatically
   * have the Destination-Host and Destination-Realm AVPs set to the provided
   * values.
   * 
   * @param destinationHost a destination host to automatically put in all messages, may be null if not needed
   * @param destinationRealm a destination realm to automatically put in all messages
   * @return a instance of a CreditControlClientSession to send credit control messages
   * @throws CreateActivityException 
   */
  CxDxServerSession createServerSession(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException;

  /**
   * Return a message factory to be used to create credit control messages
   * 
   * @return a CreditControlMessageFactory implementation
   */
  CxDxMessageFactory getCxDxMessageFactory();

  /**
   * Return a AVP factory to be used to create credit control AVPs
   * 
   * @return a CreditControlAVPFactory implementation
   */
  CxDxAVPFactory getCxDxAVPFactory();

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
