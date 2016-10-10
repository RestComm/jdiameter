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
 */

package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractClient;

public class Client extends AbstractClient {

  protected boolean receivedPLA;
  protected boolean sentPLR;
  protected boolean receivedLRA;
  protected boolean sentLRR;

  public Client() {
  }

  public void sendProvideLocationRequest() throws Exception {
    ProvideLocationRequest plr = super.createPLR(super.clientSLgSession);
    super.clientSLgSession.sendProvideLocationRequest(plr);
    Utils.printMessage(log, super.stack.getDictionary(), plr.getMessage(), true);
    this.sentPLR = true;
  }

  public void sendLocationReportRequest() throws Exception {
    LocationReportRequest lrr = super.createLRR(super.clientSLgSession);
    super.clientSLgSession.sendLocationReportRequest(lrr);
    Utils.printMessage(log, super.stack.getDictionary(), lrr.getMessage(), true);
    this.sentPLR = true;
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.slg.AbstractClient#doProvideLocationAnswerEvent(
   *    org.jdiameter.api.slg.ClientSLgSession, org.jdiameter.api.slg.events.ProvideLocationRequest, org.jdiameter.api.slg.events.ProvideLocationAnswer)
   */
  @Override
  public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedPLA) {
      fail("Received PLA more than once", null);
      return;
    }
    this.receivedPLA = true;
  }

  @Override
  public void doLocationReportAnswerEvent(ClientSLgSession session, LocationReportRequest request, LocationReportAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedLRA) {
      fail("Received PLA more than once", null);
      return;
    }
    this.receivedLRA = true;
  }

  // PLR/PLA methods

  // { SLg-Location-Type }
  protected int getSLgLocationType(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.2
    The SLg-Location-Type AVP is of type Enumerated. The following values are defined:
    CURRENT_LOCATION (0)
    CURRENT_OR_LAST_KNOWN_LOCATION (1)
    INITIAL_LOCATION (2)
    ACTIVATE_DEFERRED_LOCATION (3)
    CANCEL_DEFERRED_LOCATION (4)
    NOTIFICATION_VERIFICATION_ONLY (5)
  */
    int slgLocationType = 0;
    return slgLocationType;
  }

  @Override
  protected String getLCSNameString(){
    String lcsNameString = "Restcomm Geolocation API";
    return lcsNameString;
  }

  protected int getLCSFormatIndicator(){
  /*
    "0" = "LOGICAL_NAME"
    "1" = "EMAIL_ADDRESS"
    "2" = "MSISDN"
    "3" = "URL"
    "4" = "SIP_URL"
  */
    int lcsFormatIndicator = 2;
    return lcsFormatIndicator;
  }

  @Override
  protected String getUserName(){
    // Information Element IMSI Mapped to AVP User-Name
    String imsi = "748039876543210";
    return imsi;
  }

  @Override
  protected byte[] getMSISDN(){
    String msisdnString = "59899077937";
    byte[] msisdn = msisdnString.getBytes();
    return msisdn;
  }

  @Override
  protected String getIMEI(){
    String imei = "011714004661057";
    return imei;
  }

  @Override
  protected int getLCSClientType(){
  /*
    "0" = "EMERGENCY_SERVICES"
    "1" = "VALUE_ADDED_SERVICES"
    "2" = "PLMN_OPERATOR_SERVICES"
    "3" = "LAWFUL_INTERCEPT_SERVICES"
  */
    int lcsClientType = 1;
    return lcsClientType;
  }

  @Override
  protected String getLCSRequestorIdString(){
    String lcsRequestorIdString = "restcomm_geolocation_23";
    return lcsRequestorIdString;
  }

  @Override
  protected int getReqLCSFormatIndicator(){
  /*
    "0" = "LOGICAL_NAME"
    "1" = "EMAIL_ADDRESS"
    "2" = "MSISDN"
    "3" = "URL"
    "4" = "SIP_URL"
  */
    int requestorLCSFormatIndicator = 3;
    return requestorLCSFormatIndicator;
  }

  @Override
  protected long getLCSPriority(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.5
    The LCS-Priority AVP is of type Unsigned32. It indicates the priority of the location request.
    The value 0 shall indicate the highest priority, and the value 1 shall indicate normal priority.
    All other values shall be treated as 1 (normal priority). For details, refer to 3GPP TS 22.071.
  */
    int lcsPriority = 1;
    return lcsPriority;
  }

  @Override
  protected int getLCSQoSClass(){
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.27
      ASSURED (0)
      BEST EFFORT (1)
  */
    int lcsQoSClass = 1;
    return lcsQoSClass;
  }

  @Override
  protected long getHorizontalAccuracy() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.7
    The Horizontal-Accuracy AVP is of type Unsigned32. Bits 6-0 corresponds to Uncertainty Code defined in 3GPP TS 23.032 [3].
    The horizontal location error should be less than the error indicated by the uncertainty code with 67% confidence.
    Bits 7 to 31 shall be ignored
  */
    long horizontalAccuracy = 120;
    return horizontalAccuracy;
  }

  @Override
  protected long getVerticalAccuracy() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.8
    The Vertical-Accuracy AVP is of type Unsigned32. Bits 6-0 corresponds to Uncertainty Code defined in 3GPP TS 23.032 [3].
    The Vertical location error should be less than the error indicated by the uncertainty code with 67% confidence.
    Bits 7 to 31 shall be ignored
  */
    long verticalAccuracy = 3237;
    return verticalAccuracy;
  }

  @Override
  protected int getVerticalRequested() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.9
    VERTICAL_COORDINATE_IS_NOT REQUESTED (0)
	VERTICAL_COORDINATE_IS_REQUESTED (1)
  */
    int verticalRequested = 0;
    return verticalRequested;
  }

  @Override
  protected byte[] getVelocityRequested() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.10
    VELOCITY_IS_NOT_REQUESTED (0)
    VELOCITY_IS_REQUESTED (1)
   */
    String velocityRequested = "0";
    byte[] velReq = velocityRequested.getBytes();
    return velReq;
  }

  @Override
  protected int getResponseTime() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.11
    LOW_DELAY (0)
    DELAY_TOLERANT (1)
  */
    int responseTime = 1;
    return responseTime;
  }

  @Override
  protected long getSupportedGADShapes() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.12
    The Supported-GAD-Shapes AVP is of type Unsigned32 and it shall contain a bitmask.
    A node shall mark in the BIT STRING all Shapes defined in 3GPP TS 23.032 [3] it supports.
    Bits 6-0 in shall indicate the supported Shapes defined in 3GPP TS 23.032 [3]. Bits 7 to 31 shall be ignored.
    ellipsoidPoint (0)
    ellipsoidPointWithUncertaintyCircle (1)
    ellipsoidPointWithUncertaintyEllipse (2)
    polygon (3)
    ellipsoidPointWithAltitude (4)
    ellipsoidPointWithAltitudeAndUncertaintyElipsoid (5)
    ellipsoidArc (6)
  */
    long supportedGADShapes = 3;
    return supportedGADShapes;
  }

  @Override
  protected long getLSCServiceTypeId() {
    long lcsServiceTypeId = 234;
    return lcsServiceTypeId;
  }

  @Override
  protected String getLCSCodeword() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.1
    The LCS-Codeword AVP is of type UTF8String.
    It indicates the potential codeword string to send in a notification message to the UE
  */
    String lcsCodeword = "rcgl49f9f$#ERSD";
    return lcsCodeword;
  }

  @Override
  protected int getLCSPrivacyCheckSession() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.14
    ALLOWED_WITHOUT_NOTIFICATION (0)
    ALLOWED_WITH_NOTIFICATION (1)
    ALLOWED_IF_NO_RESPONSE (2)
    RESTRICTED_IF_NO_RESPONSE (3)
    NOT_ALLOWED (4)
  */
    int lcsPrivacyCheckSession = 2;
    return lcsPrivacyCheckSession;
  }

  @Override
  protected int getLCSPrivacyCheckNonSession() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.14
    ALLOWED_WITHOUT_NOTIFICATION (0)
    ALLOWED_WITH_NOTIFICATION (1)
    ALLOWED_IF_NO_RESPONSE (2)
    RESTRICTED_IF_NO_RESPONSE (3)
    NOT_ALLOWED (4)
  */
    int lcsPrivacyCheckNonSession = 0;
    return lcsPrivacyCheckNonSession;
  }

  @Override
  protected String getServiceSelection() {
    String apn = "restcomm.org";
    return apn;
  }

  @Override
  protected long getDeferredLocationType() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.36
    Bit	Event Type          Description
    0   UE-Available        Any event in which the SGSN has established a contact with the UE.
    1   Entering-Into-Area  An event where the UE enters a pre-defined geographical area.
    2   Leaving-From-Area   An event where the UE leaves a pre-defined geographical area.
    3   Being-Inside-Area   An event where the UE is currently within the pre-defined geographical area.For this event,
                            the value of Occurrence-Info AVP is always treated as set to “ONE_TIME_EVENT”.
    4   Periodic-LDR        An event where a defined periodic timer expires in the UE and activates a location report or a location request.
  */
    long deferredLocationType = 8;
    return deferredLocationType;
  }

  @Override
  protected byte[] getLCSReferenceNumber() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.37
      The LCS-Reference-Number AVP is of type OctetString of length 1. It shall contain the reference number identifying the deferred location request.
  */
    String lcsRefNumber = "579";
    byte[] lcsRefNum = lcsRefNumber.getBytes();
    return lcsRefNum;
  }

  @Override
  protected int getOccurrenceInfo(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.43
     The Occurrence-Info AVP is of type Enumerated. The following values are defined:
       ONE_TIME_EVENT (0)
       MULTIPLE_TIME_EVENT (1)
  */
    int occurrenceInfo = 1;
    return occurrenceInfo;
  }

  @Override
  protected long getIntervalTime(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.44
     The Interval-Time AVP is of type Unsigned32 and it contains minimum time interval between area reports, in seconds.
  */
    long intervalTime = 3600;
    return intervalTime;
  }

  @Override
  protected long getAreaType() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.41
     "Country Code"			0
     "PLMN ID"				1
     "Location Area ID"		2
     "Routing Area ID"		3
     "Cell Global ID"		4
     "UTRAN Cell ID"		5
  */
    long areaType = 3;
    return areaType;
  }

  @Override
  protected byte[] getAreaIdentification() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.42
     The Area-Identification AVP is of type OctetString and shall contain the identification of the area applicable
     for the change of area event based deferred location reporting. Octets are coded as described in 3GPP TS 29.002 [24].
  */
    String areaId = "area51";
    byte[] areaIdentification = areaId.getBytes();
    return areaIdentification;
  }

  @Override
  protected java.net.InetAddress getGMLCAddress(){

    try {
      java.net.InetAddress gmlcAddress = java.net.InetAddress.getLocalHost();
      return gmlcAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected long getPLRFLags() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.52
    Bit	Event Type                                    Description
    0   MO-LR-ShortCircuit-Indicator                  This bit, when set, indicates that the MO-LR short circuit feature is requested
                                                      for the periodic location.This bit is applicable only when the deferred MT-LR procedure is
                                                      initiated for a periodic location event and when the message is sent over Lgd interface.
    1   Optimized-LCS-Proc-Req                        This bit, when set, indicates that the GMLC is requesting the optimized LCS procedure
                                                      for the combined MME/SGSN. This bit is applicable only when the MT-LR procedure is
                                                      initiated by the GMLC. The GMLC shall set this bit only when the HSS indicates the combined MME/SGSN
                                                      node supporting the optimized LCS procedure.
    2   Delayed-Location-Reporting-Support-Indicator  This bit, when set, indicates that the GMLC supports delayed location reporting for UEs transiently
                                                      not reachable (e.g. UEs in extended idle mode DRX or Power Saving Mode) as specified in subclauses
                                                      9.1.6 and 9.1.15 of 3GPP TS 23.271 [2], i.e. that the GMLC supports receiving a
                                                      PROVIDE SUBSCRIBER LOCATION RESPONSE with the UE-Transiently-Not-Reachable-Indicator set in the
                                                      PLA-Flags IE; and receiving the location information in a subsequent SUBSCRIBER LOCATION REPORT
                                                      when the UE becomes reachable.
  */
    long plrFlags = 4;
    return plrFlags;
  }

  @Override
  protected long getReportingAmount() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.46
     The Reporting-Amount AVP is of type Unsigned32 and it contains reporting frequency. Its minimum value shall be 1 and maximum value shall be 8639999.
  */
    long reportingAmount = 8639910;
    return reportingAmount;
  }

  @Override
  protected long getReportingInterval() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.47
     The Interval-Time  AVP is of type Unsigned32 and it contains reporting frequency. Its minimum value shall be 1 and maximum value shall be 8639999.
  */
    long reportingInterval = 8639998;
    return reportingInterval;
  }

  @Override
  protected int getPrioritizedListIndicator(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.51
    The Prioritized-List-Indicator AVP is of type Enumerated and it indicates if the PLMN-ID-List is provided in prioritized order or not.
      NOT_PRIORITIZED  (0)
      PRIORITIZED (1)
  */
    int prioritizedListIndicator = 0;
    return prioritizedListIndicator;
  }

  @Override
  protected byte[] getVisitedPLMNId(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.49
    The PLMN-ID-List AVP is of type Grouped.
      AVP format:
      PLMN-ID-List ::= <AVP header: 2544 10415>
        { Visited-PLMN-Id }
        [ Periodic-Location-Support-Indicator ]
        *[ AVP ]
    If not included, the default value of Periodic-Location-Support-Indicator shall be considered as "NOT_SUPPORTED" (0).
  */
    String vPlmnIdList = "74803, 74801";
    byte[] visitedPlmnIdList = vPlmnIdList.getBytes();
    return visitedPlmnIdList;
  }

  @Override
  protected int getPeriodicLocationSupportIndicator(){
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.50
    The Periodic-Location-Support-Indicator AVP is of type Enumerated and it indicates if the given PLMN-ID (indicated by Visited-PLMN-Id)
    supports periodic location or not.
      NOT_SUPPORTED (0)
      SUPPORTED (1)
  */
    int periodicLocationSupportIndicator = 1;
    return periodicLocationSupportIndicator;
  }

  public boolean isReceivedPLA() {
    return receivedPLA;
  }

  public boolean isSentPLR() {
    return sentPLR;
  }

  @Override
  protected int getLocationEvent() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.20
      EMERGENCY_CALL_ORIGINATION (0)
      EMERGENCY_CALL_RELEASE (1)
      MO_LR (2)
      EMERGENCY_CALL_HANDOVER (3)
      DEFERRED_MT_LR_RESPONSE (4)
      DEFERRED_MO_LR_TTTP_INITIATION (5)
      DELAYED_LOCATION_REPORTING (6)
  */
    int locationEvent = 4;
    return locationEvent;
  }

  @Override
  protected byte[] getLocationEstimate(){
    String locEstimate = "N43°38'19.39\" W116°14'28.86\"";
    byte[] locationEstimate = locEstimate.getBytes();
    return locationEstimate;
  }

  @Override
  protected int getAccuracyFulfilmentIndicator(){
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.15
      REQUESTED_ACCURACY_FULFILLED (0)
      REQUESTED_ACCURACY_NOT_FULFILLED (1)
  */
    int accuracyFulfilmentIndicator = 0;
    return accuracyFulfilmentIndicator;
  }

  @Override
  protected long getAgeOfLocationEstimate() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.19
      The Age-Of-Location-Estimate AVP is of type Unsigned32.
      It indicates how long ago the location estimate was obtained in minutes, as indicated in 3GPP TS 29.002 [19].
  */
    long ageOfLocationEstimate = 37;
    return ageOfLocationEstimate;
  }

  @Override
  protected byte[] getVelocityEstimate() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.17
      The Velocity-Estimate AVP is of type OctetString.
      It is composed of 4 or more octets with an internal structure according to 3GPP TS 23.032 [3].
  */
    String vel = "200mph";
    byte[] velocityEstimate = vel.getBytes();
    return velocityEstimate;
  }

  @Override
  protected byte[] getEUTRANPositioningData() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.18
      The EUTRAN-Positioning-Data AVP is of type OctetString.
      It shall contain the encoded content of the "Positioning-Data" Information Element as defined in 3GPP TS 29.171 [7].
  */
    String eutran = "eNB453ltea23";
    byte[] eutranPositioningData = eutran.getBytes();
    return eutranPositioningData;
  }

  @Override
  protected byte[] getECGI() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.19
      The ECGI AVP is of type OctetString. It indicates the E-UTRAN Cell Global Identifier.
      It is coded according to clause 8.21.5, in 3GPP TS 29.274 [8].
  */
    String eCgi = "eNB9437";
    byte[] ecgi = eCgi.getBytes();
    return ecgi;
  }

  @Override
  protected byte[] getGERANPositioningData() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.30
      The GERAN-Positioning-Data AVP is of type OctetString.
      It shall contain the encoded content of the "Positioning Data" Information Element as defined in 3GPP TS 49.031 [20]
  */
    String geran = "BTS943BSC3";
    byte[] geranPositioningData = geran.getBytes();
    return geranPositioningData;
  }

  @Override
  protected byte[] getGERANGANSSPositioningData() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.30
      The GERAN-GANSS-Positioning-Data  AVP is of type OctetString.
      It shall contain the encoded content of the "GANSS Positioning Data" Information Element as defined in 3GPP TS 49.031 [20]
  */
    String geranGanss = "BTS73RNC1Ganss43";
    byte[] geranGanssPositioningData = geranGanss.getBytes();
    return geranGanssPositioningData;
  }

  @Override
  protected byte[] getCellGlobalIdentity() {
    String cgi = "9342784713907";
    byte[] CellGlobalIdentity = cgi.getBytes();
    return CellGlobalIdentity;
  }

  @Override
  protected byte[] getUTRANPositioningData() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.33
      The UTRAN-Positioning-Data AVP is of type OctetString.
      It shall contain the encoded content of the "positioningDataDiscriminator" and the "positioningDataSet" included in the
      "positionData" Information Element as defined in 3GPP TS 25.413 [21].
  */
    String utran = "NB943RNC1";
    byte[] utranPositioningData = utran.getBytes();
    return utranPositioningData;
  }

  @Override
  protected byte[] getUTRANGANSSPositioningData() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.33
      The UTRAN-Positioning-Data AVP is of type OctetString.
      It shall contain the encoded content of the "positioningDataDiscriminator" and the "positioningDataSet" included in the
      "positionData" Information Element as defined in 3GPP TS 25.413 [21].
  */
    String utranGanss = "NB031RNC5Ganss43";
    byte[] utranGanssPositioningData = utranGanss.getBytes();
    return utranGanssPositioningData;
  }

  @Override
  protected byte[] getServiceAreaIdentity() {
  /*
    3GPP TS 29.172 v13.0.0 -> 3GPP TS 29.272
    SAI shall contain the current service area of the target UE. The Service Area Identifier (SAI) is used to globally identify a service area.
    This Information Element is applicable only when the UE is attached to UTRAN access and when the message is sent by the SGSN or combined MME/SGSN
  */
    String sai = "service-area-umts-3";
    byte[] serviceAreaIdentity = sai.getBytes();
    return serviceAreaIdentity;
  }

  @Override
  protected int getPseudonymIndicator() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.21
      PSEUDONYM_NOT_REQUESTED (0)
      PSEUDONYM_REQUESTED (1)
  */
    int pseudonymIndicator = 0;
    return pseudonymIndicator;
  }

  @Override
  protected byte[] getSGSNNumber(){
    String sgsnNumString = "59899004501";
    byte[] sgsnNumber = sgsnNumString.getBytes();
    return sgsnNumber;
  }

  @Override
  protected String getSGSNName(){
    String sgsnName = "SGSN01";
    return sgsnName;
  }

  @Override
  protected String getSGSNRealm(){
    String sgsnRealm = "sgsn.restcomm.com";
    return sgsnRealm;
  }

  @Override
  protected String getMMEName(){
    String mmeName = "MME710";
    return mmeName;
  }

  @Override
  protected String getMMERealm(){
    String mmeRealm = "mme.restcomm.com";
    return mmeRealm;
  }

  @Override
  protected byte[] getMSCNumber(){
    String mscNumString = "59899001207";
    byte[] mscNumber = mscNumString.getBytes();
    return mscNumber;
  }

  @Override
  protected String get3GPPAAAServerName(){
    String tgppAAAServerName = "aaa.restcomm.com";
    return tgppAAAServerName;
  }

  @Override
  protected long getLCSCapabilitiesSets(){
    long lcsCapabilitiesSets = 99900123;
    return lcsCapabilitiesSets;
  }

  @Override
  protected long getLRRFLags() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.35
    Bit	Event Type                                    Description
    0   Lgd/SLg-Indicator                             This bit, when set, indicates that the Location Report Request message is sent on the Lgd interface,
                                                      i.e. the source node is an SGSN (or a combined MME/SGSN to which the UE is attached via UTRAN or GERAN).
                                                      This bit, when cleared, indicates that the Location Report Request message is sent on the SLg interface,
                                                      i.e. the source node is an MME (or a combined MME/SGSN to which the UE is attached via E-UTRAN).
    1   MO-LR-ShortCircuit-Indicator                  This bit, when set, indicates that the MO-LR short circuit feature is used by the UE for
                                                      location estimate. This bit is applicable only when for deferred MT-LR procedure and
                                                      when the message is sent over Lgd interface.
    2   MO-LR-ShortCircuit-Requested                  This bit, when set, indicates that the UE is requesting to use MO-LR short circuit feature
                                                      for location estimate.
                                                      This bit is applicable only when periodic MO-LR TTTP procedure is initiated by the UE and when the
                                                      message is sent over Lgd interface.
  */
    long lrrFlags = 1;
    return lrrFlags;
  }

  @Override
  protected long getTerminationCause() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.55
    "Normal"											0
    "Error Undefined"									1
    "Internal Timeout"									2
    "Congestion"										3
    "MT_LR_Restart"									    4
    "Privacy Violation"									5
    "Shape of Location Estimate Not Supported"		    6
    "Subscriber Termination"							7
    "UE Termination"									8
    "Network Termination"								9
  */
    long terminationCause = 7;
    return terminationCause;
  }

  @Override
  protected long getCellPortionId() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.58
    The Cell-Portion-ID AVP is of type Unsigned32. It indicates the current Cell Portion location of the target UE as provided by the E-SMLC.
    It shall contain the value of the "Cell Portion ID" Information Element as defined in 3GPP TS 29.171
  */
    long cellPortionId = 34923;
    return cellPortionId;
  }

  @Override
  protected byte[] get1xRTTRCID() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.59
    The 1xRTT-RCID AVP is of type OctetString.
    It indicates the 1xRTT Reference Cell Id that consists of a Cell Identification Discriminator and a Cell Identification and shall be formatted
    according to octets 3 through the end of the Cell Identifier element defined in subclause 4.2.17 in 3GPP2 A.S0014-D [22].
    The allowable cell discriminator values are "0000 0010", and "0000 0111".
  */
    String oxrttrcid = "00000010";
    byte[] onexrttrcid = oxrttrcid.getBytes();
    return onexrttrcid;
  }

  @Override
  protected String getCivicAddress() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.61
    The Civic-Address AVP is of type UTF8String.
    It contains the XML document carried in the "Civic Address" Information Element as defined in 3GPP TS 29.171.
  */
    String civicAddress = "<avpdefn name=\"Civic-Address\" >\n" + "\t\t<type street=\"5th Avenue\" />\n" + "\t</avpdefn>";
    return civicAddress;
  }

  @Override
  protected long getBarometricPressure() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.62
    The Barometric-Pressure AVP is of type Unsigned32.
    It contains the "Barometric Pressure" Information Element as defined in 3GPP TS 29.171.
  */
    long barometricPressure = 101327;
    return barometricPressure;
  }

  public boolean isReceivedLRA() {
	return receivedLRA;
  }

  public boolean isSentLRR() {
    return sentLRR;
  }
}
