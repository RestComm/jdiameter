/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.parser;

/**
 * Signals that an parser exception has occurred in a during decoding message
 */
public class DecodeException extends Exception {

    /**
     * Create instance of class
     */
    public DecodeException() {
    }

    /**
     * Create instance of class with predefined parameters
     * @param message error message
     */
    public DecodeException(String message) {
        super(message);
    }

    /**
     * Create instance of class with predefined parameters
     * @param message error message
     * @param cause error cause
     */
    public DecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create instance of class with predefined parameters
     * @param cause error cause
     */
    public DecodeException(Throwable cause) {
        super(cause);
    }
}
