/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.impl.app.auth;

import static org.jdiameter.common.api.app.auth.ServerAuthSessionState.DISCONNECTED;
import static org.jdiameter.common.api.app.auth.ServerAuthSessionState.IDLE;
import static org.jdiameter.common.api.app.auth.ServerAuthSessionState.OPEN;
import static org.jdiameter.server.impl.app.auth.Event.Type.RECEIVE_AUTH_REQUEST;
import static org.jdiameter.server.impl.app.auth.Event.Type.RECEVE_ASR_ANSWER;
import static org.jdiameter.server.impl.app.auth.Event.Type.TIMEOUT_EXPIRES;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.client.impl.app.auth.ClientAuthSessionImpl;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.auth.ClientAuthSessionState;
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
import org.jdiameter.common.api.app.auth.IServerAuthActionContext;
import org.jdiameter.common.api.app.auth.ServerAuthSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;
import org.jdiameter.common.impl.app.auth.AppAuthSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAuthSessionImpl extends AppAuthSessionImpl implements ServerAuthSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(ClientAuthSessionImpl.class);

  protected ServerAuthSessionState state = IDLE;
  protected boolean stateless;
  protected long tsTimeout;
  protected ScheduledFuture tsTask;
  protected IAuthMessageFactory factory;
  protected IServerAuthActionContext context;
  protected ServerAuthSessionListener listener;

  private Lock sendAndStateLock = new ReentrantLock();

  // =================== CONSTRUCTORS

  public ServerAuthSessionImpl(Session session, Request initialRequest, ServerAuthSessionListener lst, IAuthMessageFactory fct, long tsTimeout, boolean stateless, StateChangeListener... scListeners)
  {
    if (session == null) {
      throw new IllegalArgumentException("Session can not be null");
    }
    if (lst == null) {
      throw new IllegalArgumentException("Session listener can not be null");
    }
    this.session = session;
    appId = fct.getApplicationId();
    listener = lst;
    factory = fct;
    this.tsTimeout = tsTimeout;
    this.stateless = stateless;
    this.session.setRequestListener(this);
    for (StateChangeListener l : scListeners) {
      addStateChangeNotification(l);
    }
    if (listener instanceof IServerAuthActionContext) {
      context = (IServerAuthActionContext) listener;
    }
    for (StateChangeListener l : scListeners) {
      addStateChangeNotification(l);
    }

    //processRequest(initialRequest);
  }

  public void sendAuthAnswer(AppAnswerEvent appAnswerEvent) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(null, appAnswerEvent);
  }

  public void sendReAuthRequest(ReAuthRequest reAuthRequest) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(null, reAuthRequest);
  }

  public void sendAbortSessionRequest(AbortSessionRequest abortSessionRequest) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_ASR_REQUEST, abortSessionRequest);
  }

  public void sendSessionTerminationAnswer(SessionTermAnswer sessionTermAnswer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(null, sessionTermAnswer);
  }

  protected void send(Event.Type type, AppEvent event) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, event));
      }
      session.send(event.getMessage(), this);
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

  protected void setState(ServerAuthSessionState newState) {
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
      switch (state) {
      case IDLE:
        switch ((Event.Type) event.getType()) {
        case RECEIVE_AUTH_REQUEST:
          listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
          setState(IDLE);
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
        break;
        }
        break;
      }
    }
    catch (Throwable t) {
      throw new InternalException(t);
    }

    return true;
  }

  public boolean handleEventForStatefullSession(StateEvent event) throws InternalException, OverloadException {
    ServerAuthSessionState oldState = state;
    try {
      switch (state) {
      case IDLE: {
        switch ((Event.Type) event.getType()) {
        case RECEIVE_AUTH_REQUEST:
          try {
            listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
            setState(OPEN);
          }
          catch (Exception e) {
            setState(IDLE);
          }
          break;
        case RECEIVE_STR_REQUEST:
          try {
            listener.doSessionTerminationRequestEvent(this,
                (SessionTermRequest) event.getData());
          }
          catch (Exception e) {
            logger.debug("Can not handle event", e);
          }
          break;
        case SEND_ASR_REQUEST:
          setState(DISCONNECTED);
          break;
        case TIMEOUT_EXPIRES:
          if (context != null) {
            context.accessTimeoutElapses(this);
          }
          setState(IDLE);
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
        break;
        }
        break;
      }
      case OPEN: {
        switch ((Event.Type) event.getType()) {
        case RECEIVE_AUTH_REQUEST:
          try {
            listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
          }
          catch (Exception e) {
            setState(IDLE);
          }
          break;
        case RECEIVE_STR_REQUEST:
          try {
            listener.doSessionTerminationRequestEvent(this, (SessionTermRequest) event.getData());
          }
          catch (Exception e) {
            logger.debug("Can not handle event", e);
          }
          setState(IDLE);
          break;
        case SEND_ASR_REQUEST:
          setState(DISCONNECTED);
          break;
        default:
          logger.debug("Unknown event {}", event.getType());
        break;
        }
        break;
      }
      case DISCONNECTED: {
        switch ((Event.Type) event.getType()) {
        case SEND_ASR_FAILURE:
          setState(DISCONNECTED);
          break;
        case RECEVE_ASR_ANSWER:
          listener.doAbortSessionAnswerEvent(this, (AbortSessionAnswer) event.getData());
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
        if (OPEN.equals(state) && context != null) {
          scheduler.schedule(new Runnable() {
            public void run() {
              if (context != null) {
                try {
                  handleEvent(new Event(TIMEOUT_EXPIRES, null));
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
      if (request.getCommandCode() == factory.getAuthMessageCommandCode()) {
        handleEvent(new Event(RECEIVE_AUTH_REQUEST, factory.createAuthRequest(request)));
      }
      else if (request.getCommandCode() == AbortSessionRequestImpl.code) {
        handleEvent(new Event(RECEVE_ASR_ANSWER, new AbortSessionAnswerImpl(answer)));
      }
      else {
        listener.doOtherEvent(this, factory.createAuthRequest(request), new AppAnswerEventImpl(answer));
      }
    }
    catch (Exception e) {
      logger.debug("Can not handle event", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public void timeoutExpired(Request request) {
    try {
      if (request.getCommandCode() == AbortSessionRequestImpl.code) {
        handleEvent(new Event(Event.Type.SEND_ASR_FAILURE, new AbortSessionRequestImpl(request)));
      }
      else {
        logger.debug("Timeout for unknown request {}", request);
      }
    }
    catch (Exception e) {
      logger.debug("Can not handle event", e);
    }
  }

  public Answer processRequest(Request request) {
    if (request != null) {
      if (request.getCommandCode() == factory.getAuthMessageCommandCode()) {
        try {
          sendAndStateLock.lock();
          handleEvent(new Event(RECEIVE_AUTH_REQUEST, factory.createAuthRequest(request)));
        }
        catch (Exception e) {
          logger.debug("Can not handle event", e);
        }
        finally {
          sendAndStateLock.unlock();
        }
      }
      else {
        try {
          listener.doOtherEvent(this, factory.createAuthRequest(request), null);
        }
        catch (Exception e) {
          logger.debug("Can not handle event", e);
        }
      }
    }

    return null;
  }

}