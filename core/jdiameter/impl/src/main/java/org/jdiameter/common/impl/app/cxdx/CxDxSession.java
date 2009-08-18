package org.jdiameter.common.impl.app.cxdx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
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
	public static final int _TX_TIMEOUT=30*1000;
	protected Lock sendAndStateLock = new ReentrantLock();
	protected static final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(4);
	protected List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
	protected CxDxSessionState state;
	protected Future timeoutTaskFuture;
	protected ICxDxMessageFactory messageFactory;
	public void addStateChangeNotification(StateChangeListener listener) {
		if (!stateListeners.contains(listener)) {
			stateListeners.add(listener);
		}
	}

	public void removeStateChangeNotification(StateChangeListener listener) {
		stateListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdiameter.api.app.AppSession#isStateless()
	 */
	public boolean isStateless() {
		//Right?
		return true;
	}

}
