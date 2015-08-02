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
package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.common.impl.app.slg.ProvideLocationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractClient;

/**
 * Base implementation of Client
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class ClientPLR extends AbstractClient {

  protected boolean sentLocationRequest;
  protected boolean sentLocationReport;
  protected boolean receiveLocationAnswer;
  protected boolean receiveLocationReport;

  /**
	 * 
	 */
  public ClientPLR() {
  }

  public void sendLocationRequest() throws Exception {
    ProvideLocationRequest request = new ProvideLocationRequestImpl(super.clientSlgSession.getSessions().get(0).createRequest(ProvideLocationRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    AvpSet vendorSpecificApplicationId = avpSet.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
    vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
    vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    avpSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    avpSet.addAvp(Avp.SLG_LOCATION_TYPE,0);
    AvpSet lcsEpsClientName = avpSet.addGroupedAvp(Avp.LCS_EPS_CLIENT_NAME);
    lcsEpsClientName.addAvp(Avp.LCS_NAME_STRING, "LCS", false);
    lcsEpsClientName.addAvp(Avp.LCS_FORMAT_INDICATOR,0);
    avpSet.addAvp(Avp.LCS_CLIENT_TYPE,0);

    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    super.clientSlgSession.sendProvideLocationRequest(request);
    this.sentLocationRequest = true;
  }

  // ------------ event handlers;
  
  @Override
	public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	  receiveLocationReport = true;
	  fail("Received \"LRR\" event, request[" + request + "], on session[" + session + "]", null);
	}
  
  @Override
	public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
	  receiveLocationAnswer = true;
	}

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
      OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  protected String getClientURI() {
    return clientURI;
  }

  public boolean isSentLocationRequest() {
    return sentLocationRequest;
  }

  public boolean isSentLocationReport() {
    return sentLocationReport;
  }

  public boolean isReceiveLocationAnswer() {
    return receiveLocationAnswer;
  }

  public boolean isReceiveLocationReport() {
    return receiveLocationReport;
  }
}
