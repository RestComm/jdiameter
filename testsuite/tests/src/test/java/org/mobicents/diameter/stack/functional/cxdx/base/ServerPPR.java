/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional.cxdx.base;

import java.io.InputStream;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.jdiameter.common.impl.app.cxdx.JPushProfileRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.cxdx.AbstractServer;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerPPR extends AbstractServer {

  protected boolean receivedPushProfile;
  protected boolean sentPushProfile;

  /**
   *
   */
  public ServerPPR() {
  }

  @Override
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777216));
      CxDxSessionFactoryImpl cxDxSessionFactory = new CxDxSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerCxDxSession.class, cxDxSessionFactory);
      sessionFactory.registerAppFacory(ClientCxDxSession.class, cxDxSessionFactory);
      cxDxSessionFactory.setServerSessionListener(this);
      this.serverCxDxSession = sessionFactory.getNewAppSession(getApplicationId(), ServerCxDxSession.class);
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

  public void sendPushProfile() throws Exception {

    JPushProfileRequest request = new JPushProfileRequestImpl(super.createRequest(this.serverCxDxSession, JPushProfileRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    // < Push-Profile-Request > ::= < Diameter Header: 305, REQ, PXY, 16777216 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    // { Auth-Session-State }
    // { Origin-Host }
    // { Origin-Realm }
    // { Destination-Host }
    reqSet.addAvp(Avp.DESTINATION_HOST, clientHost, true);
    // { Destination-Realm }
    // { User-Name }
    reqSet.addAvp(Avp.USER_NAME, "ala", false);
    // *[ Supported-Features ]
    // [ User-Data ]
    // [ Charging-Information ]
    // [ SIP-Auth-Data-Item ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    this.serverCxDxSession.sendPushProfileRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);

    this.sentPushProfile = true;
  }

  @Override
  public void doPushProfileAnswer(ServerCxDxSession session, JPushProfileRequest request, JPushProfileAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedPushProfile) {
      fail("Received PPA more than once", null);
      return;
    }

    this.receivedPushProfile = true;
  }

  public boolean isReceivedPushProfile() {
    return receivedPushProfile;
  }

  public boolean isSentPushProfile() {
    return sentPushProfile;
  }

}
