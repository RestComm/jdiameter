package org.mobicents.slee.resource.diameter.base;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.UIDGenerator;
import org.jdiameter.client.impl.parser.MessageImpl;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationRequestImpl;

/**
 * Diameter Base Message Factory
 * 
 * <br>
 * Super project: mobicents <br>
 * 6:52:13 PM May 9, 2008 <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Erick Svenson
 */
public class DiameterMessageFactoryImpl implements DiameterMessageFactory {

	private static Logger logger = Logger.getLogger(DiameterMessageFactoryImpl.class);

	// Used for generating session id's
	public static final UIDGenerator uid = new UIDGenerator();

	protected Session session;
	protected Stack stack;

	public DiameterMessageFactoryImpl(Session session, Stack stack, DiameterIdentity... avps) {
		this.session = session;
		this.stack = stack;
	}

	public DiameterMessageFactoryImpl(Stack stack) {
		this.stack = stack;
	}

	public AbortSessionAnswer createAbortSessionAnswer(AbortSessionRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		//Message msg = this.createMessage(Message.ABORT_SESSION_ANSWER, avps);
		//createMessage
		//msg.setRequest(false);
		AbortSessionAnswer msg = (AbortSessionAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.ABORT_SESSION_ANSWER, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public AbortSessionAnswer createAbortSessionAnswer(AbortSessionRequest request) {
		try {
			return createAbortSessionAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error(e);
			return null;
		}
	}

	public AbortSessionRequest createAbortSessionRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(null, avps,Message.ABORT_SESSION_REQUEST,ApplicationId.createByAccAppId(0, 0));
		msg.setRequest(true);

		AbortSessionRequest asr = new AbortSessionRequestImpl(msg);

		return asr;
	}

	public AbortSessionRequest createAbortSessionRequest() {
		try {
			return createAbortSessionRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error(e);
			return null;
		}
	}

	public AccountingAnswer createAccountingAnswer(AccountingRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		AccountingAnswer msg = (AccountingAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.ACCOUNTING_ANSWER, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;

	}

	public AccountingAnswer createAccountingAnswer(AccountingRequest request) {
		try {
			return createAccountingAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public AccountingRequest createAccountingRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId( 193, 19302 );

		AccountingRequestImpl msg = (AccountingRequestImpl) this.createDiameterMessage(null, avps, Message.ACCOUNTING_REQUEST, ApplicationId.createByAccAppId( 0, 0 ));
		
		if(!msg.hasSessionId() && session!=null)
		{
			msg.setSessionId(session.getSessionId());
		}

		

		return msg;
	}

	public AccountingRequest createAccountingRequest() {
		try {
			return createAccountingRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer(CapabilitiesExchangeRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		CapabilitiesExchangeAnswer msg = (CapabilitiesExchangeAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.CAPABILITIES_EXCHANGE_ANSWER, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;

	}

	public CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer(CapabilitiesExchangeRequest request) {
		try {
			return createCapabilitiesExchangeAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public CapabilitiesExchangeRequest createCapabilitiesExchangeRequest(DiameterAvp[] avps) throws AvpNotAllowedException {

		

		CapabilitiesExchangeRequest msg = (CapabilitiesExchangeRequest) this.createDiameterMessage(null, avps, Message.CAPABILITIES_EXCHANGE_REQUEST, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;

	}

	public CapabilitiesExchangeRequest createCapabilitiesExchangeRequest() {
		try {
			return createCapabilitiesExchangeRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DeviceWatchdogAnswer createDeviceWatchdogAnswer(DeviceWatchdogRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		DeviceWatchdogAnswer msg = (DeviceWatchdogAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.DEVICE_WATCHDOG_ANSWER, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public DeviceWatchdogAnswer createDeviceWatchdogAnswer(DeviceWatchdogRequest request) {
		try {
			return createDeviceWatchdogAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DeviceWatchdogRequest createDeviceWatchdogRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		DeviceWatchdogRequest msg = (DeviceWatchdogRequest) this.createDiameterMessage(null, avps, Message.DEVICE_WATCHDOG_REQUEST, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public DeviceWatchdogRequest createDeviceWatchdogRequest() {
		try {
			return createDeviceWatchdogRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DisconnectPeerAnswer createDisconnectPeerAnswer(DisconnectPeerRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		DisconnectPeerAnswer msg = (DisconnectPeerAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.DISCONNECT_PEER_ANSWER, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public DisconnectPeerAnswer createDisconnectPeerAnswer(DisconnectPeerRequest request) {
		try {
			return createDisconnectPeerAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DisconnectPeerRequest createDisconnectPeerRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		DisconnectPeerRequest msg = (DisconnectPeerRequest) this.createDiameterMessage(null, avps, Message.DISCONNECT_PEER_REQUEST, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public DisconnectPeerRequest createDisconnectPeerRequest() {
		try {
			return createDisconnectPeerRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public ExtensionDiameterMessage createMessage(DiameterCommand command, DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );
		ExtensionDiameterMessageImpl msg = (ExtensionDiameterMessageImpl) this.createDiameterMessage(null, avps, command.getCode(), ApplicationId.createByAccAppId(0, command.getApplicationId()));
		msg.getGenericData().setRequest(command.isRequest());
		((MessageImpl)msg.getGenericData()).setProxiable(command.isProxiable());

		
		return msg;
	}

//	protected Message createRawMessage(DiameterHeader header)
//	{
//		int commandCode = header.getCommandCode();
//		long endToEndId = header.getEndToEndId();
//		long hopByHopId = header.getHopByHopId();
//		ApplicationId aid = ApplicationId.createByAccAppId(header.getApplicationId());
//		try {
//			Message msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, aid, hopByHopId, endToEndId);
//			return msg;
//		} catch (InternalException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalDiameterStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	public DiameterMessage createMessage(DiameterHeader header, DiameterAvp[] avps) throws AvpNotAllowedException {
//		int commandCode = header.getCommandCode();
//	
//
//		try {
//			Message msg =createRawMessage(header);
//
//			for (DiameterAvp avp : avps)
//				msg.getAvps().addAvp(avp.getCode(), avp.byteArrayValue());
//
//			DiameterMessage diamMessage = null;
//
//			switch (commandCode) {
//			case Message.ABORT_SESSION_REQUEST:
//				diamMessage = header.isRequest() ? new AbortSessionRequestImpl(msg) : new AbortSessionAnswerImpl(msg);
//				break;
//			case Message.ACCOUNTING_REQUEST:
//				diamMessage = header.isRequest() ? new AccountingRequestImpl(msg) : new AccountingAnswerImpl(msg);
//				break;
//			case Message.CAPABILITIES_EXCHANGE_REQUEST:
//				diamMessage = header.isRequest() ? new CapabilitiesExchangeRequestImpl(msg) : new CapabilitiesExchangeAnswerImpl(msg);
//				break;
//			case Message.DEVICE_WATCHDOG_REQUEST:
//				diamMessage = header.isRequest() ? new DeviceWatchdogRequestImpl(msg) : new DeviceWatchdogAnswerImpl(msg);
//				break;
//			case Message.DISCONNECT_PEER_REQUEST:
//				diamMessage = header.isRequest() ? new DisconnectPeerRequestImpl(msg) : new DisconnectPeerAnswerImpl(msg);
//				break;
//			case Message.RE_AUTH_REQUEST:
//				diamMessage = header.isRequest() ? new ReAuthRequestImpl(msg) : new ReAuthAnswerImpl(msg);
//				break;
//			case Message.SESSION_TERMINATION_REQUEST:
//				diamMessage = header.isRequest() ? new SessionTerminationRequestImpl(msg) : new SessionTerminationAnswerImpl(msg);
//				break;
//			default:
//				diamMessage = new ExtensionDiameterMessageImpl(msg);
//			}
//
//			return diamMessage;
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//
//		return null;
//	}

	public ReAuthAnswer createReAuthAnswer(ReAuthRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		ReAuthAnswer msg = (ReAuthAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.RE_AUTH_ANSWER, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public ReAuthAnswer createReAuthAnswer(ReAuthRequest request) {
		try {
			return createReAuthAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public ReAuthRequest createReAuthRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		ReAuthRequest msg = (ReAuthRequest) this.createDiameterMessage(null, avps, Message.RE_AUTH_REQUEST, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public ReAuthRequest createReAuthRequest() {
		try {
			return createReAuthRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public SessionTerminationAnswer createSessionTerminationAnswer(SessionTerminationRequest request,DiameterAvp[] avps) throws AvpNotAllowedException {
		SessionTerminationAnswer msg = (SessionTerminationAnswer) this.createDiameterMessage(request.getHeader(), avps, Message.SESSION_TERMINATION_REQUEST, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public SessionTerminationAnswer createSessionTerminationAnswer(SessionTerminationRequest request) {
		try {
			return createSessionTerminationAnswer(request);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public SessionTerminationRequest createSessionTerminationRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		SessionTerminationRequest msg = (SessionTerminationRequest) this.createDiameterMessage(null, avps, Message.SESSION_TERMINATION_REQUEST, ApplicationId.createByAccAppId(0, 0));

		if (!msg.hasSessionId() && session != null) {
			msg.setSessionId(session.getSessionId());
		}

		return msg;
	}

	public SessionTerminationRequest createSessionTerminationRequest() {
		try {
			return createSessionTerminationRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	// ========== AUX STUFF ==========

	public String generateSessionId() {
		String host = stack.getMetaData().getLocalPeer().getUri().getFQDN();

		long id = uid.nextLong();
		long high32 = (id & 0xffffffff00000000L) >> 32;
		long low32 = (id & 0xffffffffL);

		return host + ";" + high32 + ";" + low32;
	}

//	protected Message createMessage(int commandCode, DiameterAvp[] avps) {
//		Message msg = null;
//
//		ApplicationId applicationId = null;
//
//		// Try to get Application-Id from Message AVPs
//		if (avps != null) {
//			for (DiameterAvp avp : avps) {
//				if (avp.getCode() == Avp.ACCT_APPLICATION_ID) {
//					applicationId = ApplicationId.createByAccAppId(avp.getVendorId(), avp.longValue());
//					break;
//				} else if (avp.getCode() == Avp.AUTH_APPLICATION_ID) {
//					applicationId = ApplicationId.createByAuthAppId(avp.getVendorId(), avp.longValue());
//					break;
//				}
//			}
//		}
//
//		if (applicationId == null) {
//			applicationId = ApplicationId.createByAccAppId(ApplicationId.Standard.DIAMETER_COMMON_MESSAGE);
//		}
//
//		if (session == null) {
//			try { // FIXME: baranowb: This should create activity, shouldnt it?
//				// Alex this has to be cleared
//				msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
//			} catch (Exception e) {
//				logger.error("Failure creating jDiameter message.", e);
//			}
//		} else {
//			String destRealm = null;
//			String destHost = null;
//
//			for (DiameterAvp avp : avps) {
//				if (avp.getCode() == Avp.DESTINATION_REALM)
//					destRealm = avp.stringValue();
//				else if (avp.getCode() == Avp.DESTINATION_HOST)
//					destHost = avp.stringValue();
//			}
//
//			msg = destHost == null ? session.createRequest(commandCode, applicationId, destRealm) : session.createRequest(commandCode, applicationId, destRealm, destHost);
//		}
//
//		if (avps != null) {
//			for (DiameterAvp avp : avps) {
//				addAvp(avp, msg.getAvps());
//			}
//		}
//
//		// Do we have a session-id already or shall we make one?
//		if (msg.getAvps().getAvp(Avp.SESSION_ID) == null) {
//			msg.getAvps().addAvp(Avp.SESSION_ID, generateSessionId(), true, false, false);
//		}
//
//		return msg;
//	}
//
//	protected void addAvp(DiameterAvp avp, AvpSet set) {
//		// FIXME: alexandre: Should we look at the types and add them with
//		// proper function?
//		if (avp instanceof GroupedAvp) {
//			AvpSet avpSet = set.addGroupedAvp(avp.getCode(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
//
//			DiameterAvp[] groupedAVPs = ((GroupedAvp) avp).getExtensionAvps();
//			for (DiameterAvp avpFromGroup : groupedAVPs) {
//				addAvp(avpFromGroup, avpSet);
//			}
//		} else if (avp != null)
//			set.addAvp(avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
//	}
//	
	
	
	
	protected DiameterMessage createDiameterMessage(DiameterHeader diameterHeader, DiameterAvp[] avps, int _commandCode, ApplicationId appId) throws IllegalArgumentException {

		// List<DiameterAvp> list = (List<DiameterAvp>) this.avpList.clone();
		boolean isRequest = diameterHeader == null || diameterHeader.isRequest();
		Message msg = null;
		if (!isRequest) {
			Message raw = createMessage(diameterHeader, avps, 0, null);
			raw.setProxiable(true);
			raw.setRequest(false);
			msg = raw;

		} else {

			Message raw = createMessage(diameterHeader, avps, _commandCode, appId);
			raw.setProxiable(true);
			raw.setRequest(true);
			msg = raw;
		}
		// now now we msut add VendorSpecific?
		int commandCode = isRequest ? diameterHeader.getCommandCode() : _commandCode;
		DiameterMessage diamMessage = null;

		switch (commandCode) {
		case Message.ABORT_SESSION_REQUEST:
			diamMessage = isRequest ? new AbortSessionRequestImpl(msg) : new AbortSessionAnswerImpl(msg);
			break;
		case Message.ACCOUNTING_REQUEST:
			diamMessage = isRequest ? new AccountingRequestImpl(msg) : new AccountingAnswerImpl(msg);
			break;
		case Message.CAPABILITIES_EXCHANGE_REQUEST:
			diamMessage = isRequest ? new CapabilitiesExchangeRequestImpl(msg) : new CapabilitiesExchangeAnswerImpl(msg);
			break;
		case Message.DEVICE_WATCHDOG_REQUEST:
			diamMessage = isRequest ? new DeviceWatchdogRequestImpl(msg) : new DeviceWatchdogAnswerImpl(msg);
			break;
		case Message.DISCONNECT_PEER_REQUEST:
			diamMessage = isRequest ? new DisconnectPeerRequestImpl(msg) : new DisconnectPeerAnswerImpl(msg);
			break;
		case Message.RE_AUTH_REQUEST:
			diamMessage = isRequest ? new ReAuthRequestImpl(msg) : new ReAuthAnswerImpl(msg);
			break;
		case Message.SESSION_TERMINATION_REQUEST:
			diamMessage = isRequest ? new SessionTerminationRequestImpl(msg) : new SessionTerminationAnswerImpl(msg);
			break;
		default:
			diamMessage = new ExtensionDiameterMessageImpl(msg);
		}

		return diamMessage;
	}

	protected Message createMessage(DiameterHeader header, DiameterAvp[] avps,int _commandCode,ApplicationId appId) throws AvpNotAllowedException {

		try {
			Message msg = createRawMessage(header,  _commandCode,appId);
			AvpSet set = msg.getAvps();
			for (DiameterAvp avp : avps)
				addAvp(avp, set);
				

		
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Message createRawMessage(DiameterHeader header, int _commandCode,ApplicationId appId) {

		int commandCode = 0;
		long endToEndId = 0;
		long hopByHopId = 0;
		ApplicationId aid = null;
		if (header != null) {
			commandCode = header.getCommandCode();
			endToEndId = header.getEndToEndId();
			hopByHopId = header.getHopByHopId();
			aid = ApplicationId.createByAuthAppId(header.getApplicationId());
		} else {
			//FIXME: This is the only one ;[
			commandCode = _commandCode;
			endToEndId = (long) (Math.random()*1000000);
			hopByHopId = (long) (Math.random()*1000000)+1;
			aid = appId == null? ApplicationId.createByAuthAppId(0,0):appId;
		}
		try {
			Message msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, aid, hopByHopId, endToEndId);
			return msg;
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	protected void addAvp(DiameterAvp avp, AvpSet set) {
		// FIXME: alexandre: Should we look at the types and add them with
		// proper function?
		if (avp instanceof GroupedAvp) {
			AvpSet avpSet = set.addGroupedAvp(avp.getCode(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);

			DiameterAvp[] groupedAVPs = ((GroupedAvp) avp).getExtensionAvps();
			for (DiameterAvp avpFromGroup : groupedAVPs) {
				addAvp(avpFromGroup, avpSet);
			}
		} else if (avp != null)
			set.addAvp(avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
	}

	
	

	/**
	 * 
	 */
	public void clean() {
		this.session = null;

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.base.DiameterMessageFactory#createMessage(net.java.slee.resource.diameter.base.events.DiameterHeader, net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
	 */
	public DiameterMessage createMessage(DiameterHeader header, DiameterAvp[] avps) throws AvpNotAllowedException {
		return this.createDiameterMessage(header, avps, header.getCommandCode(), ApplicationId.createByAccAppId(0,header.getApplicationId()));
	}

	

	// ################
	// # PROVISIONING #
	// ################

}
