package org.jdiameter.common.impl.app.acc;

import org.jdiameter.api.Answer;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.common.impl.app.AppSessionImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AppAccSessionImpl extends AppSessionImpl implements  NetworkReqListener, org.jdiameter.api.app.StateMachine {

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

    protected AccountRequest createAccountRequest(Request request) {
        return new AccountRequestImpl(request);
    }

    protected AccountAnswer createAccountAnswer(Answer answer) {
        return new AccountAnswerImpl(answer);
    }

    public void release() {
        scheduler.shutdown();
        super.release();
    }
}