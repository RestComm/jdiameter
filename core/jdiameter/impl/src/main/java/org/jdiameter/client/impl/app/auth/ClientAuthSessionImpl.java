package org.jdiameter.client.impl.app.auth;

import static org.jdiameter.api.Message.SESSION_TERMINATION_REQUEST;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.DISCONNECTED;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.IDLE;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.OPEN;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.PENDING;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.auth.ClientAuthSessionState;
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
import org.jdiameter.common.api.app.auth.IClientAuthActionContext;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;
import org.jdiameter.common.impl.app.auth.AppAuthSessionImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.auth.SessionTermAnswerImpl;
import org.jdiameter.common.impl.app.auth.SessionTermRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAuthSessionImpl extends AppAuthSessionImpl implements ClientAuthSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(ClientAuthSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected boolean stateless = false;
  protected ClientAuthSessionState state = IDLE;
  private Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected IAuthMessageFactory factory;
  protected IClientAuthActionContext context;
  protected ClientAuthSessionListener listener;

  protected String destHost, destRealm;
  protected ScheduledFuture sessionTimer;
  protected AppEvent buffer;

  // Constructors -------------------------------------------------------------

  public ClientAuthSessionImpl(boolean stl, IAuthMessageFactory fct, SessionFactory sf, ClientAuthSessionListener lst) {
    this(stl, null, fct, sf, lst);
  }

  public ClientAuthSessionImpl(boolean stl, String sessionId, IAuthMessageFactory fct, SessionFactory sf, ClientAuthSessionListener lst) {
    super(sf);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() == null) {
      throw new IllegalArgumentException("ApplicationId can not be null");
    }
    appId = fct.getApplicationId();
    listener = lst;
    factory = fct;
    stateless = stl;
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
    if (listener instanceof IClientAuthActionContext) {
      context = (IClientAuthActionContext) listener;
    }
  }

  // ClientAuthSession Implementation methods ---------------------------------

  public void sendAbortSessionAnswer(AbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_SESSION_ABORT_ANSWER, answer);
  }

  public void sendAuthRequest(AppRequestEvent request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_AUTH_REQUEST, request);
  }

  public void sendReAuthAnswer(ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_AUTH_ANSWER, answer);
  }

  public void sendSessionTerminationRequest(SessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_SESSION_TERMINATION_REQUEST, request);
  }

  protected void send(Event.Type type, AppEvent event) throws InternalException {
    //This is called from app thread, it may be due to callback from our delivery thread, but we dont care
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, event));
      }
      session.send(event.getMessage(), this);
      // Store last destination information
      destRealm = event.getMessage().getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
      destHost = event.getMessage().getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString();
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public boolean isStateless() {
    return stateless;
  }

  protected void setState(ClientAuthSessionState newState) {
    IAppSessionState oldState = state;
    state = newState;
    for (StateChangeListener i : stateListeners) {
      i.stateChanged((Enum) oldState, (Enum) newState);
    }
  }

  public <E> E getState(Class<E> eClass) {
    return eClass == ClientAuthSessionState.class ? (E) state : null;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return stateless ? handleEventForStatelessSession(event) : handleEventForStatefulSession(event);
  }

  public boolean handleEventForStatelessSession(StateEvent event) throws InternalException, OverloadException {
    try {

      ClientAuthSessionState oldState = state;

      switch (state) {
      case IDLE:
        switch ((Event.Type) event.getType()) {
        case SEND_AUTH_REQUEST:
          // Current State: IDLE 
          // Event: Client or Device Requests access
          // Action: Send service specific auth req
          // New State: PENDING
          setState(PENDING);
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
          break;
        }
        break;
      case PENDING:
        switch ((Event.Type) event.getType()) {
        case RECEIVE_AUTH_ANSWER:
          try {
            // Current State: PENDING
            // Event: Successful service-specific authorization answer received with Auth-Session-State set to NO_STATE_MAINTAINED
            // Action: Grant Access
            // New State: OPEN
            listener.doAuthAnswerEvent(this, null, (AppAnswerEvent) event.getData());
            setState(OPEN);
          }
          catch (Exception e) {
            // Current State: PENDING
            // Event: Failed service-specific authorization answer received
            // Action: Cleanup
            // New State: IDLE
            setState(IDLE);
          }
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
          break;
        }
        break;
      case OPEN:
        switch ((Event.Type) event.getType()) {
        case SEND_SESSION_ABORT_ANSWER:
        case SEND_SESSION_TERMINATION_REQUEST:
          // Current State: OPEN
          // Event: Service to user is terminated
          // Action: Disconnect User/Device
          // New State: IDLE
          setState(IDLE);
          break;
        case TIMEOUT_EXPIRES:
          // Current State: OPEN
          // Event: Session-Timeout Expires on Access Device
          // Action: Send STR
          // New State: DISCON
          if (context != null) {
            context.accessTimeoutElapses();
            Request str = createSessionTermRequest();
            context.disconnectUserOrDev(this, str);
            session.send(str, this);
          }
          // IDLE is the same as DISCON
          setState(IDLE);
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
          break;
        }
        break;
      }

      // post processing
      if (oldState != state) {
        if (DISCONNECTED.equals(state) || IDLE.equals(state)) {
          if (sessionTimer != null) {
            sessionTimer.cancel(false);
          }
          sessionTimer = null;
        }
        else if (OPEN.equals(state) && context != null && context.createAccessTimer() > 0) {
          sessionTimer = scheduler.schedule(new Runnable() {
            public void run() {
              if (context != null) {
                try {
                  handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, null));
                }
                catch (Exception e) {
                  logger.debug("Can not handle event", e);
                }
              }
            }
          }, context.createAccessTimer(), TimeUnit.MILLISECONDS);
        }
      }
    }
    catch (Throwable t) {
      throw new InternalException(t);
    }

    return true;
  }

  public boolean handleEventForStatefulSession(StateEvent event) throws InternalException, OverloadException {

    ClientAuthSessionState oldState = state;

    try {
      switch (state) {
      case IDLE: {
        switch ((Event.Type) event.getType()) {
        case SEND_AUTH_REQUEST:
          // Current State: IDLE 
          // Event: Client or Device Requests access
          // Action: Send service specific auth req
          // New State: PENDING
          setState(PENDING);
          break;
        case RECEIVE_ABORT_SESSION_REQUEST:
          // Current State: IDLE
          // Event: ASR Received for unknown session
          // Action: Send ASA with Result-Code = UNKNOWN_SESSION_ID
          // New State: IDLE
          // FIXME: Should send ASA with UNKNOWN_SESSION_ID instead ?
          listener.doAbortSessionRequestEvent(this, (AbortSessionRequest) event.getData());
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
          break;
        }
        break;
      }
      case PENDING: {
        switch ((Event.Type) event.getType()) {
        case RECEIVE_AUTH_ANSWER:
          try {
            // Current State: PENDING
            // Event: Successful service-specific authorization answer received with default Auth-Session-State value
            // Action: Grant Access
            // New State: OPEN
            listener.doAuthAnswerEvent(this, null, (AppAnswerEvent) event.getData());
            setState(OPEN);
          }
          catch (InternalException e) {
            // Current State: PENDING
            // Event: Successful service-specific authorization answer received but service not provided
            // Action: Send STR
            // New State: DISCON

            // Current State: PENDING
            // Event: Error Processing successful service-specific authorization answer
            // Action: Send STR
            // New State: DISCON
            setState(DISCONNECTED);
          }
          catch (Exception e) {
            // Current State: PENDING
            // Event: Failed service-specific authorization answer received
            // Action: Cleanup
            // New State: IDLE
            setState(IDLE);
          }
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
          break;
        }
        break;
      }
      case OPEN: {
        switch ((Event.Type) event.getType()) {
        case SEND_AUTH_REQUEST:
          // Current State: OPEN 
          // Event: User or client device requests access to service
          // Action: Send service specific auth req 
          // New State: OPEN
          break;
        case RECEIVE_AUTH_ANSWER:
          try {
            // Current State: OPEN 
            // Event: Successful service-specific authorization answer received
            // Action: Provide Service
            // New State: OPEN
            listener.doAuthAnswerEvent(this, null, (AppAnswerEvent) event.getData());
          }
          catch (Exception e) {
            // Current State: OPEN 
            // Event: ASR Received, client will comply with request to end the session
            // Action: Send ASA with Result-Code = SUCCESS, Send STR
            // New State: DISCON
            setState(DISCONNECTED);
          }
          break;
        case RECEIVE_FAILED_AUTH_ANSWER:
          // Current State: OPEN 
          // Event: Failed Service-specific authorization answer received
          // Action: Disconnect User/Device
          // New State: IDLE
          if (context != null) {
            Request str = createSessionTermRequest();
            context.disconnectUserOrDev(this, str);
            session.send(str, this);
          }
          setState(IDLE);
          break;
        case RECEIVE_ABORT_SESSION_REQUEST:
          // Current State: OPEN 
          // Event: ASR Received (client to take comply or not)  
          // Action: TBD
          // New State: TBD (comply = DISCON, !comply = OPEN)
          listener.doAbortSessionRequestEvent(this, (AbortSessionRequestImpl) event.getData());
          break;
        case SEND_SESSION_TERMINATION_REQUEST:
          setState(DISCONNECTED);
          break;
        case TIMEOUT_EXPIRES:
          // Current State: OPEN 
          // Event: Session-Timeout Expires on Access Device
          // Action: Send STR
          // New State: DISCON

          // Current State: OPEN 
          // Event: Authorization-Lifetime + Auth-Grace-Period expires on access device
          // Action: Send STR
          // New State: DISCON
          if (context != null) {
            context.accessTimeoutElapses();
            Request str = createSessionTermRequest();
            context.disconnectUserOrDev(this, str);
            session.send(str, this);
          }
          setState(DISCONNECTED);
          break;
        }
        break;
      }
      case DISCONNECTED: {
        switch ((Event.Type) event.getType()) {
        case RECEIVE_ABORT_SESSION_REQUEST:
          // Current State: DISCON
          // Event: ASR Received
          // Action: Send ASA
          // New State: DISCON
          listener.doAbortSessionRequestEvent(this, (AbortSessionRequest) event.getData());
          break;
        case RECEIVE_SESSION_TERINATION_ANSWER:
          // Current State: DISCON
          // Event: STA Received
          // Action: Disconnect User/Device
          // New State: IDLE
          listener.doSessionTerminationAnswerEvent(this, ((SessionTermAnswerImpl) event.getData()));
          setState(IDLE);
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
          break;
        }
        break;
      }
      default: {
        logger.debug("Unknown state {}", state);
        break;
      }
      }

      // post processing
      if (oldState != state) {
        if (OPEN.equals(state) && context != null && context.createAccessTimer() > 0) {
          scheduler.schedule(new Runnable() {
            public void run() {
              if (context != null) {
                try {
                  handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, null));
                }
                catch (Exception e) {
                  logger.debug("Can not handle event", e);
                }
              }
            }
          }, context.createAccessTimer(), TimeUnit.MILLISECONDS);
        }
      }
    }
    catch (Throwable t) {
      throw new InternalException(t);
    }

    return true;
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery ad = new AnswerDelivery();
    ad.session = this;
    ad.request = request;
    ad.answer = answer;
    super.scheduler.execute(ad);
  }

  public void timeoutExpired(Request request) {
    try {
      //FIXME: should this also be async ?
      handleEvent(new Event(Event.Type.RECEIVE_FAILED_AUTH_ANSWER, new AppRequestEventImpl(request)));
    }
    catch (Exception e) {
      logger.debug("Can not handle timeout event", e);
    }
  }

  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);

    return null;
  }

  protected AbortSessionAnswer createAbortSessionAnswer(Answer answer) {
    return new AbortSessionAnswerImpl(answer);
  }

  protected AbortSessionRequest createAbortSessionRequest(Request request) {
    return new AbortSessionRequestImpl(request);
  }

  protected ReAuthAnswer createReAuthAnswer(Answer answer) {
    return new ReAuthAnswerImpl(answer);
  }

  protected ReAuthRequest createReAuthRequest(Request request) {
    return new ReAuthRequestImpl(request);
  }

  protected SessionTermAnswer createSessionTermAnswer(Answer answer) {
    return new SessionTermAnswerImpl(answer);
  }

  protected SessionTermRequest createSessionTermRequest(Request request) {
    return new SessionTermRequestImpl(request);
  }

  protected Request createSessionTermRequest() {
    return session.createRequest(SESSION_TERMINATION_REQUEST, appId, destRealm, destHost);
  }

  private class RequestDelivery implements Runnable {
    ClientAuthSession session;
    Request request;

    public void run() {
      try {
        if (request.getCommandCode() == AbortSessionRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_ABORT_SESSION_REQUEST, createAbortSessionRequest(request)));
        }
        else if (request.getCommandCode() == ReAuthRequestImpl.code) {
          listener.doReAuthRequestEvent(session, createReAuthRequest(request));
        }
        else if (request.getCommandCode() == AbortSessionRequestImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_ABORT_SESSION_REQUEST, createAbortSessionRequest(request)));
        }
        else {
          listener.doOtherEvent(session, factory.createAuthRequest(request), null);
        }
      }
      catch (Exception e) {
        logger.debug("Can not process received request", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientAuthSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        // FIXME: baranowb: this shouldnt be like that
        if (answer.getCommandCode() == factory.getAuthMessageCommandCode()) {
          handleEvent(new Event(Event.Type.RECEIVE_AUTH_ANSWER, factory.createAuthAnswer(answer)));
        }
        else if (answer.getCommandCode() == SessionTermAnswerImpl.code) {
          handleEvent(new Event(Event.Type.RECEIVE_SESSION_TERINATION_ANSWER, createSessionTermAnswer(answer)));
        }
        else {
          listener.doOtherEvent(session, factory.createAuthRequest(request), new AppAnswerEventImpl(answer));
        }
      }
      catch (Exception e) {
        logger.debug("Can not process received message", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }
}
