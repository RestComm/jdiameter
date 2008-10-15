/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app.acc;

import org.jdiameter.common.api.app.IAppSessionState;


public enum ClientAccSessionState implements IAppSessionState<ClientAccSessionState> {

    IDLE(0),
    OPEN(1),
    PENDING_EVENT(2),
    PENDING_START (3) ,
    PENDING_INTERIM(4),
    PENDING_CLOSE(5),
    PENDING_BUFFERED(6);

    private final int value;

    private ClientAccSessionState(int val) {
        value = val;
    }

    public final int getValue() {
        return value;
    }

    public final ClientAccSessionState fromInt(int val) throws IllegalArgumentException {
        switch (val) {
            case 0:
                return IDLE;
            case 1:
                return OPEN;
            case 2:
                return PENDING_EVENT;
            case 3:
                return PENDING_START;
            case 4:
                return PENDING_INTERIM;
            case 5:
                return PENDING_CLOSE;
            case 6:
                return PENDING_BUFFERED;
            default:
                throw new IllegalArgumentException();
        }
    }
}