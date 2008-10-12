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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

/**
 * Peer contains everything that is worth knowing about a peer and
 * define some operation for working with this peer.
 * @version 1.5.1 Final
 */

public interface Peer {

    /**
     *  Establishes a connection towards a remote peer.
     * @throws IllegalDiameterStateException
     */
    void connect() throws InternalException, IOException, IllegalDiameterStateException;

    /**
     * Close the connection to the peer.
     * @throws IllegalDiameterStateException
     */
    void disconnect() throws InternalException, IllegalDiameterStateException;

    /**
     * All implementations must support PeerState intrface as argument
     * @return state of peer
     */
    <E> E getState(Class<E> enumc);

    /**
     * @return URI of peer
     */
    URI getUri();

    /**
     * @return array of peer ip addresses
     */
    InetAddress[] getIPAddresses();

    /**
     * @return Realm name of peer
     */
    String getRealmName();

    /**
     * @return vendor id of peer stack implimentation
     */
    long getVendorId(); 

    /**
     * @return product name of peer stack implimentation
     */
    String getProductName();

    /**
     * @return firmware version  of peer stack implimentation
     */
    long getFirmware(); 

    /**
     * @return set of common Application-id of peer;
     */
    Set<ApplicationId> getCommonApplications();

    /**
     * Append peer state change listener to the peer manager

     * @param listener listener instance
     */
    void addPeerStateListener(PeerStateListener listener);

    /**
     * Remove peer state change listener from the peer manager
     * @param listener listener instance
     */
    void removePeerStateListener(PeerStateListener listener);
}
