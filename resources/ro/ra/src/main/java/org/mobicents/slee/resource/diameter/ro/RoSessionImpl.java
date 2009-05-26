package org.mobicents.slee.resource.diameter.ro;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.ro.RoAvpFactory;
import net.java.slee.resource.diameter.ro.RoMessageFactory;
import net.java.slee.resource.diameter.ro.RoSession;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.mobicents.slee.resource.diameter.cca.CreditControlSessionImpl;

/**
 * RoSessionImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:20:06 PM Apr 14, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class RoSessionImpl extends CreditControlSessionImpl implements RoSession {

  RoMessageFactory roMessageFactory;
  RoAvpFactory roAvpFactory;
  
  /**
   * @param messageFactory
   * @param avpFactory
   * @param session
   * @param raEventListener
   * @param timeout
   * @param destinationHost
   * @param destinationRealm
   * @param endpoint
   */
  public RoSessionImpl( RoMessageFactory messageFactory, RoAvpFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint )
  {
    // FIXME: Alexandre: Not sure here...
    super( null, null, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint );
    
    this.roMessageFactory = messageFactory;
    this.roAvpFactory = avpFactory;
  }

}
