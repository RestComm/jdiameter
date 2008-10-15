package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

/**
 * Java class to represent the PsAppendFreeFormatData enumerated type.
 * Author: baranowb
 */
public class PsAppendFreeFormatData implements net.java.slee.resource.diameter.base.events.avp.Enumerated, java.io.Serializable{
    public static final int _APPEND=0;

    public static final int _OVERWRITE=1;

    /**
     * If this AVP is present and indicates ???Append???, the GGSN shall append the received PS free format data to the PS free format data stored for the online charging session.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.PsAppendFreeFormatData APPEND=new PsAppendFreeFormatData(_APPEND);

    /**
     * If this AVP is absent or in value ???Overwrite???, the GGSN shall overwrite all PS free format data already stored for the online charging session.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.PsAppendFreeFormatData OVERWRITE=new PsAppendFreeFormatData(_OVERWRITE);

    private PsAppendFreeFormatData(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static PsAppendFreeFormatData fromInt(int type) {
        switch(type) {
            case _APPEND: return APPEND;
            case _OVERWRITE: return OVERWRITE;

            default: throw new IllegalArgumentException("Invalid DisconnectCause value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _APPEND: return "APPEND";
        case _OVERWRITE: return "OVERWRITE";
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
