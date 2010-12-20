package org.jdiameter.common.api.app.gx;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 *
 * ClientRoSessionState.java
 *
 * <br>Super project:  mobicents
 * <br>4:21:53 PM Dec 2, 2008
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public enum ClientGxSessionState implements IAppSessionState<ClientGxSessionState> {

    IDLE(0),
    PENDING_EVENT(1),
    PENDING_INITIAL(2),
    PENDING_UPDATE(3),
    PENDING_TERMINATION(4),
    PENDING_BUFFERED(5),
    OPEN(6);
    private int stateValue = -1;

    ClientGxSessionState(int stateV) {
        this.stateValue = stateV;
    }

    public ClientGxSessionState fromInt(int v) throws IllegalArgumentException {
        switch (v) {
            case 0:
                return IDLE;
            case 1:
                return PENDING_EVENT;
            case 2:
                return PENDING_INITIAL;
            case 3:
                return PENDING_UPDATE;
            case 4:
                return PENDING_TERMINATION;
            case 5:
                return PENDING_BUFFERED;
            case 6:
                return OPEN;
            default:
                throw new IllegalArgumentException("Illegal value of int representation!!!!");
        }
    }

    public int getValue() {
        return stateValue;
    }
}
