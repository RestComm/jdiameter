/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.acc;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.RouteException;

/**
 * Basic class for server accounting application specific session.
 * Listener must injection from constructor of implementation class.
 * @version 1.5.1 Final
 */

public interface ServerAccSession extends AppSession, StateMachine {

     /**
     * Send Account Answer to Client
     * @param answer request object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void sendAccountAnswer(AccountAnswer answer)
            throws InternalException, IllegalStateException, RouteException, OverloadException;

}
