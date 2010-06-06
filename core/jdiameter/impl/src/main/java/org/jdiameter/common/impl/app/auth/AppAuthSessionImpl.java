package org.jdiameter.common.impl.app.auth;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;

public abstract class AppAuthSessionImpl extends AppSessionImpl implements  NetworkReqListener, org.jdiameter.api.app.StateMachine {

  private static final long serialVersionUID = 1L;

  protected Lock sendAndStateLock = new ReentrantLock();
  protected ScheduledExecutorService scheduler = null;
  protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  protected SessionFactory sf = null;

  public AppAuthSessionImpl(SessionFactory sf) {
    if (sf == null) {
      throw new IllegalArgumentException("SessionFactory must not be null");
    }
    this.sf = sf;
    this.scheduler = ((ISessionFactory) this.sf).getConcurrentFactory().getScheduledExecutorService(
        IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
  }

  public void addStateChangeNotification(StateChangeListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  public void removeStateChangeNotification(StateChangeListener listener) {
    stateListeners.remove(listener);
  }

  public void release() {
    //scheduler.shutdownNow();
    super.release();
  }
}