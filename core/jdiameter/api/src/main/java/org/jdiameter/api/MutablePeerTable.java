/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

/**
 * This interface extends PeerTable interface and
 * append some operation for controls peer and realm table
 * @version 1.5.1 Final
 */
public interface MutablePeerTable extends PeerTable {

    /**
     * Return peer statistics
     * @param peerHost host of peer
     * @return peer statistics
     */
    Statistic getStatistic(String peerHost);

    /**
     * Append peer table listener
     * @param listener istener instance
     */
    void setPeerTableListener(PeerTableListener listener);    

    /**
     * Add new peer to peer table
     * @param peer URI of peer (host, port and other connection information)
     * for example: aaa://host.example.com:6666;transport=tcp;protocol=diameter
     * @param realmName name of realm
     * @param connecting attempt connect
     * @return peer instance
     */
    Peer addPeer(URI peer, String realmName, boolean connecting);

    /**
     * Remove peer from peer table
     * @param peerHost host of peer
     * @return removed peer instance
     */
    Peer removePeer(String peerHost);
}
