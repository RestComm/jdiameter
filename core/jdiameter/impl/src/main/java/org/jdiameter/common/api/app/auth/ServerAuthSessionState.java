/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app.auth;

import org.jdiameter.common.api.app.IAppSessionState;


public enum ServerAuthSessionState implements IAppSessionState<ServerAuthSessionState> {

    IDLE(0), OPEN(1), DISCONNECTED(2);

    private final int value;

    private ServerAuthSessionState(int val) {
        value = val;
    }

    public final int getValue() {
        return value;
    }

    public final ServerAuthSessionState fromInt(int val) throws IllegalArgumentException {
        switch (val) {
            case 0:
                return IDLE;
            case 1:
                return OPEN;
            case 2:
                return DISCONNECTED;
            default:
                throw new IllegalArgumentException();
        }
    }
}