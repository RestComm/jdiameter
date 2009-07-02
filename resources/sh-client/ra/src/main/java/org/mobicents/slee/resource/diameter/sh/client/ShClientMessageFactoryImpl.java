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
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
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
		ProfileUpdateRequest pur = this.createProfileUpdateRequest();

		pur.setUserIdentity(userIdentity);
		pur.setDataReference(reference);
		pur.setUserData(new String(userData));
		return pur;
	}

	public ProfileUpdateRequest createProfileUpdateRequest() {
		//ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		//Message msg = createMessage(ProfileUpdateRequest.commandCode, applicationId, null);
		//msg.setRequest(true);
		DiameterAvp[] avps = new DiameterAvp[0];
		
		if(session != null) {
  		try {
  			DiameterAvp sessionIdAvp = null;
  			sessionIdAvp = baseAvpFactory.createAvp(0, DiameterAvpCodes.SESSION_ID, session.getSessionId());
  			avps = new DiameterAvp[]{sessionIdAvp};
  		}
  		catch (NoSuchAvpException e) {
  		  logger.error( "Unexpected failure trying to create Session-Id AVP.", e );
  		}
		}
		
		Message msg = createShMessage(null, avps, ProfileUpdateRequest.commandCode);
		ProfileUpdateRequestImpl pur = new ProfileUpdateRequestImpl(msg);
		addOrigin(pur);
		
		return pur;
	}

	public PushNotificationAnswer createPushNotificationAnswer(PushNotificationRequest request,long resultCode, boolean isExperimentalResultCode) {

	  PushNotificationAnswer pna = this.createPushNotificationAnswer(request);

    if (isExperimentalResultCode) {
      pna.setExperimentalResult(this.baseAvpFactory.createExperimentalResult(0, resultCode));
    }
    else {
      pna.setResultCode(resultCode);
    }

    return pna;
	}

	public PushNotificationAnswer createPushNotificationAnswer(PushNotificationRequest request) {
		//ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		//Message msg = createMessage(PushNotificationAnswer.commandCode, applicationId, null);
		//msg.setRequest(false);
		DiameterAvp[] avps = new DiameterAvp[0];
		
		try {
			DiameterAvp sessionIdAvp = null;
			sessionIdAvp = baseAvpFactory.createAvp(0, DiameterAvpCodes.SESSION_ID, request.getSessionId());
			avps = new DiameterAvp[]{sessionIdAvp};
		}
		catch (NoSuchAvpException e) {
      logger.error( "Unexpected failure trying to create Session-Id AVP.", e );
		}

		Message msg = createShMessage(request.getHeader(), avps, PushNotificationAnswer.commandCode);
		PushNotificationAnswerImpl pna = new PushNotificationAnswerImpl(msg);
		addOrigin(pna);
		return pna;
	}

	public SubscribeNotificationsRequest createSubscribeNotificationsRequest(UserIdentityAvp userIdentity, DataReferenceType reference, SubsReqType subscriptionType) {
		SubscribeNotificationsRequest snr=this.createSubscribeNotificationsRequest();
		snr.setUserIdentity(userIdentity);
		snr.setDataReference(reference);
		snr.setSubsReqType(subscriptionType);
		return snr;
	}

	public SubscribeNotificationsRequest createSubscribeNotificationsRequest() {
		//ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		//Message msg = createMessage(SubscribeNotificationsRequest.commandCode, applicationId, null);
		//msg.setRequest(true);
		DiameterAvp[] avps = new DiameterAvp[0];
		if(session != null) {
  		try {
  			DiameterAvp sessionIdAvp = null;
  			sessionIdAvp = baseAvpFactory.createAvp(0, DiameterAvpCodes.SESSION_ID, session.getSessionId());
  			avps = new DiameterAvp[]{sessionIdAvp};
  		}
  		catch (NoSuchAvpException e) {
        logger.error( "Unexpected failure trying to create Session-Id AVP.", e );
  		}
		}

		Message msg = createShMessage(null, avps, SubscribeNotificationsRequest.commandCode);
		SubscribeNotificationsRequestImpl snr = new SubscribeNotificationsRequestImpl(msg);
		addOrigin(snr);
		return snr;
	}

	public UserDataRequest createUserDataRequest(UserIdentityAvp userIdentity, DataReferenceType reference) {
		UserDataRequest udr = this.createUserDataRequest();
		udr.setUserIdentity(userIdentity);
		udr.setDataReference(reference);
		return udr;
	}

	public UserDataRequest createUserDataRequest() {
//		ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
//		Message msg = createMessage(UserDataRequest.commandCode, applicationId, null);
//		msg.setRequest(true);
		
		DiameterAvp[] avps = new DiameterAvp[0];
		
		if(session!=null) {
  		try {
  			DiameterAvp sessionIdAvp = null;
  			sessionIdAvp = baseAvpFactory.createAvp(0, DiameterAvpCodes.SESSION_ID, session.getSessionId());
  			avps = new DiameterAvp[]{sessionIdAvp};
  		}
  		catch (NoSuchAvpException e) {
        logger.error( "Unexpected failure trying to create Session-Id AVP.", e );
  		}
		}
		
		Message msg = createShMessage(null, avps, UserDataRequest.commandCode);
		UserDataRequestImpl udr = new UserDataRequestImpl(msg);
		addOrigin(udr);
		return udr;
	}

	

//	// FIXME: This has been copied from base
//	protected Message createMessage(int commandCode, ApplicationId applicationId, DiameterAvp[] avps) {
//		Message msg = null;
//
//		if (session == null) {
//			try {
//				msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
//			} catch (InternalException e) {
//				// TODO Auto-generated catch block
//				logger.error("", e);
//			} catch (IllegalDiameterStateException e) {
//				// TODO Auto-generated catch block
//				logger.error("", e);
//			}
//		} else {
//			String destRealm = null;
//			String destHost = null;
//			if(avps!=null)
//				for (DiameterAvp avp : avps) {
//					if (avp.getCode() == Avp.DESTINATION_REALM)
//						destRealm = avp.octetStringValue();
//					else if (avp.getCode() == Avp.DESTINATION_HOST)
//						destHost = avp.octetStringValue();
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
//		if (msg.getAvps().getAvp(Avp.SESSION_ID) == null)
//			msg.getAvps().addAvp(Avp.SESSION_ID, this.baseFactory.generateSessionId(), true, false, false);
//
//    msg.setProxiable( true );
//
//    return msg;
//	}

	private Message createShMessage(DiameterHeader diameterHeader, DiameterAvp[] avps, int _commandCode) throws IllegalArgumentException {

		// List<DiameterAvp> list = (List<DiameterAvp>) this.avpList.clone();
		boolean isRequest = diameterHeader == null;
		Message msg = null;
		
		if (!isRequest) {
			Message raw = createMessage(diameterHeader, avps,0);
			raw.setProxiable(true);
			raw.setRequest(false);
			msg = raw;
		}
		else {
			Message raw = createMessage(null, avps,_commandCode);
			raw.setProxiable(true);
			raw.setRequest(true);
			msg = raw;
		}
		// now now we msut add VendorSpecific?

		return msg;
	}

	protected Message createMessage(DiameterHeader header, DiameterAvp[] avps, int commandCode) throws AvpNotAllowedException {

		try {
			Message msg = createRawMessage(header,commandCode);
			AvpSet set = msg.getAvps();
			for (DiameterAvp avp : avps)
				addAvp(avp, set);

			if (msg.getAvps().getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
				DiameterAvp avpVendorId = this.baseAvpFactory.createAvp(Avp.VENDOR_ID, MessageFactory._SH_VENDOR_ID);
				DiameterAvp avpAcctApplicationId = this.baseAvpFactory.createAvp(Avp.ACCT_APPLICATION_ID, MessageFactory._SH_APP_ID);
				DiameterAvp vendorSpecific = this.baseAvpFactory.createAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[] { avpVendorId, avpAcctApplicationId });
				msg.getAvps().addAvp(vendorSpecific.getCode(), vendorSpecific.byteArrayValue());

			}
			
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Message createRawMessage(DiameterHeader header, int _commandCode) {

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
			commandCode = _commandCode;
			//endToEndId = (long) (Math.random()*1000000);
			//hopByHopId = (long) (Math.random()*1000000)+1;
			aid = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		}
		try {
			if(header!=null)
				return stack.getSessionFactory().getNewRawSession().createMessage(commandCode, aid, hopByHopId, endToEndId);
			else
				return stack.getSessionFactory().getNewRawSession().createMessage(commandCode, aid);
			
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
	
	
	//public DiameterMessageFactory getBaseMessageFactory() {
	//	return this.baseFactory;
	//}
	private void addOrigin(DiameterMessage msg)
	{
		if(!msg.hasOriginHost())
			msg.setOriginHost(new DiameterIdentity(stack.getMetaData().getLocalPeer().getUri().getFQDN().toString()));
		if(!msg.hasOriginRealm())
			msg.setOriginRealm(new DiameterIdentity(stack.getMetaData().getLocalPeer().getRealmName()));
	}
}
