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

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slg.LocationReportRequestImpl;
import org.jdiameter.common.impl.app.slg.ProvideLocationAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractServer;

/**
 * Base implementation of Server
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class ServerLRR extends AbstractServer {

  protected boolean sentLocationAnswer;
  protected boolean sentLocationReport;
  protected boolean receiveLocationRequest;
  protected boolean receiveLocationReport;

  protected ProvideLocationRequest provideLocationRequest;

  // ------- send methods to trigger answer

  public void sendLocationAnswer() throws Exception {
    if (!this.receiveLocationRequest || this.provideLocationRequest == null) {
      fail("Did not receive Provide Location request or answer already sent.", null);
      throw new Exception("Did not receive Provide Location request or answer already sent. Request: " + this.provideLocationRequest);
    }
    ProvideLocationAnswer answer = new ProvideLocationAnswerImpl((Request) this.provideLocationRequest.getMessage(), 2001);

    AvpSet reqSet = provideLocationRequest.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(Avp.LOCATION_ESTIMATE, "00100", true);
    this.serverSLgSession.sendProvideLocationAnswer(answer);

    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.provideLocationRequest = null;
  }

  public void sendLocationReport() throws Exception {
    if (super.serverSLgSession == null) {
      super.serverSLgSession = ((ISessionFactory) this.sessionFactory).getNewAppSession(this.sessionFactory.getSessionId("xxTESTxx"), getApplicationId(), ServerSLgSession.class, (Object) null);
    }
    LocationReportRequest request = new LocationReportRequestImpl(super.serverSLgSession.getSessions().get(0)
        .createRequest(LocationReportRequestImpl.code, getApplicationId(), getClientRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    AvpSet vendorSpecificApplicationId = avpSet.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
    vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
    vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getServerURI(), true);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    avpSet.addAvp(Avp.LOCATION_EVENT, 0);

    this.serverSLgSession.sendLocationReportRequest(request);
    this.sentLocationReport = true;
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
  }

  // ------- initial, this will be triggered for first msg.

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != ProvideLocationRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverSLgSession == null) {
      try {
        super.serverSLgSession = ((ISessionFactory) this.sessionFactory).getNewAppSession(request.getSessionId(), getApplicationId(), ServerSLgSession.class, (Object) null);
        ((NetworkReqListener) this.serverSLgSession).processRequest(request);
      }
      catch (Exception e) {
        fail(null, e);
      }
    }
    else {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    return null;
  }

  // ------------- specific, app session listener.

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.cca.ServerRoSessionListener#doOtherEvent(org.jdiameter.api.app.AppSession,
   * org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
      OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }
  
  @Override
	public void doLocationReportAnswerEvent(ServerSLgSession session, LocationReportRequest request, LocationReportAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
	    if (this.receiveLocationReport) {
	        fail("Received Location Report more than once!", null);
	    }
	    this.receiveLocationReport = true;
		
	}
  
  @Override
	public void doProvideLocationRequestEvent(ServerSLgSession session, ProvideLocationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	  if (this.receiveLocationRequest) {
	      fail("Received Provide Location Request more than once!", null);
	    }
	    this.receiveLocationRequest = true;
	    this.provideLocationRequest = request;
	}

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    fail("Received \"SuccessMessage\" event, request[" + request + "], answer[" + answer + "]", null);
  }

  @Override
  public void timeoutExpired(Request request) {
    fail("Received \"Timoeout\" event, request[" + request + "]", null);
  }

  public boolean isSentLocationAnswer() {
    return sentLocationAnswer;
  }

  public boolean isSentLocationReport() {
    return sentLocationReport;
  }

  public boolean isReceiveLocationRequest() {
    return receiveLocationRequest;
  }

  public boolean isReceiveLocationReport() {
    return receiveLocationReport;
  }
}
