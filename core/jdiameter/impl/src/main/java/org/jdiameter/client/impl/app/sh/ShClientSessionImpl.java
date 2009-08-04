package org.jdiameter.client.impl.app.sh;

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
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.api.app.sh.ShSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.ShSession;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;

/**
 * Basic implementation of ShClientSession - can be one time - for UDR,PUR and
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
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShClientSessionImpl extends ShSession implements ClientShSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
  protected boolean stateless = false;
  protected IShMessageFactory factory = null;
  protected String destHost, destRealm;
  protected Lock sendAndStateLock = new ReentrantLock();
  protected ClientShSessionListener listener;
  protected long appId = -1;
  protected ScheduledFuture sft = null;

  public ShClientSessionImpl(IShMessageFactory fct, SessionFactory sf, ClientShSessionListener lst) {
    this(null, fct, sf, lst);
  }

  public ShClientSessionImpl(String sessionId, IShMessageFactory fct, SessionFactory sf, ClientShSessionListener lst) {

    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    appId = fct.getApplicationId();
    listener = lst;
    factory = fct;
    try {
      if (sessionId == null) {
        session = sf.getNewSession();
      }
      else {
        session = sf.getNewSession(sessionId);
      }
      session.setRequestListener(this);
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public Answer processRequest(Request request) {
    try {
      if (request.getApplicationId() == factory.getApplicationId()) {
        if (request.getCommandCode() == org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_PUSH_NOTIFICATION_REQUEST, factory.createPushNotificationRequest(request), null));
          return null;
        }
      }

      listener.doOtherEvent(this, new AppRequestEventImpl(request), null);
    }
    catch (Exception e) {
      logger.debug("Failed to process request {}", request, e);
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
        AppEvent answer = localEvent.getAnswer();
        switch (state) {
        case NOTSUBSCRIBED:
          if (event.getType() == Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER) {
            newState = doSNX(answer);
          }
          else if (event.getType() == Event.Type.RECEIVE_PUSH_NOTIFICATION_REQUEST) {
            newState = ShSessionState.SUBSCRIBED;
          }
          else if (event.getType() == Event.Type.TIMEOUT_EXPIRES) {
            newState = ShSessionState.TERMINATED;
          }
          else if(event.getType() != Event.Type.SEND_USER_DATA_REQUEST && event.getType() != Event.Type.SEND_PROFILE_UPDATE_REQUEST && event.getType() != Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_REQUEST ){
            // Other messages just make it go into terminated state and release
            newState = ShSessionState.TERMINATED;
          }
          break;
        case SUBSCRIBED:
          if (event.getType() == Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER) {
            newState = doSNX(answer);
          }
          else if (event.getType() == Event.Type.TIMEOUT_EXPIRES) {
            if (localEvent.getRequest().getCommandCode() == SubscribeNotificationsRequestImpl.code) {
              newState = ShSessionState.TERMINATED;
            }
          }
          else {
            // FIXME: What about timeout here?
          }
          break;
        case TERMINATED:
          // We shouldnt receive anything here
          break;
        }

        // Do the delivery
        // FIXME: Should we look if we are in terminated state?
        try {
          switch ((Event.Type) localEvent.getType()) {
          case RECEIVE_PUSH_NOTIFICATION_REQUEST:
            listener.doPushNotificationRequestEvent(this, new PushNotificationRequestImpl( (Request) localEvent.getRequest().getMessage() ));
            break;
          case RECEIVE_PROFILE_UPDATE_ANSWER:
            listener.doProfileUpdateAnswerEvent(this, null, new ProfileUpdateAnswerImpl( (Answer) localEvent.getAnswer().getMessage()));
            break;
          case RECEIVE_USER_DATA_ANSWER:
            listener.doUserDataAnswerEvent(this, null, new UserDataAnswerImpl((Answer) localEvent.getAnswer().getMessage()));
            break;
          case RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER:
            listener.doSubscribeNotificationsAnswerEvent(this, null, new SubscribeNotificationsAnswerImpl( (Answer) localEvent.getAnswer().getMessage()));
            break;
          case SEND_PROFILE_UPDATE_REQUEST:
          case SEND_PUSH_NOTIFICATION_ANSWER:
          case SEND_SUBSCRIBE_NOTIFICATIONS_REQUEST:
          case SEND_USER_DATA_REQUEST:
          case TIMEOUT_EXPIRES:
            // TODO Anything here?
            break;
          default:
            logger.error("Wrong message type={} req={} ans={}", new Object[]{localEvent.getType(), localEvent.getRequest(), localEvent.getAnswer()});
          }
        }
        catch (IllegalDiameterStateException idse) {
          throw new InternalException(idse);
        }
        catch (RouteException re) {
          throw new InternalException(re);
        }
      }
      finally {
        if (newState != oldState) {
          setState(newState);
        }
      }
    }
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }

  protected ShSessionState doSNX(AppEvent answer) throws InternalException {
    ShSessionState newState = state;
    AvpSet set = answer.getMessage().getAvps();
    long resultCode = -1;

    try {
      Avp avp = set.getAvp(Avp.EXPERIMENTAL_RESULT) != null ? 
          set.getAvp(Avp.EXPERIMENTAL_RESULT).getGrouped().getAvp(Avp.EXPERIMENTAL_RESULT_CODE) : 
            set.getAvp(Avp.RESULT_CODE);

          resultCode = avp.getUnsigned32();
          if (resultCode >= 2000 && resultCode < 3000) {
            long expiryTime = extractExpirationTime(answer.getMessage());
            if (expiryTime >= 0) {
              if (this.sft != null) {
                this.sft.cancel(true);
              }
              this.sft = scheduler.schedule(new Runnable() {
                public void run() {
                  try {
                    sendAndStateLock.lock();
                    if (state != ShSessionState.TERMINATED) {
                      setState(ShSessionState.TERMINATED);
                    }
                  }
                  finally {
                    sendAndStateLock.unlock();
                  }
                }
              }, expiryTime, TimeUnit.SECONDS);
            }
            else {
              // FIXME: We relly on user?
            }
            newState = ShSessionState.SUBSCRIBED;
          }
          else {
            // its a failure?
            newState = ShSessionState.TERMINATED;
          }
    }
    catch (AvpDataException e) {
      logger.debug("Could not retrieve Result-Code from Message", e);
    }

    return newState;
  }

  public boolean isStateless() {
    return stateless;
  }

  public void sendProfileUpdateRequest(ProfileUpdateRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PROFILE_UPDATE_REQUEST, request, null);
  }

  public void sendPushNotificationAnswer(PushNotificationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PUSH_NOTIFICATION_ANSWER, null, answer);
  }

  public void sendSubscribeNotificationsRequest(SubscribeNotificationsRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_REQUEST, request, null);
  }

  public void sendUserDataRequest(UserDataRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_USER_DATA_REQUEST, request, null);
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
      AppEvent event = null;
      if (request != null) {
        event = request;
      }
      else {
        event = answer;
      }
      session.send(event.getMessage(), this);

      if(request != null)
      {
        AvpSet avps = request.getMessage().getAvps();
        Avp a = null;
        // Store last destinmation information
        if((a = avps.getAvp(Avp.DESTINATION_REALM)) != null) {
          destRealm = a.getOctetString();         
        }
        if((a = avps.getAvp(Avp.DESTINATION_HOST)) != null) {
          destHost = a.getOctetString();         
        }
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    try {
      sendAndStateLock.lock();
      if (request.getApplicationId() == factory.getApplicationId()) {
        if (request.getCommandCode() == ProfileUpdateRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_PROFILE_UPDATE_ANSWER, factory.createProfileUpdateRequest(request), factory.createProfileUpdateAnswer(answer)));
          return;
        }
        else if (request.getCommandCode() == UserDataRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_USER_DATA_ANSWER, factory.createUserDataRequest(request), factory.createUserDataAnswer(answer)));
          return;
        }
        else if (request.getCommandCode() == SubscribeNotificationsRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER, factory.createSubscribeNotificationsRequest(request), factory
              .createSubscribeNotificationsAnswer(answer)));
          return;
        }
      }
      listener.doOtherEvent(this, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
    }
    catch (Exception e) {
      logger.debug("Failed to process success message", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public void timeoutExpired(Request request) {
    try {
      sendAndStateLock.lock();
      if (request.getApplicationId() == factory.getApplicationId()) {
        if (request.getCommandCode() == ProfileUpdateRequestImpl.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createProfileUpdateRequest(request), null));
          return;
        }
        else if (request.getCommandCode() == UserDataRequestImpl.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createUserDataRequest(request), null));
          return;
        }
        else if (request.getCommandCode() == SubscribeNotificationsRequestImpl.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createSubscribeNotificationsRequest(request), null));
          return;
        }
      }
      // FIXME: Anything else todo?
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void setState(ShSessionState newState) {
    setState(newState, true);
  }

  protected void setState(ShSessionState newState, boolean release) {
    IAppSessionState oldState = state;
    state = newState;
    for (StateChangeListener i : stateListeners) {
      i.stateChanged((Enum) oldState, (Enum) newState);
    }
    if (newState == ShSessionState.TERMINATED) {
      if (release) {
        this.release();
      }
      if (sft != null) {
        sft.cancel(true);
        sft = null;
      }
    }
  }

  protected long extractExpirationTime(Message answer) {
    return -1;
  }

  public void release() {
    try {
      sendAndStateLock.lock();
      if (state != ShSessionState.TERMINATED) {
        setState(ShSessionState.TERMINATED, false);
        //session.release();
      }

      if(super.isValid()) {
        super.release();
      }

      if(super.session != null) { 
        super.session.setRequestListener(null);
      }
      this.session = null;
      if(listener != null) { 
        this.removeStateChangeNotification((StateChangeListener) listener);
      }
      this.listener = null;
      this.factory = null;
    }
    catch (Exception e) {
      logger.debug("Failed to release session", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }
}
