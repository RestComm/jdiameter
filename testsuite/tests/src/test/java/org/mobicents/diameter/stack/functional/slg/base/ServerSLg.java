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
import org.mobicents.diameter.stack.functional.slg.AbstractSLgServer;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ServerSLg extends AbstractSLgServer {

  protected boolean receivedPLR;
  protected boolean receivedLRA;
  protected boolean sentPLA;
  protected boolean sentLRR;

  protected ProvideLocationRequest provideLocationRequest;

  public void sendProvideLocationAnswer() throws Exception {
    if (!receivedPLR || provideLocationRequest == null) {
      fail("Did not receive PLR or answer already sent.", null);
      throw new Exception("Did not receive PLR or answer already sent. Request: " + this.provideLocationRequest);
    }

    ProvideLocationAnswer pla = super.createPLA(provideLocationRequest, 2001);

    super.serverSLgSession.sendProvideLocationAnswer(pla);

    this.sentPLA = true;
    provideLocationRequest = null;
    Utils.printMessage(log, super.stack.getDictionary(), pla.getMessage(), true);
  }

  public void sendLocationReportRequest() throws Exception {
    LocationReportRequest lrr = super.createLRR(super.serverSLgSession);
    this.serverSLgSession.sendLocationReportRequest(lrr);
    Utils.printMessage(log, super.stack.getDictionary(), lrr.getMessage(), true);
    this.sentLRR = true;
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.TBase#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != ProvideLocationRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverSLgSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    } else {
      try {

        super.serverSLgSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerSLgSession.class, (Object) null);
        ((NetworkReqListener) this.serverSLgSession).processRequest(request);

      } catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  @Override
  public void doProvideLocationRequestEvent(ServerSLgSession session, ProvideLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedPLR) {
      fail("Received PLR more than once", null);
      return;
    }
    this.receivedPLR = true;
    this.provideLocationRequest = request;
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

  // PLA methods

  @Override
  protected byte[] getLocationEstimate() {
    String locEstimate = "S35°38'15.37\" WE109°45'21.77\"";
    byte[] locationEstimate = locEstimate.getBytes();
    return locationEstimate;
  }

  @Override
  protected int getAccuracyFulfilmentIndicator() {
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
    long ageOfLocationEstimate = 37L;
    return ageOfLocationEstimate;
  }

  @Override
  protected byte[] getVelocityEstimate() {
/*
  3GPP TS 29.172 v13.0.0 section 7.4.17
    The Velocity-Estimate AVP is of type OctetString.
    It is composed of 4 or more octets with an internal structure according to 3GPP TS 23.032 [3].
*/
    String vel = "210";
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
    String eutran = "654E423435336C7465613233";
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
    String eCgi = "654E4239343337";
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
    String geran = "42545339343342534333";
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
    String geranGanss = "4254533733524E4331473433";
    byte[] geranGanssPositioningData = geranGanss.getBytes();
    return geranGanssPositioningData;
  }

  @Override
  protected byte[] getCellGlobalIdentity() {
    String cgi = "A342784713907";
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
    String utran = "4E42393433524E4331";
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
    String utranGanss = "4E42303331524E4335473433";
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
    String sai = "736572766963652D617265612D756D74732D33";
    byte[] serviceAreaIdentity = sai.getBytes();
    return serviceAreaIdentity;
  }

  @Override
  protected byte[] getSGSNNumber() {
    String sgsnNumString = "59899004501";
    byte[] sgsnNumber = sgsnNumString.getBytes();
    return sgsnNumber;
  }

  @Override
  protected String getSGSNName() {
    String sgsnName = "SGSN01";
    return sgsnName;
  }

  @Override
  protected String getSGSNRealm() {
    String sgsnRealm = "sgsn.restcomm.com";
    return sgsnRealm;
  }

  @Override
  protected String getMMEName() {
    String mmeName = "MME710";
    return mmeName;
  }

  @Override
  protected String getMMERealm() {
    String mmeRealm = "mme.restcomm.com";
    return mmeRealm;
  }

  @Override
  protected byte[] getMSCNumber() {
    String mscNumString = "59899001207";
    byte[] mscNumber = mscNumString.getBytes();
    return mscNumber;
  }

  @Override
  protected String get3GPPAAAServerName() {
    String tgppAAAServerName = "aaa.restcomm.com";
    return tgppAAAServerName;
  }

  @Override
  protected long getLCSCapabilitiesSets() {
    long lcsCapabilitiesSets = 99900123L;
    return lcsCapabilitiesSets;
  }

  @Override
  protected java.net.InetAddress getGMLCAddress() {
  /*
  3GPP TS 29.173 v13.0.0 section 6.4.7
    The GMLC-Address AVP is of type Address and shall contain the IPv4 or IPv6 address of H-GMLC or the V-GMLC associated with the serving node.
  */
    try {
      java.net.InetAddress gmlcAddress = java.net.InetAddress.getLocalHost();
      return gmlcAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected long getPLAFLags() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.53
  Bit	Event Type                                    Description
  0   Deferred-MT-LR-Response-Indicator             This bit, when set, indicates that the message is sent in response to the deferred-MT location request.
                                                    This bit is applicable only when the message is sent over Lgd interface.
  1   MO-LR-ShortCircuit-Indicator                  This bit, when set, indicates that the MO-LR short circuit feature is accepted by the UE,
                                                    for periodic location reporting. This bit is applicable only when the message is sent over Lgd interface.
  2   Optimized-LCS-Proc-Performed                  This bit, when set, indicates that the combined MME/SGSN has performed the optimized LCS procedure
                                                    to retrieve the location of the target UE. This bit is applicable only when the message is sent for
                                                    the MT-LR procedure.
  3   UE-Transiently-Not-Reachable-Indicator        This bit, when set, indicates that the UE is transiently not reachable due to power saving
                                                    (e.g. UE is in extended idle mode DRX or in Power Saving Mode), and that the location information
                                                    will be returned in a subsequent Subscriber Location Report when the UE becomes reachable.
  */
    long plaFlags = 8L;
    return plaFlags;
  }

  @Override
  protected long getCellPortionId() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.58
    The Cell-Portion-ID AVP is of type Unsigned32. It indicates the current Cell Portion location of the target UE as provided by the E-SMLC.
    It shall contain the value of the "Cell Portion ID" Information Element as defined in 3GPP TS 29.171
  */
    long cellPortionId = 34923L;
    return cellPortionId;
  }

  @Override
  protected String getCivicAddress() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.61
    The Civic-Address AVP is of type UTF8String.
    It contains the XML document carried in the "Civic Address" Information Element as defined in 3GPP TS 29.171.
  */
    String civicAddress = "<civicAddress xml:lang=\"en-US\"\n" +
        "        xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr\"\n" +
        "        xmlns:cae=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr:ext\">\n" +
        "     <country>US</country>\n" +
        "     <A1>CA</A1>\n" +
        "     <A2>Sacramento</A2>\n" +
        "     <RD>Colorado</RD>\n" +
        "     <HNO>223</HNO>\n" +
        "     <cae:STP>Boulevard</cae:STP>\n" +
        "     <cae:HNP>A</cae:HNP>\n" +
        "   </civicAddress>"; // From IETF RFC 6848, XML Example with Street Type Prefix and House Number Prefix
    return civicAddress;
  }

  @Override
  protected long getBarometricPressure() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.62
    The Barometric-Pressure AVP is of type Unsigned32.
    It contains the "Barometric Pressure" Information Element as defined in 3GPP TS 29.171.
  */
    long barometricPressure = 101327L;
    return barometricPressure;
  }

  public boolean isReceivedPLR() {
    return receivedPLR;
  }

  public boolean isSentPLA() {
    return sentPLA;
  }

  // LRR methods

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
  protected long getLSCServiceTypeId() {
    long lcsServiceTypeId = 234567012L;
    return lcsServiceTypeId;
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
  protected long getReportingAmount() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.46
     The Reporting-Amount AVP is of type Unsigned32 and it contains reporting frequency. Its minimum value shall be 1 and maximum value shall be 8639999.
  */
    long reportingAmount = 8639910L;
    return reportingAmount;
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
    "Normal"								    0
    "Error Undefined"					        1
    "Internal Timeout"						    2
    "Congestion"							    3
    "MT_LR_Restart"							    4
    "Privacy Violation"						    5
    "Shape of Location Estimate Not Supported"	6
    "Subscriber Termination"					7
    "UE Termination"							8
    "Network Termination"						9
  */
    long terminationCause = 7;
    return terminationCause;
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
  protected byte[] getLCSReferenceNumber() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.37
      The LCS-Reference-Number AVP is of type OctetString of length 1. It shall contain the reference number identifying the deferred location request.
  */
    String lcsRefNumber = "4C4353353739";
    byte[] lcsRefNum = lcsRefNumber.getBytes();
    return lcsRefNum;
  }

  /*@Override
  protected String getLCSNameString() */

  /*@Override
  protected byte[] getLocationEstimate()*/

  /*@Override
  protected int getAccuracyFulfilmentIndicator()*/

  /*@Override
  protected long getAgeOfLocationEstimate()*/

  /*@Override
  protected byte[] getVelocityEstimate()*/

  /*@Override
  protected byte[] getEUTRANPositioningData()*/

  /*@Override
  protected byte[] getECGI()*/

  /*@Override
  protected byte[] getGERANPositioningData()*/

  /*@Override
  protected byte[] getGERANGANSSPositioningData()*/

  /*@Override
  protected byte[] getCellGlobalIdentity()*/

  /*@Override
  protected byte[] getUTRANPositioningData()*/

  /*@Override
  protected byte[] getUTRANGANSSPositioningData()*/

  /*@Override
  protected byte[] getServiceAreaIdentity()*/

  /*@Override
  protected long getCellPortionId()*/

  /*@Override
  protected byte[] getSGSNNumber()*/

  /*@Override
  protected String getSGSNName()*/

  /*@Override
  protected String getSGSNRealm()*/

  /*@Override
  protected String getMMEName()*/

  /*@Override
  protected String getMMERealm()*/

  /*@Override
  protected byte[] getMSCNumber()*/

  /*@Override
  protected String get3GPPAAAServerName()*/

  /*@Override
  protected long getLCSCapabilitiesSets()*/

  /*@Override
  protected String getCivicAddress() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.61
    The Civic-Address AVP is of type UTF8String.
    It contains the XML document carried in the "Civic Address" Information Element as defined in 3GPP TS 29.171.
  */
  /*  String civicAddress = "<civicAddress xml:lang='en-GB'" +
        "           xmlns=\"urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr\"\n" +
        "           xmlns:cdc=\"http://devon.canals.example.com/civic\">\n" +
        "        <country>UK</country>\n" +
        "        <A1>Devon</A1>\n" +
        "        <A3>Monkokehampton</A3>\n" +
        "        <RD>Deckport</RD>\n" +
        "        <STS>Cross</STS>\n" +
        "\n" +
        "        <cdc:bridge>21451338</cdc:bridge>\n" +
        "\n" +
        "      </civicAddress>"; // From IETF RFC 6848, Extended Civic Address Example
    return civicAddress;
  }*/

  /*@Override
  protected long getBarometricPressure()*/

  public boolean isReceivedLRA() {
    return receivedLRA;
  }

  public boolean isSentLRR() {
    return sentLRR;
  }


}
