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

package org.jdiameter.client.impl.app.acc;

import static org.jdiameter.api.Avp.ACCOUNTING_REALTIME_REQUIRED;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.IDLE;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.OPEN;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.PENDING_BUFFERED;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.PENDING_CLOSE;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.PENDING_EVENT;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.PENDING_INTERIM;
import static org.jdiameter.common.api.app.acc.ClientAccSessionState.PENDING_START;

import java.io.Serializable;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;
import org.jdiameter.common.api.app.acc.IClientAccActionContext;
import org.jdiameter.common.impl.app.AppEventImpl;
import org.jdiameter.common.impl.app.acc.AppAccSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Accounting session implementation
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientAccSessionImpl extends AppAccSessionImpl implements EventListener<Request, Answer>, ClientAccSession {

  private static final Logger logger = LoggerFactory.getLogger(ClientAccSessionImpl.class);

  // Constants ----------------------------------------------------------------
  public static final int DELIVER_AND_GRANT = 1;
  public static final int GRANT_AND_LOSE = 3;

  // Session State Handling ---------------------------------------------------
  //protected ClientAccSessionState state = IDLE;

  // Factories and Listeners --------------------------------------------------
  protected IClientAccActionContext context;
  protected ClientAccSessionListener listener;
  //protected transient IMessageParser parser;
  //protected Serializable timerId_interim;
  protected static final String TIMER_NAME_INTERIM = "CLIENT_INTERIM";

  //protected String destHost, destRealm;
  //protected AccountRequest buffer;
  protected IClientAccSessionData sessionData;
  public ClientAccSessionImpl(IClientAccSessionData sessionData, ISessionFactory sessionFactory, ClientAccSessionListener clientAccSessionListener,
      IClientAccActionContext iClientAccActionContext, StateChangeListener<AppSession> stateChangeListener) {
    super(sessionFactory, sessionData);
    this.sessionData = sessionData;
    this.listener = clientAccSessionListener;
    this.context = iClientAccActionContext;

    super.addStateChangeNotification(stateChangeListener);
  }

  @Override
  public void sendAccountRequest(AccountRequest accountRequest) throws InternalException, IllegalStateException, RouteException, OverloadException {
    try {
      sendAndStateLock.lock();
      handleEvent(new Event(accountRequest));
      try {
        session.send(accountRequest.getMessage(), this);
        // Store last destination information
        sessionData.setDestinationRealm(accountRequest.getMessage().getAvps().getAvp(Avp.DESTINATION_REALM).getDiameterIdentity());
        Avp destHostAvp = accountRequest.getMessage().getAvps().getAvp(Avp.DESTINATION_HOST);
        if (destHostAvp != null) {
          sessionData.setDestinationHost(destHostAvp.getDiameterIdentity());
        }
      }
      catch (Throwable t) {
        logger.debug("Failed to send ACR.", t);
        handleEvent(new Event(Event.Type.FAILED_SEND_RECORD, accountRequest));
      }
    }
    catch (Exception exc) {
      throw new InternalException(exc);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected synchronized void storeToBuffer(Request accountRequest) {
    sessionData.setBuffer(accountRequest);

  }

  protected synchronized boolean checkBufferSpace() {
    //TODO: make this different op, so it does not have to fetch data.
    return sessionData.getBuffer() == null;
  }

  @SuppressWarnings("unchecked")
  protected void setState(IAppSessionState newState) {
    IAppSessionState oldState = sessionData.getClientAccSessionState();
    sessionData.setClientAccSessionState((ClientAccSessionState) newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  @Override
  public boolean isStateless() {
    return false;
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {

    ClientAccSessionState oldState = sessionData.getClientAccSessionState();

    try {
      switch (oldState) {
        // Idle ==========
        case IDLE: {
          switch ((Event.Type) event.getType()) {
            // Client or device requests access
            case SEND_START_RECORD:
              // Current State: IDLE
              // Event: Client or Device Requests access
              // Action: Send accounting start req.
              // New State: PENDING_S
              setState(PENDING_START);
              break;
            case SEND_EVENT_RECORD:
              // Current State: IDLE
              // Event: Client or device requests a one-time service
              // Action: Send accounting event req
              // New State: PENDING_E
              setState(PENDING_EVENT);
              break;
              // Send buffered message action in other section of this method see below
            default:
              throw new IllegalStateException("Current state " + oldState + " action " + event.getType());
          }
          break;
        }
        // PendingS ==========
        case PENDING_START: {
          switch ((Event.Type) event.getType()) {
            case FAILED_SEND_RECORD:
              AccountRequest request = (AccountRequest) event.getData();
              Avp accRtReq = request.getMessage().getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);

              // Current State: PENDING_S
              // Event: Failure to send and buffer space available and realtime not equal to DELIVER_AND_GRANT
              // Action: Store Start Record
              // New State: OPEN
              if (checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != DELIVER_AND_GRANT) {
                //... cast overkill...
                storeToBuffer((Request) ((AppEventImpl) request).getMessage());
                setState(OPEN);
              }
              else {
                // Current State: PENDING_S
                // Event: Failure to send and no buffer space available and realtime equal to GRANT_AND_LOSE
                // Action: -
                // New State: OPEN
                if (!checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
                  setState(OPEN);
                }
                else {
                  // Current State: PENDING_S
                  // Event: Failure to send and no buffer space available and realtime not equal to GRANT_AND_LOSE
                  // Action: Disconnect User/Device
                  // New State: IDLE
                  if (!checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != GRANT_AND_LOSE) {
                    try {
                      if (context != null) {
                        Request str = createSessionTermRequest();
                        context.disconnectUserOrDev(this, str);
                        session.send(str, this);
                      }
                    }
                    finally {
                      setState(IDLE);
                    }
                  }
                }
              }
              break;
            case RECEIVED_RECORD:
              // Current State: PENDING_S
              // Event: Successful accounting start answer received
              // Action: -
              // New State: OPEN
              processInterimIntervalAvp(event);
              setState(OPEN);
              break;
            case FAILED_RECEIVE_RECORD:
              try {
                AccountAnswer answer = (AccountAnswer) event.getData();
                accRtReq = answer.getMessage().getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);
                // Current State: PENDING_S
                // Event: Failed accounting start answer received and realtime equal to GRANT_AND_LOSE
                // Action: -
                // New State: OPEN
                if (accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
                  setState(OPEN);
                }
                else {
                  // Current State: PENDING_S
                  // Event: Failed accounting start answer received and realtime not equal to GRANT_AND_LOSE
                  // Action: Disconnect User/Device
                  // New State: IDLE
                  if (accRtReq != null && accRtReq.getInteger32() != GRANT_AND_LOSE) {
                    try {
                      if (context != null) {
                        Request str = createSessionTermRequest();
                        context.disconnectUserOrDev(this, str);
                        session.send(str, this);
                      }
                    }
                    finally {
                      setState(IDLE);
                    }
                  }
                }
              }
              catch (Exception e) {
                logger.debug("Can not process answer", e);
                setState(IDLE);
              }
              break;
            case SEND_STOP_RECORD:
              // Current State: PENDING_S
              // Event: User service terminated
              // Action: Store stop record
              // New State: PENDING_S
              if (context != null) {
                Request str = createSessionTermRequest();
                context.disconnectUserOrDev(this, str);
                storeToBuffer(str);
              }
              break;
          }
          break;
        }
        // OPEN ==========
        case OPEN: {
          switch ((Event.Type) event.getType()) {
            // User service terminated
            case SEND_STOP_RECORD:
              // Current State: OPEN
              // Event: User service terminated
              // Action: Send accounting stop request
              // New State: PENDING_L
              setState(PENDING_CLOSE);
              break;
            case SEND_INTERIM_RECORD:
              // FIXME: Shouldn't this be different ?
              // Current State: OPEN
              // Event: Interim interval elapses
              // Action: Send accounting interim record
              // New State: PENDING_I
              setState(PENDING_INTERIM);
              break;

              // Create timer for "Interim interval elapses" event
            case RECEIVED_RECORD:
              processInterimIntervalAvp(event);
              break;
          }
        }
        break;
        //FIXME: add check for abnormal
        // PendingI ==========
        case PENDING_INTERIM: {
          switch ((Event.Type) event.getType()) {
            case RECEIVED_RECORD:
              // Current State: PENDING_I
              // Event: Successful accounting interim answer received
              // Action: -
              // New State: OPEN
              processInterimIntervalAvp(event);
              setState(OPEN);
              break;
            case FAILED_SEND_RECORD:
              AccountRequest request = (AccountRequest) event.getData();
              Avp accRtReq = request.getMessage().getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);

              // Current State: PENDING_I
              // Event: Failure to send and buffer space available (or old record interim can be overwritten) and realtime not equal to DELIVER_AND_GRANT
              // Action: Store interim record
              // New State: OPEN
              if (checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != DELIVER_AND_GRANT) {
                //... cast overkill...
                storeToBuffer((Request) ((AppEventImpl) request).getMessage());
                setState(OPEN);
              }
              else {
                // Current State: PENDING_I
                // Event: Failure to send and no buffer space available and realtime equal to GRANT_AND_LOSE
                // Action: -
                // New State: OPEN
                if (!checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
                  setState(OPEN);
                }
                else {
                  // Current State: PENDING_I
                  // Event: Failure to send and no buffer space available and realtime not equal to GRANT_AND_LOSE
                  // Action: Disconnect User/Device
                  // New State: IDLE
                  if (!checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != GRANT_AND_LOSE) {
                    try {
                      if (context != null) {
                        Request str = createSessionTermRequest();
                        context.disconnectUserOrDev(this, str);
                        session.send(str, this);
                      }
                    }
                    finally {
                      setState(IDLE);
                    }
                  }
                }
              }
              break;
            case FAILED_RECEIVE_RECORD:
              try {
                AccountAnswer answer = (AccountAnswer) event.getData();
                accRtReq = answer.getMessage().getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);

                // Current State: PENDING_I
                // Event: Failed accounting interim answer received and realtime equal to GRANT_AND_LOSE
                // Action: -
                // New State: OPEN
                if (accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
                  setState(OPEN);
                }
                else {
                  // Current State: PENDING_I
                  // Event: Failed account interim answer received and realtime not equal to GRANT_AND_LOSE
                  // Action: Disconnect User/Device
                  // New State: IDLE
                  if (accRtReq != null && accRtReq.getInteger32() != GRANT_AND_LOSE) {
                    try {
                      if (context != null) {
                        Request str = createSessionTermRequest();
                        context.disconnectUserOrDev(this, str);
                        session.send(str, this);
                      }
                    }
                    finally {
                      setState(IDLE);
                    }
                  }
                }
              }
              catch (Exception e) {
                logger.debug("Can not process received request", e);
                setState(IDLE);
              }
              break;
            case SEND_STOP_RECORD:
              // Current State: PENDING_I
              // Event: User service terminated
              // Action: Store stop record
              // New State: PENDING_I
              if (context != null) {
                Request str = createSessionTermRequest();
                context.disconnectUserOrDev(this, str);
                storeToBuffer(str);
              }
              break;
          }
          break;
        }
        // PendingE ==========
        case PENDING_EVENT: {
          switch ((Event.Type) event.getType()) {
            case RECEIVED_RECORD:
              // Current State: PENDING_E
              // Event: Successful accounting event answer received
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
            case FAILED_SEND_RECORD:
              if (checkBufferSpace()) {
                // Current State: PENDING_E
                // Event: Failure to send and buffer space available
                // Action: Store event record
                // New State: IDLE
                AccountRequest data = (AccountRequest) event.getData();
                //... cast overkill...
                storeToBuffer((Request) ((AppEventImpl) data).getMessage());
              }

              // Current State: PENDING_E
              // Event: Failure to send and no buffer space available
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
            case FAILED_RECEIVE_RECORD:
              // Current State: PENDING_E
              // Event: Failed accounting event answer received
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
          }
          break;
        }
        // PendingB ==========
        case PENDING_BUFFERED: {
          switch ((Event.Type) event.getType()) {
            case RECEIVED_RECORD:
              // Current State: PENDING_B
              // Event: Successful accounting answer received
              // Action: Delete record
              // New State: IDLE
              synchronized (this) {
                storeToBuffer(null);
              }
              setState(IDLE);
              break;
              // Failure to send
            case FAILED_SEND_RECORD:
              // Current State: PENDING_B
              // Event: Failure to send
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
              // Failed accounting answer received
            case FAILED_RECEIVE_RECORD:
              // Current State: PENDING_B
              // Event: Failed accounting answer received
              // Action: Delete record
              // New State: IDLE
              synchronized (this) {
                storeToBuffer(null);
              }
              setState(IDLE);
              break;
          }
          break;
        }
        // PendingL ==========
        case PENDING_CLOSE: {
          switch ((Event.Type) event.getType()) {
            case RECEIVED_RECORD:
              // Current State: PENDING_L
              // Event: Successful accounting stop answer received
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
            case FAILED_SEND_RECORD:
              if (checkBufferSpace()) {
                // Current State: PENDING_L
                // Event: Failure to send and buffer space available
                // Action: Store stop record
                // New State: IDLE
                AccountRequest data = (AccountRequest) event.getData();
                //... cast overkill...
                storeToBuffer((Request) ((AppEventImpl) data).getMessage());
              }
              // Current State: PENDING_L
              // Event: Failure to send and no buffer space available
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
              // Failed accounting stop answer received
            case FAILED_RECEIVE_RECORD:
              // Current State: PENDING_L
              // Event: Failed accounting stop answer received
              // Action: -
              // New State: IDLE
              setState(IDLE);
              break;
          }
          break;
        }
      }
      ClientAccSessionState state = sessionData.getClientAccSessionState();
      // Post processing
      if (oldState != state) {
        switch (state) {
          // IDLE ===========
          case IDLE:
          {
            // Current State: IDLE
            // Event: Records in storage
            // Action: Send record
            // New State: PENDING_B
            try {
              synchronized (this) {
                if (!checkBufferSpace()) {
                  session.send(sessionData.getBuffer(), this);
                  setState(PENDING_BUFFERED);
                }
              }
            }
            catch (Exception e) {
              logger.debug("can not send buffered message", e);
              synchronized (this) {
                if (context != null && !checkBufferSpace()) {
                  if (!context.failedSendRecord(this, sessionData.getBuffer())) {
                    storeToBuffer(null);
                  }
                }
              }
            }
          }
        }
      }
    }
    catch (Throwable t) {
      throw new InternalException(t);
    }
    return true;
  }

  protected void processInterimIntervalAvp(StateEvent event) throws InternalException {
    //    Avp interval = ((AppEvent) event.getData()).getMessage().getAvps().getAvp(Avp.ACCT_INTERIM_INTERVAL);
    //    if (interval != null) {
    //      // create timer
    //      try {
    //        long v = interval.getUnsigned32();
    //        if (v != 0) {
    //          //          scheduler.schedule(
    //          //              new Runnable() {
    //          //                public void run() {
    //          //                  if (context != null) {
    //          //                    try {
    //          //                      Request interimRecord = createInterimRecord();
    //          //                      context.interimIntervalElapses(interimRecord);
    //          //                      sendAndStateLock.lock();
    //          //                      session.send(interimRecord, ClientAccSessionImpl.this);
    //          //                      setState(PENDING_INTERIM);
    //          //                    }
    //          //                    catch (Exception e) {
    //          //                      logger.debug("Can not process Interim Interval AVP", e);
    //          //                    }
    //          //                    finally {
    //          //                      sendAndStateLock.unlock();
    //          //                    }
    //          //                  }
    //          //                }
    //          //              },
    //          //              v, TimeUnit.SECONDS
    //          //          );
    //          cancelInterimTimer();
    //          this.timerId_interim = startInterimTimer(v);
    //        }
    //      }
    //      catch (AvpDataException e) {
    //        logger.debug("Unable to retrieve Acct-Interim-Interval AVP value", e);
    //      }
    //    }
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else if (timerName.equals(TIMER_NAME_INTERIM)) {
      if (context != null) {
        try {
          Request interimRecord = createInterimRecord();
          context.interimIntervalElapses(this, interimRecord);
          sendAndStateLock.lock();
          session.send(interimRecord, ClientAccSessionImpl.this);
          setState(PENDING_INTERIM);
          sessionData.setInterimTimerId(null);
        }
        catch (Exception e) {
          logger.debug("Can not process Interim Interval AVP", e);
        }
        finally {
          sendAndStateLock.unlock();
        }
      }
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }

  private Serializable startInterimTimer(long v) {
    try {
      sendAndStateLock.lock();
      Serializable interimTimerId = super.timerFacility.schedule(sessionData.getSessionId(), TIMER_NAME_INTERIM, v);
      sessionData.setInterimTimerId(interimTimerId);

      return interimTimerId;
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  private void cancelInterimTimer() {
    try {
      sendAndStateLock.lock();
      Serializable interimTimerId = sessionData.getInterimTimerId();
      if (interimTimerId != null) {
        super.timerFacility.cancel(interimTimerId);
        sessionData.setInterimTimerId(null);
      }
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> eClass) {
    return eClass == ClientAccSessionState.class ? (E) sessionData.getClientAccSessionState() : null;
  }

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    if (request.getCommandCode() == AccountRequest.code) {
      // state should be changed before event listener call
      try {
        sendAndStateLock.lock();
        handleEvent(new Event(createAccountAnswer(answer)));
      }
      catch (Exception e) {
        logger.debug("Can not process received request", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
      try {
        listener.doAccAnswerEvent(this, createAccountRequest(request), createAccountAnswer(answer));
      }
      catch (Exception e) {
        logger.debug("Unable to deliver message to listener.", e);
      }
    }
    else {
      try {
        listener.doOtherEvent(this, createAccountRequest(request), createAccountAnswer(answer));
      }
      catch (Exception e) {
        logger.debug("Can not process received request", e);
      }
    }
  }

  @Override
  public void timeoutExpired(Request request) {
    try {
      sendAndStateLock.lock();
      handleEvent(new Event(Event.Type.FAILED_SEND_RECORD, createAccountRequest(request)));
    }
    catch (Exception e) {
      logger.debug("Can not handle timeout event", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  @Override
  public Answer processRequest(Request request) {
    Answer a = request.createAnswer(5001);
    return a;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }

  protected Request createInterimRecord() {
    Request interimRecord = session.createRequest(AccountRequest.code, sessionData.getApplicationId(), sessionData.getDestinationRealm(),
        sessionData.getDestinationHost());
    interimRecord.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 3);
    return interimRecord;
  }

  protected Request createSessionTermRequest() {
    return session.createRequest(Message.SESSION_TERMINATION_REQUEST, sessionData.getApplicationId(), sessionData.getDestinationRealm(),
        sessionData.getDestinationHost());
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
    ClientAccSessionImpl other = (ClientAccSessionImpl) obj;
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

}