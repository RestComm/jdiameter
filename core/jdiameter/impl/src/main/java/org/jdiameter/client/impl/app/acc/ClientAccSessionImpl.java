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
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.jdiameter.common.impl.app.acc.AppAccSessionImpl;

/**
 * Client Accounting session implementation
 */
public class ClientAccSessionImpl extends AppAccSessionImpl implements EventListener<Request, Answer>, ClientAccSession {

  private static final long serialVersionUID = 1L;

  public static final int DELIVER_AND_GRANT = 1;
  public static final int GRANT_AND_LOSE = 3;

  protected ClientAccSessionState state = IDLE;
  protected String destHost, destRealm;
  protected IClientAccActionContext context;
  protected AppEvent buffer;
  protected ClientAccSessionListener listener;


  public ClientAccSessionImpl(SessionFactory sf, ClientAccSessionListener lst, ApplicationId app) {
    if (lst == null)
      throw new IllegalArgumentException("Listener can not be null");
    if (app == null)
      throw new IllegalArgumentException("ApplicationId can not be null");
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
        // Store last destinmation information
        destRealm = accountRequest.getMessage().getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
        destHost = accountRequest.getMessage().getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString();
      }
      catch (Throwable t) {
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
      i.stateChanged( (Enum) oldState, (Enum) newState);
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
          setState(PENDING_START);
          break;
          // Client or device requests a one-time service
        case SEND_EVENT_RECORD:
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
          // Failure to send and buffer space available and realtime not equal to DELIVER_AND_GRANT
          if (checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != DELIVER_AND_GRANT) {
            storeToBuffer(request);
            setState(OPEN);
          }
          else {
            // Failure to send and no buffer space available and realtime equal to GRANT_AND_LOSE
            if (!checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
              setState(OPEN);
            }
            else {
              // Failure to send and no buffer space available and realtime not equal to GRANT_AND_LOSE
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
          // Successful accounting start answer received
        case RECEIVED_RECORD:
          processInterimIntervalAvp(event);
          setState(OPEN);
          break;
        case FAILED_RECEIVE_RECORD:
          try {
            AccountAnswer answer = (AccountAnswer) event.getData();
            accRtReq = answer.getMessage().getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);
            // Failed accounting start answer received and realtime equal to GRANT_AND_LOSE
            if (accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
              setState(OPEN);
            }
            else {
              // Failed accounting start answer received and realtime not equal to GRANT_AND_LOSE
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
            logger.debug("Can not processed answer", e);
            setState(IDLE);
          }
          break;
          // User service terminated
        case SEND_STOP_RECORD:
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
          setState(PENDING_CLOSE);
          break;
          // Create timer for "Interim interval elapses" event
        case RECEIVED_RECORD:
          processInterimIntervalAvp(event);
          break;
        }
      }
      break;
      // PendingI ==========
      case PENDING_INTERIM: {
        switch ((Event.Type) event.getType()) {
        // Successful accounting interim answer received
        case RECEIVED_RECORD:
          processInterimIntervalAvp(event);
          setState(OPEN);
          break;

        case FAILED_SEND_RECORD:
          AccountRequest request = (AccountRequest) event.getData();
          Avp accRtReq = ((Message) event.getData()).getAvps().getAvp(ACCOUNTING_REALTIME_REQUIRED);
          // Failure to send and realtime not equal to DELIVER_AND_GRANT
          if (checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() != DELIVER_AND_GRANT) {
            storeToBuffer(request);
            setState(OPEN);
          }
          else {
            // Failure to send and no buffer space available and realtime equal to GRANT_AND_LOSE
            if (!checkBufferSpace() && accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
              setState(OPEN);
            }
            else {
              // Failure to send and no buffer space available and realtime not equal to GRANT_AND_LOSE
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
            // Failed accounting interim answer received and realtime equal to GRANT_AND_LOSE
            if (accRtReq != null && accRtReq.getInteger32() == GRANT_AND_LOSE) {
              setState(OPEN);
            }
            else {
              // Failed accounting interim answer received and realtime not equal to GRANT_AND_LOSE
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
            logger.debug(e.getMessage(), e);
            setState(IDLE);
          }
          break;
          // User service terminated
        case SEND_STOP_RECORD:
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
        // Successful accounting event answer received
        case RECEIVED_RECORD:
          setState(IDLE);
          break;
        case FAILED_SEND_RECORD:
          if (checkBufferSpace()) {
            // Failure to send and buffer space available
            AccountRequest data = (AccountRequest) event.getData();
            storeToBuffer(data);
          }
          setState(IDLE);
          break;
          // Failed accounting event answer received
        case FAILED_RECEIVE_RECORD:
          setState(IDLE);
          break;
        }
        break;
      }
      // PendingB ==========
      case PENDING_BUFFERED: {
        switch ((Event.Type) event.getType()) {
        // Successful accounting answer received
        case RECEIVED_RECORD:
          synchronized (this) {
            storeToBuffer(null);
          }
          setState(IDLE);
          break;
          // Failure to send
        case FAILED_SEND_RECORD:
          setState(IDLE);
          break;
          // Failed accounting answer received
        case FAILED_RECEIVE_RECORD:
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
        // Successful accounting  stop answer received
        case RECEIVED_RECORD:
          setState(IDLE);
          break;
        case FAILED_SEND_RECORD:
          if (checkBufferSpace()) {
            // Failure to send and buffer space available
            AccountRequest data = (AccountRequest) event.getData();
            storeToBuffer(data);
          }
          setState(IDLE);
          break;
          // Failed accounting stop answer received
        case FAILED_RECEIVE_RECORD:
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
          // Records in storage
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
              if (!context.failedSendRecord((Request) buffer.getMessage())) {
                storeToBuffer(null);
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
                      logger.debug(e.getMessage(), e);
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
        logger.debug(e.getMessage(), e);
      }
    }
  }

  public <E> E getState(Class<E> eClass) {
    return eClass == ClientAccSessionState.class ? (E) state : null;
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    if (request.getCommandCode() == AccountRequestImpl.code) {
      try {
        sendAndStateLock.lock();
        handleEvent(new Event(createAccountAnswer(answer)));
      }
      catch (Exception e) {
        logger.debug(e.getMessage(), e);
      }
      finally {
        sendAndStateLock.unlock();
      }
      try {
        listener.doAccAnswerEvent(this, createAccountRequest(request), createAccountAnswer(answer));
      }
      catch (Exception e) {
        logger.debug(e.getMessage(), e);
      }
    }
    else {
      try {
        listener.doOtherEvent(this, createAccountRequest(request), createAccountAnswer(answer));
      }
      catch (Exception e) {
        logger.debug(e.getMessage(), e);
      }
    }
  }

  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.FAILED_RECEIVE_RECORD, createAccountRequest(request)));
    }
    catch (Exception e) {
      logger.debug(e.getMessage(), e);
    }
  }

  public Answer processRequest(Request request) {
    if (request.getCommandCode() == AccountRequestImpl.code) {
      try {
        listener.doAccAnswerEvent(this, createAccountRequest(request), null);
      }
      catch (Exception e) {
        logger.debug(e.getMessage(), e);
      }
    }
    else {
      try {
        listener.doOtherEvent(this, createAccountRequest(request), null);
      }
      catch (Exception e) {
        logger.debug(e.getMessage(), e);
      }
    }
    return null;
  }

  protected Request createInterimRecord() {
    Request interimRecord = session.createRequest(AccountRequestImpl.code, appId, destRealm, destHost);
    interimRecord.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 3);
    return interimRecord;
  }

  protected Request createSessionTermRequest() {
    return session.createRequest(Message.SESSION_TERMINATION_REQUEST, appId, destRealm, destHost);
  }
}