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

package org.mobicents.diameter.stack.functional.s13;

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
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ClientS13SessionListener;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;
import org.jdiameter.common.impl.app.s13.JMEIdentityCheckRequestImpl;
import org.jdiameter.common.impl.app.s13.S13SessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 * @author baranowb
 *
 */
public abstract class AbstractClient extends TBase implements ClientS13SessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ClientS13Session clientS13Session;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777252));
      S13SessionFactoryImpl s13SessionFactory = new S13SessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerS13Session.class, s13SessionFactory);
      sessionFactory.registerAppFacory(ClientS13Session.class, s13SessionFactory);

      s13SessionFactory.setClientSessionListener(this);

      this.clientS13Session = this.sessionFactory.getNewAppSession(this.sessionFactory.getSessionId("xx-S13-TESTxx"), getApplicationId(),
          ClientS13Session.class, null);
    }
    finally {
      try {
        configStream.close();
      }
      catch (Exception e) {
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

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doMEIdentityCheckAnswerEvent(ClientS13Session session, JMEIdentityCheckRequest request, JMEIdentityCheckAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"ECA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  // ----------- conf parts

  public String getSessionId() {
    return this.clientS13Session.getSessionId();
  }

  public ClientS13Session getSession() {
    return this.clientS13Session;
  }

  protected abstract String getIMEI();
  protected abstract String getTgpp2MEID();
  protected abstract String getSoftwareVersion();
  protected abstract String getUserName();

  // ----------- helper

  protected JMEIdentityCheckRequest createECR(ClientS13Session s13Session) throws Exception {
    /*
     < ME-Identity-Check-Request > ::= < Diameter Header: 324, REQ, PXY, 16777252 >
      < Session-Id >
      [ Vendor-Specific-Application-Id ]
      { Auth-Session-State }
      { Origin-Host }
      { Origin-Realm }
      [ Destination-Host ]
      { Destination-Realm }
      { Terminal-Information }
      [ User-Name ]
     *[ AVP ]
     *[ Proxy-Info ]
     *[ Route-Record ]
     */
    // Create ME-Identity-Check-Request
    JMEIdentityCheckRequest ecr =
        new JMEIdentityCheckRequestImpl(s13Session.getSessions().get(0).createRequest(JMEIdentityCheckRequest.code, getApplicationId(), getServerRealmName()));
    // <ME-Identity-Check-Request>::=<Diameter Header:324,REQ,PXY,16777252>

    // AVPs present by default: Origin-Host, Origin-Realm, Session-Id,
    // Destination-Realm

    AvpSet reqSet = ecr.getMessage().getAvps();

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

    // Terminal Information ::= <AVP header: 1401 10415>
    // [IMEI]
    // [3GPP2-MEID]
    // [Software-Version]
    AvpSet terminalInfo = reqSet.addGroupedAvp(Avp.TERMINAL_INFORMATION, 10415, true, false);

    String imei = getIMEI();
    String tgpp2MEID = getTgpp2MEID();
    String softwareVersion = getSoftwareVersion();
    if (imei != null) {
      terminalInfo.addAvp(Avp.TGPP_IMEI, imei, 10415, false, false, false);
    }
    if (tgpp2MEID != null) {
      terminalInfo.addAvp(Avp.TGPP2_MEID, tgpp2MEID, 10415, false, false, true);
      // Avp addAvp(int avpCode, String value, long vendorId, boolean mFlag, boolean pFlag, boolean asOctetString);
    }
    if (softwareVersion != null) {
      terminalInfo.addAvp(Avp.SOFTWARE_VERSION, softwareVersion, 10415, false, false, false);
    }

    // [ User-Name ]
    String userName = getUserName();
    if (userName != null) {
      reqSet.addAvp(Avp.USER_NAME, userName, false);
    }
    return ecr;
  }

}
