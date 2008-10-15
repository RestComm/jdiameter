package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the FileRepairSupported enumerated type.
 * Author: baranowb
 */
public class FileRepairSupported implements Enumerated, java.io.Serializable{
    public static final int _NOT_SUPPORTED=2;

    public static final int _SUPPORTED=1;

    /**
     * The MBMS user service does not support point-to-point file repair.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported NOT_SUPPORTED=new FileRepairSupported(_NOT_SUPPORTED);

    /**
     * The MBMS user service does support point-to-point file repair.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported SUPPORTED=new FileRepairSupported(_SUPPORTED);

    private FileRepairSupported(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static  FileRepairSupported fromInt(int type) {
        switch(type) {
            case _NOT_SUPPORTED: return NOT_SUPPORTED;
            case _SUPPORTED: return SUPPORTED;
            
            default: throw new IllegalArgumentException("Invalid FileRepairSupported value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _NOT_SUPPORTED: return "NOT_SUPPORTED";
        case _SUPPORTED: return "SUPPORTED";
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
