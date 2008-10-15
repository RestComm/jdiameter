package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the MediaInitiatorFlag enumerated type.
 * Author: baranowb
 */
public class MediaInitiatorFlag implements Enumerated, java.io.Serializable{
    public static final int _CALLED_PARTY=0;

    public static final int _CALLING_PARTY=1;

    public static final int _UNKNOWN=2;

    public static final net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag CALLED_PARTY=new MediaInitiatorFlag(_CALLED_PARTY);

    public static final net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag CALLING_PARTY=new MediaInitiatorFlag(_CALLING_PARTY);

    public static final net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag UNKNOWN=new MediaInitiatorFlag(_UNKNOWN);

    private MediaInitiatorFlag(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static MediaInitiatorFlag fromInt(int type) {
        switch(type) {
        case _CALLED_PARTY: return CALLED_PARTY;
        case _CALLING_PARTY: return CALLING_PARTY;
        case _UNKNOWN: return UNKNOWN;
            default: throw new IllegalArgumentException("Invalid MediaInitiatorFlag value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _CALLED_PARTY: return "CALLED_PARTY";
            case _CALLING_PARTY: return "CALLING_PARTY";
            case _UNKNOWN: return "UNKNOWN";
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

    private int value=0;

}
