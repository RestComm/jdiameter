package org.mobicents.slee.resource.diameter.sh.client;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.common.impl.app.sh.PushNotificationAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.handlers.ShClientSessionListener;

public class ShClientSubscriptionActivityImpl extends DiameterActivityImpl
		implements ShClientSubscriptionActivity, StateChangeListener {

	protected ClientShSession clientSession = null;
	protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
	protected ShClientSessionListener listener = null;
	protected DiameterShAvpFactory shAvpFactory = null;
	protected ShClientMessageFactory messageFactory = null;

	public ShClientSubscriptionActivityImpl(
			DiameterMessageFactoryImpl messageFactory,
			ShClientMessageFactory shClientMessageFactory,
			DiameterAvpFactoryImpl avpFactory,
			DiameterShAvpFactory diameterShAvpFactory, ClientShSession session,
			long timeout, DiameterIdentityAvp destinationHost,
			DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint) {
		super(messageFactory, avpFactory, null,
				(EventListener<Request, Answer>) session, timeout,
				destinationHost, destinationRealm, endpoint);
		this.clientSession = session;
		this.clientSession.addStateChangeNotification(this);
		super.setCurrentWorkingSession(this.clientSession.getSessions().get(0));
		this.shAvpFactory = diameterShAvpFactory;
		this.messageFactory = shClientMessageFactory;

	}

	public GroupedAvp getSubscribedUserIdendity() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendPushNotificationAnswer(PushNotificationAnswer answer)
			throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) answer;
		try {
			this.clientSession
					.sendPushNotificationAnswer(new PushNotificationAnswerImpl(
							(Answer) msg.getGenericData()));
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendPushNotificationAnswer(long resultCode,
			boolean isExperimentalResultCode) throws IOException {
		// TODO Auto-generated method stub

	}

	public void sendSubscriptionNotificationRequest(
			SubscribeNotificationsRequest request) throws IOException {
		DiameterMessageImpl msg = (DiameterMessageImpl) request;
		try {
			this.clientSession
					.sendSubscribeNotificationsRequest(new SubscribeNotificationsRequestImpl(
							(Answer) msg.getGenericData()));
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendUnsubscribeRequest() throws IOException {
		// TODO Auto-generated method stub

	}

	public void stateChanged(Enum oldState, Enum newState) {
		org.jdiameter.common.api.app.sh.ShSessionState _state = (org.jdiameter.common.api.app.sh.ShSessionState) newState;
		switch (_state) {
		case NOTSUBSCRIBED:
			break;
		case SUBSCRIBED:
			state = ShSessionState.SUBSCRIBED;
			// FIXME: error?

			break;
		case TERMINATED:
			state = ShSessionState.TERMINATED;
			listener.sessionDestroyed(getSessionId(), clientSession);
			break;
		}
	}

	public Object getSessionListener() {
		return this.listener;
	}

	public void setSessionListener(Object ra) {
		this.listener = (ShClientSessionListener) ra;
	}

	public void endActivity() {
		this.clientSession.release();

	}

	public Object getDiameterAvpFactory() {
		return this.shAvpFactory;
	}

	public Object getDiameterMessageFactory() {

		return this.messageFactory;
	}

	ClientShSession getClientSession() {
		return this.clientSession;
	}

	void fetchSubscriptionData(SubscribeNotificationsRequest request) {
	}

	void fetchSubscriptionData(PushNotificationRequest request) {
	}
}
