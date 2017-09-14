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

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.common.impl.app.cxdx.JLocationInfoRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.cxdx.AbstractClient;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ClientLIR extends AbstractClient {

  protected boolean receivedLocationInfo;
  protected boolean sentLocationInfo;

  /**
   *
   */
  public ClientLIR() {
  }

  public void sendLocationInfo() throws Exception {
    JLocationInfoRequest request = new JLocationInfoRequestImpl(super.createRequest(this.clientCxDxSession, JLocationInfoRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    reqSet.addAvp(Avp.SERVER_NAME, "ala", getApplicationId().getVendorId(), true, false, false);
    // <Location-Info-Request> ::= < Diameter Header: 302, REQ, PXY,
    // 16777216 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    // { Auth-Session-State }
    // { Origin-Host }
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // [ Originating-Request ]
    // *[ Supported-Features ]
    // { Public-Identity }
    reqSet.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [MSISDN]
    // *[AVP]
    // [ User-Authorization-Type ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    this.clientCxDxSession.sendLocationInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentLocationInfo = true;
  }

  @Override
  public void doLocationInformationAnswer(ClientCxDxSession session, JLocationInfoRequest request, JLocationInfoAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedLocationInfo) {
      fail("Received LIA more than once", null);
      return;
    }

    this.receivedLocationInfo = true;
  }

  public boolean isReceivedLocationInfo() {
    return receivedLocationInfo;
  }

  public boolean isSentLocationInfo() {
    return sentLocationInfo;
  }

}
