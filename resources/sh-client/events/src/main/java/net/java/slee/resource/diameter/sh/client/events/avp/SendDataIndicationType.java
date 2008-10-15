/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.sh.client.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;


/**
 * Java class to represent the SendDataIndication enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Send-Data-Indication AVP is of type Enumerated. If present it indicates that the sender requests the User-Data. The following values are defined: 
 *
 * @author Open Cloud
 */

public class SendDataIndicationType implements Serializable, Enumerated {

    public static final int _USER_DATA_NOT_REQUESTED = 0;
    public static final int _USER_DATA_REQUESTED = 1;

    /**
     * 
     */
    public static final SendDataIndicationType USER_DATA_NOT_REQUESTED = new SendDataIndicationType(_USER_DATA_NOT_REQUESTED);

    /**
     * 
     */
    public static final SendDataIndicationType USER_DATA_REQUESTED = new SendDataIndicationType(_USER_DATA_REQUESTED);

    private SendDataIndicationType(int value) {
        this.value = value;
    }

    public static SendDataIndicationType fromInt(int type) {
        switch(type) {
            case _USER_DATA_NOT_REQUESTED: return USER_DATA_NOT_REQUESTED;
            case _USER_DATA_REQUESTED: return USER_DATA_REQUESTED;
            default: throw new IllegalArgumentException("Invalid SendDataIndication value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _USER_DATA_NOT_REQUESTED: return "USER_DATA_NOT_REQUESTED";
            case _USER_DATA_REQUESTED: return "USER_DATA_REQUESTED";
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
