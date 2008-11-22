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
 * Java class to represent the DataReference enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Data-Reference AVP is of type Enumerated, and indicates the type of the requested user data in the operation UDR and SNR. Its exact values and meaning is defined in 3GPP TS 29.328 [1]. 
 *
 * @author Open Cloud
 */

public class DataReferenceType implements Serializable, Enumerated {

    public static final int _REPOSITORY_DATA = 0;
    public static final int _IMS_PUBLIC_IDENTITY = 10;
    public static final int _IMS_USER_STATE = 11;
    public static final int _S_CSCFNAME = 12;
    public static final int _INITIAL_FILTER_CRITERIA = 13;
    public static final int _LOCATION_INFORMATION = 14;
    public static final int _USER_STATE = 15;
    public static final int _CHARGING_INFORMATION = 16;
    public static final int _MSISDN = 17;

    /**
     * 
     */
    public static final DataReferenceType REPOSITORY_DATA = new DataReferenceType(_REPOSITORY_DATA);

    /**
     * 
     */
    public static final DataReferenceType IMS_PUBLIC_IDENTITY = new DataReferenceType(_IMS_PUBLIC_IDENTITY);

    /**
     * 
     */
    public static final DataReferenceType IMS_USER_STATE = new DataReferenceType(_IMS_USER_STATE);

    /**
     * 
     */
    public static final DataReferenceType S_CSCFNAME = new DataReferenceType(_S_CSCFNAME);

    /**
     * This value is used to request initial filter criteria relevant to the requesting AS 
     */
    public static final DataReferenceType INITIAL_FILTER_CRITERIA = new DataReferenceType(_INITIAL_FILTER_CRITERIA);

    /**
     * 
     */
    public static final DataReferenceType LOCATION_INFORMATION = new DataReferenceType(_LOCATION_INFORMATION);

    /**
     * 
     */
    public static final DataReferenceType USER_STATE = new DataReferenceType(_USER_STATE);

    /**
     * 
     */
    public static final DataReferenceType CHARGING_INFORMATION = new DataReferenceType(_CHARGING_INFORMATION);

    /**
     * 
     */
    public static final DataReferenceType MSISDN = new DataReferenceType(_MSISDN);

    private DataReferenceType(int value) {
        this.value = value;
    }

    public static DataReferenceType fromInt(int type) {
        switch(type) {
            case _REPOSITORY_DATA: return REPOSITORY_DATA;
            case _IMS_PUBLIC_IDENTITY: return IMS_PUBLIC_IDENTITY;
            case _IMS_USER_STATE: return IMS_USER_STATE;
            case _S_CSCFNAME: return S_CSCFNAME;
            case _INITIAL_FILTER_CRITERIA: return INITIAL_FILTER_CRITERIA;
            case _LOCATION_INFORMATION: return LOCATION_INFORMATION;
            case _USER_STATE: return USER_STATE;
            case _CHARGING_INFORMATION: return CHARGING_INFORMATION;
            case _MSISDN: return MSISDN;
            default: throw new IllegalArgumentException("Invalid DataReference value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
            case _REPOSITORY_DATA: return "REPOSITORY_DATA";
            case _IMS_PUBLIC_IDENTITY: return "IMS_PUBLIC_IDENTITY";
            case _IMS_USER_STATE: return "IMS_USER_STATE";
            case _S_CSCFNAME: return "S_CSCFNAME";
            case _INITIAL_FILTER_CRITERIA: return "INITIAL_FILTER_CRITERIA";
            case _LOCATION_INFORMATION: return "LOCATION_INFORMATION";
            case _USER_STATE: return "USER_STATE";
            case _CHARGING_INFORMATION: return "CHARGING_INFORMATION";
            case _MSISDN: return "MSISDN";
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
