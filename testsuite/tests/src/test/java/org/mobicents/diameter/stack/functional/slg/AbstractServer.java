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

package org.mobicents.diameter.stack.functional.slg;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.*;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.impl.app.slg.ProvideLocationAnswerImpl;
import org.jdiameter.common.impl.app.slg.LocationReportAnswerImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public abstract class AbstractServer extends TBase implements ServerSLgSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ServerSLgSession serverSLgSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777255));
      SLgSessionFactoryImpl slgSessionFactory = new SLgSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerSLgSession.class, (IAppSessionFactory) slgSessionFactory);
      sessionFactory.registerAppFacory(ClientSLgSession.class, (IAppSessionFactory) slgSessionFactory);
      slgSessionFactory.setServerSessionListener(this);
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

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException,
          RouteException,
          OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  public void doProvideLocationRequestEvent(ServerSLgSession session, ProvideLocationRequest request) throws InternalException, IllegalDiameterStateException,
          RouteException, OverloadException {
    fail("Received \"PLR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  public void doLocationReportRequestEvent(ServerSLgSession session, ProvideLocationRequest request) throws InternalException, IllegalDiameterStateException,
          RouteException, OverloadException {
    fail("Received \"LRR\" event, request[" + request + "], on session[" + session + "]", null);
  }
  // -------- conf

  public String getSessionId() {
    return this.serverSLgSession.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.serverSLgSession = stack.getSession(sessionId, ServerSLgSession.class);
  }

  public ServerSLgSession getSession() {
    return this.serverSLgSession;
  }

  // Attributes for Provide Location Answer (PLA) and Location Report Answer (LRA)
  protected abstract String getLocationEstimate();
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
  protected abstract byte[] getSGSNNumber();
  protected abstract String getSGSNName();
  protected abstract String getSGSNRealm();
  protected abstract String getMMEName();
  protected abstract String getMMERealm();
  protected abstract byte[] getMSCNumber();
  protected abstract String get3GPPAAAServerName();
  protected abstract long getLCSCapabilitiesSets();
  protected abstract long getPLAFLags();
  protected abstract long getCellPortionId();
  protected abstract String getCivicAddress();
  protected abstract long getBarometricPressure();
  // Attributes only applying for Location Report Answer (LRA)
  protected abstract java.net.InetAddress getGMLCAddress();
  protected abstract long getLRAFLags();
  protected abstract int getPrioritizedListIndicator();
  protected abstract byte[] getVisitedPLMNId();
  protected abstract int getPeriodicLocationSupportIndicator();
  protected abstract byte[] getLCSReferenceNumber();

  // ----------- 3GPP TS 29.172 reference

  public ProvideLocationAnswer createPLA(ProvideLocationRequest plr, long resultCode) throws Exception {
  /*
   < Provide-Location-Answer > ::=	< Diameter Header: 8388620, PXY, 16777255 >
	< Session-Id >
	[ Vendor-Specific-Application-Id ]
	[ Result-Code ]
	[ Experimental-Result ]
	{ Auth-Session-State }
	{ Origin-Host }
	{ Origin-Realm }
	[ Location-Estimate ]
	[ Accuracy-Fulfilment-Indicator ]
	[ Age-Of-Location-Estimate]
	[ Velocity-Estimate ]
	[ EUTRAN-Positioning-Data]
	[ ECGI ]
	[ GERAN-Positioning-Info ]
	[ Cell-Global-Identity ]
	[ UTRAN-Positioning-Info ]
	[ Service-Area-Identity ]
	[ Serving-Node ]
	[ PLA-Flags ]
	[ ESMLC-Cell-Info ]
	[ Civic-Address ]
	[ Barometric-Pressure ]
	*[ Supported-Features ]
	*[ AVP ]
	*[ Failed-AVP ]
	*[ Proxy-Info ]
	*[ Route-Record ]

  */
    ProvideLocationAnswer pla = new ProvideLocationAnswerImpl((Request) plr.getMessage(), resultCode);

    AvpSet reqSet = plr.getMessage().getAvps();
    AvpSet set = pla.getMessage().getAvps();
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

    // [ Location-Estimate ]
    String locationEstimate = getLocationEstimate();
    if (locationEstimate != null){
      set.addAvp(Avp.LOCATION_ESTIMATE, locationEstimate, false);
    }

    // [ Accuracy-Fulfilment-Indicator ]
    int accuracyFulfilmentIndicator = getAccuracyFulfilmentIndicator();
    if (accuracyFulfilmentIndicator != -1){
      set.addAvp(Avp.ACCURACY_FULFILMENT_INDICATOR, accuracyFulfilmentIndicator);
    }

    // [ Age-Of-Location-Estimate ]
    long ageOfLocationEstimate = getAgeOfLocationEstimate();
    if (ageOfLocationEstimate != -1){
      set.addAvp(Avp.AGE_OF_LOCATION_ESTIMATE, ageOfLocationEstimate);
    }

    // [ Velocity-Estimate ]
    byte[] velocityEstimate = getVelocityEstimate();
    if (velocityEstimate != null){
      set.addAvp(Avp.VELOCITY_ESTIMATE, velocityEstimate);
    }

    // [ EUTRAN-Positioning-Data ]
    byte[] eutranPositioningData = getEUTRANPositioningData();
    if (eutranPositioningData != null){
      set.addAvp(Avp.EUTRAN_POSITIONING_DATA, eutranPositioningData);
    }

    // [ ECGI ]
    byte[] ecgi = getECGI();
    if (ecgi != null){
      set.addAvp(Avp.ECGI, ecgi);
    }

    // [ GERAN-Positioning-Info ]
    AvpSet geranPositioningInfo = set.addGroupedAvp(Avp.GERAN_POSITIONING_INFO, 10415, false, false);
    byte[] geranPositioningData = getGERANPositioningData();
    byte[] geranGanssPositioningData = getGERANGANSSPositioningData();

    if (geranPositioningData != null){
      geranPositioningInfo.addAvp(Avp.GERAN_POSITIONING_DATA, geranPositioningData);
    }
    if (geranGanssPositioningData != null){
      geranPositioningInfo.addAvp(Avp.GERAN_GANSS_POSITIONING_DATA, geranGanssPositioningData);
    }

    // [ Cell-Global-Identity ]
    byte[] cellGlobalIdentity = getCellGlobalIdentity();
    if (cellGlobalIdentity != null){
      set.addAvp(Avp.CELL_GLOBAL_IDENTITY, cellGlobalIdentity);
    }

    // [ UTRAN-Positioning-Info ]
    AvpSet utranPositioningInfo = set.addGroupedAvp(Avp.UTRAN_POSITIONING_INFO, 10415, false, false);
    byte[] utranPositioningData = getUTRANPositioningData();
    byte[] utranGanssPositioningData = getUTRANGANSSPositioningData();

    if ( utranPositioningData != null){
      utranPositioningInfo.addAvp(Avp.UTRAN_POSITIONING_DATA, utranPositioningData);
    }
    if ( utranGanssPositioningData != null){
      utranPositioningInfo.addAvp(Avp.UTRAN_GANSS_POSITIONING_DATA, utranGanssPositioningData);
    }

    // [ Service-Area-Identity ]
    byte[] serviceAreaIdentity = getServiceAreaIdentity();
    if (serviceAreaIdentity != null){
      set.addAvp(Avp.SERVICE_AREA_IDENTITY, serviceAreaIdentity);
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
    AvpSet servingNode = set.addGroupedAvp(Avp.SERVING_NODE, 10415, false, false);
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

    // [ PLA-Flags ]
    long plaFlags = getPLAFLags();
    if (plaFlags != -1){
      set.addAvp(Avp.PLA_FLAGS, plaFlags);
    }

    // [ ESMLC-Cell-Info ]
/*
  ESMLC-Cell-Info ::= <AVP header: 2552 10415>
    [ ECGI ]
    [ Cell-Portion-ID ]
    *[ AVP ]
*/
    AvpSet esmlcCellInfo = set.addGroupedAvp(Avp.ESMLC_CELL_INFO, 10415, false, false);
    // ECGI attribute already defined
    long cellPortionId = getCellPortionId();

    if (ecgi != null){
      esmlcCellInfo.addAvp(Avp.ECGI, ecgi);
    }
    if (cellPortionId != -1){
      esmlcCellInfo.addAvp(Avp.CELL_PORTION_ID, cellPortionId);
    }

    // [ Civic-Address ]
    String civicAddress = getCivicAddress();
    if (civicAddress != null){
      set.addAvp(Avp.CIVIC_ADDRESS, civicAddress, false);
    }

    // [ Barometric-Pressure ]
    long barometricPressure = getBarometricPressure();
    if (barometricPressure != -1){
      set.addAvp(Avp.BAROMETRIC_PRESSURE, barometricPressure);
    }

    return pla;
  }


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
      set.addAvp(Avp.GMLC_ADDRESS, gmlcAddress);
    }

    // [ LRA-Flags ]
    long lraFlags = getLRAFLags();
    if (lraFlags != -1){
      set.addAvp(Avp.LRA_FLAGS, lraFlags);
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

    if (visitedPLMNId != null){
      plmnIdList.addAvp(Avp.VISITED_PLMN_ID, visitedPLMNId);
    }
    if (periodicLocationSupportIndicator != -1){
      plmnIdList.addAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR, periodicLocationSupportIndicator);
    }
    if (plmnIdList != null){
      reportingPLMNList.addAvp(plmnIdList);
    }
    if (prioritizedListIndicator != -1){
      reportingPLMNList.addAvp(Avp.PRIORITIZED_LIST_INDICATOR, prioritizedListIndicator);
    }

    // [ LCS-Reference-Number ]
    byte[] lcsReferenceNumber = getLCSReferenceNumber();
    if (lcsReferenceNumber != null){
      set.addAvp(Avp.LCS_REFERENCE_NUMBER, lcsReferenceNumber);
    }

    return lra;
  }
}
