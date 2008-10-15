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
 * This interface is used to inform about changes in the state for a given peer.
 * @version 1.5.1 Final
 */

public interface PeerStateListener {

    /**
     * A change of state has occured for a peer.
     * @param oldState old state of peer
     * @param newState new state of peer
     */
    public void stateChanged (PeerState oldState, PeerState newState);

}
