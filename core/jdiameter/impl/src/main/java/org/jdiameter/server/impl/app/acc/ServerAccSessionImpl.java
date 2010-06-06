/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.impl.app.acc;

import static org.jdiameter.common.api.app.acc.ServerAccSessionState.IDLE;
import static org.jdiameter.common.api.app.acc.ServerAccSessionState.OPEN;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.acc.IServerAccActionContext;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.jdiameter.common.impl.app.acc.AppAccSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAccSessionImpl extends AppAccSessionImpl implements EventListener<Request, Answer>, ServerAccSession, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  private Logger logger = LoggerFactory.getLogger(ServerAccSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected boolean stateless = false;
  protected ServerAccSessionState state = ServerAccSessionState.IDLE;

  // Factories and Listeners --------------------------------------------------
  protected IServerAccActionContext context;
  protected ServerAccSessionListener listener;

  // Ts Timer -----------------------------------------------------------------
  protected long tsTimeout;
  protected ScheduledFuture tsTask;

  // Constructors -------------------------------------------------------------

  public ServerAccSessionImpl(Session session,SessionFactory sf, Request initialRequest, ServerAccSessionListener listener, long tsTimeout, boolean stateless, StateChangeListener... scListeners) {
    super(sf);

    if (session == null) {
      throw new IllegalArgumentException("Session can not be null");
    }
    if (listener == null) {
      throw new IllegalArgumentException("Session listener can not be null");
    }

    this.session = session;
    this.listener = listener;
    if (this.listener instanceof IServerAccActionContext) {
      context = (IServerAccActionContext) this.listener;
    }

    this.tsTimeout = tsTimeout;
    this.stateless = stateless;
    this.session.setRequestListener(this);
    for (StateChangeListener l : scListeners) {
      addStateChangeNotification(l);
    }
  }

  public void sendAccountAnswer(AccountAnswer accountAnswer) throws InternalException, IllegalStateException, RouteException, OverloadException {
    try {
      session.send(accountAnswer.getMessage());
    }
    catch (IllegalDiameterStateException e) {
      throw new IllegalStateException(e);
    }
  }

  public boolean isStateless() {
    return stateless;
  }

  protected void setState(IAppSessionState newState) {
    IAppSessionState oldState = state;
    state = (ServerAccSessionState) newState;
    for (StateChangeListener i : stateListeners) {
      i.stateChanged((Enum) oldState, (Enum) newState);
    }
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return stateless ? handleEventForStatelessMode(event) : handleEventForStatefulMode(event);
  }

  public boolean handleEventForStatelessMode(StateEvent event) throws InternalException, OverloadException {       
    try {
      switch (state) {
      case IDLE: {
        switch ((Event.Type) event.getType()) {
        case RECEIVED_START_RECORD:
          // Current State: IDLE    
          // Event: Accounting start request received, and successfully processed.
          // Action: Send accounting start answer
          // New State: IDLE
          if (listener != null) {
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
            }
          }
          // TODO: This is unnecessary state change: setState(IDLE);
          break;
        case RECEIVED_EVENT_RECORD:
          // Current State: IDLE
          // Event: Accounting event request received, and successfully processed.
          // Action: Send accounting event answer
          // New State: IDLE
          if (listener != null) {
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
            }
          }
          // TODO: This is unnecessary state change: setState(IDLE);
          break;
        case RECEIVED_INTERIM_RECORD:
          // Current State: IDLE
          // Event: Interim record received, and successfully processed.
          // Action: Send accounting interim answer
          // New State: IDLE
          if (listener != null) {
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
            }
          }
          // TODO: This is unnecessary state change: setState(IDLE);
          break;
        case RECEIVED_STOP_RECORD:
          // Current State: IDLE
          // Event: Accounting stop request received, and successfully processed
          // Action: Send accounting stop answer
          // New State: IDLE
          if (listener != null) {
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
            }
          }
          // TODO: This is unnecessary state change: setState(IDLE);
          break;
        default:
          throw new IllegalStateException("Current state " + state + " action " + event.getType());
        }
      }
      }
    }
    catch (Exception e) {
      logger.debug("Can not process event", e);
      return false;
    }
    finally {
      // TODO: Since setState was removed, we are now using this to terminate. Correct?
      release();
    }
    return true;
  }

  public boolean handleEventForStatefulMode(StateEvent event) throws InternalException, OverloadException {
    try {
      switch (state) {
      case IDLE: {
        switch ((Event.Type) event.getType()) {
        case RECEIVED_START_RECORD:
          // Current State: IDLE
          // Event: Accounting start request received, and successfully processed.
          // Action: Send accounting start answer, Start Ts
          // New State: OPEN
          if (listener != null) {
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
              tsTask = runTsTimer();
              if (context != null) {
                context.sessionTimerStarted(this, tsTask);
              }
              setState(OPEN);
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
              setState(IDLE);
            }
          }
          break;
        case RECEIVED_EVENT_RECORD:
          // Current State: IDLE
          // Event: Accounting event request received, and successfully processed.
          // Action: Send accounting event answer
          // New State: IDLE
          if (listener != null) {
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
            }
          }                            
          break;
        }
        break;
      }
      case OPEN: {
        switch ((Event.Type) event.getType()) {
        case RECEIVED_INTERIM_RECORD:
          // Current State: OPEN
          // Event: Interim record received, and successfully processed.
          // Action: Send accounting interim answer, Restart Ts
          // New State: OPEN
          try {
            listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            tsTask = runTsTimer();
            if (context != null) {
              context.sessionTimerStarted(this, tsTask);
            }
          }
          catch (Exception e) {
            logger.debug("Can not handle event", e);
            setState(IDLE);
          }
          break;
        case RECEIVED_STOP_RECORD:
          // Current State: OPEN
          // Event: Accounting stop request received, and successfully processed
          // Action: Send accounting stop answer, Stop Ts
          // New State: IDLE
          try {
            listener.doAccRequestEvent(this, (AccountRequest) event.getData());
            tsTask.cancel(true);
            if (context != null) {
              context.srssionTimerCanceled(this, tsTask);
            }
            setState(IDLE);
          }
          catch (Exception e) {
            logger.debug("Can not handle event", e);
            setState(IDLE);
          }
          break;
        }
        break;
      }
      }
    }
    catch (Exception e) {
      logger.debug("Can not process event", e);
      return false;
    }
    return true;
  }

  private ScheduledFuture runTsTimer() {
    return scheduler.schedule(new Runnable() {
      public void run() {
        logger.debug("Ts timer expired");
        if (context != null) {
          try {
            context.sessionTimeoutElapses(ServerAccSessionImpl.this);
          }
          catch (InternalException e) {
            logger.debug("Failure on processing expired Ts", e);
          }
        }
        setState(IDLE);
      }
    }, tsTimeout, TimeUnit.MILLISECONDS);
  }

  protected Answer createStopAnswer(Request request) {
    Answer answer = request.createAnswer(ResultCode.SUCCESS);
    answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 4);
    answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
    return answer;
  }

  protected Answer createInterimAnswer(Request request) {
    Answer answer = request.createAnswer(ResultCode.SUCCESS);
    answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 3);
    answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
    return answer;
  }

  protected Answer createEventAnswer(Request request) {
    Answer answer = request.createAnswer(ResultCode.SUCCESS);
    answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 2);
    answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
    return answer;
  }

  protected Answer createStartAnswer(Request request) {
    Answer answer = request.createAnswer(ResultCode.SUCCESS);
    answer.getAvps().addAvp(Avp.ACC_RECORD_TYPE, 1);
    answer.getAvps().addAvp(request.getAvps().getAvp(Avp.ACC_RECORD_NUMBER));
    return answer;
  }

  public <E> E getState(Class<E> eClass) {
    return eClass == ServerAccSessionState.class ? (E) state : null;
  }

  public Answer processRequest(Request request) {        
    if (request.getCommandCode() == AccountRequestImpl.code) {
      try {
        sendAndStateLock.lock();
        handleEvent(new Event(createAccountRequest(request)));
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      try {
        listener.doOtherEvent(this, createAccountRequest(request), null); 
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
    }
    return null;
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    if(request.getCommandCode() == AccountRequestImpl.code) {
      try {
        sendAndStateLock.lock();
        handleEvent(new Event(createAccountRequest(request)));
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
      finally {
        sendAndStateLock.unlock();
      }

      try {
        listener.doAccRequestEvent(this, createAccountRequest(request));
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
    }
    else {
      try {
        listener.doOtherEvent(this, createAccountRequest(request), createAccountAnswer(answer));
      }
      catch (Exception e) {
        logger.debug("Can not handle event", e);
      }
    }      
  }

  public void timeoutExpired(Request request) {
    // FIXME: alexandre: We don't do anything here... are we even getting this on server?      
  }

}
