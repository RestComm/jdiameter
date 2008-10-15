package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the DrmContent enumerated type.
 * Author: baranowb
 */
public class DrmContent implements Enumerated, java.io.Serializable{
    public static final int _NO=0;

    public static final int _YES=1;

    public static final net.java.slee.resource.diameter.ro.events.avp.DrmContent NO=new DrmContent(_NO);

    public static final net.java.slee.resource.diameter.ro.events.avp.DrmContent YES=new DrmContent(_YES);

    
    private DrmContent(int v)
    {
    	value=v;
    }
    
    /**
     * Return the value of this instance of this enumerated type.
     */
    public static DrmContent fromInt(int type) {
        switch(type) {
            case _YES: return YES;
            case _NO: return NO;
            
            default: throw new IllegalArgumentException("Invalid DrmContent value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _YES: return "YES";
        case _NO: return "NO";
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
