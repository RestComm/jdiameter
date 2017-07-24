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

package org.mobicents.diameter.stack.functional.slg;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slg.LocationReportRequestImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public abstract class AbstractDeferredClient extends TBase implements ClientSLgSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ClientSLgSession clientSLgSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777255));
      SLgSessionFactoryImpl sLgSessionFactory = new SLgSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerSLgSession.class, sLgSessionFactory);
      sessionFactory.registerAppFacory(ClientSLgSession.class, sLgSessionFactory);

      sLgSessionFactory.setClientSessionListener(this);

      this.clientSLgSession = ((ISessionFactory) this.sessionFactory).getNewAppSession(this.sessionFactory.getSessionId("xx-SLg-TESTxx"), getApplicationId(),
          ClientSLgSession.class, null);
    } finally {
      try {
        configStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  // ----------- delegate methods so

  public void start() throws IllegalDiameterStateException, InternalException {
    stack.start();
  }

  public void start(Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    stack.start(mode, timeOut, timeUnit);
  }

  public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
    stack.stop(timeOut, timeUnit, disconnectCause);
  }

  public void stop(int disconnectCause) {
    stack.stop(disconnectCause);
  }

  // ------- def methods, to fail :)

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException,
      RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  public void doLocationReportAnswerEvent(ClientSLgSession session, LocationReportRequest request, LocationReportAnswer answer) throws InternalException,
      IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"LRA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  // ----------- conf parts

  public String getSessionId() {
    return this.clientSLgSession.getSessionId();
  }

  public ClientSLgSession getSession() {
    return this.clientSLgSession;
  }

  // Attributes for Location Report Request (LRR)
  protected abstract String getUserName(); // IE: IMSI
  protected abstract byte[] getMSISDN();
  protected abstract String getIMEI();
  protected abstract String getLCSNameString();
  protected abstract int getLCSFormatIndicator();
  protected abstract int getLCSQoSClass();
  protected abstract long getLSCServiceTypeId();
  protected abstract long getDeferredLocationType();
  protected abstract byte[] getLCSReferenceNumber();
  protected abstract java.net.InetAddress getGMLCAddress();
  protected abstract long getReportingAmount();
  protected abstract long getReportingInterval();
  protected abstract int getLocationEvent();
  protected abstract byte[] getLocationEstimate();
  protected abstract int getAccuracyFulfilmentIndicator();
  protected abstract long getAgeOfLocationEstimate();
  protected abstract byte[] getVelocityEstimate();
  protected abstract byte[] getEUTRANPositioningData();
  protected abstract byte[] getECGI();
  protected abstract byte[] getGERANPositioningData();
  protected abstract byte[] getGERANGANSSPositioningData();
  protected abstract byte[] getCellGlobalIdentity();
  protected abstract byte[] getUTRANPositioningData();
  protected abstract byte[] getUTRANGANSSPositioningData();
  protected abstract byte[] getServiceAreaIdentity();
  protected abstract int getPseudonymIndicator();
  protected abstract byte[] getSGSNNumber();
  protected abstract String getSGSNName();
  protected abstract String getSGSNRealm();
  protected abstract String getMMEName();
  protected abstract String getMMERealm();
  protected abstract byte[] getMSCNumber();
  protected abstract String get3GPPAAAServerName();
  protected abstract long getLCSCapabilitiesSets();
  protected abstract long getLRRFLags();
  protected abstract long getTerminationCause();
  protected abstract long getCellPortionId();
  protected abstract byte[] get1xRTTRCID();
  protected abstract String getCivicAddress();
  protected abstract long getBarometricPressure();

  // ----------- 3GPP TS 29.172 reference

  protected LocationReportRequest createLRR(ClientSLgSession slgSession) throws Exception {
  /*
   < Location-Report-Request> ::=	< Diameter Header: 8388621, REQ, PXY, 16777255 >

	< Session-Id >
	[ Vendor-Specific-Application-Id ]
	{ Auth-Session-State }
	{ Origin-Host }
	{ Origin-Realm }
	{ Destination-Host }
	{ Destination-Realm }
	{ Location-Event }
	[ LCS-EPS-Client-Name ]
	[ User-Name ]
	[ MSISDN]
	[ IMEI ]
	[ Location-Estimate ]
	[ Accuracy-Fulfilment-Indicator ]
	[ Age-Of-Location-Estimate ]
	[ Velocity-Estimate ]
	[ EUTRAN-Positioning-Data ]
	[ ECGI ]
	[ GERAN-Positioning-Info ]
	[ Cell-Global-Identity ]
	[ UTRAN-Positioning-Info ]
	[ Service-Area-Identity ]
	[ LCS-Service-Type-ID ]
	[ Pseudonym-Indicator ]
	[ LCS-QoS-Class ]
	[ Serving-Node ]
	[ LRR-Flags ]
	[ LCS-Reference-Number ]
	[ Deferred-MT-LR-Data]
	[ GMLC-Address ]
	[ Periodic-LDR-Information ]
	[ ESMLC-Cell-Info ]
	[ 1xRTT-RCID ]
	[ Civic-Address ]
	[ Barometric-Pressure ]
	*[ Supported-Features ]
	*[ AVP ]
	*[ Proxy-Info ]
  *[ Route-Record ]

  */
    // Create LocationReportRequest
    LocationReportRequest lrr = new LocationReportRequestImpl(slgSession.getSessions().get(0).createRequest(LocationReportRequest.code, getApplicationId(),
        getServerRealmName()));
    // < Location-Report-Request> ::=	< Diameter Header: 8388621, REQ, PXY, 16777255 >

    AvpSet reqSet = lrr.getMessage().getAvps();

    if (reqSet.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = reqSet.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }

    // { Auth-Session-State }
    if (reqSet.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      reqSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }

    // { Origin-Host }
    reqSet.removeAvp(Avp.ORIGIN_HOST);
    reqSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);

    // { Location-Event }
    int locationEvent = getLocationEvent();
    if (locationEvent != -1){
      reqSet.addAvp(Avp.LOCATION_EVENT, locationEvent, 10415, true, false);
    }

    // { LCS-EPS-Client-Name }
    AvpSet lcsEPSClientName = reqSet.addGroupedAvp(Avp.LCS_EPS_CLIENT_NAME, 10415, false, false);
    String lcsNameString = getLCSNameString();
    int lcsFormatIndicator = getLCSFormatIndicator();

    if (lcsNameString != null){
      lcsEPSClientName.addAvp(Avp.LCS_NAME_STRING, lcsNameString, 10415, false, false, false);
    }
    if (lcsFormatIndicator != -1){
      lcsEPSClientName.addAvp(Avp.LCS_FORMAT_INDICATOR, lcsFormatIndicator, 10415, false, false);
    }

    // [ User-Name ] IE: IMSI
    String userName = getUserName();
    if (userName != null) {
      reqSet.addAvp(Avp.USER_NAME, userName, 10415, true, false, false);
    }

    // [ MSISDN ]
    byte[] msisdn = getMSISDN();
    if (msisdn != null){
      reqSet.addAvp(Avp.MSISDN, msisdn, 10415, true, false);
    }

    // [ IMEI ]
    String imei = getIMEI();
    if (imei != null){
      reqSet.addAvp(Avp.TGPP_IMEI, imei, 10415, false, false, false);
    }

    // [ Location-Estimate ]
    byte[] locationEstimate = getLocationEstimate();
    if (locationEstimate != null){
      reqSet.addAvp(Avp.LOCATION_ESTIMATE, locationEstimate, 10415, true, false);
    }

    // [ Accuracy-Fulfilment-Indicator ]
    int accuracyFulfilmentIndicator = getAccuracyFulfilmentIndicator();
    if (accuracyFulfilmentIndicator != -1){
      reqSet.addAvp(Avp.ACCURACY_FULFILMENT_INDICATOR, accuracyFulfilmentIndicator, 10415, false, false);
    }

    // [ Age-Of-Location-Estimate ]
    long ageOfLocationEstimate = getAgeOfLocationEstimate();
    if (ageOfLocationEstimate != -1){
      reqSet.addAvp(Avp.AGE_OF_LOCATION_ESTIMATE, ageOfLocationEstimate, 10415, false, false, true);
    }

    // [ Velocity-Estimate ]
    byte[] velocityEstimate = getVelocityEstimate();
    if (velocityEstimate != null){
      reqSet.addAvp(Avp.VELOCITY_ESTIMATE, velocityEstimate, 10415, false, false);
    }

    // [ EUTRAN-Positioning-Data ]
    byte[] eutranPositioningData = getEUTRANPositioningData();
    if (eutranPositioningData != null){
      reqSet.addAvp(Avp.EUTRAN_POSITIONING_DATA, eutranPositioningData, 10415, false, false);
    }

    // [ ECGI ]
    byte[] ecgi = getECGI();
    if (ecgi != null){
      reqSet.addAvp(Avp.ECGI, ecgi, 10415, false, false);
    }

    // [ GERAN-Positioning-Info ]
    AvpSet geranPositioningInfo = reqSet.addGroupedAvp(Avp.GERAN_POSITIONING_INFO, 10415, false, false);
    byte[] geranPositioningData = getGERANPositioningData();
    byte[] geranGanssPositioningData = getGERANGANSSPositioningData();

    if (geranPositioningData != null){
      geranPositioningInfo.addAvp(Avp.GERAN_POSITIONING_DATA, geranPositioningData, 10415, false, false);
    }
    if (geranGanssPositioningData != null){
      geranPositioningInfo.addAvp(Avp.GERAN_GANSS_POSITIONING_DATA, geranGanssPositioningData, 10415, false, false);
    }

    // [ Cell-Global-Identity ]
    byte[] cellGlobalIdentity = getCellGlobalIdentity();
    if (cellGlobalIdentity != null){
      reqSet.addAvp(Avp.CELL_GLOBAL_IDENTITY, cellGlobalIdentity, 10415, false, false);
    }

    // [ UTRAN-Positioning-Info ]
    AvpSet utranPositioningInfo = reqSet.addGroupedAvp(Avp.UTRAN_POSITIONING_INFO, 10415, false, false);
    byte[] utranPositioningData = getUTRANPositioningData();
    byte[] utranGanssPositioningData = getUTRANGANSSPositioningData();

    if (utranPositioningData != null){
      utranPositioningInfo.addAvp(Avp.UTRAN_POSITIONING_DATA, utranPositioningData, 10415, false, false);
    }
    if (utranGanssPositioningData != null){
      utranPositioningInfo.addAvp(Avp.UTRAN_GANSS_POSITIONING_DATA, utranGanssPositioningData, 10415, false, false);
    }

    // [ Service-Area-Identity ]
    byte[] serviceAreaIdentity = getServiceAreaIdentity();
    if (serviceAreaIdentity != null){
      reqSet.addAvp(Avp.SERVICE_AREA_IDENTITY, serviceAreaIdentity, 10415, false, false);
    }

    // [ LCS-Service-Type-ID ]
    long lscServiceTypeId = getLSCServiceTypeId();
    if (lscServiceTypeId != -1){
      reqSet.addAvp(Avp.LCS_SERVICE_TYPE_ID, lscServiceTypeId, 10415, false, false, true);
    }

    // [ Pseudonym-Indicator ]
    int pseudonymIndicator = getPseudonymIndicator();
    if (pseudonymIndicator != -1){
      reqSet.addAvp(Avp.PSEUDONYM_INDICATOR, pseudonymIndicator, 10415, false, false);
    }

    // [ LCS-QoS-Class ]
    int lcsQoSClass = getLCSQoSClass();
    if (lcsQoSClass != -1){
      reqSet.addAvp(Avp.LCS_QOS_CLASS, lcsQoSClass, 10415, false, false);
    }

    // [ Serving-Node ] IE: Target Serving Node Identity
/*
  Serving-Node ::= <AVP header: 2401 10415>
    [ SGSN-Number ]
    [ SGSN-Name ]
    [ SGSN-Realm ]
    [ MME-Name ]
    [ MME-Realm ]
    [ MSC-Number ]
    [ 3GPP-AAA-Server-Name ]
    [ LCS-Capabilities-Sets ]
    [ GMLC-Address ]
    *[AVP]

*/
    AvpSet servingNode = reqSet.addGroupedAvp(Avp.SERVING_NODE, 10415, true, false);
    byte[] sgsnNumber = getSGSNNumber();
    String sgsnName= getSGSNName();
    String sgsnRealm = getSGSNRealm();
    String mmeName = getMMEName();
    String mmeRealm = getMMERealm();
    byte[] mscNumber = getMSCNumber();
    String tgppAAAServerName= get3GPPAAAServerName();
    long lcsCapabilitiesSet = getLCSCapabilitiesSets();
    java.net.InetAddress gmlcAddress = getGMLCAddress();

    if (sgsnNumber != null){
      servingNode.addAvp(Avp.SGSN_NUMBER, sgsnNumber, 10415, false, false);
    }
    if (sgsnName != null){
      servingNode.addAvp(Avp.SGSN_NAME, sgsnName, 10415, false, false, false);
    }
    if (sgsnRealm != null){
      servingNode.addAvp(Avp.SGSN_REALM, sgsnRealm, 10415, false, false, false);
    }
    if (mmeName != null){
      servingNode.addAvp(Avp.MME_NAME, mmeName, 10415, false, false, false);
    }
    if (mmeRealm != null){
      servingNode.addAvp(Avp.MME_REALM, mmeRealm, 10415, false, false, false);
    }
    if (mscNumber != null){
      servingNode.addAvp(Avp.MSC_NUMBER, mscNumber, 10415, false, false);
    }
    if (tgppAAAServerName != null){
      servingNode.addAvp(Avp.TGPP_AAA_SERVER_NAME, tgppAAAServerName, 10415, false, false, false);
    }
    if (lcsCapabilitiesSet != -1){
      servingNode.addAvp(Avp.LCS_CAPABILITIES_SETS, lcsCapabilitiesSet, 10415, false, false, true);
    }
    if (gmlcAddress != null){
      servingNode.addAvp(Avp.GMLC_ADDRESS, gmlcAddress, 10415, false, false);
    }

    // [ LRR-Flags ]
    long lrrFlags = getLRRFLags();
    if (lrrFlags != -1){
      reqSet.addAvp(Avp.LRR_FLAGS, lrrFlags, 10415, false, false, true);
    }

    // [ LCS-Reference-Number ]
    byte[] lcsReferenceNumber = getLCSReferenceNumber();
    if (lcsReferenceNumber != null){
      reqSet.addAvp(Avp.LCS_REFERENCE_NUMBER, lcsReferenceNumber, 10415, true, false);
    }

    // [ Deferred-MT-LR-Data]
    AvpSet deferredMTLRData = reqSet.addGroupedAvp(Avp.DEFERRED_MT_LR_DATA, 10415, false, false);
    long deferredLocationType = getDeferredLocationType();
    long terminationCause = getTerminationCause();

    if (deferredLocationType != -1){
      deferredMTLRData.addAvp(Avp.DEFERRED_LOCATION_TYPE, deferredLocationType, 10415, false, false, true);
    }
    if (terminationCause != -1){
      deferredMTLRData.addAvp(Avp.TERMINATION_CAUSE_LCS, terminationCause, 10415, false, false, true);
    }

    // [ GMLC-Address ]
    // attribute already defined for grouped AVP Serving Node
    if (gmlcAddress != null){
      reqSet.addAvp(Avp.GMLC_ADDRESS, gmlcAddress, 10415, false, false);
    }

    //[ Periodic-LDR-Information ]
/*
Periodic-LDR-Info ::= <AVP header: 2540 10415>
  { Reporting-Amount }
  { Reporting-Interval }
  *[ AVP ]
Reporting-Interval x Rreporting-Amount shall not exceed 8639999 (99 days, 23 hours, 59 minutes and 59 seconds)
for compatibility with OMA MLP and RLP.
*/
    AvpSet periodicLDRInfo = reqSet.addGroupedAvp(Avp.PERIODIC_LDR_INFORMATION, 10415, false, false);
    long reportingAmount = getReportingAmount();
    long reportingInterval = getReportingInterval();

    if (reportingAmount != -1){
      periodicLDRInfo.addAvp(Avp.REPORTING_AMOUNT, reportingAmount, 10415, false, false, true);
    }
    if (reportingInterval != -1){
      periodicLDRInfo.addAvp(Avp.REPORTING_INTERVAL, reportingInterval, 10415, false, false, true);
    }

    // [ ESMLC-Cell-Info ]
/*
  ESMLC-Cell-Info ::= <AVP header: 2552 10415>
    [ ECGI ]
    [ Cell-Portion-ID ]
    *[ AVP ]
*/
    AvpSet esmlcCellInfo = reqSet.addGroupedAvp(Avp.ESMLC_CELL_INFO, 10415, false, false);
    // ECGI attribute already defined
    long cellPortionId = getCellPortionId();

    if (ecgi != null){
      esmlcCellInfo.addAvp(Avp.ECGI, ecgi, 10415, false, false);
    }
    if (cellPortionId != -1){
      esmlcCellInfo.addAvp(Avp.CELL_PORTION_ID, cellPortionId, 10415, false, false, true);
    }

    // [ 1xRTT-RCID ]
    byte[] onexRTTRCID = get1xRTTRCID();
    if (onexRTTRCID != null){
      reqSet.addAvp(Avp.ONEXRTT_RCID, onexRTTRCID, 10415, false, false);
    }

    // [ Civic-Address ]
    String civicAddress = getCivicAddress();
    if (civicAddress != null){
      reqSet.addAvp(Avp.CIVIC_ADDRESS, civicAddress, 10415, false, false, false);
    }

    // [ Barometric-Pressure ]
    long barometricPressure = getBarometricPressure();
    if (barometricPressure != -1){
      reqSet.addAvp(Avp.BAROMETRIC_PRESSURE, barometricPressure, 10415, false, false, true);
    }

    return lrr;
  }

}
