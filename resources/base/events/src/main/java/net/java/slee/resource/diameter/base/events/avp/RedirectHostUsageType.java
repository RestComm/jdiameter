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
package net.java.slee.resource.diameter.base.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;



/**
 * Java class to represent the RedirectHostUsage enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Redirect-Host-Usage AVP (AVP Code 261) is of type Enumerated. This AVP MAY be present in answer messages whose 'E' bit is set and the Result-Code AVP is set to DIAMETER_REDIRECT_INDICATION. When present, this AVP dictates how the routing entry resulting from the Redirect-Host is to be used. 
 *
 * @author Open Cloud
 */

public class RedirectHostUsageType implements Serializable, Enumerated {

    public static final int _DONT_CACHE = 0;
    public static final int _ALL_SESSION = 1;
    public static final int _ALL_REALM = 2;
    public static final int _REALM_AND_APPLICATION = 3;
    public static final int _ALL_APPLICATION = 4;
    public static final int _ALL_HOST = 5;
    public static final int _ALL_USER = 6;

    /**
     * The host specified in the Redirect-Host AVP should not be cached. This is the default value. 
     */
    public static final RedirectHostUsageType DONT_CACHE = new RedirectHostUsageType(_DONT_CACHE);

    /**
     * All messages within the same session, as defined by the same value of the Session-ID AVP MAY be sent to the host specified in the Redirect-Host AVP. 
     */
    public static final RedirectHostUsageType ALL_SESSION = new RedirectHostUsageType(_ALL_SESSION);

    /**
     * All messages destined for the realm requested MAY be sent to the host specified in the Redirect-Host AVP. 
     */
    public static final RedirectHostUsageType ALL_REALM = new RedirectHostUsageType(_ALL_REALM);

    /**
     * All messages for the application requested to the realm specified MAY be sent to the host specified in the Redirect-Host AVP. 
     */
    public static final RedirectHostUsageType REALM_AND_APPLICATION = new RedirectHostUsageType(_REALM_AND_APPLICATION);

    /**
     * All messages for the application requested MAY be sent to the host specified in the Redirect-Host AVP. 
     */
    public static final RedirectHostUsageType ALL_APPLICATION = new RedirectHostUsageType(_ALL_APPLICATION);

    /**
     * All messages that would be sent to the host that generated the Redirect-Host MAY be sent to the host specified in the Redirect- Host AVP. 
     */
    public static final RedirectHostUsageType ALL_HOST = new RedirectHostUsageType(_ALL_HOST);

    /**
     * All messages for the user requested MAY be sent to the host specified in the Redirect-Host AVP. 
     */
    public static final RedirectHostUsageType ALL_USER = new RedirectHostUsageType(_ALL_USER);

    private RedirectHostUsageType(int value) {
        this.value = value;
    }

    public static RedirectHostUsageType fromInt(int type) {
        switch(type) {
            case _DONT_CACHE: return DONT_CACHE;
            case _ALL_SESSION: return ALL_SESSION;
            case _ALL_REALM: return ALL_REALM;
            case _REALM_AND_APPLICATION: return REALM_AND_APPLICATION;
            case _ALL_APPLICATION: return ALL_APPLICATION;
            case _ALL_HOST: return ALL_HOST;
            case _ALL_USER: return ALL_USER;
            default: throw new IllegalArgumentException("Invalid RedirectHostUsage value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _DONT_CACHE: return "DONT_CACHE";
            case _ALL_SESSION: return "ALL_SESSION";
            case _ALL_REALM: return "ALL_REALM";
            case _REALM_AND_APPLICATION: return "REALM_AND_APPLICATION";
            case _ALL_APPLICATION: return "ALL_APPLICATION";
            case _ALL_HOST: return "ALL_HOST";
            case _ALL_USER: return "ALL_USER";
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
