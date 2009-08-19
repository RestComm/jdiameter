/**
 * Start time:16:23:22 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.client.impl.app.cxdx;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
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
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;
import org.jdiameter.client.impl.app.cxdx.Event.Type;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.cxdx.CxDxSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Start time:16:23:22 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class CxDxClientSessionImpl extends CxDxSession implements ClientCxDxSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CxDxClientSessionImpl.class);
  // FIXME: use super..super.appId ??
  protected long appId = -1;
  private ClientCxDxSessionListener listener;
  private ICxDxMessageFactory factory;

  public CxDxClientSessionImpl(ICxDxMessageFactory fct, SessionFactory sf, ClientCxDxSessionListener lst) {
    this(null, fct, sf, lst);
  }

  public CxDxClientSessionImpl(String sessionId, ICxDxMessageFactory fct, SessionFactory sf, ClientCxDxSessionListener lst) {

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
      } else {
        session = sf.getNewSession(sessionId);
      }
      session.setRequestListener(this);
    } catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.app.StateMachine#getState(java.lang.Class)
   */
  public <E> E getState(Class<E> stateType) {

    return stateType == CxDxSessionState.class ? (E) super.state : null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  public Answer processRequest(Request request) {
    try {
      if (request.getCommandCode() == JRegistrationTerminationRequest.code) {
        this.handleEvent(new Event(Event.Type.RECEIVE_RTR, this.factory.createRegistrationTerminationRequest(request), null));
      }
      else if (request.getCommandCode() == JPushProfileRequest.code) {
        this.handleEvent(new Event(Event.Type.RECEIVE_PPR, this.factory.createPushProfileRequest(request), null));
      }
      else {
        this.listener.doOtherEvent(this, new AppRequestEventImpl(request), null);
      }
    }
    catch (Exception e) {
      logger.debug("Failed to process request message", e);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendLocationInformationRequest(org.jdiameter.api.cxdx.events.JLocationInfoRequest)
   */
  public void sendLocationInformationRequest(JLocationInfoRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendMultimediaAuthRequest(org.jdiameter.api.cxdx.events.JMultimediaAuthRequest)
   */
  public void sendMultimediaAuthRequest(JMultimediaAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendServerAssignmentRequest(org.jdiameter.api.cxdx.events.JServerAssignmentRequest)
   */
  public void sendServerAssignmentRequest(JServerAssignmentRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendUserAuthorizationRequest(org.jdiameter.api.cxdx.events.JUserAuthorizationRequest)
   */
  public void sendUserAuthorizationRequest(JUserAuthorizationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendPushProfileAnswer(org.jdiameter.api.cxdx.events.JPushProfileAnswer)
   */
  public void sendPushProfileAnswer(JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendRegistrationTerminationAnswer(org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
   */
  public void sendRegistrationTerminationAnswer(JRegistrationTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.api.Message, org.jdiameter.api.Message)
   */
  public void receivedSuccessMessage(Request request, Answer answer) {
    try{
      switch (answer.getCommandCode()) {
      case JUserAuthorizationAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_UAA, null, this.factory.createUserAuthorizationAnswer(answer)));
        break;
      case JServerAssignmentAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_SAA, null, this.factory.createServerAssignmentAnswer(answer)));
        break;
      case JMultimediaAuthAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_MAA, null, this.factory.createMultimediaAuthAnswer(answer)));
        break;
      case JLocationInfoAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_LIA, null, this.factory.createLocationInfoAnswer(answer)));
        break;
  
      default:
        listener.doOtherEvent(this, null, new AppAnswerEventImpl(answer));
      break;
      }
    }
    catch (Exception e) {
      logger.debug("Failed to process success message", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {

      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
      AppEvent event = null;
      if (request != null) {
        event = request;
      } else {
        event = answer;
      }
      session.send(event.getMessage(), this);

    } catch (Exception e) {
      throw new InternalException(e);
    }
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME?
        // throw new InternalException("Generic session is not valid.");
        return false;
      }
      CxDxSessionState newState = null;
      Event.Type eventType = (Type) event.getType();
      switch (super.state) {

      case IDLE:
        switch (eventType) {
        case RECEIVE_PPR:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doPushProfileRequest(this, (JPushProfileRequest) event.getData(), null);
          break;
        case RECEIVE_RTR:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doRegistrationTerminationRequest(this, (JRegistrationTerminationRequest) event.getData(), null);
          break;

        case SEND_MESSAGE:
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          super.session.send(((AppEvent) event.getData()).getMessage());
          break;
        default:
          logger.error("Something went wrong, we should not b here, its a bug.");
        break;
        }

        break;

        // hmm, this allow rtr from this side, but this should not happen.
      case MESSAGE_SENT_RECEIVED:

        switch (eventType) {
        case TIMEOUT_EXPIRES:
          newState = CxDxSessionState.TIMEDOUT;
          break;
        case SEND_MESSAGE:
          if (super.timeoutTaskFuture != null) {
            super.timeoutTaskFuture.cancel(false);
            super.timeoutTaskFuture = null;
          }
          super.session.send((Message) event.getData(), this);
          newState = CxDxSessionState.TERMINATED;
          break;
        case RECEIVE_LIA:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) event.getData()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doLocationInformationAnswer(this, null, (JLocationInfoAnswer) event.getData());
          break;
        case RECEIVE_MAA:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) event.getData()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doMultimediaAuthAnswer(this, null, (JMultimediaAuthAnswer) event.getData());
          break;
        case RECEIVE_SAA:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) event.getData()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doServerAssignmentAnswer(this, null, (JServerAssignmentAnswer) event.getData());
          break;
        case RECEIVE_UAA:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) event.getData()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doUserAuthorizationAnswer(this, null, (JUserAuthorizationAnswer) event.getData());
          break;
        default:
          // FIXME: this could be rtr?
          throw new InternalException("Can not receive more messages after initial!!!. Command: " + event.getData());
        }

        break;
      case TERMINATED:
        throw new InternalException("Cant receive message in state termianted. Command: " + event.getData());
      case TIMEDOUT:
        throw new InternalException("Cant receive message in state timedout. Command: " + event.getData());
      default:
        logger.error("Wrong state: " + super.state);
      break;
      }

      if (newState != null && newState != super.state) {
        setState(newState);
      }
    }
    catch (Exception e) {
    	e.printStackTrace();
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }

  protected void setState(CxDxSessionState newState) {
    CxDxSessionState oldState = super.state;
    super.state = newState;
    for (StateChangeListener i : stateListeners) {
      i.stateChanged((Enum) oldState, (Enum) newState);
    }
    if (newState == CxDxSessionState.TERMINATED || newState == CxDxSessionState.TIMEDOUT) {

      this.release();

      if (super.timeoutTaskFuture != null) {
        timeoutTaskFuture.cancel(true);
        timeoutTaskFuture = null;
      }
    }
  }

  private class TimeoutTimerTask implements Runnable {
    private Request r;

    public TimeoutTimerTask(Request r) {
      super();
      this.r = r;
    }

    public void run() {
      try {
        handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(r), null));
      }
      catch (Exception e) {
        logger.debug("Failure handling Timeout event.");
      }
    }

  }
}
