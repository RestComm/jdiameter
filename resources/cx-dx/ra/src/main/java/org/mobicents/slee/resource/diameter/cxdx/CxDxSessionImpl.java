package org.mobicents.slee.resource.diameter.cxdx;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.CxDxSession;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener;


/**
 *
 * CxDxSessionImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class CxDxSessionImpl extends DiameterActivityImpl implements CxDxSession ,StateChangeListener{

  protected CxDxMessageFactory cxdxMessageFactory = null;
  protected CxDxAVPFactory cxdxAvpFactory = null;

  protected DiameterMessage lastRequest = null;
  protected boolean terminated = false;
  //FIXME: missed this in last release, this has to be fixed in base....!!!!!!!!!!!!!!!!!!
  protected CxDxSessionCreationListener cxdxSessionListener;
  public CxDxSessionImpl(CxDxMessageFactory messageFactory, CxDxAVPFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
    super(null, null, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);

    this.cxdxMessageFactory = messageFactory;
    this.cxdxAvpFactory = avpFactory;
    this.cxdxSessionListener = (CxDxSessionCreationListener)raEventListener;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxSession#getCxDxAvpFactory()
   */
  public CxDxAVPFactory getCxDxAvpFactory() {
    return this.cxdxAvpFactory;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxSession#getCxDxMessageFactory()
   */
  public CxDxMessageFactory getCxDxMessageFactory() {
    return this.cxdxMessageFactory;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxSession#getSessionId()
   */
  public String getSessionId() {
    return session.getSessionId();
  }

  public void fetchSessionData(DiameterMessage cxdxRequest) {
    this.lastRequest = cxdxRequest;
  }



}
