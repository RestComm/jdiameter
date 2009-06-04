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

	public AbortSessionAnswer createAbortSessionAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		Message msg = this.createMessage(Message.ABORT_SESSION_ANSWER, avps);
		msg.setRequest(false);

		return new AbortSessionAnswerImpl(msg);
	}

	public AbortSessionAnswer createAbortSessionAnswer() {
		try {
			return createAbortSessionAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error(e);
			return null;
		}
	}

	public AbortSessionRequest createAbortSessionRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.ABORT_SESSION_REQUEST, avps);
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

	public AccountingAnswer createAccountingAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.ACCOUNTING_ANSWER, avps);

		msg.setRequest(false);

		AccountingAnswer aca = new AccountingAnswerImpl(msg);

		return aca;
	}

	public AccountingAnswer createAccountingAnswer() {
		try {
			return createAccountingAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public AccountingRequest createAccountingRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId( 193, 19302 );

		Message msg = this.createMessage(Message.ACCOUNTING_REQUEST, avps);
		msg.setRequest(true);

		AccountingRequest acr = new AccountingRequestImpl(msg);

		return acr;
	}

	public AccountingRequest createAccountingRequest() {
		try {
			return createAccountingRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.CAPABILITIES_EXCHANGE_ANSWER, avps);

		CapabilitiesExchangeAnswer cea = new CapabilitiesExchangeAnswerImpl(msg);

		return cea;
	}

	public CapabilitiesExchangeAnswer createCapabilitiesExchangeAnswer() {
		try {
			return createCapabilitiesExchangeAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public CapabilitiesExchangeRequest createCapabilitiesExchangeRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.CAPABILITIES_EXCHANGE_REQUEST, avps);
		msg.setRequest(true);

		CapabilitiesExchangeRequest cer = new CapabilitiesExchangeRequestImpl(msg);

		return cer;
	}

	public CapabilitiesExchangeRequest createCapabilitiesExchangeRequest() {
		try {
			return createCapabilitiesExchangeRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DeviceWatchdogAnswer createDeviceWatchdogAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.DEVICE_WATCHDOG_ANSWER, avps);

		DeviceWatchdogAnswer dwa = new DeviceWatchdogAnswerImpl(msg);

		return dwa;
	}

	public DeviceWatchdogAnswer createDeviceWatchdogAnswer() {
		try {
			return createDeviceWatchdogAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DeviceWatchdogRequest createDeviceWatchdogRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.DEVICE_WATCHDOG_REQUEST, avps);
		msg.setRequest(true);

		DeviceWatchdogRequest dwr = new DeviceWatchdogRequestImpl(msg);

		return dwr;
	}

	public DeviceWatchdogRequest createDeviceWatchdogRequest() {
		try {
			return createDeviceWatchdogRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DisconnectPeerAnswer createDisconnectPeerAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.DISCONNECT_PEER_ANSWER, avps);

		DisconnectPeerAnswer dpa = new DisconnectPeerAnswerImpl(msg);

		return dpa;
	}

	public DisconnectPeerAnswer createDisconnectPeerAnswer() {
		try {
			return createDisconnectPeerAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public DisconnectPeerRequest createDisconnectPeerRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.DISCONNECT_PEER_REQUEST, avps);
		msg.setRequest(true);

		DisconnectPeerRequest dpr = new DisconnectPeerRequestImpl(msg);

		return dpr;
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

		return new ExtensionDiameterMessageImpl(this.createMessage(command.getCode(), null));
	}

	protected Message createRawMessage(DiameterHeader header)
	{
		int commandCode = header.getCommandCode();
		long endToEndId = header.getEndToEndId();
		long hopByHopId = header.getHopByHopId();
		ApplicationId aid = ApplicationId.createByAccAppId(header.getApplicationId());
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
	
	public DiameterMessage createMessage(DiameterHeader header, DiameterAvp[] avps) throws AvpNotAllowedException {
		int commandCode = header.getCommandCode();
	

		try {
			Message msg =createRawMessage(header);

			for (DiameterAvp avp : avps)
				msg.getAvps().addAvp(avp.getCode(), avp.byteArrayValue());

			DiameterMessage diamMessage = null;

			switch (commandCode) {
			case Message.ABORT_SESSION_REQUEST:
				diamMessage = header.isRequest() ? new AbortSessionRequestImpl(msg) : new AbortSessionAnswerImpl(msg);
				break;
			case Message.ACCOUNTING_REQUEST:
				diamMessage = header.isRequest() ? new AccountingRequestImpl(msg) : new AccountingAnswerImpl(msg);
				break;
			case Message.CAPABILITIES_EXCHANGE_REQUEST:
				diamMessage = header.isRequest() ? new CapabilitiesExchangeRequestImpl(msg) : new CapabilitiesExchangeAnswerImpl(msg);
				break;
			case Message.DEVICE_WATCHDOG_REQUEST:
				diamMessage = header.isRequest() ? new DeviceWatchdogRequestImpl(msg) : new DeviceWatchdogAnswerImpl(msg);
				break;
			case Message.DISCONNECT_PEER_REQUEST:
				diamMessage = header.isRequest() ? new DisconnectPeerRequestImpl(msg) : new DisconnectPeerAnswerImpl(msg);
				break;
			case Message.RE_AUTH_REQUEST:
				diamMessage = header.isRequest() ? new ReAuthRequestImpl(msg) : new ReAuthAnswerImpl(msg);
				break;
			case Message.SESSION_TERMINATION_REQUEST:
				diamMessage = header.isRequest() ? new SessionTerminationRequestImpl(msg) : new SessionTerminationAnswerImpl(msg);
				break;
			default:
				diamMessage = new ExtensionDiameterMessageImpl(msg);
			}

			return diamMessage;
		} catch (Exception e) {
			logger.error("", e);
		}

		return null;
	}

	public ReAuthAnswer createReAuthAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.RE_AUTH_ANSWER, avps);

		ReAuthAnswer raa = new ReAuthAnswerImpl(msg);

		return raa;
	}

	public ReAuthAnswer createReAuthAnswer() {
		try {
			return createReAuthAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public ReAuthRequest createReAuthRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.RE_AUTH_REQUEST, avps);
		msg.setRequest(true);

		ReAuthRequest rar = new ReAuthRequestImpl(msg);

		return rar;
	}

	public ReAuthRequest createReAuthRequest() {
		try {
			return createReAuthRequest(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public SessionTerminationAnswer createSessionTerminationAnswer(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.SESSION_TERMINATION_ANSWER, avps);

		SessionTerminationAnswer sta = new SessionTerminationAnswerImpl(msg);

		return sta;
	}

	public SessionTerminationAnswer createSessionTerminationAnswer() {
		try {
			return createSessionTerminationAnswer(null);
		} catch (AvpNotAllowedException e) {
			logger.error("", e);
			return null;
		}
	}

	public SessionTerminationRequest createSessionTerminationRequest(DiameterAvp[] avps) throws AvpNotAllowedException {
		// FIXME: alexandre: What should be used here?
		// ApplicationId aid = ApplicationId.createByAccAppId(
		// ApplicationId.Standard.DIAMETER_COMMON_MESSAGE );

		Message msg = this.createMessage(Message.SESSION_TERMINATION_REQUEST, avps);
		msg.setRequest(true);

		SessionTerminationRequest str = new SessionTerminationRequestImpl(msg);

		return str;
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

	protected Message createMessage(int commandCode, DiameterAvp[] avps) {
		Message msg = null;

		ApplicationId applicationId = null;

		// Try to get Application-Id from Message AVPs
		if (avps != null) {
			for (DiameterAvp avp : avps) {
				if (avp.getCode() == Avp.ACCT_APPLICATION_ID) {
					applicationId = ApplicationId.createByAccAppId(avp.getVendorId(), avp.longValue());
					break;
				} else if (avp.getCode() == Avp.AUTH_APPLICATION_ID) {
					applicationId = ApplicationId.createByAuthAppId(avp.getVendorId(), avp.longValue());
					break;
				}
			}
		}

		if (applicationId == null) {
			applicationId = ApplicationId.createByAccAppId(ApplicationId.Standard.DIAMETER_COMMON_MESSAGE);
		}

		if (session == null) {
			try { // FIXME: baranowb: This should create activity, shouldnt it?
				// Alex this has to be cleared
				msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
			} catch (Exception e) {
				logger.error("Failure creating jDiameter message.", e);
			}
		} else {
			String destRealm = null;
			String destHost = null;

			for (DiameterAvp avp : avps) {
				if (avp.getCode() == Avp.DESTINATION_REALM)
					destRealm = avp.stringValue();
				else if (avp.getCode() == Avp.DESTINATION_HOST)
					destHost = avp.stringValue();
			}

			msg = destHost == null ? session.createRequest(commandCode, applicationId, destRealm) : session.createRequest(commandCode, applicationId, destRealm, destHost);
		}

		if (avps != null) {
			for (DiameterAvp avp : avps) {
				addAvp(avp, msg.getAvps());
			}
		}

		// Do we have a session-id already or shall we make one?
		if (msg.getAvps().getAvp(Avp.SESSION_ID) == null) {
			msg.getAvps().addAvp(Avp.SESSION_ID, generateSessionId(), true, false, false);
		}

		return msg;
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

	// ################
	// # PROVISIONING #
	// ################

}
