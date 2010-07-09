/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.server.impl.app.auth;

import static org.jdiameter.common.api.app.auth.ServerAuthSessionState.DISCONNECTED;
import static org.jdiameter.common.api.app.auth.ServerAuthSessionState.IDLE;
import static org.jdiameter.common.api.app.auth.ServerAuthSessionState.OPEN;
import static org.jdiameter.server.impl.app.auth.Event.Type.RECEIVE_ASR_ANSWER;
import static org.jdiameter.server.impl.app.auth.Event.Type.RECEIVE_AUTH_REQUEST;
import static org.jdiameter.server.impl.app.auth.Event.Type.TIMEOUT_EXPIRES;

import java.io.Serializable;
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
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.auth.ClientAuthSessionState;
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
import org.jdiameter.common.api.app.auth.IAuthSessionFactory;
import org.jdiameter.common.api.app.auth.IServerAuthActionContext;
import org.jdiameter.common.api.app.auth.ServerAuthSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;
import org.jdiameter.common.impl.app.auth.AppAuthSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server Authorization session implementation
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerAuthSessionImpl extends AppAuthSessionImpl implements ServerAuthSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(ServerAuthSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected boolean stateless = false;
  protected ServerAuthSessionState state = IDLE;
  private Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IAuthMessageFactory factory;
  protected transient IServerAuthActionContext context;
  protected transient ServerAuthSessionListener listener;

  // Ts Timer -----------------------------------------------------------------
  protected long tsTimeout;
  //protected ScheduledFuture tsTask;
  protected Serializable timerId_ts;
  protected final static String TIMER_NAME_TS="AUTH_TS";

  // Constructors -------------------------------------------------------------

  public ServerAuthSessionImpl(String sessionId,SessionFactory sf, Request initialRequest, ServerAuthSessionListener lst, IAuthMessageFactory fct, StateChangeListener<AppSession> scListener,IServerAuthActionContext context, long tsTimeout, boolean stateless) {
    super(sf,sessionId);  


    appId = fct.getApplicationId();
    listener = lst;
    factory = fct;
    this.context = context;
    this.tsTimeout = tsTimeout;
    this.stateless = stateless;
    super.addStateChangeNotification(scListener);
  }

  // ServerAuthSession Implementation methods ---------------------------------

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

  @SuppressWarnings("unchecked")
  protected void setState(ServerAuthSessionState newState) {
    IAppSessionState oldState = state;
    state = newState;
    super.sessionDataSource.updateSession(this);
    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  @SuppressWarnings("unchecked")
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
          // Current State: IDLE
          // Event: Service-specific authorization request received, and successfully processed
          // Action: Send service specific answer
          // New State: IDLE
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
            // Current State: IDLE
            // Event: Service-specific authorization request received, and user is authorized
            // Action: Send successful service specific answer
            // New State: OPEN
            listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
            setState(OPEN);
          }
          catch (Exception e) {
            setState(IDLE);
          }
          break;
        case RECEIVE_STR_REQUEST:
          try {
            // Current State: ANY
            // Event: STR Received
            // Action: Send STA, Cleanup
            // New State: IDLE
            listener.doSessionTerminationRequestEvent(this, (SessionTermRequest) event.getData());
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
            // Current State: OPEN
            // Event: Service-specific authorization request received, and user is authorized
            // Action: Send successful service specific answer
            // New State: OPEN
            listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
          }
          catch (Exception e) {
            // Current State: OPEN
            // Event: Service-specific authorization request received, and user is not authorized
            // Action: Send failed service specific answer, Cleanup
            // New State: IDLE
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
          // Current State: OPEN
          // Event: Home server wants to terminate the service
          // Action: Send ASR
          // New State: DISCON
          setState(DISCONNECTED);
          break;
        case TIMEOUT_EXPIRES:
          // Current State: OPEN
          // Event: Authorization-Lifetime (and Auth-Grace-Period) expires on home server.
          // Action: Cleanup
          // New State: IDLE

          // Current State: OPEN
          // Event: Session-Timeout expires on home server
          // Action: Cleanup
          // New State: IDLE
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
      case DISCONNECTED: {
        switch ((Event.Type) event.getType()) {
        case SEND_ASR_FAILURE:
          // Current State: DISCON
          // Event: Failure to send ASR
          // Action: Wait, Re-send ASR
          // New State: DISCON
          setState(DISCONNECTED);
          break;
        case RECEIVE_ASR_ANSWER:
          // Current State: DISCON
          // Event: ASR successfully sent and ASA Received with Result-Code
          // Action: Cleanup
          // New State: IDLE
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
          if(context != null) {
            cancelTsTimer();
            startTsTimer();
          }
          //          scheduler.schedule(new Runnable() {
          //            public void run() {
          //              if (context != null) {
          //                try {
          //                  handleEvent(new Event(TIMEOUT_EXPIRES, null));
          //                }
          //                catch (Exception e) {
          //                  logger.debug("Can not handle event", e);
          //                }
          //              }
          //            }
          //          }, context.createAccessTimer(), TimeUnit.MILLISECONDS);
        }
      }
    }
    catch (Throwable t) {
      throw new InternalException(t);
    }

    return true;
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
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
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }
  
  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.auth.AppAuthSessionImpl#relink(org.jdiameter.client.api.IContainer)
   */
  @Override
  public void relink(IContainer stack) {
    if(super.sf == null) {
      super.relink(stack);
      IAuthSessionFactory fct = (IAuthSessionFactory) ((ISessionFactory)super.sf).getAppSessionFactory(ServerAuthSession.class);
      this.listener = fct.getServerSessionListener();
      this.context = fct.getServerSessionContext();
      this.factory = fct.getMessageFactory();
    }
  }


  protected void startTsTimer() {
    try {
      sendAndStateLock.lock();
      this.timerId_ts=super.timerFacility.schedule(sessionId, TIMER_NAME_TS, tsTimeout);
      super.sessionDataSource.updateSession(this);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void cancelTsTimer() {
    try {
      sendAndStateLock.lock();
      super.timerFacility.cancel(timerId_ts);
      this.timerId_ts = null;
      super.sessionDataSource.updateSession(this);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if(timerName.equals(TIMER_NAME_TS)) {	
      try {
        sendAndStateLock.lock();
        this.timerId_ts = null;
        handleEvent(new Event(TIMEOUT_EXPIRES, null));
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      super.onTimer(timerName);
    }
  }



  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + (stateless ? 1231 : 1237);
    result = prime * result + ((timerId_ts == null) ? 0 : timerId_ts.hashCode());
    result = prime * result + (int) (tsTimeout ^ (tsTimeout >>> 32));
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    ServerAuthSessionImpl other = (ServerAuthSessionImpl) obj;
    if (state == null) {
      if (other.state != null) {
        return false;
      }
    }
    else if (!state.equals(other.state)) {
      return false;
    }
    if (stateless != other.stateless) {
      return false;
    }
    if (timerId_ts == null) {
      if (other.timerId_ts != null) {
        return false;
      }
    }
    else if (!timerId_ts.equals(other.timerId_ts)) {
      return false;
    }
    if (tsTimeout != other.tsTimeout) {
      return false;
    }
    return true;
  }

  private class RequestDelivery implements Runnable {
    ServerAuthSession session;
    Request request;

    public void run() {
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
            listener.doOtherEvent(session, factory.createAuthRequest(request), null);
          }
          catch (Exception e) {
            logger.debug("Can not handle event", e);
          }
        }
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ServerAuthSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getCommandCode() == factory.getAuthMessageCommandCode()) {
          handleEvent(new Event(RECEIVE_AUTH_REQUEST, factory.createAuthRequest(request)));
        }
        else if (request.getCommandCode() == AbortSessionRequestImpl.code) {
          handleEvent(new Event(RECEIVE_ASR_ANSWER, new AbortSessionAnswerImpl(answer)));
        }
        else {
          listener.doOtherEvent(session, factory.createAuthRequest(request), new AppAnswerEventImpl(answer));
        }
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

}