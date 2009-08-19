package org.jdiameter.server.impl.app.cxdx;

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
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSessionListener;
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
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.cxdx.CxDxSession;
import org.jdiameter.server.impl.app.cxdx.Event.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Start time:19:57:44 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CxDxServerSessionImpl extends CxDxSession implements ServerCxDxSession, EventListener<Request, Answer>, NetworkReqListener {
  
  private static final long serialVersionUID = 1L;
  
  private static final Logger logger = LoggerFactory.getLogger(CxDxServerSessionImpl.class);
  protected long appId = -1;
  private ServerCxDxSessionListener listener;
  private ICxDxMessageFactory factory;

  public CxDxServerSessionImpl(ICxDxMessageFactory fct, SessionFactory sf, ServerCxDxSessionListener lst) {
    this(null, fct, sf, lst);
  }

  public CxDxServerSessionImpl(String sessionId, ICxDxMessageFactory fct, SessionFactory sf, ServerCxDxSessionListener lst) {
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if ((appId = fct.getApplicationId()) < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

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

  /* (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ServerCxDxSession#sendLocationInformationAnswer(org.jdiameter.api.cxdx.events.JLocationInfoAnswer)
   */
  public void sendLocationInformationAnswer(JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ServerCxDxSession#sendMultimediaAuthAnswer(org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer)
   */
  public void sendMultimediaAuthAnswer(JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ServerCxDxSession#sendServerAssignmentAnswer(org.jdiameter.api.cxdx.events.JServerAssignmentAnswer)
   */
  public void sendServerAssignmentAnswer(JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ServerCxDxSession#sendUserAuthorizationAnswer(org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer)
   */
  public void sendUserAuthorizationAnswer(JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ServerCxDxSession#sendPushProfileRequest(org.jdiameter.api.cxdx.events.JPushProfileRequest)
   */
  public void sendPushProfileRequest(JPushProfileRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ServerCxDxSession#sendRegistrationTerminationRequest(org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest)
   */
  public void sendRegistrationTerminationRequest(JRegistrationTerminationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateMachine#getState(java.lang.Class)
   */
  public <E> E getState(Class<E> stateType) {
    return stateType == CxDxSessionState.class ? (E) super.state : null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateMachine#handleEvent(org.jdiameter.api.app.StateEvent)
   */
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

        case RECEIVE_LIR:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doLocationInformationRequest(this,  (JLocationInfoRequest) event.getData(),null);
          break;
        case RECEIVE_MAR:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doMultimediaAuthRequest(this,  (JMultimediaAuthRequest) event.getData(),null);
          break;
        case RECEIVE_SAR:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doServerAssignmentRequest(this,  (JServerAssignmentRequest) event.getData(),null);
          break;
        case RECEIVE_UAR:
          CxDxSession.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
          newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
          listener.doUserAuthorizationRequest(this, (JUserAuthorizationRequest) event.getData(),null);
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
          super.session.send(((AppEvent) event.getData()).getMessage(), this);
          newState = CxDxSessionState.TERMINATED;
          break;

        case RECEIVE_PPA:
          newState = CxDxSessionState.TERMINATED;
          listener.doPushProfileAnswer(this,  null, (JPushProfileAnswer)event.getData());
          break;
        case RECEIVE_RTA:
          newState = CxDxSessionState.TERMINATED;
          listener.doRegistrationTerminationAnswer(this,  null,(JRegistrationTerminationAnswer) event.getData());
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
      throw new InternalException(e);
    }		
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.
   * api.Message, org.jdiameter.api.Message)
   */
  public void receivedSuccessMessage(Request request, Answer answer) {
    try{
      switch (answer.getCommandCode()) {
      case JPushProfileAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_PPA, null, this.factory.createUserAuthorizationAnswer(answer)));
        break;
      case JRegistrationTerminationAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_RTA, null, this.factory.createRegistrationTerminationAnswer(answer)));
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
   * @see org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  public Answer processRequest(Request request) {
    try {
      switch (request.getCommandCode()) {
      case JUserAuthorizationAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_UAR, this.factory.createUserAuthorizationRequest(request), null));
        break;
      case JServerAssignmentAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_SAR, this.factory.createServerAssignmentRequest(request), null));
        break;
      case JMultimediaAuthAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_MAR, this.factory.createMultimediaAuthRequest(request), null));
        break;
      case JLocationInfoAnswer.code:
        handleEvent(new Event(Event.Type.RECEIVE_LIR, this.factory.createLocationInfoRequest(request), null));
        break;

      default:
        listener.doOtherEvent(this, null, new AppAnswerEventImpl(request));
      break;
      }
    }
    catch (Exception e) {
      logger.debug("Failed to process request message", e);
    }

    return null;
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
      AppEvent event = request != null ? request : answer;
      session.send(event.getMessage(), this);
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
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
