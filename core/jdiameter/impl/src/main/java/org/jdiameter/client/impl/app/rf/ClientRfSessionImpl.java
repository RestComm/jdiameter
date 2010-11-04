/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.client.impl.app.rf;

import static org.jdiameter.api.Avp.ACCOUNTING_REALTIME_REQUIRED;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.IDLE;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.OPEN;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.PENDING_BUFFERED;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.PENDING_CLOSE;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.PENDING_EVENT;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.PENDING_INTERIM;
import static org.jdiameter.common.api.app.rf.ClientRfSessionState.PENDING_START;

import java.io.Serializable;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.rf.ClientRfSession;
import org.jdiameter.api.rf.ClientRfSessionListener;
import org.jdiameter.api.rf.events.RfAccountingRequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.rf.ClientRfSessionState;
import org.jdiameter.common.api.app.rf.IClientRfActionContext;
import org.jdiameter.common.api.app.rf.IRfSessionFactory;
import org.jdiameter.common.impl.app.rf.AppRfSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Accounting session implementation
 * 
 *  
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientRfSessionImpl extends AppRfSessionImpl implements EventListener<Request, Answer>, ClientRfSession {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(ClientRfSessionImpl.class);

  // Constants ----------------------------------------------------------------
  public static final int DELIVER_AND_GRANT = 1;
  public static final int GRANT_AND_LOSE = 3;

  // Session State Handling ---------------------------------------------------
  protected ClientRfSessionState state = IDLE;

  // Factories and Listeners --------------------------------------------------
  protected transient IClientRfActionContext context;
  protected transient ClientRfSessionListener listener;

  protected Serializable timerId_interim;
  protected static final String TIMER_NAME_INTERIM = "CLIENT_INTERIM";

  protected String destHost, destRealm;
  protected AppEvent buffer;

  public ClientRfSessionImpl(String sessionId, ISessionFactory sessionFactory,ClientRfSessionListener clientAccSessionListener, IClientRfActionContext iClientRfActionContext,
      StateChangeListener<AppSession> stateChangeListener, ApplicationId applicationId) {
    super(sessionFactory,sessionId);
    appId = applicationId;
    listener = clientAccSessionListener;
    this.context = iClientRfActionContext;
    super.addStateChangeNotification(stateChangeListener);
  }

  public void sendAccountRequest(RfAccountingRequest accountRequest) throws InternalException, IllegalStateException, RouteException, OverloadException {
    try {
      sendAndStateLock.lock();
      handleEvent(new Event(accountRequest));
      try {
        session.send(accountRequest.getMessage(), this);
        // Store last destination information
        destRealm = accountRequest.getMessage().getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
        Avp destHostAvp = accountRequest.getMessage().getAvps().getAvp(Avp.DESTINATION_HOST);
        if(destHostAvp != null) {
          destHost = destHostAvp.getOctetString();
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

  protected synchronized void storeToBuffer(RfAccountingRequest accountRequest) {
    buffer = accountRequest;
    super.sessionDataSource.updateSession(this);
  }

  protected synchronized boolean checkBufferSpace() {
    return buffer == null;
  }

  @SuppressWarnings("unchecked")
  protected void setState(IAppSessionState newState) {
    IAppSessionState oldState = state;
    state = (ClientRfSessionState) newState;
    super.sessionDataSource.updateSession(this);
    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  public boolean isStateless() {
    return false;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {

    ClientRfSessionState oldState = state;

    try {
      switch (state) {
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
          throw new IllegalStateException("Current state " + state + " action " + event.getType());
        }
        break;
      }
      // PendingS ==========
      case PENDING_START: {
        switch ((Event.Type) event.getType()) {
        case FAILED_SEND_RECORD:
          RfAccountingRequest request = (RfAccountingRequest) event.getData();
          Avp accRtReq = request.getMessage().getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);

          // Current State: PENDING_S
          // Event: Failure to send and buffer space available and realtime not equal to DELIVER_AND_GRANT
          // Action: Store Start Record
          // New State: OPEN
          if (checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != DELIVER_AND_GRANT) {
            storeToBuffer(request);
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
                sendAndStateLock.lock();
                if (context != null) {
                  Request str = createSessionTermRequest();
                  context.disconnectUserOrDev(this,str);
                  session.send(str, this);
                }
                setState(IDLE);
                sendAndStateLock.unlock();
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
        	  RfAccountingRequest answer = (RfAccountingRequest) event.getData();
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
                sendAndStateLock.lock();
                if (context != null) {
                  Request str = createSessionTermRequest();
                  context.disconnectUserOrDev(this,str);
                  session.send(str, this);
                }
                setState(IDLE);
                sendAndStateLock.unlock();
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
            context.disconnectUserOrDev(this,str);
            storeToBuffer(createAccountRequest(str));
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
        	RfAccountingRequest request = (RfAccountingRequest) event.getData();
          Avp accRtReq = ((Message) event.getData()).getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);

          // Current State: PENDING_I
          // Event: Failure to send and buffer space available (or old record interim can be overwritten) and realtime not equal to DELIVER_AND_GRANT
          // Action: Store interim record
          // New State: OPEN
          if (checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != DELIVER_AND_GRANT) {
            storeToBuffer(request);
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
                sendAndStateLock.lock();
                if (context != null) {
                  Request str = createSessionTermRequest();
                  context.disconnectUserOrDev(this,str);
                  session.send(str, this);
                }
                setState(IDLE);
                sendAndStateLock.unlock();
              }
            }
          }
          break;
        case FAILED_RECEIVE_RECORD:
          try {
        	  RfAccountingRequest answer = (RfAccountingRequest) event.getData();
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
                sendAndStateLock.lock();
                if (context != null) {
                  Request str = createSessionTermRequest();
                  context.disconnectUserOrDev(this,str);
                  session.send(str, this);
                }
                setState(IDLE);
                sendAndStateLock.unlock();
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
            context.disconnectUserOrDev(this,str);
            storeToBuffer(createAccountRequest(str));
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
        	  RfAccountingRequest data = (RfAccountingRequest) event.getData();
            storeToBuffer(data);
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
        	  RfAccountingRequest data = (RfAccountingRequest) event.getData();
            storeToBuffer(data);
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
              if (buffer != null) {
                session.send(buffer.getMessage(), this);
                setState(PENDING_BUFFERED);
              }
            }
          }
          catch (Exception e) {
            logger.debug("can not send buffered message", e);
            synchronized (this) {
              if (context != null && buffer != null) {
                if (!context.failedSendRecord(this,(Request) buffer.getMessage())) {
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
//          //                      session.send(interimRecord, ClientRfSessionImpl.this);
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
    if(timerName.equals(TIMER_NAME_INTERIM)) {
      if (context != null) {
        try {
          Request interimRecord = createInterimRecord();
          context.interimIntervalElapses(this,interimRecord);
          sendAndStateLock.lock();
          session.send(interimRecord, ClientRfSessionImpl.this);
          setState(PENDING_INTERIM);
          this.timerId_interim = null;
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
      super.onTimer(timerName);
    }
  }

  private Serializable startInterimTimer(long v) {
    try{
      sendAndStateLock.lock();
      this.timerId_interim = super.timerFacility.schedule(sessionId, TIMER_NAME_INTERIM, v);
      super.sessionDataSource.updateSession(this);
      return this.timerId_interim;
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  private void cancelInterimTimer() {
    try{
      sendAndStateLock.lock();
      if(this.timerId_interim != null) {
        super.timerFacility.cancel(timerId_interim);
        this.timerId_interim = null;
        super.sessionDataSource.updateSession(this);
      }
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> eClass) {
    return eClass == ClientRfSessionState.class ? (E) state : null;
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    if (request.getCommandCode() == RfAccountingRequest.code) {
      //FIXME: any reason for this to be after handle?
      try {
        listener.doRfAccountingAnswerEvent(this, createAccountRequest(request), createAccountAnswer(answer));
      }
      catch (Exception e) {
        logger.debug("Unable to deliver message to listener.", e);
      }	
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

  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.FAILED_RECEIVE_RECORD, createAccountRequest(request)));
    }
    catch (Exception e) {
      logger.debug("Can not handle timeout event", e);
    }
  }

  public Answer processRequest(Request request) {
    if (request.getCommandCode() == RfAccountingRequest.code) {
      try {
        // FIXME Is this wrong?
        listener.doRfAccountingAnswerEvent(this, createAccountRequest(request), null);
      }
      catch (Exception e) {
        logger.debug("Can not process received request", e);
      }
    }
    else {
      try {
        listener.doOtherEvent(this, createAccountRequest(request), null);
      }
      catch (Exception e) {
        logger.debug("Can not process received request", e);
      }
    }
    return null;
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
  @Override
  public void relink(IContainer stack) {
    if(super.sf == null) {
      super.relink(stack);
      IRfSessionFactory fct = (IRfSessionFactory) ((ISessionFactory) super.sf).getAppSessionFactory(ClientRfSession.class);

      this.listener = fct.getClientSessionListener();
      this.context = fct.getClientContextListener();
    }
  }

  protected Request createInterimRecord() {
    Request interimRecord = session.createRequest(RfAccountingRequest.code, appId, destRealm, destHost);
    interimRecord.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 3);
    return interimRecord;
  }

  protected Request createSessionTermRequest() {
    return session.createRequest(Message.SESSION_TERMINATION_REQUEST, appId, destRealm, destHost);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
    result = prime * result + ((destHost == null) ? 0 : destHost.hashCode());
    result = prime * result + ((destRealm == null) ? 0 : destRealm.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((timerId_interim == null) ? 0 : timerId_interim.hashCode());
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

    ClientRfSessionImpl other = (ClientRfSessionImpl) obj;
    if (buffer == null) {
      if (other.buffer != null)
        return false;
    }
    else if (!buffer.equals(other.buffer)) {
      return false;
    }
    if (destHost == null) {
      if (other.destHost != null) {
        return false;
      }
    }
    else if (!destHost.equals(other.destHost)) {
      return false;
    }
    if (destRealm == null) {
      if (other.destRealm != null) {
        return false;
      }
    }
    else if (!destRealm.equals(other.destRealm)) {
      return false;
    }
    if (state == null) {
      if (other.state != null) {
        return false;
      }
    }
    else if (!state.equals(other.state)) {
      return false;
    }
    if (timerId_interim == null) {
      if (other.timerId_interim != null) {
        return false;
      }
    }
    else if (!timerId_interim.equals(other.timerId_interim)) {
      return false;
    }
    return true;
  }

}