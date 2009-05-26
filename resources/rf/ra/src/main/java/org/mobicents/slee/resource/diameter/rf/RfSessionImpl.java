package org.mobicents.slee.resource.diameter.rf;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.rf.RfMessageFactory;
import net.java.slee.resource.diameter.rf.RfSession;
import net.java.slee.resource.diameter.ro.RoAvpFactory;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.mobicents.slee.resource.diameter.base.AccountingSessionActivityImpl;

/**
 * 
 * RfSessionImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:02:04 AM Apr 8, 2009 
 * <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class RfSessionImpl extends AccountingSessionActivityImpl implements RfSession {

  RfMessageFactory rfMessageFactory;
  RoAvpFactory rfAvpFactory;
  
  /**
   * 
   * @param messageFactory
   * @param avpFactory
   * @param session
   * @param raEventListener
   * @param timeout
   * @param destinationHost
   * @param destinationRealm
   * @param endpoint
   */
  public RfSessionImpl( RfMessageFactory messageFactory, RoAvpFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint )
  {
    // FIXME: Alexandre: Not sure here...
    super(null, null, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);
    
    this.rfMessageFactory = messageFactory;
    this.rfAvpFactory = avpFactory;
  }

}
