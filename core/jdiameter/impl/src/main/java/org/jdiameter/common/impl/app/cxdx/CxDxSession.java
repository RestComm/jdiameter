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
package org.jdiameter.common.impl.app.cxdx;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.api.app.cxdx.ICxDxSessionFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;

/**
 * Start time:15:18:44 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class CxDxSession extends AppSessionImpl implements NetworkReqListener, StateMachine {

  private static final long serialVersionUID = 1L;

  public static final int _TX_TIMEOUT = 30 * 1000;

  protected Lock sendAndStateLock = new ReentrantLock();

  @SuppressWarnings("unchecked")
  protected transient List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  protected transient ICxDxMessageFactory messageFactory;

  protected CxDxSessionState state = CxDxSessionState.IDLE;
  // protected Future timeoutTaskFuture;
  // this can be weird
  protected Serializable timerId_timeout;
  protected static final String TIMER_NAME_MSG_TIMEOUT = "MSG_TIMEOUT";
  protected Message buffer;

  public CxDxSession(SessionFactory sf, String sessionId) {
    super(sf, sessionId);
  }

  @SuppressWarnings("unchecked")
  public void addStateChangeNotification(StateChangeListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  @SuppressWarnings("unchecked")
  public void removeStateChangeNotification(StateChangeListener listener) {
    stateListeners.remove(listener);
  }

  public boolean isStateless() {
    // Cx/Dx is always stateless
    return true;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    //Cx/Dx is event based..
    return false;
  }

  protected void startMsgTimer() {
    try {
      sendAndStateLock.lock();
      this.timerId_timeout = super.timerFacility.schedule(sessionId, TIMER_NAME_MSG_TIMEOUT, _TX_TIMEOUT);
      super.sessionDataSource.updateSession(this);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void cancelMsgTimer() {
    try {
      sendAndStateLock.lock();
      if(this.timerId_timeout == null) {
        return;
      }
      super.timerFacility.cancel(timerId_timeout);
      this.timerId_timeout = null;
      super.sessionDataSource.updateSession(this);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((timerId_timeout == null) ? 0 : timerId_timeout.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
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

    CxDxSession other = (CxDxSession) obj;
    if (buffer == null) {
      if (other.buffer != null) {
        return false;
      }
    }
    else if (!buffer.equals(other.buffer)) {
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
    if (timerId_timeout == null) {
      if (other.timerId_timeout != null) {
        return false;
      }
    }
    else if (!timerId_timeout.equals(other.timerId_timeout)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.impl.app.AppSessionImpl#relink(org.jdiameter.client.api.IContainer)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void relink(IContainer stack) {
    super.relink(stack);

    // FIXME Any better way to do this?
    Class interfaze = null;
    for (Class possibleInterface : this.getClass().getInterfaces()) {
      if (interfaze != null) {
        break;
      }
      for (Class appSessionInterface : possibleInterface.getInterfaces()) {
        if (appSessionInterface.equals(AppSession.class)) {
          interfaze = possibleInterface;
          break;
        }
      }
    }

    ICxDxSessionFactory fct = (ICxDxSessionFactory) ((ISessionFactory) super.sf).getAppSessionFactory(interfaze);
    this.stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
    this.addStateChangeNotification(fct.getStateListener());
    this.messageFactory = fct.getMessageFactory();
  }

}
