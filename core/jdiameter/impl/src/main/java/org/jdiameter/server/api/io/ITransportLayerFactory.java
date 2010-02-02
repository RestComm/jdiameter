/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.api.io;

import org.jdiameter.client.api.io.TransportException;

import java.net.InetAddress;

/**
 * Factory of Network Layer elements. This interface append to parent interface
 * additional method for creating INetWorkGuard guard instances.
 * Additional parameters (Configuration, Parsers and etc) injection to instance over constructor
 */
public interface ITransportLayerFactory extends org.jdiameter.client.api.io.ITransportLayerFactory {

    /**
     * Create INetworkGuard instance with predefined parameters
     * 
     * @param inetAddress address of server socket
     * @param port  port of server socket
     * @return INetWorkGuard instance
     * @throws TransportException
     */
    INetworkGuard createNetworkGuard(InetAddress inetAddress, int port) throws TransportException;

    /**
     * Create INetworkGuard instance with predefined parameters
     * 
     * @param inetAddress address of server socket
     * @param port  port of server socket
     * @param listener event listener
     * @return INetWorkGuard instance
     * @throws TransportException
     */    
    INetworkGuard createNetworkGuard(InetAddress inetAddress, int port, INetworkConnectionListener listener) throws TransportException;

}
