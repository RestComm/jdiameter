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

package org.jdiameter.client.impl.app.ro;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.NoMorePeersAvailableException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.ro.ClientRoSession;
import org.jdiameter.api.ro.ClientRoSessionListener;
import org.jdiameter.api.ro.events.RoCreditControlAnswer;
import org.jdiameter.api.ro.events.RoCreditControlRequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.app.ro.Event.Type;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.ro.ClientRoSessionState;
import org.jdiameter.common.api.app.ro.IClientRoSessionContext;
import org.jdiameter.common.api.app.ro.IRoMessageFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.ro.AppRoSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jdiameter.client.impl.helpers.Parameters.RetransmissionRequiredResCodes;
import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.TxTimeOut;

/**
 * Client Credit-Control Application session implementation
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:grzegorz.figiel@pro-ids.com"> Grzegorz Figiel (ProIDS sp. z o.o.)</a>
 */
public class ClientRoSessionImpl extends AppRoSessionImpl implements ClientRoSession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ClientRoSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected IClientRoSessionData sessionData;
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IRoMessageFactory factory;
  protected transient ClientRoSessionListener listener;
  protected transient IClientRoSessionContext context;
  protected transient IMessageParser parser;
  protected transient IRouter router;

  // Tx Timer -----------------------------------------------------------------
  protected static final String TX_TIMER_NAME = "Ro_CLIENT_TX_TIMER";
  protected final long txTimerVal;

  // Response Timer -----------------------------------------------------------
  protected static final String RETRANSMISSION_TIMER_NAME = "Ro_CLIENT_RETRANSMISSION_TIMER";
  protected final long retransmissionTimerVal;

  protected long[] authAppIds = new long[]{4};
  protected final Set<Long> retrRequiredErrorCodes;

  // Requested Action + Credit-Control and Direct-Debiting Failure-Handling ---
  protected static final int CCFH_TERMINATE = 0;
  protected static final int CCFH_CONTINUE = 1;
  protected static final int CCFH_RETRY_AND_TERMINATE = 2;

  private static final int DDFH_TERMINATE_OR_BUFFER = 0;
  private static final int DDFH_CONTINUE = 1;

  // Requested-Action Values --------------------------------------------------
  private static final int DIRECT_DEBITING = 0;
  private static final int REFUND_ACCOUNT = 1;
  private static final int CHECK_BALANCE = 2;
  private static final int PRICE_ENQUIRY = 3;

  // CC-Request-Type  ---------------------------------------------------------
  private static final int EVENT_REQUEST = 4;

  // Error Codes --------------------------------------------------------------
  private static final long END_USER_SERVICE_DENIED = 4010;
  private static final long CREDIT_CONTROL_NOT_APPLICABLE = 4011;
  private static final long USER_UNKNOWN = 5030;

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

  public ClientRoSessionImpl(IClientRoSessionData sessionData, IRoMessageFactory fct, ISessionDatasource sds, ISessionFactory sf, ClientRoSessionListener
      lst, IClientRoSessionContext ctx, StateChangeListener<AppSession> stLst) {
    super(sds, sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    if (sessionData == null) {
      throw new IllegalArgumentException("SessionData can not be null");
    }
    this.context = ctx;
    this.sessionData = sessionData;
    this.authAppIds = fct.getApplicationIds();
    this.listener = lst;
    this.factory = fct;

    IContainer icontainer = sf.getContainer();

    this.parser = icontainer.getAssemblerFacility().getComponentInstance(IMessageParser.class);
    this.router = icontainer.getAssemblerFacility().getComponentInstance(IRouter.class);
    this.txTimerVal = icontainer.getConfiguration().getLongValue(TxTimeOut.ordinal(), (Long) TxTimeOut.defValue());
    this.retransmissionTimerVal = icontainer.getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());

    Set<Long> tmpErrCodes = new HashSet<>();
    for (int val : icontainer.getConfiguration().getIntArrayValue(RetransmissionRequiredResCodes.ordinal(), new int[0])) {
      tmpErrCodes.add(new Long(val));
    }
    this.retrRequiredErrorCodes = Collections.unmodifiableSet(tmpErrCodes);

    super.addStateChangeNotification(stLst);
  }

  protected int getLocalCCFH() {
    return sessionData.getGatheredCCFH() >= 0 ? sessionData.getGatheredCCFH() : context.getDefaultCCFHValue();
  }

  protected int getLocalDDFH() {
    return sessionData.getGatheredDDFH() >= 0 ? sessionData.getGatheredDDFH() : context.getDefaultDDFHValue();
  }

  protected boolean isSessionFailoverSupported() {
    return sessionData.getGatheredCCSF() > 0;
  }


  public void sendCreditControlRequest(RoCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException,
      OverloadException {
    try {
      extractFHAVPs(request, null);
      this.handleEvent(new Event(true, request, null));
    }
    catch (AvpDataException e) {
      throw new InternalException(e);
    }
  }

  @Override
  public void sendReAuthAnswer(ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    this.handleEvent(new Event(Event.Type.SEND_RAA, null, answer));
  }

  @Override
  public boolean isStateless() {
    return false;
  }

  public boolean isEventBased() {
    return sessionData.isEventBased();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ClientRoSessionState.class ? (E) sessionData.getClientRoSessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return this.isEventBased() ? handleEventForEventBased(event) : handleEventForSessionBased(event);
  }

  protected boolean handleEventForEventBased(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      ClientRoSessionState state = sessionData.getClientRoSessionState();
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) localEvent.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case SEND_EVENT_REQUEST:
              // Current State: IDLE
              // Event: Client or device requests a one-time service
              // Action: Send CC event request, start Tx
              // New State: PENDING_E
              startTx(localEvent.getRequest().getMessage());
              setState(ClientRoSessionState.PENDING_EVENT);
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
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
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
                  // Event: Successful CC event answer received
                  // Action: Grant service to end user
                  // New State: IDLE
                  setState(ClientRoSessionState.IDLE, false);
                }
                if (isProvisional(resultCode) || isFailure(resultCode)) {
                  handleFailureMessage((RoCreditControlAnswer) answer, (RoCreditControlRequest) localEvent.getRequest(), eventType);
                }

                deliverRoAnswer((RoCreditControlRequest) localEvent.getRequest(), (RoCreditControlAnswer) localEvent.getAnswer());
              }
              catch (AvpDataException e) {
                logger.debug("Failure handling received answer event", e);
                setState(ClientRoSessionState.IDLE, false);
              }
              break;
            case Tx_TIMER_FIRED:
              deliverRequestTxTimeout(localEvent.getRequest().getMessage());
              handleTxExpires(localEvent.getRequest().getMessage());
              break;
            default:
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
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
              setState(ClientRoSessionState.IDLE, false);
              sessionData.setBuffer(null);
              deliverRoAnswer((RoCreditControlRequest) localEvent.getRequest(), (RoCreditControlAnswer) localEvent.getAnswer());
              break;
            default:
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        default:
          logger.warn("Wrong event type ({}) on state {}", eventType, state);
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
      ClientRoSessionState state = sessionData.getClientRoSessionState();
      Event localEvent = (Event) event;
      Event.Type eventType = (Type) localEvent.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case SEND_INITIAL_REQUEST:
              // Current State: IDLE
              // Event: Client or device requests access/service
              // Action: Send CC initial request, start Tx
              // New State: PENDING_I
              startTx(localEvent.getRequest().getMessage());
              setState(ClientRoSessionState.PENDING_INITIAL);
              // RFC 4006: For new credit-control sessions, failover to an alternative
              // credit-control server SHOULD be performed if possible.
              sessionData.setGatheredCCSF(IMessage.SESSION_FAILOVER_SUPPORTED_VALUE);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (NoMorePeersAvailableException nmpae) {
                handlePeerUnavailability(localEvent.getRequest().getMessage(), nmpae);
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            default:
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_INITIAL:
          AppAnswerEvent answer = (AppAnswerEvent) localEvent.getAnswer();
          switch (eventType) {
            case RECEIVED_INITIAL_ANSWER:

              Message message = localEvent.getAnswer().getMessage();
              int ccSessionFailover = IMessage.SESSION_FAILOVER_NOT_SUPPORTED_VALUE;

              Avp avpCcSessionFailover = message.getAvps().getAvp(Avp.CC_SESSION_FAILOVER);
              if (avpCcSessionFailover != null) {
                ccSessionFailover = avpCcSessionFailover.getInteger32();
              }
              else {
                logger.warn("Failed to fetch CC-Session-Failover");
              }

              sessionData.setGatheredCCSF(ccSessionFailover);
              long resultCode = answer.getResultCodeAvp().getUnsigned32();
              if (isSuccess(resultCode)) {
                // Current State: PENDING_I
                // Event: Successful CC initial answer received
                // Action: Stop Tx
                // New State: OPEN
                stopTx();
                setState(ClientRoSessionState.OPEN);
                //Session persistence record shall be created after a peer had answered
                //the first (initial) request for that session
                if (sf.isSessionPersistenceEnabled()) {
                  initSessionPersistenceContext(localEvent.getRequest(), localEvent.getAnswer());
                  startIdleSessionTimer();
                }
              }
              else if (retrRequiredErrorCodes.contains(resultCode)) {
                handleRetransmissionDueToError(eventType, (IMessage) localEvent.getRequest().getMessage());
                break;
              }
              else if (isProvisional(resultCode) || isFailure(resultCode)) {
                handleFailureMessage((RoCreditControlAnswer) answer, (RoCreditControlRequest) localEvent.getRequest(), eventType);
              }

              deliverRoAnswer((RoCreditControlRequest) localEvent.getRequest(), (RoCreditControlAnswer) localEvent.getAnswer());
              break;
            case Tx_TIMER_FIRED:
              deliverRequestTxTimeout(localEvent.getRequest().getMessage());
              if (isRetransmissionRequired()) {
                handleRetransmissionDueToTimeout(eventType, localEvent.getRequest());
              }
              else {
                handleRetransmissionFailure((RoCreditControlRequest) localEvent.getRequest());
              }
              break;
            case RETRANSMISSION_TIMER_FIRED:
              handleRetransmissionFailure((RoCreditControlRequest) localEvent.getRequest());
              break;
            case SEND_UPDATE_REQUEST:
            case SEND_TERMINATE_REQUEST:
              // Current State: PENDING_I
              // Event: User service terminated
              // Action: Queue termination event
              // New State: PENDING_I

              // Current State: PENDING_I
              // Event: Change in rating condition
              // Action: Queue changed rating condition event
              // New State: PENDING_I
              eventQueue.add(localEvent);
              break;
            default:
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case OPEN:
          switch (eventType) {
            case SEND_UPDATE_REQUEST:
              // Current State: OPEN
              // Event: Granted unit elapses and no final unit indication received
              // Action: Send CC update request, start Tx
              // New State: PENDING_U

              // Current State: OPEN
              // Event: Change in rating condition in queue
              // Action: Send CC update request, start Tx
              // New State: PENDING_U

              // Current State: OPEN
              // Event: Change in rating condition or Validity-Time elapses
              // Action: Send CC update request, start Tx
              // New State: PENDING_U

              // Current State: OPEN
              // Event: RAR received
              // Action: Send RAA followed by CC update request, start Tx
              // New State: PENDING_U
              startTx(localEvent.getRequest().getMessage());
              if (sf.isSessionPersistenceEnabled()) {
                startIdleSessionTimer();
              }
              setState(ClientRoSessionState.PENDING_UPDATE);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (NoMorePeersAvailableException nmpae) {
                handlePeerUnavailability(localEvent.getRequest().getMessage(), nmpae);
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case SEND_TERMINATE_REQUEST:
              // Current State: OPEN
              // Event: Granted unit elapses and final unit action equal to TERMINATE received
              // Action: Terminate end user�s service, send CC termination request
              // New State: PENDING_T

              // Current State: OPEN
              // Event: Service terminated in queue
              // Action: Send CC termination request
              // New State: PENDING_T

              // Current State: OPEN
              // Event: User service terminated
              // Action: Send CC termination request
              // New State: PENDING_T

              // In all cases start Tx in order to assure failover
              startTx(localEvent.getRequest().getMessage());
              if (sf.isSessionPersistenceEnabled()) {
                stopIdleSessionTimerTimer();
              }
              setState(ClientRoSessionState.PENDING_TERMINATION);
              try {
                dispatchEvent(localEvent.getRequest());
              }
              catch (NoMorePeersAvailableException nmpae) {
                handlePeerUnavailability(localEvent.getRequest().getMessage(), nmpae);
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;
            case RECEIVED_RAR:
              deliverRAR((ReAuthRequest) localEvent.getRequest());
              break;
            case SEND_RAA:
              try {
                dispatchEvent(localEvent.getAnswer());
              }
              catch (Exception e) {
                handleSendFailure(e, eventType, localEvent.getRequest().getMessage());
              }
              break;

            default:
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        case PENDING_UPDATE:
          answer = (AppAnswerEvent) localEvent.getAnswer();
          switch (eventType) {
            case RECEIVED_UPDATE_ANSWER:

              Message message = localEvent.getAnswer().getMessage();
              int ccSessionFailover = IMessage.SESSION_FAILOVER_NOT_SUPPORTED_VALUE;

              Avp avpCcSessionFailover = message.getAvps().getAvp(Avp.CC_SESSION_FAILOVER);
              if (avpCcSessionFailover != null) {
                ccSessionFailover = avpCcSessionFailover.getInteger32();
              }
              else {
                logger.warn("Failed to fetch CC-Session-Failover");
              }

              sessionData.setGatheredCCSF(ccSessionFailover);
              long resultCode = answer.getResultCodeAvp().getUnsigned32();
              if (isSuccess(resultCode)) {
                // Current State: PENDING_U
                // Event: Successful CC update answer received
                // Action: Stop Tx
                // New State: OPEN
                stopTx();
                setState(ClientRoSessionState.OPEN);
              }
              else if (retrRequiredErrorCodes.contains(resultCode)) {
                handleRetransmissionDueToError(eventType, (IMessage) localEvent.getRequest().getMessage());
                break;
              }
              else if (isProvisional(resultCode) || isFailure(resultCode)) {
                handleFailureMessage((RoCreditControlAnswer) answer, (RoCreditControlRequest) localEvent.getRequest(), eventType);
              }

              deliverRoAnswer((RoCreditControlRequest) localEvent.getRequest(), (RoCreditControlAnswer) localEvent.getAnswer());
              break;
            case Tx_TIMER_FIRED:
              deliverRequestTxTimeout(localEvent.getRequest().getMessage());
              if (isRetransmissionRequired()) {
                handleRetransmissionDueToTimeout(eventType, localEvent.getRequest());
              }
              else {
                handleRetransmissionFailure((RoCreditControlRequest) localEvent.getRequest());
              }
              break;
            case RETRANSMISSION_TIMER_FIRED:
              handleRetransmissionFailure((RoCreditControlRequest) localEvent.getRequest());
              break;
            case SEND_UPDATE_REQUEST:
            case SEND_TERMINATE_REQUEST:
              // Current State: PENDING_U
              // Event: User service terminated
              // Action: Queue termination event
              // New State: PENDING_U

              // Current State: PENDING_U
              // Event: Change in rating condition
              // Action: Queue changed rating condition event
              // New State: PENDING_U
              eventQueue.add(localEvent);
              break;
            case RECEIVED_RAR:
              deliverRAR((ReAuthRequest) localEvent.getRequest());
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
          }

          break;

        case PENDING_TERMINATION:
          switch (eventType) {
            case SEND_UPDATE_REQUEST:
              try {
                // Current State: PENDING_T
                // Event: Change in rating condition
                // Action: -
                // New State: PENDING_T
                dispatchEvent(localEvent.getRequest());
                // No transition
              }
              catch (Exception e) {
                // This handles failure to send in PendingI state in FSM table
                // handleSendFailure(e, eventType);
              }
              break;
            case RECEIVED_TERMINATED_ANSWER:
              // Current State: PENDING_T
              // Event: Successful CC termination answer received
              // Action: -
              // New State: IDLE

              // Current State: PENDING_T
              // Event: Failure to send, temporary error, or failed answer
              // Action: -
              // New State: IDLE

              //FIXME: Alex broke this, setting back "true" ?
              //setState(ClientRoSessionState.IDLE, false);
              Message message = localEvent.getAnswer().getMessage();
              int ccSessionFailover = IMessage.SESSION_FAILOVER_NOT_SUPPORTED_VALUE;

              Avp avpCcSessionFailover = message.getAvps().getAvp(Avp.CC_SESSION_FAILOVER);
              if (avpCcSessionFailover != null) {
                ccSessionFailover = avpCcSessionFailover.getInteger32();
              }
              else {
                logger.warn("Failed to fetch CC-Session-Failover");
              }

              sessionData.setGatheredCCSF(ccSessionFailover);
              long resultCode = ((AppAnswerEvent) localEvent.getAnswer()).getResultCodeAvp().getUnsigned32();
              if (retrRequiredErrorCodes.contains(resultCode)) {
                handleRetransmissionDueToError(eventType, (IMessage) localEvent.getRequest().getMessage());
                break;
              }

              deliverRoAnswer((RoCreditControlRequest) localEvent.getRequest(), (RoCreditControlAnswer) localEvent.getAnswer());
              setState(ClientRoSessionState.IDLE, true);
              break;
            case Tx_TIMER_FIRED:
              deliverRequestTxTimeout(localEvent.getRequest().getMessage());
              if (isRetransmissionRequired()) {
                handleRetransmissionDueToTimeout(eventType, localEvent.getRequest());
              }
              else {
                handleRetransmissionFailure((RoCreditControlRequest) localEvent.getRequest());
              }
              break;
            case RETRANSMISSION_TIMER_FIRED:
              handleRetransmissionFailure((RoCreditControlRequest) localEvent.getRequest());
              break;
            default:
              logger.warn("Wrong event type ({}) on state {}", eventType, state);
              break;
          }
          break;

        default:
          // any other state is bad
          setState(ClientRoSessionState.IDLE, true);
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
    if (request.getCommandCode() == RoCreditControlAnswer.code) {
      try {
        sendAndStateLock.lock();
        stopTx();
        handleSendFailure(null, null, request);
      }
      catch (Exception e) {
        logger.debug("Failure processing timeout message for request", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

  protected void startTx(Message message) {
    stopTx(false);
    logger.debug("Scheduling Tx timer in [{}] ms", this.txTimerVal);
    try {
      sessionData.setTxTimerRequest((Request) message);
      sessionData.setTxTimerId(this.timerFacility.schedule(this.sessionData.getSessionId(), TX_TIMER_NAME, this.txTimerVal));
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to store request.", e);
    }
  }

  protected void stopTx(boolean stopDependant) {
    Serializable txTimerId = this.sessionData.getTxTimerId();
    if (txTimerId != null) {
      if (stopDependant) {
        stopFailoverStopTimer();
      }
      logger.debug("Stopping Tx timer [{}]", txTimerId);
      timerFacility.cancel(txTimerId);
      sessionData.setTxTimerId(null);
      sessionData.setTxTimerRequest(null);
    }
  }

  protected void stopTx() {
    stopTx(true);
  }

  protected void startFailoverStopTimer() {
    long timerVal = this.retransmissionTimerVal - this.txTimerVal;
    if (timerVal <= 0) {
      logger.warn("Value of Tx timer cannot exceed failover stop timer: [{}] vs. [{}] (taking default values)", this.txTimerVal, this.retransmissionTimerVal);
      timerVal = this.txTimerVal + (((Long) MessageTimeOut.defValue()) - ((Long) TxTimeOut.defValue()));
    }
    logger.debug("Scheduling failover stop timer in [{}] ms", timerVal);
    stopFailoverStopTimer();
    sessionData.setRetransmissionTimerId(this.timerFacility.schedule(this.sessionData.getSessionId(), RETRANSMISSION_TIMER_NAME, timerVal));
  }

  protected void stopFailoverStopTimer() {
    Serializable failoverStopTimerId = this.sessionData.getRetransmissionTimerId();
    if (failoverStopTimerId != null) {
      logger.debug("Stopping failover stop timer [{}]", failoverStopTimerId);
      timerFacility.cancel(failoverStopTimerId);
      sessionData.setRetransmissionTimerId(null);
    }
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(TX_TIMER_NAME)) {
      new TxTimerTask(this, sessionData.getTxTimerRequest()).run();
    }
    else if (timerName.equals(RETRANSMISSION_TIMER_NAME)) {
      new RetransmissionTimerTask(this, sessionData.getTxTimerRequest()).run();
    }
    else {
      try {
        sendAndStateLock.lock();
        super.onTimer(timerName);
      }
      catch (Exception ex) {
        logger.error("Cannot properly handle timer expiry", ex);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

  protected void setState(ClientRoSessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ClientRoSessionState newState, boolean release) {
    try {
      IAppSessionState state = sessionData.getClientRoSessionState();
      IAppSessionState oldState = state;
      sessionData.setClientRoSessionState(newState);
      for (StateChangeListener i : stateListeners) {
        i.stateChanged(this, (Enum) oldState, newState);
      }

      if (newState == ClientRoSessionState.IDLE) {
        if (release) {
          this.release();
        }
        stopTx();

        if (sf.isSessionPersistenceEnabled()) {
          stopIdleSessionTimerTimer();
          if (!release) {
            String oldPeer = flushSessionPersistenceContext();
            logger.debug("Session state reset, routing context for peer [{}] was removed from session [{}]", oldPeer, this.getSessionId());
          }
        }
      }
    }
    catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure switching to state " + sessionData.getClientRoSessionState() + " (release=" + release + ")", e);
      }
    }
  }

  @Override
  public void release() {
    if (isValid()) {
      try {
        this.sendAndStateLock.lock();
        this.stopTx();
        super.release();
      }
      catch (Exception e) {
        logger.debug("Failed to release session", e);
      }
      finally {
        this.sendAndStateLock.unlock();
      }
    }
    else {
      logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
    }
  }

  protected void handleSendFailure(Exception e, Event.Type eventType, Message request) throws Exception {
    logger.warn("Failed to send message", e);
    if (logger.isDebugEnabled()) {
      logger.debug("Failed to send message, type: {} message: {}, failure: {}", new Object[]{eventType, request, e != null ? e.getLocalizedMessage() : ""});
    }
    try {
      ClientRoSessionState state = sessionData.getClientRoSessionState();
      // Event Based ----------------------------------------------------------
      if (isEventBased()) {
        int gatheredRequestedAction = sessionData.getGatheredRequestedAction();
        switch (state) {
          case PENDING_EVENT:
            if (gatheredRequestedAction == CHECK_BALANCE || gatheredRequestedAction == PRICE_ENQUIRY) {
              // Current State: PENDING_E
              // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action CHECK_BALANCE or PRICE_ENQUIRY
              // Action: Indicate service error
              // New State: IDLE
              setState(ClientRoSessionState.IDLE);
              context.indicateServiceError(this);
            }
            else if (gatheredRequestedAction == DIRECT_DEBITING) {
              switch (getLocalDDFH()) {
                case DDFH_TERMINATE_OR_BUFFER:
                  // Current State: PENDING_E
                  // Event: Failure to send; request action DIRECT_DEBITING; DDFH equal to TERMINATE_OR_BUFFER
                  // Action: Store request with T-flag
                  // New State: IDLE
                  request.setReTransmitted(true);
                  sessionData.setBuffer((Request) request);

                  setState(ClientRoSessionState.IDLE, false);
                  break;
                case DDFH_CONTINUE:
                  // Current State: PENDING_E
                  // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action DIRECT_DEBITING; DDFH equal to CONTINUE
                  // Action: Grant service to end user
                  // New State: IDLE
                  context.grantAccessOnDeliverFailure(this, request);
                  break;
                default:
                  logger.warn("Invalid Direct-Debiting-Failure-Handling AVP value {}", getLocalDDFH());
              }
            }
            else if (gatheredRequestedAction == REFUND_ACCOUNT) {
              // Current State: PENDING_E
              // Event: Failure to send or Tx expired; requested action REFUND_ACCOUNT
              // Action: Store request with T-flag
              // New State: IDLE
              setState(ClientRoSessionState.IDLE, false);
              request.setReTransmitted(true);
              sessionData.setBuffer((Request) request);
            }
            else {
              logger.warn("Invalid Requested-Action AVP value {}", gatheredRequestedAction);
            }
            break;
          case PENDING_BUFFERED:
            // Current State: PENDING_B
            // Event: Failure to send or temporary error
            // Action: -
            // New State: IDLE
            setState(ClientRoSessionState.IDLE, false);
            sessionData.setBuffer(null);// FIXME: Action does not mention, but ...
            break;
          default:
            logger.warn("Wrong event type ({}) on state {}", eventType, state);
            break;
        }
      }
      // Session Based --------------------------------------------------------
      else {
        switch (getLocalCCFH()) {
          case CCFH_CONTINUE:
            // Current State: PENDING_I
            // Event: Failure to send, or temporary error and CCFH equal to CONTINUE
            // Action: Grant service to end user
            // New State: IDLE

            // Current State: PENDING_U
            // Event: Failure to send, or temporary error and CCFH equal to CONTINUE
            // Action: Grant service to end user
            // New State: IDLE
            setState(ClientRoSessionState.IDLE, false);
            this.context.grantAccessOnDeliverFailure(this, request);
            break;

          default:
            // Current State: PENDING_I
            // Event: Failure to send, or temporary error and CCFH equal to TERMINATE or to RETRY_AND_TERMINATE
            // Action: Terminate end user�s service
            // New State: IDLE

            // Current State: PENDING_U
            // Event: Failure to send, or temporary error and CCFH equal to TERMINATE or to RETRY_AND_TERMINATE
            // Action: Terminate end user�s service
            // New State: IDLE
            this.context.denyAccessOnDeliverFailure(this, request);
            setState(ClientRoSessionState.IDLE, true);
            break;
        }
      }
    }
    finally {
      dispatch();
    }
  }

  protected void handleFailureMessage(RoCreditControlAnswer event, RoCreditControlRequest request, Event.Type eventType) {
    try {
      // Event Based ----------------------------------------------------------
      long resultCode = event.getResultCodeAvp().getUnsigned32();
      ClientRoSessionState state = sessionData.getClientRoSessionState();
      Serializable txTimerId = sessionData.getTxTimerId();
      if (isEventBased()) {
        int gatheredRequestedAction = sessionData.getGatheredRequestedAction();
        switch (state) {
          case PENDING_EVENT:
            if (resultCode == END_USER_SERVICE_DENIED || resultCode == USER_UNKNOWN) {
              if (txTimerId != null) {
                // Current State: PENDING_E
                // Event: CC event answer received with result code END_USER_SERVICE_DENIED or USER_UNKNOWN and Tx running
                // Action: Terminate end user�s service
                // New State: IDLE
                context.denyAccessOnFailureMessage(this);
                deliverRoAnswer(request, event);
                setState(ClientRoSessionState.IDLE);
              }
              else if (gatheredRequestedAction == DIRECT_DEBITING && txTimerId == null) {
                // Current State: PENDING_E
                // Event: Failed answer or answer received w/ result code END_USER_SERVICE DENIED or USER_UNKNOWN; requested action DIRECT_DEBITING; Tx expired
                // Action: -
                // New State: IDLE
                setState(ClientRoSessionState.IDLE);
              }
            }
            else if (resultCode == CREDIT_CONTROL_NOT_APPLICABLE && gatheredRequestedAction == DIRECT_DEBITING) {
              // Current State: PENDING_E
              // Event: CC event answer received with result code CREDIT_CONTROL_NOT_APPLICABLE; requested action DIRECT_DEBITING
              // Action: Grant service to end user
              // New State: IDLE
              context.grantAccessOnFailureMessage(this);
              deliverRoAnswer(request, event);
              setState(ClientRoSessionState.IDLE);
            }
            else if (temporaryErrorCodes.contains(resultCode)) {
              if (gatheredRequestedAction == CHECK_BALANCE || gatheredRequestedAction == PRICE_ENQUIRY) {
                // Current State: PENDING_E
                // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action CHECK_BALANCE or PRICE_ENQUIRY
                // Action: Indicate service error
                // New State: IDLE
                context.indicateServiceError(this);
                deliverRoAnswer(request, event);
                setState(ClientRoSessionState.IDLE);
              }
              else if (gatheredRequestedAction == DIRECT_DEBITING) {
                if (getLocalDDFH() == DDFH_CONTINUE) {
                  // Current State: PENDING_E
                  // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action DIRECT_DEBITING; DDFH equal to CONTINUE
                  // Action: Grant service to end user
                  // New State: IDLE
                  context.grantAccessOnFailureMessage(this);
                  deliverRoAnswer(request, event);
                  setState(ClientRoSessionState.IDLE);
                }
                else if (getLocalDDFH() == DDFH_TERMINATE_OR_BUFFER && txTimerId != null) {
                  // Current State: PENDING_E
                  // Event: Failed CC event answer received or temporary error; requested action DIRECT_DEBITING; DDFH equal to TERMINATE_OR_BUFFER and Tx running
                  // Action: Terminate end user�s service
                  // New State: IDLE
                  context.denyAccessOnFailureMessage(this);
                  deliverRoAnswer(request, event);
                  setState(ClientRoSessionState.IDLE);
                }
              }
              else if (gatheredRequestedAction == REFUND_ACCOUNT) {
                // Current State: PENDING_E
                // Event: Temporary error, and requested action REFUND_ACCOUNT
                // Action: Store request
                // New State: IDLE
                sessionData.setBuffer((Request) request);
                setState(ClientRoSessionState.IDLE, false);
              }
              else {
                logger.warn("Invalid combination for Ro Client FSM: State {}, Result-Code {}, Requested-Action {}, DDFH {}, Tx {}",
                    new Object[]{state, resultCode, gatheredRequestedAction, getLocalDDFH(), txTimerId});
              }
            }
            else { // Failure
              if (gatheredRequestedAction == CHECK_BALANCE || gatheredRequestedAction == PRICE_ENQUIRY) {
                // Current State: PENDING_E
                // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action CHECK_BALANCE or PRICE_ENQUIRY
                // Action: Indicate service error
                // New State: IDLE
                context.indicateServiceError(this);
                deliverRoAnswer(request, event);
                setState(ClientRoSessionState.IDLE);
              }
              else if (gatheredRequestedAction == DIRECT_DEBITING) {
                if (getLocalDDFH() == DDFH_CONTINUE) {
                  // Current State: PENDING_E
                  // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action DIRECT_DEBITING; DDFH equal to CONTINUE
                  // Action: Grant service to end user
                  // New State: IDLE
                  context.grantAccessOnFailureMessage(this);
                  deliverRoAnswer(request, event);
                  setState(ClientRoSessionState.IDLE);
                }
                else if (getLocalDDFH() == DDFH_TERMINATE_OR_BUFFER && txTimerId != null) {
                  // Current State: PENDING_E
                  // Event: Failed CC event answer received or temporary error; requested action DIRECT_DEBITING; DDFH equal to TERMINATE_OR_BUFFER and Tx running
                  // Action: Terminate end user�s service
                  // New State: IDLE
                  context.denyAccessOnFailureMessage(this);
                  deliverRoAnswer(request, event);
                  setState(ClientRoSessionState.IDLE);
                }
              }
              else if (gatheredRequestedAction == REFUND_ACCOUNT) {
                // Current State: PENDING_E
                // Event: Failed CC event answer received; requested action REFUND_ACCOUNT
                // Action: Indicate service error and delete request
                // New State: IDLE
                sessionData.setBuffer(null);
                context.indicateServiceError(this);
                deliverRoAnswer(request, event);
                setState(ClientRoSessionState.IDLE);
              }
              else {
                logger.warn("Invalid combination for Ro Client FSM: State {}, Result-Code {}, Requested-Action {}, DDFH {}, Tx {}",
                    new Object[]{state, resultCode, gatheredRequestedAction, getLocalDDFH(), txTimerId});
              }
            }
            break;

          case PENDING_BUFFERED:
            // Current State: PENDING_B
            // Event: Failed CC answer received
            // Action: Delete request
            // New State: IDLE
            sessionData.setBuffer(null);
            setState(ClientRoSessionState.IDLE, false);
            break;
          default:
            logger.warn("Wrong event type ({}) on state {}", eventType, state);
        }
      }
      // Session Based --------------------------------------------------------
      else {
        switch (state) {
          case PENDING_INITIAL:
            if (resultCode == CREDIT_CONTROL_NOT_APPLICABLE) {
              // Current State: PENDING_I
              // Event: CC initial answer received with result code equal to CREDIT_CONTROL_NOT_APPLICABLE
              // Action: Grant service to end user
              // New State: IDLE
              context.grantAccessOnFailureMessage(this);
              setState(ClientRoSessionState.IDLE, false);
            }
            else if ((resultCode == END_USER_SERVICE_DENIED) || (resultCode == USER_UNKNOWN)) {
              // Current State: PENDING_I
              // Event: CC initial answer received with result code END_USER_SERVICE_DENIED or USER_UNKNOWN
              // Action: Terminate end user�s service
              // New State: IDLE
              context.denyAccessOnFailureMessage(this);
              setState(ClientRoSessionState.IDLE, false);
            }
            else {
              // Temporary errors and others
              switch (getLocalCCFH()) {
                case CCFH_CONTINUE:
                  // Current State: PENDING_I
                  // Event: Failed CC initial answer received and CCFH equal to CONTINUE
                  // Action: Grant service to end user
                  // New State: IDLE
                  context.grantAccessOnFailureMessage(this);
                  setState(ClientRoSessionState.IDLE, false);
                  break;
                case CCFH_TERMINATE:
                case CCFH_RETRY_AND_TERMINATE:
                  // Current State: PENDING_I
                  // Event: Failed CC initial answer received and CCFH equal to TERMINATE or to RETRY_AND_TERMINATE
                  // Action: Terminate end user�s service
                  // New State: IDLE
                  context.denyAccessOnFailureMessage(this);
                  setState(ClientRoSessionState.IDLE, false);
                  break;
                default:
                  logger.warn("Invalid value for CCFH: {}", getLocalCCFH());
                  break;
              }
            }
            break;

          case PENDING_UPDATE:
            if (resultCode == CREDIT_CONTROL_NOT_APPLICABLE) {
              // Current State: PENDING_U
              // Event: CC update answer received with result code equal to CREDIT_CONTROL_NOT_APPLICABLE
              // Action: Grant service to end user
              // New State: IDLE
              context.grantAccessOnFailureMessage(this);
              setState(ClientRoSessionState.IDLE, false);
            }
            else if (resultCode == END_USER_SERVICE_DENIED) {
              // Current State: PENDING_U
              // Event: CC update answer received with result code END_USER_SERVICE_DENIED
              // Action: Terminate end user�s service
              // New State: IDLE
              context.denyAccessOnFailureMessage(this);
              setState(ClientRoSessionState.IDLE, false);
            }
            else {
              // Temporary errors and others
              switch (getLocalCCFH()) {
                case CCFH_CONTINUE:
                  // Current State: PENDING_U
                  // Event: Failed CC update answer received and CCFH equal to CONTINUE
                  // Action: Grant service to end user
                  // New State: IDLE
                  context.grantAccessOnFailureMessage(this);
                  setState(ClientRoSessionState.IDLE, false);
                  break;
                case CCFH_TERMINATE:
                case CCFH_RETRY_AND_TERMINATE:
                  // Current State: PENDING_U
                  // Event: Failed CC update answer received and CCFH equal to CONTINUE or to RETRY_AND_CONTINUE
                  // Action: Terminate end user�s service
                  // New State: IDLE
                  context.denyAccessOnFailureMessage(this);
                  setState(ClientRoSessionState.IDLE, false);
                  break;
                default:
                  logger.warn("Invalid value for CCFH: " + getLocalCCFH());
                  break;
              }
            }
            break;

          default:
            logger.warn("Wrong event type ({}) on state {}", eventType, state);
        }
      }
    }
    catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure handling failure message for Event " + event + " (" + eventType + ") and Request " + request, e);
      }
    }
  }

  protected void handleTxExpires(Message message) {
    // Event Based ----------------------------------------------------------
    ClientRoSessionState state = sessionData.getClientRoSessionState();

    if (isEventBased()) {
      int gatheredRequestedAction = sessionData.getGatheredRequestedAction();
      if (gatheredRequestedAction == CHECK_BALANCE || gatheredRequestedAction == PRICE_ENQUIRY) {
        // Current State: PENDING_E
        // Event: Failure to send, temporary error, failed CC event answer received or Tx expired; requested action CHECK_BALANCE or PRICE_ENQUIRY
        // Action: Indicate service error
        // New State: IDLE
        context.indicateServiceError(this);
        setState(ClientRoSessionState.IDLE);
      }
      else if (gatheredRequestedAction == DIRECT_DEBITING) {
        if (sessionData.getGatheredDDFH() == DDFH_TERMINATE_OR_BUFFER) {
          // Current State: PENDING_E
          // Event: Temporary error; requested action DIRECT_DEBITING; DDFH equal to TERMINATE_OR_BUFFER; Tx expired
          // Action: Store request
          // New State: IDLE
          sessionData.setBuffer((Request) message);
          setState(ClientRoSessionState.IDLE, false);
        }
        else {
          // Current State: PENDING_E
          // Event: Tx expired; requested action DIRECT_DEBITING
          // Action: Grant service to end user
          // New State: PENDING_E
          context.grantAccessOnTxExpire(this);
          setState(ClientRoSessionState.PENDING_EVENT);
        }
      }
      else if (gatheredRequestedAction == REFUND_ACCOUNT) {
        // Current State: PENDING_E
        // Event: Failure to send or Tx expired; requested action REFUND_ACCOUNT
        // Action: Store request with T-flag
        // New State: IDLE
        message.setReTransmitted(true);
        sessionData.setBuffer((Request) message);

        setState(ClientRoSessionState.IDLE, false);
      }
    }
    // Session Based --------------------------------------------------------
    else {
      switch (state) {
        case PENDING_INITIAL:
          switch (getLocalCCFH()) {
            case CCFH_CONTINUE:
            case CCFH_RETRY_AND_TERMINATE:
              // Current State: PENDING_I
              // Event: Tx expired and CCFH equal to CONTINUE or to RETRY_AND_TERMINATE
              // Action: Grant service to end user
              // New State: PENDING_I
              context.grantAccessOnTxExpire(this);
              break;

            case CCFH_TERMINATE:
              // Current State: PENDING_I
              // Event: Tx expired and CCFH equal to TERMINATE
              // Action: Terminate end user�s service
              // New State: IDLE
              context.denyAccessOnTxExpire(this);
              setState(ClientRoSessionState.IDLE, true);
              break;

            default:
              logger.warn("Invalid value for CCFH: " + getLocalCCFH());
              break;
          }
          break;

        case PENDING_UPDATE:
          switch (getLocalCCFH()) {
            case CCFH_CONTINUE:
            case CCFH_RETRY_AND_TERMINATE:
              // Current State: PENDING_U
              // Event: Tx expired and CCFH equal to CONTINUE or to RETRY_AND_TERMINATE
              // Action: Grant service to end user
              // New State: PENDING_U
              context.grantAccessOnTxExpire(this);
              break;

            case CCFH_TERMINATE:
              // Current State: PENDING_U
              // Event: Tx expired and CCFH equal to TERMINATE
              // Action: Terminate end user�s service
              // New State: IDLE
              context.denyAccessOnTxExpire(this);
              setState(ClientRoSessionState.IDLE, true);
              break;

            default:
              logger.error("Bad value of CCFH: " + getLocalCCFH());
              break;
          }
          break;

        default:
          logger.error("Unknown state (" + sessionData.getClientRoSessionState() + ") on txExpire");
          break;
      }
    }

    this.release();
  }

  protected void handleRetransmissionFailure(RoCreditControlRequest req) {
    try {
      deliverRequestTimeout(req.getMessage());
      resetMessageStatus(req.getMessage());
    }
    catch (InternalException e) {
      logger.error("Cannot remove the expired message from either peer or rouoter tables for session [{}]", this.getSessionId());
    }

    setState(ClientRoSessionState.IDLE, true);
  }

  protected void handlePeerUnavailability(Message msg, NoMorePeersAvailableException nmpae) {
    logger.warn("No more peers available for sending diameter message: ", nmpae.getMessage());
    if (logger.isDebugEnabled()) {
      logger.debug("nmpa exception: {}", nmpae);
    }
    deliverPeerUnavailabilityError(msg, nmpae);
    resetMessageStatus(msg);
    setState(ClientRoSessionState.IDLE, true);
  }

  protected void handleRetransmission(Type eventType, IMessage msg, boolean tFlagSetting) {
    msg.setReTransmitted(tFlagSetting);
    if (this.sessionData.getRetransmissionTimerId() == null) {
      startFailoverStopTimer();
    }

    if (sf.isSessionPersistenceEnabled()) {
      String oldPeer = flushSessionPersistenceContext();
      logger.debug("Routing context for peer [{}] was removed from session [{}] due to retransmission", oldPeer, this.getSessionId());
    }

    resetMessageStatus(msg);
    startTx(msg);

    try {
      dispatchEvent(msg);
    }
    catch (NoMorePeersAvailableException nmpae) {
      handlePeerUnavailability(msg, nmpae);
    }
    catch (Exception e1) {
      logger.error("Cannot retransmit an old request", e1);
      try {
        handleSendFailure(e1, eventType, msg);
      }
      catch (Exception e2) {
        logger.error("Failure handling error", e2);
      }
    }
  }

  protected void handleRetransmissionDueToError(Type eventType, IMessage msg) {
    logger.warn("Message will be retransmitted due to error response [{}] ", msg);

    try {
      if (msg.isRetransmissionAllowed()) {
        if (isSessionFailoverSupported()) {
          handleRetransmission(eventType, msg, false);
          msg.decrementNumberOfRetransAllowed();
        }
        else {
          handleSendFailure(new Exception("Failover unsupported for session ID: " + sessionData.getSessionId()), eventType, msg);
        }
      }
      else {
        NoMorePeersAvailableException cause = new NoMorePeersAvailableException("No more peers available for retransmission");
        cause.setSessionPersistentRoutingEnabled(router.isSessionAware());
        handlePeerUnavailability(msg, cause);
      }
    }
    catch (Exception e) {
      logger.error("Failure handling send failure error in handleRetransmissionDueToError", e);
    }
  }

  protected void handleRetransmissionDueToTimeout(Type eventType, AppEvent event) throws InternalException {
    if (isSessionFailoverSupported()) {
      handleRetransmission(eventType, (IMessage) event.getMessage(), true);
    }
    else {
      logger.warn("Failed to send message. Failover unsupported for session ID: {}", sessionData.getSessionId());
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to send message, type: {} message: {}, failure: Failover unsupported for session ID: {}", new Object[]{eventType, event
            .getMessage(), sessionData.getSessionId()});
      }
      handleRetransmissionFailure((RoCreditControlRequest) event);
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
      Request buffer = sessionData.getBuffer();
      if (buffer != null) {
        setState(ClientRoSessionState.PENDING_BUFFERED);
        try {
          dispatchEvent(new AppRequestEventImpl(buffer));
        }
        catch (Exception e) {
          try {
            handleSendFailure(e, Event.Type.SEND_EVENT_REQUEST, buffer);
          }
          catch (Exception e1) {
            logger.error("Failure handling buffer send failure", e1);
          }
        }
      }
    }
    // Session Based --------------------------------------------------------
    else {
      if (sessionData.getClientRoSessionState() == ClientRoSessionState.OPEN && eventQueue.size() > 0) {
        try {
          this.handleEvent(eventQueue.remove(0));
        }
        catch (Exception e) {
          logger.error("Failure handling queued event", e);
        }
      }
    }
  }

  protected void deliverRoAnswer(RoCreditControlRequest request, RoCreditControlAnswer answer) {
    logger.debug("Propagating answer event to listener [{}] on {} session", listener, isValid() ? "valid" : "invalid");
    try {
      if (isValid()) {
        listener.doCreditControlAnswer(this, request, answer);
      }
    }
    catch (Exception e) {
      logger.warn("Failure delivering Ro Answer", e);
    }
  }

  protected void deliverRAR(ReAuthRequest request) {
    logger.debug("Propagating RAR event to listener [{}] on {} session", listener, isValid() ? "valid" : "invalid");
    try {
      listener.doReAuthRequest(this, request);
    }
    catch (Exception e) {
      logger.warn("Failure delivering RAR", e);
    }
  }

  protected void deliverRequestTxTimeout(Message msg) {
    logger.debug("Propagating Tx timeout event to listener [{}] on {} session", listener, isValid() ? "valid" : "invalid");
    try {
      if (isValid()) {
        listener.doRequestTxTimeout(this, msg, ((IMessage) msg).getPeer());
      }
    }
    catch (Exception e) {
      logger.warn("Failure delivering request tx timeout", e);
    }
  }

  protected void deliverRequestTimeout(Message msg) {
    logger.debug("Propagating timeout event to listener [{}] on {} session", listener, isValid() ? "valid" : "invalid");
    try {
      if (isValid()) {
        listener.doRequestTimeout(this, msg, ((IMessage) msg).getPeer());
      }
    }
    catch (Exception e) {
      logger.warn("Failure delivering request timeout", e);
    }
  }

  protected void deliverPeerUnavailabilityError(Message msg, NoMorePeersAvailableException cause) {
    logger.debug("Propagating peer unavailability error event to listener [{}] on {} session", listener, isValid() ? "valid" : "invalid");
    try {
      if (isValid()) {
        listener.doPeerUnavailability(this, msg, ((IMessage) msg).getPeer(), cause);
      }
    }
    catch (Exception e) {
      logger.warn("Failure delivering peer unavailability error", e);
    }
  }

  protected void extractFHAVPs(RoCreditControlRequest request, RoCreditControlAnswer answer) throws AvpDataException {
    if (answer != null) {
      try {
        if (answer.isCreditControlFailureHandlingAVPPresent()) {
          sessionData.setGatheredCCFH(answer.getCredidControlFailureHandlingAVPValue());
        }
      }
      catch (Exception e) {
        logger.debug("Failure trying to obtain Credit-Control-Failure-Handling AVP value", e);
      }
      try {
        if (answer.isDirectDebitingFailureHandlingAVPPresent()) {
          sessionData.setGatheredDDFH(answer.getDirectDebitingFailureHandlingAVPValue());
        }
      }
      catch (Exception e) {
        logger.debug("Failure trying to obtain Direct-Debit-Failure-Handling AVP value", e);
      }
      if (!sessionData.isRequestTypeSet()) {
        sessionData.setRequestTypeSet(true);
        // No need to check if it exists.. it must, if not fail with exception
        sessionData.setEventBased(answer.getRequestTypeAVPValue() == EVENT_REQUEST);
      }
    }
    else if (request != null) {
      try {
        if (request.isRequestedActionAVPPresent()) {
          sessionData.setGatheredRequestedAction(request.getRequestedActionAVPValue());
        }
      }
      catch (Exception e) {
        logger.debug("Failure trying to obtain Request-Action AVP value", e);
      }

      if (!sessionData.isRequestTypeSet()) {
        sessionData.setRequestTypeSet(true);
        // No need to check if it exists.. it must, if not fail with exception
        sessionData.setEventBased(request.getRequestTypeAVPValue() == EVENT_REQUEST);
      }
    }
  }

  protected void dispatchEvent(IMessage message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (message.isRequest()) {
      message.setRetransmissionSupervised(true);
    }
    session.send(message, this);
  }

  protected void dispatchEvent(AppEvent event) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    dispatchEvent((IMessage) event.getMessage());
  }

  protected boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected boolean isFailure(long code) {
    return (!isProvisional(code) && !isSuccess(code) && ((code >= 3000 && /*code < 4000) || (code >= 5000 &&*/ code < 6000)) && !temporaryErrorCodes.contains
        (code));
  }

  protected boolean isRetransmissionRequired() {
    return (getLocalCCFH() == CCFH_CONTINUE || getLocalCCFH() == CCFH_RETRY_AND_TERMINATE);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }

  private class TxTimerTask implements Runnable {
    private ClientRoSession session = null;
    private Request request = null;

    private TxTimerTask(ClientRoSession session, Request request) {
      super();
      this.session = session;
      this.request = request;
    }

    public void run() {
      try {
        sendAndStateLock.lock();
        logger.debug("Fired Tx timer");
        sessionData.setTxTimerId(null);
        sessionData.setTxTimerRequest(null);

        RoCreditControlRequest req = factory.createCreditControlRequest(request);
        handleEvent(new Event(Event.Type.Tx_TIMER_FIRED, req, null));
      }
      catch (InternalException e) {
        logger.error("Internal Exception", e);
      }
      catch (OverloadException e) {
        logger.error("Overload Exception", e);
      }
      catch (Exception e) {
        logger.error("Exception", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

  private class RetransmissionTimerTask implements Runnable {
    private Request request = null;

    private RetransmissionTimerTask(ClientRoSession session, Request request) {
      super();
      this.request = request;
    }

    public void run() {
      try {
        sendAndStateLock.lock();
        logger.debug("Fired failover stop timer (Retransmission timout occured)");
        stopTx(false);
        sessionData.setRetransmissionTimerId(null);
        handleEvent(new Event(Event.Type.RETRANSMISSION_TIMER_FIRED, factory.createCreditControlRequest(request), null));
      }
      catch (InternalException e) {
        logger.error("Internal Exception", e);
      }
      catch (OverloadException e) {
        logger.error("Overload Exception", e);
      }
      catch (Exception e) {
        logger.error("Exception", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

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

  private void resetMessageStatus(Message message) {
    IMessage msg = (IMessage) message;
    msg.clearTimer();
    msg.setState(IMessage.STATE_NOT_SENT);
    if (msg.getPeer() != null) {
      msg.getPeer().remMessage(msg);
    }
    router.garbageCollectRequestRouteInfo(msg);
  }

  private class RequestDelivery implements Runnable {
    ClientRoSession session;
    Request request;

    public void run() {
      try {
        switch (request.getCommandCode()) {
          case ReAuthAnswerImpl.code:
            handleEvent(new Event(Event.Type.RECEIVED_RAR, factory.createReAuthRequest(request), null));
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
    ClientRoSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        switch (request.getCommandCode()) {
          case RoCreditControlAnswer.code:
            RoCreditControlRequest _request = factory.createCreditControlRequest(request);
            RoCreditControlAnswer _answer = factory.createCreditControlAnswer(answer);
            extractFHAVPs(null, _answer);
            handleEvent(new Event(false, _request, _answer));
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

}
