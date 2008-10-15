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
 * Java class to represent the SubsReqType enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Subs-Req-Type AVP is of type Enumerated, and indicates the type of the subscription-to-notifications request. 
 *
 * @author Open Cloud
 */

public class SubsReqType implements Serializable, Enumerated {

    public static final int _SUBSCRIBE = 0;
    public static final int _UNSUBSCRIBE = 1;

    /**
     * This value is used by an AS to subscribe to notifications of changes in data. 
     */
    public static final SubsReqType SUBSCRIBE = new SubsReqType(_SUBSCRIBE);

    /**
     * This value is used by an AS to unsubscribe to notifications of changes in data. 
     */
    public static final SubsReqType UNSUBSCRIBE = new SubsReqType(_UNSUBSCRIBE);

    private SubsReqType(int value) {
        this.value = value;
    }

    public static SubsReqType fromInt(int type) {
        switch(type) {
            case _SUBSCRIBE: return SUBSCRIBE;
            case _UNSUBSCRIBE: return UNSUBSCRIBE;
            default: throw new IllegalArgumentException("Invalid SubsReqType value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _SUBSCRIBE: return "SUBSCRIBE";
            case _UNSUBSCRIBE: return "UNSUBSCRIBE";
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
