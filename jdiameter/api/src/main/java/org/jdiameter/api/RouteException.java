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
 * The NoRouteException signals that no route exist for a given realm.
 * @version 1.5.1 Final
 */

public class RouteException extends Exception {

    /**
     * Constructor with reason string
     * @param message reason string
     */
    public RouteException(String message) {
        super(message);
    }


    /**
     * Constructor with reason string and parent exception
     * @param message message reason string
     * @param cause parent exception
     */
    public RouteException(String message, Throwable cause) {
        super(message, cause);
    }
}
