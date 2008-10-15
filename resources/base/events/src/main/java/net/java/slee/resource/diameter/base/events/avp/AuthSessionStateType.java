package net.java.slee.resource.diameter.base.events.avp;


import java.io.Serializable;
import java.io.StreamCorruptedException;



/**
 * Java class to represent the AuthSessionState enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Auth-Session-State AVP (AVP Code 277) is of type Enumerated and specifies whether state is maintained for a particular session. The client MAY include this AVP in requests as a hint to the server, but the value in the server's answer message is binding. 
 *
 * @author Open Cloud
 */

public class AuthSessionStateType implements Serializable, Enumerated {

    public static final int _STATE_MAINTAINED = 0;
    public static final int _NO_STATE_MAINTAINED = 1;

    /**
     * This value is used to specify that session state is being maintained, and the access device MUST issue a session termination message when service to the user is terminated. This is the default value. 
     */
    public static final AuthSessionStateType STATE_MAINTAINED = new AuthSessionStateType(_STATE_MAINTAINED);

    /**
     * This value is used to specify that no session termination messages will be sent by the access device upon expiration of the Authorization-Lifetime. 
     */
    public static final AuthSessionStateType NO_STATE_MAINTAINED = new AuthSessionStateType(_NO_STATE_MAINTAINED);

    private AuthSessionStateType(int value) {
        this.value = value;
    }

    public static AuthSessionStateType fromInt(int type) {
        switch(type) {
            case _STATE_MAINTAINED: return STATE_MAINTAINED;
            case _NO_STATE_MAINTAINED: return NO_STATE_MAINTAINED;
            default: throw new IllegalArgumentException("Invalid AuthSessionState value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _STATE_MAINTAINED: return "STATE_MAINTAINED";
            case _NO_STATE_MAINTAINED: return "NO_STATE_MAINTAINED";
            default: return "<Invalid Value>";
        }
    }

    private Object readResolve() throws StreamCorruptedException {
        try {
            return fromInt(value);
        }
        catch (IllegalArgumentException iae) {
            throw new StreamCorruptedException("Invalid internal state found: " + value);
        }
    }

    private int value;
}
