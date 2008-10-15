package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the MbmsUserServiceType enumerated type.
 * Author: baranowb
 */
public class MbmsUserServiceType implements Enumerated, java.io.Serializable{
    public static final int _DOWNLOAD=1;

    public static final int _STREAMING=2;

    /**
     * The MBMS user service of type: download.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType DOWNLOAD=new MbmsUserServiceType(_DOWNLOAD);

    /**
     * The MBMS user service is of type: streaming.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType STREAMING=new MbmsUserServiceType(_STREAMING);

    private MbmsUserServiceType(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static MbmsUserServiceType  fromInt(int type) {
        switch(type) {
            case _DOWNLOAD: return DOWNLOAD;
            case _STREAMING: return STREAMING;
            
            default: throw new IllegalArgumentException("Invalid DisconnectCause value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _DOWNLOAD: return "DOWNLOAD";
        case _STREAMING: return "STREAMING";
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
