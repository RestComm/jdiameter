/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

/**
 * The ApplicationAlreadyUse signals that the application a user is reporting support for is already in use by someone else.
 * @version 1.5.1 Final
 */

public class ApplicationAlreadyUseException extends Exception {

    /**
     * Constructor with reason string
     * @param message reason string
     */
    public ApplicationAlreadyUseException(String message) {
        super(message);
    }

    /**
     * Constructor with reason string and parent exception
     * @param message message reason string
     * @param cause parent exception
     */
    public ApplicationAlreadyUseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with parent exception
     * @param cause  parent exception
     */
    public ApplicationAlreadyUseException(Throwable cause) {
        super(cause);
    }
}
