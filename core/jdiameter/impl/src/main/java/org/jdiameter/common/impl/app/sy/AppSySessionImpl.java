/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.common.impl.app.sy;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sy.SySession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.impl.app.AppSessionImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Policy and charging control, Spending Limit Report - Sy session implementation
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public abstract class AppSySessionImpl extends AppSessionImpl implements SySession, NetworkReqListener {

  protected Lock sendAndStateLock = new ReentrantLock();

  protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();

  public AppSySessionImpl(ISessionFactory sf, IAppSessionData appSessionData) {
    super(sf, appSessionData);
  }

  @Override
  public void addStateChangeNotification(StateChangeListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  @Override
  public void removeStateChangeNotification(StateChangeListener listener) {
    stateListeners.remove(listener);
  }

  @Override
  public void release() {
    //stateListeners.clear();
    super.release();
  }
}
