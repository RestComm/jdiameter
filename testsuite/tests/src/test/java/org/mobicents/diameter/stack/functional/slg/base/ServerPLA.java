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

package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractImmediateServer;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */
public class ServerPLA extends AbstractImmediateServer {

  protected boolean receivedPLR;
  protected boolean sentPLA;
  protected boolean receivedLRR;

  protected ProvideLocationRequest provideLocationRequest;
  protected LocationReportRequest locationReportRequest;

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
  public void doLocationReportRequestEvent(ServerSLgSession session, LocationReportRequest request)
    throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedLRR) {
      fail("Received LRR more than once", null);
      return;
    }
    this.receivedLRR = true;
    this.locationReportRequest = request;
  }

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



}
