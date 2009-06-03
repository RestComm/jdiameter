package org.jdiameter.client.impl.app.cca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.client.impl.app.cca.Event.Type;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.cca.ClientCCASessionState;
import org.jdiameter.common.api.app.cca.ICCAMessageFactory;
import org.jdiameter.common.api.app.cca.IClientCCASessionContext;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.acc.AccountAnswerImpl;
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.SessionTermAnswerImpl;
import org.jdiameter.common.impl.app.auth.SessionTermRequestImpl;
import org.jdiameter.common.impl.app.cca.AppCCASessionImpl;

public class ClientCCASessionImpl extends AppCCASessionImpl implements
		ClientCCASession, NetworkReqListener, EventListener<Request, Answer> {

	protected boolean stateless = true;
	protected boolean statelessModeSet=false;
	protected ClientCCASessionState state = ClientCCASessionState.IDLE;
	protected ICCAMessageFactory factory = null;
	//protected String destHost, destRealm;
	protected String originHost,originRealm;
	
	
	protected Lock sendAndStateLock = new ReentrantLock();
	protected long[] authAppIds = new long[] { 4 };
	protected ClientCCASessionListener listener = null;
	protected IClientCCASessionContext context = null;
	protected ScheduledFuture txFuture = null;
	protected static final Set<Integer> temporaryErrorCodes;
	private static final long TX_TIMER_DEFAULT_VALUE = 10;

	protected int gatheredCCFH = -300;
	protected int gatheredDDFH = -300;
	protected int gatheredRequestedAction = -300;

	protected static final int CCFH_TERMINATE = 0;
	protected static final int CCFH_CONTINUE = 1;
	protected static final int CCFH_RETRY_AND_TERMINATE = 2;

	protected static final int DIAMETER_END_USER_SERVICE_DENIED = 4010;
	private static final long CREDIT_CONTROL_NOT_APPLICABLE = 4011;
	private static final long USER_UNKNOWN = 5030;

	private static final long DIRECT_DEBITING = 0;
	private static final long REFUND_ACCOUNT = 1;
	private static final long CHECK_BALANCE = 2;
	private static final long PRICE_ENQUIRY = 3;

	private static final long TERMINATE_OR_BUFFER = 0;
	private static final long CONTINUE = 1;
	/**
	 * This is buffered message, there can be only one, specs do not say a thing
	 * about multiple
	 */
	private Message buffer = null;

	static {
		HashSet<Integer> tmp = new HashSet<Integer>();
		// FIXME: add codes
		// DIAMETER_TOO_BUSY
		tmp.add(3004);
		// DIAMETER_UNABLE_TO_DELIVER
		tmp.add(3002);
		// DIAMETER_LOOP_DETECTED
		tmp.add(3005);
		temporaryErrorCodes = Collections.unmodifiableSet(tmp);
	}

	// FIXME: This is not described, but in FSM - transitions go from PendingI
	// -> PendingI and PendungU -> PendingU in two cases - TermRequest is to be
	// sent and UpdateRequest
	// Indicating that messages are queued and sent once response is received
	// (possibly) - once session goe sinto Open state this queue is looked UP :]
	// This should be done by app, but if for some reason its not, we handle it.
	protected ArrayList<Event> eventQueue = new ArrayList<Event>();

	
	public ClientCCASessionImpl(ICCAMessageFactory fct, SessionFactory sf, ClientCCASessionListener lst)
	{
		this(null,fct,sf,lst);
	}
	public ClientCCASessionImpl(String sessionId, ICCAMessageFactory fct, SessionFactory sf, ClientCCASessionListener lst)
	{
		if (lst == null)
			throw new IllegalArgumentException("Listener can not be null");
		if (fct.getApplicationIds() == null)
			throw new IllegalArgumentException("ApplicationId can not be less than zer0");
		if(lst instanceof IServerCCASessionContext)
		{
			context=(IClientCCASessionContext)lst;
		}
		authAppIds = fct.getApplicationIds();
		listener = lst;
		factory = fct;
		try {
			if (sessionId == null) {
				session = sf.getNewSession();
			} else {
				session = sf.getNewSession(sessionId);
			}
			session.setRequestListener(this);
		} catch (InternalException e) {
			throw new IllegalArgumentException(e);
		}

	}
	
	
	
	protected int getLocalCCFH() {
		int CCFH = -1;
		if (gatheredCCFH >= 0) {
			CCFH = gatheredCCFH;
		} else {
			CCFH = context.getDefaultCCFHValue();
		}
		return CCFH;
	}

	protected int getLocalDDFH() {
		int DDFH = -1;
		if (gatheredDDFH >= 0) {
			DDFH = gatheredDDFH;
		} else {
			DDFH = context.getDefaultDDFHValue();
		}
		return DDFH;
	}

	public void sendCreditControlRequest(JCreditControlRequest request)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		extractFHAVPs(request, null);
		this.handleEvent(new Event(true, request, null));

	}

	public void sendReAuthAnswer(ReAuthAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {

		this.handleEvent(new Event(Event.Type.SEND_RAA, null, answer));

	}

	public boolean isStateless() {
		return this.stateless;
	}

	public <E> E getState(Class<E> stateType) {
		return stateType == ClientCCASessionState.class ? (E) state : null;
	}

	public boolean handleEvent(StateEvent event) throws InternalException,
			OverloadException {

		return this.isStateless() ? handleEventForEventBased(event)
				: handleEventForSessionBased(event);
	}

	protected boolean handleEventForEventBased(StateEvent event)
			throws InternalException, OverloadException {

		try {
			sendAndStateLock.lock();
			Event localEvent = (Event) event;
			Event.Type eventType = (Type) localEvent.getType();
			switch (this.state) {

			case IDLE:
				switch (eventType) {
				case SEND_EVENT_REQUEST:
					// Action: send initial request, starTx, move to PendingI
					// state
					// However failure handling is complicated, so we shift
					// state first
					startTx((JCreditControlRequest) localEvent.getRequest());
					setState(ClientCCASessionState.PENDING_EVENT);
					try {
						dispatchEvent(localEvent.getRequest());
					} catch (Exception e) {
						// This handles failure to send in PendingI state in FSM
						// table
						e.printStackTrace();
						handleSendFailure(e, eventType, localEvent.getRequest()
								.getMessage());
					}
					break;
				default:
					logger.error("Wrong event type on " + state + " state:"
							+ eventType);
					break;
				}
				break;

			case PENDING_EVENT:

				switch (eventType) {

				case RECEIVE_EVENT_ANSWER:
					AppAnswerEvent answer = (AppAnswerEvent) localEvent
							.getAnswer();
					try {
						if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
							// stopTx();
							setState(ClientCCASessionState.IDLE, false);
						}
						if (isProvisional(answer.getResultCodeAvp()
								.getUnsigned32())) {
						} else if (isFailure(answer.getResultCodeAvp()
								.getUnsigned32())) {
							handleFailureMessage((JCreditControlAnswer) answer,
									(JCreditControlRequest) localEvent
											.getRequest(), eventType);
						}

						deliverCCAnswer((JCreditControlRequest) localEvent
								.getRequest(),
								(JCreditControlAnswer) localEvent.getAnswer());
					} catch (AvpDataException ade) {
						// FIXME?
						ade.printStackTrace();
						setState(ClientCCASessionState.IDLE, false);
					}
					break;
				case Tx_TIMER_FIRED:
					handleTxExpires(localEvent.getRequest().getMessage());
					break;
				default:
					logger.error("Wrong event type on " + state + " state:"
							+ eventType);
					break;
				}

				break;

			case PENDING_BUFFERED:

				switch (eventType) {
				case RECEIVE_EVENT_ANSWER:
					// We should delete request but since we remove it once we
					// resend it so....
					// Its always IDLE here
					// FIXME: xxxxxxxxxxxxxx to rfc
					setState(ClientCCASessionState.IDLE, false);
					buffer = null;
					listener.doCreditControlAnswer(this,
							(JCreditControlRequest) localEvent.getRequest(),
							(JCreditControlAnswer) localEvent.getAnswer());
					break;
				default:
					logger.error("Wrong event type on " + state + " state:"
							+ eventType);
					break;
				}

				break;

			default:
				logger.error("Wrong event type on " + state + " state??:"
						+ eventType);
				break;

			}

			doEndChecks();
			return true;
		} catch (Exception e) {

			throw new InternalException(e);
		} finally {

			sendAndStateLock.unlock();
		}

	}

	protected boolean handleEventForSessionBased(StateEvent event)
			throws InternalException, OverloadException {

		try {
			sendAndStateLock.lock();
			Event localEvent = (Event) event;
			Event.Type eventType = (Type) localEvent.getType();
			switch (this.state) {

			// IDLE BLOCK - only SEND_INITIAL_REQUEST event type is permited!!!
			case IDLE:
				switch (eventType) {
				case SEND_INITIAL_REQUEST:
					// Action: send initial request, starTx, move to PendingI
					// state
					// However failure handling is complicated, so we shift
					// state first
					startTx((JCreditControlRequest) localEvent.getRequest());
					setState(ClientCCASessionState.PENDING_INITIAL);
					try {
						dispatchEvent(localEvent.getRequest());
					} catch (Exception e) {
						// This handles failure to send in PendingI state in FSM
						// table
						handleSendFailure(e, eventType, localEvent.getRequest()
								.getMessage());
					}

				default:
					logger.error("Wrong event type on " + state + " state??:"
							+ eventType);
					break;
				}

				break;

			// ////////////////////////
			// PENDING_INITIAL BLOCK //
			// /////////////////////////
			case PENDING_INITIAL:
				AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
				switch (eventType) {
				case RECEIVED_INITIAL_ANSWER:
					try {
						if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
							stopTx();
							setState(ClientCCASessionState.OPEN);
						}
						if (isProvisional(answer.getResultCodeAvp()
								.getUnsigned32())) {
						} else if (isFailure(answer.getResultCodeAvp()
								.getUnsigned32())) {
							handleFailureMessage((JCreditControlAnswer) answer,
									(JCreditControlRequest) localEvent
											.getRequest(), eventType);
						}

						deliverCCAnswer((JCreditControlRequest) localEvent
								.getRequest(),
								(JCreditControlAnswer) localEvent.getAnswer());
					} catch (AvpDataException ade) {
						// FIXME?
						ade.printStackTrace();
						setState(ClientCCASessionState.IDLE, false);
					}
					break;
				case Tx_TIMER_FIRED:
					handleTxExpires(localEvent.getRequest().getMessage());
					break;
				case SEND_UPDATE_REQUEST:
				case SEND_TERMINATE_REQUEST:
					// we schedule, once in Open state, those messages can fly
					eventQueue.add(localEvent);
					break;
				default:
					logger.error("Wrong event type on " + state + " state??:"
							+ eventType);
					break;
				}
				break;

			// /////////////////
			// // OPEN BLOCK //
			// ////////////////
			case OPEN:
				switch (eventType) {
				case SEND_UPDATE_REQUEST:

					startTx((JCreditControlRequest) localEvent.getRequest());
					setState(ClientCCASessionState.PENDING_UPDATE);
					try {
						dispatchEvent(localEvent.getRequest());
					} catch (Exception e) {
						// This handles failure to send in PendingI state in FSM
						// table
						handleSendFailure(e, eventType, localEvent.getRequest()
								.getMessage());
					}
					break;
				case SEND_TERMINATE_REQUEST:
					setState(ClientCCASessionState.PENDING_TERMINATION);
					try {
						dispatchEvent(localEvent.getRequest());
					} catch (Exception e) {

						handleSendFailure(e, eventType, localEvent.getRequest()
								.getMessage());
					}
					break;
				case RECEIVED_RAR:
					deliverRAR((ReAuthRequest) localEvent.getRequest());
					break;
				case SEND_RAA:
					try {
						dispatchEvent(localEvent.getAnswer());
					} catch (Exception e) {

						handleSendFailure(e, eventType, localEvent.getRequest()
								.getMessage());
					}
					break;

				default:
					logger.error("Wrong event type on " + state + " state??:"
							+ eventType);
					break;
				}
				break;

			// /////////////////////////
			// // PendingUpdate BLOCK //
			// /////////////////////////
			case PENDING_UPDATE:

				answer = (AppAnswerEvent) localEvent.getAnswer();
				switch (eventType) {
				case RECEIVED_UPDATE_ANSWER:

					try {
						if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
							stopTx();
							setState(ClientCCASessionState.OPEN);
						}
						if (isProvisional(answer.getResultCodeAvp()
								.getUnsigned32())) {
						} else if (isFailure(answer.getResultCodeAvp()
								.getUnsigned32())) {
							handleFailureMessage((JCreditControlAnswer) answer,
									(JCreditControlRequest) localEvent
											.getRequest(), eventType);
						}
						deliverCCAnswer((JCreditControlRequest) localEvent
								.getRequest(),
								(JCreditControlAnswer) localEvent.getAnswer());
					} catch (AvpDataException ade) {
						// FIXME?
						ade.printStackTrace();
						setState(ClientCCASessionState.IDLE, false);
					}
					break;
				case Tx_TIMER_FIRED:
					handleTxExpires(localEvent.getRequest().getMessage());
					break;

				case SEND_UPDATE_REQUEST:
				case SEND_TERMINATE_REQUEST:
					// we schedule, once in Open state, those messages can fly
					eventQueue.add(localEvent);
					break;
				case RECEIVED_RAR:
					deliverRAR((ReAuthRequest) localEvent.getRequest());
					break;
				case SEND_RAA:
					try {
						dispatchEvent(localEvent.getAnswer());
					} catch (Exception e) {

						handleSendFailure(e, eventType, localEvent.getRequest()
								.getMessage());
					}
					break;
				}

				break;

			// ////////////////////////////
			// // PendingTERMIANTE BLOCK //
			// ////////////////////////////

			case PENDING_TERMINATION:
				switch (eventType) {
				case SEND_UPDATE_REQUEST:
					try {
						dispatchEvent(localEvent.getRequest());
						// No transition
					} catch (Exception e) {
						// This handles failure to send in PendingI state in FSM
						// table
						// handleSendFailure(e, eventType);
					}
					break;
				case RECEIVED_TERMINATED_ANSWER:
					setState(ClientCCASessionState.IDLE, false);
					deliverCCAnswer((JCreditControlRequest) localEvent
							.getRequest(), (JCreditControlAnswer) localEvent
							.getAnswer());

				default:
					logger.error("Wrong event type on " + state + " state??:"
							+ eventType);
					break;
				}
				break;

			default:
				// any other state is bad
				setState(ClientCCASessionState.IDLE, true);

			}

			doEndChecks();
			return true;
		} catch (Exception e) {

			throw new InternalException(e);
		} finally {

			sendAndStateLock.unlock();
		}

	}

	public Answer processRequest(Request request) {

		
		try{
			//FIXME: baranowb: add message validation here!!!
			//We handle CCR,STR,ACR,ASR other go into extension
			switch(request.getCommandCode())
			{
				case ReAuthAnswerImpl.code:
					handleEvent(new Event(Event.Type.RECEIVED_RAR, factory
											.createReAuthRequest(request), null));
					break;
					
					//All other go straight to listner, they dont change state machine
					//Suprisingly there is no factory.... ech
					//FIXME: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
				case SessionTermAnswer.code:
					listener.doSessionTerminationRequest(this, new SessionTermRequestImpl(request));
					break;
				case AbortSessionAnswer.code:
					listener.doAbortSessionRequest(this, new AbortSessionRequestImpl(request));
					break;
				case AccountAnswer.code:
					listener.doAccountingRequest(this, new AccountRequestImpl(request));
					break;
					
					
				default:
					listener.doOtherEvent(this, new AppRequestEventImpl(request), null);
					break;
			}
			

			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
		return null;
		
		//if (request.getCommandCode() == ReAuthAnswerImpl.code) {
		//	try {
		//		handleEvent(new Event(Event.Type.RECEIVED_RAR, factory
		//				.createReAuthRequest(request), null));
		//	} catch (InternalException e) {
				
		//		e.printStackTrace();
		//	} catch (OverloadException e) {
				
		//		e.printStackTrace();
		//	}
		//} else {
			// FIXME ?????
		//	try {
		//		listener.doOtherEvent(this, new AppRequestEventImpl(request),
		//				null);
		//	} catch (InternalException e) {
				
		//		e.printStackTrace();
		//	} catch (IllegalDiameterStateException e) {
		//		
		//		e.printStackTrace();
		//	} catch (RouteException e) {
				
		//		e.printStackTrace();
		//	} catch (OverloadException e) {
		//		
		//		e.printStackTrace();
		//	}
		//}

	}

	public void receivedSuccessMessage(Request request, Answer answer) {

		
		try{
			//FIXME: baranowb: add message validation here!!!
			//We handle CCR,STR,ACR,ASR other go into extension
			switch(request.getCommandCode())
			{
				case JCreditControlAnswer.code:
					JCreditControlAnswer _answer=factory.createCreditControlAnswer(answer);
					extractFHAVPs(null,_answer );
					handleEvent(new Event(false, null, _answer));
					break;
				
					//All other go straight to listner, they dont change state machine
					//Suprisingly there is no factory.... ech
					//FIXME: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
				case SessionTermAnswer.code:
					listener.doSessionTerminationAnswer(this,null ,new SessionTermAnswerImpl(answer));
					break;
				case AbortSessionAnswer.code:
					listener.doAbortSessionAnswer(this,null, new AbortSessionAnswerImpl(answer));
					break;
				case AccountAnswer.code:
					listener.doAccountingAnswer(this,null, new AccountAnswerImpl(answer));
					break;
					
					
				default:
					listener.doOtherEvent(this, null, new AppAnswerEventImpl(answer));
					break;
			}
		
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
//		if (answer.getCommandCode() == JCreditControlAnswer.code) {
//			extractFHAVPs(null, factory.createCreditControlAnswer(answer));
//			try {
//				handleEvent(new Event(false, factory
//						.createCreditControlRequest(request), factory
//						.createCreditControlAnswer(answer)));
//			} catch (InternalException e) {
//				
//				e.printStackTrace();
//			} catch (OverloadException e) {
//				
//				e.printStackTrace();
//			}
//		} else if (answer.getCommandCode() == ReAuthAnswerImpl.code) {
//			try {
//				handleEvent(new Event(Event.Type.SEND_RAA, factory
//						.createReAuthRequest(request), factory
//						.createReAuthAnswer(answer)));
//			} catch (InternalException e) {
//				
//				e.printStackTrace();
//			} catch (OverloadException e) {
//				
//				e.printStackTrace();
//			}
//		} else {
//			// FIXME ?????
//			try {
//				listener.doOtherEvent(this, new AppRequestEventImpl(request),
//						new AppAnswerEventImpl(answer));
//			} catch (InternalException e) {
//				
//				e.printStackTrace();
//			} catch (IllegalDiameterStateException e) {
//				
//				e.printStackTrace();
//			} catch (RouteException e) {
//				
//				e.printStackTrace();
//			} catch (OverloadException e) {
//				
//				e.printStackTrace();
//			}
//		}

	}

	public void timeoutExpired(Request request) {
		
		if(request.getCommandCode()== JCreditControlAnswer.code)
		{
			try {
				handleSendFailure(null, null, request);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

	}

	protected void startTx(JCreditControlRequest request) {
		long txTimerValue = context.getDefaultTxTimerValue();
		if (txTimerValue < 0)
			txTimerValue = TX_TIMER_DEFAULT_VALUE;
		stopTx();

		System.out.println("SCHEDULING TX TIMER:"+txTimerValue);
		this.txFuture = super.scheduler.schedule(
				new TxTimerTask(this, request), txTimerValue, TimeUnit.SECONDS);
	}

	protected void stopTx() {
		if (this.txFuture != null) {
			
			this.txFuture.cancel(true);
			this.txFuture = null;
		}

	}

	protected void setState(ClientCCASessionState newState) {
		setState(newState, true);
	}

	protected void setState(ClientCCASessionState newState, boolean release) {
		try {
			IAppSessionState oldState = state;
			state = newState;
			for (StateChangeListener i : stateListeners)
				i.stateChanged((Enum) oldState, (Enum) newState);
			if (newState == ClientCCASessionState.IDLE) {
				if (release)
					this.release();
				stopTx();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public void release() {
		
		this.stopTx();
		if(super.isValid())
			super.release();
		if(super.session!=null)
			super.session.setRequestListener(null);
		super.session = null;
		if(listener!=null)
			this.removeStateChangeNotification((StateChangeListener) listener);
		this.listener = null;
		this.factory = null;
	}
	protected void handleSendFailure(Exception e, Event.Type type,
			Message request) throws Exception {

		logger.error("Failed to send message, type: " + type + ", message:\n"
				+ request, e);
		// FIXME: do we need check for RAR ?
		try {
			if (isStateless()) {

				switch (state) {
				case PENDING_EVENT:
					if (gatheredRequestedAction == CHECK_BALANCE
							|| gatheredRequestedAction == PRICE_ENQUIRY) {
						// #1
						setState(ClientCCASessionState.IDLE);
						context.indicateServiceError(this);

					} else if (gatheredRequestedAction == DIRECT_DEBITING
							&& getLocalDDFH() == TERMINATE_OR_BUFFER) {
						// #7
						setState(ClientCCASessionState.IDLE, false);
						buffer = request;
						buffer.setReTransmitted(true);
						// context.grantAccessOnDeliverFailure(this, request);
					} else if (gatheredRequestedAction == REFUND_ACCOUNT) {
						// #11
						setState(ClientCCASessionState.IDLE, false);
						buffer = request;
						buffer.setReTransmitted(true);
						// context.grantAccessOnDeliverFailure(this, request);
					} else {
						// FIXME: no transition is mentioned in specs
						setState(ClientCCASessionState.IDLE, false);
					}
					break;
				case PENDING_BUFFERED:
					setState(ClientCCASessionState.IDLE, false);
					
					//FIXME: baranowb?
					buffer=null;
					break;
				default:
					//This happens when message timesout
					logger.error("Error - wrong state on handle send failure: "
							+ state + " for event: " + type);
					break;
				}

			} else {
				// In all cases it moves to idle
				setState(ClientCCASessionState.IDLE, false);

				switch (getLocalCCFH()) {
				case CCFH_CONTINUE:
					this.context.grantAccessOnDeliverFailure(this, request);
					break;

				default:
					this.context.denyAccessOnDeliverFailure(this, request);
					break;

				}

				// throw e;
			}
		} finally {
			doEndChecks();
		}

	}

	protected void handleFailureMessage(JCreditControlAnswer event,
			JCreditControlRequest request, Event.Type type) {
		try {
			if (isStateless()) {

				// Stateless FSM part is a killer ;[
				// This has to be present.....
				switch (state) {
				case PENDING_EVENT:

					int resultCode = event.getRequestTypeAVPValue();

					if ((resultCode == DIAMETER_END_USER_SERVICE_DENIED || resultCode == USER_UNKNOWN)
							&& txFuture != null) {
						// #2
						setState(ClientCCASessionState.IDLE);
						context.denyAccessOnFailureMessage(this);
						deliverCCAnswer(request, event);
					} else if (resultCode == CREDIT_CONTROL_NOT_APPLICABLE
							&& gatheredRequestedAction == DIRECT_DEBITING) {
						// #3
						setState(ClientCCASessionState.IDLE);
						context.grantAccessOnFailureMessage(this);
						deliverCCAnswer(request, event);
					} else if (temporaryErrorCodes.contains(resultCode)) {

						if (gatheredRequestedAction == CHECK_BALANCE
								|| gatheredRequestedAction == PRICE_ENQUIRY) {
							// #1
							setState(ClientCCASessionState.IDLE);
							context.indicateServiceError(this);
							deliverCCAnswer(request, event);
						} else if (gatheredRequestedAction == DIRECT_DEBITING
								&& getLocalDDFH() == CONTINUE) {
							// #4
							setState(ClientCCASessionState.IDLE);
							context.grantAccessOnFailureMessage(this);
							deliverCCAnswer(request, event);
						} else if (gatheredRequestedAction == DIRECT_DEBITING
								&& getLocalDDFH() == CONTINUE
								&& txFuture != null) {
							// #5
							setState(ClientCCASessionState.IDLE);
							context.denyAccessOnFailureMessage(this);
							deliverCCAnswer(request, event);
						} else if (gatheredRequestedAction == REFUND_ACCOUNT) {
							// #12
							buffer = request.getMessage();
							setState(ClientCCASessionState.IDLE, false);
						} else {
							// FIXME
							setState(ClientCCASessionState.IDLE, false);
							deliverCCAnswer(request, event);
						}

					} else {
						// we are in fauilure zone isFailure(true}
						if (gatheredRequestedAction == CHECK_BALANCE
								|| gatheredRequestedAction == PRICE_ENQUIRY) {
							// #1
							setState(ClientCCASessionState.IDLE);
							context.indicateServiceError(this);
							deliverCCAnswer(request, event);
						} else if (gatheredRequestedAction == DIRECT_DEBITING
								&& getLocalDDFH() == CONTINUE) {
							// #4
							setState(ClientCCASessionState.IDLE);
							context.grantAccessOnFailureMessage(this);
							deliverCCAnswer(request, event);
						} else if (gatheredRequestedAction == DIRECT_DEBITING
								&& getLocalDDFH() == CONTINUE
								&& txFuture != null) {
							// #5
							setState(ClientCCASessionState.IDLE);
							context.denyAccessOnFailureMessage(this);
							deliverCCAnswer(request, event);
						} else if (gatheredRequestedAction == REFUND_ACCOUNT) {
							// #10

							buffer = null;
							setState(ClientCCASessionState.IDLE);
							context.indicateServiceError(this);
							deliverCCAnswer(request, event);
						} else {
							// FIXME
							setState(ClientCCASessionState.IDLE, false);
							deliverCCAnswer(request, event);
						}

					}

					break;
				case PENDING_BUFFERED:
					buffer = null;
					setState(ClientCCASessionState.IDLE, false);
					break;
				default:
					logger.error("Bad state: " + state
							+ ", on failure message: " + type);
				}

			} else {
				// FIXME CRAP what a pain
				long responseCode = event.getResultCodeAvp().getUnsigned32();
				switch (state) {
				case PENDING_INITIAL:
					if (responseCode == CREDIT_CONTROL_NOT_APPLICABLE) {
						setState(ClientCCASessionState.IDLE, false);
						context.grantAccessOnFailureMessage(this);

					} else if ((responseCode == DIAMETER_END_USER_SERVICE_DENIED)
							|| (responseCode == USER_UNKNOWN)) {
						setState(ClientCCASessionState.IDLE, false);
						context.denyAccessOnFailureMessage(this);
					} else {
						// Temporary errors and others
						switch (getLocalCCFH()) {
						case CCFH_CONTINUE:
							setState(ClientCCASessionState.IDLE, false);
							context.grantAccessOnFailureMessage(this);
							break;
						case CCFH_TERMINATE:
						case CCFH_RETRY_AND_TERMINATE:
							setState(ClientCCASessionState.IDLE, false);
							context.denyAccessOnFailureMessage(this);
							break;

						default:
							logger
									.error("Bad value of CCFH: "
											+ getLocalCCFH());
							break;

						}
					}
					break;
				case PENDING_UPDATE:
					if (responseCode == CREDIT_CONTROL_NOT_APPLICABLE) {
						setState(ClientCCASessionState.IDLE, false);
						context.grantAccessOnFailureMessage(this);

					} else if (responseCode == DIAMETER_END_USER_SERVICE_DENIED) {
						setState(ClientCCASessionState.IDLE, false);
						context.denyAccessOnFailureMessage(this);
					} else {
						// Temporary errors and others
						switch (getLocalCCFH()) {
						case CCFH_CONTINUE:
							setState(ClientCCASessionState.IDLE, false);
							context.grantAccessOnFailureMessage(this);
							break;
						case CCFH_TERMINATE:
						case CCFH_RETRY_AND_TERMINATE:
							setState(ClientCCASessionState.IDLE, false);
							context.denyAccessOnFailureMessage(this);
							break;

						default:
							logger
									.error("Bad value of CCFH: "
											+ getLocalCCFH());
							break;

						}
					}
					break;
				default:
					logger.error("Bad state: " + state
							+ ", on failure message: " + type);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void handleTxExpires(Message m) {
		ClientCCASessionState newState = state;
		try {
			if (isStateless()) {
				if (gatheredRequestedAction == CHECK_BALANCE
						|| gatheredRequestedAction == PRICE_ENQUIRY) {
					// #1
					setState(ClientCCASessionState.IDLE);
					context.indicateServiceError(this);
				} else if (gatheredRequestedAction == DIRECT_DEBITING) {
					setState(ClientCCASessionState.IDLE);
					context.grantAccessOnTxExpire(this);
				} else if (gatheredRequestedAction == REFUND_ACCOUNT) {
					buffer = m;
					buffer.setReTransmitted(true);
					setState(ClientCCASessionState.IDLE, false);
				}
			} else {
				switch (state) {
				case PENDING_INITIAL:

					switch (getLocalCCFH()) {
					case CCFH_CONTINUE:
					case CCFH_RETRY_AND_TERMINATE:
						newState = ClientCCASessionState.PENDING_INITIAL;
						context.grantAccessOnTxExpire(this);
						break;
					case CCFH_TERMINATE:
						context.denyAccessOnTxExpire(this);
						break;

					default:
						logger.error("Bad value of CCFH: " + getLocalCCFH());
						break;

					}

					break;

				case PENDING_UPDATE:

					switch (getLocalCCFH()) {
					case CCFH_CONTINUE:
					case CCFH_RETRY_AND_TERMINATE:
						newState = ClientCCASessionState.PENDING_UPDATE;
						context.grantAccessOnTxExpire(this);
						break;
					case CCFH_TERMINATE:
						context.denyAccessOnTxExpire(this);
						break;

					default:
						logger.error("Bad value of CCFH: " + getLocalCCFH());
						break;

					}
					break;
				default:
					logger.error("Possibly bad state on txExpire: " + state);
					break;

				}
			}
		} finally {
			if (state == newState)
				setState(ClientCCASessionState.IDLE, true);
			// else
			// setState(newState, false);
		}

	}

	/**
	 * This makes checks on queue, moves it to proper state if event there is
	 * present on Open state ;]
	 */
	protected void doEndChecks() {

		if (isStateless()) {
			if (buffer != null) {
				setState(ClientCCASessionState.PENDING_BUFFERED);
				try {
					dispatchEvent(new AppRequestEventImpl(buffer));
				} catch (Exception e) {
					try {
						handleSendFailure(e, Event.Type.SEND_EVENT_REQUEST,
								buffer);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
				}

			}
		} else {
			if (state == ClientCCASessionState.OPEN && eventQueue.size() > 0) {
				try {
					this.handleEvent(eventQueue.remove(0));
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}
	}

	protected void deliverCCAnswer(JCreditControlRequest request,
			JCreditControlAnswer answer) {

		try {
			listener.doCreditControlAnswer(this, request, answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void extractFHAVPs(JCreditControlRequest request,
			JCreditControlAnswer answer) {
		if (answer != null) {

			try {
				if (answer.isCreditControlFailureHandlingAVPPresent())
					this.gatheredCCFH = answer
							.getCredidControlFailureHandlingAVPValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (answer.isDirectDebitingFailureHandlingAVPPresent())
					this.gatheredCCFH = answer
							.getDirectDebitingFailureHandlingAVPValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!statelessModeSet)
			{
				statelessModeSet=true;
				if(answer.isRequestTypeAVPPresent())
				{
					if(answer.getRequestTypeAVPValue()==4)
					{
						stateless=true;
					}else
					{
						stateless=false;
					}
				}else
				{
					//FIXME: send error ?
				}
			}
			
			
		} else if (request != null) {
			try {
				if (request.isRequestedActionAVPPresent()) {
					this.gatheredRequestedAction = request
							.getRequestedActionAVPValue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if(!statelessModeSet)
			{
				statelessModeSet=true;
				if(request.isRequestTypeAVPPresent())
				{
					if(request.getRequestTypeAVPValue()==4)
					{
						stateless=true;
					}else
					{
						stateless=false;
					}
				}else
				{
					//FIXME: send error ?
				}
			}
		}
	}

	protected void deliverRAR(ReAuthRequest request) {
		try {
			listener.doReAuthRequest(this, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void dispatchEvent(AppEvent event) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {


		session.send(event.getMessage(), this);
		// Store last destinmation information
		

	}

	
	
	protected boolean isProvisional(long unsigned32) {
		return unsigned32 < 2000 && unsigned32 >= 1000;
	}

	protected boolean isSuccess(long code) {
		if (code < 3000 && code >= 2000) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isFailure(long code) {
		if (!isProvisional(code)
				&& !isSuccess(code)
				&& ((code >= 3000 && code < 4000) || (code >= 5000 && code < 6000))
				&& !temporaryErrorCodes.contains(code)) {
			return true;
		} else {
			return false;
		}
	}

	private class TxTimerTask implements Runnable {

		private ClientCCASession session = null;
		private JCreditControlRequest request = null;

		private TxTimerTask(ClientCCASession session,
				JCreditControlRequest request) {
			super();
			this.session = session;
			this.request=request;
		}

		public void run() {
			try {
				sendAndStateLock.lock();
				System.out.println("FIRED TX TIMER");
				txFuture = null;
				try {
					context.txTimerExpired(session);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handleEvent(new Event(Event.Type.Tx_TIMER_FIRED,
						request == null ? null : request, null));
			} catch (InternalException e) {
				
				e.printStackTrace();
			} catch (OverloadException e) {
				
				e.printStackTrace();
			} finally {
				sendAndStateLock.unlock();
			}

		}

	}

}
