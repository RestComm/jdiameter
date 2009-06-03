/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;

/**
 * 
 * DiameterRoAvpCodes.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DiameterRoAvpCodes
{

  private DiameterRoAvpCodes() {}

  public static final long TGPP_VENDOR_ID = 10415L;

  public static final int TGPP_CHARGING_ID = 2;
  public static final DiameterAvpType TGPP_CHARGING_ID_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_PDP_TYPE = 3;
  public static final DiameterAvpType TGPP_PDP_TYPE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_GPRS_NEGOTIATED_QOS_PROFILE = 5;
  public static final DiameterAvpType TGPP_GPRS_NEGOTIATED_QOS_PROFILE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_IMSI_MCC_MNC = 8;
  public static final DiameterAvpType TGPP_IMSI_MCC_MNC_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_GGSN_MCC_MNC = 9;
  public static final DiameterAvpType TGPP_GGSN_MCC_MNC_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_NSAPI = 10;
  public static final DiameterAvpType TGPP_NSAPI_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_SESSION_STOP_INDICATOR = 11;
  public static final DiameterAvpType TGPP_SESSION_STOP_INDICATOR_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_SELECTION_MODE = 12;
  public static final DiameterAvpType TGPP_SELECTION_MODE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_CHARGING_CHARACTERISTICS = 13;
  public static final DiameterAvpType TGPP_CHARGING_CHARACTERISTICS_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_SGSN_MCC_MNC = 18;
  public static final DiameterAvpType TGPP_SGSN_MCC_MNC_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_MS_TIMEZONE = 23;
  public static final DiameterAvpType TGPP_MS_TIMEZONE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_CAMEL_CHARGING_INFO = 24;
  public static final DiameterAvpType TGPP_CAMEL_CHARGING_INFO_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_USER_LOCATION_INFO = 22;
  public static final DiameterAvpType TGPP_USER_LOCATION_INFO_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TGPP_RAT_TYPE = 21;
  public static final DiameterAvpType TGPP_RAT_TYPE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int ADAPTATIONS = 1217;
  public static final DiameterAvpType ADAPTATIONS_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int ADDITIONAL_CONTENT_INFORMATION = 1207;
  public static final DiameterAvpType ADDITIONAL_CONTENT_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int ADDITIONAL_TYPE_INFORMATION = 1205;
  public static final DiameterAvpType ADDITIONAL_TYPE_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int ADDRESS_DATA = 897;
  public static final DiameterAvpType ADDRESS_DATA_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int ADDRESS_DOMAIN = 898;
  public static final DiameterAvpType ADDRESS_DOMAIN_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int ADDRESS_TYPE = 899;
  public static final DiameterAvpType ADDRESS_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int ADDRESSEE_TYPE = 1208;
  public static final DiameterAvpType ADDRESSEE_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int APPLIC_ID = 1218;
  public static final DiameterAvpType APPLIC_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int APPLICATION_PROVIDED_CALLED_PARTY_ADDRESS = 837;
  public static final DiameterAvpType APPLICATION_PROVIDED_CALLED_PARTY_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int APPLICATION_SERVER = 836;
  public static final DiameterAvpType APPLICATION_SERVER_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int APPLICATION_SERVER_INFORMATION = 850;
  public static final DiameterAvpType APPLICATION_SERVER_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int AUTHORIZED_QOS = 849;
  public static final DiameterAvpType AUTHORIZED_QOS_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int AUX_APPLIC_INFO = 1219;
  public static final DiameterAvpType AUX_APPLIC_INFO_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int BEARER_SERVICE = 854;
  public static final DiameterAvpType BEARER_SERVICE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int CALLED_PARTY_ADDRESS = 832;
  public static final DiameterAvpType CALLED_PARTY_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int CALLING_PARTY_ADDRESS = 831;
  public static final DiameterAvpType CALLING_PARTY_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int CAUSE_CODE = 861;
  public static final DiameterAvpType CAUSE_CODE_AVP_TYPE = DiameterAvpType.fromString("Integer32");
  public static final int CG_ADDRESS = 846;
  public static final DiameterAvpType CG_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int CHARGING_RULE_BASE_NAME = 1004;
  public static final DiameterAvpType CHARGING_RULE_BASE_NAME_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int CLASS_IDENTIFIER = 1214;
  public static final DiameterAvpType CLASS_IDENTIFIER_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int CONTENT_CLASS = 1220;
  public static final DiameterAvpType CONTENT_CLASS_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int CONTENT_DISPOSITION = 828;
  public static final DiameterAvpType CONTENT_DISPOSITION_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int CONTENT_LENGTH = 827;
  public static final DiameterAvpType CONTENT_LENGTH_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int CONTENT_SIZE = 1206;
  public static final DiameterAvpType CONTENT_SIZE_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int CONTENT_TYPE = 826;
  public static final DiameterAvpType CONTENT_TYPE_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int DEFERRED_LOCATION_EVENT_TYPE = 1230;
  public static final DiameterAvpType DEFERRED_LOCATION_EVENT_TYPE_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int DELIVERY_REPORT_REQUESTED = 1216;
  public static final DiameterAvpType DELIVERY_REPORT_REQUESTED_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int DOMAIN_NAME = 1200;
  public static final DiameterAvpType DOMAIN_NAME_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int DRM_CONTENT = 1221;
  public static final DiameterAvpType DRM_CONTENT_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int EVENT = 825;
  public static final DiameterAvpType EVENT_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int EVENT_TYPE = 823;
  public static final DiameterAvpType EVENT_TYPE_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int EXPIRES = 888;
  public static final DiameterAvpType EXPIRES_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int FILE_REPAIR_SUPPORTED = 1224;
  public static final DiameterAvpType FILE_REPAIR_SUPPORTED_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int GGSN_ADDRESS = 847;
  public static final DiameterAvpType GGSN_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int IMS_CHARGING_IDENTIFIER = 841;
  public static final DiameterAvpType IMS_CHARGING_IDENTIFIER_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int IMS_INFORMATION = 876;
  public static final DiameterAvpType IMS_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int INCOMING_TRUNK_GROUP_ID = 852;
  public static final DiameterAvpType INCOMING_TRUNK_GROUP_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int INTER_OPERATOR_IDENTIFIER = 838;
  public static final DiameterAvpType INTER_OPERATOR_IDENTIFIER_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int LCS_APN = 1231;
  public static final DiameterAvpType LCS_APN_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LCS_CLIENT_DIALED_BY_MS = 1233;
  public static final DiameterAvpType LCS_CLIENT_DIALED_BY_MS_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LCS_CLIENT_EXTERNAL_ID = 1234;
  public static final DiameterAvpType LCS_CLIENT_EXTERNAL_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LCS_CLIENT_ID = 1232;
  public static final DiameterAvpType LCS_CLIENT_ID_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int LCS_CLIENT_NAME = 1235;
  public static final DiameterAvpType LCS_CLIENT_NAME_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int LCS_CLIENT_TYPE = 1241;
  public static final DiameterAvpType LCS_CLIENT_TYPE_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LCS_DATA_CODING_SCHEME = 1236;
  public static final DiameterAvpType LCS_DATA_CODING_SCHEME_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LCS_FORMAT_INDICATOR = 1237;
  public static final DiameterAvpType LCS_FORMAT_INDICATOR_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int LCS_INFORMATION = 878;
  public static final DiameterAvpType LCS_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int LCS_NAME_STRING = 1238;
  public static final DiameterAvpType LCS_NAME_STRING_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LCS_REQUESTOR_ID = 1239;
  public static final DiameterAvpType LCS_REQUESTOR_ID_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int LCS_REQUESTOR_ID_STRING = 1240;
  public static final DiameterAvpType LCS_REQUESTOR_ID_STRING_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LOCATION_ESTIMATE = 1242;
  public static final DiameterAvpType LOCATION_ESTIMATE_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int LOCATION_ESTIMATE_TYPE = 1243;
  public static final DiameterAvpType LOCATION_ESTIMATE_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int LOCATION_TYPE = 1244;
  public static final DiameterAvpType LOCATION_TYPE_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int MANDATORY_CAPABILITY = 604;
  public static final DiameterAvpType MANDATORY_CAPABILITY_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int MEDIA_INITIATOR_FLAG = 882;
  public static final DiameterAvpType MEDIA_INITIATOR_FLAG_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int MESSAGE_BODY = 889;
  public static final DiameterAvpType MESSAGE_BODY_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int MBMS_INFORMATION = 880;
  public static final DiameterAvpType MBMS_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int MBMS_SERVICE_AREA = 903;
  public static final DiameterAvpType MBMS_SERVICE_AREA_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int MBMS_SESSION_IDENTITY = 908;
  public static final DiameterAvpType MBMS_SESSION_IDENTITY_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int MBMS_SERVICE_TYPE = 906;
  public static final DiameterAvpType MBMS_SERVICE_TYPE_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int MBMS_USER_SERVICE_TYPE = 1225;
  public static final DiameterAvpType MBMS_USER_SERVICE_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int MBMS_2G_3G_INDICATOR = 907;
  public static final DiameterAvpType MBMS_2G_3G_INDICATOR_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int MESSAGE_CLASS = 1213;
  public static final DiameterAvpType MESSAGE_CLASS_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int MM_CONTENT_TYPE = 1203;
  public static final DiameterAvpType MM_CONTENT_TYPE_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int MESSAGE_ID = 1210;
  public static final DiameterAvpType MESSAGE_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int MESSAGE_TYPE = 1211;
  public static final DiameterAvpType MESSAGE_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int MESSAGE_SIZE = 1212;
  public static final DiameterAvpType MESSAGE_SIZE_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int MMS_INFORMATION = 877;
  public static final DiameterAvpType MMS_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int NODE_FUNCTIONALITY = 862;
  public static final DiameterAvpType NODE_FUNCTIONALITY_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int NUMBER_OF_PARTICIPANTS = 885;
  public static final DiameterAvpType NUMBER_OF_PARTICIPANTS_AVP_TYPE = DiameterAvpType.fromString("Integer32");
  public static final int OPTIONAL_CAPABILITY = 605;
  public static final DiameterAvpType OPTIONAL_CAPABILITY_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int ORIGINATING_IOI = 839;
  public static final DiameterAvpType ORIGINATING_IOI_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int ORIGINATOR = 864;
  public static final DiameterAvpType ORIGINATOR_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int ORIGINATOR_ADDRESS = 886;
  public static final DiameterAvpType ORIGINATOR_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int OUTGOING_TRUNK_GROUP_ID = 853;
  public static final DiameterAvpType OUTGOING_TRUNK_GROUP_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int PARTICIPANTS_INVOLVED = 887;
  public static final DiameterAvpType PARTICIPANTS_INVOLVED_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int PDG_ADDRESS = 895;
  public static final DiameterAvpType PDG_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int PDG_CHARGING_ID = 896;
  public static final DiameterAvpType PDG_CHARGING_ID_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int PDP_ADDRESS = 1227;
  public static final DiameterAvpType PDP_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int POC_CONTROLLING_ADDRESS = 858;
  public static final DiameterAvpType POC_CONTROLLING_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int POC_GROUP_NAME = 859;
  public static final DiameterAvpType POC_GROUP_NAME_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int POC_INFORMATION = 879;
  public static final DiameterAvpType POC_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int POC_SERVER_ROLE = 883;
  public static final DiameterAvpType POC_SERVER_ROLE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int POC_SESSION_ID = 1229;
  public static final DiameterAvpType POC_SESSION_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int POC_SESSION_TYPE = 884;
  public static final DiameterAvpType POC_SESSION_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int PRIORITY = 1209;
  public static final DiameterAvpType PRIORITY_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int PS_APPEND_FREE_FORMAT_DATA = 867;
  public static final DiameterAvpType PS_APPEND_FREE_FORMAT_DATA_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int PS_FREE_FORMAT_DATA = 866;
  public static final DiameterAvpType PS_FREE_FORMAT_DATA_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int PS_FURNISH_CHARGING_INFORMATION = 865;
  public static final DiameterAvpType PS_FURNISH_CHARGING_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int PS_INFORMATION = 874;
  public static final DiameterAvpType PS_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int QUOTA_CONSUMPTION_TIME = 881;
  public static final DiameterAvpType QUOTA_CONSUMPTION_TIME_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int QUOTA_HOLDING_TIME = 871;
  public static final DiameterAvpType QUOTA_HOLDING_TIME_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int RAI = 909;
  public static final DiameterAvpType RAI_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int READ_REPLY_REPORT_REQUESTED = 1222;
  public static final DiameterAvpType READ_REPLY_REPORT_REQUESTED_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int RECIPIENT_ADDRESS = 1201;
  public static final DiameterAvpType RECIPIENT_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int REPLY_APPLIC_ID = 1223;
  public static final DiameterAvpType REPLY_APPLIC_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int REPORTING_REASON = 872;
  public static final DiameterAvpType REPORTING_REASON_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int REQUIRED_MBMS_BEARER_CAPABILITIES = 901;
  public static final DiameterAvpType REQUIRED_MBMS_BEARER_CAPABILITIES_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int ROLE_OF_NODE = 829;
  public static final DiameterAvpType ROLE_OF_NODE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int SDP_MEDIA_COMPONENT = 843;
  public static final DiameterAvpType SDP_MEDIA_COMPONENT_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int SDP_MEDIA_DESCRIPTION = 845;
  public static final DiameterAvpType SDP_MEDIA_DESCRIPTION_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int SDP_MEDIA_NAME = 844;
  public static final DiameterAvpType SDP_MEDIA_NAME_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int SDP_SESSION_DESCRIPTION = 842;
  public static final DiameterAvpType SDP_SESSION_DESCRIPTION_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int SERVED_PARTY_IP_ADDRESS = 848;
  public static final DiameterAvpType SERVED_PARTY_IP_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int SERVER_CAPABILITIES = 603;
  public static final DiameterAvpType SERVER_CAPABILITIES_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int SERVER_NAME = 602;
  public static final DiameterAvpType SERVER_NAME_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int SERVICE_ID = 855;
  public static final DiameterAvpType SERVICE_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int SERVICE_INFORMATION = 873;
  public static final DiameterAvpType SERVICE_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int SERVICE_SPECIFIC_DATA = 863;
  public static final DiameterAvpType SERVICE_SPECIFIC_DATA_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int SGSN_ADDRESS = 1228;
  public static final DiameterAvpType SGSN_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int SIP_METHOD = 824;
  public static final DiameterAvpType SIP_METHOD_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int SIP_REQUEST_TIMESTAMP = 834;
  public static final DiameterAvpType SIP_REQUEST_TIMESTAMP_AVP_TYPE = DiameterAvpType.fromString("Time");
  public static final int SIP_RESPONSE_TIMESTAMP = 835;
  public static final DiameterAvpType SIP_RESPONSE_TIMESTAMP_AVP_TYPE = DiameterAvpType.fromString("Time");
  public static final int SUBMISSION_TIME = 1202;
  public static final DiameterAvpType SUBMISSION_TIME_AVP_TYPE = DiameterAvpType.fromString("Time");
  public static final int TALK_BURST_EXCHANGE = 860;
  public static final DiameterAvpType TALK_BURST_EXCHANGE_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int TERMINATING_IOI = 840;
  public static final DiameterAvpType TERMINATING_IOI_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int TIME_QUOTA_THRESHOLD = 868;
  public static final DiameterAvpType TIME_QUOTA_THRESHOLD_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int TIME_STAMPS = 833;
  public static final DiameterAvpType TIME_STAMPS_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int TMGI = 900;
  public static final DiameterAvpType TMGI_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int TOKEN_TEXT = 1215;
  public static final DiameterAvpType TOKEN_TEXT_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int TRIGGER_TYPE = 870;
  public static final DiameterAvpType TRIGGER_TYPE_AVP_TYPE = DiameterAvpType.fromString("Enumerated");
  public static final int TRUNK_GROUP_ID = 851;
  public static final DiameterAvpType TRUNK_GROUP_ID_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int TYPE_NUMBER = 1204;
  public static final DiameterAvpType TYPE_NUMBER_AVP_TYPE = DiameterAvpType.fromString("Integer32");
  public static final int UNIT_QUOTA_THRESHOLD = 1226;
  public static final DiameterAvpType UNIT_QUOTA_THRESHOLD_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int USER_DATA = 606;
  public static final DiameterAvpType USER_DATA_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int USER_SESSION_ID = 830;
  public static final DiameterAvpType USER_SESSION_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int VAS_ID = 1102;
  public static final DiameterAvpType VAS_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int VASP_ID = 1101;
  public static final DiameterAvpType VASP_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int VOLUME_QUOTA_THRESHOLD = 869;
  public static final DiameterAvpType VOLUME_QUOTA_THRESHOLD_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int WAG_ADDRESS = 890;
  public static final DiameterAvpType WAG_ADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
  public static final int WAG_PLMN_ID = 891;
  public static final DiameterAvpType WAG_PLMN_ID_AVP_TYPE = DiameterAvpType.fromString("OctetString");
  public static final int WLAN_INFORMATION = 875;
  public static final DiameterAvpType WLAN_INFORMATION_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int WLAN_RADIO_CONTAINER = 892;
  public static final DiameterAvpType WLAN_RADIO_CONTAINER_AVP_TYPE = DiameterAvpType.fromString("Grouped");
  public static final int WLAN_SESSION_ID = 1246;
  public static final DiameterAvpType WLAN_SESSION_ID_AVP_TYPE = DiameterAvpType.fromString("UTF8String");
  public static final int WLAN_TECHNOLOGY = 893;
  public static final DiameterAvpType WLAN_TECHNOLOGY_AVP_TYPE = DiameterAvpType.fromString("Unsigned32");
  public static final int WLAN_UE_LOCAL_IPADDRESS = 894;
  public static final DiameterAvpType WLAN_UE_LOCAL_IPADDRESS_AVP_TYPE = DiameterAvpType.fromString("Address");
}
