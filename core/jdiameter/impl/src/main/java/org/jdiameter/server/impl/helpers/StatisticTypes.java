/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.impl.helpers;

/**
 * Enumeration of all static records
 */
public interface StatisticTypes {

    /**
     * Count of sended requests
     */
    int REQ_MESS_COUNTER  = 0x02;
    /**
     * Count of received responses
     */
    int RESP_MESS_COUNTER = 0x04;
    /**
     * Count of regestered network listeners (appId)
     */
    int NET_APPID_LIST_COUNTER  = 0x08;
    /**
     * Count of regestered network listeners (selector)
     */
    int NET_SELECTOR_LIST_COUNTER  = 0x10;    
    /**
     * Size of peer fsm queue
     */
    int PEER_QUEUE_SIZE   = 0x0A;

}
