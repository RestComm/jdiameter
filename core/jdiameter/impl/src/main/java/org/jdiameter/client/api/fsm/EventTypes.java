/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.fsm;

/**
 * This enumeration describe all fsm events
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
