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
package org.jdiameter.common.impl.app.auth;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.auth.IAuthSessionFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class AppAuthSessionImpl extends AppSessionImpl implements NetworkReqListener, org.jdiameter.api.app.StateMachine {

  private static final long serialVersionUID = 1L;

  protected Lock sendAndStateLock = new ReentrantLock();
  // protected ScheduledExecutorService scheduler = null;

  @SuppressWarnings("unchecked")
  protected transient List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();

  // protected SessionFactory sf = null;

  public AppAuthSessionImpl(SessionFactory sf, String sessionId) {
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

  public void release() {
    // scheduler.shutdownNow();
    super.release();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.common.impl.app.AppSessionImpl#relink(org.jdiameter.client
   * .api.IContainer)
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
    IAuthSessionFactory fct = (IAuthSessionFactory) ((ISessionFactory) super.sf).getAppSessionFactory(interfaze);
    this.stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
    this.addStateChangeNotification(fct.getStateListener());
  }

}