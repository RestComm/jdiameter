/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app.acc;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.acc.ServerAccSession;

import java.util.concurrent.ScheduledFuture;

/**
 * Additional listener
 * Actions for FSM
 */
public interface IServerAccActionContext {

    void sessionTimerStarted(ServerAccSession appSession, ScheduledFuture timer) throws InternalException;

    void sessionTimeoutElapses(ServerAccSession appSession) throws InternalException;

    void srssionTimerCanceled(ServerAccSession appSession, ScheduledFuture timer) throws InternalException;
}
