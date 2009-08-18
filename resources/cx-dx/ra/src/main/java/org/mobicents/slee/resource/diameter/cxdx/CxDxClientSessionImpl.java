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
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileAnswerImpl;

/**
 *
 * CxDxClientSessionImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxClientSessionImpl extends CxDxSessionImpl implements CxDxClientSession {

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
  public CxDxClientSessionImpl(CxDxMessageFactory messageFactory, CxDxAVPFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint) {
    super(messageFactory, avpFactory, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);
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
  public RegistrationTerminationRequest createRegistrationTerminationRequest() {
    // Create the request
    RegistrationTerminationRequest rtr = super.cxdxMessageFactory.createRegistrationTerminationRequest(super.getSessionId());

    // If there's a Destination-Host, add the AVP
    if (destinationHost != null) {
      rtr.setDestinationHost(destinationHost);
    }

    if (destinationRealm != null) {
      rtr.setDestinationRealm(destinationRealm);
    }

    // Fill extension avps if present
    if (sessionAvps.size() > 0) {
      try {
        rtr.setExtensionAvps(sessionAvps.toArray(new DiameterAvp[sessionAvps.size()]));
      }
      catch (AvpNotAllowedException e) {
        logger.error("Failed to add Session AVPs to request.", e);
      }
    }

    return rtr;
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendMultimediaAuthenticationRequest(net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest)
   */
  public void sendMultimediaAuthenticationRequest(MultimediaAuthenticationRequest multimediaAuthenticationRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) multimediaAuthenticationRequest;
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendPushProfileAnswer(net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer)
   */
  public void sendPushProfileAnswer(PushProfileAnswer pushProfileAnswer) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) pushProfileAnswer;
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendRegistrationTerminationRequest(net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest)
   */
  public void sendRegistrationTerminationRequest(RegistrationTerminationRequest registrationTerminationRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) registrationTerminationRequest;
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendServerAssignmentRequest(net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest)
   */
  public void sendServerAssignmentRequest(ServerAssignmentRequest serverAssignmentRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) serverAssignmentRequest;
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxClientSession#sendUserAuthorizationRequest(net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest)
   */
  public void sendUserAuthorizationRequest(UserAuthorizationRequest userAuthorizationRequest) throws IOException {
    DiameterMessageImpl msg = (DiameterMessageImpl) userAuthorizationRequest;
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
