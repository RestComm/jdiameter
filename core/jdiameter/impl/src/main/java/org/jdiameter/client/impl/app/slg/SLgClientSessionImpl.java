/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.jdiameter.client.impl.app.slg;

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
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.slg.Event.Type;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.api.app.slg.SLgSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.slg.SLgSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public class SLgClientSessionImpl extends SLgSession
    implements ClientSLgSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(SLgClientSessionImpl.class);

  private transient ClientSLgSessionListener listener;

  protected long appId = -1;
  protected IClientSLgSessionData sessionData;

  public SLgClientSessionImpl(IClientSLgSessionData sessionData, ISLgMessageFactory fct, ISessionFactory sf,
                              ClientSLgSessionListener lst) {
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

  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == SLgSessionState.class ? (E) this.sessionData.getSLgSessionState() : null;
  }

  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }

  public void sendProvideLocationRequest(ProvideLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  public void sendLocationReportAnswer(LocationReportAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
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
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    } catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    } catch (Exception e) {
      throw new InternalException(e);
    }
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME: throw new InternalException("Generic session is not valid.");
        return false;
      }
      final SLgSessionState state = this.sessionData.getSLgSessionState();
      SLgSessionState newState;
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {

            case SEND_MESSAGE:
              newState = SLgSessionState.MESSAGE_SENT_RECEIVED;
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              setState(newState); // FIXME: is this ok to be here?
              break;

            case RECEIVE_LRR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = SLgSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doLocationReportRequestEvent(this, (LocationReportRequest) event.getData());
              break;

            default:
              logger.error("Invalid Event Type {} for SLg Client Session at state {}.", eventType,
                  sessionData.getSLgSessionState());
              break;
          }
          break;

        case MESSAGE_SENT_RECEIVED:
          switch (eventType) {
            case TIMEOUT_EXPIRES:
              newState = SLgSessionState.TIMEDOUT;
              setState(newState);
              break;

            case SEND_MESSAGE:
              try {
                super.session.send(((AppEvent) event.getData()).getMessage(), this);
              } finally {
                newState = SLgSessionState.TERMINATED;
                setState(newState);
              }
              break;

            case RECEIVE_PLA:
              newState = SLgSessionState.TERMINATED;
              setState(newState);
              listener.doProvideLocationAnswerEvent(this, (ProvideLocationRequest) localEvent.getRequest(),
                  (ProvideLocationAnswer) localEvent.getAnswer());
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
          logger.error("SLg Client FSM in wrong state: {}", state);
          break;
      }
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      sendAndStateLock.unlock();
    }
    return true;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void setState(SLgSessionState newState) {
    SLgSessionState oldState = this.sessionData.getSLgSessionState();
    this.sessionData.setSLgSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, oldState, newState);
    }
    if (newState == SLgSessionState.TERMINATED || newState == SLgSessionState.TIMEDOUT) {
      super.cancelMsgTimer();
      this.release();
    }
  }

  public void onTimer(String timerName) {
    if (timerName.equals(SLgSession.TIMER_NAME_MSG_TIMEOUT)) {
      try {
        sendAndStateLock.lock();
        try {
          handleEvent(
              new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(this.sessionData.getBuffer()), null));
        } catch (Exception e) {
          logger.debug("Failure handling Timeout event.");
        }
        this.sessionData.setBuffer(null);
        this.sessionData.setTsTimerId(null);
      } finally {
        sendAndStateLock.unlock();
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (appId ^ (appId >>> 32));
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

    SLgClientSessionImpl other = (SLgClientSessionImpl) obj;
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
      } catch (Exception e) {
        logger.debug("Failed to release session", e);
      } finally {
        sendAndStateLock.unlock();
      }
    } else {
      logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
    }
  }

  private class RequestDelivery implements Runnable {
    ClientSLgSession session;
    Request request;

    public void run() {
      try {
        switch (request.getCommandCode()) {

          case LocationReportRequest.code:
            handleEvent(
                new Event(Event.Type.RECEIVE_LRR, messageFactory.createLocationReportRequest(request), null));
            break;

          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      } catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientSLgSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        switch (answer.getCommandCode()) {

          case ProvideLocationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_PLA, messageFactory.createProvideLocationRequest(request),
                messageFactory.createProvideLocationAnswer(answer)));
            break;

          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      } catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
    }
  }

}