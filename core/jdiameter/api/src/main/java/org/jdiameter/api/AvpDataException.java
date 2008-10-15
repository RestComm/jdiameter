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
 * The AvpDataException signals invalid operations on Avp data.
 * @version 1.5.1 Final
 */

public class AvpDataException extends Exception {

    /**
     * Default constructor
     */
    public AvpDataException() {
        super();
    }

    /**
     * Constructor with reason string
     * @param message reason string
     */
    public AvpDataException(String message) {
        super(message);
    }

    /**
     * Constructor with reason string and parent exception
     * @param message message reason string
     * @param cause parent exception
     */
    public AvpDataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with parent exception
     * @param cause  parent exception
     */
    public AvpDataException(Throwable cause) {
        super(cause);
    }
}
