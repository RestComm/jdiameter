/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.io;

/**
 * This enumeration describe types on network errors
 * These types help to more details define behaviour of high layers
 */
public enum TransportError {
    /**
     * Internal error (network layer exceptions)
     */
    Internal,
    /**
     * Overload errors (overload netowrk queues)
     */
    Overload,
    /**
     * Error during send message procedure (special type)
     */
    FailedSendMessage,
    /**
     * Received broken message (special type) 
      */
    ReceivedBrokenMessage,
    /**
     * Network error (io exceptions)
     */
    NetWorkError
}
