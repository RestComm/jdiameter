/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app.rf;

import org.jdiameter.common.api.app.IAppSessionState;


public enum ServerRfSessionState implements IAppSessionState<ServerRfSessionState> {

    IDLE(0),
    OPEN(1);

    private final int value;

    private ServerRfSessionState(int val) {
        value = val;
    }

    public final int getValue() {
        return value;
    }

    public final ServerRfSessionState fromInt(int val) throws IllegalArgumentException {
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
