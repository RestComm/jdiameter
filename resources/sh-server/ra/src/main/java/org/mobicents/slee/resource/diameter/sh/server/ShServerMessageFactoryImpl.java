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
package org.mobicents.slee.resource.diameter.sh.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
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

	public ProfileUpdateAnswer createProfileUpdateAnswer(long resultCode, boolean isExperimentalResult) {
		ProfileUpdateAnswer pua = this.createProfileUpdateAnswer();

		if (isExperimentalResult) {
			pua.setExperimentalResult(this.localFactory.getBaseFactory().createExperimentalResult(0, resultCode));
		} else {
			pua.setResultCode(resultCode);
		}

		return pua;
	}

	public ProfileUpdateAnswer createProfileUpdateAnswer() {
		Message msg = createShMessage(ProfileUpdateRequest.commandCode, session == null ? null : session.getSessionId(), false);

		return new ProfileUpdateAnswerImpl(msg);
	}

	public PushNotificationRequest createPushNotificationRequest(UserIdentityAvp userIdentity, byte[] userData) {
		PushNotificationRequest pnr = this.createPushNotificationRequest();

		pnr.setUserIdentity(userIdentity);
		pnr.setUserData(userData);

		return pnr;
	}

	public PushNotificationRequest createPushNotificationRequest() {
		Message msg = createShMessage(PushNotificationRequest.commandCode, session == null ? null : session.getSessionId(), true);

		return new PushNotificationRequestImpl(msg);
	}

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(long resultCode, boolean isExperimentalResult) {
		SubscribeNotificationsAnswer sna = this.createSubscribeNotificationsAnswer();

		if (isExperimentalResult) {
			sna.setExperimentalResult(this.localFactory.getBaseFactory().createExperimentalResult(0, resultCode));
		} else {
			sna.setResultCode(resultCode);
		}

		return sna;
	}

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer() {
		Message msg = createShMessage(SubscribeNotificationsAnswer.commandCode, session == null ? null : session.getSessionId(), false);

		return new SubscribeNotificationsAnswerImpl(msg);
	}

	public UserDataAnswer createUserDataAnswer(byte[] userData) {
		UserDataAnswer uda = this.createUserDataAnswer();
		uda.setUserData(userData);

		return uda;
	}

	public UserDataAnswer createUserDataAnswer(long resultCode, boolean isExperimentalResult) {
		UserDataAnswer uda = this.createUserDataAnswer();

		if (isExperimentalResult) {
			uda.setExperimentalResult(this.localFactory.getBaseFactory().createExperimentalResult(0, resultCode));
		} else {
			uda.setResultCode(resultCode);
		}

		return uda;
	}

	public UserDataAnswer createUserDataAnswer() {
		Message msg = createShMessage(UserDataAnswer.commandCode, session != null ? session.getSessionId() : null, false);

		return new UserDataAnswerImpl(msg);
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

	protected Message createMessage(int commandCode, ApplicationId applicationId, DiameterAvp... avps) {
		Message msg = null;
		DiameterAvp sessionIdAvp = null;

		for (DiameterAvp avp : avps) {
			if (avp.getCode() == Avp.SESSION_ID) {
				sessionIdAvp = avp;
				break;
			}
		}

		if (session == null) {
			try {
				msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
			} catch (Exception e) {
				logger.error("Error creating message in new session.", e);
			}
		} else {
			String destRealm = null;
			String destHost = null;

			if (avps != null) {
				for (DiameterAvp avp : avps) {
					if (avp.getCode() == Avp.DESTINATION_REALM) {
						destRealm = avp.octetStringValue();
					} else if (avp.getCode() == Avp.DESTINATION_HOST) {
						destHost = avp.octetStringValue();
					}
				}
			}

			msg = destHost == null ? session.createRequest(commandCode, applicationId, destRealm) : session.createRequest(commandCode, applicationId, destRealm, destHost);
		}

		if (sessionIdAvp != null) {
			msg.getAvps().removeAvp(Avp.SESSION_ID);
			addAvp(sessionIdAvp, msg.getAvps());
		} else if (msg.getAvps().getAvp(Avp.SESSION_ID) == null) {
			// Do we have a session-id already or shall we make one?
			if (this.session != null) {
				msg.getAvps().addAvp(Avp.SESSION_ID, this.session.getSessionId(), true, false, false);
			} else {
				msg.getAvps().addAvp(Avp.SESSION_ID, this.baseFactory.generateSessionId(), true, false, false);
			}
		}

		if (avps != null) {
			for (DiameterAvp avp : avps) {
				if (avp.getCode() == Avp.SESSION_ID) {
					continue;
				}
				addAvp(avp, msg.getAvps());
			}
		}

		msg.setProxiable(true);

		return msg;
	}

	private void addAvp(DiameterAvp avp, AvpSet set) {
		if (avp instanceof GroupedAvp) {
			AvpSet avpSet = set.addGroupedAvp(avp.getCode(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);

			DiameterAvp[] groupedAVPs = ((GroupedAvp) avp).getExtensionAvps();

			for (DiameterAvp avpFromGroup : groupedAVPs) {
				addAvp(avpFromGroup, avpSet);
			}
		} else if (avp != null) {
			set.addAvp(avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
		}
	}

	protected Message createShMessage(int commandCode, String sessionId, boolean isRequest) throws IllegalArgumentException {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_SH_VENDOR_ID, _SH_APP_ID);

		List<DiameterAvp> list = (List<DiameterAvp>) this.avpList.clone();

		if (sessionId != null) {
			DiameterAvp sessionIdAvp;

			try {
				sessionIdAvp = this.localFactory.getBaseFactory().createAvp(Avp.SESSION_ID, sessionId);
			} catch (NoSuchAvpException e) {
				throw new IllegalArgumentException(e);
			}

			// Clean any present Session-Id AVP
			for (DiameterAvp avp : list) {
				if (avp.getCode() == Avp.SESSION_ID) {
					list.remove(avp);
				}
			}

			// And add this to as close as possible to the header
			list.add(0, sessionIdAvp);
		}

		Message msg = createMessage(commandCode, applicationId, list.toArray(new DiameterAvp[list.size()]));
		msg.setRequest(isRequest);

		return msg;
	}

}
