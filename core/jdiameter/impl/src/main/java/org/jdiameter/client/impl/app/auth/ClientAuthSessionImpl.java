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

  protected ClientAuthSessionState state = IDLE;
  protected boolean stateless;
  protected IAuthMessageFactory factory;
  protected String destHost, destRealm;
  protected IClientAuthActionContext context;
  protected AppEvent buffer;
  protected ClientAuthSessionListener listener;
  protected ScheduledFuture fsf;
  private Lock sendAndStateLock = new ReentrantLock();

  // =================== CONSTRUCTORS

  public ClientAuthSessionImpl(boolean stl, IAuthMessageFactory fct, SessionFactory sf, ClientAuthSessionListener lst) {
    this(stl, null, fct, sf, lst);
  }

  public ClientAuthSessionImpl(boolean stl, String sessionId, IAuthMessageFactory fct, SessionFactory sf, ClientAuthSessionListener lst) {
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
    send(Event.Type.SEND_SESSION_TERINATION_REQUEST, request);
  }

  protected void send(Event.Type type, AppEvent event) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, event));
      }
      session.send(event.getMessage(), this);
      // Store last destinmation information
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
    return stateless ? handleEventForStatelessSession(event) : handleEventForStatefullSession(event);
  }

  public boolean handleEventForStatelessSession(StateEvent event) throws InternalException, OverloadException {
    try {

      ClientAuthSessionState oldState = state;

      switch (state) {
      case IDLE:
        switch ((Event.Type) event.getType()) {
        case SEND_AUTH_REQUEST:
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
            /**
             * Successful Service-specific authorization answer
             * received with Auth-Session- State set to NO_STATE_MAINTAINED
             */
            listener.doAuthAnswerEvent(this, null, (AppAnswerEvent) event.getData());
            setState(OPEN);
          }
          catch (Exception e) {
            // Failed Service-specific authorization answer received
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
        case SEND_SESSION_TERINATION_REQUEST:
          setState(IDLE);
          break;
        case TIMEOUT_EXPIRES:
          if (context != null) {
            context.accessTimeoutElapses();
            Request str = createSessionTermRequest();
            context.disconnectUserOrDev(this, str);
            session.send(str, this);
          }
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
          if (fsf != null) {
            fsf.cancel(false);
          }
          fsf = null;
        }
        else if (OPEN.equals(state) && context != null && context.createAccessTimer() > 0) {
          fsf = scheduler.schedule(new Runnable() {
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
    } catch (Throwable t) {
      throw new InternalException(t);
    }

    return true;
  }

  public boolean handleEventForStatefullSession(StateEvent event) throws InternalException, OverloadException {

    ClientAuthSessionState oldState = state;

    try {
      switch (state) {
      case IDLE: {
        switch ((Event.Type) event.getType()) {
        case SEND_AUTH_REQUEST:
          setState(PENDING);
          break;
        case RECEIVE_ABORT_SESSION_REQUEST:
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
            /**
             * Listener processed following actions: Grant access / Send STR / Cleanup
             */
            listener.doAuthAnswerEvent(this, null, (AppAnswerEvent) event.getData());
            setState(OPEN);
          }
          catch (InternalException e) {
            // Successful Service-specific authorization answer received but service not provided
            // Error processing successful Service-specific authorization answer
            setState(DISCONNECTED);
          }
          catch (Exception e) {
            // Failed Service-specific authorization answer received
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
          // User or client device equests access to service
          break;
        case RECEIVE_AUTH_ANSWER:
          try {
            // Successful Service-specific authorization answer received
            listener.doAuthAnswerEvent(this, null, (AppAnswerEvent) event.getData());
          }
          catch (Exception e) {
            // ASR Received, client will comply with request to end the session
            setState(DISCONNECTED);
          }
          break;
        case RECEIVE_FAILED_AUTH_ANSWER:
          // Failed Service-specific authorization answer received
          if (context != null) {
            Request str = createSessionTermRequest();
            context.disconnectUserOrDev(this, str);
            session.send(str, this);
          }
          setState(IDLE);
          break;
        case RECEIVE_ABORT_SESSION_REQUEST:
          listener.doAbortSessionRequestEvent(this, (AbortSessionRequestImpl) event.getData());
          break;
        case SEND_SESSION_TERINATION_REQUEST:
          setState(DISCONNECTED);
          break;
        case TIMEOUT_EXPIRES:
          // Session-Timeout Expires on Access Device Authorization-Lifetime + 
          // Auth-Grace-Period expires on access device
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
          listener.doAbortSessionRequestEvent(this, (AbortSessionRequest) event.getData());
          break;
        case RECEIVE_SESSION_TERINATION_ANSWER:
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
    try {
      sendAndStateLock.lock();
      //FIXME: baranowb: this shouldnt be like that
      if (request.getCommandCode() == factory.getAuthMessageCommandCode()) {
        handleEvent(new Event(Event.Type.RECEIVE_AUTH_ANSWER, factory.createAuthAnswer(answer)));
      }
      else if (request.getCommandCode() == AbortSessionRequestImpl.code) {
        handleEvent(new Event(Event.Type.RECEIVE_ABORT_SESSION_REQUEST, createAbortSessionRequest(request)));
      }
      else if (request.getCommandCode() == ReAuthRequestImpl.code) {
        listener.doReAuthRequestEvent(this, createReAuthRequest(request));
      }
      else if (request.getCommandCode() == SessionTermAnswerImpl.code) {
        listener.doSessionTerminationAnswerEvent(this, new SessionTermAnswerImpl(answer));
        handleEvent(new Event(Event.Type.RECEIVE_SESSION_TERINATION_ANSWER, createSessionTermAnswer(answer)));
      }
      else {
        listener.doOtherEvent(this, factory.createAuthRequest(request), new AppAnswerEventImpl(answer));
      }
    }
    catch (Exception e) {
      logger.debug("Can not process received message", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.RECEIVE_FAILED_AUTH_ANSWER, new AppRequestEventImpl(request)));
    }
    catch (Exception e) {
      logger.debug("Can not handle timeout event", e);
    }
  }

  public Answer processRequest(Request request) {
    try {
      if (request.getCommandCode() == AbortSessionRequestImpl.code) {
        handleEvent(new Event(Event.Type.RECEIVE_ABORT_SESSION_REQUEST, createAbortSessionRequest(request)));
      }
      else if (request.getCommandCode() == ReAuthRequestImpl.code) {
        listener.doReAuthRequestEvent(this, createReAuthRequest(request));
      }
      else {
        //FIXME: baranowb : should it be like that?
        listener.doOtherEvent(this, factory.createAuthRequest(request), null);
      }
    }
    catch (Exception e) {
      logger.debug("Can not process received request", e);
    }

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

}
