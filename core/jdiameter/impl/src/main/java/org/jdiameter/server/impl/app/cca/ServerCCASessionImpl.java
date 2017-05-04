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

package org.jdiameter.server.impl.app.cca;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.cca.ICCAMessageFactory;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.api.app.cca.ServerCCASessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.AppCCASessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Credit Control Application Server session implementation
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerCCASessionImpl extends AppCCASessionImpl implements ServerCCASession, NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ServerCCASessionImpl.class);

  protected IServerCCASessionData sessionData;
  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient ICCAMessageFactory factory = null;
  protected transient IServerCCASessionContext context = null;
  protected transient ServerCCASessionListener listener = null;

  protected static final String TCC_TIMER_NAME = "TCC_CCASERVER_TIMER";

  protected long[] authAppIds = new long[]{4};
  //protected String originHost, originRealm;

  public ServerCCASessionImpl(IServerCCASessionData data, ICCAMessageFactory fct, ISessionFactory sf, ServerCCASessionListener lst,
      IServerCCASessionContext ctx, StateChangeListener<AppSession> stLst) {
    super(sf, data);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (data == null) {
      throw new IllegalArgumentException("IServerCCASessionData can not be null");
    }

    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    sessionData = data;
    context = ctx;

    authAppIds = fct.getApplicationIds();
    listener = lst;
    factory = fct;
    super.addStateChangeNotification(stLst);
  }

  @Override
  public void sendCreditControlAnswer(JCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    handleEvent(new Event(false, null, answer));
  }

  @Override
  public void sendReAuthRequest(ReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SENT_RAR, request, null);
  }

  @Override
  public boolean isStateless() {
    return sessionData.isStateless();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == ServerCCASessionState.class ? (E) sessionData.getServerCCASessionState() : null;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    ServerCCASessionState newState = null;

    try {
      sendAndStateLock.lock();
      ServerCCASessionState state = this.sessionData.getServerCCASessionState();
      // Can be null if there is no state transition, transition to IDLE state should terminate this app session
      Event localEvent = (Event) event;

      //Its kind of awkward, but with two state on server side its easier to go through event types?
      //but for sake of FSM readability
      Event.Type eventType = (Event.Type) localEvent.getType();
      switch (state) {
        case IDLE:
          switch (eventType) {
            case RECEIVED_INITIAL:
              listener.doCreditControlRequest(this, (JCreditControlRequest) localEvent.getRequest());
              break;

            case RECEIVED_EVENT:
              // Current State: IDLE
              // Event: CC event request received and successfully processed
              // Action: Send CC event answer
              // New State: IDLE
              listener.doCreditControlRequest(this, (JCreditControlRequest) localEvent.getRequest());
              break;

            case SENT_EVENT_RESPONSE:
              // Current State: IDLE
              // Event: CC event request received and successfully processed
              // Action: Send CC event answer
              // New State: IDLE

              // Current State: IDLE
              // Event: CC event request received but not successfully processed
              // Action: Send CC event answer with Result-Code != SUCCESS
              // New State: IDLE
              newState = ServerCCASessionState.IDLE;
              dispatchEvent(localEvent.getAnswer());
              setState(newState);
              break;

            case SENT_INITIAL_RESPONSE:
              JCreditControlAnswer answer = (JCreditControlAnswer) localEvent.getAnswer();
              try {
                long resultCode = answer.getResultCodeAvp().getUnsigned32();
                // Current State: IDLE
                // Event: CC initial request received and successfully processed
                // Action: Send CC initial answer, reserve units, start Tcc
                // New State: OPEN
                if (isSuccess(resultCode)) {
                  Avp vtAvp = answer.getValidityTimeAvp();
                  if (vtAvp == null) {
                    Avp mscc = answer.getMessage().getAvps().getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL);
                    vtAvp = mscc != null ? mscc.getGrouped().getAvp(Avp.VALIDITY_TIME) : null;
                  }
                  startTcc(vtAvp);
                  newState = ServerCCASessionState.OPEN;
                }
                // Current State: IDLE
                // Event: CC initial request received but not successfully processed
                // Action: Send CC initial answer with Result-Code != SUCCESS
                // New State: IDLE
                else {
                  newState = ServerCCASessionState.IDLE;
                }
                dispatchEvent(localEvent.getAnswer());
                setState(newState);
              }
              catch (AvpDataException e) {
                throw new InternalException(e);
              }
              break;
            case RECEIVED_UPDATE:
            case RECEIVED_TERMINATE:
              Answer errorAnswer = ((Request) localEvent.getRequest().getMessage()).createAnswer(ResultCode.UNKNOWN_SESSION_ID);
              session.send(errorAnswer);
              logger.debug("Received an UPDATE or TERMINATE for a new session. Answering with 5002 (UNKNOWN_SESSION_ID) and terminating session.");
              // and let it throw exception anyway ...
            default:
              throw new InternalException("Wrong state: " + ServerCCASessionState.IDLE + " on event: " + eventType + " " + localEvent.getRequest() + " " +
                  localEvent.getAnswer());
          }

        case OPEN:
          switch (eventType) {
            /* This should not happen, it should be silently discarded, right?
        case RECEIVED_INITIAL:
          // only for rtr
          if (((JCreditControlRequest) localEvent.getRequest()).getMessage().isReTransmitted()) {
            listener.doCreditControlRequest(this, (JCreditControlRequest) localEvent.getRequest());
          }
          else {
            //do nothing?
          }
          break;
             */
            case RECEIVED_UPDATE:
              listener.doCreditControlRequest(this, (JCreditControlRequest) localEvent.getRequest());
              break;

            case SENT_UPDATE_RESPONSE:
              JCreditControlAnswer answer = (JCreditControlAnswer) localEvent.getAnswer();
              try {
                if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
                  // Current State: OPEN
                  // Event: CC update request received and successfully processed
                  // Action: Send CC update answer, debit used units, reserve new units, restart Tcc
                  // New State: OPEN
                  Avp vtAvp = answer.getValidityTimeAvp();
                  if (vtAvp == null) {
                    Avp mscc = answer.getMessage().getAvps().getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL);
                    vtAvp = mscc != null ? mscc.getGrouped().getAvp(Avp.VALIDITY_TIME) : null;
                  }
                  startTcc(vtAvp);
                }
                else {
                  // Current State: OPEN
                  // Event: CC update request received but not successfully processed
                  // Action: Send CC update answer with Result-Code != SUCCESS, debit used units
                  // New State: IDLE

                  // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                }
              }
              catch (AvpDataException e) {
                throw new InternalException(e);
              }
              dispatchEvent(localEvent.getAnswer());
              break;
            case RECEIVED_TERMINATE:
              listener.doCreditControlRequest(this, (JCreditControlRequest) localEvent.getRequest());
              break;
            case SENT_TERMINATE_RESPONSE:
              answer = (JCreditControlAnswer) localEvent.getAnswer();
              try {
                // Current State: OPEN
                // Event: CC termination request received and successfully processed
                // Action: Send CC termination answer, Stop Tcc, debit used units
                // New State: IDLE
                if (isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
                  stopTcc(false);
                }
                else {
                  // Current State: OPEN
                  // Event: CC termination request received but not successfully processed
                  // Action: Send CC termination answer with Result-Code != SUCCESS, debit used units
                  // New State: IDLE

                  // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
                }
              }
              catch (AvpDataException e) {
                throw new InternalException(e);
              }

              newState = ServerCCASessionState.IDLE;
              dispatchEvent(localEvent.getAnswer());
              setState(newState);
              break;

            case RECEIVED_RAA:
              listener.doReAuthAnswer(this, new ReAuthRequestImpl(localEvent.getRequest().getMessage()),
                  new ReAuthAnswerImpl((Answer) localEvent.getAnswer().getMessage()));
              break;
            case SENT_RAR:
              dispatchEvent(localEvent.getRequest());
              break;
          }
      }
      return true;
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
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

  private class TccScheduledTask implements Runnable {
    ServerCCASession session = null;

    private TccScheduledTask(ServerCCASession session) {
      super();
      this.session = session;
    }

    @Override
    public void run() {
      // Current State: OPEN
      // Event: Session supervision timer Tcc expired
      // Action: Release reserved units
      // New State: IDLE
      context.sessionSupervisionTimerExpired(session);
      try {
        sendAndStateLock.lock();
        // tccFuture = null;
        sessionData.setTccTimerId(null);
        setState(ServerCCASessionState.IDLE);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    //rd.session = (ServerCCASession) LocalDataSource.INSTANCE.getSession(request.getSessionId());
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

  @Override
  public void timeoutExpired(Request request) {
    context.timeoutExpired(request);
    //FIXME: Should we release ?
  }

  private void startTcc(Avp validityAvp) {
    long tccTimeout;

    if (validityAvp != null) {
      try {
        tccTimeout = 2 * validityAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        logger.debug("Unable to retrieve Validity-Time AVP value, using default.", e);
        tccTimeout = 2 * context.getDefaultValidityTime();
      }
    }
    else {
      tccTimeout = 2 * context.getDefaultValidityTime();
    }

    logger.debug("Starting TCC timer with Validity-Avp[{}] and tccTimeout[{}] seconds", validityAvp, tccTimeout);

    if (sessionData.getTccTimerId() != null) {
      stopTcc(true);
      //tccFuture = super.scheduler.schedule(new TccScheduledTask(this), defaultValue, TimeUnit.SECONDS);
      this.sessionData.setTccTimerId(super.timerFacility.schedule(this.getSessionId(), TCC_TIMER_NAME, tccTimeout * 1000));
      // FIXME: this accepts Future!
      context.sessionSupervisionTimerReStarted(this, null);
    }
    else {
      //tccFuture = super.scheduler.schedule(new TccScheduledTask(this), defaultValue, TimeUnit.SECONDS);
      this.sessionData.setTccTimerId(super.timerFacility.schedule(this.getSessionId(),  TCC_TIMER_NAME, tccTimeout * 1000));
      //FIXME: this accepts Future!
      context.sessionSupervisionTimerStarted(this, null);
    }
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
    else if (timerName.equals(TCC_TIMER_NAME)) {
      new TccScheduledTask(this).run();
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }

  private void stopTcc(boolean willRestart) {
    Serializable tccTimerId = this.sessionData.getTccTimerId();
    if (tccTimerId != null) {
      // tccFuture.cancel(false);
      super.timerFacility.cancel(tccTimerId);
      // ScheduledFuture f = tccFuture;
      this.sessionData.setTccTimerId(null);
      if (!willRestart) {
        context.sessionSupervisionTimerStopped(this, null);
      }
    }
  }

  protected  boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected void setState(ServerCCASessionState newState) {
    setState(newState, true);
  }

  @SuppressWarnings("unchecked")
  protected void setState(ServerCCASessionState newState, boolean release) {
    IAppSessionState oldState = this.sessionData.getServerCCASessionState();
    this.sessionData.setServerCCASessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, (Enum) oldState, (Enum) newState);
    }
    if (newState == ServerCCASessionState.IDLE) {
      // do EVERYTHING before this!
      stopTcc(false);
      if (release) {
        this.release();
      }
    }
  }

  @Override
  public void release() {
    if (isValid()) {
      try {
        sendAndStateLock.lock();

        this.stopTcc(false);
        super.release();
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
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException {
    try {
      session.send(event.getMessage(), this);
      // Store last destination information
      // FIXME: add differentiation on server/client request
      //originRealm = event.getMessage().getAvps().getAvp(Avp.ORIGIN_REALM).getOctetString();
      //originHost = event.getMessage().getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString();
    }
    catch (Exception e) {
      //throw new InternalException(e);
      logger.debug("Failure trying to dispatch event", e);
    }
  }

  private class RequestDelivery implements Runnable {
    ServerCCASession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case JCreditControlAnswer.code:
            handleEvent(new Event(true, factory.createCreditControlRequest(request), null));
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
    ServerCCASession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        // FIXME: baranowb: add message validation here!!!
        // We handle CCR, STR, ACR, ASR other go into extension
        switch (request.getCommandCode()) {
          case ReAuthRequest.code:
            handleEvent(new Event(Event.Type.RECEIVED_RAA, factory.createReAuthRequest(request), factory.createReAuthAnswer(answer)));
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
    int result = 1;
    result = prime * result + ((sessionData == null) ? 0 : sessionData.hashCode());
    return result;
  }

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
    ServerCCASessionImpl other = (ServerCCASessionImpl) obj;
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
  public String toString() {
    return "ServerCCASessionImpl [sessionData=" + sessionData + "]";
  }

}
