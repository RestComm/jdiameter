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

package org.jdiameter.server.impl.app.s6a;

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
import org.jdiameter.api.s6a.ServerS6aSession;
import org.jdiameter.api.s6a.ServerS6aSessionListener;
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
import org.jdiameter.common.api.app.s6a.IS6aMessageFactory;
import org.jdiameter.common.api.app.s6a.S6aSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.s6a.S6aSession;
import org.jdiameter.server.impl.app.s6a.Event.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * S6a Server session implementation
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:paul.carter-brown@smilecoms.com"> Paul Carter-Brown </a>
 */
public class S6aServerSessionImpl extends S6aSession implements ServerS6aSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(S6aServerSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ServerS6aSessionListener listener;
  protected long appId = -1;
  protected IServerS6aSessionData sessionData;

  public S6aServerSessionImpl(IServerS6aSessionData sessionData, IS6aMessageFactory fct, ISessionFactory sf, ServerS6aSessionListener lst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if ((this.appId = fct.getApplicationId()) < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.listener = lst;
    super.messageFactory = fct;
    this.sessionData = sessionData;
  }

  @Override
  public void sendAuthenticationInformationAnswer(JAuthenticationInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendPurgeUEAnswer(JPurgeUEAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendUpdateLocationAnswer(JUpdateLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendNotifyAnswer(JNotifyAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendCancelLocationRequest(JCancelLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendInsertSubscriberDataRequest(JInsertSubscriberDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendDeleteSubscriberDataRequest(JDeleteSubscriberDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendResetRequest(JResetRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateMachine#getState(java.lang.Class)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == S6aSessionState.class ? (E) this.sessionData.getS6aSessionState() : null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateMachine#handleEvent(org.jdiameter.api.app.StateEvent)
   */
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

            case RECEIVE_AIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doAuthenticationInformationRequestEvent(this, (JAuthenticationInformationRequest) event.getData());
              break;

            case RECEIVE_PUR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doPurgeUERequestEvent(this, (JPurgeUERequest) event.getData());
              break;

            case RECEIVE_ULR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doUpdateLocationRequestEvent(this, (JUpdateLocationRequest) event.getData());
              break;

            case RECEIVE_NOR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doNotifyRequestEvent(this, (JNotifyRequest) event.getData());
              break;

            case SEND_MESSAGE:
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              newState = S6aSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              break;

            default:
              logger.error("Wrong action in S6a Server FSM. State: IDLE, Event Type: {}", eventType);
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

            case RECEIVE_CLA:
              try {
                super.cancelMsgTimer();
                listener.doCancelLocationAnswerEvent(this, (JCancelLocationRequest) localEvent.getRequest(), (JCancelLocationAnswer) localEvent.getAnswer());
              }
              finally {
                newState = S6aSessionState.TERMINATED;
                setState(newState);
              }
              break;

            case RECEIVE_IDA:
              try {
                super.cancelMsgTimer();
                listener.doInsertSubscriberDataAnswerEvent(this, (JInsertSubscriberDataRequest) localEvent.getRequest(),
                    (JInsertSubscriberDataAnswer) localEvent.getAnswer());
              }
              finally {
                newState = S6aSessionState.TERMINATED;
                setState(newState);
              }
              break;

            case RECEIVE_DSA:
              try {
                super.cancelMsgTimer();
                listener.doDeleteSubscriberDataAnswerEvent(this, (JDeleteSubscriberDataRequest) localEvent.getRequest(),
                    (JDeleteSubscriberDataAnswer) localEvent.getAnswer());
              }
              finally {
                newState = S6aSessionState.TERMINATED;
                setState(newState);
              }
              break;

            case RECEIVE_RSA:
              try {
                super.cancelMsgTimer();
                listener.doResetAnswerEvent(this, (JResetRequest) localEvent.getRequest(), (JResetAnswer) localEvent.getAnswer());
              }
              finally {
                newState = S6aSessionState.TERMINATED;
                setState(newState);
              }
              break;

            default:
              throw new InternalException("Should not receive more messages after initial. Command: " + event.getData());
          }
          break;

        case TERMINATED:
          throw new InternalException("Cant receive message in state TERMINATED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("Cant receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("S6a Server FSM in wrong state: {}", state);
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
   * @see org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
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

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
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

    ServerS6aSession session;
    Request request;

    @Override
    public void run() {

      try {
        switch (request.getCommandCode()) {
          case JAuthenticationInformationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_AIR, messageFactory.createAuthenticationInformationRequest(request), null));
            break;

          case JPurgeUERequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_PUR, messageFactory.createPurgeUERequest(request), null));
            break;

          case JUpdateLocationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_ULR, messageFactory.createUpdateLocationRequest(request), null));
            break;

          case JNotifyRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_NOR, messageFactory.createNotifyRequest(request), null));
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

    ServerS6aSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (answer.getCommandCode()) {
          case JCancelLocationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_CLA, messageFactory.createCancelLocationRequest(request),
                messageFactory.createCancelLocationAnswer(answer)));
            break;

          case JInsertSubscriberDataAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_IDA, messageFactory.createInsertSubscriberDataRequest(request),
                messageFactory.createInsertSubscriberDataAnswer(answer)));
            break;

          case JDeleteSubscriberDataAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_DSA, messageFactory.createDeleteSubscriberDataRequest(request),
                messageFactory.createDeleteSubscriberDataAnswer(answer)));
            break;

          case JResetAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_RSA, messageFactory.createResetRequest(request), messageFactory.createResetAnswer(answer)));
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
