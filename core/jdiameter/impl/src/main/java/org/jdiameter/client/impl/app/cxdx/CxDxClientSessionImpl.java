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

package org.jdiameter.client.impl.app.cxdx;

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
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.cxdx.Event.Type;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.cxdx.CxDxSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diameter Cx/Dx Client Session implementation
 *
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CxDxClientSessionImpl extends CxDxSession implements ClientCxDxSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(CxDxClientSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ClientCxDxSessionListener listener;

  protected long appId = -1;
  protected IClientCxDxSessionData sessionData;
  public CxDxClientSessionImpl(IClientCxDxSessionData sessionData, ICxDxMessageFactory fct, ISessionFactory sf, ClientCxDxSessionListener lst) {
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
    return stateType == CxDxSessionState.class ? (E) this.sessionData.getCxDxSessionState() : null;
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

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendLocationInformationRequest(org.jdiameter.api.cxdx.events.JLocationInfoRequest)
   */
  @Override
  public void sendLocationInformationRequest(JLocationInfoRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendMultimediaAuthRequest(org.jdiameter.api.cxdx.events.JMultimediaAuthRequest)
   */
  @Override
  public void sendMultimediaAuthRequest(JMultimediaAuthRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendServerAssignmentRequest(org.jdiameter.api.cxdx.events.JServerAssignmentRequest)
   */
  @Override
  public void sendServerAssignmentRequest(JServerAssignmentRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendUserAuthorizationRequest(org.jdiameter.api.cxdx.events.JUserAuthorizationRequest)
   */
  @Override
  public void sendUserAuthorizationRequest(JUserAuthorizationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendPushProfileAnswer(org.jdiameter.api.cxdx.events.JPushProfileAnswer)
   */
  @Override
  public void sendPushProfileAnswer(JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.cxdx.ClientCxDxSession#sendRegistrationTerminationAnswer(org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
   */
  @Override
  public void sendRegistrationTerminationAnswer(JRegistrationTerminationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
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
      final CxDxSessionState state = this.sessionData.getCxDxSessionState();
      CxDxSessionState newState = null;
      Event.Type eventType = (Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case RECEIVE_PPR:
              //super.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
              this.sessionData.setBuffer( (Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doPushProfileRequest(this, (JPushProfileRequest) event.getData());
              break;

            case RECEIVE_RTR:
              //super.scheduler.schedule(new TimeoutTimerTask((Request) ((AppEvent) event.getData()).getMessage()), CxDxSession._TX_TIMEOUT, TimeUnit.MILLISECONDS);
              newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              this.sessionData.setBuffer( (Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              listener.doRegistrationTerminationRequest(this, (JRegistrationTerminationRequest) event.getData());
              break;

            case SEND_MESSAGE:
              newState = CxDxSessionState.MESSAGE_SENT_RECEIVED;
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              setState(newState); //FIXME: is this ok to be here?
              break;

            default:
              logger.error("Invalid Event Type {} for Cx/Dx Client Session at state {}.", eventType, sessionData.getCxDxSessionState());
              break;
          }
          break;

        case MESSAGE_SENT_RECEIVED:
          switch (eventType) {
            case TIMEOUT_EXPIRES:
              newState = CxDxSessionState.TIMEDOUT;
              setState(newState);
              break;

            case SEND_MESSAGE:
              //          if (super.timeoutTaskFuture != null) {
              //            super.timeoutTaskFuture.cancel(false);
              //            super.timeoutTaskFuture = null;
              //          }
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              newState = CxDxSessionState.TERMINATED;
              setState(newState);
              break;

            case RECEIVE_LIA:
              newState = CxDxSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doLocationInformationAnswer(this, null, (JLocationInfoAnswer) event.getData());
              break;

            case RECEIVE_MAA:
              newState = CxDxSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doMultimediaAuthAnswer(this, null, (JMultimediaAuthAnswer) event.getData());
              break;

            case RECEIVE_SAA:
              newState = CxDxSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doServerAssignmentAnswer(this, null, (JServerAssignmentAnswer) event.getData());
              break;

            case RECEIVE_UAA:
              newState = CxDxSessionState.TERMINATED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doUserAuthorizationAnswer(this, null, (JUserAuthorizationAnswer) event.getData());
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
          logger.error("Cx/Dx Client FSM in wrong state: {}", state);
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
  protected void setState(CxDxSessionState newState) {
    CxDxSessionState oldState = this.sessionData.getCxDxSessionState();
    this.sessionData.setCxDxSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, oldState, newState);
    }
    if (newState == CxDxSessionState.TERMINATED || newState == CxDxSessionState.TIMEDOUT) {
      super.cancelMsgTimer();
      this.release();
      //      if (super.timeoutTaskFuture != null) {
      //        timeoutTaskFuture.cancel(true);
      //        timeoutTaskFuture = null;
      //      }
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
    else if (timerName.equals(CxDxSession.TIMER_NAME_MSG_TIMEOUT)) {
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


  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (appId ^ (appId >>> 32));
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
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    CxDxClientSessionImpl other = (CxDxClientSessionImpl) obj;
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
    ClientCxDxSession session;
    Request request;

    @Override
    public void run() {
      try {
        if (request.getCommandCode() == JRegistrationTerminationRequest.code) {
          handleEvent(new Event(Event.Type.RECEIVE_RTR, messageFactory.createRegistrationTerminationRequest(request), null));
        }
        else if (request.getCommandCode() == JPushProfileRequest.code) {
          handleEvent(new Event(Event.Type.RECEIVE_PPR, messageFactory.createPushProfileRequest(request), null));
        }
        else {
          listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientCxDxSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (answer.getCommandCode()) {
          case JUserAuthorizationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_UAA, null, messageFactory.createUserAuthorizationAnswer(answer)));
            break;

          case JServerAssignmentAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_SAA, null, messageFactory.createServerAssignmentAnswer(answer)));
            break;

          case JMultimediaAuthAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_MAA, null, messageFactory.createMultimediaAuthAnswer(answer)));
            break;

          case JLocationInfoAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_LIA, null, messageFactory.createLocationInfoAnswer(answer)));
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
