/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.acc;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;

/**
 * Basic class for accounting application specific session
 * Listener must injection from constructor of implementation class
 * @version 1.5.1 Final
 */

public interface ClientAccSession extends AppSession, StateMachine {

    /**
     * Send Account Request to Server
     * @param request request object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void sendAccountRequest(AccountRequest request)
            throws InternalException, IllegalStateException, RouteException, OverloadException;

}
