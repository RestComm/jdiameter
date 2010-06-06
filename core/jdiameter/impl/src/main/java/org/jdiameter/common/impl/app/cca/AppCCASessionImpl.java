package org.jdiameter.common.impl.app.cca;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * AppCCASessionImpl.java
 *
 * <br>Super project:  mobicents
 * <br>5:00:55 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public abstract class AppCCASessionImpl extends AppSessionImpl implements NetworkReqListener, StateMachine {

  private static final long serialVersionUID = 1L;

  protected Lock sendAndStateLock = new ReentrantLock();

  protected  ScheduledExecutorService scheduler = null;

  protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();

  protected Logger logger = LoggerFactory.getLogger(AppCCASessionImpl.class);

  protected SessionFactory sf = null;

  public AppCCASessionImpl(SessionFactory sf) {
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
    super.release();
  }

}
