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
 * Java class to represent the RequestedDomain enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Requested-Domain AVP is of type Enumerated, and indicates the access domain for which certain data e.g. user state) are requested. 
 *
 * @author Open Cloud
 */

public class RequestedDomainType implements Serializable, Enumerated {

    public static final int _CS_DOMAIN = 0;
    public static final int _PS_DOMAIN = 1;

    /**
     * The requested data apply to the CS domain. 
     */
    public static final RequestedDomainType CS_DOMAIN = new RequestedDomainType(_CS_DOMAIN);

    /**
     * The requested data apply to the PS domain. 
     */
    public static final RequestedDomainType PS_DOMAIN = new RequestedDomainType(_PS_DOMAIN);

    private RequestedDomainType(int value) {
        this.value = value;
    }

    public static RequestedDomainType fromInt(int type) {
        switch(type) {
            case _CS_DOMAIN: return CS_DOMAIN;
            case _PS_DOMAIN: return PS_DOMAIN;
            default: throw new IllegalArgumentException("Invalid RequestedDomain value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _CS_DOMAIN: return "CS_DOMAIN";
            case _PS_DOMAIN: return "PS_DOMAIN";
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
