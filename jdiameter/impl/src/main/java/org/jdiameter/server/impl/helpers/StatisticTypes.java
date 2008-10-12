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
     * Count of regestered network listeners (applications)
     */
    int NET_LIST_COUNTER  = 0x08;
    /**
     * Size of peer fsm queue
     */
    int PEER_QUEUE_SIZE   = 0x0A;

}
