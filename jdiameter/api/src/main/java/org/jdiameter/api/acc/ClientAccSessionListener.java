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

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppAnswerEvent;

/**
 * This interface defines the possible actions that the different states in the
 * Accounting state machine
 * @version 1.5.1 Final
 */

public interface ClientAccSessionListener {

    /**
     * Notifies this AccSessionEventListener that the ClientAccSesssion has recived AccAnswer message.
     * @param appSession parent application session (FSM)
     * @param request accounting request object
     * @param answer accounting answer object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doAccAnswerEvent(ClientAccSession appSession, AccountRequest request, AccountAnswer answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     * Notifies this AccSessionEventListener that the ClientAccSesssion has recived not account message.
     * @param appSession parent application session (FSM)
     * @param request request object
     * @param answer answer object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
