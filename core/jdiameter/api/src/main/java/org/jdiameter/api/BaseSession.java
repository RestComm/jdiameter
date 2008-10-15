/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

import java.io.Serializable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * The session delivery objects are responsible for delivering all incomming Message to a specific session.
 * It determines the Diameter Session object that the message belongs to by querying the message's session id AVP.
 * The delivery object searches the local session database for a matching session. If no matching session is found,
 * the delivery object will lookup a matching session factory object that has an application id matching the
 * application id of the message. If there is a registered session factory, then the delivery object will ask the
 * factory to create a new session and delivery the message to the newly created session. If non of these lookup's
 * are successful, the session delivery object will silently discard the message.
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * Serializable interface allows use this class in SLEE Event objects
 * @version 1.5.1 Final
 */
public interface BaseSession extends Wrapper, Serializable {

    /**
     * Returns the time when this session was created (milliseconds)
     * Start point of time January 1, 1970 GMT.
     * @return long specifying when this session was created
     */
    long getCreationTime();

    /**
     * Returns the last time an event occurred on this session (milliseconds)
     * Start point of time January 1, 1970 GMT.
     * @return long specifying when last time an event occurred on this session
     */
    long getLastAccessedTime();

    /**
     * Return true if session is not released
     * @return true if session is not released
     */
    boolean isValid();

    /**
     * Sends and wait response message with default timeout
     * @param message request/answer diameter message
     * @return InFuture result of an asynchronous operation
     * @throws org.jdiameter.api.InternalException The InternalException signals that internal error is occurred.
     * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
     */
    Future<Message> send(Message message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     * Sends and wait response message with defined timeout
     * @param message  request/answer diameter message
     * @return InFuture result of an asynchronous operation
     * @param timeOut value of timeout
     * @param timeUnit type of timeOut value
     * @throws org.jdiameter.api.InternalException  The InternalException signals that internal error is occurred.
     * @throws org.jdiameter.api.IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws org.jdiameter.api.RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws org.jdiameter.api.OverloadException The OverloadException signals that destination host is overloaded.
     */
    Future<Message> send(Message message, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
    

    /**
     * Release all resources append to session
     */
    void release();
}
