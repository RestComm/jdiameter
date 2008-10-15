package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the Originator enumerated type.
 * Author: baranowb
 */
public class Originator implements Enumerated, java.io.Serializable{
    public static final int _CALLED_PARTY=1;

    public static final int _CALLING_PARTY=0;

    public static final net.java.slee.resource.diameter.ro.events.avp.Originator CALLED_PARTY=new Originator(_CALLED_PARTY);

    public static final net.java.slee.resource.diameter.ro.events.avp.Originator CALLING_PARTY=new Originator(_CALLING_PARTY);

    
    private Originator(int v)
    {
    	value=v;
    }
    /**
     * Return the value of this instance of this enumerated type.
     */
    public static Originator fromInt(int type) {
        switch(type) {
            case _CALLED_PARTY: return CALLED_PARTY;
            case _CALLING_PARTY: return CALLING_PARTY;
          
            default: throw new IllegalArgumentException("Invalid Originator value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _CALLED_PARTY: return "CALLED_PARTY";
        case _CALLING_PARTY: return "CALLING_PARTY";
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
