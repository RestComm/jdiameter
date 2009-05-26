package org.mobicents.slee.resource.diameter.ro;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.ro.RoClientSession;
import net.java.slee.resource.diameter.ro.RoMessageFactory;

import org.jdiameter.api.Stack;
import org.jdiameter.api.cca.ClientCCASession;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlClientSessionImpl;


/**
 * RoClientSessionImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:24:20 PM Apr 14, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoClientSessionImpl extends CreditControlClientSessionImpl implements RoClientSession {

  RoMessageFactory roMessageFactory = null;

  /**
   * @param messageFactory
   * @param avpFactory
   * @param session
   * @param timeout
   * @param destinationHost
   * @param destinationRealm
   * @param endpoint
   */
  public RoClientSessionImpl( CreditControlMessageFactory messageFactory, CreditControlAVPFactory avpFactory, ClientCCASession session, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint, Stack stack )
  {
    super( messageFactory, avpFactory, session, timeout, destinationHost, destinationRealm, endpoint );
    
    this.roMessageFactory = new RoMessageFactoryImpl((DiameterMessageFactoryImpl) messageFactory.getBaseMessageFactory(), session.getSessions().get(0), stack, avpFactory);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoClientSession#sendEventCreditControlRequest(net.java.slee.resource.diameter.cca.events.CreditControlRequest)
   */
  public void sendEventCreditControlRequest( CreditControlRequest ccr ) throws IOException
  {
    super.sendCreditControlRequest( ccr );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoSession#getRoMessageFactory()
   */
  public RoMessageFactory getRoMessageFactory()
  {
    return this.roMessageFactory;
  }

}
