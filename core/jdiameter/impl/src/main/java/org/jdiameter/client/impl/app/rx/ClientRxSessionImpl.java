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

package org.jdiameter.client.impl.app.rx;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
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
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.ClientRxSessionListener;
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxReAuthAnswer;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.impl.app.rx.Event.Type;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.rx.ClientRxSessionState;
import org.jdiameter.common.api.app.rx.IClientRxSessionContext;
import org.jdiameter.common.api.app.rx.IRxMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.rx.AppRxSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 3GPP IMS Rx Reference Point Client Session implementation
 *
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ClientRxSessionImpl extends AppRxSessionImpl implements ClientRxSession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ClientRxSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  //protected boolean isEventBased = true;
  //protected boolean requestTypeSet = false;
  //protected ClientRxSessionState state = ClientRxSessionState.IDLE;
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IRxMessageFactory factory;
  protected transient ClientRxSessionListener listener;
  protected transient IClientRxSessionContext context;
  protected transient IMessageParser parser;
  protected IClientRxSessionData sessionData;

  // protected String originHost, originRealm;
  protected long[] authAppIds = new long[]{4};
  // Requested Action + Credit-Control and Direct-Debiting Failure-Handling ---
  static final int NON_INITIALIZED = -300;

  // Session State Handling ---------------------------------------------------
  protected boolean isEventBased = false;
  //protected boolean requestTypeSet = false;
  //protected ClientRxSessionState state = ClientRxSessionState.IDLE;

  protected byte[] buffer;

  protected String originHost, originRealm;

  // Error Codes --------------------------------------------------------------
  private static final long INVALID_SERVICE_INFORMATION = 5061L;
  private static final long FILTER_RESTRICTIONS = 5062L;
  private static final long REQUESTED_SERVICE_NOT_AUTHORIZED = 5063L;
  private static final long DUPLICATED_AF_SESSION = 5064L;
  private static final long IP_CAN_SESSION_NOT_AVAILABLE = 5065L;
  private static final long UNAUTHORIZED_NON_EMERGENCY_SESSION = 5066L;
  private static final long UNAUTHORIZED_SPONSORED_DATA_CONNECTIVITY = 5067L;

  private static final long DIAMETER_UNABLE_TO_DELIVER = 3002L;
  private static final long DIAMETER_TOO_BUSY = 3004L;
  private static final long DIAMETER_LOOP_DETECTED = 3005L;
  protected static final Set<Long> temporaryErrorCodes;

  static {
    HashSet<Long> tmp = new HashSet<Long>();
    tmp.add(DIAMETER_UNABLE_TO_DELIVER);
    tmp.add(DIAMETER_TOO_BUSY);
    tmp.add(DIAMETER_LOOP_DETECTED);
    temporaryErrorCodes = Collections.unmodifiableSet(tmp);
  }
  // Session Based Queue
  protected ArrayList<Event> eventQueue = new ArrayList<Event>();

  public ClientRxSessionImpl(IClientRxSessionData sessionData, IRxMessageFactory fct, ISessionFactory sf, ClientRxSessionListener lst,
      IClientRxSessionContext ctx, StateChangeListener<AppSession> stLst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.context = ctx;

    this.authAppIds = fct.getApplicationIds();
    this.listener = lst;
    this.factory = fct;

    IContainer icontainer = sf.getContainer();
    this.parser = icontainer.getAssemblerFacility().getComponentInstance(IMessageParser.class);
    this.sessionData = sessionData;
    super.addStateChangeNotification(stLst);
  }

  @Override
  public void sendAARequest(RxAARequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {
      this.handleEvent(new Event(true, request, null));
    }
    catch (AvpDataException e) {
      throw new InternalException(e);
    }
  }


  @Override
  public void sendSessionTermRequest(RxSessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {
      this.handleEvent(new Event(true, request, null));
    }
    catch (AvpDataException e) {
      throw new InternalException(e);
    }
  }

  @Override
  public void sendReAuthAnswer(RxReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_RAA, null, answer));
  }

  @Override
  public void sendAbortSessionAnswer(RxAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_ASA, null, answer));
  }

  @Override
  public boolean isStateless() {
    return false;
  }

  public boolean isEventBased() {
    return this.isEventBased;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ClientRxSessionState.class ? (E) sessionData.getClientRxSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return this.isEventBased() ? handleEventForEventBased(event) : handleEventForSessionBased(event);
  }

  protected boolean handleEventForEventBased(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      final ClientRxSessionState state = this.sessionData.getClientRxSessionState();
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) localEvent.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case SEND_EVENT_REQUEST:
              // Current State: IDLE
              // Event: Client or device requests a one-time service
              // Action: Send AA event request
              // New State: PENDING_E
              setState(ClientRxSessionState.PENDING_EVENT);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                logger.debug("Failure handling send event request", e);
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_EVENT:
          switch (eventType) {
            case RECEIVE_EVENT_ANSWER:
              AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
              try {
                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                if (isSuccess(resultCode)) {
                  // Current State: PENDING_E
                  // Event: Successful AA event answer received
                  // Action: Grant service to end user
                  // New State: IDLE
                  setState(ClientRxSessionState.IDLE, false);
                }
                if (isProvisional(resultCode) || isFailure(resultCode)) {
                  handleFailureMessage(answer, (AppRequestEvent) localEvent.getRequest(), eventType);
                }

                deliverRxAAAnswer((RxAARequest) localEvent.getRequest(), (RxAAAnswer) localEvent.getAnswer());
              }
              catch (AvpDataException e) {
                logger.debug("Failure handling received answer event", e);
                setState(ClientRxSessionState.IDLE, false);
              }
              break;
            default:
              logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_BUFFERED:
          switch (eventType) {
            case RECEIVE_EVENT_ANSWER:
              // Current State: PENDING_B
              // Event: Successful CC answer received
              // Action: Delete request
              // New State: IDLE
              setState(ClientRxSessionState.IDLE, false);
              //this.sessionData.setBuffer(null);
              buffer = null;
              deliverRxAAAnswer((RxAARequest) localEvent.getRequest(), (RxAAAnswer) localEvent.getAnswer());
              break;
            default:
              logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        default:
          logger.warn("Event Based Handling - Wrong event type ({}) on state {}", eventType, state);
          break;
      }

      dispatch();
      return true;
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected boolean handleEventForSessionBased(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      final ClientRxSessionState state = this.sessionData.getClientRxSessionState();
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case SEND_AAR:
              // Current State: IDLE
              // Event: Client or device requests access/service
              // Action: Send AAR
              // New State: PENDING_AAR
              setState(ClientRxSessionState.PENDING_AAR);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;
        case PENDING_AAR:
          AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
          switch (eventType) {
            case RECEIVE_AAA:
              long resultCode = answer.getResultCodeAvp().getUnsigned32();
              if (isSuccess(resultCode)) {
                // Current State: PENDING_AAR
                // Event: Successful AA answer received
                // New State: OPEN
                setState(ClientRxSessionState.OPEN);
              }
              else if (isProvisional(resultCode) || isFailure(resultCode)) {
                handleFailureMessage(answer, (AppRequestEvent) localEvent.getRequest(), eventType);
              }
              deliverRxAAAnswer((RxAARequest) localEvent.getRequest(), (RxAAAnswer) localEvent.getAnswer());
              break;
            case SEND_AAR:
            case SEND_STR:
              // Current State: PENDING_AAR
              // Event: User service terminated
              // Action: Queue termination event
              // New State: PENDING_AAR

              // Current State: PENDING_AAR
              // Event: Change in request
              // Action: Queue changed rating condition event
              // New State: PENDING_AAR
              eventQueue.add(localEvent);
              break;
            case RECEIVE_RAR:
              deliverReAuthRequest((RxReAuthRequest) localEvent.getRequest());
              break;
            case SEND_RAA:
              // Current State: PENDING_U
              // Event: RAR received
              // Action: Send RAA
              // New State: PENDING_U
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case RECEIVE_ASR:
              deliverAbortSessionRequest((RxAbortSessionRequest) localEvent.getRequest());
              break;
            case SEND_ASA:
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_STR:
          AppAnswerEvent stanswer = (AppAnswerEvent) localEvent.getAnswer();
          switch (eventType) {
            case RECEIVE_STA:
              long resultCode = stanswer.getResultCodeAvp().getUnsigned32();
              if (isSuccess(resultCode)) {
                // Current State: PENDING_STR
                // Event: Successful ST answer received
                // New State: IDLE
                setState(ClientRxSessionState.IDLE, false);
              }
              else if (isProvisional(resultCode) || isFailure(resultCode)) {
                handleFailureMessage(stanswer, (AppRequestEvent) localEvent.getRequest(), eventType);
              }
              deliverRxSessionTermAnswer((RxSessionTermRequest) localEvent.getRequest(), (RxSessionTermAnswer) localEvent.getAnswer());
              break;
            case SEND_AAR:
              try {
                // Current State: PENDING_STR
                // Event: Change in AA request
                // Action: -
                // New State: PENDING_STR
                dispatchEvent(localEvent.getRequest());
                // No transition
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                // handleSendFailure(e, eventType);
              }
              break;
              //                        case RECEIVE_STA:
              //                            // Current State: PENDING_T
              //                            // Event: Successful CC termination answer received
              //                            // Action: -
              //                            // New State: IDLE
              //
              //                            // Current State: PENDING_T
              //                            // Event: Failure to send, temporary error, or failed answer
              //                            // Action: -
              //                            // New State: IDLE
              //
              //                            //FIXME: Alex broke this, setting back "true" ?
              //                            setState(ClientRxSessionState.IDLE, false);
              //                            //setState(ClientRxSessionState.IDLE, true);
              //                            deliverRxSessionTermAnswer((RxSessionTermRequest) localEvent.getRequest(), (RxSessionTermAnswer) localEvent.getAnswer());
              //                            //setState(ClientRxSessionState.IDLE, true);
              //                            break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;
        case OPEN:
          switch (eventType) {
            case SEND_AAR:
              // Current State: OPEN
              // Event: Updated AAR send by AF
              // Action: Send AAR update request
              // New State: PENDING_AAR

              setState(ClientRxSessionState.PENDING_AAR);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case SEND_STR:
              // Current State: OPEN
              // Event: Session Termination event request received to be sent
              // Action: Terminate end user's service, send STR termination request
              // New State: PENDING STR

              setState(ClientRxSessionState.PENDING_STR);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case RECEIVE_RAR:
              deliverReAuthRequest((RxReAuthRequest) localEvent.getRequest());
              break;
            case SEND_RAA:
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case RECEIVE_ASR:
              deliverAbortSessionRequest((RxAbortSessionRequest) localEvent.getRequest());
              break;
            case SEND_ASA:
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Session Based Handling - Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;
        default:
          // any other state is bad
          setState(ClientRxSessionState.IDLE, true);
          break;
      }
      dispatch();
      return true;
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
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
    //        if (request.getCommandCode() == RxAAAnswer.code) {
    //            try {
    //                handleSendFailure(null, null, request);
    //            }
    //            catch (Exception e) {
    //                logger.debug("Failure processing timeout message for request", e);
    //            }
    //        }
  }

  protected void setState(ClientRxSessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ClientRxSessionState newState, boolean release) {
    try {
      IAppSessionState oldState = this.sessionData.getClientRxSessionState();
      this.sessionData.setClientRxSessionState(newState);
      for (StateChangeListener i : stateListeners) {
        i.stateChanged(this, (Enum) oldState, (Enum) newState);
      }

      if (newState == ClientRxSessionState.IDLE) {
        if (release) {
          this.release();
        }

      }
    }
    catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure switching to state " + this.sessionData.getClientRxSessionState() + " (release=" + release + ")", e);
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

  protected void handleSendFailure(Exception e, Event.Type eventType, Message request) throws Exception {
    logger.debug("Failed to send message, type: {} message: {}, failure: {}", new Object[]{eventType, request, e != null ? e.getLocalizedMessage() : ""});
    //try {
    //  setState(ClientRxSessionState.IDLE);
    //}
    //finally {
    //  dispatch();
    //}
  }

  protected void handleFailureMessage(final AppAnswerEvent event, final AppRequestEvent request, final Event.Type eventType) {
    try {
      setState(ClientRxSessionState.IDLE);
    }
    catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure handling failure message for Event " + event + " (" + eventType + ") and Request " + request, e);
      }
    }
  }

  /**
   * This makes checks on queue, moves it to proper state if event there is
   * present on Open state ;]
   */
  protected void dispatch() {
    // Event Based ----------------------------------------------------------
    if (isEventBased()) {
      // Current State: IDLE
      // Event: Request in storage
      // Action: Send stored request
      // New State: PENDING_B
      if (buffer != null) {
        setState(ClientRxSessionState.PENDING_BUFFERED);
        try {
          dispatchEvent(new AppRequestEventImpl(messageFromBuffer(ByteBuffer.wrap(buffer))));
        }
        catch (Exception e) {
          try {
            handleSendFailure(e, Event.Type.SEND_EVENT_REQUEST, messageFromBuffer(ByteBuffer.wrap(buffer)));
          }
          catch (Exception e1) {
            logger.error("Failure handling buffer send failure", e1);
          }
        }
      }
    } // Session Based --------------------------------------------------------
    else {
      if (sessionData.getClientRxSessionState() == ClientRxSessionState.OPEN && eventQueue.size() > 0) {
        try {
          this.handleEvent(eventQueue.remove(0));
        }
        catch (Exception e) {
          logger.error("Failure handling queued event", e);
        }
      }
    }
  }

  protected void deliverRxAAAnswer(RxAARequest request, RxAAAnswer answer) {
    try {
      listener.doAAAnswer(this, request, answer);
    }
    catch (Exception e) {
      logger.warn("Failure delivering AAA", e);
    }
  }

  protected void deliverRxSessionTermAnswer(RxSessionTermRequest request, RxSessionTermAnswer answer) {
    try {
      listener.doSessionTermAnswer(this, request, answer);
    }
    catch (Exception e) {
      logger.warn("Failure delivering STA", e);
    }
  }

  protected void deliverReAuthRequest(RxReAuthRequest request) {
    try {
      listener.doReAuthRequest(this, request);
    }
    catch (Exception e) {
      logger.debug("Failure delivering RAR", e);
    }
  }

  protected void deliverAbortSessionRequest(RxAbortSessionRequest request) {
    try {
      listener.doAbortSessionRequest(this, request);
    }
    catch (Exception e) {
      logger.debug("Failure delivering RAR", e);
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    session.send(event.getMessage(), this);
  }

  protected boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected boolean isFailure(long code) {
    return (!isProvisional(code) && !isSuccess(code) && ((code >= 3000 && code < 6000)) && !temporaryErrorCodes.contains(code));
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#relink(org.jdiameter.client.api.IContainer)
   */
  private Message messageFromBuffer(ByteBuffer request) throws InternalException {
    if (request != null) {
      Message m;
      try {
        m = parser.createMessage(request);
        return m;
      }
      catch (AvpDataException e) {
        throw new InternalException("Failed to decode message.", e);
      }
    }
    return null;
  }

  private ByteBuffer messageToBuffer(IMessage msg) throws InternalException {
    try {
      return parser.encodeMessage(msg);
    }
    catch (ParseException e) {
      throw new InternalException("Failed to encode message.", e);
    }
  }

  private class RequestDelivery implements Runnable {
    ClientRxSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case RxReAuthRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_RAR, factory.createReAuthRequest(request), null));
            break;
          case RxAbortSessionRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_ASR, factory.createAbortSessionRequest(request), null));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failure processing request", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {

    ClientRxSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case RxAAAnswer.code:
            final RxAARequest myAARequest = factory.createAARequest(request);
            final RxAAAnswer myAAAnswer = factory.createAAAnswer(answer);
            handleEvent(new Event(false, myAARequest, myAAAnswer));
            break;
          case RxSessionTermAnswer.code:
            final RxSessionTermRequest mySTRequest = factory.createSessionTermRequest(request);
            final RxSessionTermAnswer mySTAnswer = factory.createSessionTermAnswer(answer);
            handleEvent(new Event(false, mySTRequest, mySTAnswer));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failure processing success message", e);
      }
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(authAppIds);
    result = prime * result + (isEventBased ? 1231 : 1237);
    result = prime * result + ((originHost == null) ? 0 : originHost.hashCode());
    result = prime * result + ((originRealm == null) ? 0 : originRealm.hashCode());
    result = prime * result + ((sessionData == null) ? 0 : (sessionData.getClientRxSessionState() == null ? 0 :
      sessionData.getClientRxSessionState().hashCode()));
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

    ClientRxSessionImpl other = (ClientRxSessionImpl) obj;
    if (!Arrays.equals(authAppIds, other.authAppIds)) {
      return false;
    }
    if (isEventBased != other.isEventBased) {
      return false;
    }
    if (originHost == null) {
      if (other.originHost != null) {
        return false;
      }
    }
    else if (!originHost.equals(other.originHost)) {
      return false;
    }
    if (originRealm == null) {
      if (other.originRealm != null) {
        return false;
      }
    }
    else if (!originRealm.equals(other.originRealm)) {
      return false;
    }
    if (sessionData == null) {
      if (other.sessionData != null) {
        return false;
      }
    }
    else if (sessionData.getClientRxSessionState() == null) {
      if (other.sessionData.getClientRxSessionState() != null) {
        return false;
      }
    }
    else if (!sessionData.getClientRxSessionState().equals(other.sessionData.getClientRxSessionState())) {
      return false;
    }


    return true;
  }

  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }
}