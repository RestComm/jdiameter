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
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.jdiameter.common.impl.app.cxdx.JRegistrationTerminationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.cxdx.AbstractServer;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerRTR extends AbstractServer {

  protected boolean receivedRegistrationTermination;
  protected boolean sentRegistrationTermination;

  /**
   *
   */
  public ServerRTR() {
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

  public void sendRegistrationTermination() throws Exception {

    JRegistrationTerminationRequest request =
        new JRegistrationTerminationRequestImpl(super.createRequest(this.serverCxDxSession, JRegistrationTerminationRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    // <Registration-Termination-Request> ::= < Diameter Header: 304, REQ, PXY, 16777216 >
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
    // [ Associated-Identities ]
    // *[ Supported-Features ]
    // *[ Public-Identity ]
    // { Deregistration-Reason }
    AvpSet deregeReason = reqSet.addGroupedAvp(Avp.DEREGISTRATION_REASON, getApplicationId().getVendorId(), true, false);
    deregeReason.addAvp(Avp.REASON_CODE, 0, getApplicationId().getVendorId(), true, false, true);
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    this.serverCxDxSession.sendRegistrationTerminationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);

    this.sentRegistrationTermination = true;
  }

  @Override
  public void doRegistrationTerminationAnswer(ServerCxDxSession session, JRegistrationTerminationRequest request, JRegistrationTerminationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedRegistrationTermination) {
      fail("Received RTA more than once", null);
      return;
    }

    this.receivedRegistrationTermination = true;
  }

  public boolean isReceivedRegistrationTermination() {
    return receivedRegistrationTermination;
  }

  public boolean isSentRegistrationTermination() {
    return sentRegistrationTermination;
  }

}
