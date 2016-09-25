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

import java.io.InputStream;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
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
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slg.LocationReportAnswerImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractClient;

/**
 * Base implementation of Client
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class ClientLRR extends AbstractClient {

  protected boolean sentLocationRequest;
  protected boolean sentLocationReport;
  protected boolean receiveLocationAnswer;
  protected boolean receiveLocationReport;

  protected LocationReportRequest request;

  /**
	 * 
	 */
  public ClientLRR() {
  }

  // override init, so we dont create session
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777255));
      SLgSessionFactoryImpl slgSessionFactory = new SLgSessionFactoryImpl(this.sessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ServerSLgSession.class, slgSessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ClientSLgSession.class, slgSessionFactory);
      slgSessionFactory.setClientSLgSessionListener(this);
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

  public void sendLocationReport() throws Exception {
    if (!this.receiveLocationReport || this.request == null) {
      fail("Did not receive Location Report Request or answer already sent.", null);
      throw new Exception("Did not receive Location Report Request or answer already sent. Request: " + this.request);
    }
    LocationReportAnswer answer = new LocationReportAnswerImpl((Request) request.getMessage(), 2001);

    //AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(Avp.LRA_FLAGS,0);

    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }

    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    super.clientSlgSession.sendLocationReportAnswer(answer);
    this.sentLocationReport = true;
  }

  // ------------ event handlers;
  @Override
	public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	    receiveLocationReport = true;
	    this.request = request;
	}
  
  @Override
	public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
	    receiveLocationAnswer = true;
	    fail("Received \"PLR\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);

	}

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
      OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != LocationReportRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientSlgSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        super.clientSlgSession = ((ISessionFactory) this.sessionFactory).getNewAppSession(request.getSessionId(), getApplicationId(), ClientSLgSession.class, (Object) null);
        ((NetworkReqListener) this.clientSlgSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
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
