/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

/**
 * This enumerated class define Peer states. More information you can read on document
 * "Authentication, Authorization and Accounting (AAA) Transport Profile"
 * @version 1.5.1 Final
 */
public enum PeerState {
    OKAY, SUSPECT, DOWN, REOPEN, INITIAL
}
