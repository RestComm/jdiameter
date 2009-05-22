/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package net.java.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;

/**
 * Diameter Sh AVP codes constants.
 *
 */
public class DiameterShAvpCodes {

    private DiameterShAvpCodes() {}

    /**
     * AVP Code defined by Diameter specification for User-Identity AVP.  Data type
     * is Grouped.
     */
    public static final int USER_IDENTITY = 700;
    /**
     * AVP Data Type defined by Diameter specification for User-Identity AVP.
     */
    public static final DiameterAvpType USER_IDENTITY_AVP_TYPE = DiameterAvpType.fromString("Grouped");

    /**
     * AVP Code defined by Diameter specification for MSISDN AVP.  Data type
     * is OctetString.
     */
    public static final int MSISDN = 701;
    /**
     * AVP Data Type defined by Diameter specification for MSISDN AVP.
     */
    public static final DiameterAvpType MSISDN_AVP_TYPE = DiameterAvpType.fromString("OctetString");

    /**
     * AVP Code defined by Diameter specification for User-Data AVP.  Data type
     * is UserData.
     */
    public static final int USER_DATA = 702;
    /**
     * AVP Data Type defined by Diameter specification for User-Data AVP.
     */
    public static final DiameterAvpType USER_DATA_AVP_TYPE = DiameterAvpType.fromString("UserData");

    /**
     * AVP Code defined by Diameter specification for Data-Reference AVP.  Data type
     * is Enumerated.
     */
    public static final int DATA_REFERENCE = 703;
    /**
     * AVP Data Type defined by Diameter specification for Data-Reference AVP.
     */
    public static final DiameterAvpType DATA_REFERENCE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");

    /**
     * AVP Code defined by Diameter specification for Service-Indication AVP.  Data type
     * is OctetString.
     */
    public static final int SERVICE_INDICATION = 704;
    /**
     * AVP Data Type defined by Diameter specification for Service-Indication AVP.
     */
    public static final DiameterAvpType SERVICE_INDICATION_AVP_TYPE = DiameterAvpType.fromString("OctetString");

    /**
     * AVP Code defined by Diameter specification for Subs-Req-Type AVP.  Data type
     * is Enumerated.
     */
    public static final int SUBS_REQ_TYPE = 705;
    /**
     * AVP Data Type defined by Diameter specification for Subs-Req-Type AVP.
     */
    public static final DiameterAvpType SUBS_REQ_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");

    /**
     * AVP Code defined by Diameter specification for Requested-Domain AVP.  Data type
     * is Enumerated.
     */
    public static final int REQUESTED_DOMAIN = 706;
    /**
     * AVP Data Type defined by Diameter specification for Requested-Domain AVP.
     */
    public static final DiameterAvpType REQUESTED_DOMAIN_AVP_TYPE = DiameterAvpType.fromString("Enumerated");

    /**
     * AVP Code defined by Diameter specification for Current-Location AVP.  Data type
     * is Enumerated.
     */
    public static final int CURRENT_LOCATION = 707;
    /**
     * AVP Data Type defined by Diameter specification for Current-Location AVP.
     */
    public static final DiameterAvpType CURRENT_LOCATION_AVP_TYPE = DiameterAvpType.fromString("Enumerated");

    /**
     * AVP Code defined by Diameter specification for Identity-Set AVP.  Data type
     * is Enumerated.
     */
    public static final int IDENTITY_SET = 708;
    /**
     * AVP Data Type defined by Diameter specification for Identity-Set AVP.
     */
    public static final DiameterAvpType IDENTITY_SET_AVP_TYPE = DiameterAvpType.fromString("Enumerated");

    /**
     * AVP Code defined by Diameter specification for Expiry-Time AVP.  Data type
     * is Time.
     */
    public static final int EXPIRY_TIME = 709;
    /**
     * AVP Data Type defined by Diameter specification for Expiry-Time AVP.
     */
    public static final DiameterAvpType EXPIRY_TIME_AVP_TYPE = DiameterAvpType.fromString("Time");

    /**
     * AVP Code defined by Diameter specification for Send-Data-Indication AVP.  Data type
     * is Enumerated.
     */
    public static final int SEND_DATA_INDICATION = 710;
    /**
     * AVP Data Type defined by Diameter specification for Send-Data-Indication AVP.
     */
    public static final DiameterAvpType SEND_DATA_INDICATION_AVP_TYPE = DiameterAvpType.fromString("Enumerated");

    /**
     * AVP Code defined by Diameter specification for Server-Name AVP.  Data type
     * is UTF8String.
     */
    public static final int SERVER_NAME = 602;
    /**
     * AVP Data Type defined by Diameter specification for Server-Name AVP.
     */
    public static final DiameterAvpType SERVER_NAME_AVP_TYPE = DiameterAvpType.fromString("UTF8String");

    /**
     * AVP Code defined by Diameter specification for Supported-Features AVP.  Data type
     * is Grouped.
     */
    public static final int SUPPORTED_FEATURES = 628;
    /**
     * AVP Data Type defined by Diameter specification for Supported-Features AVP.
     */
    public static final DiameterAvpType SUPPORTED_FEATURES_AVP_TYPE = DiameterAvpType.fromString("Grouped");

    /**
     * AVP Code defined by Diameter specification for Feature-List-ID AVP.  Data type
     * is Unsigned32.
     */
    public static final int FEATURE_LIST_ID = 629;
    /**
     * AVP Data Type defined by Diameter specification for Feature-List-ID AVP.
     */
    public static final DiameterAvpType FEATURE_LIST_ID_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");

    /**
     * AVP Code defined by Diameter specification for Feature-List AVP.  Data type
     * is Unsigned32.
     */
    public static final int FEATURE_LIST = 630;
    /**
     * AVP Data Type defined by Diameter specification for Feature-List AVP.
     */
    public static final DiameterAvpType FEATURE_LIST_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");

    /**
     * AVP Code defined by Diameter specification for Supported-Applications AVP.  Data type
     * is Grouped.
     */
    public static final int SUPPORTED_APPLICATIONS = 631;
    /**
     * AVP Data Type defined by Diameter specification for Supported-Applications AVP.
     */
    public static final DiameterAvpType SUPPORTED_APPLICATIONS_AVP_TYPE = DiameterAvpType.fromString("Grouped");

    /**
     * AVP Code defined by Diameter specification for Public-Identity AVP.  Data type
     * is UTF8String.
     */
    public static final int PUBLIC_IDENTITY = 601;
    /**
     * AVP Data Type defined by Diameter specification for Public-Identity AVP.
     */
    public static final DiameterAvpType PUBLIC_IDENTITY_AVP_TYPE = DiameterAvpType.fromString("UTF8String");

}
