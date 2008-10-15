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

/**
 * The application Id is used to point out an application
 * that is supported or used. It is a combination of Authentication application id,
 * Accounting application id and Vendor id.
 * @version 1.5.1 Final
 */

public final class ApplicationId implements Serializable {

    /**
     * Undefined value of id for application identifier
     */
    public static final long UNDEFINED_VALUE = 0x0;

    /**
     * Standards-track application IDs are by Designated Expert with Specification Required [IANA]
     */
    public interface Standard {

        long DIAMETER_COMMON_MESSAGE = 0x0;
        long NASREQ = 0x1;
        long MOBILE_IP = 0x2;
        long DIAMETER_BASE_ACCOUNTING = 0x3;
        long RELAY = 0xffffffff;

    }

    /**
     * IANA [IANA] has assigned the range 0x00000001 to 0x00ffffff for
     * standards-track applications; and 0x01000000 - 0xfffffffe for vendor
     * specific applications, on a first-come, first-served basis.  The
     * following values are allocated.
     */
    public interface Ranges {

       long STANDARDS_TRACK_APPLICATIONS_MIN = 0x00000001;
       long STANDARDS_TRACK_APPLICATIONS_MAX = 0x00ffffff;

       long VENDOR_SPECIFIC_APPLICATIONS_MIN = 0x01000000;
       long VENDOR_SPECIFIC_APPLICATIONS_MAX = 0xfffffffe;
    }


    private long venId = UNDEFINED_VALUE;
    private long authId = UNDEFINED_VALUE;
    private long acctId = UNDEFINED_VALUE;


    /**
     * Create instance of ApplicationId use Authentication-App-Id
     * @param authAppId authentication application id
     * @return instance of class
     */
    public static ApplicationId createByAuthAppId(long authAppId) {
        return new ApplicationId(UNDEFINED_VALUE, authAppId, UNDEFINED_VALUE);
    }

    /**
     * Create instance of ApplicationId use Accounting-Applicaion-Id
     * @param acchAppId accounting applicaion Id
     * @return instance of class
     */
    public static ApplicationId createByAccAppId(long acchAppId) {
        return new ApplicationId(UNDEFINED_VALUE, UNDEFINED_VALUE, acchAppId);
    }

    /**
     * Create instance of ApplicationId use Authentication-App-Id and Vendor-Id
     * @param vendorId  vendor specific id
     * @param authAppId authentication application id
     * @return instance of class
     */
    public static ApplicationId createByAuthAppId(long vendorId, long authAppId) {
        return new ApplicationId(vendorId, authAppId, UNDEFINED_VALUE);
    }

    /**
     * Create instance of ApplicationId use Accounting-Applicaion-Id and Vendor-Id
     * @param vendorId vendor specific id
     * @param acchAppId accounting applicaion Id
     * @return instance of class
     */
    public static ApplicationId createByAccAppId(long vendorId, long acchAppId) {
        return new ApplicationId(vendorId, UNDEFINED_VALUE, acchAppId);
    }

    /**
     * Create instance
     * @param vendorId vendor specific id
     * @param authAppId authentication application id
     * @param acctAppId accounting applicaion Id
     */
    private ApplicationId(long vendorId, long authAppId, long acctAppId) {
        this.authId = authAppId;
        this.acctId = acctAppId;
        this.venId = vendorId;
    }

    /**
     * @return Vendor-Isd
     */
    public long getVendorId() {
        return venId;
    }

    /**
     * @return Authentication-Application-Id
     */
    public long getAuthAppId() {
        return authId;
    }

    /**
     * @return Accounting-Application-Id
     */

    public long getAcctAppId() {
        return acctId;
    }

    /**
     * @param obj check object
     * @return true if check object equals current instance (all appId is equals)
     */

    public boolean equals(Object obj) {
        if (obj instanceof ApplicationId) {
            ApplicationId appId = (ApplicationId) obj;
            return authId  == appId.authId &&
                    acctId == appId.acctId &&
                    venId  == appId.venId;
        } else {
            return false;
        }
    }

    /**
     * @return hash code of object
     */
    public int hashCode() {
        int result;
        result = (int) (venId ^ (venId >>> 32));
        result = 31 * result + (int) (authId ^ (authId >>> 32));
        result = 31 * result + (int) (acctId ^ (acctId >>> 32));
        return result;
    }

    /**
     * @return String representation of object
     */
    public String toString() {
        return new StringBuffer("AppId [").
                    append("Vendor-Id:").append(venId).
                    append("; Auth-Application-Id:").append(authId).
                    append("; Acct-Application-Id:").append(acctId).
                    append("]").
                    toString();
    }
}
