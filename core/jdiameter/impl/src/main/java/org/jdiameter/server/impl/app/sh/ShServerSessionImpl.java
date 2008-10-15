package org.jdiameter.server.impl.app.sh;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.ServerShSessionListener;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.api.app.sh.ShSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.ShSession;

/**
 * Basic implementation of ShServerSession - can be one time - for UDR,PUR and
 * constant for SNR-PNR pair, in case when SNA contains response code from range
 * different than 2001-2004(success codes) user is responsible for maintaing
 * state - releasing etc, same goes if result code is contained
 * Experimental-Result AVP <br>
 * If ShSession moves to ShSessionState.TERMINATED - it means that no further
 * messages can be received via it and it should be discarded. <br>
 * <br>
 * Super project: mobicents-jainslee-server <br>
 * 10:53:02 2008-09-05 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShServerSessionImpl extends ShSession implements ServerShSession, EventListener<Request, Answer>, NetworkReqListener {
	protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
	protected boolean stateless = false;
	protected IShMessageFactory factory = null;
	protected String destHost, destRealm;
	protected Lock sendAndStateLock = new ReentrantLock();
	protected ServerShSessionListener listener;
	protected long appId = -1;
	protected ScheduledFuture sft = null;

	public ShServerSessionImpl(IShMessageFactory fct, SessionFactory sf, ServerShSessionListener lst) {
		this(null, fct, sf, lst);
	}

	public ShServerSessionImpl(String sessionId, IShMessageFactory fct, SessionFactory sf, ServerShSessionListener lst) {

		if (lst == null)
			throw new IllegalArgumentException("Listener can not be null");
		if (fct.getApplicationId() < 0)
			throw new IllegalArgumentException("ApplicationId can not be less than zer0");
		appId = fct.getApplicationId();
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

	public void sendProfileUpdateAnswer(ProfileUpdateAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		send(Event.Type.SEND_PROFILE_UPDATE_ANSWER, null, answer);

	}

	public void sendPushNotificationRequest(PushNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		send(Event.Type.SEND_PUSH_NOTIFICATION_REQUEST,request,null);

	}

	public void sendSubscribeNotificationsAnswer(SubscribeNotificationsAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		send(Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER, null, answer);
	}

	public void sendUserDataAnswer(UserDataAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		send(Event.Type.SEND_USER_DATA_ANSWER, null, answer);

	}

	public void receivedSuccessMessage(Request request, Answer answer) {
		
		try {
			sendAndStateLock.lock();
			if (request.getApplicationId() == factory.getApplicationId()) {

				if (request.getCommandCode() == ProfileUpdateRequestImpl.code) {
					handleEvent(new Event(Event.Type.RECEIVE_PUSH_NOTIFICATION_ANSWER, factory.createPushNotificationRequest(request), factory.createPushNotificationAnswer(answer)));
					return;
				} 
			}

			listener.doOtherEvent(this, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		} finally {
			sendAndStateLock.unlock();
		}

	}

	public void timeoutExpired(Request request) {
		try {
			sendAndStateLock.lock();
			if (request.getApplicationId() == factory.getApplicationId()) {

				if (request.getCommandCode() == ProfileUpdateRequestImpl.code) {
					handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createPushNotificationRequest(request), null));
					return;
				} 
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		} finally {
			sendAndStateLock.unlock();
		}

	}

	public Answer processRequest(Request request) {
		try {
			if (request.getApplicationId() == factory.getApplicationId()) {
				if (request.getCommandCode() == org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl.code) {
					handleEvent(new Event(Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST, factory.createSubscribeNotificationsRequest(request), null));
					return null;
				}else if(request.getCommandCode() == org.jdiameter.common.impl.app.sh.UserDataRequestImpl.code)
				{
					handleEvent(new Event(Event.Type.RECEIVE_USER_DATA_REQUEST,factory.createUserDataRequest(request),null));
					return null;
				}else if(request.getCommandCode() == org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl.code)
				{
					handleEvent(new Event(Event.Type.RECEIVE_PROFILE_UPDATE_REQUEST,factory.createProfileUpdateRequest(request),null));
					return null;
				}else
				{
					//FIXME: ????
				}

			}

			listener.doOtherEvent(this, new AppRequestEventImpl(request), null);

		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		}
		return null;
	}

	public <E> E getState(Class<E> stateType) {
		return stateType == ShSessionState.class ? (E) state : null;
	}

	public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
		
		try {
			sendAndStateLock.lock();
			ShSessionState oldState = this.state;
			ShSessionState newState = this.state;
			try {

				Event localEvent = (Event) event;
				AppEvent request = localEvent.getReqeust();
				switch (state) {
				case NOTSUBSCRIBED:

					if (event.getType() == Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST) {
						
						//Do nothing, we have to wait for response send callback

					} else if (event.getType() == Event.Type.RECEIVE_PROFILE_UPDATE_REQUEST) {
						newState=ShSessionState.TERMINATED;
					}else if (event.getType() == Event.Type.RECEIVE_USER_DATA_REQUEST) {
						newState=ShSessionState.TERMINATED;
					}else if(event.getType()== Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER)
					{
						
						newState=doSNX(localEvent.getAnswer());
					} else if (event.getType() == Event.Type.TIMEOUT_EXPIRES) {
						//newState = ShSessionState.TERMINATED;
						//FIXME: What happens here?
					} else {
						// Other messages just make it go into terminated state
						// and release
						newState = ShSessionState.TERMINATED;

					}

					break;
				case SUBSCRIBED:
					if(event.getType()== Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER)
					{
						
						newState=doSNX(localEvent.getAnswer());
					}
					//FIXME: Any change here - even on timeout?
					break;
				case TERMINATED:
					// We shouldnt receive anything here!!!
					break;
				}

				// Now lets deliver
				// FIXME: Should we look if we are in terminated state?
				try {
					switch ((Event.Type) localEvent.getType()) {
					case RECEIVE_PROFILE_UPDATE_REQUEST:
						listener.doProfileUpdateRequestEvent(this, (ProfileUpdateRequest) localEvent.getReqeust());
						break;
					case RECEIVE_PUSH_NOTIFICATION_ANSWER:
						listener.doPushNotificationAnswerEvent(this, (PushNotificationRequest) localEvent.getReqeust(), (PushNotificationAnswer) localEvent.getAnswer());
						break;
					case RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST:
						listener.doSubscribeNotificationsRequestEvent(this, (SubscribeNotificationsRequest) localEvent.getReqeust());
						break;
					case RECEIVE_USER_DATA_REQUEST:
						listener.doUserDataRequestEvent(this, (UserDataRequest) localEvent.getReqeust());
						break;
					case SEND_PROFILE_UPDATE_ANSWER:
					case SEND_PUSH_NOTIFICATION_REQUEST:
					case SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER:
					case SEND_USER_DATA_ANSWER:
					case TIMEOUT_EXPIRES:
						break;

					default:
						logger.error("Wrong messageT[" + localEvent.getType() + "] R[" + localEvent.getReqeust() + "] A[" + localEvent.getAnswer() + "]");
					}
				} catch (IllegalDiameterStateException idse) {
					throw new InternalException(idse);
				} catch (RouteException re) {

					throw new InternalException(re);
				}
			} finally {
				if (newState != oldState) {
					setState(newState);

				}
			}
		} finally {
			sendAndStateLock.unlock();
		}

		return true;
	}

	public boolean isStateless() {

		return stateless;
	}

	protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
	}
	
	protected void setState(ShSessionState newState) {
		setState(newState, true);
	}

	protected void setState(ShSessionState newState, boolean release) {
		IAppSessionState oldState = state;
		state = newState;
		for (StateChangeListener i : stateListeners)
			i.stateChanged((Enum) oldState, (Enum) newState);
		if (newState == ShSessionState.TERMINATED) {
			if (release)
				this.release();
			if (sft != null) {
				sft.cancel(true);
				sft = null;
			}
		}
	}


	public void release() {
		try {
			sendAndStateLock.lock();
			if (state != ShSessionState.TERMINATED) {
				setState(ShSessionState.TERMINATED, false);
				session.release();
				super.release();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);
		} finally {
			sendAndStateLock.unlock();
		}
	}
	
	protected long extractExpirationTime(Message answer) {
		return -1;
	}
	
	protected ShSessionState doSNX(AppEvent answer) throws InternalException {
		ShSessionState newState = state;
		AvpSet set = answer.getMessage().getAvps();
		long resultCode = -1;
		// Experimental-Result-Code:297, Result-Code:268
		Avp avp = null;
		if (set.getAvp(297) != null) {
			avp = set.getAvp(297);
			// FIXME: how to handle that?
			newState = ShSessionState.SUBSCRIBED;
		} else {
			avp = set.getAvp(268);
			try {
				resultCode = avp.getUnsigned32();
				if (resultCode > 2000 && resultCode < 2005) {
					long expiryTime = extractExpirationTime(answer.getMessage());
					if (expiryTime >= 0) {
						if(this.sft!=null)
						{
							this.sft.cancel(true);
						}
						this.sft = scheduler.schedule(new Runnable() {

							public void run() {
								try {
									sendAndStateLock.lock();
									if (state != ShSessionState.TERMINATED)
										setState(ShSessionState.TERMINATED);

								} finally {
									sendAndStateLock.unlock();
								}
							}
						}, expiryTime, TimeUnit.SECONDS);
					} else {
						// FIXME:we relly on user?
					}
					newState = ShSessionState.SUBSCRIBED;
				} else {
					// its a failure?
					newState = ShSessionState.TERMINATED;
				}
			} catch (AvpDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return newState;
	}
}
