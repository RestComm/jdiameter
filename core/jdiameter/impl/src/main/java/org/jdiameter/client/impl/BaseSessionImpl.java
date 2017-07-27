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

package org.jdiameter.client.impl;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.SessionTimeOut;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.BaseSession;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IEventListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for {@link BaseSession}.
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class BaseSessionImpl implements BaseSession {

  private static final Logger logger = LoggerFactory.getLogger(BaseSessionImpl.class);

  protected final long creationTime = System.currentTimeMillis();
  protected long lastAccessedTime = creationTime;
  protected boolean isValid = true;
  protected String sessionId;

  protected long maxIdleTime = 0;

  protected transient IContainer container;
  protected transient IMessageParser parser;
  protected NetworkReqListener reqListener;

  protected Serializable istTimerId;

  @Override
  public long getCreationTime() {
    return creationTime;
  }

  @Override
  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  protected long setLastAccessTime() {
    lastAccessedTime = System.currentTimeMillis();
    if (sessionId != null) {
      maxIdleTime = container.getConfiguration().getLongValue(SessionTimeOut.ordinal(), (Long) SessionTimeOut.defValue());
      if (maxIdleTime > 0) {
        IAssembler assembler = container.getAssemblerFacility();
        ITimerFacility timerFacility = assembler.getComponentInstance(ITimerFacility.class);
        if (istTimerId != null) {
          timerFacility.cancel(istTimerId);
        }
        istTimerId = timerFacility.schedule(this.getSessionId(), IDLE_SESSION_TIMER_NAME, maxIdleTime);
      }
    }
    return lastAccessedTime;
  }

  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      if (!isValid() || (maxIdleTime > 0 && System.currentTimeMillis() - getLastAccessedTime() >= maxIdleTime)) {
        logger.debug("Terminating idle/invalid application session [{}] with SID[{}]", this, getSessionId());
        this.release();
      }
    }
  }

  @Override
  public boolean isValid() {
    return isValid;
  }

  @Override
  public String getSessionId() {
    return sessionId;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.BaseSession#isAppSession()
   */
  @Override
  public boolean isAppSession() {
    return false;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.BaseSession#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return false;
  }

  protected void genericSend(Message message, EventListener listener)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (isValid) {
      long timeOut = container.getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());
      genericSend(message, listener, timeOut, TimeUnit.MILLISECONDS);
    }
    else {
      throw new IllegalDiameterStateException("Session already released");
    }
  }

  protected void genericSend(Message aMessage, EventListener listener, long timeout, TimeUnit timeUnit)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (isValid) {
      setLastAccessTime();

      IMessage message = (IMessage) aMessage;
      IEventListener localListener = createListenerWrapper(listener);
      if (message.isRequest()) {
        message.setListener(localListener);

        // Auto set system avps
        if (message.getAvps().getAvpByIndex(0).getCode() != Avp.SESSION_ID && sessionId != null) {
          // Just to make sure it doesn't get duplicated
          message.getAvps().removeAvp(Avp.SESSION_ID);
          message.getAvps().insertAvp(0, Avp.SESSION_ID, sessionId, true, false, false);
        }
      }

      //Add Origin-Host/Realm AVPs if not present
      MessageUtility.addOriginAvps(aMessage, container.getMetaData());

      if (message.getState() != IMessage.STATE_NOT_SENT && message.getState() != IMessage.STATE_ANSWERED) {
        throw new IllegalDiameterStateException("Illegal state");
      }

      message.createTimer(container.getScheduledFacility(), timeout, timeUnit);
      try {
        container.sendMessage(message);
      }
      catch (RouteException e) {
        message.clearTimer();
        throw e;
      }
      catch (Exception e) {
        message.clearTimer();
        throw new InternalException(e);
      }
    }
    else {
      throw new IllegalDiameterStateException("Session already released");
    }
  }

  @SuppressWarnings("unchecked")
  protected IEventListener createListenerWrapper(final EventListener listener) {
    return listener == null ? null : new MyEventListener(this, listener);
  }

  public Future<Message> send(final Message message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    MyFuture future = new MyFuture();
    future.send(message);
    return future;
  }

  public Future<Message> send(Message message, long timeOut, TimeUnit timeUnit)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    MyFuture future = new MyFuture();
    future.send(message, timeOut, timeUnit);
    return future;
  }

  private class MyFuture implements Future<Message> {

    private boolean canceled;
    private boolean done;
    private boolean timeOut;
    private Lock lock = new ReentrantLock();
    private CountDownLatch block = new CountDownLatch(1);
    private Message result;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      lock.lock();
      try {
        canceled = true;
        done = false;
        block.countDown();
      }
      finally {
        lock.unlock();
      }

      return true;
    }

    @Override
    public boolean isCancelled() {
      return canceled;
    }

    @Override
    public boolean isDone() {
      return done;
    }

    @Override
    public Message get() throws InterruptedException, ExecutionException {
      try {
        block.await();
      }
      catch (Exception e) {
        throw new ExecutionException(e);
      }

      Message rc = canceled ? null : result;
      result = null;
      return rc;
    }

    @Override
    public Message get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      try {
        block.await(timeout, unit);
      }
      catch (Exception e) {
        throw new ExecutionException(e);
      }

      if (timeOut) {
        throw new TimeoutException();
      }

      Message rc = canceled ? null : result;
      result = null;
      return rc;
    }

    private IEventListener createListener() {
      return new IEventListener() {

        @Override
        public void setValid(boolean value) {
        }

        @Override
        public boolean isValid() {
          return !canceled;
        }

        @Override
        public void receivedSuccessMessage(Request r, Answer a) {
          lock.lock();
          try {
            if (!canceled) {
              result = a;
              canceled = false;
              done = true;
            }
            block.countDown();
          }
          finally {
            lock.unlock();
          }
        }

        @Override
        public void timeoutExpired(Request message) {
          lock.lock();
          try {
            if (!canceled) {
              done = true;
              timeOut = true;
            }
            block.countDown();
          }
          finally {
            lock.unlock();
          }
        }
      };
    }

    public void send(Message message) throws RouteException, OverloadException, IllegalDiameterStateException, InternalException {
      genericSend(message, createListener());
    }

    public void send(Message message, long timeOut, TimeUnit timeUnit)
        throws RouteException, OverloadException, IllegalDiameterStateException, InternalException {
      genericSend(message, createListener(), timeOut, timeUnit);
    }
  }

  /**
   * Appends an *-Application-Id AVP to the message, if none is present already.
   *
   * @param appId the application-id value
   * @param m the message to append the *-Application-Id
   */
  protected void appendAppId(ApplicationId appId, Message m) {
    if (appId == null) {
      return;
    }

    // check if any application-id avp is already present.
    // we could use m.getApplicationIdAvps().size() > 0 but this should spare a few cpu cycles
    for (Avp avp : m.getAvps()) {
      int code = avp.getCode();
      if (code == Avp.ACCT_APPLICATION_ID || code == Avp.AUTH_APPLICATION_ID || code == Avp.VENDOR_SPECIFIC_APPLICATION_ID) {
        return;
      }
    }

    if (appId.getVendorId() == 0) {
      if (appId.getAcctAppId() != 0) {
        m.getAvps().addAvp(Avp.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
      }
      if (appId.getAuthAppId() != 0) {
        m.getAvps().addAvp(Avp.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
      }
    }
    else {
      AvpSet avp = m.getAvps().addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, true, false);
      avp.addAvp(Avp.VENDOR_ID, appId.getVendorId(), true, false, true);
      if (appId.getAuthAppId() != 0) {
        avp.addAvp(Avp.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
      }
      if (appId.getAcctAppId() != 0) {
        avp.addAvp(Avp.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
      }
    }
  }

  protected long getAppId(ApplicationId appId) {
    if (appId == null) {
      return 0;
    }
    // if (appId.getVendorId() == 0) {
    if (appId.getAcctAppId() != 0) {
      return appId.getAcctAppId();
    }
    if (appId.getAuthAppId() != 0) {
      return appId.getAuthAppId();
    }
    // }
    return appId.getVendorId();
  }
}

class MyEventListener implements IEventListener {

  BaseSessionImpl session;
  EventListener listener;
  boolean isValid = true;

  MyEventListener(BaseSessionImpl session, EventListener listener) {
    this.session = session;
    this.listener = listener;
  }

  @Override
  public void setValid(boolean value) {
    isValid = value;
    if (!isValid) {
      session = null;
      listener = null;
    }
  }

  @Override
  public boolean isValid() {
    return isValid;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void receivedSuccessMessage(Request request, Answer answer) {
    if (isValid) {
      session.setLastAccessTime();
      listener.receivedSuccessMessage(request, answer);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void timeoutExpired(Request message) {
    if (isValid) {
      session.setLastAccessTime();
      listener.timeoutExpired(message);
    }
  }

  @Override
  public int hashCode() {
    return listener == null ? 0 : listener.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return listener != null && listener.equals(obj);
  }

  @Override
  public String toString() {
    return listener == null ? "null" : listener.toString();
  }
}
