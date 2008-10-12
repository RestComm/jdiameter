/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

import java.util.List;

/**
 * PeerManager is Diameter Stack PCB(RFC 3588) realisation.
 * Wrapper interface allows adapt message to any driver vendor specific interface.
 *  List of wrapper classes:
 * - Server API : Network, MutablePeerTable
 * @version 1.5.1 Final
 */

public interface PeerTable extends Wrapper {

    /**
     * Return peer by host name
     * @param peerHost host name
     * @return peer
     */
    Peer getPeer(String peerHost);

    /**
     * Return no mutable list of peers
     * @return List of current stack known peers 
     */
    List<Peer> getPeerTable();

}
