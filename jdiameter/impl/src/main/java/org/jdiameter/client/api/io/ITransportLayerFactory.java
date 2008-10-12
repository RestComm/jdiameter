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

import org.jdiameter.api.Wrapper;

import java.net.InetAddress;

/**
 * Factory of Network Layer elements.
 * Configuration and message parser instances injection by constructor
 */

public interface ITransportLayerFactory extends Wrapper {

    /**
     * Create new IConnection instance with predefined parameters
     * @param inetAddress destination host address
     * @param port destination port address
     * @param ref reference to additional parameters
     * @return IConnection instance
     * @throws TransportException
     */
    IConnection createConnection(InetAddress inetAddress, int port, String ref) throws TransportException;

    /**
     * Create new IConnection instance with predefined parameters
     * @param inetAddress destination host address
     * @param port  destination port address
     * @param listener connection listener instance
     * @param ref reference to additional parameters
     * @return IConnection instance
     * @throws TransportException
     */
    IConnection createConnection(InetAddress inetAddress, int port, IConnectionListener listener, String ref) throws TransportException;

}
