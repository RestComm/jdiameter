package org.mobicents.slee.resource.diameter.cca.handlers;

import java.util.concurrent.ScheduledFuture;

import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.client.impl.app.cca.ClientCCASessionImpl;
import org.jdiameter.client.impl.app.sh.ShClientSessionImpl;
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

public class CreditControlSessionFactory implements IAppSessionFactory,
		ClientCCASessionListener, ServerCCASessionListener,
		StateChangeListener, ICCAMessageFactory , IServerCCASessionContext, IClientCCASessionContext{

	protected SessionFactory sessionFactory = null;
	protected CCAResourceAdaptor resourceAdaptor = null;
	//its miliseconds
	protected long messageTimeout = 5000;

	protected int defaultDirectDebitingFailureHandling = 0;
	protected int defaultCreditControlFailureHandling = 0;

	
	//its seconds
	protected long defaultValidityTime=30;
	protected long defaultTxTimerValue=10;
	protected Logger logger = Logger
			.getLogger(CreditControlSessionFactory.class);

	public CreditControlSessionFactory(SessionFactory sessionFactory,
			CCAResourceAdaptor resourceAdaptor, long messageTimeout) {
		super();
		this.sessionFactory = sessionFactory;
		this.resourceAdaptor = resourceAdaptor;
		this.messageTimeout = messageTimeout;


	}

	
	
	public CreditControlSessionFactory(SessionFactory sessionFactory,
			CCAResourceAdaptor resourceAdaptor, long messageTimeout,
			int defaultDirectDebitingFailureHandling,
			int defaultCreditControlFailureHandling, long defaultValidityTime,
			long defaultTxTimerValue) {
		super();
		this.sessionFactory = sessionFactory;
		this.resourceAdaptor = resourceAdaptor;
		this.messageTimeout = messageTimeout;
		this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
		this.defaultCreditControlFailureHandling = defaultCreditControlFailureHandling;
		this.defaultValidityTime = defaultValidityTime;
		this.defaultTxTimerValue = defaultTxTimerValue;
	}



	public AppSession getNewSession(String sessionId,
			Class<? extends AppSession> aClass, ApplicationId applicationId,
			Object[] args) {

		AppSession value = null;
		try {
			if (aClass == ClientCCASession.class) {

				ClientCCASessionImpl clientSession=null;
				 if(args!=null && args.length>1 && args[0] instanceof Request)
				 {
				 Request request = (Request) args[0];
				 clientSession=new  ClientCCASessionImpl(request.getSessionId(),this,sessionFactory,this);

				 }else
				 {
				 clientSession=new	 ClientCCASessionImpl(null,this,sessionFactory,this);
				 }

				 clientSession.addStateChangeNotification(this);
				this.resourceAdaptor.sessionCreated(clientSession);
				 value=clientSession;

			} else if (aClass == ServerCCASession.class) {
				ServerCCASessionImpl serverSession = null;
				if (args != null && args.length > 1
						&& args[0] instanceof Request) {
					// This shouldnt happen but just in case
					Request request = (Request) args[0];
					serverSession = new ServerCCASessionImpl(request
							.getSessionId(), this, sessionFactory, this);

				} else {
					serverSession = new ServerCCASessionImpl(null, this,
							sessionFactory, this);
				}
				serverSession.addStateChangeNotification(this);
				this.resourceAdaptor.sessionCreated(serverSession);
				value = serverSession;
			} else {
				throw new IllegalArgumentException("Wrong session class!!["
						+ aClass + "]. Supported[" + ClientShSession.class
						+ "]");
			}

		} catch (Exception e) {
			logger.error("Failure to obtain new Accounting Session.", e);
		}

		return value;
	}

	// //////////////////////
	// // MESSAGE HANDLERS //
	// //////////////////////

	public void doCreditControlAnswer(ClientCCASession session,
			JCreditControlRequest request, JCreditControlAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		DiameterActivityHandle handle = new DiameterActivityHandle(session
				.getSessions().get(0).getSessionId());
		
		this.resourceAdaptor.fireEvent(handle, this.resourceAdaptor.events.get(answer.getCommandCode()) + "Answer", null, (Answer) answer.getMessage());

	}

	

	public void doReAuthRequest(ClientCCASession session, ReAuthRequest request)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		DiameterActivityHandle handle = new DiameterActivityHandle(session
				.getSessions().get(0).getSessionId());
		this.resourceAdaptor.fireEvent(handle, this.resourceAdaptor.events.get(request.getCommandCode()) + "Request",(Request)request.getMessage(), null);
	}

	public void doCreditControlRequest(ServerCCASession session,
			JCreditControlRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		DiameterActivityHandle handle = new DiameterActivityHandle(session
				.getSessions().get(0).getSessionId());

		this.resourceAdaptor.fireEvent(handle, this.resourceAdaptor.events.get(request.getCommandCode()) + "Request",(Request)request.getMessage(), null);
		
	}

	public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request,
			ReAuthAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		DiameterActivityHandle handle = new DiameterActivityHandle(session
				.getSessions().get(0).getSessionId());

		this.resourceAdaptor.fireEvent(handle, this.resourceAdaptor.events.get(answer.getCommandCode()) + "Answer", null, (Answer) answer.getMessage());
		
	}

	public void doOtherEvent(AppSession session, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		DiameterActivityHandle handle = new DiameterActivityHandle(session
				.getSessions().get(0).getSessionId());
		//baranowb: here we get ASR/ASA STR/STA ACR/ACA and extension
		//Still  we relly on CCA termination mechanisms, those message are sent via generic send, which does not trigger FSM
		
		
		logger.info("Diameter CCA RA :: doOtherEvent :: appSession[" + session + "], Request[" + request + "], Answer[" + answer + "]");
		

		if (answer != null)
		{
			this.resourceAdaptor.fireEvent(handle, this.resourceAdaptor.events.get(answer.getCommandCode()) + "Answer", null, (Answer) answer.getMessage());
		}
		else
		{
			this.resourceAdaptor.fireEvent(handle, this.resourceAdaptor.events.get(request.getCommandCode()) + "Request", (Request) request.getMessage(), null);
		}

	}
	


	public void stateChanged(Enum oldState, Enum newState) {
		if (logger.isInfoEnabled()) {
			logger
					.info("Diameter CCA SessionFactory :: stateChanged :: oldState["
							+ oldState + "], newState[" + newState + "]");
		}
	}

	

	public long[] getApplicationIds() {
		//FIXME: ???
		return new long[]{4};
	}

	public long getDefaultValidityTime() {
		return this.defaultValidityTime;
	}

	
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
	
	
	
	
	
	
	
	
	
	// /////////////////////
	// // CONTEXT METHODS //
	// /////////////////////
	public void sessionSupervisionTimerExpired(ServerCCASession session) {
		
		this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
		session.release();
		
	}

	public void sessionSupervisionTimerReStarted(ServerCCASession session,
			ScheduledFuture future) {
		// TODO Auto-generated method stub
		
	}

	public void sessionSupervisionTimerStarted(ServerCCASession session,
			ScheduledFuture future) {
		// TODO Auto-generated method stub
		
	}

	public void sessionSupervisionTimerStopped(ServerCCASession session,
			ScheduledFuture future) {
		// TODO Auto-generated method stub
		
	}

	public void timeoutExpired(Request request) {
		//FIXME ???
		
	}

	public void denyAccessOnDeliverFailure(
			ClientCCASession clientCCASessionImpl, Message request) {
		// TODO Auto-generated method stub
		
	}

	public void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
		
	}

	public void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
		this.resourceAdaptor.sessionDestroyed(clientCCASessionImpl.getSessions().get(0).getSessionId(), clientCCASessionImpl);
		clientCCASessionImpl.release();
		
	}

	public long getDefaultCCFHValue() {
		
		return defaultCreditControlFailureHandling;
	}

	public long getDefaultDDFHValue() {
		
		return defaultDirectDebitingFailureHandling;
	}

	public long getDefaultTxTimerValue() {
		
		return defaultTxTimerValue;
	}

	public void grantAccessOnDeliverFailure(
			ClientCCASession clientCCASessionImpl, Message request) {
		// TODO Auto-generated method stub
		
	}

	public void grantAccessOnFailureMessage(
			ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
		
	}

	public void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
		
	}

	public void indicateServiceError(ClientCCASession clientCCASessionImpl) {
		// TODO Auto-generated method stub
		
	}

	public void txTimerExpired(ClientCCASession session) {
		
			this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
			session.release();
			
		
		
	}

	

}
