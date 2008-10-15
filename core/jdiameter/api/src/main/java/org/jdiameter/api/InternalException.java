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
 * An exception that provides information on a stack  error or other errors.
 * @version 1.5.1 Final
 */

public class InternalException extends Exception {

    /**
     * Default constructor
     */
    public InternalException() {
        super();
    }

    /**
     * Constructor with reason string
     * @param message reason string
     */
    public InternalException(String message) {
        super(message);
    }

    /**
     * Constructor with reason string and parent exception
     * @param message message reason string
     * @param cause parent exception
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with parent exception
     * @param cause  parent exception
     */
    public InternalException(Throwable cause) {
        super(cause);
    }

}
