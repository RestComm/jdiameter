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
 * Signals that a method has been invoked at an illegal or
 * inappropriate time.
 * @version 1.5.1 Final
 */

public class IllegalDiameterStateException extends Exception {

    public IllegalDiameterStateException() {
    }

    public IllegalDiameterStateException(String message) {
        super(message);
    }

    public IllegalDiameterStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDiameterStateException(Throwable cause) {
        super(cause);
    }
}
