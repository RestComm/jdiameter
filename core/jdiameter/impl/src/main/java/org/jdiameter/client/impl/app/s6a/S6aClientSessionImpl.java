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

package org.jdiameter.client.impl.app.s6a;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.s6a.ClientS6aSession;
import org.jdiameter.api.s6a.ClientS6aSessionListener;
import org.jdiameter.api.s6a.events.JAuthenticationInformationAnswer;
import org.jdiameter.api.s6a.events.JAuthenticationInformationRequest;
import org.jdiameter.api.s6a.events.JCancelLocationAnswer;
import org.jdiameter.api.s6a.events.JCancelLocationRequest;
import org.jdiameter.api.s6a.events.JDeleteSubscriberDataAnswer;
import org.jdiameter.api.s6a.events.JDeleteSubscriberDataRequest;
import org.jdiameter.api.s6a.events.JInsertSubscriberDataAnswer;
import org.jdiameter.api.s6a.events.JInsertSubscriberDataRequest;
import org.jdiameter.api.s6a.events.JNotifyAnswer;
import org.jdiameter.api.s6a.events.JNotifyRequest;
import org.jdiameter.api.s6a.events.JPurgeUEAnswer;
import org.jdiameter.api.s6a.events.JPurgeUERequest;
import org.jdiameter.api.s6a.events.JResetAnswer;
import org.jdiameter.api.s6a.events.JResetRequest;
import org.jdiameter.api.s6a.events.JUpdateLocationAnswer;
import org.jdiameter.api.s6a.events.JUpdateLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.s6a.Event.Type;
import org.jdiameter.common.api.app.s6a.IS6aMessageFactory;
import org.jdiameter.common.api.app.s6a.S6aSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.s6a.S6aSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diameter S6a Client Session implementation
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class S6aClientSessionImpl extends S6aSession implements ClientS6aSession, EventListener<Request, Answer>, NetworkReqListener  {

  private static final Logger logger = LoggerFactory.getLogger(S6aClientSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ClientS6aSessionListener listener;

  protected long appId = -1;
  protected IClientS6aSessionData sessionData;
  public S6aClientSessionImpl(IClientS6aSessionData sessionData, IS6aMessageFactory fct, ISessionFactory sf, ClientS6aSessionListener lst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.appId = fct.getApplicationId();
    this.listener = lst;
    super.messageFactory = fct;
    this.sessionData = sessionData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.app.StateMachine#getState(java.lang.Class)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == S6aSessionState.class ? (E) this.sessionData.getS6aSessionState() : null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd  = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }

  @Override
  public void sendAuthenticationInformationRequest(JAuthenticationInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendPurgeUERequest(JPurgeUERequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendNotifyRequest(JNotifyRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendUpdateLocationRequest(JUpdateLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendCancelLocationAnswer(JCancelLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendInsertSubscriberDataAnswer(JInsertSubscriberDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendDeleteSubscriberDataAnswer(JDeleteSubscriberDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendResetAnswer(JResetAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.api.Message, org.jdiameter.api.Message)
   */
  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  @Override
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
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME: throw new InternalException("Generic session is not valid.");
        return false;
      }
      final S6aSessionState state = this.sessionData.getS6aSessionState();
      S6aSessionState newState = null;
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case RECEIVE_CLR:
              this.sessionData.setBuffer( (Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doCancelLocationRequestEvent(this, (JCancelLocationRequest) event.getData());
              break;

            case RECEIVE_IDR:
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              this.sessionData.setBuffer( (Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              listener.doInsertSubscriberDataRequestEvent(this, (JInsertSubscriberDataRequest) event.getData());
              break;

            case RECEIVE_DSR:
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              this.sessionData.setBuffer( (Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              listener.doDeleteSubscriberDataRequestEvent(this, (JDeleteSubscriberDataRequest) event.getData());
              break;

            case RECEIVE_RSR:
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              this.sessionData.setBuffer( (Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              listener.doResetRequestEvent(this, (JResetRequest) event.getData());
              break;

            case SEND_MESSAGE:
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              setState(newState); //FIXME: is this ok to be here?
              break;

            default:
              logger.error("Invalid Event Type {} for S6a Client Session at state {}.", eventType, sessionData.getS6aSessionState());
              break;
          }
          break;

        case MESSAGE_SENT_RECEIVED:
          switch (eventType) {
            case TIMEOUT_EXPIRES:
              newState = S6aSessionState.TIMEDOUT;
              setState(newState);
              break;

            case SEND_MESSAGE:
              try {
                super.session.send(((AppEvent) event.getData()).getMessage(), this);
              }
              finally {
                newState = S6aSessionState.TERMINATED;
                setState(newState);
              }
              break;

            case RECEIVE_ULA:
              newState = S6aSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doUpdateLocationAnswerEvent(this, (JUpdateLocationRequest) localEvent.getRequest(), (JUpdateLocationAnswer) localEvent.getAnswer());
              break;

            case RECEIVE_AIA:
              newState = S6aSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doAuthenticationInformationAnswerEvent(this, (JAuthenticationInformationRequest) localEvent.getRequest(),
                  (JAuthenticationInformationAnswer) localEvent.getAnswer());
              break;

            case RECEIVE_PUA:
              newState = S6aSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doPurgeUEAnswerEvent(this, (JPurgeUERequest) localEvent.getRequest(), (JPurgeUEAnswer) localEvent.getAnswer());
              break;

            case RECEIVE_NOA:
              newState = S6aSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doNotifyAnswerEvent(this, (JNotifyRequest) localEvent.getRequest(), (JNotifyAnswer) localEvent.getAnswer());
              break;

            default:
              throw new InternalException("Unexpected/Unknown message received: " + event.getData());
          }
          break;

        case TERMINATED:
          throw new InternalException("Cant receive message in state TERMINATED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("Cant receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("S6a Client FSM in wrong state: {}", state);
          break;
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

  @SuppressWarnings("unchecked")
  protected void setState(S6aSessionState newState) {
    S6aSessionState oldState = this.sessionData.getS6aSessionState();
    this.sessionData.setS6aSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, oldState, newState);
    }
    if (newState == S6aSessionState.TERMINATED || newState == S6aSessionState.TIMEDOUT) {
      super.cancelMsgTimer();
      this.release();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else if (timerName.equals(S6aSession.TIMER_NAME_MSG_TIMEOUT)) {
      try {
        sendAndStateLock.lock();
        try {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(this.sessionData.getBuffer()), null));
        }
        catch (Exception e) {
          logger.debug("Failure handling Timeout event.");
        }
        this.sessionData.setBuffer(null);
        this.sessionData.setTsTimerId(null);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }


  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.impl.app.s6a.S6aSession#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (appId ^ (appId >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.impl.app.s6a.S6aSession#equals(java.lang.Object)
   */
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

    S6aClientSessionImpl other = (S6aClientSessionImpl) obj;
    if (appId != other.appId) {
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
    ClientS6aSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case JCancelLocationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_CLR, messageFactory.createCancelLocationRequest(request), null));
            break;

          case JInsertSubscriberDataRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_IDR, messageFactory.createInsertSubscriberDataRequest(request), null));
            break;

          case JDeleteSubscriberDataRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_DSR, messageFactory.createDeleteSubscriberDataRequest(request), null));
            break;

          case JResetRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_RSR, messageFactory.createResetRequest(request), null));
            break;

          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientS6aSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (answer.getCommandCode()) {
          case JUpdateLocationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_ULA, messageFactory.createUpdateLocationRequest(request),
                messageFactory.createUpdateLocationAnswer(answer)));
            break;

          case JAuthenticationInformationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_AIA, messageFactory.createAuthenticationInformationRequest(request),
                messageFactory.createAuthenticationInformationAnswer(answer)));
            break;

          case JPurgeUEAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_PUA, messageFactory.createPurgeUERequest(request), messageFactory.createPurgeUEAnswer(answer)));
            break;

          case JNotifyAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_NOA, messageFactory.createNotifyRequest(request), messageFactory.createNotifyAnswer(answer)));
            break;

          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
    }
  }

}
