/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.jdiameter.common.impl.app.slh;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.jdiameter.common.api.app.slh.ISLhMessageFactory;
import org.jdiameter.common.api.app.slh.ISLhSessionData;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public abstract class SLhSession extends AppSessionImpl implements NetworkReqListener, StateMachine {

  public static final int _TX_TIMEOUT = 30 * 1000;

  protected Lock sendAndStateLock = new ReentrantLock();

  @SuppressWarnings("rawtypes")
  protected transient List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  protected transient ISLhMessageFactory messageFactory;

  protected static final String TIMER_NAME_MSG_TIMEOUT = "MSG_TIMEOUT";
  protected ISLhSessionData sessionData;

  public SLhSession(ISessionFactory sf, ISLhSessionData sessionData) {
    super(sf, sessionData);
    this.sessionData = sessionData;
  }

  @SuppressWarnings("rawtypes")
  public void addStateChangeNotification(StateChangeListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  @SuppressWarnings("rawtypes")
  public void removeStateChangeNotification(StateChangeListener listener) {
    stateListeners.remove(listener);
  }

  public boolean isStateless() {
    return true;
  }

  @Override
  public boolean isReplicable() {
    return false;
  }

  protected void startMsgTimer() {
    try {
      sendAndStateLock.lock();
      sessionData.setTsTimerId(super.timerFacility.schedule(getSessionId(), TIMER_NAME_MSG_TIMEOUT, _TX_TIMEOUT));
    } finally {
      sendAndStateLock.unlock();
    }
  }

  protected void cancelMsgTimer() {
    try {
      sendAndStateLock.lock();
      final Serializable timerId = this.sessionData.getTsTimerId();
      if (timerId == null) {
        return;
      }
      super.timerFacility.cancel(timerId);
      this.sessionData.setTsTimerId(null);
    } finally {
      sendAndStateLock.unlock();
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((sessionData == null) ? 0 : sessionData.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SLhSession other = (SLhSession) obj;
    if (sessionData == null) {
      if (other.sessionData != null) {
        return false;
      }
    } else if (!sessionData.equals(other.sessionData)) {
      return false;
    }
    return true;
  }

}