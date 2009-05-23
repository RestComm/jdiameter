package org.mobicents.slee.resource.diameter.cca;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlSession;
import net.java.slee.resource.diameter.cca.CreditControlSessionState;
import net.java.slee.resource.diameter.cca.handlers.CCASessionCreationListener;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;

/**
 * 
 * CreditControlSessionImpl.java
 *
 * <br>Super project:  mobicents
 * <br>8:34:55 PM Dec 29, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public abstract class CreditControlSessionImpl extends DiameterActivityImpl implements CreditControlSession,StateChangeListener{

  protected CreditControlMessageFactory ccaMessageFactory = null;
  protected CreditControlAVPFactory ccaAvpFactory = null;
  protected CreditControlSessionState state = CreditControlSessionState.IDLE;
  protected CCASessionCreationListener listener = null;

  public CreditControlSessionImpl(CreditControlMessageFactory messageFactory, CreditControlAVPFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout,
      DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint)
  {
    super(null, null, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);

    this.ccaMessageFactory=messageFactory;
    this.ccaAvpFactory=avpFactory;
  }

  public CreditControlSessionState getState()
  {
    return state;
  }

  @Override
  public Object getDiameterAvpFactory()
  {
    return this.ccaAvpFactory;
  }

  @Override
  public Object getDiameterMessageFactory()
  {
    return this.ccaMessageFactory;
  }

  @Override
  public Object getSessionListener()
  {
    return this.listener;
  }

  @Override
  public void setSessionListener(Object ra)
  {
    this.listener = (CCASessionCreationListener) ra;
  }

  public void setDestinationHost(DiameterIdentityAvp destinationHost)
  {
    super.destinationHost = destinationHost;

    //((CreditControlMessageFactoryImpl)ccaMessageFactory).removeAvpFromInnerList(destinationHost.getCode());
    ((CreditControlMessageFactoryImpl)ccaMessageFactory).addAvpToInnerList(destinationHost);
  }

  public void setDestinationRealm(DiameterIdentityAvp destinationRealm)
  {
    super.destinationRealm=destinationRealm;

    //((CreditControlMessageFactoryImpl)ccaMessageFactory).removeAvpFromInnerList(destinationRealm.getCode());
    ((CreditControlMessageFactoryImpl)ccaMessageFactory).addAvpToInnerList(destinationRealm);
  }
}
