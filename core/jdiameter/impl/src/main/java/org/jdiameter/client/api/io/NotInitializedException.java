/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 *
 *
 */
package org.jdiameter.client.api.io;

/**
 * Signals that an exception has occurred in a during start
 * transport element. An <code>NotInitializedException</code> is thrown to indicate that
 * transport element is not  configured.
 */
public class NotInitializedException extends Exception{

    /**
     * Create instance of class
     */
    public NotInitializedException() {
        super();
    }

    /**
     * Create instance of class with predefined parameters
     * @param message error message
     */
    public NotInitializedException(String message) {
        super(message);
    }

    /**
     * Create instance of class with predefined parameters
     * @param message error message
     * @param cause error cause
     */
    public NotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create instance of class with predefined parameters
     * @param cause error cause
     */
    public NotInitializedException(Throwable cause) {
        super(cause);
    }
}
