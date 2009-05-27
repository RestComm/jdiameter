/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

/**
 * The Avp class implements a Diameter AVP. This class allows applications to build and read arbitrary
 * Diameter AVP objects.
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * Serializable interface allows use this class in SLEE Event objects
 * @version 1.5.1 Final
 */

public interface Avp extends Wrapper, Serializable {

    /**
     * The Accounting-Realtime-required avp code
     */
    public static final int ACCOUNTING_REALTIME_REQUIRED = 483;

    /**
     * The Auth-Request-Type avp code
     */
    public static final int AUTH_REQUEST_TYPE = 274;

    /**
     * The Authorization-Lifetime avp code
     */
    public static final int AUTHORIZATION_LIFETIME = 291;

    /**
     * The Auth-Grace-Period avp code
     */
    public static final int AUTH_GRACE_PERIOD = 276;

    /**
     * The Auth-Session-State avp code
     */
    public static final int AUTH_SESSION_STATE = 277;

    /**
     * The Class avp code
     */
    public static final int CLASS = 25;

    /**
     * The E2E-Sequence-Avp avp code
     */
    public static final int E2E_SEQUENCE_AVP = 300;

    /**
     * The Error-reporting-host avp code
     */
    public static final int ERROR_REPORTING_HOST = 294;

    /**
     * The Event-Timestamp avp code
     */
    public static final int EVENT_TIMESTAMP = 55;

    /**
     * The File-Avp avp code
     */
    public static final int FAILED_AVP = 279;

    /**
     * The Acct-Interim-Interval avp code
     */
    public static final int ACCT_INTERIM_INTERVAL = 85;

    /**
     * The User-Name avp code
     */
    public static final int USER_NAME = 1;
    /**
     * The Result-Code avp code
     */
    public static final int RESULT_CODE = 268;
    /**
      * Experemental-Result avp code
    */
    public static final int EXPERIMENTAL_RESULT = 297;
    /**
     * The Experimental-Result-Code avp code
     */
    public static final int EXPERIMENTAL_RESULT_CODE = 298;
    /**
     * The Termination-Cause avp code
     */
    public static final int TERMINATION_CAUSE = 295;
    /**
     * The FirmWare-Revision avp code
     */
    public static final int FIRMWARE_REVISION = 267;
    /**
     * The Host-IP-Address avp code
     */
    public static final int HOST_IP_ADDRESS = 257;
    /**
     * The Muti-Round-Timeout avp code
     */
    public static final int MULTI_ROUND_TIMEOUT = 272;
    /**
     * The Origin-Host avp code
     */
    public static final int ORIGIN_HOST = 264;
    /**
     * The Origin-Realm avp code
     */
    public static final int ORIGIN_REALM = 296;
    /**
     * The Origin-State-Id avp code
     */
    public static final int ORIGIN_STATE_ID = 278;
    /**
     * The Rediect-Host avp code
     */
    public static final int REDIRECT_HOST = 292;
    /**
     * The Redirect-Host-Usage avp code
     */
    public static final int REDIRECT_HOST_USAGE = 261;
    /**
     * The Redirect-Max-Cache-Time avp code
     */
    public static final int REDIRECT_MAX_CACHE_TIME = 262;
    /**
     * The Product-Name avp code
     */
    public static final int PRODUCT_NAME = 269;
    /**
     * The Session-Id avp code
     */
    public static final int SESSION_ID = 263;
    /**
     * The Session-Timeout avp code
     */
    public static final int SESSION_TIMEOUT = 27;
    /**
     * The Session-Binding avp code
     */
    public static final int SESSION_BINDING = 270;
    /**
     * The Session-Server-Failover avp code
     */
    public static final int SESSION_SERVER_FAILOVER = 271;
    /**
     * The Destination-Host avp code
     */
    public static final int DESTINATION_HOST = 293;
    /**
     * The Destination-Realm avp code
     */
    public static final int DESTINATION_REALM = 283;
    /**
     * The Route-Record avp code
     */
    public static final int ROUTE_RECORD = 282;
    /**
     * The Proxy-Info avp code
     */
    public static final int PROXY_INFO = 284;
    /**
     * The Proxy-Host avp code
     */
    public static final int PROXY_HOST = 280;
    /**
     * The Proxy-State avp code
     */
    public static final int PROXY_STATE = 33;
    /**
     * The Authentication-Application-Id avp code
     */
    public static final int AUTH_APPLICATION_ID = 258;
    /**
     * The Accounting-Application-Id avp code
     */
    public static final int ACCT_APPLICATION_ID = 259;
    /**
     * The Inband-Security-Id avp code
     */
    public static final int INBAND_SECURITY_ID = 299;
    /**
     * The Vendor-Id avp code
     */
    public static final int VENDOR_ID = 266;
    /**
     * The Supported-Vendor-Id avp code
     */
    public static final int SUPPORTED_VENDOR_ID = 265;
    /**
     * The Vendor-Specific-Application-Id avp code
     */
    public static final int VENDOR_SPECIFIC_APPLICATION_ID = 260;
    /**
     * The Re-Authentication-Request-type avp code
     */
    public static final int RE_AUTH_REQUEST_TYPE = 285;
    /**
     * The Accouting-Record-Type avp code
     */
    public static final int ACC_RECORD_TYPE = 480;
    /**
     * The Accouting-Record-Number avp code
     */
    public static final int ACC_RECORD_NUMBER = 485;
    /**
     * The Accouting-Session-Id avp code
     */
    public static final int ACC_SESSION_ID = 44;
    /**
     * The Accouting-Sub-Session-Id avp code
     */
    public static final int ACC_SUB_SESSION_ID = 287;
    /**
     * The Accouting-Multi-Session-Id avp code
     */
    public static final int ACC_MULTI_SESSION_ID = 50;
    /**
     * The Disconnect cause avp code
     */
    public static final int DISCONNECT_CAUSE = 273;
    /**
     * The Error-Message avp code
     */
    public static final int ERROR_MESSAGE = 281;

    /**
     * @return the AVP code.
     */
    int getCode();

    /**
     * @return true if Vendor-id is present in Avp header
     */
    boolean isVendorId();

    /**
     *
     * @return true if flag M is set 1
     */
    boolean isMandatory();

    /**
     * @return true if flag E is set 1
     */
    boolean isEncrypted();

    /**
     * @return Vendor-Id if it present (-1 if it not avalible)
     */
    long getVendorId();

    /**
     * @return data as byte array (Raw format)
     * @throws AvpDataException if data has incorrect format
     */
    byte[] getRaw()throws AvpDataException;

    /**
     * @return data as an String (Use AS-ASCI code page)
     * @throws AvpDataException if data has incorrect format
     */
    String getOctetString() throws AvpDataException;

    /**
     * @return data as an integer
     * @throws AvpDataException if data has incorrect format
     */
    int getInteger32() throws AvpDataException;

    /**
     * @return data as an unsigned  long
     * @throws AvpDataException if data has incorrect format
     */
    long getInteger64() throws AvpDataException;

    /**
     * @return data as an unsigned integer
     * @throws AvpDataException if data has incorrect format
     */
    long getUnsigned32() throws AvpDataException;

    /**
     * @return data as an long
     * @throws AvpDataException if data has incorrect format
     */
    long getUnsigned64() throws AvpDataException;

    /**
     * @return data as an float
     * @throws AvpDataException if data has incorrect format
     */
    float getFloat32() throws AvpDataException;

    /**
     *
     * @return data as an double
     * @throws AvpDataException if data has incorrect format
     */
    double getFloat64() throws AvpDataException;

    /**
     * @return data as an Diameter Address (Inet4Address or Inet6Address)
     * @throws AvpDataException if data has incorrect format
     */
    InetAddress getAddress() throws AvpDataException;

    /**
     * @return data as an Diameter Time (millisecond is truncated)
     * @throws AvpDataException if data has incorrect format
     */
    Date getTime() throws AvpDataException;

    /**
     * @return data as an String (Use UTF-8 code page)
     * @throws AvpDataException if data has incorrect format
     */
    String getUTF8String() throws AvpDataException;

    /**
     * @return data as an String (Use AS-ASCI code page)
     * @throws AvpDataException if data has incorrect format
     */
    String getDiameterIdentity() throws AvpDataException;

    /**
     * @return data as an Diamter URI
     * @throws AvpDataException if data has incorrect format
     */
    URI getDiameterURI() throws AvpDataException;

    /**
     * @return data as an AVP group.
     * @throws AvpDataException if data has incorrect format
     */
    AvpSet getGrouped() throws AvpDataException;

    public byte[] getRawData();
}
