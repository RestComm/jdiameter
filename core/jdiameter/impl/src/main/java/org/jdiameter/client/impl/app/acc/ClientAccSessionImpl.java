/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
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

import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;
import org.jdiameter.common.api.app.acc.IClientAccActionContext;
import org.jdiameter.common.impl.app.acc.AppAccSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Accounting session implementation
 */
public class ClientAccSessionImpl extends AppAccSessionImpl implements EventListener<Request, Answer>, ClientAccSession {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(ClientAccSessionImpl.class);

  // Constants ----------------------------------------------------------------
  public static final int DELIVER_AND_GRANT = 1;
  public static final int GRANT_AND_LOSE = 3;

  // Session State Handling ---------------------------------------------------
  protected ClientAccSessionState state = IDLE;

  // Factories and Listeners --------------------------------------------------
  protected IClientAccActionContext context;
  protected ClientAccSessionListener listener;

  protected String destHost, destRealm;
  protected AppEvent buffer;

  public ClientAccSessionImpl(SessionFactory sf, ClientAccSessionListener lst, ApplicationId app) {
    super(sf);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (app == null) {
      throw new IllegalArgumentException("ApplicationId can not be null");
    }

    appId = app;
    listener = lst;

    if (listener instanceof IClientAccActionContext) {
      context = (IClientAccActionContext) listener;
    }
    try {
      session = sf.getNewSession();
      session.setRequestListener(this);
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public ClientAccSessionImpl(SessionFactory sf, String sessionId, ClientAccSessionListener lst, ApplicationId app) {
    this(sf, lst, app);        
    try {
      session = sf.getNewSession(sessionId);
      session.setRequestListener(this);
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public void sendAccountRequest(AccountRequest accountRequest) throws InternalException, IllegalStateException, RouteException, OverloadException {
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

  protected synchronized void storeToBuffer(AccountRequest accountRequest) {
    buffer = accountRequest;
  }

  protected synchronized boolean checkBufferSpace() {
    return buffer == null;
  }

  protected void setState(IAppSessionState newState) {
    IAppSessionState oldState = state;
    state = (ClientAccSessionState) newState;
    for (StateChangeListener i : stateListeners) {
      i.stateChanged((Enum) oldState, (Enum) newState);
    }
  }

  public boolean isStateless() {
    return false;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {

    ClientAccSessionState oldState = state;

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
          AccountRequest request = (AccountRequest) event.getData();
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
                  context.disconnectUserOrDev(str);
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
                sendAndStateLock.lock();
                if (context != null) {
                  Request str = createSessionTermRequest();
                  context.disconnectUserOrDev(str);
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
            context.disconnectUserOrDev(str);
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
          AccountRequest request = (AccountRequest) event.getData();
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
                  context.disconnectUserOrDev(str);
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
                sendAndStateLock.lock();
                if (context != null) {
                  Request str = createSessionTermRequest();
                  context.disconnectUserOrDev(str);
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
            context.disconnectUserOrDev(str);
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
            AccountRequest data = (AccountRequest) event.getData();
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
            AccountRequest data = (AccountRequest) event.getData();
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
                if (!context.failedSendRecord((Request) buffer.getMessage())) {
                  storeToBuffer(null);
                }
              }
              if (!IDLE.equals(IDLE)) {
                setState(IDLE);
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
    Avp interval = ((AppEvent) event.getData()).getMessage().getAvps().getAvp(Avp.ACCT_INTERIM_INTERVAL);
    if (interval != null) {
      // create timer
      try {
        long v = interval.getUnsigned32();
        if (v != 0) {
          scheduler.schedule(
              new Runnable() {
                public void run() {
                  if (context != null) {
                    try {
                      Request interimRecord = createInterimRecord();
                      context.interimIntervalElapses(interimRecord);
                      sendAndStateLock.lock();
                      session.send(interimRecord, ClientAccSessionImpl.this);
                      setState(PENDING_INTERIM);
                    }
                    catch (Exception e) {
                      logger.debug("Can not process Interim Interval AVP", e);
                    }
                    finally {
                      sendAndStateLock.unlock();
                    }
                  }
                }
              },
              v, TimeUnit.SECONDS
          );
        }
      }
      catch (AvpDataException e) {
        logger.debug("Unable to retrieve Acct-Interim-Interval AVP value", e);
      }
    }
  }

  public <E> E getState(Class<E> eClass) {
    return eClass == ClientAccSessionState.class ? (E) state : null;
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    if (request.getCommandCode() == AccountRequest.code) {
      //FIXME: any reason for this to be after handle?
      try {
        listener.doAccAnswerEvent(this, createAccountRequest(request), createAccountAnswer(answer));
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
    if (request.getCommandCode() == AccountRequest.code) {
      try {
        listener.doAccAnswerEvent(this, createAccountRequest(request), null);
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

  protected Request createInterimRecord() {
    Request interimRecord = session.createRequest(AccountRequest.code, appId, destRealm, destHost);
    interimRecord.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 3);
    return interimRecord;
  }

  protected Request createSessionTermRequest() {
    return session.createRequest(Message.SESSION_TERMINATION_REQUEST, appId, destRealm, destHost);
  }
}