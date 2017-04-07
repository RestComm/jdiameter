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

package org.jdiameter.server.impl.app.acc;

import static org.jdiameter.common.api.app.acc.ServerAccSessionState.IDLE;
import static org.jdiameter.common.api.app.acc.ServerAccSessionState.OPEN;

import java.io.Serializable;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.acc.IServerAccActionContext;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;
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

  private static final Logger logger = LoggerFactory.getLogger(ServerAccSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  protected transient IServerAccActionContext context;
  protected transient  ServerAccSessionListener listener;

  // Ts Timer -----------------------------------------------------------------
  protected static final String TIMER_NAME_TS = "TS";

  protected IServerAccSessionData sessionData;

  // Constructors -------------------------------------------------------------
  public ServerAccSessionImpl(IServerAccSessionData sessionData, ISessionFactory sessionFactory,
      ServerAccSessionListener serverSessionListener,
      IServerAccActionContext serverContextListener, StateChangeListener<AppSession> stLst, boolean stateless) {
    // TODO Auto-generated constructor stub
    this(sessionData, sessionFactory, serverSessionListener, serverContextListener, stLst);

    this.sessionData.setTsTimeout(0); // 0 == turn off
    this.sessionData.setStateless(stateless);

  }

  public ServerAccSessionImpl(IServerAccSessionData sessionData, ISessionFactory sessionFactory, ServerAccSessionListener serverSessionListener,
      IServerAccActionContext serverContextListener, StateChangeListener<AppSession> stLst) {
    // TODO Auto-generated constructor stub
    super(sessionFactory, sessionData);
    this.sessionData = sessionData;
    this.listener = serverSessionListener;
    this.context = serverContextListener;

    super.addStateChangeNotification(stLst);
  }

  @Override
  public void sendAccountAnswer(AccountAnswer accountAnswer) throws InternalException, IllegalStateException, RouteException, OverloadException {
    try {
      AvpSet avpSet = accountAnswer.getMessage().getAvps();
      Avp acctInterimIntervalAvp = avpSet.getAvp(Avp.ACCT_INTERIM_INTERVAL); //Unsigned32
      if (acctInterimIntervalAvp != null) {
        try {
          this.sessionData.setTsTimeout(acctInterimIntervalAvp.getUnsigned32());
        }
        catch (AvpDataException e) {
          throw new InternalException(e);
        }
      }
      cancelTsTimer();
      startTsTimer();
      session.send(accountAnswer.getMessage());
      /* TODO: Do we need to notify state change ? */
      if (isStateless() && isValid()) {
        session.release();
      }
    }
    catch (IllegalDiameterStateException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean isStateless() {
    return sessionData.isStateless();
  }

  @SuppressWarnings("unchecked")
  protected void setState(IAppSessionState newState) {
    IAppSessionState oldState = sessionData.getServerAccSessionState();
    sessionData.setServerAccSessionState((ServerAccSessionState) newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this,(Enum) oldState, (Enum) newState);
    }
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    return sessionData.isStateless() ? handleEventForStatelessMode(event) : handleEventForStatefulMode(event);
  }

  public boolean handleEventForStatelessMode(StateEvent event) throws InternalException, OverloadException {
    try {
      //NOTE: NO Ts Timer here
      //this will handle RTRs as well, no need to alter.
      ServerAccSessionState state = sessionData.getServerAccSessionState();
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
        try {
          cancelTsTimer();
          startTsTimer();
          setState(OPEN);
          listener.doAccRequestEvent(this, (AccountRequest) event.getData());

          if (context != null) {
            context.sessionTimerStarted(this, null);
          }
        }
        catch (Exception e) {
          logger.debug("Can not handle event", e);
          setState(IDLE);
        }
        return true;
      }
      else {
        ServerAccSessionState state = sessionData.getServerAccSessionState();
        AccountRequest request =  (AccountRequest) event.getData();
        AvpSet avpSet = request.getMessage().getAvps();
        Avp acctInterimIntervalAvp = avpSet.getAvp(85);//Unsigned32
        if (acctInterimIntervalAvp != null) {
          this.sessionData.setTsTimeout(acctInterimIntervalAvp.getUnsigned32());
        }
        switch (state) {
          case IDLE: {
            switch ((Event.Type) event.getType()) {
              case RECEIVED_START_RECORD:
                // Current State: IDLE
                // Event: Accounting start request received, and successfully processed.
                // Action: Send accounting start answer, Start Ts
                // New State: OPEN
                setState(OPEN);
                if (listener != null) {
                  try {
                    listener.doAccRequestEvent(this, (AccountRequest) event.getData());
                    cancelTsTimer();
                    startTsTimer();
                    if (context != null) {
                      context.sessionTimerStarted(this, null);
                    }
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
                  startTsTimer();
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
                  setState(IDLE);
                  cancelTsTimer();
                  listener.doAccRequestEvent(this, (AccountRequest) event.getData());
                  if (context != null) {
                    context.sessionTimerCanceled(this, null);
                  }
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

  private void startTsTimer() {

    try {
      sendAndStateLock.lock();
      if (sessionData.getTsTimeout() > 0) {
        Serializable tsTid = super.timerFacility.schedule(sessionData.getSessionId(), TIMER_NAME_TS, sessionData.getTsTimeout());
        sessionData.setTsTimerId(tsTid);
      }
      return;
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  private void cancelTsTimer() {
    try {
      sendAndStateLock.lock();
      Serializable tsTid = sessionData.getTsTimerId();
      if (tsTid != null) {
        super.timerFacility.cancel(tsTid);
        sessionData.setTsTimerId(null);
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
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else if (timerName.equals(TIMER_NAME_TS)) {
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
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
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

  @Override
  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> eClass) {
    return eClass == ServerAccSessionState.class ? (E) sessionData.getServerAccSessionState() : null;
  }

  @Override
  public Answer processRequest(Request request) {
    if (request.getCommandCode() == AccountRequest.code) {
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

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    if (request.getCommandCode() == AccountRequest.code) {
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

  @Override
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
