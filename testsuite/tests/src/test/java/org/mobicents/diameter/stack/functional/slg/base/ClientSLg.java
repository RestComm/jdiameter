/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractSLgClient;

import static sun.jdbc.odbc.JdbcOdbcObject.hexStringToByteArray;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ClientSLg extends AbstractSLgClient {

  protected boolean receivedPLA;
  protected boolean receivedLRR;
  protected boolean sentPLR;
  protected boolean sentLRA;

  protected LocationReportRequest locationReportRequest;

  public ClientSLg() {
  }

  public void sendProvideLocationRequest() throws Exception {
    ProvideLocationRequest plr = super.createPLR(super.clientSLgSession);
    super.clientSLgSession.sendProvideLocationRequest(plr);
    Utils.printMessage(log, super.stack.getDictionary(), plr.getMessage(), true);
    this.sentPLR = true;
  }

  public void sendLocationReportAnswer() throws Exception {
    if (!receivedLRR || locationReportRequest == null) {
      fail("Did not receive LRR or answer already sent.", null);
      throw new Exception("Did not receive LRR or answer already sent. Request: " + this.locationReportRequest);
    }

    LocationReportAnswer lra = super.createLRA(locationReportRequest, 2001);

    this.clientSLgSession.sendLocationReportAnswer(lra);

    this.sentLRA = true;
    locationReportRequest = null;
    Utils.printMessage(log, super.stack.getDictionary(), lra.getMessage(), true);
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.slg.AbstractImmediateClient#doProvideLocationAnswerEvent(
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
  public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedLRR) {
      fail("Received LRR more than once", null);
      return;
    }
    this.receivedLRR = true;
    this.locationReportRequest = request;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != LocationReportRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientSLgSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    } else {
      try {

        super.clientSLgSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ClientSLgSession.class, (Object) null);
        ((NetworkReqListener) this.clientSLgSession).processRequest(request);

      } catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  //*********************************************************
  //***************** PLR methods ***************************
  //*********************************************************

  // { SLg-Location-Type }
  protected int getSLgLocationType() {
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
  protected String getLCSNameString() {
    String lcsNameString = "Restcomm Geolocation API";
    return lcsNameString;
  }

  protected int getLCSFormatIndicator() {
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
  protected String getUserName() {
    // Information Element IMSI Mapped to AVP User-Name
    String imsi = "748039876543210";
    return imsi;
  }

  @Override
  protected byte[] getMSISDN() {
    String msisdnString = "59899077937";
    byte[] msisdn = msisdnString.getBytes();
    return msisdn;
  }

  @Override
  protected String getIMEI() {
    String imei = "011714004661057";
    return imei;
  }

  @Override
  protected int getLCSClientType() {
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
  protected String getLCSRequestorIdString() {
    String lcsRequestorIdString = "restcomm_geolocation_23";
    return lcsRequestorIdString;
  }

  @Override
  protected int getReqLCSFormatIndicator() {
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
  protected long getLCSPriority() {
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
  protected int getLCSQoSClass() {
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
    long horizontalAccuracy = 120L;
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
    long verticalAccuracy = 3237L;
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
  protected int getVelocityRequested() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.10
    VELOCITY_IS_NOT_REQUESTED (0)
    VELOCITY_IS_REQUESTED (1)
   */
    int velocityRequested = 0;
    return velocityRequested;
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
    long supportedGADShapes = 3L;
    return supportedGADShapes;
  }

  @Override
  protected long getLSCServiceTypeId() {
    long lcsServiceTypeId = 234L;
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
  protected String getServiceSelection() {
    String apn = "restcomm.org";
    return apn;
  }

  @Override
  protected int getLCSPrivacyCheckSession() {
  /*
  3GPP TS 291.172 v13.0.0 section 7.4.22
  LCS-Privacy-Check-Session ::= <AVP header: 2522 10415>
    { LCS-Privacy-Check }
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
  3GPP TS 291.172 v13.0.0 section 7.4.22
  LCS-Privacy-Check-Non-Session ::= <AVP header: 2521 10415>
    { LCS-Privacy-Check }
  3GPP TS 29.172 v13.0.0 section 7.4.14
    ALLOWED_WITHOUT_NOTIFICATION (0)
    ALLOWED_WITH_NOTIFICATION (1)
    ALLOWED_IF_NO_RESPONSE (2)
    RESTRICTED_IF_NO_RESPONSE (3)
    NOT_ALLOWED (4)
  */
    int lcsPrivacyCheckNonSession = 4;
    return lcsPrivacyCheckNonSession;
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
    long deferredLocationType = 8L;
    return deferredLocationType;
  }

  @Override
  protected byte[] getLCSReferenceNumber() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.37
      The LCS-Reference-Number AVP is of type OctetString of length 1. It shall contain the reference number identifying the deferred location request.
  */
    String lcsRefNumber = "4C4353353739";
    byte[] lcsRefNum = lcsRefNumber.getBytes();
    return lcsRefNum;
  }

  @Override
  protected int getOccurrenceInfo() {
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
  protected long getIntervalTime() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.44
     The Interval-Time AVP is of type Unsigned32 and it contains minimum time interval between area reports, in seconds.
  */
    long intervalTime = 3600L;
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
    long areaType = 3L;
    return areaType;
  }

  @Override
  protected byte[] getAreaIdentification() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.42
     The Area-Identification AVP is of type OctetString and shall contain the identification of the area applicable
     for the change of area event based deferred location reporting. Octets are coded as described in 3GPP TS 29.002 [24].
  */
    String areaId = "617265613531";
    byte[] areaIdentification = areaId.getBytes();
    return areaIdentification;
  }

  @Override
  protected java.net.InetAddress getGMLCAddress() {

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
    long plrFlags = 4L;
    return plrFlags;
  }

  @Override
  protected long getReportingAmount() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.46
     The Reporting-Amount AVP is of type Unsigned32 and it contains reporting frequency. Its minimum value shall be 1 and maximum value shall be 8639999.
  */
    long reportingAmount = 8639910L;
    return reportingAmount;
  }

  @Override
  protected long getReportingInterval() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.47
     The Interval-Time  AVP is of type Unsigned32 and it contains reporting frequency. Its minimum value shall be 1 and maximum value shall be 8639999.
  */
    long reportingInterval = 8639998L;
    return reportingInterval;
  }

  @Override
  protected int getPrioritizedListIndicator() {
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
  protected byte[] getVisitedPLMNId() {
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
    byte[] visitedPlmnIdList = hexStringToByteArray("473800");
    return visitedPlmnIdList;
  }

  @Override
  protected int getPeriodicLocationSupportIndicator() {
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


  //*********************************************************
  //***************** LRA methods ***************************
  //*********************************************************

  // (commented LRA methods are already defined above for PLR)

  /*@Override
  protected java.net.InetAddress getGMLCAddress()*/

  @Override
  protected long getLRAFLags() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.56
    Bit	Event Type                    Description
    0   MO-LR-ShortCircuit-Indicator  This bit, when set, indicates that the MO-LR short circuit feature is used for obtaining location estimate.
                                      This bit is applicable only when the message is sent over Lgd interface.
  */
    long lraFlags = 0;
    return lraFlags;
  }

  /*@Override
  protected int getPrioritizedListIndicator()*/

  /*@Override
  protected byte[] getVisitedPLMNId()*/

  /*@Override
  protected int getPeriodicLocationSupportIndicator()*/

  /*@Override
  protected byte[] getLCSReferenceNumber()*/


  public boolean isReceivedLRR() {
    return receivedLRR;
  }

  public boolean isSentLRA() {
    return sentLRA;
  }


}
