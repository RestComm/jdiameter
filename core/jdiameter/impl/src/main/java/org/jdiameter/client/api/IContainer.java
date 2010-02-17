/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api;

import org.jdiameter.api.*;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This interface extends behaviour of stack interface
 * Data: $Date: 2008/07/03 19:43:10 $
 * Revision: $Revision: 1.1 $
 * @version 1.5.0.1
 */
public interface IContainer extends Stack {

    /**
     * Return state of stack
     * @return Return state of stack
     */
    StackState getState();

    /**
     * Return configuration instance
     * @return configuration instance
     */
    Configuration getConfiguration();

    /**
     * Return root IOC
     * @return root IOC
     */
    IAssembler getAssemblerFacility();

    /**
     * Return common Scheduled Executor Service
     * @return common Scheduled Executor Service
     */
    ScheduledExecutorService getScheduledFacility();

    /**
     * Return common concurrent factory 
     * @return
     */
    IConcurrentFactory getConcurrentFactory();
    /**
     * Send messahe
     * @param session session instance
     * @throws RouteException
     * @throws AvpDataException
     * @throws IllegalDiameterStateException
     * @throws IOException
     */
    void sendMessage(IMessage session) throws RouteException, AvpDataException, IllegalDiameterStateException, IOException;


    /**
     * Add session listener
     * @param sessionId session id
     * @param listener listener instance
     */
    public void addSessionListener(String sessionId, NetworkReqListener listener);

    /**
     * Remove session event listener by sessionId
     * @param sessionId session identifier
     */
    void removeSessionListener(String sessionId);
}
