package net.java.slee.resource.diameter.base.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;



/**
 * Java class to represent the ReAuthRequestType enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Re-Auth-Request-Type AVP (AVP Code 285) is of type Enumerated and is included in application-specific auth answers to inform the client of the action expected upon expiration of the Authorization-Lifetime. If the answer message contains an Authorization-Lifetime AVP with a positive value, the Re-Auth-Request-Type AVP MUST be present in an answer message. 
 *
 * @author Open Cloud
 */

public class ReAuthRequestTypeAvp implements Serializable, Enumerated {

    public static final int _AUTHORIZE_ONLY = 0;
    public static final int _AUTHORIZE_AUTHENTICATE = 1;

    /**
     * An authorization only re-auth is expected upon expiration of the Authorization-Lifetime. This is the default value if the AVP is not present in answer messages that include the Authorization- Lifetime. 
     */
    public static final ReAuthRequestTypeAvp AUTHORIZE_ONLY = new ReAuthRequestTypeAvp(_AUTHORIZE_ONLY);

    /**
     * An authentication and authorization re-auth is expected upon expiration of the Authorization-Lifetime. 
     */
    public static final ReAuthRequestTypeAvp AUTHORIZE_AUTHENTICATE = new ReAuthRequestTypeAvp(_AUTHORIZE_AUTHENTICATE);

    private ReAuthRequestTypeAvp(int value) {
        this.value = value;
    }

    public static ReAuthRequestTypeAvp fromInt(int type) {
        switch(type) {
            case _AUTHORIZE_ONLY: return AUTHORIZE_ONLY;
            case _AUTHORIZE_AUTHENTICATE: return AUTHORIZE_AUTHENTICATE;
            default: throw new IllegalArgumentException("Invalid ReAuthRequestType value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _AUTHORIZE_ONLY: return "AUTHORIZE_ONLY";
            case _AUTHORIZE_AUTHENTICATE: return "AUTHORIZE_AUTHENTICATE";
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
