package org.mobicents.slee.resource.diameter.sh.server;

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
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.ProfileUpdateAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.PushNotificationRequestImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.UserDataAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.ProfileUpdateRequestImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.UserDataRequestImpl;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;

public class ShServerMessageFactoryImpl implements ShServerMessageFactory {

	protected Session session;
	protected Stack stack;
	protected DiameterMessageFactoryImpl baseFactory = null;
	protected DiameterAvpFactory baseAvpFactory = null;

	private static Logger logger = Logger.getLogger(ShServerMessageFactoryImpl.class);

	// Used for generating session id's
	protected static UIDGenerator uid = new UIDGenerator();

	public ShServerMessageFactoryImpl(Session session, Stack stack) {
		super();
		this.session = session;
		this.stack = stack;
		this.baseFactory = new DiameterMessageFactoryImpl(this.session, this.stack);
	}

	public ShServerMessageFactoryImpl(Stack stack) {
		super();
		this.stack = stack;
		this.baseFactory = new DiameterMessageFactoryImpl(this.stack);
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer(long resultCode, boolean isExperimentalResult) {
		ProfileUpdateAnswer pus = this.createProfileUpdateAnswer();
		if (isExperimentalResult) {
			pus.setExperimentalResult(this.baseAvpFactory.createExperimentalResult(_SH_VENDOR_ID, resultCode));
		} else {
			pus.setResultCode(resultCode);
		}

		return pus;
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer() {
		ApplicationId applicationId = ApplicationId.createByAccAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(ProfileUpdateRequest.commandCode, applicationId, null);
		ProfileUpdateAnswerImpl pua = new ProfileUpdateAnswerImpl(msg);
		return pua;
	}

	public PushNotificationRequest createPushNotificationRequest(UserIdentityAvp userIdentity, byte[] userData) {
		PushNotificationRequest pnr = this.createPushNotificationRequest();
		pnr.setUserIdentity(userIdentity);
		pnr.setUserData(userData);
		return pnr;
	}

	public PushNotificationRequest createPushNotificationRequest() {
		ApplicationId applicationId = ApplicationId.createByAccAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(PushNotificationRequest.commandCode, applicationId, null);
		PushNotificationRequestImpl pnr = new PushNotificationRequestImpl(msg);
		return pnr;
	}

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(long resultCode, boolean isExperimentalResult) {
		SubscribeNotificationsAnswer pus = this.createSubscribeNotificationsAnswer();
		if (isExperimentalResult) {
			pus.setExperimentalResult(this.baseAvpFactory.createExperimentalResult(_SH_VENDOR_ID, resultCode));
		} else {
			pus.setResultCode(resultCode);
		}

		return pus;
	}

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer() {
		ApplicationId applicationId = ApplicationId.createByAccAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(SubscribeNotificationsAnswer.commandCode, applicationId, null);
		SubscribeNotificationsAnswerImpl sna = new SubscribeNotificationsAnswerImpl(msg);
		return sna;
	}

	public UserDataAnswer createUserDataAnswer(byte[] userData) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserDataAnswer createUserDataAnswer(long resultCode, boolean isExperimentalResult) {
		UserDataAnswer uda = this.createUserDataAnswer();
		if (isExperimentalResult) {
			uda.setExperimentalResult(this.baseAvpFactory.createExperimentalResult(_SH_VENDOR_ID, resultCode));
		} else {
			uda.setResultCode(resultCode);
		}

		return uda;
	}

	public UserDataAnswer createUserDataAnswer() {
		ApplicationId applicationId = ApplicationId.createByAccAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(UserDataAnswer.commandCode, applicationId, null);
		UserDataAnswerImpl uda = new UserDataAnswerImpl(msg);
		return uda;
	}

	public DiameterMessageFactory getBaseMessageFactory() {
		return this.baseFactory;
	}

	// FIXME: This has been copied from base
	protected Message createMessage(int commandCode, ApplicationId applicationId, DiameterAvp[] avps) {
		Message msg = null;

		if (session == null) {
			try {
				msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
			} catch (InternalException e) {
				// TODO Auto-generated catch block
				logger.error("", e);
			} catch (IllegalDiameterStateException e) {
				// TODO Auto-generated catch block
				logger.error("", e);
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
		if (msg.getAvps().getAvp(Avp.SESSION_ID) == null)
			msg.getAvps().addAvp(Avp.SESSION_ID, this.baseFactory.generateSessionId(), true, false, false);

		return msg;
	}

	private void addAvp(DiameterAvp avp, AvpSet set) {
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

}
