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
package org.mobicents.diameter.stack.functional.slh.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.common.impl.app.slh.LCSRoutingInfoRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slh.AbstractClient;

/**
 * Base implementation of Client
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class ClientRIR extends AbstractClient {

  protected boolean sentRoutingInfo;
  protected boolean receiveRoutingInfo;

  /**
	 * 
	 */
  public ClientRIR() {
  }

  public void sendLCSRoutingInfo() throws Exception {
    LCSRoutingInfoRequest request = new LCSRoutingInfoRequestImpl(super.clientSLhSession.getSessions().get(0).createRequest(LCSRoutingInfoRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < User-Data -Request> ::= < Diameter Header: 306, REQ, PXY, 16777217 >
    // < Session-Id >
    AvpSet vendorSpecificApplicationId = avpSet.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
    // 1* [ Vendor-Id ]
    vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
    // 0*1{ Auth-Application-Id }
    vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    // 0*1{ Acct-Application-Id }
    // { Auth-Session-State }
    avpSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    avpSet.addAvp(1, "User", false);

    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    super.clientSLhSession.sendLCSRoutingInfoRequest(request);
    this.sentRoutingInfo = true;
  }

  @Override
	public void doLCSRoutingInfoAnswerEvent(ClientSLhSession session, LCSRoutingInfoRequest request, LCSRoutingInfoAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		receiveRoutingInfo = true;
	}
  

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
      OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  protected String getClientURI() {
    return clientURI;
  }
  
  public boolean isSentRoutingInfo() {
	return sentRoutingInfo;
  }
  
  public boolean isReceiveRoutingInfo() {
	return receiveRoutingInfo;
  }
}
