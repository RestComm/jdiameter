package org.mobicents.slee.resource.diameter.cxdx;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
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
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.LocationInfoAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.LocationInfoRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.ServerAssignmentAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.ServerAssignmentRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.UserAuthorizationAnswerImpl;
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

  public static final ApplicationId cxdxAppId = ApplicationId.createByAuthAppId(_CXDX_VENDOR, _CXFX_AUTH_APP_ID);
  private DiameterAvpFactory baseAvpFactory = null;


  /**
   * @param session
   * @param stack
   * @param avps
   */
  public CxDxMessageFactoryImpl(Session session, Stack stack, DiameterIdentity... avps) {
    super(session, stack, avps);

    this.baseAvpFactory = new DiameterAvpFactoryImpl();
  }

  /**
   * @param stack
   */
  public CxDxMessageFactoryImpl(Stack stack) {
    super(stack);

    this.baseAvpFactory = new DiameterAvpFactoryImpl();
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxMessageFactory#createLocationInfoRequest()
   */
  public LocationInfoRequest createLocationInfoRequest() {
    LocationInfoRequest lir = null;
    try {
      //Message msg = stack.getSessionFactory().getNewRawSession().createMessage(LocationInfoRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      //msg.setRequest(true);
      //lir = new LocationInfoRequestImpl(msg);
			DiameterAvp[] avps = new DiameterAvp[0];

			if (session != null) {
				try {
					DiameterAvp sessionIdAvp = null;
					sessionIdAvp = baseAvpFactory
							.createAvp(0, DiameterAvpCodes.SESSION_ID, session
									.getSessionId());
					avps = new DiameterAvp[] { sessionIdAvp };
				} catch (NoSuchAvpException e) {
					logger
							.error(
									"Unexpected failure trying to create Session-Id AVP.",
									e);
				}
			}
			lir = (LocationInfoRequest) createCxDxMessage(null, avps,
					LocationInfoRequest.COMMAND_CODE, cxdxAppId);
    }
    catch (InternalException e) {
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
     // Message msg = stack.getSessionFactory().getNewRawSession().createMessage(MultimediaAuthenticationRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
     // msg.setRequest(true);
     // mar = new MultimediaAuthenticationRequestImpl(msg);
    	DiameterAvp[] avps = new DiameterAvp[0];

		if (session != null) {
			try {
				DiameterAvp sessionIdAvp = null;
				sessionIdAvp = baseAvpFactory
						.createAvp(0, DiameterAvpCodes.SESSION_ID, session
								.getSessionId());
				avps = new DiameterAvp[] { sessionIdAvp };
			} catch (NoSuchAvpException e) {
				logger
						.error(
								"Unexpected failure trying to create Session-Id AVP.",
								e);
			}
		}
		mar =  (MultimediaAuthenticationRequest) createCxDxMessage(null, avps,
				MultimediaAuthenticationRequest.COMMAND_CODE, cxdxAppId);
    }
    catch (InternalException e) {
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
     // Message msg = stack.getSessionFactory().getNewRawSession().createMessage(PushProfileRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      //msg.setRequest(true);
      //ppr = new PushProfileRequestImpl(msg);
    	DiameterAvp[] avps = new DiameterAvp[0];

		if (session != null) {
			try {
				DiameterAvp sessionIdAvp = null;
				sessionIdAvp = baseAvpFactory
						.createAvp(0, DiameterAvpCodes.SESSION_ID, session
								.getSessionId());
				avps = new DiameterAvp[] { sessionIdAvp };
			} catch (NoSuchAvpException e) {
				logger
						.error(
								"Unexpected failure trying to create Session-Id AVP.",
								e);
			}
		}
		ppr =   (PushProfileRequest) createCxDxMessage(null, avps,
				PushProfileRequest.COMMAND_CODE, cxdxAppId);
    }
    catch (InternalException e) {
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
      //Message msg = stack.getSessionFactory().getNewRawSession().createMessage(RegistrationTerminationRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      //msg.setRequest(true);
      //rtr = new RegistrationTerminationRequestImpl(msg);
    	DiameterAvp[] avps = new DiameterAvp[0];

		if (session != null) {
			try {
				DiameterAvp sessionIdAvp = null;
				sessionIdAvp = baseAvpFactory
						.createAvp(0, DiameterAvpCodes.SESSION_ID, session
								.getSessionId());
				avps = new DiameterAvp[] { sessionIdAvp };
			} catch (NoSuchAvpException e) {
				logger
						.error(
								"Unexpected failure trying to create Session-Id AVP.",
								e);
			}
		}
		rtr =   (RegistrationTerminationRequest) createCxDxMessage(null, avps,
				RegistrationTerminationRequest.COMMAND_CODE, cxdxAppId);
    }
    catch (InternalException e) {
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
      //Message msg = stack.getSessionFactory().getNewRawSession().createMessage(ServerAssignmentRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
      //msg.setRequest(true);
      //sar = new ServerAssignmentRequestImpl(msg);
    	DiameterAvp[] avps = new DiameterAvp[0];

		if (session != null) {
			try {
				DiameterAvp sessionIdAvp = null;
				sessionIdAvp = baseAvpFactory
						.createAvp(0, DiameterAvpCodes.SESSION_ID, session
								.getSessionId());
				avps = new DiameterAvp[] { sessionIdAvp };
			} catch (NoSuchAvpException e) {
				logger
						.error(
								"Unexpected failure trying to create Session-Id AVP.",
								e);
			}
		}
		sar =   (ServerAssignmentRequest) createCxDxMessage(null, avps,
				ServerAssignmentRequest.COMMAND_CODE, cxdxAppId);
    }
    catch (InternalException e) {
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
      //Message msg = stack.getSessionFactory().getNewRawSession().createMessage(UserAuthorizationRequest.COMMAND_CODE, cxdxAppId, new Avp[]{});
     // msg.setRequest(true);
      //uar = new UserAuthorizationRequestImpl(msg);
    	DiameterAvp[] avps = new DiameterAvp[0];

		if (session != null) {
			try {
				DiameterAvp sessionIdAvp = null;
				sessionIdAvp = baseAvpFactory
						.createAvp(0, DiameterAvpCodes.SESSION_ID, session
								.getSessionId());
				avps = new DiameterAvp[] { sessionIdAvp };
			} catch (NoSuchAvpException e) {
				logger
						.error(
								"Unexpected failure trying to create Session-Id AVP.",
								e);
			}
		}
		uar =   (UserAuthorizationRequest) createCxDxMessage(null, avps,
				UserAuthorizationRequest.COMMAND_CODE, cxdxAppId);
    }
    catch (InternalException e) {
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
  DiameterMessage createCxDxMessage(DiameterHeader diameterHeader, DiameterAvp[] avps, int _commandCode, ApplicationId appId) throws  InternalException {

		boolean creatingRequest = diameterHeader == null;
		Message msg = null;

		if (!creatingRequest) {
			Message raw = createMessage(diameterHeader, avps, 0, appId);
			raw.setProxiable(true);
			raw.setRequest(false);
			msg = raw;
		}
		else {
			Message raw = createMessage(diameterHeader, avps, _commandCode, appId);
			raw.setProxiable(true);
			raw.setRequest(true);
			msg = raw;
		}
		
		if(msg == null)
		{
			throw new InternalException("Failed to create message!");
		}
		
		if (msg.getAvps().getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
			
			try{
				DiameterAvp avpVendorId = this.baseAvpFactory.createAvp(Avp.VENDOR_ID, _CXDX_VENDOR);
				DiameterAvp avpAcctApplicationId = this.baseAvpFactory.createAvp(Avp.AUTH_APPLICATION_ID, _CXFX_AUTH_APP_ID);
				DiameterAvp vendorSpecific = this.baseAvpFactory.createAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[] { avpVendorId, avpAcctApplicationId });
				msg.getAvps().addAvp(vendorSpecific.getCode(), vendorSpecific.byteArrayValue());
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		int commandCode = creatingRequest ? _commandCode : diameterHeader.getCommandCode();
		DiameterMessage diamMessage = null;

		switch (commandCode) {
		case LocationInfoRequest.COMMAND_CODE:
			diamMessage = creatingRequest ? new LocationInfoRequestImpl(msg) : new LocationInfoAnswerImpl(msg);
			break;
		case MultimediaAuthenticationRequest.COMMAND_CODE:
			diamMessage = creatingRequest ? new MultimediaAuthenticationRequestImpl(msg) : new MultimediaAuthenticationAnswerImpl(msg);
			break;
		case PushProfileRequest.COMMAND_CODE:
			diamMessage = creatingRequest ? new PushProfileRequestImpl(msg) : new PushProfileAnswerImpl(msg);
			break;
			
			
		case RegistrationTerminationRequest.COMMAND_CODE:
			diamMessage = creatingRequest ? new RegistrationTerminationRequestImpl(msg) : new RegistrationTerminationAnswerImpl(msg);
			break;
		case ServerAssignmentRequest.COMMAND_CODE:
			diamMessage = creatingRequest ? new ServerAssignmentRequestImpl(msg) : new ServerAssignmentAnswerImpl(msg);
			break;
		case UserAuthorizationRequest.COMMAND_CODE:
			diamMessage = creatingRequest ? new UserAuthorizationRequestImpl(msg) : new UserAuthorizationAnswerImpl(msg);
			break;
		
		default:
			diamMessage = new ExtensionDiameterMessageImpl(msg);
		}
		
		// Finally, add Origin-Host and Origin-Realm, if not present.
		addOriginHostAndRealm(diamMessage);
		if (!diamMessage.hasSessionId() && session != null) {
			diamMessage.setSessionId(session.getSessionId());
		}

		return diamMessage;
	}

}
