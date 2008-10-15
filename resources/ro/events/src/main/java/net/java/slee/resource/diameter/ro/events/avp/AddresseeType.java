package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the AddresseeType enumerated type.
 * Author: baranowb
 */
public class AddresseeType implements Enumerated, java.io.Serializable{
    public static final int _BCC=2;

    public static final int _CC=1;

    public static final int _TO=0;

    public static final net.java.slee.resource.diameter.ro.events.avp.AddresseeType BCC=new AddresseeType(_BCC);

    public static final net.java.slee.resource.diameter.ro.events.avp.AddresseeType CC=new AddresseeType(_CC);

    public static final net.java.slee.resource.diameter.ro.events.avp.AddresseeType TO=new AddresseeType(_TO);

    private AddresseeType(int v)
    {
    	value=v;
    }
  
    public static AddresseeType  fromInt(int type) {
        switch(type) {
            case _BCC: return BCC;
            case _CC: return CC;
            case _TO: return TO;
            default: throw new IllegalArgumentException("Invalid DisconnectCause value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _TO: return "TO";
            case _CC: return "CC";
            case _BCC: return "BCC";
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
