/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.sh.client;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

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
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.ProfileUpdateRequestImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.PushNotificationAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequestImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.UserDataRequestImpl;
/**
 * 
 * Start time:16:43:52 2009-05-23<br>
 * Project: diameter-parent<br>
 * Implementation of sh client message factory.
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShClientMessageFactory
 */
public class ShClientMessageFactoryImpl implements ShClientMessageFactory {

	protected Session session;
	protected Stack stack;
	protected DiameterMessageFactoryImpl baseFactory = null;
	protected DiameterAvpFactory baseAvpFactory = null;
	
	private static Logger logger = Logger.getLogger(ShClientMessageFactoryImpl.class);

	// Used for generating session id's
	protected static UIDGenerator uid = new UIDGenerator();

	public ShClientMessageFactoryImpl(Session session, Stack stack) {
		super();
		this.session = session;
		this.stack = stack;
		this.baseFactory = new DiameterMessageFactoryImpl(this.session, this.stack);
		this.baseAvpFactory = new DiameterAvpFactoryImpl();
	}

	public ShClientMessageFactoryImpl(Stack stack) {
		super();
		this.stack = stack;
		this.baseFactory = new DiameterMessageFactoryImpl(this.stack);
    this.baseAvpFactory = new DiameterAvpFactoryImpl();
	}

	public ProfileUpdateRequest createProfileUpdateRequest(UserIdentityAvp userIdentity, DataReferenceType reference, byte[] userData) {
		// FIXME: baranowb: What should be used here?
		ProfileUpdateRequest pur = this.createProfileUpdateRequest();

		pur.setUserIdentity(userIdentity);
		pur.setDataReference(reference);
		pur.setUserData(userData);
		return pur;
	}

	public ProfileUpdateRequest createProfileUpdateRequest() {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(ProfileUpdateRequest.commandCode, applicationId, null);
		msg.setRequest(true);
		ProfileUpdateRequestImpl pur = new ProfileUpdateRequestImpl(msg);
		
		return pur;
	}

	public PushNotificationAnswer createPushNotificationAnswer(long resultCode, boolean isExperimentalResultCode) {

	  PushNotificationAnswer pna = this.createPushNotificationAnswer();

    if (isExperimentalResultCode)
    {
      pna.setExperimentalResult(this.baseAvpFactory.createExperimentalResult(0, resultCode));
    }
    else
    {
      pna.setResultCode(resultCode);
    }

    return pna;
	}

	public PushNotificationAnswer createPushNotificationAnswer() {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(PushNotificationAnswer.commandCode, applicationId, null);
		msg.setRequest(false);
		PushNotificationAnswerImpl pns = new PushNotificationAnswerImpl(msg);
		return pns;
	}

	public SubscribeNotificationsRequest createSubscribeNotificationsRequest(UserIdentityAvp userIdentity, DataReferenceType reference, SubsReqType subscriptionType) {
		SubscribeNotificationsRequest snr=this.createSubscribeNotificationsRequest();
		snr.setUserIdentity(userIdentity);
		snr.setDataReference(reference);
		snr.setSubsReqType(subscriptionType);
		return snr;
	}

	public SubscribeNotificationsRequest createSubscribeNotificationsRequest() {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(SubscribeNotificationsRequest.commandCode, applicationId, null);
		msg.setRequest(true);
		SubscribeNotificationsRequestImpl snr = new SubscribeNotificationsRequestImpl(msg);
		return snr;
	}

	public UserDataRequest createUserDataRequest(UserIdentityAvp userIdentity, DataReferenceType reference) {
		UserDataRequest udr = this.createUserDataRequest();
		udr.setUserIdentity(userIdentity);
		udr.setDataReference(reference);
		return udr;
	}

	public UserDataRequest createUserDataRequest() {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		Message msg = createMessage(UserDataRequest.commandCode, applicationId, null);
		msg.setRequest(true);
		UserDataRequestImpl udr = new UserDataRequestImpl(msg);
		return udr;
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
			if(avps!=null)
				for (DiameterAvp avp : avps) {
					if (avp.getCode() == Avp.DESTINATION_REALM)
						destRealm = avp.octetStringValue();
					else if (avp.getCode() == Avp.DESTINATION_HOST)
						destHost = avp.octetStringValue();
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

    msg.setProxiable( true );

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

	//public DiameterMessageFactory getBaseMessageFactory() {
	//	return this.baseFactory;
	//}
}
