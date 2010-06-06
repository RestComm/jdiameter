package org.jdiameter.common.impl.app.cxdx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
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
  protected ScheduledExecutorService scheduler = null;
  protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  protected SessionFactory sf = null;

  protected CxDxSessionState state = CxDxSessionState.IDLE;
  protected Future timeoutTaskFuture;
  protected ICxDxMessageFactory messageFactory;

  public CxDxSession(SessionFactory sf) {
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

  public boolean isStateless() {
    // Cx/Dx is always stateless
    return true;
  }

}
