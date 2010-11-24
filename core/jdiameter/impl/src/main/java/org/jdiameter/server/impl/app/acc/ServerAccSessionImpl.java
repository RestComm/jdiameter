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
package org.jdiameter.server.impl.app.acc;

import static org.jdiameter.common.api.app.acc.ServerAccSessionState.IDLE;
import static org.jdiameter.common.api.app.acc.ServerAccSessionState.OPEN;

import java.io.Serializable;

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
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.acc.IAccSessionFactory;
import org.jdiameter.common.api.app.acc.IServerAccActionContext;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.jdiameter.common.impl.app.acc.AppAccSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server Accounting session implementation
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerAccSessionImpl extends AppAccSessionImpl implements EventListener<Request, Answer>, ServerAccSession, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(ServerAccSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected boolean stateless = false;
  protected ServerAccSessionState state = ServerAccSessionState.IDLE;

  // Factories and Listeners --------------------------------------------------
  protected transient IServerAccActionContext context;
  protected transient  ServerAccSessionListener listener;

  // Ts Timer -----------------------------------------------------------------
  protected long tsTimeout;
  //protected ScheduledFuture tsTask;
  protected Serializable timerId_ts;
  protected static final String TIMER_NAME_TS = "TS";

  // Constructors -------------------------------------------------------------
  public ServerAccSessionImpl(String sessionId, SessionFactory sessionFactory, Request request,
      ServerAccSessionListener serverSessionListener, 
      IServerAccActionContext serverContextListener,StateChangeListener<AppSession> stLst, long tsTimeout, boolean stateless) {
    // TODO Auto-generated constructor stub
    super(sessionFactory,sessionId);
    this.listener = serverSessionListener;
    this.context = serverContextListener;
    this.tsTimeout = tsTimeout;
    this.stateless = stateless;
    super.addStateChangeNotification(stLst);
  }

  public void sendAccountAnswer(AccountAnswer accountAnswer) throws InternalException, IllegalStateException, RouteException, OverloadException {
    try {
      session.send(accountAnswer.getMessage());
      /* TODO: Do we need to notify state change ? */
      if(isStateless() && isValid()) {
        session.release();
      }
    }
    catch (IllegalDiameterStateException e) {
      throw new IllegalStateException(e);
    }
  }

  public boolean isStateless() {
    return stateless;
  }

  @SuppressWarnings("unchecked")
  protected void setState(IAppSessionState newState) {
    IAppSessionState oldState = state;
    state = (ServerAccSessionState) newState;
    super.sessionDataSource.updateSession(this);
    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return stateless ? handleEventForStatelessMode(event) : handleEventForStatefulMode(event);
  }

  public boolean handleEventForStatelessMode(StateEvent event) throws InternalException, OverloadException {
    try {
      //this will handle RTRs as well, no need to alter.
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
          // FIXME: it is required, so we know it ends up again in IDLE!
          setState(IDLE);
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
      // We can't release here, answer needs to be sent through. done at send.
      // release();
    }
    return true;
  }

  public boolean handleEventForStatefulMode(StateEvent event) throws InternalException, OverloadException {
    try {
      if (((AccountRequest) event.getData()).getMessage().isReTransmitted()) {
        // FIXME: Alex is this ok?
        try {
          listener.doAccRequestEvent(this, (AccountRequest) event.getData());
          // FIXME: should we do this before passing to lst?
          cancelTsTimer();
          timerId_ts = startTsTimer();
          if (context != null) {
            context.sessionTimerStarted(this, null);
          }
          setState(OPEN);
        }
        catch (Exception e) {
          logger.debug("Can not handle event", e);
          setState(IDLE);
        }
        return true;
      }
      else {
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
                cancelTsTimer();
                timerId_ts = startTsTimer();
                if (context != null) {
                  context.sessionTimerStarted(this, null);
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
            // Event: Accounting event request received, and
            // successfully processed.
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
            // Event: Interim record received, and successfully
            // processed.
            // Action: Send accounting interim answer, Restart Ts
            // New State: OPEN
            try {
              listener.doAccRequestEvent(this, (AccountRequest) event.getData());
              cancelTsTimer();
              timerId_ts = startTsTimer();
              if (context != null) {
                context.sessionTimerStarted(this, null);
              }
            }
            catch (Exception e) {
              logger.debug("Can not handle event", e);
              setState(IDLE);
            }
            break;
          case RECEIVED_STOP_RECORD:
            // Current State: OPEN
            // Event: Accounting stop request received, and
            // successfully
            // processed
            // Action: Send accounting stop answer, Stop Ts
            // New State: IDLE
            try {
              listener.doAccRequestEvent(this,
                  (AccountRequest) event.getData());
              cancelTsTimer();
              if (context != null) {
                context.sessionTimerCanceled(this, null);
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
    }
    catch (Exception e) {
      logger.debug("Can not process event", e);
      return false;
    }
    return true;
  }

  private Serializable startTsTimer() {
    //    return scheduler.schedule(new Runnable() {
    //      public void run() {
    //        logger.debug("Ts timer expired");
    //        if (context != null) {
    //          try {
    //            context.sessionTimeoutElapses(ServerAccSessionImpl.this);
    //          }
    //          catch (InternalException e) {
    //            logger.debug("Failure on processing expired Ts", e);
    //          }
    //        }
    //        setState(IDLE);
    //      }
    //    }, tsTimeout, TimeUnit.MILLISECONDS);
    try{
      sendAndStateLock.lock();
      this.timerId_ts = super.timerFacility.schedule(sessionId, TIMER_NAME_TS, tsTimeout);
      super.sessionDataSource.updateSession(this);
      return this.timerId_ts;
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  private void cancelTsTimer() {
    try{
      sendAndStateLock.lock();
      if(this.timerId_ts != null) {
        super.timerFacility.cancel(timerId_ts);
        this.timerId_ts = null;
        super.sessionDataSource.updateSession(this);
      }
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#onTimer(java.lang.String)
   */
  @Override
  public void onTimer(String timerName) {
    if(timerName.equals(TIMER_NAME_TS)) {
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
    else {
      super.onTimer(timerName);
    }
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

  @SuppressWarnings("unchecked")
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

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }
  /* (non-Javadoc)
   * 
   * @see org.jdiameter.common.impl.app.AppSessionImpl#relink(org.jdiameter.client.api.IContainer)
   */
  @Override
  public void relink(IContainer stack) {
    if(super.sf == null) {
      super.relink(stack);
      IAccSessionFactory fct = (IAccSessionFactory) ((ISessionFactory) super.sf).getAppSessionFactory(ServerAccSession.class);

      this.listener = fct.getServerSessionListener();
      this.context = fct.getServerContextListener();

    }
  }

}
