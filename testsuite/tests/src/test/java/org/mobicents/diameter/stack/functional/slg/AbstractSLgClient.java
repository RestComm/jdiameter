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

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slg.LocationReportAnswerImpl;
import org.jdiameter.common.impl.app.slg.ProvideLocationRequestImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 *
 *@author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public abstract class AbstractSLgClient extends TBase implements ClientSLgSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ClientSLgSession clientSLgSession;
  protected ServerSLgSession serverSLgSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777255));
      SLgSessionFactoryImpl sLgSessionFactory = new SLgSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerSLgSession.class, sLgSessionFactory);
      sessionFactory.registerAppFacory(ClientSLgSession.class, sLgSessionFactory);

      sLgSessionFactory .setClientSessionListener(this);

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

  public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer) throws InternalException,
      IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"PLA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request) throws InternalException, IllegalDiameterStateException,
      RouteException, OverloadException {
    fail("Received \"LRR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  // ----------- conf parts

  public String getSessionId() {
    return this.clientSLgSession.getSessionId();
  }

  public ClientSLgSession getSession() {
    return this.clientSLgSession;
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.serverSLgSession = stack.getSession(sessionId, ServerSLgSession.class);
  }

  // Attributes for Provide Location Request (PLR) and Location Report Answer
  protected abstract int getSLgLocationType();
  protected abstract String getUserName(); // IE: IMSI
  protected abstract byte[] getMSISDN();
  protected abstract String getIMEI();
  protected abstract String getLCSNameString();
  protected abstract int getLCSFormatIndicator();
  protected abstract int getLCSClientType();
  protected abstract String getLCSRequestorIdString();
  protected abstract int getReqLCSFormatIndicator();
  protected abstract long getLCSPriority();
  protected abstract int getLCSQoSClass();
  protected abstract long getHorizontalAccuracy();
  protected abstract long getVerticalAccuracy();
  protected abstract int getVerticalRequested();
  protected abstract int getResponseTime();
  protected abstract int getVelocityRequested();
  protected abstract long getSupportedGADShapes();
  protected abstract long getLSCServiceTypeId();
  protected abstract String getLCSCodeword();
  protected abstract String getServiceSelection(); // IE: APN
  protected abstract int getLCSPrivacyCheckSession();
  protected abstract int getLCSPrivacyCheckNonSession();
  protected abstract long getDeferredLocationType();
  protected abstract byte[] getLCSReferenceNumber();
  protected abstract int getOccurrenceInfo();
  protected abstract long getIntervalTime();
  protected abstract long getAreaType();
  protected abstract byte[] getAreaIdentification();
  protected abstract java.net.InetAddress getGMLCAddress();
  protected abstract long getPLRFLags();
  protected abstract long getReportingAmount();
  protected abstract long getReportingInterval();
  protected abstract int getPrioritizedListIndicator();
  protected abstract byte[] getVisitedPLMNId();
  protected abstract int getPeriodicLocationSupportIndicator();
  protected abstract long getLRAFLags();

  // ----------- 3GPP TS 29.172 reference

  protected ProvideLocationRequest createPLR(ClientSLgSession slgSession) throws Exception {
  /*
   < Provide-Location-Request> ::=	< Diameter Header: 8388620, REQ, PXY, 16777255 >

	< Session-Id >
	[ Vendor-Specific-Application-Id ]
	{ Auth-Session-State }
	{ Origin-Host }
	{ Origin-Realm }
	{ Destination-Host }
	{ Destination-Realm }
	{ SLg-Location-Type }
	[ User-Name ]
	[ MSISDN ]
	[ IMEI ]
	{ LCS-EPS-Client-Name }
	{ LCS-Client-Type }
	[ LCS-Requestor-Name ]
	[ LCS-Priority ]
	[ LCS-QoS ]
	[ Velocity-Requested ]
	[ LCS-Supported-GAD-Shapes ]
	[ LCS-Service-Type-ID ]
	[ LCS-Codeword ]
	[ LCS-Privacy-Check-Non-Session ]
	[ LCS-Privacy-Check-Session ]
	[ Service-Selection ]
	[ Deferred-Location-Type ]
	[ PLR-Flags ]
	*[ Supported-Features ]
	*[ AVP ]
	*[ Proxy-Info ]
	*[ Route-Record ]
	Note: plus all extra AVPs defined in Table 6.2.2-1: Provide Subscriber Location Request, i.e.
	[ LCS-Reference-Number ]
	[ Area-Event-Info ]
	[ GMLC-Address ]
	[ Periodic-LDR-Information ]
	[ Reporting-PLMN-List ]
  */
    // Create ProvideLocationRequest
    ProvideLocationRequest plr = new ProvideLocationRequestImpl(slgSession.getSessions().get(0).createRequest(ProvideLocationRequest.code, getApplicationId(),
        getServerRealmName()));
    // < Provide-Location-Request> ::=	< Diameter Header: 8388620, REQ, PXY, 16777255 >

    AvpSet reqSet = plr.getMessage().getAvps();

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


    // { SLg-Location-Type }
    int slgLocationType = getSLgLocationType();
    if (slgLocationType != -1){
      reqSet.addAvp(Avp.SLG_LOCATION_TYPE, slgLocationType, 10415, true, false);
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

    // { LCS-Client-Type }
    int lcsClientType = getLCSClientType();
    if (lcsClientType != -1){
      reqSet.addAvp(Avp.LCS_CLIENT_TYPE, lcsClientType, 10415, false, false);
    }

    // [ LCS-Requestor-Name ]
    AvpSet lcsRequestorName = reqSet.addGroupedAvp(Avp.LCS_REQUESTOR_NAME, 10415, false, false);
    String lcsRequestorIdString = getLCSRequestorIdString();
    int reqLCSFormatIndicator = getReqLCSFormatIndicator();

    if (lcsRequestorIdString != null){
      lcsRequestorName.addAvp(Avp.LCS_REQUESTOR_ID_STRING, lcsRequestorIdString, 10415, false, false, false);
    }
    if (reqLCSFormatIndicator != -1){
      lcsRequestorName.addAvp(Avp.LCS_FORMAT_INDICATOR, reqLCSFormatIndicator, 10415, false, false);
    }

    // [ LCS-Priority ]
    long lcsPriority = getLCSPriority();
    if (lcsPriority != -1){
      reqSet.addAvp(Avp.LCS_PRIORITY, lcsPriority, 10415, false, false, true);
    }

    // [ LCS-QoS ]
    AvpSet lcsQoS = reqSet.addGroupedAvp(Avp.LCS_QOS, 10415, false, false);
    int lcsQoSClass = getLCSQoSClass();
    long horizontalAccuracy = getHorizontalAccuracy();
    long verticalAccuracy = getVerticalAccuracy();
    int verticalRequested = getVerticalRequested();
    int responseTime = getResponseTime();

    if (lcsQoSClass != -1){
      lcsQoS.addAvp(Avp.LCS_QOS_CLASS, lcsQoSClass, 10415, false, false);
    }
    if (horizontalAccuracy != -1){
      lcsQoS.addAvp(Avp.HORIZONTAL_ACCURACY, horizontalAccuracy, 10415, false, false, true);
    }
    if(verticalAccuracy != -1){
      lcsQoS.addAvp(Avp.VERTICAL_ACCURACY, verticalAccuracy, 10415, false, false, true);
    }
    if(verticalRequested != -1){
      lcsQoS.addAvp(Avp.VERTICAL_REQUESTED, verticalRequested, 10415, false, false);
    }
    if(responseTime != -1){
      lcsQoS.addAvp(Avp.RESPONSE_TIME, responseTime, 10415, false, false);
    }

    // [ Velocity-Requested ]
    int velocityRequested = getVelocityRequested();
    if (velocityRequested != -1){
      reqSet.addAvp(Avp.VELOCITY_REQUESTED, velocityRequested, 10415, false, false);
    }

    // [ LCS-Supported-GAD-Shapes ]
    long supportedGADShapes = getSupportedGADShapes();
    if (supportedGADShapes != -1){
      reqSet.addAvp(Avp.SUPPORTED_GAD_SHAPES, supportedGADShapes, 10415, false, false, true);
    }

    // [ LCS-Service-Type-ID ]
    long lscServiceTypeId = getLSCServiceTypeId();
    if (lscServiceTypeId != -1){
      reqSet.addAvp(Avp.LCS_SERVICE_TYPE_ID, lscServiceTypeId, 10415, true, false, true);
    }

    // [ LCS-Codeword ]
    String lcsCodeword = getLCSCodeword();
    if (lcsCodeword != null){
      reqSet.addAvp(Avp.LCS_CODEWORD, lcsCodeword, 10415, false, false, false);
    }

    //[ Service-Selection ]
    String serviceSelection = getServiceSelection(); // IE: APN
    if (serviceSelection != null){
      reqSet.addAvp(Avp.SERVICE_SELECTION, serviceSelection, false, false, false);
    }

    // [ LCS-Privacy-Check-Session ] // IE: Session-Related Privacy Check
    AvpSet lcsPrivacyCheckSession = reqSet.addGroupedAvp(Avp.LCS_PRIVACY_CHECK_SESSION, 10415, false, false);
    int lcsPrivacyCheck = getLCSPrivacyCheckSession();

    if (lcsPrivacyCheck != -1){
      lcsPrivacyCheckSession.addAvp(Avp.LCS_PRIVACY_CHECK, lcsPrivacyCheck, 10415, false, false);
    }

    // [ LCS-Privacy-Check-Non-Session ] // IE: Non-Session-Related Privacy Check
    AvpSet lcsPrivacyCheckNonSession = reqSet.addGroupedAvp(Avp.LCS_PRIVACY_CHECK_SESSION, 10415, false, false);
    int lcsPrivacyCheckNS = getLCSPrivacyCheckNonSession();

    if (lcsPrivacyCheckNS != -1){
      lcsPrivacyCheckNonSession.addAvp(Avp.LCS_PRIVACY_CHECK, lcsPrivacyCheck, 10415, false, false);
    }

    // [ Deferred-Location-Type ]
    long deferredLocationType = getDeferredLocationType();
    if (deferredLocationType != -1){
      reqSet.addAvp(Avp.DEFERRED_LOCATION_TYPE, deferredLocationType, 10415, false, false, true);
    }

    // [ LCS-Reference-Number ]
    byte[] lcsReferenceNumber = getLCSReferenceNumber();
    if (lcsReferenceNumber != null){
      reqSet.addAvp(Avp.LCS_REFERENCE_NUMBER, lcsReferenceNumber, 10415, true, false);
    }

    // [ Area-Event-Info ]
/*
    Area-Event-Info AVP of type grouped, includes:
      Area-Definition, Occurrence-Info, Interval-Time
    Area-Definition AVP of type grouped, includes:
      Area-Type, Area-Identification
*/
    AvpSet areaEventInfo = reqSet.addGroupedAvp(Avp.AREA_EVENT_INFO, 10415, false, false);
    int occurrenceInfo = getOccurrenceInfo();
    long intervalTime = getIntervalTime();
    AvpSet areaDefinition =reqSet.addGroupedAvp(Avp.AREA_DEFINITION, 10415, false, false);
    long areaType = getAreaType();
    byte[] areaIdentification = getAreaIdentification();

    if (occurrenceInfo != -1){
      areaEventInfo.addAvp(Avp.OCCURRENCE_INFO, occurrenceInfo, 10415, false, false);
    }
    if (intervalTime != -1){
      areaEventInfo.addAvp(Avp.INTERVAL_TIME, intervalTime, 10415, false, false, true);
    }
    if (areaDefinition != null){
      areaEventInfo.addAvp(areaDefinition);
    }
    if (areaType != -1){
      areaDefinition.addAvp(Avp.AREA_TYPE, areaType, 10415, false, false, true);
    }
    if (areaIdentification != null){
      areaDefinition.addAvp(Avp.AREA_IDENTIFICATION, areaIdentification, 10415, false, false);
    }


    //[ GMLC-Address ]
    java.net.InetAddress gmlcAddress = getGMLCAddress();
    if (gmlcAddress != null){
      reqSet.addAvp(Avp.GMLC_ADDRESS, gmlcAddress, 10415, false, false);
    }

    //[ PLR-Flags ]
    long plrfLags = getPLRFLags();
    if (plrfLags != -1){
      reqSet.addAvp(Avp.PLR_FLAGS, plrfLags, 10415, false, false, true);
    }

    // [ Periodic-LDR-Information ]
    AvpSet periodicLDRInformation = reqSet.addGroupedAvp(Avp.AREA_EVENT_INFO, 10415, false, false);
    long reportingAmount = getReportingAmount();
    long reportingInterval = getReportingInterval();

    if (reportingAmount != -1){
      periodicLDRInformation.addAvp(Avp.REPORTING_AMOUNT, reportingAmount, 10415, false, false, true);
    }
    if (reportingInterval != -1){
      periodicLDRInformation.addAvp(Avp.REPORTING_INTERVAL, reportingInterval, 10415, false, false, true);
    }

    // [ Reporting-PLMN-List ]
    AvpSet reportingPLMNList = reqSet.addGroupedAvp(Avp.REPORTING_PLMN_LIST, 10415, false, false);
    int prioritizedListIndicator = getPrioritizedListIndicator();
    AvpSet plmnIdList = reqSet.addGroupedAvp(Avp.PLMN_ID_LIST, 10415, false, false);
    byte[] visitedPLMNId = getVisitedPLMNId();
    int periodicLocationSupportIndicator = getPeriodicLocationSupportIndicator();

    if (prioritizedListIndicator != -1){
      reportingPLMNList.addAvp(Avp.PRIORITIZED_LIST_INDICATOR, prioritizedListIndicator, 10415, false, false);
    }
    if (plmnIdList != null){
      reportingPLMNList.addAvp(plmnIdList);
    }
    if (visitedPLMNId != null){
      plmnIdList.addAvp(Avp.VISITED_PLMN_ID, visitedPLMNId, 10415, false, false);
    }
    if (periodicLocationSupportIndicator != -1){
      plmnIdList.addAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR, periodicLocationSupportIndicator, 10415, false, false);
    }

    return plr;
  }

  // ----------- 3GPP TS 29.172 reference

  public LocationReportAnswer createLRA(LocationReportRequest lrr, long resultCode) throws Exception {
  /*
  < Location-Report-Answer > ::=	< Diameter Header: 8388621, PXY, 16777255>

    < Session-Id >
	[ Vendor-Specific-Application-Id ]
	[ Result-Code ]
	[ Experimental-Result ]
	{ Auth-Session-State }
	{ Origin-Host }
	{ Origin-Realm }
	[ GMLC-Address ]
	[ LRA-Flags ]
	[ Reporting-PLMN-List ]
	[ LCS-Reference-Number ]
	*[ Supported-Features ]
	*[ AVP ]
	*[ Failed-AVP ]
	*[ Proxy-Info ]
	*[ Route-Record ]

  */
    LocationReportAnswer lra = new LocationReportAnswerImpl((Request) lrr.getMessage(), resultCode);

    AvpSet reqSet = lrr.getMessage().getAvps();
    AvpSet set = lra.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    // { Vendor-Specific-Application-Id }
    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }
    // [ Result-Code ]
    // [ Experimental-Result ]
    // { Auth-Session-State }
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }

    //[ GMLC-Address ]
    java.net.InetAddress gmlcAddress = getGMLCAddress();
    if (gmlcAddress != null){
      set.addAvp(Avp.GMLC_ADDRESS, gmlcAddress, 10415, false, false);
    }

    // [ LRA-Flags ]
    long lraFlags = getLRAFLags();
    if (lraFlags != -1){
      set.addAvp(Avp.LRA_FLAGS, lraFlags, 10415, false, false, true);
    }

    //[ Reporting-PLMN-List ]
/*
  Reporting-PLMN-List ::= <AVP header: 2543 10415>
    1*20{ PLMN-ID-List }
    [ Prioritized-List-Indicator ]
    *[ AVP ]

  PLMN-ID-List ::= <AVP header: 2544 10415>
    { Visited-PLMN-Id }
    [ Periodic-Location-Support-Indicator ]
    *[ AVP ]
*/
    AvpSet reportingPLMNList = set.addGroupedAvp(Avp.REPORTING_PLMN_LIST, 10415, false, false);
    int prioritizedListIndicator = getPrioritizedListIndicator();
    AvpSet plmnIdList = set.addGroupedAvp(Avp.PLMN_ID_LIST, 10415, false, false);
    byte[] visitedPLMNId = getVisitedPLMNId();
    int periodicLocationSupportIndicator = getPeriodicLocationSupportIndicator();

    if (prioritizedListIndicator != -1){
      reportingPLMNList.addAvp(Avp.PRIORITIZED_LIST_INDICATOR, prioritizedListIndicator, 10415, false, false);
    }
    if (plmnIdList != null){
      reportingPLMNList.addAvp(plmnIdList);
    }
    if (visitedPLMNId != null){
      plmnIdList.addAvp(Avp.VISITED_PLMN_ID, visitedPLMNId, 10415, false, false);
    }
    if (periodicLocationSupportIndicator != -1){
      plmnIdList.addAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR, periodicLocationSupportIndicator, 10415, false, false);
    }

    // [ LCS-Reference-Number ]
    byte[] lcsReferenceNumber = getLCSReferenceNumber();
    if (lcsReferenceNumber != null){
      set.addAvp(Avp.LCS_REFERENCE_NUMBER, lcsReferenceNumber, 10415, true, false);
    }

    return lra;
  }


}
