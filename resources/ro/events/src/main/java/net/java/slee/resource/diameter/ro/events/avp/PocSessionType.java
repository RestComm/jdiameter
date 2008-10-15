package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

/**
 * Java class to represent the PocSessionType enumerated type.
 * Author: baranowb
 */
public class PocSessionType implements net.java.slee.resource.diameter.base.events.avp.Enumerated, java.io.Serializable{
    public static final int _AD_HOC_POC_GROUP_SESSION=3;

    public static final int _CHAT_POC_GROUP_SESSION=1;

    public static final int _ONE_TO_ONE_POC_SESSION=0;

    public static final int _PRE_ARRANGED_POC_GROUP_SESSION=2;

    public static final net.java.slee.resource.diameter.ro.events.avp.PocSessionType AD_HOC_POC_GROUP_SESSION=new PocSessionType(_AD_HOC_POC_GROUP_SESSION);

    public static final net.java.slee.resource.diameter.ro.events.avp.PocSessionType CHAT_POC_GROUP_SESSION=new PocSessionType(_CHAT_POC_GROUP_SESSION);

    public static final net.java.slee.resource.diameter.ro.events.avp.PocSessionType ONE_TO_ONE_POC_SESSION=new PocSessionType(_ONE_TO_ONE_POC_SESSION);

    public static final net.java.slee.resource.diameter.ro.events.avp.PocSessionType PRE_ARRANGED_POC_GROUP_SESSION=new PocSessionType(_PRE_ARRANGED_POC_GROUP_SESSION);

    private PocSessionType(int v)
    {
    	value=v;
    }
    /**
     * Return the value of this instance of this enumerated type.
     */
    public static PocSessionType fromInt(int type) {
        switch(type) {
        case _AD_HOC_POC_GROUP_SESSION: return AD_HOC_POC_GROUP_SESSION;

        case _CHAT_POC_GROUP_SESSION: return CHAT_POC_GROUP_SESSION;

        case _ONE_TO_ONE_POC_SESSION: return ONE_TO_ONE_POC_SESSION;

        case _PRE_ARRANGED_POC_GROUP_SESSION: return PRE_ARRANGED_POC_GROUP_SESSION;
            default: throw new IllegalArgumentException("Invalid PocSessionType value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _AD_HOC_POC_GROUP_SESSION: return "AD_HOC_POC_GROUP_SESSION";

        case _CHAT_POC_GROUP_SESSION: return "CHAT_POC_GROUP_SESSION";

        case _ONE_TO_ONE_POC_SESSION: return "ONE_TO_ONE_POC_SESSION";

        case _PRE_ARRANGED_POC_GROUP_SESSION: return "PRE_ARRANGED_POC_GROUP_SESSION";
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
