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

package org.jdiameter.server.impl.app.rx;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
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
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.api.rx.ServerRxSessionListener;
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxReAuthAnswer;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.rx.IRxMessageFactory;
import org.jdiameter.common.api.app.rx.IServerRxSessionContext;
import org.jdiameter.common.api.app.rx.ServerRxSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.rx.AppRxSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 3GPP IMS Rx Reference Point Server session implementation
 *
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerRxSessionImpl extends AppRxSessionImpl implements ServerRxSession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ServerRxSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  //protected boolean stateless = true;
  //protected ServerGxSessionState state = ServerGxSessionState.IDLE;
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IRxMessageFactory factory = null;
  protected transient IServerRxSessionContext context = null;
  protected transient ServerRxSessionListener listener = null;

  protected long[] authAppIds = new long[]{4};
  //protected String originHost, originRealm;
  protected IServerRxSessionData sessionData;

  public ServerRxSessionImpl(IServerRxSessionData sessionData, IRxMessageFactory fct, ISessionFactory sf, ServerRxSessionListener lst,
      IServerRxSessionContext ctx, StateChangeListener<AppSession> stLst)  {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    context = ctx;

    authAppIds = fct.getApplicationIds();
    listener = lst;
    factory = fct;
    this.sessionData = sessionData;
    super.addStateChangeNotification(stLst);
  }

  @Override
  public void sendAAAnswer(RxAAAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    handleEvent(new Event(false, null, answer));
  }

  @Override
  public void sendSessionTermAnswer(RxSessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    handleEvent(new Event(false, null, answer));
  }

  @Override
  public void sendReAuthRequest(RxReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_RAR, request, null);
  }

  @Override
  public void sendAbortSessionRequest(RxAbortSessionRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_ASR, request, null);
  }

  @Override
  public boolean isStateless() {
    return this.sessionData.isStateless();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ServerRxSessionState.class ? (E) this.sessionData.getServerRxSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    ServerRxSessionState newState = null;

    try {
      sendAndStateLock.lock();

      // Can be null if there is no state transition, transition to IDLE state should terminate this app session
      final Event localEvent = (Event) event;
      final ServerRxSessionState state = this.sessionData.getServerRxSessionState();

      //Its kind of awkward, but with two state on server side its easier to go through event types?
      //but for sake of FSM readability
      final Event.Type eventType = (Event.Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case RECEIVE_AAR:
              listener.doAARequest(this, (RxAARequest) localEvent.getRequest());
              break;

            case RECEIVE_EVENT_REQUEST:
              // Current State: IDLE
              // Event: AA event request received and successfully processed
              // Action: Send AA event answer
              // New State: IDLE
              listener.doAARequest(this, (RxAARequest) localEvent.getRequest());
              break;

              // case SEND_EVENT_ANSWER:
              // // Current State: IDLE
              // // Event: AAR event request received and successfully processed
              // // Action: Send AA event answer
              // // New State: IDLE
              //
              // newState = ServerRxSessionState.IDLE;
              // dispatchEvent(localEvent.getAnswer());
              // break;

            case SEND_AAA:
              RxAAAnswer answer = (RxAAAnswer) localEvent.getAnswer();
              try {
                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                // Current State: IDLE
                // Event: AA initial request received and successfully processed
                // Action: Send AAinitial answer
                // New State: OPEN
                if (isSuccess(resultCode)) {
                  newState = ServerRxSessionState.OPEN;
                } // Current State: IDLE
                // Event: AA initial request received but not successfully processed
                // Action: Send AA initial answer with Result-Code != SUCCESS
                // New State: IDLE
                else {
                  newState = ServerRxSessionState.IDLE;
                }
                dispatchEvent(localEvent.getAnswer());
              }
              catch (AvpDataException e) {
                throw new InternalException(e);
              }
              break;
            default:
              throw new InternalException("Wrong state: " + ServerRxSessionState.IDLE + " one event: " + eventType + " " + localEvent.getRequest() + " " +
                  localEvent.getAnswer());
          } //end switch eventType
          break;

        case OPEN:
          switch (eventType) {
            case RECEIVE_AAR:
              listener.doAARequest(this, (RxAARequest) localEvent.getRequest());
              break;

            case SEND_AAA:
              RxAAAnswer answer = (RxAAAnswer) localEvent.getAnswer();
              try {
                if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
                  // Current State: OPEN
                  // Event: AA update request received and successfully processed
                  // Action: Send AA update answer
                  // New State: OPEN
                }
                else {
                  // Current State: OPEN
                  // Event: AA update request received but not successfully processed
                  // Action: Send AA update answer with Result-Code != SUCCESS
                  // New State: IDLE
                  // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                  newState = ServerRxSessionState.IDLE;
                }
              }
              catch (AvpDataException e) {
                throw new InternalException(e);
              }
              dispatchEvent(localEvent.getAnswer());
              break;
            case RECEIVE_STR:
              listener.doSessionTermRequest(this, (RxSessionTermRequest) localEvent.getRequest());
              break;
            case SEND_STA:
              RxSessionTermAnswer STA = (RxSessionTermAnswer) localEvent.getAnswer();
              try {
                if (isSuccess(STA.getResultCodeAvp().getUnsigned32())) {
                  // Current State: OPEN
                  // Event: AA update request received and successfully processed
                  // Action: Send AA update answer
                  // New State: OPEN
                }
                else {
                  // Current State: OPEN
                  // Event: AA update request received but not successfully processed
                  // Action: Send AA update answer with Result-Code != SUCCESS
                  // New State: IDLE
                  // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                  newState = ServerRxSessionState.IDLE;
                }
              }
              catch (AvpDataException e) {
                throw new InternalException(e);
              }
              finally {
                newState = ServerRxSessionState.IDLE;
              }
              dispatchEvent(localEvent.getAnswer());
              break;

            case RECEIVE_RAA:
              listener.doReAuthAnswer(this, (RxReAuthRequest) localEvent.getRequest(), (RxReAuthAnswer) localEvent.getAnswer());
              break;
            case SEND_RAR:
              dispatchEvent(localEvent.getRequest());
              break;
            case RECEIVE_ASA:
              listener.doAbortSessionAnswer(this, (RxAbortSessionRequest) localEvent.getRequest(), (RxAbortSessionAnswer) localEvent.getAnswer());
              break;
            case SEND_ASR:
              dispatchEvent(localEvent.getRequest());
              break;
          } //end switch eventtype
          break;
      }
      return true;
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      if (newState != null) {
        setState(newState);
      }
      sendAndStateLock.unlock();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }

  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    //rd.session = (ServerRxSession) LocalDataSource.INSTANCE.getSession(request.getSessionId());
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }

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
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }

  @Override
  public void timeoutExpired(Request request) {
    //  context.timeoutExpired(request);
    //FIXME: Should we release ?
  }

  protected boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected void setState(ServerRxSessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ServerRxSessionState newState, boolean release) {
    IAppSessionState oldState = this.sessionData.getServerRxSessionState();
    this.sessionData.setServerRxSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, (Enum) oldState, (Enum) newState);
    }
    if (newState == ServerRxSessionState.IDLE) {
      if (release) {
        // NOTE: do EVERYTHING before release.
        this.release();
      }
    }
  }

  @Override
  public void release() {
    if (isValid()) {
      try {
        this.sendAndStateLock.lock();
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

  protected void send(Event.Type type, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException {
    try {
      session.send(event.getMessage(), this);
      // Store last destination information
    }
    catch (Exception e) {
      //throw new InternalException(e);
      logger.debug("Failure trying to dispatch event", e);
    }
  }

  private class RequestDelivery implements Runnable {

    ServerRxSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case RxAARequest.code:
            handleEvent(new Event(true, factory.createAARequest(request), null));
            break;
          case RxSessionTermRequest.code:
            handleEvent(new Event(true, factory.createSessionTermRequest(request), null));
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

    ServerRxSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        // FIXME: baranowb: add message validation here!!!
        // We handle CCR, STR, ACR, ASR other go into extension
        switch (request.getCommandCode()) {
          case RxReAuthRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_RAA, factory.createReAuthRequest(request), factory.createReAuthAnswer(answer)));
            break;
          case RxAbortSessionRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_ASA, factory.createAbortSessionRequest(request), factory.createAbortSessionAnswer(answer)));
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
    ServerRxSessionImpl other = (ServerRxSessionImpl) obj;
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

}
