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
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.impl.app.slg.ProvideLocationRequestImpl;
import org.jdiameter.common.impl.app.slg.LocationReportRequestImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public abstract class AbstractClient extends TBase implements ClientSLgSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ClientSLgSession clientSLgSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777255));
      SLgSessionFactoryImpl sLgSessionFactory = new SLgSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerSLgSession.class, (IAppSessionFactory) sLgSessionFactory);
      sessionFactory.registerAppFacory(ClientSLgSession.class, (IAppSessionFactory) sLgSessionFactory);

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

  // missing protected abstract attributes TODO

  // ----------- helper

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

    // PENDING -  TODO
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

    // PENDING - TODO
    return plr;
  }

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
	                        [ Reporting-Amount ]
	                        [ Periodic-LDR-Information ]
	                        [ ESMLC-Cell-Info ]
	                        [ 1xRTT-RCID ] ]
	                        [ Civic-Address ]
	                        [ Barometric-Pressure ]
	                        *[ Supported-Features ]
	                        *[ AVP ]
	                        *[ Proxy-Info ]
                            *[ Route-Record ]

  */
    // Create ProvideLocationRequest
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

    // PENDING -  TODO
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

    // PENDING - TODO
    return lrr;
  }
}

