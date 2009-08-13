package org.mobicents.slee.resource.diameter.cxdx;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.LocationInfoRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.ServerAssignmentRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.UserAuthorizationRequestImpl;

/**
 *
 * CxDxMessageFactoryImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxMessageFactoryImpl extends DiameterMessageFactoryImpl implements CxDxMessageFactory {

  private static Logger logger = Logger.getLogger(CxDxMessageFactoryImpl.class);

  private ApplicationId cxdxAppId = ApplicationId.createByAuthAppId(_CXDX_VENDOR, _CXFX_AUTH_APP_ID);

  private DiameterMessageFactory baseMessagefactory;

  /**
   * @param session
   * @param stack
   * @param avps
   */
  public CxDxMessageFactoryImpl(Session session, Stack stack, DiameterIdentity... avps) {
    super(session, stack, avps);
    this.baseMessagefactory = new DiameterMessageFactoryImpl(stack);
  }

  /**
   * @param stack
   */
  public CxDxMessageFactoryImpl(Stack stack) {
    super(stack);
    this.baseMessagefactory = new DiameterMessageFactoryImpl(stack);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createLocationInfoRequest()
   */
  public LocationInfoRequest createLocationInfoRequest() {
    LocationInfoRequest lir = null;
    try {
      Message msg = stack.getSessionFactory().getNewRawSession().createMessage(LocationInfoRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      msg.setRequest(true);
      lir = new LocationInfoRequestImpl(msg);
    }
    catch (InternalException e) {
      logger.error("Failed to create Location-Info-Request", e);
    }
    catch (IllegalDiameterStateException e) {
      logger.error("Failed to create Location-Info-Request", e);
    }

    return lir;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createLocationInfoRequest(java.lang.String)
   */
  public LocationInfoRequest createLocationInfoRequest(String sessionId) throws IllegalArgumentException {
    LocationInfoRequest lir = createLocationInfoRequest();
    lir.setSessionId(sessionId);

    return lir;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createMultimediaAuthenticationRequest()
   */
  public MultimediaAuthenticationRequest createMultimediaAuthenticationRequest() {
    MultimediaAuthenticationRequest mar = null;
    try {
      Message msg = stack.getSessionFactory().getNewRawSession().createMessage(MultimediaAuthenticationRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      msg.setRequest(true);
      mar = new MultimediaAuthenticationRequestImpl(msg);
    }
    catch (InternalException e) {
      logger.error("Failed to create Multimedia-Authentication-Request", e);
    }
    catch (IllegalDiameterStateException e) {
      logger.error("Failed to create Multimedia-Authentication-Request", e);
    }

    return mar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createMultimediaAuthenticationRequest(java.lang.String)
   */
  public MultimediaAuthenticationRequest createMultimediaAuthenticationRequest(String sessionId) throws IllegalArgumentException {
    MultimediaAuthenticationRequest mar = createMultimediaAuthenticationRequest();
    mar.setSessionId(sessionId);

    return mar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createPushProfileRequest()
   */
  public PushProfileRequest createPushProfileRequest() {
    PushProfileRequest ppr = null;
    try {
      Message msg = stack.getSessionFactory().getNewRawSession().createMessage(PushProfileRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      msg.setRequest(true);
      ppr = new PushProfileRequestImpl(msg);
    }
    catch (InternalException e) {
      logger.error("Failed to create Push-Profile-Request", e);
    }
    catch (IllegalDiameterStateException e) {
      logger.error("Failed to create Push-Profile-Request", e);
    }

    return ppr;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createPushProfileRequest(java.lang.String)
   */
  public PushProfileRequest createPushProfileRequest(String sessionId) throws IllegalArgumentException {
    PushProfileRequest ppr = createPushProfileRequest();
    ppr.setSessionId(sessionId);

    return ppr;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createRegistrationTerminationRequest()
   */
  public RegistrationTerminationRequest createRegistrationTerminationRequest() {
    RegistrationTerminationRequest rtr = null;
    try {
      Message msg = stack.getSessionFactory().getNewRawSession().createMessage(RegistrationTerminationRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      msg.setRequest(true);
      rtr = new RegistrationTerminationRequestImpl(msg);
    }
    catch (InternalException e) {
      logger.error("Failed to create Registration-Termination-Request", e);
    }
    catch (IllegalDiameterStateException e) {
      logger.error("Failed to create Registration-Termination-Request", e);
    }

    return rtr;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createRegistrationTerminationRequest(java.lang.String)
   */
  public RegistrationTerminationRequest createRegistrationTerminationRequest(String sessionId) throws IllegalArgumentException {
    RegistrationTerminationRequest rtr = createRegistrationTerminationRequest();
    rtr.setSessionId(sessionId);

    return rtr;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createServerAssignmentRequest()
   */
  public ServerAssignmentRequest createServerAssignmentRequest() {
    ServerAssignmentRequest sar = null;
    try {
      Message msg = stack.getSessionFactory().getNewRawSession().createMessage(ServerAssignmentRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      msg.setRequest(true);
      sar = new ServerAssignmentRequestImpl(msg);
    }
    catch (InternalException e) {
      logger.error("Failed to create Server-Assignment-Request", e);
    }
    catch (IllegalDiameterStateException e) {
      logger.error("Failed to create Server-Assignment-Request", e);
    }

    return sar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createServerAssignmentRequest(java.lang.String)
   */
  public ServerAssignmentRequest createServerAssignmentRequest(String sessionId) throws IllegalArgumentException {
    ServerAssignmentRequest sar = createServerAssignmentRequest();
    sar.setSessionId(sessionId);

    return sar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createUserAuthorizationRequest()
   */
  public UserAuthorizationRequest createUserAuthorizationRequest() {
    UserAuthorizationRequest uar = null;
    try {
      Message msg = stack.getSessionFactory().getNewRawSession().createMessage(UserAuthorizationRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      msg.setRequest(true);
      uar = new UserAuthorizationRequestImpl(msg);
    }
    catch (InternalException e) {
      logger.error("Failed to create User-Authorization-Request", e);
    }
    catch (IllegalDiameterStateException e) {
      logger.error("Failed to create User-Authorization-Request", e);
    }

    return uar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createUserAuthorizationRequest(java.lang.String)
   */
  public UserAuthorizationRequest createUserAuthorizationRequest(String sessionId) throws IllegalArgumentException {
    UserAuthorizationRequest uar = createUserAuthorizationRequest();
    uar.setSessionId(sessionId);

    return uar;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#getBaseMessageFactory()
   */
  public DiameterMessageFactory getBaseMessageFactory() {
    return baseMessagefactory;
  }

}
