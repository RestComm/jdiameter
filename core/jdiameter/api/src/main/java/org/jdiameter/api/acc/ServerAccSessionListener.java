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

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.acc.events.AccountRequest;

/**
 * This interface defines the possible actions that the different states in the
 * Accounting state machine
 * @version 1.5.1 Final
 */

public interface ServerAccSessionListener {

    /**
     * Notifies this AccSessionEventListener that the ServerAccSesssion has recived AccRequest message.
     * @param appSession parent application session (FSM)
     * @param request accounting request object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doAccRequestEvent(ServerAccSession appSession, AccountRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived not authentication message.
     * @param session parent application session (FSM)
     * @param request request object
     * @param answer answer object
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
    void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
            

}
