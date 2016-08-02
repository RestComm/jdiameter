 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.api;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

/**
 * The Avp class implements a Diameter AVP. This class allows applications to build and read arbitrary Diameter AVP objects. Wrapper
 * interface allows adapt message to any driver vendor specific interface Serializable interface allows use this class in SLEE Event objects
 *
 * @version 1.5.1 Final
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface Avp extends Wrapper, Serializable {

  /**
   * The Accounting-Realtime-Required AVP code
   */
  int ACCOUNTING_REALTIME_REQUIRED = 483;

  /**
   * The Auth-Request-Type AVP code
   */
  int AUTH_REQUEST_TYPE = 274;

  /**
   * The Authorization-Lifetime AVP code
   */
  int AUTHORIZATION_LIFETIME = 291;

  /**
   * The Auth-Grace-Period AVP code
   */
  int AUTH_GRACE_PERIOD = 276;

  /**
   * The Auth-Session-State AVP code
   */
  int AUTH_SESSION_STATE = 277;

  /**
   * The Class AVP code
   */
  int CLASS = 25;

  /**
   * The E2E-Sequence-Avp AVP code
   */
  int E2E_SEQUENCE_AVP = 300;

  /**
   * The Error-reporting-host AVP code
   */
  int ERROR_REPORTING_HOST = 294;

  /**
   * The Event-Timestamp AVP code
   */
  int EVENT_TIMESTAMP = 55;

  /**
   * The File-Avp AVP code
   */
  int FAILED_AVP = 279;

  /**
   * The Acct-Interim-Interval AVP code
   */
  int ACCT_INTERIM_INTERVAL = 85;

  /**
   * The User-Name AVP code
   */
  int USER_NAME = 1;

  /**
   * The Result-Code AVP code
   */
  int RESULT_CODE = 268;

  /**
   * Experimental-Result AVP code
   */
  int EXPERIMENTAL_RESULT = 297;

  /**
   * The Experimental-Result-Code AVP code
   */
  int EXPERIMENTAL_RESULT_CODE = 298;

  /**
   * The Termination-Cause AVP code
   */
  int TERMINATION_CAUSE = 295;

  /**
   * The FirmWare-Revision AVP code
   */
  int FIRMWARE_REVISION = 267;

  /**
   * The Host-IP-Address AVP code
   */
  int HOST_IP_ADDRESS = 257;

  /**
   * The Muti-Round-Timeout AVP code
   */
  int MULTI_ROUND_TIMEOUT = 272;

  /**
   * The Origin-Host AVP code
   */
  int ORIGIN_HOST = 264;

  /**
   * The Origin-Realm AVP code
   */
  int ORIGIN_REALM = 296;

  /**
   * The Origin-State-Id AVP code
   */
  int ORIGIN_STATE_ID = 278;

  /**
   * The Redirect-Host AVP code
   */
  int REDIRECT_HOST = 292;

  /**
   * The Redirect-Host-Usage AVP code
   */
  int REDIRECT_HOST_USAGE = 261;

  /**
   * The Redirect-Max-Cache-Time AVP code
   */
  int REDIRECT_MAX_CACHE_TIME = 262;

  /**
   * The Product-Name AVP code
   */
  int PRODUCT_NAME = 269;

  /**
   * The Session-Id AVP code
   */
  int SESSION_ID = 263;

  /**
   * The Session-Timeout AVP code
   */
  int SESSION_TIMEOUT = 27;

  /**
   * The Session-Binding AVP code
   */
  int SESSION_BINDING = 270;

  /**
   * The Session-Server-Failover AVP code
   */
  int SESSION_SERVER_FAILOVER = 271;

  /**
   * The Destination-Host AVP code
   */
  int DESTINATION_HOST = 293;

  /**
   * The Destination-Realm AVP code
   */
  int DESTINATION_REALM = 283;

  /**
   * The Route-Record AVP code
   */
  int ROUTE_RECORD = 282;

  /**
   * The Proxy-Info AVP code
   */
  int PROXY_INFO = 284;

  /**
   * The Proxy-Host AVP code
   */
  int PROXY_HOST = 280;

  /**
   * The Proxy-State AVP code
   */
  int PROXY_STATE = 33;

  /**
   * The Authentication-Application-Id AVP code
   */
  int AUTH_APPLICATION_ID = 258;
  /**
   * The Accounting-Application-Id AVP code
   */
  int ACCT_APPLICATION_ID = 259;

  /**
   * The Inband-Security-Id AVP code
   */
  int INBAND_SECURITY_ID = 299;

  /**
   * The Vendor-Id AVP code
   */
  int VENDOR_ID = 266;

  /**
   * The Supported-Vendor-Id AVP code
   */
  int SUPPORTED_VENDOR_ID = 265;

  /**
   * The Vendor-Specific-Application-Id AVP code
   */
  int VENDOR_SPECIFIC_APPLICATION_ID = 260;

  /**
   * The Re-Authentication-Request-type AVP code
   */
  int RE_AUTH_REQUEST_TYPE = 285;

  /**
   * The Accounting-Record-Type AVP code
   */
  int ACC_RECORD_TYPE = 480;

  /**
   * The Accounting-Record-Number AVP code
   */
  int ACC_RECORD_NUMBER = 485;

  /**
   * The Accounting-Session-Id AVP code
   */
  int ACC_SESSION_ID = 44;

  /**
   * The Accounting-Sub-Session-Id AVP code
   */
  int ACC_SUB_SESSION_ID = 287;

  /**
   * The Accounting-Multi-Session-Id AVP code
   */
  int ACC_MULTI_SESSION_ID = 50;

  /**
   * The Disconnect cause AVP code
   */
  int DISCONNECT_CAUSE = 273;

  /**
   * The Error-Message AVP code
   */
  int ERROR_MESSAGE = 281;

  // RFC 4006 (Credit-Control-Application) AVPs

  /**
   * CCA (RFC4006) Correlation ID AVP code
   */
  int CC_CORRELATION_ID = 411;

  /**
   * CCA (RFC4006) Credit Control Input Octets AVP code
   */
  int CC_INPUT_OCTETS = 412;

  /**
   * CCA (RFC4006) Credit Control Money AVP code
   */
  int CC_MONEY = 413;

  /**
   * CCA (RFC4006) Credit Control Output Octets AVP code
   */
  int CC_OUTPUT_OCTETS = 414;

  /**
   * CCA (RFC4006) Credit Control Request Number AVP code
   */
  int CC_REQUEST_NUMBER = 415;

  /**
   * CCA (RFC4006) Request Type AVP code
   */
  int CC_REQUEST_TYPE = 416;

  /**
   * CCA (RFC4006) Credit Control Service Specific Units AVP code
   */
  int CC_SERVICE_SPECIFIC_UNITS = 417;

  /**
   * CCA (RFC4006) Credit Control Session Failover AVP code
   */
  int CC_SESSION_FAILOVER = 418;

  /**
   * CCA (RFC4006) Credit Control Sub Session ID AVP code
   */
  int CC_SUB_SESSION_ID = 419;

  /**
   * CCA (RFC4006) Credit Control Time AVP code
   */
  int CC_TIME = 420;

  /**
   * CCA (RFC4006) Credit Control Total Octets AVP code
   */
  int CC_TOTAL_OCTETS = 421;

  /**
   * CCA (RFC4006) Credit Control Unit Type AVP code
   */
  int CC_UNIT_TYPE = 454;

  /**
   * CCA (RFC4006) Check Balance result AVP code
   */
  int CHECK_BALANCE_RESULT = 422;

  /**
   * CCA (RFC4006) Cost Information AVP code
   */
  int COST_INFORMATION = 423;

  /**
   * CCA (RFC4006) Cost Unit AVP code
   */
  int COST_UNIT = 424;

  /**
   * CCA (RFC4006) Currency Code AVP code
   */
  int CURRENCY_CODE = 425;

  /**
   * CCA (RFC4006) Credit Control AVP code
   */
  int CREDIT_CONTROL = 426;

  /**
   * CCA (RFC4006) Credit Control Failure Handling AVP code
   */
  int CREDIT_CONTROL_FAILURE_HANDLING = 427;

  /**
   * CCA (RFC4006) Direct Debiting Failure Handling AVP code
   */
  int DIRECT_DEBITING_FAILURE_HANDLING = 428;
  /**
   * CCA (RFC4006) Exponent AVP code
   */
  int EXPONENT = 429;
  /**
   * CCA (RFC4006) Final Unit Action AVP code
   */
  int FINAL_UNIT_ACTION = 449;
  /**
   * CCA (RFC4006) Final Unit Indication AVP code
   */
  int FINAL_UNIT_INDICATION = 430;
  /**
   * CCA (RFC4006) Granted Service Unit AVP code
   */
  int GRANTED_SERVICE_UNIT = 431;
  /**
   * CCA (RFC4006) GSU Pool Identifier AVP code
   */
  int GSU_POOL_ID = 453;
  /**
   * CCA (RFC4006) GSU Pool Reference AVP code
   */
  int GSU_POOL_REFERENCE = 457;
  /**
   * CCA (RFC4006) Multiple Services Credit Control AVP code
   */
  int MULTIPLE_SERVICES_CREDIT_CONTROL = 456;
  /**
   * CCA (RFC4006) Multiple Services Indicator AVP code
   */
  int MULTIPLE_SERVICES_INDICATOR = 455;
  /**
   * CCA (RFC4006) Rating Group AVP code
   */
  int RATING_GROUP = 432;
  /**
   * CCA (RFC4006) Redirect Address Type AVP code
   */
  int REDIRECT_ADDRESS_TYPE = 433;
  /**
   * CCA (RFC4006) Redirect Server AVP code
   */
  int REDIRECT_SERVER = 434;
  /**
   * CCA (RFC4006) Redirect Address AVP code
   */
  int REDIRECT_ADDRESS = 435;

  /**
   * CCA (RFC4006) Requested Action AVP code
   */
  int REQUESTED_ACTION = 436;
  /**
   * CCA (RFC4006) Requested Service Unit AVP code
   */
  int REQUESTED_SERVICE_UNIT = 437;
  /**
   * CCA (RFC4006) Restriction Filter Rule AVP code
   */
  int RESTRICTION_FILTER_RULE = 438;
  /**
   * CCA (RFC4006) Service Context Id AVP code
   */
  int SERVICE_CONTEXT_ID = 461;

  /**
   * CCA (RFC4006) Service Id AVP code
   */
  int SERVICE_IDENTIFIER_CCA = 439;

  /**
   * CCA (RFC4006) Service Parameter Info AVP code
   */
  int SERVICE_PARAMETER_INFO = 440;

  /**
   * CCA (RFC4006) Service Parameter Type AVP code
   */
  int SERVICE_PARAMETER_TYPE = 441;

  /**
   * CCA (RFC4006) Service Parameter Value AVP code
   */
  int SERVICE_PARAMETER_VALUE = 442;

  /**
   * CCA (RFC4006) Subscription Id AVP code
   */
  int SUBSCRIPTION_ID = 443;

  /**
   * CCA (RFC4006) Subscription Id Data AVP code
   */
  int SUBSCRIPTION_ID_DATA = 444;

  /**
   * CCA (RFC4006) Subscription Id Type AVP code
   */
  int SUBSCRIPTION_ID_TYPE = 450;

  /**
   * CCA (RFC4006) Tariff Change Usage AVP code
   */
  int TARIFF_CHANGE_USAGE = 452;

  /**
   * CCA (RFC4006) Tariff Time Change AVP code
   */
  int TARIFF_TIME_CHANGE = 451;

  /**
   * CCA (RFC4006) Unit Value AVP code
   */
  int UNIT_VALUE = 445;

  /**
   * CCA (RFC4006) Used Service Unit AVP code
   */
  int USED_SERVICE_UNIT = 446;

  /**
   * CCA (RFC4006) User Equipment Info AVP code
   */
  int USER_EQUIPMENT_INFO = 458;

  /**
   * CCA (RFC4006) User Equipment Info Type AVP code
   */
  int USER_EQUIPMENT_INFO_TYPE = 459;

  /**
   * CCA (RFC4006) User Equipment Info Value AVP code
   */
  int USER_EQUIPMENT_INFO_VALUE = 460;

  /**
   * CCA (RFC4006) Value Digits AVP code
   */
  int VALUE_DIGITS = 447;

  /**
   * CCA (RFC4006) Validity Time AVP code
   */
  int VALIDITY_TIME = 448;

  // Cx/Dx IMS Interface AVPs

  /**
   * Cx/Dx (3GPP TS 29.229) Visited Network Identifier AVP code
   */
  int VISITED_NETWORK_ID = 600;

  /**
   * Cx/Dx (3GPP TS 29.229) Public Identity AVP code
   */
  int PUBLIC_IDENTITY = 601;

  /**
   * Cx/Dx (3GPP TS 29.229) Server Name AVP code
   */
  int SERVER_NAME = 602;

  /**
   * Cx/Dx (3GPP TS 29.229) Server Capabilities AVP code
   */
  int SERVER_CAPABILITIES = 603;

  /**
   * Cx/Dx (3GPP TS 29.229) Mandatory Capability AVP code
   */
  int MANDATORY_CAPABILITY = 604;

  /**
   * Cx/Dx (3GPP TS 29.229) Optional Capability AVP code
   */
  int OPTIONAL_CAPABILITY = 605;

  /**
   * Cx/Dx (3GPP TS 29.229) UserData AVP code
   */
  int USER_DATA_CXDX = 606; // why the hell there is double def, is 3GPP sane?

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Number Auth Items AVP code
   */
  int SIP_NUMBER_AUTH_ITEMS = 607;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Authentication Scheme AVP code
   */
  int SIP_AUTHENTICATION_SCHEME = 608;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Authenticate AVP code
   */
  int SIP_AUTHENTICATE = 609;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Authorization AVP code
   */
  int SIP_AUTHORIZATION = 610;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Authentication Context AVP code
   */
  int SIP_AUTHENTICATION_CONTEXT = 611;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Auth Data Item AVP code
   */
  int SIP_AUTH_DATA_ITEM = 612;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Item Number AVP code
   */
  int SIP_ITEM_NUMBER = 613;

  /**
   * Cx/Dx (3GPP TS 29.229) Server Assignment Type AVP code
   */
  int SERVER_ASSIGNMENT_TYPE = 614;

  /**
   * Cx/Dx (3GPP TS 29.229) Deregistration Reason AVP code
   */
  int DEREGISTRATION_REASON = 615;

  /**
   * Cx/Dx (3GPP TS 29.229) Reason Code AVP code
   */
  int REASON_CODE = 616;

  /**
   * Cx/Dx (3GPP TS 29.229) Reason Info AVP code
   */
  int REASON_INFO = 617;

  /**
   * Cx/Dx (3GPP TS 29.229) Charging Information AVP code
   */
  int CHARGING_INFORMATION = 618;

  /**
   * Cx/Dx (3GPP TS 29.229) Primary Event Charging Function Name AVP code
   */
  int PRI_EVENT_CHARGING_FUNCTION = 619;

  /**
   * Cx/Dx (3GPP TS 29.229) Secondary Event Charging Function Name AVP code
   */
  int SEC_EVENT_CHARGING_FUNCTION = 620;

  /**
   * Cx/Dx (3GPP TS 29.229) Primary Charging Collection Function Name AVP code
   */
  int PRI_CHARGING_COLLECTION_FUNCTION = 621;

  /**
   * Cx/Dx (3GPP TS 29.229) Secondary Charging Collection Function Name AVP code
   */
  int SEC_CHARGING_COLLECTION_FUNCTION = 622;

  /**
   * Cx/Dx (3GPP TS 29.229) User Authorization Type AVP code
   */
  int USER_AUTORIZATION_TYPE = 623;

  /**
   * Cx/Dx (3GPP TS 29.229) User Data Already Available AVP code
   */
  int USER_DATA_ALREADY_AVAILABLE = 624;

  /**
   * Cx/Dx (3GPP TS 29.229) Confidentiality Key AVP code
   */
  int CONFIDENTIALITY_KEY = 625;

  /**
   * Cx/Dx (3GPP TS 29.229) Integrity Key AVP code
   */
  int INTEGRITY_KEY = 626;

  /**
   * Supported Features AVP code
   */
  int SUPPORTED_FEATURES = 628;

  /**
   * Cx/Dx (3GPP TS 29.229) Feature List ID AVP code
   */
  int FEATURE_LIST_ID = 629;

  /**
   * Cx/Dx (3GPP TS 29.229) Feature List AVP code
   */
  int FEATURE_LIST = 630;

  /**
   * Cx/Dx (3GPP TS 29.229) Supported Applications AVP code
   */
  int SUPPORTED_APPLICATIONS = 631;

  /**
   * Cx/Dx (3GPP TS 29.229) Associated Identities AVP code
   */
  int ASSOCAITED_IDENTITIES = 632;

  /**
   * Cx/Dx (3GPP TS 29.229) originating Request AVP code
   */
  int ORIGINATING_REQUEST = 633;

  /**
   * Wildcarded PSI AVP code
   */
  int WILDCARDED_PSI = 634;

  /**
   * Cx/Dx (3GPP TS 29.229) SIP Digest Authenticate AVP code
   */
  int SIP_DIGEST_AUTHENTICATE = 635;

  /**
   * Wildcarded IMPU AVP code
   */
  int WILDCARDED_IMPU = 636;

  /**
   * Cx/Dx (3GPP TS 29.229) UAR Flags AVP code
   */
  int UAR_FLAGS = 637;

  /**
   * Cx/Dx (3GPP TS 29.229) Loose Route Indication AVP code
   */
  int LOOSE_ROUTE_INDICATION = 638;

  /**
   * Cx/Dx (3GPP TS 29.229) SCSCF Restoration Info AVP code
   */
  int SCSCF_RESTORATION_INFO = 639;

  /**
   * Cx/Dx (3GPP TS 29.229) Path AVP code
   */
  int PATH = 640;

  /**
   * Cx/Dx (3GPP TS 29.229) Contact AVP code
   */
  int CONTACT = 641;

  /**
   * Cx/Dx (3GPP TS 29.229) Subscription Info AVP code
   */
  int SUBSCRIPTION_INFO = 642;

  /**
   * Cx/Dx (3GPP TS 29.229) Call ID SIP Header AVP code
   */
  int CALL_ID_SIP_HEADER = 643;

  /**
   * Cx/Dx (3GPP TS 29.229) From SIP Header AVP code
   */
  int FROM_SIP_HEADER = 644;

  /**
   * Cx/Dx (3GPP TS 29.229) To SIP Header AVP code
   */
  int TO_SIP_HEADER = 645;

  /**
   * Cx/Dx (3GPP TS 29.229) Record Route AVP code
   */
  int RECORD_ROUTE = 646;

  /**
   * Cx/Dx (3GPP TS 29.229) Associated Registered Identities AVP code
   */
  int ASSOCIATED_REGISTERED_IDENTITIES = 647;

  /**
   * Cx/Dx (3GPP TS 29.229) Multiple Registration Indication AVP code
   */
  int MULTIPLE_REGISTRATION_INDICATION = 648;

  /**
   * Cx/Dx (3GPP TS 29.229) Restoration Info AVP code
   */
  int RESTORATION_INFO = 649;

  // Sh IMS Interface AVPs

  /**
   * Sh (3GPP TS 29.329) User Identity AVP code
   */
  int USER_IDENTITY = 700;

  /**
   * Sh (3GPP TS 29.329) User Data AVP code
   */
  int USER_DATA_SH = 702;

  /**
   * Sh (3GPP TS 29.329) Data Reference AVP code
   */
  int DATA_REFERENCE = 703;

  /**
   * Sh (3GPP TS 29.329) Service Indication AVP code
   */
  int SERVICE_INDICATION = 704;

  /**
   * Sh (3GPP TS 29.329) Subs Req Type AVP code
   */
  int SUBS_REQ_TYPE = 705;

  /**
   * Sh (3GPP TS 29.329) Requested Domain AVP code
   */
  int REQUESTED_DOMAIN = 706;

  /**
   * Sh (3GPP TS 29.329) Current Location AVP code
   */
  int CURRENT_LOCATION = 707;

  /**
   * Sh (3GPP TS 29.329) identity Set AVP code
   */
  int IDENTITY_SET = 708;

  /**
   * Sh (3GPP TS 29.329) Expiry Time AVP code
   */
  int EXPIRY_TIME = 709;

  /**
   * Sh (3GPP TS 29.329) Send Data Indication AVP code
   */
  int SEND_DATA_INDICATION = 710;

  /**
   * Sh (3GPP TS 29.329) DSAI Tag AVP code
   */
  int DSAI_TAG = 711;

  // Ro/Rf IMS Interfaces AVPs

  /**
   * Ro/Rf (3GPP TS 32.299) Low Balance Indication AVP code
   */
  int LOW_BALANCE_INDICATION = 2020;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP Charging Characteristics AVP code
   */
  int TGPP_CHARGING_CHARACTERISTICS = 13;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP Charging Id AVP code
   */
  int TGPP_CHARGING_ID = 2;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP GGSN MCC MNC AVP code
   */
  int TGPP_GGSN_MCC_MNC = 9;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP IMSI AVP code
   */
  int TGPP_IMSI = 1;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP IMSI MCC MNC AVP code
   */
  int TGPP_IMSI_MCC_MNC = 8;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP MS TimeZone AVP code
   */
  int TGPP_MS_TIMEZONE = 23;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP NSAPI AVP code
   */
  int TGPP_NSAPI = 10;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP PDP Type AVP code
   */
  int TGPP_PDP_TYPE = 3;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP RAT Type AVP code
   */
  int TGPP_RAT_TYPE = 21;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP Selection Mode AVP code
   */
  int TGPP_SELECTION_MODE = 12;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP Session Stop Indicator AVP code
   */
  int TGPP_SESSION_STOP_INDICATOR = 11;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP SGSN MCC MNC AVP code
   */
  int GPP_SGSN_MCC_MNC = 18;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP User Location Info AVP code
   */
  int GPP_USER_LOCATION_INFO = 22;

  /**
   * Ro/Rf (3GPP TS 32.299) 3GPP2 BSID AVP code
   */
  int TGPP2_BSID = 5535;

  /**
   * Ro/Rf (3GPP TS 32.299) Access Network Charging Identifier Value AVP code
   */
  int ACCESS_NETWORK_CHARGING_IDENTIFIER_VALUE = 503;

  /**
   * Ro/Rf (3GPP TS 32.299) Access Network Information AVP code
   */
  int ACCESS_NETWORK_INFORMATION = 1263;

  /**
   * Ro/Rf (3GPP TS 32.299) Accumulated Cost AVP code
   */
  int ACCUMULATED_COST = 2052;

  /**
   * Ro/Rf (3GPP TS 32.299) Adaptations AVP code
   */
  int ADAPTATIONS = 1217;

  /**
   * Ro/Rf (3GPP TS 32.299) Additional Content Information AVP code
   */
  int ADDITIONAL_CONTENT_INFORMATION = 1207;

  /**
   * Ro/Rf (3GPP TS 32.299) Additional Type Information AVP code
   */
  int ADDITIONAL_TYPE_INFORMATION = 1205;

  /**
   * Ro/Rf (3GPP TS 32.299) Address Data AVP code
   */
  int ADDRESS_DATA = 897;

  /**
   * Ro/Rf (3GPP TS 32.299) Address Domain AVP code
   */
  int ADDRESS_DOMAIN = 898;

  /**
   * Ro/Rf (3GPP TS 32.299) Addressee Type AVP code
   */
  int ADDRESSEE_TYPE = 1208;

  /**
   * Ro/Rf (3GPP TS 32.299) Address Type AVP code
   */
  int ADDRESS_TYPE = 899;

  /**
   * Ro/Rf (3GPP TS 32.299) AF Charging Identifier AVP code
   */
  int AF_CHARGING_IDENTIFIER = 505;

  /**
   * Ro/Rf (3GPP TS 32.299) AF Correlation Information AVP code
   */
  int AF_CORRELATION_INFORMATION = 1276;

  /**
   * Ro/Rf (3GPP TS 32.299) Allocation Retention Priority AVP code
   */
  int ALLOCATION_RETENTION_PRIORITY = 1034;

  /**
   * Ro/Rf (3GPP TS 32.299) Alternate Charged Party Address AVP code
   */
  int ALTERNATE_CHARGED_PARTY_ADDRESS = 1280;

  /**
   * Gx/Gxx (3GPP TS 29.212) APN aggregate max bitrate DL AVP code
   */
  int APN_AGGREGATE_MAX_BITRATE_DL = 1040;

  /**
   * Gx/Gxx (3GPP TS 29.212) APN aggregate max bitrate UL AVP code
   */
  int APN_AGGREGATE_MAX_BITRATE_UL = 1041;

  /**
   * Ro/Rf (3GPP TS 32.299) AoC Cost Information AVP code
   */
  int AOC_COST_INFORMATION = 2053;

  /**
   * Ro/Rf (3GPP TS 32.299) AoC Information AVP code
   */
  int AOC_INFORMATION = 2054;

  /**
   * Ro/Rf (3GPP TS 32.299) AoC Request Type AVP code
   */
  int AOC_REQUEST_TYPE = 2055;

  /**
   * Ro/Rf (3GPP TS 32.299) Application provided called party address AVP code
   */
  int APPLICATION_PROVIDED_CALLED_PARTY_ADDRESS = 837;

  /**
   * Ro/Rf (3GPP TS 32.299) Application Server AVP code
   */
  int APPLICATION_SERVER = 836;

  /**
   * Ro/Rf (3GPP TS 32.299) Application Server ID AVP code
   */
  int APPLICATION_SERVER_ID = 2101;

  /**
   * Ro/Rf (3GPP TS 32.299) Application Server Information AVP code
   */
  int APPLICATION_SERVER_INFORMATION = 850;

  /**
   * Ro/Rf (3GPP TS 32.299) Application Service Type AVP code
   */
  int APPLICATION_SERVICE_TYPE = 2102;

  /**
   * Ro/Rf (3GPP TS 32.299) Application Session ID AVP code
   */
  int APPLICATION_SESSION_ID = 2103;

  /**
   * Ro/Rf (3GPP TS 32.299) Applic ID AVP code
   */
  int APPLIC_ID = 1218;

  /**
   * Ro/Rf (3GPP TS 32.299) Associated Party Address AVP code
   */
  int ASSOCIATED_PARTY_ADDRESS = 2035;

  /**
   * Ro/Rf (3GPP TS 32.299) Associated URI AVP code
   */
  int ASSOCIATED_URI = 856;

  /**
   * Ro/Rf (3GPP TS 32.299) Authorized QoS AVP code
   */
  int AUTHORIZED_QOS = 849;

  /**
   * Ro/Rf (3GPP TS 32.299) Aux Applic Info AVP code
   */
  int AUX_APPLIC_INFO = 1219;

  /**
   * Ro/Rf (3GPP TS 32.299) Base Time Interval AVP code
   */
  int BASE_TIME_INTERVAL = 1265;

  /**
   * Gx/Gxx (3GPP TS 29.212) Bearer Identifier AVP code
   */
  int BEARER_IDENTIFIER = 1020;

  /**
   * Ro/Rf (3GPP TS 32.299) Bearer Service AVP code
   */
  int BEARER_SERVICE = 854;

  /**
   * Ro/Rf (3GPP TS 32.299) Called Asserted Identity AVP code
   */
  int CALLED_ASSERTED_IDENTITY = 1250;

  /**
   * Ro/Rf (3GPP TS 32.299) Called Party Address AVP code
   */
  int CALLED_PARTY_ADDRESS = 832;

  /**
   * Ro/Rf (3GPP TS 32.299) Calling Party Address AVP code
   */
  int CALLING_PARTY_ADDRESS = 831;

  /**
   * Ro/Rf (3GPP TS 32.299) Carrier Select Routing Information AVP code
   */
  int CARRIER_SELECT_ROUTING_INFORMATION = 2023;

  /**
   * Ro/Rf (3GPP TS 32.299) Cause Code AVP code
   */
  int CAUSE_CODE = 861;

  /**
   * Ro/Rf (3GPP TS 32.299) CG Address AVP code
   */
  int CG_ADDRESS = 846;

  /**
   * Ro/Rf (3GPP TS 32.299) Change Condition AVP code
   */
  int CHANGE_CONDITION = 2037;

  /**
   * Ro/Rf (3GPP TS 32.299) Change Time AVP code
   */
  int CHANGE_TIME = 2038;

  /**
   * Ro/Rf (3GPP TS 32.299) Charged Party AVP code
   */
  int CHARGED_PARTY = 857;

  /**
   * Ro/Rf (3GPP TS 32.299) Charging Rule Base Name AVP code
   */
  int CHARGING_RULE_BASE_NAME = 1004;

  /**
   * Ro/Rf (3GPP TS 32.299) Class Identifier AVP code
   */
  int CLASS_IDENTIFIER = 1214;

  /**
   * Ro/Rf (3GPP TS 32.299) Client Address AVP code
   */
  int CLIENT_ADDRESS = 2018;

  /**
   * Ro/Rf (3GPP TS 32.299) Content Class AVP code
   */
  int CONTENT_CLASS = 1220;

  /**
   * Ro/Rf (3GPP TS 32.299) Content Disposition AVP code
   */
  int CONTENT_DISPOSITION = 828;

  /**
   * Ro/Rf (3GPP TS 32.299) Content ID AVP code
   */
  int CONTENT_ID = 2116;

  /**
   * Ro/Rf (3GPP TS 32.299) Content Provider ID AVP code
   */
  int CONTENT_PROVIDER_ID = 2117;

  /**
   * Ro/Rf (3GPP TS 32.299) Content Length AVP code
   */
  int CONTENT_LENGTH = 827;

  /**
   * Ro/Rf (3GPP TS 32.299) Content Size AVP code
   */
  int CONTENT_SIZE = 1206;

  /**
   * Ro/Rf (3GPP TS 32.299) Content Type AVP code
   */
  int CONTENT_TYPE = 826;

  /**
   * Ro/Rf (3GPP TS 32.299) Current Tariff AVP code
   */
  int CURRENT_TARIFF = 2056;

  /**
   * Ro/Rf (3GPP TS 32.299) Data Coding Scheme AVP code
   */
  int DATA_CODING_SCHEME = 2001;

  /**
   * Ro/Rf (3GPP TS 32.299) DCD Information AVP code
   */
  int DCD_INFORMATION = 2115;

  /**
   * Ro/Rf (3GPP TS 32.299) Deferred Location Event Type AVP code
   */
  int DEFERRED_LOCATION_EVENT_TYPE = 1230;

  /**
   * Ro/Rf (3GPP TS 32.299) Delivery Report Requested AVP code
   */
  int DELIVERY_REPORT_REQUESTED = 1216;

  /**
   * Ro/Rf (3GPP TS 32.299) Delivery Status AVP code
   */
  int DELIVERY_STATUS = 2104;

  /**
   * Ro/Rf (3GPP TS 32.299) Destination Interface AVP code
   */
  int DESTINATION_INTERFACE = 2002;

  /**
   * Ro/Rf (3GPP TS 32.299) Diagnostics AVP code
   */
  int DIAGNOSTICS = 2039;

  /**
   * Ro/Rf (3GPP TS 32.299) Domain Name AVP code
   */
  int DOMAIN_NAME = 1200;

  /**
   * Ro/Rf (3GPP TS 32.299) DRM Content AVP code
   */
  int DRM_CONTENT = 1221;

  /**
   * Ro/Rf (3GPP TS 32.299) Dynamic Address Flag AVP code
   */
  int DYNAMIC_ADDRESS_FLAG = 2051;

  /**
   * Ro/Rf (3GPP TS 32.299) Early Media Description AVP code
   */
  int EARLY_MEDIA_DESCRIPTION = 1272;

  /**
   * Ro/Rf (3GPP TS 32.299) Envelope AVP code
   */
  int ENVELOPE = 1266;

  /**
   * Ro/Rf (3GPP TS 32.299) Envelope End Time AVP code
   */
  int ENVELOPE_END_TIME = 1267;

  /**
   * Ro/Rf (3GPP TS 32.299) Envelope Reporting AVP code
   */
  int ENVELOPE_REPORTING = 1268;

  /**
   * Ro/Rf (3GPP TS 32.299) Envelope Start Time AVP code
   */
  int ENVELOPE_START_TIME = 1269;

  /**
   * Ro/Rf (3GPP TS 32.299) Event AVP code
   */
  int EVENT = 825;

  /**
   * Ro/Rf (3GPP TS 32.299) Event Charging TimeStamp AVP code
   */
  int EVENT_CHARGING_TIMESTAMP = 1258;

  /**
   * Ro/Rf (3GPP TS 32.299) Event Type AVP code
   */
  int EVENT_TYPE = 823;

  /**
   * Ro/Rf (3GPP TS 32.299) Expires AVP code
   */
  int EXPIRES = 888;

  /**
   * Ro/Rf (3GPP TS 32.299) File Repair Supported AVP code
   */
  int FILE_REPAIR_SUPPORTED = 1224;

  /**
   * Ro/Rf (3GPP TS 32.299) Flows AVP code
   */
  int FLOWS = 510;

  /**
   * Ro/Rf (3GPP TS 32.299) GGSN Address AVP code
   */
  int GGSN_ADDRESS = 847;

  /**
   * Ro/Rf (3GPP TS 32.299) Guaranteed Bitrate UL AVP code
   */
  int GUARANTEED_BITRATE_UL = 1026;

  /**
   * Ro/Rf (3GPP TS 32.299) Guaranteed Bitrate UL AVP code
   */
  int GUARANTEED_BITRATE_DL = 1025;

  /**
   * Ro/Rf (3GPP TS 32.299) IM Information AVP code
   */
  int IM_INFORMATION = 2110;

  /**
   * Ro/Rf (3GPP TS 32.299) IMS Charging Identifier AVP code
   */
  int IMS_CHARGING_IDENTIFIER = 841;

  /**
   * Ro/Rf (3GPP TS 32.299) IMS Communication Service Identifier AVP code
   */
  int IMS_COMMUNICATION_SERVICE_IDENTIFIER = 1281;

  /**
   * Ro/Rf (3GPP TS 32.299) IMS Information AVP code
   */
  int IMS_INFORMATION = 876;

  /**
   * Ro/Rf (3GPP TS 32.299) Incoming Trunk Group Id AVP code
   */
  int INCOMING_TRUNK_GROUP_ID = 852;

  /**
   * Ro/Rf (3GPP TS 32.299) Incremental Cost AVP code
   */
  int INCREMENTAL_COST = 2062;

  /**
   * Ro/Rf (3GPP TS 32.299) Interface Id AVP code
   */
  int INTERFACE_ID = 2003;

  /**
   * Ro/Rf (3GPP TS 32.299) Interface Port AVP code
   */
  int INTERFACE_PORT = 2004;

  /**
   * Ro/Rf (3GPP TS 32.299) Interface Text AVP code
   */
  int INTERFACE_TEXT = 2005;

  /**
   * Ro/Rf (3GPP TS 32.299) Interface Type AVP code
   */
  int INTERFACE_TYPE = 2006;

  /**
   * Ro/Rf (3GPP TS 32.299) Inter Operator Identifier AVP code
   */
  int INTER_OPERATOR_IDENTIFIER = 838;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Client Dialed By MS AVP code
   */
  int LCS_CLIENT_DIALED_BY_MS = 1233;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Client External ID AVP code
   */
  int LCS_CLIENT_EXTERNAL_ID = 1234;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Client Id AVP code
   */
  int LCS_CLIENT_ID = 1232;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Client Type AVP code
   */
  int LCS_CLIENT_TYPE = 1241;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Data Coding Scheme AVP code
   */
  int LCS_DATA_CODING_SCHEME = 1236;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Format Indicator AVP code
   */
  int LCS_FORMAT_INDICATOR = 1237;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Information AVP code
   */
  int LCS_INFORMATION = 878;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Name String AVP code
   */
  int LCS_NAME_STRING = 1238;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Requestor Id AVP code
   */
  int LCS_REQUESTOR_ID = 1239;

  /**
   * Ro/Rf (3GPP TS 32.299) LCS Requestor Id String AVP code
   */
  int LCS_REQUESTOR_ID_STRING = 1240;

  /**
   * Ro/Rf (3GPP TS 32.299) Local Sequence Number AVP code
   */
  int LOCAL_SEQUENCE_NUMBER = 2063;

  /**
   * Ro/Rf (3GPP TS 32.299) Location Estimate AVP code
   */
  int LOCATION_ESTIMATE = 1242;

  /**
   * Ro/Rf (3GPP TS 32.299) Location Estimate Type AVP code
   */
  int LOCATION_ESTIMATE_TYPE = 1243;

  /**
   * Ro/Rf (3GPP TS 32.299) Location Type AVP code
   */
  int LOCATION_TYPE = 1244;

  /**
   * Ro/Rf (3GPP TS 32.299) Max Requested Bandwidth DL AVP code
   */
  int MAX_REQUESTED_BANDWIDTH_DL = 515;

  /**
   * Ro/Rf (3GPP TS 32.299) Max Requested Bandwidth UL AVP code
   */
  int MAX_REQUESTED_BANDWIDTH_UL = 516;

  /**
   * Ro/Rf (3GPP TS 32.299) MBMS 2G 3G Indicator AVP code
   */
  int MBMS_2G_3G_INDICATOR = 907;

  /**
   * Ro/Rf (3GPP TS 32.299) MBMS Information AVP code
   */
  int MBMS_INFORMATION = 880;

  /**
   * Ro/Rf (3GPP TS 32.299) MBMS Service Area AVP code
   */
  int MBMS_SERVICE_AREA = 903;

  /**
   * Ro/Rf (3GPP TS 32.299) MBMS Service Type AVP code
   */
  int MBMS_SERVICE_TYPE = 906;

  /**
   * Ro/Rf (3GPP TS 32.299) MBMS Session Identity AVP code
   */
  int MBMS_SESSION_IDENTITY = 908;

  /**
   * Ro/Rf (3GPP TS 32.299) MBMS User Service Type AVP code
   */
  int MBMS_USER_SERVICE_TYPE = 1225;

  /**
   * Ro/Rf (3GPP TS 32.299) Media Initiator Flag AVP code
   */
  int MEDIA_INITIATOR_FLAG = 882;

  /**
   * Ro/Rf (3GPP TS 32.299) Media Initiator Party AVP code
   */
  int MEDIA_INITIATOR_PARTY = 1288;

  /**
   * Ro/Rf (3GPP TS 32.299) Message Body AVP code
   */
  int MESSAGE_BODY = 889;

  /**
   * Ro/Rf (3GPP TS 32.299) Message Class AVP code
   */
  int MESSAGE_CLASS = 1213;

  /**
   * Ro/Rf (3GPP TS 32.299) Message ID AVP code
   */
  int MESSAGE_ID = 1210;

  /**
   * Ro/Rf (3GPP TS 32.299) Message Size AVP code
   */
  int MESSAGE_SIZE = 1212;

  /**
   * Ro/Rf (3GPP TS 32.299) Message Type AVP code
   */
  int MESSAGE_TYPE = 1211;

  /**
   * Ro/Rf (3GPP TS 32.299) MMBox Storage Requested AVP code
   */
  int MMBOX_STORAGE_REQUESTED = 1248;

  /**
   * Ro/Rf (3GPP TS 32.299) MM Content Type AVP code
   */
  int MM_CONTENT_TYPE = 1203;

  /**
   * Ro/Rf (3GPP TS 32.299) MMS Information AVP code
   */
  int MMS_INFORMATION = 877;

  /**
   * Ro/Rf (3GPP TS 32.299) MMTel Information AVP code
   */
  int MMTEL_INFORMATION = 2030;

  /**
   * Ro/Rf (3GPP TS 32.299) MSISDN AVP code
   */
  int MSISDN = 701;

  /**
   * Ro/Rf (3GPP TS 32.299) Next Tariff AVP code
   */
  int NEXT_TARIFF = 2057;

  /**
   * Ro/Rf (3GPP TS 32.299) Node Functionality AVP code
   */
  int NODE_FUNCTIONALITY = 862;

  /**
   * Ro/Rf (3GPP TS 32.299) Node Id AVP code
   */
  int NODE_ID = 2064;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Diversions AVP code
   */
  int NUMBER_OF_DIVERSIONS = 2034;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Messages Sent AVP code
   */
  int NUMBER_OF_MESSAGES_SENT = 2019;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Messages Successfully Exploded AVP code
   */
  int NUMBER_OF_MESSAGES_SUCCESSFULLY_EXPLODED = 2111;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Messages Successfully Sent AVP code
   */
  int NUMBER_OF_MESSAGES_SUCCESSFULLY_SENT = 2112;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Participants AVP code
   */
  int NUMBER_OF_PARTICIPANTS = 885;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Received Talk Bursts AVP code
   */
  int NUMBER_OF_RECEIVED_TALK_BURSTS = 1282;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Of Talk Bursts AVP code
   */
  int NUMBER_OF_TALK_BURSTS = 1283;

  /**
   * Ro/Rf (3GPP TS 32.299) Number Portability Routing Information AVP code
   */
  int NUMBER_PORTABILITY_ROUTING_INFORMATION = 2024;

  /**
   * Ro/Rf (3GPP TS 32.299) Offline Charging AVP code
   */
  int OFFLINE_CHARGING = 1278;

  /**
   * Ro/Rf (3GPP TS 32.299) Online Charging Flag AVP code
   */
  int ONLINE_CHARGING_FLAG = 2303;

  /**
   * Ro/Rf (3GPP TS 32.299) Originating IOI AVP code
   */
  int ORIGINATING_IOI = 839;

  /**
   * Ro/Rf (3GPP TS 32.299) Originator SCCP Address AVP code
   */
  int ORIGINATOR_SCCP_ADDRESS = 2008;

  /**
   * Ro/Rf (3GPP TS 32.299) Originator AVP code
   */
  int ORIGINATOR = 864;

  /**
   * Ro/Rf (3GPP TS 32.299) Originator Address AVP code
   */
  int ORIGINATOR_ADDRESS = 886;

  /**
   * Ro/Rf (3GPP TS 32.299) Originator Received Address AVP code
   */
  int ORIGINATOR_RECEIVED_ADDRESS = 2027;

  /**
   * Ro/Rf (3GPP TS 32.299) Originator Interface AVP code
   */
  int ORIGINATOR_INTERFACE = 2009;

  /**
   * Ro/Rf (3GPP TS 32.299) Outgoing Trunk Group Id AVP code
   */
  int OUTGOING_TRUNK_GROUP_ID = 853;

  /**
   * Ro/Rf (3GPP TS 32.299) Participant Access Priority AVP code
   */
  int PARTICIPANT_ACCESS_PRIORITY = 1259;

  /**
   * Ro/Rf (3GPP TS 32.299) Participant Action Type AVP code
   */
  int PARTICIPANT_ACTION_TYPE = 2049;

  /**
   * Ro/Rf (3GPP TS 32.299) Participant Group AVP code
   */
  int PARTICIPANT_GROUP = 1260;

  /**
   * Ro/Rf (3GPP TS 32.299) Participants Involved AVP code
   */
  int PARTICIPANTS_INVOLVED = 887;

  /**
   * Ro/Rf (3GPP TS 32.299) PDG Address AVP code
   */
  int PDG_ADDRESS = 895;

  /**
   * Ro/Rf (3GPP TS 32.299) PDG Charging Id AVP code
   */
  int PDG_CHARGING_ID = 896;

  /**
   * Ro/Rf (3GPP TS 32.299) PDN Connection ID AVP code
   */
  int PDN_CONNECTION_ID = 2050;

  /**
   * Ro/Rf (3GPP TS 32.299) PDP Address AVP code
   */
  int PDP_ADDRESS = 1227;

  /**
   * Ro/Rf (3GPP TS 32.299) PDP Context Type AVP code
   */
  int PDP_CONTEXT_TYPE = 1247;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Change Condition AVP code
   */
  int POC_CHANGE_CONDITION = 1261;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Change Time AVP code
   */
  int POC_CHANGE_TIME = 1262;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Controlling Address AVP code
   */
  int POC_CONTROLLING_ADDRESS = 858;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Event Type AVP code
   */
  int POC_EVENT_TYPE = 2025;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Group Name AVP code
   */
  int POC_GROUP_NAME = 859;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Information AVP code
   */
  int POC_INFORMATION = 879;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Server Role AVP code
   */
  int POC_SERVER_ROLE = 883;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Session Id AVP code
   */
  int POC_SESSION_ID = 1229;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Session Initiation type AVP code
   */
  int POC_SESSION_INITIATION_TYPE = 1277;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC Session Type AVP code
   */
  int POC_SESSION_TYPE = 884;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC User Role AVP code
   */
  int POC_USER_ROLE = 1252;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC User Role IDs AVP code
   */
  int POC_USER_ROLE_IDS = 1253;

  /**
   * Ro/Rf (3GPP TS 32.299) PoC User Role info Units AVP code
   */
  int POC_USER_ROLE_INFO_UNITS = 1254;

  /**
   * Ro/Rf (3GPP TS 32.299) Positioning Data AVP code
   */
  int POSITIONING_DATA = 1245;

  /**
   * Ro/Rf (3GPP TS 32.299) Priority AVP code
   */
  int PRIORITY = 1209;

  /**
   * Ro/Rf (3GPP TS 32.299) Priority Level AVP code
   */
  int PRIORITY_LEVEL = 1046;

  /**
   * Ro/Rf (3GPP TS 32.299) PS Append Free Format Data AVP code
   */
  int PS_APPEND_FREE_FORMAT_DATA = 867;

  /**
   * Ro/Rf (3GPP TS 32.299) PS Free Format Data AVP code
   */
  int PS_FREE_FORMAT_DATA = 866;

  /**
   * Ro/Rf (3GPP TS 32.299) PS Furnish Charging Information AVP code
   */
  int PS_FURNISH_CHARGING_INFORMATION = 865;

  /**
   * Ro/Rf (3GPP TS 32.299) PS Information AVP code
   */
  int PS_INFORMATION = 874;

  /**
   * Ro/Rf (3GPP TS 32.299) QoS Information AVP code
   */
  int QOS_INFORMATION = 1016;

  /**
   * Ro/Rf (3GPP TS 32.299) QoS Class Identifier AVP code
   */
  int QOS_CLASS_IDENTIFIER = 1028;

  /**
   * Ro/Rf (3GPP TS 32.299) Quota Consumption Time AVP code
   */
  int QUOTA_CONSUMPTION_TIME = 881;

  /**
   * Ro/Rf (3GPP TS 32.299) Quota Holding Time AVP code
   */
  int QUOTA_HOLDING_TIME = 871;

  /**
   * Ro/Rf (3GPP TS 32.299) RAI AVP code
   */
  int RAI = 909;

  /**
   * Ro/Rf (3GPP TS 32.299) Rate Element AVP code
   */
  int RATE_ELEMENT = 2058;

  /**
   * Ro/Rf (3GPP TS 32.299) Read Reply Report Requested AVP code
   */
  int READ_REPLY_REPORT_REQUESTED = 1222;

  /**
   * Ro/Rf (3GPP TS 32.299) Received Talk Burst Time AVP code
   */
  int RECEIVED_TALK_BURST_TIME = 1284;

  /**
   * Ro/Rf (3GPP TS 32.299) Received Talk Burst Volume AVP code
   */
  int RECEIVED_TALK_BURST_VOLUME = 1285;

  /**
   * Ro/Rf (3GPP TS 32.299) Recipient Address AVP code
   */
  int RECIPIENT_ADDRESS = 1201;

  /**
   * Ro/Rf (3GPP TS 32.299) Recipient Info AVP code
   */
  int RECIPIENT_INFO = 2026;

  /**
   * Ro/Rf (3GPP TS 32.299) Recipient Received Address AVP code
   */
  int RECIPIENT_RECEIVED_ADDRESS = 2028;

  /**
   * Ro/Rf (3GPP TS 32.299) Recipient SCCP Address AVP code
   */
  int RECIPIENT_SCCP_ADDRESS = 2010;

  /**
   * Ro/Rf (3GPP TS 32.299) Refund Information AVP code
   */
  int REFUND_INFORMATION = 2022;

  /**
   * Ro/Rf (3GPP TS 32.299) Remaining Balance AVP code
   */
  int REMAINING_BALANCE = 2021;

  /**
   * Ro/Rf (3GPP TS 32.299) Reply Applic ID AVP code
   */
  int REPLY_APPLIC_ID = 1223;

  /**
   * Ro/Rf (3GPP TS 32.299) Reply Path Requested AVP code
   */
  int REPLY_PATH_REQUESTED = 2011;

  /**
   * Ro/Rf (3GPP TS 32.299) Reporting Reason AVP code
   */
  int REPORTING_REASON = 872;

  /**
   * Ro/Rf (3GPP TS 32.299) Requested Party Address AVP code
   */
  int REQUESTED_PARTY_ADDRESS = 1251;

  /**
   * Ro/Rf (3GPP TS 32.299) Required MBMS Bearer Capabilities AVP code
   */
  int REQUIRED_MBMS_BEARER_CAPABILITIES = 901;

  /**
   * Ro/Rf (3GPP TS 32.299) Role of Node AVP code
   */
  int ROLE_OF_NODE = 829;

  /**
   * Ro/Rf (3GPP TS 32.299) Scale Factor AVP code
   */
  int SCALE_FACTOR = 2059;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Answer Timestamp AVP code
   */
  int SDP_ANSWER_TIMESTAMP = 1275;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Media Component AVP code
   */
  int SDP_MEDIA_COMPONENT = 843;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Media Description AVP code
   */
  int SDP_MEDIA_DESCRIPTION = 845;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Media Name AVP code
   */
  int SDP_MEDIA_NAME = 844;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Offer Timestamp AVP code
   */
  int SDP_OFFER_TIMESTAMP = 1274;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Session Description AVP code
   */
  int SDP_SESSION_DESCRIPTION = 842;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP TimeStamps AVP code
   */
  int SDP_TIMESTAMPS = 1273;

  /**
   * Ro/Rf (3GPP TS 32.299) SDP Type AVP code
   */
  int SDP_TYPE = 2036;

  /**
   * Ro/Rf (3GPP TS 32.299) Served Party IP Address AVP code
   */
  int SERVED_PARTY_IP_ADDRESS = 848;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Data Container AVP code
   */
  int SERVICE_DATA_CONTAINER = 2040;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Generic Information AVP code
   */
  int SERVICE_GENERIC_INFORMATION = 1256;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Id AVP code
   */
  int SERVICE_IDENTIFIER = 855;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Information AVP code
   */
  int SERVICE_INFORMATION = 873;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Mode AVP code
   */
  int SERVICE_MODE = 2032;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Specific Data AVP code
   */
  int SERVICE_SPECIFIC_DATA = 863;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Specific Info AVP code
   */
  int SERVICE_SPECIFIC_INFO = 1249;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Specific Type AVP code
   */
  int SERVICE_SPECIFIC_TYPE = 1257;

  /**
   * Ro/Rf (3GPP TS 32.299) Serving Node Type AVP code
   */
  int SERVING_NODE_TYPE = 2047;

  /**
   * Ro/Rf (3GPP TS 32.299) Service Type AVP code
   */
  int SERVICE_TYPE = 2031;

  /**
   * Ro/Rf (3GPP TS 32.299) SGSN Address AVP code
   */
  int SGSN_ADDRESS = 1228;

  /**
   * Ro/Rf (3GPP TS 32.299) SGW Change AVP code
   */
  int SGW_CHANGE = 2064;

  /**
   * Ro/Rf (3GPP TS 32.299) SIP Method AVP code
   */
  int SIP_METHOD = 824;

  /**
   * Ro/Rf (3GPP TS 32.299) SIP Request Timestamp Fraction AVP code
   */
  int SIP_REQUEST_TIMESTAMP_FRACTION = 2301;

  /**
   * Ro/Rf (3GPP TS 32.299) SIP Request Timestamp AVP code
   */
  int SIP_REQUEST_TIMESTAMP = 834;

  /**
   * Ro/Rf (3GPP TS 32.299) SIP Response Timestamp Fraction AVP code
   */
  int SIP_RESPONSE_TIMESTAMP_FRACTION = 2302;

  /**
   * Ro/Rf (3GPP TS 32.299) SIP Response Timestamp AVP code
   */
  int SIP_RESPONSE_TIMESTAMP = 835;

  /**
   * Ro/Rf (3GPP TS 32.299) SM Discharge Time AVP code
   */
  int SM_DISCHARGE_TIME = 2012;

  /**
   * Ro/Rf (3GPP TS 32.299) SM Message Type AVP code
   */
  int SM_MESSAGE_TYPE = 2007;

  /**
   * Ro/Rf (3GPP TS 32.299) SM Protocol ID AVP code
   */
  int SM_PROTOCOL_ID = 2013;

  /**
   * Ro/Rf (3GPP TS 32.299) SMSC Address AVP code
   */
  int SMSC_ADDRESS = 2017;

  /**
   * Ro/Rf (3GPP TS 32.299) SMS Information AVP code
   */
  int SMS_INFORMATION = 2000;

  /**
   * Ro/Rf (3GPP TS 32.299) SMS Node AVP code
   */
  int SMS_NODE = 2016;

  /**
   * Ro/Rf (3GPP TS 32.299) SM Service Type AVP code
   */
  int SM_SERVICE_TYPE = 2029;

  /**
   * Ro/Rf (3GPP TS 32.299) SM Status AVP code
   */
  int SM_STATUS = 2014;

  /**
   * Ro/Rf (3GPP TS 32.299) SM User Data Header AVP code
   */
  int SM_USER_DATA_HEADER = 2015;

  /**
   * Ro/Rf (3GPP TS 32.299) Start Time AVP code
   */
  int START_TIME = 2041;

  /**
   * Ro/Rf (3GPP TS 32.299) Stop Time AVP code
   */
  int STOP_TIME = 2042;

  /**
   * Ro/Rf (3GPP TS 32.299) Submission Time AVP code
   */
  int SUBMISSION_TIME = 1202;

  /**
   * Ro/Rf (3GPP TS 32.299) Subscriber Role AVP code
   */
  int SUBSCRIBER_ROLE = 2033;

  /**
   * Ro/Rf (3GPP TS 32.299) Supplementary Service AVP code
   */
  int SUPPLEMENTARY_SERVICE = 2048;

  /**
   * Ro/Rf (3GPP TS 32.299) Talk Burst Exchange AVP code
   */
  int TALK_BURST_EXCHANGE = 1255;

  /**
   * Ro/Rf (3GPP TS 32.299) Talk Burst Time AVP code
   */
  int TALK_BURST_TIME = 1286;

  /**
   * Ro/Rf (3GPP TS 32.299) Talk Burst Volume AVP code
   */
  int TALK_BURST_VOLUME = 1287;

  /**
   * Ro/Rf (3GPP TS 32.299) Tariff Information AVP code
   */
  int TARIFF_INFORMATION = 2060;

  /**
   * Ro/Rf/S13 (3GPP TS 32.299) Terminal Information AVP code
   */
  int TERMINAL_INFORMATION = 1401;

  /**
   * Ro/Rf (3GPP TS 32.299) Terminating IOI AVP code
   */
  int TERMINATING_IOI = 840;

  /**
   * Ro/Rf (3GPP TS 32.299) Time First Usage AVP code
   */
  int TIME_FIRST_USAGE = 2043;

  /**
   * Ro/Rf (3GPP TS 32.299) Time Last Usage AVP code
   */
  int TIME_LAST_USAGE = 2044;

  /**
   * Ro/Rf (3GPP TS 32.299) Time Quota Mechanism AVP code
   */
  int TIME_QUOTA_MECHANISM = 1270;

  /**
   * Ro/Rf (3GPP TS 32.299) Time Quota Threshold AVP code
   */
  int TIME_QUOTA_THRESHOLD = 868;

  /**
   * Ro/Rf (3GPP TS 32.299) Time Quota Type AVP code
   */
  int TIME_QUOTA_TYPE = 1271;

  /**
   * Ro/Rf (3GPP TS 32.299) Time Stamps AVP code
   */
  int TIME_STAMPS = 833;

  /**
   * Ro/Rf (3GPP TS 32.299) Time Usage AVP code
   */
  int TIME_USAGE = 2045;

  /**
   * Ro/Rf (3GPP TS 32.299) TMGI AVP code
   */
  int TMGI = 900;

  /**
   * Ro/Rf (3GPP TS 32.299) Token Text AVP code
   */
  int TOKEN_TEXT = 1215;

  /**
   * Ro/Rf (3GPP TS 32.299) Total Number Of Messages Exploded AVP code
   */
  int TOTAL_NUMBER_OF_MESSAGES_EXPLODED = 2113;

  /**
   * Ro/Rf (3GPP TS 32.299) Total Number Of Messages Sent AVP code
   */
  int TOTAL_NUMBER_OF_MESSAGES_SENT = 2114;

  /**
   * Ro/Rf (3GPP TS 32.299) Traffic Data Volumes AVP code
   */
  int TRAFFIC_DATA_VOLUMES = 2046;

  /**
   * Ro/Rf (3GPP TS 32.299) Trigger AVP code
   */
  int TRIGGER = 1264;

  /**
   * Ro/Rf (3GPP TS 32.299) Trigger Type AVP code
   */
  int TRIGGER_TYPE = 870;

  /**
   * Ro/Rf (3GPP TS 32.299) Trunk Group Id AVP code
   */
  int TRUNK_GROUP_ID = 851;

  /**
   * Ro/Rf (3GPP TS 32.299) Type Number AVP code
   */
  int TYPE_NUMBER = 1204;

  /**
   * Ro/Rf (3GPP TS 32.299) Unit Cost AVP code
   */
  int UNIT_COST = 2061;

  /**
   * Ro/Rf (3GPP TS 32.299) Unit Quota Threshold AVP code
   */
  int UNIT_QUOTA_THRESHOLD = 1226;

  /**
   * Ro/Rf (3GPP TS 32.299) User Data AVP code
   */
  int USER_DATA_RORF = 606;

  /**
   * Ro/Rf (3GPP TS 32.299) User Participating Type AVP code
   */
  int USER_PARTICIPATING_TYPE = 1279;

  /**
   * Ro/Rf (3GPP TS 32.299) User Session Id AVP code
   */
  int USER_SESSION_ID = 830;

  /**
   * Ro/Rf (3GPP TS 32.299) VAS Id AVP code
   */
  int VAS_ID = 1102;

  /**
   * Ro/Rf (3GPP TS 32.299) VASP Id AVP code
   */
  int VASP_ID = 1101;

  /**
   * Ro/Rf (3GPP TS 32.299) Volume Quota Threshold AVP code
   */
  int VOLUME_QUOTA_THRESHOLD = 869;

  /**
   * Ro/Rf (3GPP TS 32.299) WAG Address AVP code
   */
  int WAG_ADDRESS = 890;

  /**
   * Ro/Rf (3GPP TS 32.299) WAG PLMN Id AVP code
   */
  int WAG_PLMN_ID = 891;

  /**
   * Ro/Rf (3GPP TS 32.299) WLAN Information AVP code
   */
  int WLAN_INFORMATION = 875;

  /**
   * Ro/Rf (3GPP TS 32.299) WLAN Radio Container AVP code
   */
  int WLAN_RADIO_CONTAINER = 892;

  /**
   * Ro/Rf (3GPP TS 32.299) WLAN Session Id AVP code
   */
  int WLAN_SESSION_ID = 1246;

  /**
   * Ro/Rf (3GPP TS 32.299) WLAN Technology AVP code
   */
  int WLAN_TECHNOLOGY = 893;

  /**
   * Ro/Rf (3GPP TS 32.299) WLAN UE Local IPAddress AVP code
   */
  int WLAN_UE_LOCAL_IPADDRESS = 894;

  // S13/S13' Interfaces AVPs

  /**
   * S13/S13' (3GPP TS 29.272) 3GPP IMEI AVP code
   */
  int TGPP_IMEI = 1402;

  /**
   * S13/S13' (3GPP TS 29.272) Software Version AVP code
   */
  int SOFTWARE_VERSION = 1403;

  /**
   * S13/S13' (3GPP TS 29.272) Equipment-Status AVP code
   */
  int EQUIPMENT_STATUS = 1445;

  /**
   * S13/S13' (3GPP TS 29.272) 3GPP2 MEID AVP code
   */
  int TGPP2_MEID = 1471;


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
   * @throws AvpDataException
   *           if data has incorrect format
   */
  byte[] getRaw() throws AvpDataException;

  /**
   * @return data as an String (Use AS-ASCI code page)
   * @throws AvpDataException
   *           if data has incorrect format
   */
  byte[] getOctetString() throws AvpDataException;

  /**
   * @return data as an integer
   * @throws AvpDataException
   *           if data has incorrect format
   */
  int getInteger32() throws AvpDataException;

  /**
   * @return data as an unsigned long
   * @throws AvpDataException
   *           if data has incorrect format
   */
  long getInteger64() throws AvpDataException;

  /**
   * @return data as an unsigned integer
   * @throws AvpDataException
   *           if data has incorrect format
   */
  long getUnsigned32() throws AvpDataException;

  /**
   * @return data as an long
   * @throws AvpDataException
   *           if data has incorrect format
   */
  long getUnsigned64() throws AvpDataException;

  /**
   * @return data as an float
   * @throws AvpDataException
   *           if data has incorrect format
   */
  float getFloat32() throws AvpDataException;

  /**
   *
   * @return data as an double
   * @throws AvpDataException
   *           if data has incorrect format
   */
  double getFloat64() throws AvpDataException;

  /**
   * @return data as an Diameter Address (Inet4Address or Inet6Address)
   * @throws AvpDataException
   *           if data has incorrect format
   */
  InetAddress getAddress() throws AvpDataException;

  /**
   * @return data as an Diameter Time (millisecond is truncated)
   * @throws AvpDataException
   *           if data has incorrect format
   */
  Date getTime() throws AvpDataException;

  /**
   * @return data as an String (Use UTF-8 code page)
   * @throws AvpDataException
   *           if data has incorrect format
   */
  String getUTF8String() throws AvpDataException;

  /**
   * @return data as an String (Use AS-ASCI code page)
   * @throws AvpDataException
   *           if data has incorrect format
   */
  String getDiameterIdentity() throws AvpDataException;

  /**
   * @return data as an Diamter URI
   * @throws AvpDataException
   *           if data has incorrect format
   */
  URI getDiameterURI() throws AvpDataException;

  /**
   * @return data as an AVP group.
   * @throws AvpDataException
   *           if data has incorrect format
   */
  AvpSet getGrouped() throws AvpDataException;

  byte[] getRawData();
}
