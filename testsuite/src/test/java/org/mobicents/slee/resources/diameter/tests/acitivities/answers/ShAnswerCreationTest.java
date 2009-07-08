/**
 * Start time:22:26:45 2009-07-07<br>
 * Project: diameter-auto<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resources.diameter.tests.acitivities.answers;

import java.util.ArrayList;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.ServerShSessionListener;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.client.impl.app.sh.ShClientSessionImpl;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.server.impl.app.sh.ShServerSessionImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.ShClientMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.ShClientSubscriptionActivityImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerActivityImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerSubscriptionActivityImpl;
import org.mobicents.slee.resources.diameter.tests.factories.ShClientFactoriesTest;
import org.mobicents.slee.resources.diameter.tests.factories.ShClientFactoriesTest.MyConfiguration;

/**
 * Start time:22:26:45 2009-07-07<br>
 * Project: diameter-auto<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShAnswerCreationTest {

	private static String clientHost = "127.0.0.1";
	private static String clientPort = "21812";
	private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

	private static String serverHost = "localhost";
	private static String serverPort = "1812";
	private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

	private static String realmName = "mobicents.org";

	private static DiameterAvpFactoryImpl diameterAvpFactory = new DiameterAvpFactoryImpl();
	private static DiameterShAvpFactoryImpl diameterShAvpFactory = new DiameterShAvpFactoryImpl(diameterAvpFactory);

	private static Stack stack;
	static {
		stack = new org.jdiameter.client.impl.StackImpl();
		try {
			stack.init(new MyConfiguration());
			AvpDictionary.INSTANCE.parseDictionary(ShClientFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize the stack.");
		}

		DiameterMessageFactoryImpl baseMessageFactory = new DiameterMessageFactoryImpl(stack);

	}

	@Test
	public void testShClientActivityAnswerCreation() throws Exception {
		ClientShSession session = new ShClientSessionImpl(new IShMessageFactoryImpl(), stack.getSessionFactory(), new ClientShSessionListenerImpl());
		DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);
		ShServerMessageFactoryImpl factory = new ShServerMessageFactoryImpl(msgFactory, session.getSessions().get(0), stack, diameterShAvpFactory);
		PushNotificationRequest pnr = factory.createPushNotificationRequest();
		ArrayList<DiameterMessage> list = new ArrayList<DiameterMessage>();
		list.add(pnr);
		ShClientSubscriptionActivityImpl activity = new ShClientSubscriptionActivityImpl(msgFactory, new ShClientMessageFactoryImpl(session.getSessions().get(0), stack), diameterAvpFactory,
				new DiameterShAvpFactoryImpl(diameterAvpFactory), session, 5000, null, null, null);

		DiameterActivityAnswerCreationHelper.testAnswerCreation(activity, "stateMessages", list);

	}

	@Test
	public void testShServerActivityAnswerCreation() throws Exception {
		ServerShSession session = new ShServerSessionImpl(new IShMessageFactoryImpl(), stack.getSessionFactory(), new ServerShSessionListenerImpl());
		DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);
		ShClientMessageFactoryImpl factory = new ShClientMessageFactoryImpl(session.getSessions().get(0), stack);
		UserDataRequest udr = factory.createUserDataRequest();
		net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest pur = factory.createProfileUpdateRequest();
		net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest snr = factory.createSubscribeNotificationsRequest();
		ArrayList<DiameterMessage> list = new ArrayList<DiameterMessage>();
		list.add(udr);
		list.add(pur);
		list.add(snr);

		ShServerActivityImpl activity = new ShServerActivityImpl(new ShServerMessageFactoryImpl(msgFactory, session.getSessions().get(0), stack, diameterShAvpFactory), diameterShAvpFactory, session,
				5000, null, null, null);

		DiameterActivityAnswerCreationHelper.testAnswerCreation(activity, "stateMessages", list);

	}

	@Test
	public void testShServerSubscriptionActivityAnswerCreation() throws Exception {
		ServerShSession session = new ShServerSessionImpl(new IShMessageFactoryImpl(), stack.getSessionFactory(), new ServerShSessionListenerImpl());
		DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);
		ShClientMessageFactoryImpl factory = new ShClientMessageFactoryImpl(session.getSessions().get(0), stack);
		UserDataRequest udr = factory.createUserDataRequest();
		net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest pur = factory.createProfileUpdateRequest();
		net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest snr = factory.createSubscribeNotificationsRequest();

		ArrayList<DiameterMessage> list = new ArrayList<DiameterMessage>();
		list.add(udr);
		list.add(pur);
		list.add(snr);

		ShServerSubscriptionActivityImpl activity = new ShServerSubscriptionActivityImpl(new ShServerMessageFactoryImpl(msgFactory, session.getSessions().get(0), stack, diameterShAvpFactory),
				diameterShAvpFactory, session, 5000, null, null, null);

		DiameterActivityAnswerCreationHelper.testAnswerCreation(activity, "stateMessages", list);

	}
}

class IShMessageFactoryImpl implements IShMessageFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.sh.IShMessageFactory#createProfileUpdateAnswer
	 * (org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createProfileUpdateAnswer(Answer answer) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.sh.IShMessageFactory#createProfileUpdateRequest
	 * (org.jdiameter.api.Request)
	 */
	public AppRequestEvent createProfileUpdateRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.common.api.app.sh.IShMessageFactory#
	 * createPushNotificationAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createPushNotificationAnswer(Answer answer) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.common.api.app.sh.IShMessageFactory#
	 * createPushNotificationRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createPushNotificationRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.common.api.app.sh.IShMessageFactory#
	 * createSubscribeNotificationsAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.common.api.app.sh.IShMessageFactory#
	 * createSubscribeNotificationsRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createSubscribeNotificationsRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.sh.IShMessageFactory#createUserDataAnswer
	 * (org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createUserDataAnswer(Answer answer) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.sh.IShMessageFactory#createUserDataRequest
	 * (org.jdiameter.api.Request)
	 */
	public AppRequestEvent createUserDataRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdiameter.common.api.app.sh.IShMessageFactory#getApplicationId()
	 */
	public long getApplicationId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.sh.IShMessageFactory#getMessageTimeout()
	 */
	public long getMessageTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}

class ClientShSessionListenerImpl implements ClientShSessionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ClientShSessionListener#doOtherEvent(org.jdiameter
	 * .api.app.AppSession, org.jdiameter.api.app.AppRequestEvent,
	 * org.jdiameter.api.app.AppAnswerEvent)
	 */
	public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ClientShSessionListener#doProfileUpdateAnswerEvent
	 * (org.jdiameter.api.sh.ClientShSession,
	 * org.jdiameter.api.sh.events.ProfileUpdateRequest,
	 * org.jdiameter.api.sh.events.ProfileUpdateAnswer)
	 */
	public void doProfileUpdateAnswerEvent(ClientShSession session, ProfileUpdateRequest request, ProfileUpdateAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ClientShSessionListener#doPushNotificationRequestEvent
	 * (org.jdiameter.api.sh.ClientShSession,
	 * org.jdiameter.api.sh.events.PushNotificationRequest)
	 */
	public void doPushNotificationRequestEvent(ClientShSession session, org.jdiameter.api.sh.events.PushNotificationRequest request) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.api.sh.ClientShSessionListener#
	 * doSubscribeNotificationsAnswerEvent(org.jdiameter.api.sh.ClientShSession,
	 * org.jdiameter.api.sh.events.SubscribeNotificationsRequest,
	 * org.jdiameter.api.sh.events.SubscribeNotificationsAnswer)
	 */
	public void doSubscribeNotificationsAnswerEvent(ClientShSession session, SubscribeNotificationsRequest request, SubscribeNotificationsAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ClientShSessionListener#doUserDataAnswerEvent(org
	 * .jdiameter.api.sh.ClientShSession,
	 * org.jdiameter.api.sh.events.UserDataRequest,
	 * org.jdiameter.api.sh.events.UserDataAnswer)
	 */
	public void doUserDataAnswerEvent(ClientShSession session, org.jdiameter.api.sh.events.UserDataRequest request, UserDataAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

}

class ServerShSessionListenerImpl implements ServerShSessionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ServerShSessionListener#doOtherEvent(org.jdiameter
	 * .api.app.AppSession, org.jdiameter.api.app.AppRequestEvent,
	 * org.jdiameter.api.app.AppAnswerEvent)
	 */
	public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ServerShSessionListener#doProfileUpdateRequestEvent
	 * (org.jdiameter.api.sh.ServerShSession,
	 * org.jdiameter.api.sh.events.ProfileUpdateRequest)
	 */
	public void doProfileUpdateRequestEvent(ServerShSession session, ProfileUpdateRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ServerShSessionListener#doPushNotificationAnswerEvent
	 * (org.jdiameter.api.sh.ServerShSession,
	 * org.jdiameter.api.sh.events.PushNotificationRequest,
	 * org.jdiameter.api.sh.events.PushNotificationAnswer)
	 */
	public void doPushNotificationAnswerEvent(ServerShSession session, org.jdiameter.api.sh.events.PushNotificationRequest request, PushNotificationAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.api.sh.ServerShSessionListener#
	 * doSubscribeNotificationsRequestEvent
	 * (org.jdiameter.api.sh.ServerShSession,
	 * org.jdiameter.api.sh.events.SubscribeNotificationsRequest)
	 */
	public void doSubscribeNotificationsRequestEvent(ServerShSession session, SubscribeNotificationsRequest request) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.sh.ServerShSessionListener#doUserDataRequestEvent(org
	 * .jdiameter.api.sh.ServerShSession,
	 * org.jdiameter.api.sh.events.UserDataRequest)
	 */
	public void doUserDataRequestEvent(ServerShSession session, org.jdiameter.api.sh.events.UserDataRequest request) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		// TODO Auto-generated method stub

	}

}

