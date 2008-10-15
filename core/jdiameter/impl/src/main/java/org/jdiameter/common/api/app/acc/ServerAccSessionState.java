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


public enum ServerAccSessionState implements IAppSessionState<ServerAccSessionState> {

    IDLE(0),
    OPEN(1);

    private final int value;

    private ServerAccSessionState(int val) {
        value = val;
    }

    public final int getValue() {
        return value;
    }

    public final ServerAccSessionState fromInt(int val) throws IllegalArgumentException {
        switch (val) {
            case 0:
                return IDLE;
            case 1:
                return OPEN;
            default:
                throw new IllegalArgumentException();
        }
    }

}
