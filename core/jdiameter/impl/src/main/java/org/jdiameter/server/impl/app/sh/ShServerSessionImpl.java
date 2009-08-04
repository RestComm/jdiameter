package org.jdiameter.server.impl.app.sh;

import java.util.TimerTask;
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
import org.jdiameter.api.ResultCode;
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
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
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
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShServerSessionImpl extends ShSession implements ServerShSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
  protected boolean stateless = false;
  protected IShMessageFactory factory = null;
  protected String destHost, destRealm;
  protected Lock sendAndStateLock = new ReentrantLock();
  protected ServerShSessionListener listener;
  protected long appId = -1;
  //Subscription timer
  protected ScheduledFuture sft = null;
  protected ScheduledFuture txSft=null;
  protected boolean receivedSubTerm=false;
  protected TxTimerTask txTimerTask=null;

  public ShServerSessionImpl(IShMessageFactory fct, SessionFactory sf, ServerShSessionListener lst) {
    this(null, fct, sf, lst);
  }

  public ShServerSessionImpl(String sessionId, IShMessageFactory fct, SessionFactory sf, ServerShSessionListener lst) {
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
        if (request.getCommandCode() == PushNotificationRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_PUSH_NOTIFICATION_ANSWER, factory.createPushNotificationRequest(request), factory.createPushNotificationAnswer(answer)));
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
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createPushNotificationRequest(request), null));
          return;
        } 
      }
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public Answer processRequest(Request request) {
    try {
      if (request.getApplicationId() == factory.getApplicationId()) {
        if (request.getCommandCode() == org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST, factory.createSubscribeNotificationsRequest(request), null));
          return null;
        }
        else if(request.getCommandCode() == org.jdiameter.common.impl.app.sh.UserDataRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_USER_DATA_REQUEST,factory.createUserDataRequest(request),null));
          return null;
        }
        else if(request.getCommandCode() == org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_PROFILE_UPDATE_REQUEST,factory.createProfileUpdateRequest(request),null));
          return null;
        }
        else {
          // FIXME: Anything to do here?
        }
      }

      listener.doOtherEvent(this, new AppRequestEventImpl(request), null);
    }
    catch (Exception e) {
      logger.debug("Failed to process request message", e);
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
        AppEvent request = localEvent.getRequest();
        switch (state) {
        case NOTSUBSCRIBED:
          if (event.getType() == Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST) {
            //Do nothing, we have to wait for response send callback
            startTxTimer(request);
            //We use newState in case of first request, it can be unsubscribe or bad
            newState=doSNX(request);
          }
          else if (event.getType() == Event.Type.RECEIVE_PROFILE_UPDATE_REQUEST) {
            //newState=ShSessionState.TERMINATED;
            startTxTimer(request);
          }
          else if (event.getType() == Event.Type.RECEIVE_USER_DATA_REQUEST) {
            //newState=ShSessionState.TERMINATED;
            startTxTimer(request);
          }
          else if(event.getType()== Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER) {
            newState=doSNX(localEvent.getAnswer());
            stopTxTimer();
          }
          else if (event.getType() == Event.Type.TIMEOUT_EXPIRES) {
            newState = ShSessionState.TERMINATED;
            //FIXME: What happens here?
          }
          else if(event.getType()== Event.Type.TX_TIMER_EXPIRED) {
            newState = ShSessionState.TERMINATED;
            //FIXME Result code ???
            try {
              Answer answer= ((Request)request.getMessage()).createAnswer(ResultCode.TOO_BUSY);
              session.send(answer);
            }
            catch (Exception e) {
              logger.debug("Unable to send failure answer", e);
            }
          }
          else {
            // Other messages just make it go into terminated state and release send: UDA
            stopTxTimer();
            newState = ShSessionState.TERMINATED;
          }

          break;
        case SUBSCRIBED:
          //FIXME: should we also use here startTx, stopTx?
          if(event.getType()== Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER) {
            newState=doSNX(localEvent.getAnswer());
            stopTxTimer();
          }
          else if(event.getType() == Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST) {
            newState=doSNX(request);
            //startTxTimer(request);
          }
          //FIXME: Any change here - even on timeout?
          break;
        case TERMINATED:
          break;
        }

        // Do the delivery
        // FIXME: Should we look if we are in terminated state?
        try {
          switch ((Event.Type) localEvent.getType()) {
          case RECEIVE_PROFILE_UPDATE_REQUEST:
            listener.doProfileUpdateRequestEvent(this, (ProfileUpdateRequest) localEvent.getRequest());
            break;
          case RECEIVE_PUSH_NOTIFICATION_ANSWER:
            listener.doPushNotificationAnswerEvent(this, (PushNotificationRequest) localEvent.getRequest(), (PushNotificationAnswer) localEvent.getAnswer());
            break;
          case RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST:
            listener.doSubscribeNotificationsRequestEvent(this, (SubscribeNotificationsRequest) localEvent.getRequest());
            break;
          case RECEIVE_USER_DATA_REQUEST:
            listener.doUserDataRequestEvent(this, (UserDataRequest) localEvent.getRequest());
            break;
          case SEND_PROFILE_UPDATE_ANSWER:
            dispatchEvent(localEvent.getAnswer());
            stopTxTimer();
            break;
          case SEND_PUSH_NOTIFICATION_REQUEST:
            dispatchEvent(localEvent.getRequest());
            break;
          case SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER:
            dispatchEvent(localEvent.getAnswer());
            stopTxTimer();
            break;
          case SEND_USER_DATA_ANSWER:
            dispatchEvent(localEvent.getAnswer());
            stopTxTimer();
            break;
          case TIMEOUT_EXPIRES:
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
          setState(newState, true);
        }
      }
    }
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }

  public boolean isStateless() {
    return stateless;
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      //FIXME: isnt this bad? Shouldnt send be before state change?
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception exc) {
      throw new InternalException(exc);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException
  {
    try{
      session.send(event.getMessage(), this);
      //FIXME: add differentation on server/client request
    }
    catch(Exception e) {
      logger.debug("Failed to dispatch event", e);
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
      stopSubscriptionTimer();
      stopTxTimer();
    }
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

  protected long extractExpirationTime(Message answer) {
    return -1;
  }

  protected ShSessionState doSNX(AppEvent message) throws InternalException {
    ShSessionState newState = state;
    AvpSet set = message.getMessage().getAvps();
    long resultCode = -1;
    // Experimental-Result-Code:297, Result-Code:268
    Avp avp = null;
    try{
      if(message.getMessage().isRequest())
      {
        Avp subsReqType = set.getAvp(705);
        if(subsReqType == null || subsReqType.getInteger32() < 0 || subsReqType.getInteger32() > 1) {
          // This is wrong!
          newState = ShSessionState.TERMINATED;
        }
        else {
          switch(subsReqType.getInteger32())
          {
          case 0:
            // Subscribe
            startSubscriptionTimer(message.getMessage());
            break;
          case 1:
            receivedSubTerm = true;
            break;
          }
        }
      }
      else {
        if(receivedSubTerm) {
          newState = ShSessionState.TERMINATED;
          stopSubscriptionTimer();
        }
        else {
          avp = set.getAvp(Avp.EXPERIMENTAL_RESULT) != null ? set.getAvp(Avp.EXPERIMENTAL_RESULT).getGrouped().getAvp(Avp.EXPERIMENTAL_RESULT_CODE) : 
            set.getAvp(Avp.RESULT_CODE);
          try {
            resultCode = avp.getUnsigned32();
            if (resultCode >= 2000 && resultCode < 3000) {
              startSubscriptionTimer(message.getMessage());
              newState = ShSessionState.SUBSCRIBED;
            }
            else {
              // its a failure?
              newState= ShSessionState.TERMINATED;
            }
          }
          catch (AvpDataException e) {
            logger.debug("Could not retrieve Result-Code from message", e);
          }
        }
      }
    }
    catch(Exception e) {
      logger.debug("Unable to process event", e);
      newState = ShSessionState.TERMINATED;
    }
    return newState;
  }

  private void startSubscriptionTimer( Message message) {
    long expiryTime = extractExpirationTime(message);
    if (expiryTime >= 0) {
      stopSubscriptionTimer();
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
  }

  private void stopSubscriptionTimer() {
    if(this.sft != null) {
      this.sft.cancel(false);
      this.sft = null;
    }
  }

  private void startTxTimer(AppEvent request) {
    try {
      //FIXME: isnt this bad? Shouldnt send be before state change?
      sendAndStateLock.lock();
      this.stopTxTimer();
      this.txTimerTask = new TxTimerTask(request);
      this.txSft = ShSession.scheduler.schedule(this.txTimerTask, this.factory.getMessageTimeout(), TimeUnit.MILLISECONDS);
    }
    catch (Exception e) {
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  private void stopTxTimer()
  {
    try {
      //FIXME: isnt this bad? Shouldnt send be before state change?
      sendAndStateLock.lock();
      if (this.txTimerTask != null) {
        this.txSft.cancel(false);
        this.txSft = null;
        this.txTimerTask.cancel();
        this.txTimerTask=null;
      }
    }
    catch (Exception exc) {
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  private class TxTimerTask extends TimerTask
  {
    private AppEvent request = null;

    public TxTimerTask(AppEvent request2) {
      super();
      this.request = request2;
    }

    @Override
    public boolean cancel() {
      this.request = null;
      return super.cancel();
    }

    @Override
    public void run() {
      try {
        //FIXME: isnt this bad? Shouldnt send be before state change?
        sendAndStateLock.lock();
        handleEvent(new Event(Event.Type.TX_TIMER_EXPIRED,request,null));
      }
      catch (InternalException e) {
        logger.error("Internal Exception", e);
      }
      catch (OverloadException e) {
        logger.error("Overload Exception", e);
      }
      finally {
        this.request = null;
        sendAndStateLock.unlock();
      }
    }
  }

  protected  boolean isProvisional(long result) {
    return result >= 1000 && result < 2000;
  }

  protected boolean isSuccess(long result) {
    return result >= 2000 && result < 3000;
  }

}
