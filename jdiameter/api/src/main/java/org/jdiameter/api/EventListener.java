/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

/**
 * Common event listener
 * @version 1.5.1 Final
 */
public interface EventListener<R extends Message, A extends Message> {

    /**
     * Notifies this Listener that stack has recived diameter answer message.
     * For network requests - answer parameter is null
     *
     * @param request the request message
     * @param answer the answer on application request
     */
    void receivedSuccessMessage(R request, A answer);

    /**
     * Notifies this Listener that the request has timeout.
     *
     * @param request the request has timeout
     */
    void timeoutExpired(R request);
}
