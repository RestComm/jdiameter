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

import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Wrapper;
import org.jdiameter.client.api.IMessage;

import java.io.IOException;
import java.net.InetAddress;

/**
 * A Connection  with a remote host.
 */
public interface IConnection extends Wrapper {

    /**
     * Return created time
     * @return created time
     */
    public long getCreatedTime();
    
    /**
     * Return identifier of connection. For example:
     *  "[remote_host_name]:[remote_port]"
     * @return identifier of connection.
     */
    String getKey();

    /**
     * Connect with remote host
     * @throws TransportException
     */
    void connect() throws TransportException;

    /**
     * Disconnect wit remote host
     * @throws InternalError
     */
    void disconnect() throws InternalError;

    /**
     * Send message to remote host
     * @param message diameter message
     * @throws TransportException
     * @throws OverloadException
     */
    void sendMessage(IMessage message) throws TransportException, OverloadException;

    /**
     * Clear all attachec resources (close socket)
     * @throws IOException
     */
    void release() throws IOException;

    /**
     * Return true if connection is incomming
     * @return true if connection is incomming
     */
    boolean isNetworkInitiated();

    /**
     * Return true if is connection is valid
     * @return true if is connection is valid
     */
    boolean isConnected();

    /**
     * Return remote host address
     * @return remote host address
     */
    InetAddress getRemoteAddress();

    /**
     * Return remote socket port
     * @return remote socket port
     */
    int getRemotePort();

    /**
     * Append connection listener
     * @param connectionListener listener instance
     */
    void addConnectionListener(IConnectionListener connectionListener);

    /**
     * Remove all connection listeners
     */
    void remAllConnectionListener();
    
    /**
     * Remove connection listener
     * @param connectionListener listener instance
     */
    void remConnectionListener(IConnectionListener connectionListener);
}
