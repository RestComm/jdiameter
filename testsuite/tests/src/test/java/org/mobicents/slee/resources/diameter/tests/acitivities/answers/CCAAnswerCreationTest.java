/**
 * Start time:10:54:50 2009-07-08<br>
 * Project: diameter-auto<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resources.diameter.tests.acitivities.answers;

import java.util.ArrayList;

import net.java.slee.resource.diameter.base.events.DiameterMessage;

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
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.common.api.app.cca.ICCAMessageFactory;
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlServerSessionImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;
import org.mobicents.slee.resources.diameter.tests.factories.ShClientFactoriesTest;
import org.mobicents.slee.resources.diameter.tests.factories.ShClientFactoriesTest.MyConfiguration;

/**
 * Start time:10:54:50 2009-07-08<br>
 * Project: diameter-auto<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CCAAnswerCreationTest {
	private static String clientHost = "127.0.0.1";
	private static String clientPort = "21812";
	private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

	private static String serverHost = "localhost";
	private static String serverPort = "1812";
	private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

	private static String realmName = "mobicents.org";

	private static DiameterAvpFactoryImpl diameterAvpFactory = new DiameterAvpFactoryImpl();
	private static CreditControlAVPFactoryImpl ccaAvpFactory = new CreditControlAVPFactoryImpl(diameterAvpFactory);
	private static DiameterMessageFactoryImpl baseMessageFactory;

	private static Stack stack;
	static {
		stack = new org.jdiameter.client.impl.StackImpl();
		try {
			stack.init(new MyConfiguration());
			AvpDictionary.INSTANCE.parseDictionary(ShClientFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize the stack.");
		}

		baseMessageFactory = new DiameterMessageFactoryImpl(stack);
		
	}

	@Test
	public void testCCAServerActivityAnswerCreation() throws Exception {
		ServerCCASessionImpl session = new ServerCCASessionImpl(new ICCAMessageFactoryImpl(), stack.getSessionFactory(), new LocalServerCCASessionListenerImpl());
		CreditControlMessageFactoryImpl ccaMessageFactory = new CreditControlMessageFactoryImpl(baseMessageFactory,session.getSessions().get(0),stack,ccaAvpFactory);
		ArrayList<DiameterMessage> list = new ArrayList<DiameterMessage>();
		list.add(ccaMessageFactory.createCreditControlRequest());
		CreditControlServerSessionImpl activity = new CreditControlServerSessionImpl(ccaMessageFactory,ccaAvpFactory,session,5000,null,null,null);
		DiameterActivityAnswerCreationHelper.testAnswerCreation(activity, "lastRequest", list);
	}
}

class ICCAMessageFactoryImpl implements ICCAMessageFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.cca.ICCAMessageFactory#createCreditControlAnswer
	 * (org.jdiameter.api.Answer)
	 */
	public JCreditControlAnswer createCreditControlAnswer(Answer answer) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.common.api.app.cca.ICCAMessageFactory#
	 * createCreditControlRequest(org.jdiameter.api.Request)
	 */
	public JCreditControlRequest createCreditControlRequest(Request req) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.cca.ICCAMessageFactory#createReAuthAnswer
	 * (org.jdiameter.api.Answer)
	 */
	public ReAuthAnswer createReAuthAnswer(Answer answer) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.cca.ICCAMessageFactory#createReAuthRequest
	 * (org.jdiameter.api.Request)
	 */
	public ReAuthRequest createReAuthRequest(Request req) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.cca.ICCAMessageFactory#getApplicationIds()
	 */
	public long[] getApplicationIds() {
		return new long[]{1};
	}
}

class LocalServerCCASessionListenerImpl implements ServerCCASessionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doAbortSessionAnswer(org
	 * .jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.auth.events.AbortSessionRequest,
	 * org.jdiameter.api.auth.events.AbortSessionAnswer)
	 */
	public void doAbortSessionAnswer(ServerCCASession session, AbortSessionRequest request, AbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doAbortSessionRequest(
	 * org.jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.auth.events.AbortSessionRequest)
	 */
	public void doAbortSessionRequest(ServerCCASession session, AbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doAccountingAnswer(org
	 * .jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.acc.events.AccountRequest,
	 * org.jdiameter.api.acc.events.AccountAnswer)
	 */
	public void doAccountingAnswer(ServerCCASession session, AccountRequest request, AccountAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doAccountingRequest(org
	 * .jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.acc.events.AccountRequest)
	 */
	public void doAccountingRequest(ServerCCASession session, AccountRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doCreditControlRequest
	 * (org.jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.cca.events.JCreditControlRequest)
	 */
	public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doOtherEvent(org.jdiameter
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
	 * org.jdiameter.api.cca.ServerCCASessionListener#doReAuthAnswer(org.jdiameter
	 * .api.cca.ServerCCASession, org.jdiameter.api.auth.events.ReAuthRequest,
	 * org.jdiameter.api.auth.events.ReAuthAnswer)
	 */
	public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doSessionTerminationAnswer
	 * (org.jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.auth.events.SessionTermRequest,
	 * org.jdiameter.api.auth.events.SessionTermAnswer)
	 */
	public void doSessionTerminationAnswer(ServerCCASession session, SessionTermRequest request, SessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cca.ServerCCASessionListener#doSessionTerminationRequest
	 * (org.jdiameter.api.cca.ServerCCASession,
	 * org.jdiameter.api.auth.events.SessionTermRequest)
	 */
	public void doSessionTerminationRequest(ServerCCASession session, SessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		// TODO Auto-generated method stub

	}
}
