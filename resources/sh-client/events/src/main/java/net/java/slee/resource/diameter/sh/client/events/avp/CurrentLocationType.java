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
 * Java class to represent the CurrentLocation enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Current-Location AVP is of type Enumerated, and indicates whether an active location retrieval has to be initiated or not. 
 *
 * @author Open Cloud
 */

public class CurrentLocationType implements Serializable, Enumerated {

    public static final int _DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL = 0;
    public static final int _INITIATE_ACTIVE_LOCATION_RETRIEVAL = 1;

    /**
     * The request indicates that the initiation of an active location retrieval is not required. 
     */
    public static final CurrentLocationType DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL = new CurrentLocationType(_DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL);

    /**
     * It is requested that an active location retrieval is initiated. 
     */
    public static final CurrentLocationType INITIATE_ACTIVE_LOCATION_RETRIEVAL = new CurrentLocationType(_INITIATE_ACTIVE_LOCATION_RETRIEVAL);

    private CurrentLocationType(int value) {
        this.value = value;
    }

    public static CurrentLocationType fromInt(int type) {
        switch(type) {
            case _DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL: return DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL;
            case _INITIATE_ACTIVE_LOCATION_RETRIEVAL: return INITIATE_ACTIVE_LOCATION_RETRIEVAL;
            default: throw new IllegalArgumentException("Invalid CurrentLocation value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL: return "DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL";
            case _INITIATE_ACTIVE_LOCATION_RETRIEVAL: return "INITIATE_ACTIVE_LOCATION_RETRIEVAL";
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
