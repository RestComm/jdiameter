package org.mobicents.slee.resource.diameter.ro;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.ro.RoMessageFactory;
import net.java.slee.resource.diameter.ro.RoServerSession;

import org.jdiameter.api.Stack;
import org.jdiameter.api.cca.ServerCCASession;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlServerSessionImpl;

/**
 * RoServerSessionImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:23:06 PM Apr 14, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoServerSessionImpl extends CreditControlServerSessionImpl implements RoServerSession {

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
  public RoServerSessionImpl( CreditControlMessageFactory messageFactory, CreditControlAVPFactory avpFactory, ServerCCASession session, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint, Stack stack )
  {
    super( messageFactory, avpFactory, session, timeout, destinationHost, destinationRealm, endpoint );
    
    this.roMessageFactory = new RoMessageFactoryImpl((DiameterMessageFactoryImpl) messageFactory.getBaseMessageFactory(), session.getSessions().get(0), stack, avpFactory);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoServerSession#createRoCreditControlAnswer()
   */
  public CreditControlAnswer createRoCreditControlAnswer()
  {
    return this.ccaMessageFactory.createCreditControlAnswer((String)null);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.RoSession#getRoMessageFactory()
   */
  public RoMessageFactory getRoMessageFactory()
  {
    return this.roMessageFactory;
  }

}
