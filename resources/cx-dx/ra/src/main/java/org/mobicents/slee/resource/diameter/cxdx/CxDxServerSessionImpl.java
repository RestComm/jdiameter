package org.mobicents.slee.resource.diameter.cxdx;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.CxDxServerSession;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoAnswer;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationAnswer;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.LocationInfoAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.ServerAssignmentAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.UserAuthorizationAnswerImpl;


/**
 *
 * CxDxServerSessionImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxServerSessionImpl extends CxDxSessionImpl implements CxDxServerSession {

  private DiameterMessage lastRequest = null;

  protected ArrayList<DiameterAvp> sessionAvps = new ArrayList<DiameterAvp>();

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
  public CxDxServerSessionImpl(CxDxMessageFactory messageFactory, CxDxAVPFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
    super(messageFactory, avpFactory, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#createLocationInfoAnswer()
   */
  public LocationInfoAnswer createLocationInfoAnswer() {
    // Make sure we have the correct type of Request
    if(!(lastRequest instanceof LocationInfoRequest)) {
      logger.warn("Invalid type of answer for this activity.");
      return null;
    }
    
    // Create Request from last received and set it as answer
    Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
    msg.setRequest(false);
    LocationInfoAnswer lia = new LocationInfoAnswerImpl(msg);

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        lia.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    // Guarantee session-id is present
    if(!lia.hasSessionId()) {
      lia.setSessionId(sessionId);
    }

    return lia;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#createMultimediaAuthenticationAnswer()
   */
  public MultimediaAuthenticationAnswer createMultimediaAuthenticationAnswer() {
    // Make sure we have the correct type of Request
    if(!(lastRequest instanceof MultimediaAuthenticationRequest)) {
      logger.warn("Invalid type of answer for this activity.");
      return null;
    }
    
    // Create Request from last received and set it as answer
    Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
    msg.setRequest(false);
    MultimediaAuthenticationAnswer maa = new MultimediaAuthenticationAnswerImpl(msg);

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        maa.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    // Guarantee session-id is present
    if(!maa.hasSessionId()) {
      maa.setSessionId(sessionId);
    }

    return maa;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#createPushProfileRequest()
   */
  public PushProfileRequest createPushProfileRequest() {
    // Create the request
    PushProfileRequest ppr = super.cxdxMessageFactory.createPushProfileRequest(super.getSessionId());

    // If there's a Destination-Host, add the AVP
    if (destinationHost != null) {
      ppr.setDestinationHost(destinationHost);
    }

    if (destinationRealm != null) {
      ppr.setDestinationRealm(destinationRealm);
    }

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        ppr.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    return ppr;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#createRegistrationTerminationAnswer()
   */
  public RegistrationTerminationAnswer createRegistrationTerminationAnswer() {
    // Make sure we have the correct type of Request
    if(!(lastRequest instanceof RegistrationTerminationRequest)) {
      logger.warn("Invalid type of answer for this activity.");
      return null;
    }
    
    // Create Request from last received and set it as answer
    Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
    msg.setRequest(false);
    RegistrationTerminationAnswer rta = new RegistrationTerminationAnswerImpl(msg);

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        rta.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    // Guarantee session-id is present
    if(!rta.hasSessionId()) {
      rta.setSessionId(sessionId);
    }

    return rta;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#createServerAssignmentAnswer()
   */
  public ServerAssignmentAnswer createServerAssignmentAnswer() {
    // Make sure we have the correct type of Request
    if(!(lastRequest instanceof ServerAssignmentRequest)) {
      logger.warn("Invalid type of answer for this activity.");
      return null;
    }
    
    // Create Request from last received and set it as answer
    Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
    msg.setRequest(false);
    ServerAssignmentAnswer saa = new ServerAssignmentAnswerImpl(msg);

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        saa.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    // Guarantee session-id is present
    if(!saa.hasSessionId()) {
      saa.setSessionId(sessionId);
    }

    return saa;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#createUserAuthorizationAnswer()
   */
  public UserAuthorizationAnswer createUserAuthorizationAnswer() {
    // Make sure we have the correct type of Request
    if(!(lastRequest instanceof UserAuthorizationRequest)) {
      logger.warn("Invalid type of answer for this activity.");
      return null;
    }
    
    // Create Request from last received and set it as answer
    Message msg = session.createRequest((Request) ((DiameterMessageImpl)lastRequest).getGenericData());
    msg.setRequest(false);
    UserAuthorizationAnswer uaa = new UserAuthorizationAnswerImpl(msg);

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        uaa.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    // Guarantee session-id is present
    if(!uaa.hasSessionId()) {
      uaa.setSessionId(sessionId);
    }

    return uaa;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#sendLocationInfoAnswer(net.java.slee.resource.diameter.cxdx.events.LocationInfoAnswer)
   */
  public void sendLocationInfoAnswer(LocationInfoAnswer locationInfoAnswer) throws IOException {
    sendCxDxMessage(locationInfoAnswer);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#sendMultimediaAuthenticationAnswer(net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer)
   */
  public void sendMultimediaAuthenticationAnswer(MultimediaAuthenticationAnswer multimediaAuthenticationAnswer) throws IOException {
    sendCxDxMessage(multimediaAuthenticationAnswer);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#sendPushProfileRequest(net.java.slee.resource.diameter.cxdx.events.PushProfileRequest)
   */
  public void sendPushProfileRequest(PushProfileRequest pushProfileRequest) throws IOException {
    sendCxDxMessage(pushProfileRequest);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#sendRegistrationTerminationAnswer(net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer)
   */
  public void sendRegistrationTerminationAnswer(RegistrationTerminationAnswer registrationTerminationAnswer) throws IOException {
    sendCxDxMessage(registrationTerminationAnswer);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#sendServerAssignmentAnswer(net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer)
   */
  public void sendServerAssignmentAnswer(ServerAssignmentAnswer serverAssignmentAnswer) throws IOException {
    sendCxDxMessage(serverAssignmentAnswer);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxServerSession#sendUserAuthorizationAnswer(net.java.slee.resource.diameter.cxdx.events.UserAuthorizationAnswer)
   */
  public void sendUserAuthorizationAnswer(UserAuthorizationAnswer userAuthorizationAnswer) throws IOException {
    sendCxDxMessage(userAuthorizationAnswer);
  }

  protected void sendCxDxMessage(DiameterMessage message) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) message;
    try {
      session.send(msg.getGenericData());
    }
    catch (JAvpNotAllowedException e) {
      AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
      throw anae;
    }
    catch (Exception e) {
      IOException ioe = new IOException("Failed to send message, due to: " + e);
      throw ioe;
    }
  }
}
