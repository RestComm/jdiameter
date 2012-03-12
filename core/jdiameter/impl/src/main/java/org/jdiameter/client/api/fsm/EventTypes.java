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

package org.jdiameter.client.api.fsm;

/**
 * This enumeration describe all fsm events
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum EventTypes {

    /**
     * Connect event
     */
    CONNECT_EVENT (true),
    /**
     * Disconnect event
     */
    DISCONNECT_EVENT (true),

    /**
     * Operation timeout event
     */
    TIMEOUT_EVENT (true),
    /**
     * Start fsm event
     */
    START_EVENT(true),
    /**
     * Stop fsm event
     */
    STOP_EVENT (true),

    /**
     * Internal error during processing event
     */
    INTERNAL_ERROR(true),

    /**
     * Stack received CER message
     */
    CER_EVENT,
    /**
     * Stack received CEA message
     */
    CEA_EVENT,

    /**
     * Stack received DPR message
     */
    DPR_EVENT,
    /**
     * Stack received DPA message
     */
    DPA_EVENT,

    /**
     * Stack received DWR message
     */
    DWR_EVENT,
    /**
     * Stack received DWA message
     */
    DWA_EVENT,

    /**
     * App send message to network
     */
    SEND_MSG_EVENT,

    /**
     * Stack received Application message
     */
    RECEIVE_MSG_EVENT;

    boolean highPriority = false;

    EventTypes() {
        highPriority = false;
    }

    EventTypes(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isHighPriority() {
        return highPriority;
    }
}
