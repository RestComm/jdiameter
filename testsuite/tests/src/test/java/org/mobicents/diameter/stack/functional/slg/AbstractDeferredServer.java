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
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slg.LocationReportAnswerImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public abstract class AbstractDeferredServer extends TBase implements ServerSLgSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ServerSLgSession serverSLgSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777255));
      SLgSessionFactoryImpl slgSessionFactory = new SLgSessionFactoryImpl(this.sessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ServerSLgSession.class, slgSessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ClientSLgSession.class, slgSessionFactory);
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

  // Attributes for Location Report Answer (LRA)
  protected abstract java.net.InetAddress getGMLCAddress();
  protected abstract long getLRAFLags();
  protected abstract int getPrioritizedListIndicator();
  protected abstract byte[] getVisitedPLMNId();
  protected abstract int getPeriodicLocationSupportIndicator();
  protected abstract byte[] getLCSReferenceNumber();

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
      set.addAvp(Avp.LRA_FLAGS, lraFlags, 10415, false, false);
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
      plmnIdList.addAvp(Avp.VISITED_PLMN_ID, visitedPLMNId, 10415, false, false);
    }
    if (periodicLocationSupportIndicator != -1){
      plmnIdList.addAvp(Avp.PERIODIC_LOCATION_SUPPORT_INDICATOR, periodicLocationSupportIndicator, 10415, false, false);
    }
    if (plmnIdList != null){
      reportingPLMNList.addAvp(plmnIdList);
    }
    if (prioritizedListIndicator != -1){
      reportingPLMNList.addAvp(Avp.PRIORITIZED_LIST_INDICATOR, prioritizedListIndicator, 10415, false, false);
    }

    // [ LCS-Reference-Number ]
    byte[] lcsReferenceNumber = getLCSReferenceNumber();
    if (lcsReferenceNumber != null){
      set.addAvp(Avp.LCS_REFERENCE_NUMBER, lcsReferenceNumber, 10415, true, false);
    }

    return lra;
  }
}
