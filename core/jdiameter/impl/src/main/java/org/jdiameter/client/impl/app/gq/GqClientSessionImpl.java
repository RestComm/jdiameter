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

package org.jdiameter.client.impl.app.gq;

import static org.jdiameter.api.Message.SESSION_TERMINATION_REQUEST;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.DISCONNECTED;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.IDLE;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.OPEN;
import static org.jdiameter.common.api.app.auth.ClientAuthSessionState.PENDING;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
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
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.gq.GqClientSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.auth.IClientAuthSessionData;
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

/**
 * Client Gq Application session implementation
 *
 * @author <a href="mailto:webdev@web-ukraine.info"> Yulian Oifa </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class GqClientSessionImpl extends AppAuthSessionImpl implements GqClientSession, EventListener<Request, Answer>, NetworkReqListener {

  protected static final Logger logger = LoggerFactory.getLogger(GqClientSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IAuthMessageFactory factory;
  protected transient IClientAuthActionContext context;
  protected transient ClientAuthSessionListener listener;

  protected static final String TIMER_NAME_TS = "GQ_TS";
  protected IClientAuthSessionData sessionData;

  // Constructors -------------------------------------------------------------

  public GqClientSessionImpl(IClientAuthSessionData sessionData, ISessionFactory sf, ClientAuthSessionListener lst, IAuthMessageFactory fct,
      StateChangeListener<AppSession> scListener, IClientAuthActionContext context,  boolean stateless) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() == null) {
      throw new IllegalArgumentException("ApplicationId can not be null");
    }
    super.appId = fct.getApplicationId();
    this.listener = lst;
    this.factory = fct;
    this.context = context;
    this.sessionData = sessionData;
    this.sessionData.setStateless(stateless);
    super.addStateChangeNotification(scListener);
  }

  // ClientAuthSession Implementation methods ---------------------------------

  @Override
  public void sendAbortSessionAnswer(AbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_SESSION_ABORT_ANSWER, answer);
  }

  @Override
  public void sendAuthRequest(AppRequestEvent request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_AUTH_REQUEST, request);
  }

  @Override
  public void sendReAuthAnswer(ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_AUTH_ANSWER, answer);
  }

  @Override
  public void sendSessionTerminationRequest(SessionTermRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
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
      AvpSet avps = event.getMessage().getAvps();
      Avp destRealmAvp = avps.getAvp(Avp.DESTINATION_REALM);
      if (destRealmAvp != null) {
        sessionData.setDestinationRealm(destRealmAvp.getDiameterIdentity());
      }
      Avp destHostAvp = avps.getAvp(Avp.DESTINATION_HOST);
      if (destHostAvp != null) {
        sessionData.setDestinationHost(destHostAvp.getDiameterIdentity());
      }
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
    return this.sessionData.isStateless();
  }

  @SuppressWarnings("unchecked")
  protected void setState(ClientAuthSessionState newState) {
    IAppSessionState oldState = sessionData.getClientAuthSessionState();
    sessionData.setClientAuthSessionState(newState);
    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> eClass) {
    return eClass == ClientAuthSessionState.class ? (E) sessionData.getClientAuthSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return sessionData.isStateless() ? handleEventForStatelessSession(event) : handleEventForStatefulSession(event);
  }

  public boolean handleEventForStatelessSession(StateEvent event) throws InternalException, OverloadException {
    try {
      ClientAuthSessionState state = sessionData.getClientAuthSessionState();
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
              logger.debug("Unknown event [{}]", event.getType());
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
            case SEND_SESSION_TERMINATION_REQUEST:
              // Current State: OPEN
              // Event: Service to user is terminated
              // Action: Disconnect User/Device
              // New State: IDLE
              setState(IDLE);
              break;
            default:
              logger.debug("Unknown event [{}]", event.getType());
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
                context.accessTimeoutElapses(this);
                Request str = createSessionTermRequest();
                context.disconnectUserOrDev(this, str);
                session.send(str, this);
              }
              // IDLE is the same as DISCON
              setState(IDLE);
              break;
            default:
              logger.debug("Unknown event [{}]", event.getType());
              break;
          }
          break;
      }

      // post processing
      if (oldState != state) {
        if (DISCONNECTED.equals(state) || IDLE.equals(state)) {
          cancelTsTimer();
        }
        else if (OPEN.equals(state) && context != null && context.getAccessTimeout() > 0) {
          cancelTsTimer();
          startTsTimer();
        }
      }
    }
    catch (Throwable t) {
      throw new InternalException(t);
    }

    return true;
  }

  public boolean handleEventForStatefulSession(StateEvent event) throws InternalException, OverloadException {
    ClientAuthSessionState state = sessionData.getClientAuthSessionState();
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
              logger.debug("Unknown event [{}]", event.getType());
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
            case SEND_SESSION_TERMINATION_REQUEST:
              // Current State: OPEN
              // Event: Service to user is terminated
              // Action: Disconnect User/Device
              // New State: IDLE
              setState(DISCONNECTED);
              break;
            default:
              logger.debug("Unknown event [{}]", event.getType());
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
                context.accessTimeoutElapses(this);
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
              logger.debug("Unknown event [{}]", event.getType());
              break;
          }
          break;
        }
        default: {
          logger.debug("Unknown state [{}]", state);
          break;
        }
      }

      // post processing
      if (oldState != state) {
        if (OPEN.equals(state) && context != null && context.getAccessTimeout() > 0) {
          cancelTsTimer();
          startTsTimer();
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
    AnswerDelivery ad = new AnswerDelivery();
    ad.session = this;
    ad.request = request;
    ad.answer = answer;
    super.scheduler.execute(ad);
  }

  @Override
  public void timeoutExpired(Request request) {
    try {
      //FIXME: should this also be async ?
      handleEvent(new Event(Event.Type.RECEIVE_FAILED_AUTH_ANSWER, new AppRequestEventImpl(request)));
    }
    catch (Exception e) {
      logger.debug("Can not handle timeout event", e);
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

  protected void startTsTimer() throws IllegalArgumentException, InternalException {
    try {
      sendAndStateLock.lock();
      sessionData.setTsTimerId(super.timerFacility.schedule(sessionData.getSessionId(), TIMER_NAME_TS, context.getAccessTimeout()));
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void cancelTsTimer() {
    try {
      sendAndStateLock.lock();
      Serializable timerId = sessionData.getTsTimerId();
      if (timerId != null) {
        super.timerFacility.cancel(timerId);
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
        if (context != null) {
          try {
            handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, null));
          }
          catch (Exception e) {
            logger.debug("Can not handle event", e);
          }
        }
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
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
    return session.createRequest(SESSION_TERMINATION_REQUEST, appId, sessionData.getDestinationRealm(), sessionData.getDestinationHost());
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
    GqClientSessionImpl other = (GqClientSessionImpl) obj;
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

  private class RequestDelivery implements Runnable {
    GqClientSession session;
    Request request;

    @Override
    public void run() {
      try {
        if (request.getCommandCode() == AbortSessionRequest.code) {
          handleEvent(new Event(Event.Type.RECEIVE_ABORT_SESSION_REQUEST, createAbortSessionRequest(request)));
        }
        else if (request.getCommandCode() == ReAuthRequest.code) {
          listener.doReAuthRequestEvent(session, createReAuthRequest(request));
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
    GqClientSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        sendAndStateLock.lock();
        // FIXME: baranowb: this shouldn't be like that?
        if (answer.getCommandCode() == factory.getAuthMessageCommandCode()) {
          handleEvent(new Event(Event.Type.RECEIVE_AUTH_ANSWER, factory.createAuthAnswer(answer)));
        }
        else if (answer.getCommandCode() == SessionTermAnswer.code) {
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
