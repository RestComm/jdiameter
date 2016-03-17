/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.api;

import org.jdiameter.api.*;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This interface extends behavior of stack interface
 * Data: $Date: 2008/07/03 19:43:10 $
 * Revision: $Revision: 1.1 $
 * @version 1.5.0.1
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
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
     * Send message
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
