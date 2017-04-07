 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
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

  protected IServerAuthSessionData sessionData;

  // Session State Handling ---------------------------------------------------
  private Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IAuthMessageFactory factory;
  protected transient IServerAuthActionContext context;
  protected transient ServerAuthSessionListener listener;

  // Ts Timer -----------------------------------------------------------------
  protected static final String TIMER_NAME_TS = "AUTH_TS";

  // Constructors -------------------------------------------------------------

  public ServerAuthSessionImpl(IServerAuthSessionData sessionData, ISessionFactory sf, ServerAuthSessionListener lst, IAuthMessageFactory fct,
      StateChangeListener<AppSession> scListener, IServerAuthActionContext context, long tsTimeout, boolean stateless) {
    super(sf, sessionData);

    super.appId = fct.getApplicationId();
    this.listener = lst;
    this.factory = fct;
    this.context = context;
    this.sessionData = sessionData;
    this.sessionData.setStateless(stateless);
    this.sessionData.setTsTimeout(tsTimeout);
    super.addStateChangeNotification(scListener);
  }

  // ServerAuthSession Implementation methods ---------------------------------

  @Override
  public void sendAuthAnswer(AppAnswerEvent appAnswerEvent) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(null, appAnswerEvent);
  }

  @Override
  public void sendReAuthRequest(ReAuthRequest reAuthRequest) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(null, reAuthRequest);
  }

  @Override
  public void sendAbortSessionRequest(AbortSessionRequest abortSessionRequest)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_ASR_REQUEST, abortSessionRequest);
  }

  @Override
  public void sendSessionTerminationAnswer(SessionTermAnswer sessionTermAnswer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
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

  @Override
  public boolean isStateless() {
    return sessionData.isStateless();
  }

  @SuppressWarnings("unchecked")
  protected void setState(ServerAuthSessionState newState) {
    IAppSessionState oldState = sessionData.getServerAuthSessionState();
    sessionData.setServerAuthSessionState(newState);
    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> eClass) {
    return eClass == ServerAuthSessionState.class ? (E) sessionData.getServerAuthSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return isStateless() ? handleEventForStatelessSession(event) : handleEventForStatefullSession(event);
  }

  public boolean handleEventForStatelessSession(StateEvent event) throws InternalException, OverloadException {
    try {
      switch (sessionData.getServerAuthSessionState()) {
        case IDLE:
          switch ((Event.Type) event.getType()) {
            case RECEIVE_AUTH_REQUEST:
              // Current State: IDLE
              // Event: Service-specific authorization request received, and successfully processed
              // Action: Send service specific answer
              // New State: IDLE
              setState(IDLE);
              listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
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
    ServerAuthSessionState state = sessionData.getServerAuthSessionState();
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
                setState(OPEN);
                listener.doAuthRequestEvent(this, (AppRequestEvent) event.getData());
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
                setState(IDLE);
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
                setState(IDLE);
                listener.doSessionTerminationRequestEvent(this, (SessionTermRequest) event.getData());
              }
              catch (Exception e) {
                logger.debug("Can not handle event", e);
              }
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
              setState(IDLE);
              listener.doAbortSessionAnswerEvent(this, (AbortSessionAnswer) event.getData());
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
          if (context != null) {
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

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  @Override
  public void timeoutExpired(Request request) {
    try {
      if (request.getCommandCode() == AbortSessionRequest.code) {
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

  @Override
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

  protected void startTsTimer() {
    try {
      sendAndStateLock.lock();
      if (sessionData.getTsTimeout() > 0) {
        sessionData.setTsTimerId(super.timerFacility.schedule(sessionData.getSessionId(), TIMER_NAME_TS, sessionData.getTsTimeout()));
      }
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void cancelTsTimer() {
    try {
      sendAndStateLock.lock();
      Serializable tsTimerId = sessionData.getTsTimerId();
      if (tsTimerId != null) {
        super.timerFacility.cancel(tsTimerId);
        sessionData.setTsTimerId(null);
      }
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
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else if (timerName.equals(TIMER_NAME_TS)) {
      try {
        sendAndStateLock.lock();
        sessionData.setTsTimerId(null);
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
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((sessionData == null) ? 0 : sessionData.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ServerAuthSessionImpl other = (ServerAuthSessionImpl) obj;
    if (sessionData == null) {
      if (other.sessionData != null) {
        return false;
      }
    }
    else if (!sessionData.equals(other.sessionData)) {
      return false;
    }
    return true;
  }

  @Override
  public void release() {
    if (isValid()) {
      try {
        sendAndStateLock.lock();
        super.release();
        this.context = null;
        this.listener = null;
      }
      catch (Exception e) {
        logger.debug("Failed to release session", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
    }
  }

  private class RequestDelivery implements Runnable {
    ServerAuthSession session;
    Request request;

    @Override
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

    @Override
    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getCommandCode() == factory.getAuthMessageCommandCode()) {
          handleEvent(new Event(RECEIVE_AUTH_REQUEST, factory.createAuthRequest(request)));
        }
        else if (request.getCommandCode() == AbortSessionRequest.code) {
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