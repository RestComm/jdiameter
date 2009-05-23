package org.mobicents.slee.resource.diameter.cca;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ReAuthRequestTypeAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;
import net.java.slee.resource.diameter.cca.CreditControlSessionState;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.handlers.CCASessionCreationListener;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.common.api.app.cca.ServerCCASessionState;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

/**
 * Start time:15:26:12 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlServerSessionImpl extends CreditControlSessionImpl implements CreditControlServerSession {

  private static Logger logger = Logger.getLogger(CreditControlServerSessionImpl.class);

  protected ServerCCASession session = null;
  protected ArrayList<DiameterAvp> sessionAvps = new ArrayList<DiameterAvp>();
  protected CreditControlRequest lastRequest = null;

  /**
   * 
   * @param messageFactory
   * @param avpFactory
   * @param session
   * @param timeout
   * @param destinationHost
   * @param destinationRealm
   * @param endpoint
   */
  public CreditControlServerSessionImpl(CreditControlMessageFactory messageFactory, CreditControlAVPFactory avpFactory, ServerCCASession session,
      long timeout, DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint)
  {
    super(messageFactory, avpFactory, null, (EventListener<Request, Answer>) session, timeout, destinationHost, destinationRealm, endpoint);

    this.session = session;
    this.session.addStateChangeNotification(this);
    super.setCurrentWorkingSession(this.session.getSessions().get(0));
  }

  public void endActivity()
  {
    this.listener.sessionDestroyed(this.sessionId, this);
    this.session.release();
  }

  public Object getDiameterAvpFactory()
  {
    return this.ccaAvpFactory;
  }

  public Object getDiameterMessageFactory()
  {
    return this.ccaMessageFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlServerSession#createCreditControlAnswer()
   */
  public CreditControlAnswer createCreditControlAnswer()
  {
    CreditControlAnswer answer = super.ccaMessageFactory.createCreditControlAnswer(lastRequest);

    // Fill extension avps if present
    if (sessionAvps.size() > 0)
    {
      try
      {
        answer.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error( "Failed to add Session AVPs to answer.", e );
      }
    }

    return answer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlServerSession#sendCreditControlAnswer
   * (net.java.slee.resource.diameter.cca.events.CreditControlAnswer)
   */
  public void sendCreditControlAnswer(CreditControlAnswer cca) throws IOException
  {
    fetchCurrentState(cca);

    DiameterMessageImpl msg = (DiameterMessageImpl)cca;

    try
    {
      session.sendCreditControlAnswer(new JCreditControlAnswerImpl((Answer) msg.getGenericData()));
    }
    catch (Exception e) {
      logger.error( "Failed to send Credit-Control-Answer.", e );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlServerSession#sendReAuthRequest
   * (net.java.slee.resource.diameter.base.events.ReAuthRequest)
   */
  public void sendReAuthRequest(ReAuthRequest rar) throws IOException
  {
	  
	//RFC 4006 5.5
	rar.setReAuthRequestType(ReAuthRequestTypeAvp.AUTHORIZE_ONLY);
	rar.setAuthApplicationId(CreditControlMessageFactory._CCA_AUTH_APP_ID);
	
    DiameterMessageImpl msg = (DiameterMessageImpl) rar;

    try
    {
      session.sendReAuthRequest(new ReAuthRequestImpl((Request) msg.getGenericData()));
    }
    catch (Exception e) {
      logger.error( "Failed to send Re-Auth-Request.", e );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum, java.lang.Enum)
   */
  public void stateChanged(Enum oldState, Enum newState)
  {
    logger.info( "Credit-Control Server FSM State Changed: " + oldState + " => " + newState );

    ServerCCASessionState s = (ServerCCASessionState) newState;

    // IDLE(0), OPEN(1);    
    switch (s)
    {
    case OPEN:
      // FIXME: this should not happen?
      this.state = CreditControlSessionState.OPEN;

      break;
    case IDLE:
      this.state = CreditControlSessionState.IDLE;

      // Destroy and release session
      ((CCASessionCreationListener) this.getSessionListener()).sessionDestroyed(sessionId, this);
      this.session.release();

      break;
    default:
      logger.error("Unexpected state in Credit-Control Server FSM: " + s);
    }
  }

  public void fetchCurrentState(CreditControlRequest ccr)
  {
    this.lastRequest = ccr;
    // TODO: Complete this method.
  }

  public void fetchCurrentState(CreditControlAnswer cca)
  {
    // TODO: Complete this method.
  }

  public ServerCCASession getSession()
  {
    return this.session;
  }
}
