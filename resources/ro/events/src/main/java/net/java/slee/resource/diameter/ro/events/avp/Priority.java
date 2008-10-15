package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

/**
 * Java class to represent the Priority enumerated type.
 * Author: baranowb
 */
public class Priority implements net.java.slee.resource.diameter.base.events.avp.Enumerated, java.io.Serializable{
    public static final int _HIGH=2;

    public static final int _LOW=0;

    public static final int _NORMAL=1;

    public static final net.java.slee.resource.diameter.ro.events.avp.Priority HIGH=new Priority(_HIGH);

    public static final net.java.slee.resource.diameter.ro.events.avp.Priority LOW=new Priority(_LOW);

    public static final net.java.slee.resource.diameter.ro.events.avp.Priority NORMAL=new Priority(_NORMAL);

    private Priority(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static Priority fromInt(int type) {
        switch(type) {
            case _HIGH: return HIGH;
            case _LOW: return LOW;
            case _NORMAL: return NORMAL;
            default: throw new IllegalArgumentException("Invalid Priority value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _HIGH: return "HIGH";
        case _LOW: return "LOW";
        case _NORMAL: return "NORMAL";
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
