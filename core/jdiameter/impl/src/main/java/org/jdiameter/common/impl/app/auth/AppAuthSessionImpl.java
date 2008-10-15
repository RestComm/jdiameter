package org.jdiameter.common.impl.app.auth;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.common.impl.app.AppSessionImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AppAuthSessionImpl extends AppSessionImpl implements  NetworkReqListener, org.jdiameter.api.app.StateMachine {

    protected Lock sendAndStateLock = new ReentrantLock();
    protected ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(4);
    protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();

    public void addStateChangeNotification(StateChangeListener listener) {
        if (!stateListeners.contains(listener))
            stateListeners.add(listener);
    }

    public void removeStateChangeNotification(StateChangeListener listener) {
        stateListeners.remove(listener);
    }

    public void release() {
        scheduler.shutdown();
        super.release();
    }
}