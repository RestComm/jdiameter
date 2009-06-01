package org.mobicents.slee.resource.diameter.cca.handlers;

import java.util.concurrent.ScheduledFuture;


import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.client.impl.app.cca.ClientCCASessionImpl;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.cca.ICCAMessageFactory;
import org.jdiameter.common.api.app.cca.IClientCCASessionContext;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlAnswerImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityHandle;
import org.mobicents.slee.resource.diameter.cca.CCAResourceAdaptor;

import static org.mobicents.slee.resource.diameter.cca.handlers.CCASessionCreationListener.*;

/**
 * 
 * CreditControlSessionFactory.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 3:19:55 AM Dec 30, 2008 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlSessionFactory implements IAppSessionFactory, ClientCCASessionListener, ServerCCASessionListener, StateChangeListener, ICCAMessageFactory, IServerCCASessionContext,
		IClientCCASessionContext {

	protected SessionFactory sessionFactory = null;
	protected CCASessionCreationListener resourceAdaptor = null;

	// Message timeout value (in milliseconds)
	protected long messageTimeout = 5000;

	protected int defaultDirectDebitingFailureHandling = 0;
	protected int defaultCreditControlFailureHandling = 0;

	// its seconds
	protected long defaultValidityTime = 30;
	protected long defaultTxTimerValue = 10;
	protected Logger logger = Logger.getLogger(CreditControlSessionFactory.class);

	public CreditControlSessionFactory(SessionFactory sessionFactory, CCASessionCreationListener resourceAdaptor, long messageTimeout) {
		super();

		this.sessionFactory = sessionFactory;
		this.resourceAdaptor = resourceAdaptor;
		this.messageTimeout = messageTimeout;
	}

	public CreditControlSessionFactory(SessionFactory sessionFactory, CCASessionCreationListener resourceAdaptor, long messageTimeout, int defaultDirectDebitingFailureHandling,
			int defaultCreditControlFailureHandling, long defaultValidityTime, long defaultTxTimerValue) {
		super();

		this.sessionFactory = sessionFactory;
		this.resourceAdaptor = resourceAdaptor;
		this.messageTimeout = messageTimeout;
		this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
		this.defaultCreditControlFailureHandling = defaultCreditControlFailureHandling;
		this.defaultValidityTime = defaultValidityTime;
		this.defaultTxTimerValue = defaultTxTimerValue;
	}

	public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
		AppSession appSession = null;
		try {
			if (aClass == ClientCCASession.class) {
				ClientCCASessionImpl clientSession = null;
				if (args != null && args.length > 0 && args[0] instanceof Request) {
					Request request = (Request) args[0];
					clientSession = new ClientCCASessionImpl(request.getSessionId(), this, sessionFactory, this);
				} else {
					clientSession = new ClientCCASessionImpl(sessionId, this, sessionFactory, this);
				}

				clientSession.getSessions().get(0).setRequestListener(clientSession);
				clientSession.addStateChangeNotification(this);

				this.resourceAdaptor.sessionCreated(clientSession);

				appSession = clientSession;
			} else if (aClass == ServerCCASession.class) {
				ServerCCASessionImpl serverSession = null;

				if (args != null && args.length > 0 && args[0] instanceof Request) {
					// This shouldnt happen but just in case
					Request request = (Request) args[0];
					serverSession = new ServerCCASessionImpl(request.getSessionId(), this, sessionFactory, this);
				} else {
					serverSession = new ServerCCASessionImpl(sessionId, this, sessionFactory, this);
				}

				serverSession.addStateChangeNotification(this);
				serverSession.getSessions().get(0).setRequestListener(serverSession);

				this.resourceAdaptor.sessionCreated(serverSession);

				appSession = serverSession;
			} else {
				throw new IllegalArgumentException("Wrong session class!![" + aClass + "]. Supported[" + ClientCCASession.class + "," + ServerCCASession.class + "]");
			}
		} catch (Exception e) {
			logger.error("Failure to obtain new Credit-Control Session.", e);
		}

		return appSession;
	}

	// ////////////////////
	// Message Handlers //
	// ////////////////////

	private void doMessage(String name, AppSession appSession, AppEvent message, boolean isRequest) throws InternalException {
		DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

		if (isRequest) {
			this.resourceAdaptor.fireEvent(handle, name, (Request) message.getMessage(), null);
		} else {
			this.resourceAdaptor.fireEvent(handle, name, null, (Answer) message.getMessage());
		}
	}

	public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request) throws InternalException {
		doMessage(_CreditControlRequest, session, request, true);
	}

	public void doCreditControlAnswer(ClientCCASession session, JCreditControlRequest request, JCreditControlAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_CreditControlAnswer, session, answer, false);
	}

	public void doReAuthRequest(ClientCCASession session, ReAuthRequest request) throws InternalException {
		doMessage(_ReAuthRequest, session, request, true);
	}

	public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_ReAuthAnswer, session, answer, false);
	}

	public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
		// baranowb: here we get something weird, lets do extension
		// Still we relly on CCA termination mechanisms, those message are sent
		// via generic send, which does not trigger FSM

		DiameterActivityHandle handle = new DiameterActivityHandle(session.getSessions().get(0).getSessionId());

		logger.info("Diameter CCA RA :: doOtherEvent :: appSession[" + session + "], Request[" + request + "], Answer[" + answer + "]");

		if (answer != null) {
			if (answer.getMessage().isError())
				doMessage(_ErrorAnswer, session, answer, false);
			else
				doMessage(_ExtensionDiameterMessage, session, answer, false);
		} else {
			doMessage(_ExtensionDiameterMessage, session, answer, true);
		}
	}

	// /////////////////////////
	// Base Message Handlers //
	// /////////////////////////

	public void doAbortSessionRequest(ClientCCASession session, AbortSessionRequest request) throws InternalException {
		doMessage(_AbortSessionRequest, session, request, true);
	}

	public void doAbortSessionAnswer(ClientCCASession session, AbortSessionRequest request, AbortSessionAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_AbortSessionAnswer,session, answer, false);
	}

	public void doAccountingRequest(ClientCCASession session, AccountRequest request) throws InternalException {
	
			doMessage(_AccountingRequest,session, request, true);
	}

	public void doAccountingAnswer(ClientCCASession session, AccountRequest request, AccountAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_AccountingAnswer,session, answer, false);
	}

	public void doSessionTerminationRequest(ClientCCASession session, SessionTermRequest request) throws InternalException {
		doMessage(_SessionTerminationRequest,session, request, true);
	}

	public void doSessionTerminationAnswer(ClientCCASession session, SessionTermRequest request, SessionTermAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_SessionTerminationAnswer,session, answer, false);
	}

	public void doAbortSessionRequest(ServerCCASession session, AbortSessionRequest request) throws InternalException {
		doMessage(_AbortSessionRequest,session, request, true);
	}

	public void doAbortSessionAnswer(ServerCCASession session, AbortSessionRequest request, AbortSessionAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_AbortSessionAnswer,session, answer, false);
	}

	public void doAccountingRequest(ServerCCASession session, AccountRequest request) throws InternalException {
		doMessage(_AccountingRequest,session, request, true);
	}

	public void doAccountingAnswer(ServerCCASession session, AccountRequest request, AccountAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_AccountingAnswer,session, answer, false);
	}

	public void doSessionTerminationRequest(ServerCCASession session, SessionTermRequest request) throws InternalException {
		doMessage(_SessionTerminationRequest,session, request, true);
	}

	public void doSessionTerminationAnswer(ServerCCASession session, SessionTermRequest request, SessionTermAnswer answer) throws InternalException {
		if (answer.getMessage().isError())
			doMessage(_ErrorAnswer, session, answer, false);
		else
			doMessage(_SessionTerminationAnswer,session, answer, false);
	}

	// ///////////////////////////
	// Message Factory Methods //
	// ///////////////////////////

	public JCreditControlAnswer createCreditControlAnswer(Answer answer) {
		return new JCreditControlAnswerImpl(answer);
	}

	public JCreditControlRequest createCreditControlRequest(Request req) {
		return new JCreditControlRequestImpl(req);
	}

	public ReAuthAnswer createReAuthAnswer(Answer answer) {
		return new ReAuthAnswerImpl(answer);
	}

	public ReAuthRequest createReAuthRequest(Request req) {
		return new ReAuthRequestImpl(req);
	}

	// ///////////////////
	// Context Methods //
	// ///////////////////

	public void stateChanged(Enum oldState, Enum newState) {
		if (logger.isInfoEnabled()) {
			logger.info("Diameter CCA SessionFactory :: stateChanged :: oldState[" + oldState + "], newState[" + newState + "]");
		}
	}

	public void sessionSupervisionTimerExpired(ServerCCASession session) {
		//this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
		session.release();
	}

	public void sessionSupervisionTimerReStarted(ServerCCASession session, ScheduledFuture future) {
		// TODO Complete this method.
	}

	public void sessionSupervisionTimerStarted(ServerCCASession session, ScheduledFuture future) {
		// TODO Complete this method.
	}

	public void sessionSupervisionTimerStopped(ServerCCASession session, ScheduledFuture future) {
		// TODO Complete this method.
	}

	public void timeoutExpired(Request request) {
		// FIXME What should we do when there's a timeout?
	}

	public void denyAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
		// TODO Complete this method.
	}

	public void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
		// TODO Complete this method.
	}

	public void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
		//this.resourceAdaptor.sessionDestroyed(clientCCASessionImpl.getSessions().get(0).getSessionId(), clientCCASessionImpl);
		clientCCASessionImpl.release();
	}

	public int getDefaultCCFHValue() {
		return defaultCreditControlFailureHandling;
	}

	public int getDefaultDDFHValue() {
		return defaultDirectDebitingFailureHandling;
	}

	public long getDefaultTxTimerValue() {
		return defaultTxTimerValue;
	}

	public void grantAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
		// TODO Auto-generated method stub
	}

	public void grantAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
	}

	public void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
	}

	public void indicateServiceError(ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
	}

	public void txTimerExpired(ClientCCASession session) {
		//this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
		session.release();
	}

	public long[] getApplicationIds() {
		// FIXME: What should we do here?
		return new long[] { 4 };
	}

	public long getDefaultValidityTime() {
		return this.defaultValidityTime;
	}

}
