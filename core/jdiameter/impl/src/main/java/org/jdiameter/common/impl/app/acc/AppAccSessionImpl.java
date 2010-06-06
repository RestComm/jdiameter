package org.jdiameter.common.impl.app.acc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;

public abstract class AppAccSessionImpl extends AppSessionImpl implements  NetworkReqListener, org.jdiameter.api.app.StateMachine {

  private static final long serialVersionUID = 1L;

  protected Lock sendAndStateLock = new ReentrantLock();
  protected ScheduledExecutorService scheduler = null;
  protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  protected SessionFactory sf = null;

  public AppAccSessionImpl(SessionFactory sf) {
    if (sf == null) {
      throw new IllegalArgumentException("SessionFactory must not be null");
    }
    this.sf = sf;

    // The per-Application Executor
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

  protected AccountRequest createAccountRequest(Request request) {
    return new AccountRequestImpl(request);
  }

  protected AccountAnswer createAccountAnswer(Answer answer) {
    return new AccountAnswerImpl(answer);
  }

  public void release() {
    //scheduler.shutdownNow();
    super.release();
  }
}