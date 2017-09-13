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

package org.jdiameter.server.impl.app.slg;

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
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.api.app.slg.SLgSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.slg.SLgSession;
import org.jdiameter.server.impl.app.slg.Event.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public class SLgServerSessionImpl extends SLgSession
    implements ServerSLgSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(SLgServerSessionImpl.class);

  // Factories and Listeners
  // --------------------------------------------------
  private transient ServerSLgSessionListener listener;
  protected long appId = -1;
  protected IServerSLgSessionData sessionData;

  public SLgServerSessionImpl(IServerSLgSessionData sessionData, ISLgMessageFactory fct, ISessionFactory sf,
                              ServerSLgSessionListener lst) {
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

  public void sendProvideLocationAnswer(ProvideLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  public void sendLocationReportRequest(LocationReportRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == SLgSessionState.class ? (E) this.sessionData.getSLgSessionState() : null;
  }

  @SuppressWarnings("unused")
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

            case RECEIVE_PLR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = SLgSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              listener.doProvideLocationRequestEvent(this, (ProvideLocationRequest) event.getData());
              break;

            case SEND_MESSAGE:
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              newState = SLgSessionState.MESSAGE_SENT_RECEIVED;
              setState(newState);
              break;

            default:
              logger.error("Wrong action in SLg Server FSM. State: IDLE, Event Type: {}", eventType);
              break;
          }
          break;

        case MESSAGE_SENT_RECEIVED:
          switch (eventType) {
            case TIMEOUT_EXPIRES:
              newState = SLgSessionState.TIMEDOUT;
              setState(newState);
              break;

            case RECEIVE_LRA:
              newState = SLgSessionState.TERMINATED;
              setState(newState);
              listener.doLocationReportAnswerEvent(this, (LocationReportRequest) localEvent.getRequest(),
                  (LocationReportAnswer) localEvent.getAnswer());
              break;

            case SEND_MESSAGE:
              try {
                super.session.send(((AppEvent) event.getData()).getMessage(), this);
              } finally {
                newState = SLgSessionState.TERMINATED;
                setState(newState);
              }
              break;

            default:
              throw new InternalException(
                  "Should not receive more messages after initial. Command: " + event.getData());
          }
          break;

        case TERMINATED:
          throw new InternalException("Cant receive message in state TERMINATED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("Cant receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("SLg Server FSM in wrong state: {}", state);
          break;
      }
    } catch (Exception e) {
      throw new InternalException(e);
    } finally {
      sendAndStateLock.unlock();
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
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    } catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
  }

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
    } catch (Exception e) {
      throw new InternalException(e);
    }
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

  @Override
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
    ServerSLgSession session;
    Request request;

    public void run() {
      try {
        switch (request.getCommandCode()) {

          case ProvideLocationRequest.code:
            handleEvent(
                new Event(Event.Type.RECEIVE_PLR, messageFactory.createProvideLocationRequest(request), null));
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
    ServerSLgSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        switch (answer.getCommandCode()) {

          case LocationReportAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_LRA, messageFactory.createLocationReportRequest(request),
                messageFactory.createLocationReportAnswer(answer)));
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