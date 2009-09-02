/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
package org.mobicents.slee.resource.diameter.sh.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
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
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.ProfileUpdateAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.PushNotificationRequestImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.UserDataAnswerImpl;

/**
 * 
 * Start time:16:56:16 2009-05-23<br>
 * Project: diameter-parent<br>
 * Implementation of Sh Server Message factory.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShServerMessageFactory
 */
public class ShServerMessageFactoryImpl implements ShServerMessageFactory {

	protected Session session;
	protected Stack stack;
	protected DiameterMessageFactoryImpl baseFactory = null;
	protected DiameterShAvpFactoryImpl localFactory = null;

	private static Logger logger = Logger.getLogger(ShServerMessageFactoryImpl.class);

	protected ArrayList<DiameterAvp> avpList = new ArrayList<DiameterAvp>();

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

	public ShServerMessageFactoryImpl(DiameterMessageFactoryImpl baseMsgFactory, Session session, Stack stack, DiameterShAvpFactory localFactory) {
		this.session = session;
		this.stack = stack;
		this.baseFactory = baseMsgFactory;
		this.localFactory = (DiameterShAvpFactoryImpl) localFactory;
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer(ProfileUpdateRequest request, long resultCode, boolean isExperimentalResult) {
		ProfileUpdateAnswer pua = this.createProfileUpdateAnswer(request);

		if (isExperimentalResult) {
			pua.setExperimentalResult(this.localFactory.getBaseFactory().createExperimentalResult(0, resultCode));
		}
		else {
			pua.setResultCode(resultCode);
		}

		return pua;
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer(ProfileUpdateRequest request) {
		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

		DiameterAvp sessionIdAvp = null;
		try {
			sessionIdAvp = localFactory.getBaseFactory().createAvp(0, DiameterAvpCodes.SESSION_ID, request.getSessionId());
			avps.add(sessionIdAvp);
		}
		catch (NoSuchAvpException e) {
			logger.error("Unable to create Session-Id AVP.", e);
		}

		if (request.getUserIdentity() != null) {
			avps.add(request.getUserIdentity());
		}

		Message msg = createShMessage(request.getHeader(), avps.toArray(new DiameterAvp[avps.size()]));
		ProfileUpdateAnswerImpl answer = new ProfileUpdateAnswerImpl(msg);

		if (request.hasRouteRecords()) {
			answer.setRouteRecords(request.getRouteRecords());
		}
		// add more :) ?

		//answer.setOriginHost(request.getOriginHost());
		addOrigin(answer);

		return answer;
	}

	public PushNotificationRequest createPushNotificationRequest(UserIdentityAvp userIdentity, byte[] userData) {
		PushNotificationRequest pnr = this.createPushNotificationRequest();

		pnr.setUserIdentity(userIdentity);
		pnr.setUserData(new String(userData));

		return pnr;
	}

	public PushNotificationRequest createPushNotificationRequest() {
		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

		if (session != null)
			try {
				DiameterAvp sessionIdAvp = null;
				sessionIdAvp = localFactory.getBaseFactory().createAvp(0, DiameterAvpCodes.SESSION_ID, session.getSessionId());
				avps.add(sessionIdAvp);
			}
		catch (NoSuchAvpException e) {
	      logger.error("Unable to create Session-Id AVP.", e);
			}

		Message msg = createShMessage(null, avps.toArray(new DiameterAvp[avps.size()]));
		PushNotificationRequestImpl request = new PushNotificationRequestImpl(msg);
		addOrigin(request);
		
		return request;
	}

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(SubscribeNotificationsRequest request, long resultCode, boolean isExperimentalResult) {
		SubscribeNotificationsAnswer sna = this.createSubscribeNotificationsAnswer(request);

		if (isExperimentalResult) {
			sna.setExperimentalResult(this.localFactory.getBaseFactory().createExperimentalResult(0, resultCode));
		}
		else {
			sna.setResultCode(resultCode);
		}

		return sna;
	}

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(SubscribeNotificationsRequest request) {
		// Message msg = createShMessage(UserDataAnswer.commandCode, session !=
		// null ? session.getSessionId() : null, false);

		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

		DiameterAvp sessionIdAvp = null;
		try {
			sessionIdAvp = localFactory.getBaseFactory().createAvp(0, DiameterAvpCodes.SESSION_ID, request.getSessionId());
			avps.add(sessionIdAvp);
		}
		catch (NoSuchAvpException e) {
      logger.error("Unable to create Session-Id AVP.", e);
		}

		if (request.getUserIdentity() != null) {
			avps.add(request.getUserIdentity());
		}

		Message msg = createShMessage(request.getHeader(), avps.toArray(new DiameterAvp[avps.size()]));
		SubscribeNotificationsAnswerImpl answer = new SubscribeNotificationsAnswerImpl(msg);

		if (request.hasRouteRecords()) {
			answer.setRouteRecords(request.getRouteRecords());
		}
		// add more :) ?
		
		addOrigin(answer);
		
		return answer;
	}

	public UserDataAnswer createUserDataAnswer(UserDataRequest request, byte[] userData) {
		UserDataAnswer uda = this.createUserDataAnswer(request);
		uda.setUserData(new String(userData));

		return uda;
	}

	public UserDataAnswer createUserDataAnswer(UserDataRequest request, long resultCode, boolean isExperimentalResult) {
		UserDataAnswer uda = this.createUserDataAnswer(request);

		if (isExperimentalResult) {
			uda.setExperimentalResult(this.localFactory.getBaseFactory().createExperimentalResult(0, resultCode));
		}
		else {
			uda.setResultCode(resultCode);
		}

		return uda;
	}

	public UserDataAnswer createUserDataAnswer(UserDataRequest request) {
		// Message msg = createShMessage(UserDataAnswer.commandCode, session !=
		// null ? session.getSessionId() : null, false);

		List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

		DiameterAvp sessionIdAvp = null;
		try {
			sessionIdAvp = localFactory.getBaseFactory().createAvp(0, DiameterAvpCodes.SESSION_ID, request.getSessionId());
			avps.add(sessionIdAvp);
		}
		catch (NoSuchAvpException e) {
      logger.error("Unable to create Session-Id AVP.", e);
		}

		if (request.getUserIdentity() != null)
			avps.add(request.getUserIdentity());

		Message msg = createShMessage(request.getHeader(), avps.toArray(new DiameterAvp[avps.size()]));
		UserDataAnswerImpl answer = new UserDataAnswerImpl(msg);

		if (request.hasRouteRecords())
			answer.setRouteRecords(request.getRouteRecords());
		// add more :) ?

		addOrigin(answer);
		return answer;
	}

	public DiameterMessageFactory getBaseMessageFactory() {
		return this.baseFactory;
	}

	public List<DiameterAvp> getInnerAvps() {
		return this.avpList;
	}

	public void addAvpToInnerList(DiameterAvp avp) {
		// Remove existing occurences...
		removeAvpFromInnerList(avp.getCode());

		this.avpList.add(avp);
	}

	public void removeAvpFromInnerList(int code) {
		Iterator<DiameterAvp> it = this.avpList.iterator();

		while (it.hasNext()) {
			if (it.next().getCode() == code) {
				it.remove();
			}
		}
	}

	// »»»»»»»»»»»»»»»»»»»»»
	// »» PRIVATE METHODS ««
	// «««««««««««««««««««««

	private Message createShMessage(DiameterHeader diameterHeader, DiameterAvp[] avps) throws IllegalArgumentException {

		// List<DiameterAvp> list = (List<DiameterAvp>) this.avpList.clone();
		boolean isRequest = diameterHeader == null;
		Message msg = null;
		if (!isRequest) {
			Message raw = createMessage(diameterHeader, avps);
			raw.setProxiable(true);
			raw.setRequest(false);
			msg = raw;
		}
		else {
			Message raw = createMessage(null, avps);
			raw.setProxiable(true);
			raw.setRequest(true);
			msg = raw;
		}
		// now now we msut add VendorSpecific?

		return msg;
	}

	protected Message createMessage(DiameterHeader header, DiameterAvp[] avps) throws AvpNotAllowedException {

		try {
			Message msg = createRawMessage(header);
			AvpSet set = msg.getAvps();
			
			for (DiameterAvp avp : avps) {
				addAvp(avp, set);
			}

			if (msg.getAvps().getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
				DiameterAvp avpVendorId = this.localFactory.getBaseFactory().createAvp(Avp.VENDOR_ID, MessageFactory._SH_VENDOR_ID);
				DiameterAvp avpAcctApplicationId = this.localFactory.getBaseFactory().createAvp(Avp.ACCT_APPLICATION_ID, MessageFactory._SH_APP_ID);
				DiameterAvp vendorSpecific = this.localFactory.getBaseFactory().createAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[] { avpVendorId, avpAcctApplicationId });
				msg.getAvps().addAvp(vendorSpecific.getCode(), vendorSpecific.byteArrayValue());
			}
			return msg;
		}
		catch (Exception e) {
      logger.error("Failed to create message.", e);
		}
		return null;
	}

	protected Message createRawMessage(DiameterHeader header) {

		int commandCode = 0;
		long endToEndId = 0;
		long hopByHopId = 0;
		ApplicationId aid = null;
		if (header != null) {
			commandCode = header.getCommandCode();
			endToEndId = header.getEndToEndId();
			hopByHopId = header.getHopByHopId();
			aid = ApplicationId.createByAuthAppId(header.getApplicationId());
		}
		else {
			//FIXME: This is the only one ;[
			commandCode = PushNotificationRequest.commandCode;
			endToEndId = (long) (Math.random()*1000000);
			hopByHopId = (long) (Math.random()*1000000)+1;
			aid = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);
		}
		try {
			return stack.getSessionFactory().getNewRawSession().createMessage(commandCode, aid, hopByHopId, endToEndId);
		}
		catch (InternalException e) {
      logger.error("Unable to create Raw Message.", e);
		}
		catch (IllegalDiameterStateException e) {
      logger.error("Unable to create Raw Message.", e);
		}
		
		return null;
	}
	
	protected void addAvp(DiameterAvp avp, AvpSet set) {
		// FIXME: alexandre: Should we look at the types and add them with proper function?
		if (avp instanceof GroupedAvp) {
			AvpSet avpSet = set.addGroupedAvp(avp.getCode(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);

			DiameterAvp[] groupedAVPs = ((GroupedAvp) avp).getExtensionAvps();
			for (DiameterAvp avpFromGroup : groupedAVPs) {
				addAvp(avpFromGroup, avpSet);
			}
		}
		else if (avp != null) {
			set.addAvp(avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
		}
	}
	
	public void clean() {
		session = null;
		if (this.baseFactory != null) {
			this.baseFactory.clean();
		}
	}
	
	private void addOrigin(DiameterMessage msg) {
		if(!msg.hasOriginHost()) {
			msg.setOriginHost(new DiameterIdentity(stack.getMetaData().getLocalPeer().getUri().getFQDN().toString()));
		}
		if(!msg.hasOriginRealm()) {
			msg.setOriginRealm(new DiameterIdentity(stack.getMetaData().getLocalPeer().getRealmName()));
		}
	}
}
