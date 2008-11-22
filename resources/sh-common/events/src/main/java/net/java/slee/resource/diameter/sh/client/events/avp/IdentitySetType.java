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
 * Java class to represent the IdentitySet enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Identity-Set AVP is of type Enumerated and indicates the requested set of IMS Public Identities. 
 *
 * @author Open Cloud
 */

public class IdentitySetType implements Serializable, Enumerated {

    public static final int _ALL_IDENTITIES = 0;
    public static final int _REGISTERED_IDENTITIES = 1;
    public static final int _IMPLICIT_IDENTITIES = 2;

    /**
     * 
     */
    public static final IdentitySetType ALL_IDENTITIES = new IdentitySetType(_ALL_IDENTITIES);

    /**
     * 
     */
    public static final IdentitySetType REGISTERED_IDENTITIES = new IdentitySetType(_REGISTERED_IDENTITIES);

    /**
     * 
     */
    public static final IdentitySetType IMPLICIT_IDENTITIES = new IdentitySetType(_IMPLICIT_IDENTITIES);

    private IdentitySetType(int value) {
        this.value = value;
    }

    public static IdentitySetType fromInt(int type) {
        switch(type) {
            case _ALL_IDENTITIES: return ALL_IDENTITIES;
            case _REGISTERED_IDENTITIES: return REGISTERED_IDENTITIES;
            case _IMPLICIT_IDENTITIES: return IMPLICIT_IDENTITIES;
            default: throw new IllegalArgumentException("Invalid IdentitySet value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _ALL_IDENTITIES: return "ALL_IDENTITIES";
            case _REGISTERED_IDENTITIES: return "REGISTERED_IDENTITIES";
            case _IMPLICIT_IDENTITIES: return "IMPLICIT_IDENTITIES";
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
