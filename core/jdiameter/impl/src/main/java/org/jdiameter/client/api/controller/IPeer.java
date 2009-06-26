/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.controller;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportException;

/**
 * This interface provide additional methods for Peer interface
 */
public interface IPeer extends Peer {

    /**
     * Return rating of pee
     * @return int value
     */
    int getRaiting();

    /**
     * Return new hop by hop id for new message
     * @return new hop by hop id
     */
    int getHopByHopIdentifier();

    /**
     * Append request to peer request storage map
     * @param message request instance
     */
    void addMessage(IMessage message);

    /**
     * Remove request from request storage map
     * @param message request instance
     */
    void remMessage(IMessage message);

    /**
     * Clear request storage map
     */
    IMessage[] remAllMessage();

    /**
     * Put message to peer fsm
     * @param message request instance
     * @return true if message will be set to FSM
     * @throws TransportException
     * @throws OverloadException
     */
    public boolean handleMessage(EventTypes type, IMessage message, String key) throws TransportException, OverloadException, InternalException;
    
    /**
     * Send message to diameter network
     * @param message request instance
     * @return true if message will be set to FSM
     * @throws TransportException
     * @throws OverloadException
     */
    boolean sendMessage(IMessage message) throws TransportException, OverloadException, InternalException;

    /**
     * Return true if peer has valid connection
     * @return true if peer has valid connection
     */
    boolean hasValidConnection();

    /**
     * Attach peer to realm
     * @param realm realm name
     */
    void setRealm(String realm);

    /**
     * Add state change listener
     * @param listener listener instance
     */
    void addStateChangeListener(StateChangeListener listener);

    /**
     * Remove state change listener
     * @param listener listener instance
     */
    void remStateChangeListener(StateChangeListener listener);

    /**
     * Add connection state change listener
     * @param listener listener instance
     */
    void addConnectionListener(IConnectionListener listener);

    /**
     * Remove connection state change listener
     * @param listener listener instance
     */
    void remConnectionListener(IConnectionListener listener);
}

