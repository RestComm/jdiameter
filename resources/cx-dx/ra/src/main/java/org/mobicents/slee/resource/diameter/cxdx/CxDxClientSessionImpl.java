package org.mobicents.slee.resource.diameter.cxdx;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxClientSession;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.impl.app.cxdx.JLocationInfoRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JMultimediaAuthRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JPushProfileAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JRegistrationTerminationAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JUserAuthorizationRequestImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswerImpl;

/**
 *
 * CxDxClientSessionImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxClientSessionImpl extends CxDxSessionImpl implements CxDxClientSession {

  protected ArrayList<DiameterAvp> sessionAvps = new ArrayList<DiameterAvp>();
  protected ClientCxDxSession appSession;
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
  public CxDxClientSessionImpl(CxDxMessageFactory messageFactory, CxDxAVPFactory avpFactory, ClientCxDxSession session, EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
    super(messageFactory, avpFactory, session.getSessions().get(0), raEventListener, timeout, destinationHost, destinationRealm, endpoint);
    this.appSession = session;
    this.appSession.addStateChangeNotification(this);
  }



/* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#createLocationInfoRequest()
   */
  public LocationInfoRequest createLocationInfoRequest() {
    // Create the request
    LocationInfoRequest lir = super.cxdxMessageFactory.createLocationInfoRequest(super.getSessionId());

    // If there's a Destination-Host, add the AVP
    if (destinationHost != null) {
      lir.setDestinationHost(destinationHost);
    }

    if (destinationRealm != null) {
      lir.setDestinationRealm(destinationRealm);
    }

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        lir.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    return lir;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#createMultimediaAuthenticationRequest()
   */
  public MultimediaAuthenticationRequest createMultimediaAuthenticationRequest() {
    // Create the request
    MultimediaAuthenticationRequest mar = super.cxdxMessageFactory.createMultimediaAuthenticationRequest(super.getSessionId());

    // If there's a Destination-Host, add the AVP
    if (destinationHost != null) {
      mar.setDestinationHost(destinationHost);
    }

    if (destinationRealm != null) {
      mar.setDestinationRealm(destinationRealm);
    }

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        mar.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    return mar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#createPushProfileAnswer()
   */
  public PushProfileAnswer createPushProfileAnswer() {
    // Create Request from last received and set it as answer
    Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
    //FIXME: Alex this is prob with ANSWER
    msg.setRequest(false);
    PushProfileAnswer ppa = new PushProfileAnswerImpl(msg);

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        ppa.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    // Guarantee session-id is present
    if(!ppa.hasSessionId()) {
      ppa.setSessionId(sessionId);
    }

    return ppa;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#createRegistrationTerminationRequest()
   */
  public RegistrationTerminationAnswer createRegistrationTerminationAnswer() {
	//FIXME: Alex this is prob with ANSWER
	  Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
	    msg.setRequest(false);
	    RegistrationTerminationAnswer ppa = new RegistrationTerminationAnswerImpl(msg);

	    // Fill extension avps if present
	    if (sessionAvps.size() > 0) {
	      try {
	        ppa.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
	      }
	      catch (AvpNotAllowedException e) {
	        logger.error("Failed to add Session AVPs to request.", e);
	      }
	    }

	    // Guarantee session-id is present
	    if(!ppa.hasSessionId()) {
	      ppa.setSessionId(sessionId);
	    }

	    return ppa;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#createServerAssignmentRequest()
   */
  public ServerAssignmentRequest createServerAssignmentRequest() {
    // Create the request
    ServerAssignmentRequest sar = super.cxdxMessageFactory.createServerAssignmentRequest(super.getSessionId());

    // If there's a Destination-Host, add the AVP
    if (destinationHost != null) {
      sar.setDestinationHost(destinationHost);
    }

    if (destinationRealm != null) {
      sar.setDestinationRealm(destinationRealm);
    }

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        sar.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    return sar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#createUserAuthorizationRequest()
   */
  public UserAuthorizationRequest createUserAuthorizationRequest() {
    // Create the request
    UserAuthorizationRequest uar = super.cxdxMessageFactory.createUserAuthorizationRequest(super.getSessionId());

    // If there's a Destination-Host, add the AVP
    if (destinationHost != null) {
      uar.setDestinationHost(destinationHost);
    }

    if (destinationRealm != null) {
      uar.setDestinationRealm(destinationRealm);
    }

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        uar.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    return uar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendLocationInfoRequest(net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest)
   */
  public void sendLocationInfoRequest(LocationInfoRequest locationInfoRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) locationInfoRequest;
    try{
    	appSession.sendLocationInformationRequest(new JLocationInfoRequestImpl(msg.getGenericData()));
    } catch(JAvpNotAllowedException anae)
	{
		throw new AvpNotAllowedException(anae.getMessage(),anae.getAvpCode(),anae.getVendorId());
	}catch (Exception e) {
		e.printStackTrace();
		throw new IOException(e.getMessage());
	} 
//    try {
//      session.send(msg.getGenericData());
//    }
//    catch (JAvpNotAllowedException e) {
//      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
//      throw anae;
//    }
//    catch (Exception e) {
//      IOException ioe = new IOException("Failed to send message, due to: " + e);
//      throw ioe;
//    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendMultimediaAuthenticationRequest(net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest)
   */
  public void sendMultimediaAuthenticationRequest(MultimediaAuthenticationRequest multimediaAuthenticationRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) multimediaAuthenticationRequest;
    try{
    	appSession.sendMultimediaAuthRequest(new JMultimediaAuthRequestImpl(msg.getGenericData()));
    } catch(JAvpNotAllowedException anae)
	{
		throw new AvpNotAllowedException(anae.getMessage(),anae.getAvpCode(),anae.getVendorId());
	}catch (Exception e) {
		e.printStackTrace();
		throw new IOException(e.getMessage());
	} 
//    try {
//      session.send(msg.getGenericData());
//    }
//    catch (JAvpNotAllowedException e) {
//      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
//      throw anae;
//    }
//    catch (Exception e) {
//      IOException ioe = new IOException("Failed to send message, due to: " + e);
//      throw ioe;
//    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendPushProfileAnswer(net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer)
   */
  public void sendPushProfileAnswer(PushProfileAnswer pushProfileAnswer) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) pushProfileAnswer;
    try{
    	appSession.sendPushProfileAnswer(new JPushProfileAnswerImpl(msg.getGenericData()));
    } catch(JAvpNotAllowedException anae)
	{
		throw new AvpNotAllowedException(anae.getMessage(),anae.getAvpCode(),anae.getVendorId());
	}catch (Exception e) {
		e.printStackTrace();
		throw new IOException(e.getMessage());
	} 
//    try {
//      session.send(msg.getGenericData());
//    }
//    catch (JAvpNotAllowedException e) {
//      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
//      throw anae;
//    }
//    catch (Exception e) {
//      IOException ioe = new IOException("Failed to send message, due to: " + e);
//      throw ioe;
//    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendRegistrationTerminationRequest(net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest)
   */
  public void sendRegistrationTerminationAnswer(RegistrationTerminationAnswer registrationTerminationAnswer) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) registrationTerminationAnswer;
    try{
    	appSession.sendRegistrationTerminationAnswer(new JRegistrationTerminationAnswerImpl(msg.getGenericData()));
    } catch(JAvpNotAllowedException anae)
	{
		throw new AvpNotAllowedException(anae.getMessage(),anae.getAvpCode(),anae.getVendorId());
	}catch (Exception e) {
		e.printStackTrace();
		throw new IOException(e.getMessage());
	} 
//    try {
//      session.send(msg.getGenericData());
//    }
//    catch (JAvpNotAllowedException e) {
//      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
//      throw anae;
//    }
//    catch (Exception e) {
//      IOException ioe = new IOException("Failed to send message, due to: " + e);
//      throw ioe;
//    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendServerAssignmentRequest(net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest)
   */
  public void sendServerAssignmentRequest(ServerAssignmentRequest serverAssignmentRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) serverAssignmentRequest;
    try{
    	appSession.sendServerAssignmentRequest(new JServerAssignmentRequestImpl(msg.getGenericData()));
    } catch(JAvpNotAllowedException anae)
	{
		throw new AvpNotAllowedException(anae.getMessage(),anae.getAvpCode(),anae.getVendorId());
	}catch (Exception e) {
		e.printStackTrace();
		throw new IOException(e.getMessage());
	} 
//    try {
//      session.send(msg.getGenericData());
//    }
//    catch (JAvpNotAllowedException e) {
//      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
//      throw anae;
//    }
//    catch (Exception e) {
//      IOException ioe = new IOException("Failed to send message, due to: " + e);
//      throw ioe;
//    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendUserAuthorizationRequest(net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest)
   */
  public void sendUserAuthorizationRequest(UserAuthorizationRequest userAuthorizationRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) userAuthorizationRequest;
    try {
			appSession.sendUserAuthorizationRequest(new JUserAuthorizationRequestImpl(msg.getGenericData()));
		} catch (JAvpNotAllowedException anae) {
			throw new AvpNotAllowedException(anae.getMessage(), anae.getAvpCode(), anae.getVendorId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} 
//    try {
//      session.send(msg.getGenericData());
//    }
//    catch (JAvpNotAllowedException e) {
//      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
//      throw anae;
//    }
//    catch (Exception e) {
//      IOException ioe = new IOException("Failed to send message, due to: " + e);
//      throw ioe;
//    }
  }
  public void stateChanged(Enum oldState, Enum newState) {
		if (!terminated)
			if (newState == CxDxSessionState.TERMINATED || newState == CxDxSessionState.TIMEDOUT) {
				terminated = true;
				super.cxdxSessionListener.sessionDestroyed(sessionId, this.appSession);
			}

	}
}
