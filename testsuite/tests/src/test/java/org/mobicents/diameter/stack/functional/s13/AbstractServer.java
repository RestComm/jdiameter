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
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.api.s13.ServerS13SessionListener;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;
import org.jdiameter.common.impl.app.s13.JMEIdentityCheckAnswerImpl;
import org.jdiameter.common.impl.app.s13.S13SessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 * @author baranowb
 *
 */
public abstract class AbstractServer extends TBase implements ServerS13SessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ServerS13Session serverS13Session;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777252));
      S13SessionFactoryImpl s13SessionFactory = new S13SessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerS13Session.class, s13SessionFactory);
      sessionFactory.registerAppFacory(ClientS13Session.class, s13SessionFactory);
      s13SessionFactory.setServerSessionListener(this);
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

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException,
  OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doMEIdentityCheckRequestEvent(ServerS13Session session, JMEIdentityCheckRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"ECR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  // -------- conf

  public String getSessionId() {
    return this.serverS13Session.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.serverS13Session = stack.getSession(sessionId, ServerS13Session.class);
  }


  public ServerS13Session getSession() {
    return this.serverS13Session;
  }

  protected abstract int getEquipmentStatus();

  // ----------- helper

  public JMEIdentityCheckAnswer createECA(JMEIdentityCheckRequest ecr, long resultCode) throws Exception {
    /*
   <ME-Identity-Check-Answer>::=<Diameter Header:324,PXY,16777252>
          < Session-Id >
          [ Vendor-Specific-Application-Id ]
          [ Result-Code ]
          [ Experimental-Result ]
          { Auth-Session-State }
          { Origin-Host }
          { Origin-Realm }
          [ Equipment-Status ]
     *[ AVP ]
     *[ Failed-AVP ]
     *[ Proxy-Info ]
     *[ Route-Record ]
     */
    JMEIdentityCheckAnswer eca = new JMEIdentityCheckAnswerImpl((Request) ecr.getMessage(), resultCode);

    AvpSet reqSet = ecr.getMessage().getAvps();
    AvpSet set = eca.getMessage().getAvps();
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

    // Equipment-Status
    if (getEquipmentStatus() >= 0) {
      // Avp addAvp(int avpCode, long value, long vendorId, boolean mFlag, boolean pFlag);
      set.addAvp(Avp.EQUIPMENT_STATUS, getEquipmentStatus(), 10415, false, false);
    }
    return eca;
  }
}
