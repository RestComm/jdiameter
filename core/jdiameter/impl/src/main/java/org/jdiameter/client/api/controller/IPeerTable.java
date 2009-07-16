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

import org.jdiameter.api.*;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 *  This interface provide additional methods for PeerTable interface
 */
public interface IPeerTable extends PeerTable {

    /**
     * Start peer manager ( start network activity )
     * @throws IllegalDiameterStateException
     * @throws IOException
     */
    void start() throws IllegalDiameterStateException, IOException;

    /**
     * Run stopping oricedure (unsynchronized)
     */
    void stopping();

    /**
     * Release resources
     */
    void stopped();

    /**
     *  Destroy all resources
     */
    void destroy();

    /**
     * Send message to diameter network ( routing procedure )
     * @param message  message instance
     * @throws IllegalDiameterStateException
     * @throws IOException
     * @throws RouteException
     * @throws AvpDataException
     */
    void sendMessage(IMessage message) throws IllegalDiameterStateException, IOException, RouteException, AvpDataException;

    /**
     * Register session lister
     * @param sessionId session id
     * @param listener listener listener
     */
    void addSessionReqListener(String sessionId, NetworkReqListener listener);

    /**
     * Return peer from peer table by peerURI
     * @param peerHost peer host
     * @return peer instance
     */
    IPeer getPeerByName(String peerHost);

    /**
     * Return peer from peer table by peerURI
     * @param peerUri peer uri
     * @return peer instance
     */
    IPeer getPeerByUri(String peerUri);

    /**
     * Return map of session event listeners
     * @return map of session event listeners
     */
    Map<String, NetworkReqListener> getSessionReqListeners();

    /**
     * Remove session event listener
     * @param sessionId id of session
     */
    void removeSessionListener(String sessionId);

    /**
     * Set instance assembler
     * @param assembler assembler instance
     */
    void setAssempler(IAssembler assembler);
    
    /**
     * Return peer common executor
     * @return  peer common executor
     */
    ExecutorService getPeerTaskExecutor();    
}
