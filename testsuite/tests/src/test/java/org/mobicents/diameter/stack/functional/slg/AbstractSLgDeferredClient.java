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
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slg.LocationReportAnswerImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public abstract class AbstractSLgDeferredClient extends TBase implements ClientSLgSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

/*
  3GPP TS 23.172 v14.2.0 - Functional stage 2 description of Location Services (LCS).
  4.4.2	Deferred Location Request
  Request for location contingent on some current or future events where the response from the LCS Server
  to the LCS Client may occur some time after the request was sent.
  4.4.2.1	Types of event (summarized)
    a)	UE available: Any event in which the MSC/SGSN/MME has established a contact with the UE.
    b)	Change of Area: An event where the UE enters or leaves a pre-defined geographical area
        or if the UE is currently within the pre-defined geographical area.
    c)	Periodic Location: An event where a defined periodic timer expires in the UE
        and activates a location report or a location request.
    d)	Motion: An event where the UE moves by more than some predefined linear distance from a previous
        location. The motion event may be reported one time only, or several times.
        The motion event report shall contain an indication of the event occurrence.
        A location estimate may be included in the report if requested by the LCS client.
*/

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

  public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request) throws InternalException, IllegalDiameterStateException,
      RouteException, OverloadException {
    fail("Received \"LRR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  // ----------- 3GPP TS 29.172 v14.1.0 reference ----------- //
/*
  6.3	Subscriber Location Report
  6.3.1	General
  The Subscriber Location Report operation is used by an MME or SGSN to provide the location of a target UE
  to a GMLC, when a request for location has been implicitly issued or when a Delayed Location Reporting
  is triggered after receipt of a request for location for a UE transiently not reachable.
*/
  // Attributes for Location Report Answer (LRA)
  protected abstract byte[] getLCSReferenceNumber();
  protected abstract java.net.InetAddress getGMLCAddress();
  protected abstract int getPrioritizedListIndicator();
  protected abstract byte[] getVisitedPLMNId();
  protected abstract int getPeriodicLocationSupportIndicator();
  protected abstract long getLRAFLags();

  public LocationReportAnswer createLRA(LocationReportRequest lrr, long resultCode) throws Exception {
  /*
  3GPP TS 29.172 v14.1.0 reference
  7.3.4	Location-Report-Answer (LRA) Command
  The Location-Report-Answer (LRA) command, indicated by the Command-Code field set to 8388621 and
  the ‘R’ bit cleared in the Command Flags field, is sent by the GMLC to the MME or SGSN in response
  to the Location-Report-Request command.

  Message Format
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
    AvpSet plmnIdList = reportingPLMNList.addGroupedAvp(Avp.PLMN_ID_LIST, 10415, false, false);
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
