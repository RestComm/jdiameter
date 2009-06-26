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
     * @param remoteAddress destination host address
     * @param remotePort destination port address
     * @param localAddress local network adapter address
     * @param localPort local socket port
     * @param ref reference to additional parameters
     * @return IConnection instance
     * @throws TransportException
     */
    IConnection createConnection(InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, String ref) throws TransportException;

    /**
     * Create new IConnection instance with predefined parameters
     * @param remoteAddress destination host address
     * @param remotePort  destination port address
     * @param localAddress local network adapter address
     * @param localPort local socket port
     * @param listener connection listener instance
     * @param ref reference to additional parameters
     * @return IConnection instance
     * @throws TransportException
     */
    IConnection createConnection(InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, String ref) throws TransportException;

}
