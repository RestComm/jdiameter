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

/**
 * Diameter AVP code constants defined by the specification.
 *
 * @author Open Cloud
 */
public class DiameterAvpCodes {

    /**
     * AVP Code defined by Diameter specification for Acct-Interim-Interval AVP.  Data type
     * is Unsigned32.
     */
    public static final int ACCT_INTERIM_INTERVAL = 85;

    /**
     * AVP Code defined by Diameter specification for Accounting-Realtime-Required AVP.  Data type
     * is Enumerated.
     */
    public static final int ACCOUNTING_REALTIME_REQUIRED = 483;

    /**
     * AVP Code defined by Diameter specification for Acct-Multi-Session-Id AVP.  Data type
     * is UTF8String.
     */
    public static final int ACCT_MULTI_SESSION_ID = 50;

    /**
     * AVP Code defined by Diameter specification for Accounting-Record-Number AVP.  Data type
     * is Unsigned32.
     */
    public static final int ACCOUNTING_RECORD_NUMBER = 485;

    /**
     * AVP Code defined by Diameter specification for Accounting-Record-Type AVP.  Data type
     * is Enumerated.
     */
    public static final int ACCOUNTING_RECORD_TYPE = 480;

    /**
     * AVP Code defined by Diameter specification for Accounting-Session-Id AVP.  Data type
     * is OctetString.
     */
    public static final int ACCOUNTING_SESSION_ID = 44;

    /**
     * AVP Code defined by Diameter specification for Accounting-Sub-Session-Id AVP.  Data type
     * is Unsigned64.
     */
    public static final int ACCOUNTING_SUB_SESSION_ID = 287;

    /**
     * AVP Code defined by Diameter specification for Acct-Application-Id AVP.  Data type
     * is Unsigned32.
     */
    public static final int ACCT_APPLICATION_ID = 259;

    /**
     * AVP Code defined by Diameter specification for Auth-Application-Id AVP.  Data type
     * is Unsigned32.
     */
    public static final int AUTH_APPLICATION_ID = 258;

    /**
     * AVP Code defined by Diameter specification for Auth-Request-Type AVP.  Data type
     * is Enumerated.
     */
    public static final int AUTH_REQUEST_TYPE = 274;

    /**
     * AVP Code defined by Diameter specification for Authorization-Lifetime AVP.  Data type
     * is Unsigned32.
     */
    public static final int AUTHORIZATION_LIFETIME = 291;

    /**
     * AVP Code defined by Diameter specification for Auth-Grace-Period AVP.  Data type
     * is Unsigned32.
     */
    public static final int AUTH_GRACE_PERIOD = 276;

    /**
     * AVP Code defined by Diameter specification for Auth-Session-State AVP.  Data type
     * is Enumerated.
     */
    public static final int AUTH_SESSION_STATE = 277;

    /**
     * AVP Code defined by Diameter specification for Re-Auth-Request-Type AVP.  Data type
     * is Enumerated.
     */
    public static final int RE_AUTH_REQUEST_TYPE = 285;

    /**
     * AVP Code defined by Diameter specification for Class AVP.  Data type
     * is OctetString.
     */
    public static final int CLASS_AVP = 25;

    /**
     * AVP Code defined by Diameter specification for Destination-Host AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int DESTINATION_HOST = 293;

    /**
     * AVP Code defined by Diameter specification for Destination-Realm AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int DESTINATION_REALM = 283;

    /**
     * AVP Code defined by Diameter specification for Disconnect-Cause AVP.  Data type
     * is Enumerated.
     */
    public static final int DISCONNECT_CAUSE = 273;

    /**
     * AVP Code defined by Diameter specification for E2E-Sequence AVP.  Data type
     * is Grouped.
     */
    public static final int E2E_SEQUENCE = 300;

    /**
     * AVP Code defined by Diameter specification for Error-Message AVP.  Data type
     * is UTF8String.
     */
    public static final int ERROR_MESSAGE = 281;

    /**
     * AVP Code defined by Diameter specification for Error-Reporting-Host AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int ERROR_REPORTING_HOST = 294;

    /**
     * AVP Code defined by Diameter specification for Event-Timestamp AVP.  Data type
     * is Time.
     */
    public static final int EVENT_TIMESTAMP = 55;

    /**
     * AVP Code defined by Diameter specification for Experimental-Result AVP.  Data type
     * is Grouped.
     */
    public static final int EXPERIMENTAL_RESULT = 297;

    /**
     * AVP Code defined by Diameter specification for Experimental-Result-Code AVP.  Data type
     * is Unsigned32.
     */
    public static final int EXPERIMENTAL_RESULT_CODE = 298;

    /**
     * AVP Code defined by Diameter specification for Failed-AVP AVP.  Data type
     * is Grouped.
     */
    public static final int FAILED_AVP = 279;

    /**
     * AVP Code defined by Diameter specification for Firmware-Revision AVP.  Data type
     * is Unsigned32.
     */
    public static final int FIRMWARE_REVISION = 267;

    /**
     * AVP Code defined by Diameter specification for Host-IP-Address AVP.  Data type
     * is Address.
     */
    public static final int HOST_IP_ADDRESS = 257;

    /**
     * AVP Code defined by Diameter specification for Inband-Security-Id AVP.  Data type
     * is Unsigned32.
     */
    public static final int INBAND_SECURITY_ID = 299;

    /**
     * AVP Code defined by Diameter specification for Multi-Round-Time-Out AVP.  Data type
     * is Unsigned32.
     */
    public static final int MULTI_ROUND_TIME_OUT = 272;

    /**
     * AVP Code defined by Diameter specification for Origin-Host AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int ORIGIN_HOST = 264;

    /**
     * AVP Code defined by Diameter specification for Origin-Realm AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int ORIGIN_REALM = 296;

    /**
     * AVP Code defined by Diameter specification for Origin-State-Id AVP.  Data type
     * is Unsigned32.
     */
    public static final int ORIGIN_STATE_ID = 278;

    /**
     * AVP Code defined by Diameter specification for Product-Name AVP.  Data type
     * is UTF8String.
     */
    public static final int PRODUCT_NAME = 269;

    /**
     * AVP Code defined by Diameter specification for Proxy-Host AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int PROXY_HOST = 280;

    /**
     * AVP Code defined by Diameter specification for Proxy-Info AVP.  Data type
     * is Grouped.
     */
    public static final int PROXY_INFO = 284;

    /**
     * AVP Code defined by Diameter specification for Proxy-State AVP.  Data type
     * is OctetString.
     */
    public static final int PROXY_STATE = 33;

    /**
     * AVP Code defined by Diameter specification for Redirect-Host AVP.  Data type
     * is DiameterURI.
     */
    public static final int REDIRECT_HOST = 292;

    /**
     * AVP Code defined by Diameter specification for Redirect-Host-Usage AVP.  Data type
     * is Enumerated.
     */
    public static final int REDIRECT_HOST_USAGE = 261;

    /**
     * AVP Code defined by Diameter specification for Redirect-Max-Cache-Time AVP.  Data type
     * is Unsigned32.
     */
    public static final int REDIRECT_MAX_CACHE_TIME = 262;

    /**
     * AVP Code defined by Diameter specification for Result-Code AVP.  Data type
     * is Unsigned32.
     */
    public static final int RESULT_CODE = 268;

    /**
     * AVP Code defined by Diameter specification for Route-Record AVP.  Data type
     * is DiameterIdentity.
     */
    public static final int ROUTE_RECORD = 282;

    /**
     * AVP Code defined by Diameter specification for Session-Id AVP.  Data type
     * is UTF8String.
     */
    public static final int SESSION_ID = 263;

    /**
     * AVP Code defined by Diameter specification for Session-Timeout AVP.  Data type
     * is Unsigned32.
     */
    public static final int SESSION_TIMEOUT = 27;

    /**
     * AVP Code defined by Diameter specification for Session-Binding AVP.  Data type
     * is Unsigned32.
     */
    public static final int SESSION_BINDING = 270;

    /**
     * AVP Code defined by Diameter specification for Session-Server-Failover AVP.  Data type
     * is Enumerated.
     */
    public static final int SESSION_SERVER_FAILOVER = 271;

    /**
     * AVP Code defined by Diameter specification for Supported-Vendor-Id AVP.  Data type
     * is Unsigned32.
     */
    public static final int SUPPORTED_VENDOR_ID = 265;

    /**
     * AVP Code defined by Diameter specification for Termination-Cause AVP.  Data type
     * is Enumerated.
     */
    public static final int TERMINATION_CAUSE = 295;

    /**
     * AVP Code defined by Diameter specification for User-Name AVP.  Data type
     * is UTF8String.
     */
    public static final int USER_NAME = 1;

    /**
     * AVP Code defined by Diameter specification for Vendor-Id AVP.  Data type
     * is Unsigned32.
     */
    public static final int VENDOR_ID = 266;

    /**
     * AVP Code defined by Diameter specification for Vendor-Specific-Application-Id AVP.  Data type
     * is Grouped.
     */
    public static final int VENDOR_SPECIFIC_APPLICATION_ID = 260;

    /**
     * AVP Code defined by Diameter specification for Filter-Id AVP.  Data type
     * is UTF8String.
     */
    public static final int FILTER_ID = 11;

}