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
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slh.LCSRoutingInfoAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slh.AbstractServer;

/**
 * Base implementation of Server
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class ServerRIR extends AbstractServer {

  protected boolean sentRoutingInfo;
  protected boolean receiveRoutingInfo;

  protected LCSRoutingInfoRequest lcsRoutingInfoRequest;

  // ------- send methods to trigger answer

  public void sendRoutingInfo() throws Exception {
    if (!this.receiveRoutingInfo || this.lcsRoutingInfoRequest == null) {
      fail("Did not receive Routing Info or answer already sent.", null);
      throw new Exception("Did not receive Routing Info or answer already sent. Request: " + this.lcsRoutingInfoRequest);
    }
    LCSRoutingInfoAnswer answer = new LCSRoutingInfoAnswerImpl((Request) this.lcsRoutingInfoRequest.getMessage(), 2001);

    AvpSet reqSet = lcsRoutingInfoRequest.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.USER_NAME));
    this.serverSLhSession.sendLCSRoutingInfoAnswer(answer);

    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.lcsRoutingInfoRequest = null;
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
    if (code != LCSRoutingInfoRequest.code) {
      fail("Received Request with code not used by SLh!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverSLhSession == null) {
      try {
        super.serverSLhSession = ((ISessionFactory) this.sessionFactory).getNewAppSession(request.getSessionId(), getApplicationId(), ServerSLhSession.class, (Object) null);
        ((NetworkReqListener) this.serverSLhSession).processRequest(request);
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
	public void doLCSRoutingInfoRequestEvent(ServerSLhSession session, LCSRoutingInfoRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	  if (this.receiveRoutingInfo) {
		  fail("Received Routing Info more than once!", null);
	  }
	  this.receiveRoutingInfo = true;
	  this.lcsRoutingInfoRequest = request;
	}

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    fail("Received \"SuccessMessage\" event, request[" + request + "], answer[" + answer + "]", null);
  }

  @Override
  public void timeoutExpired(Request request) {
    fail("Received \"Timoeout\" event, request[" + request + "]", null);
  }
  
  public boolean isReceiveRoutingInfo() {
	return receiveRoutingInfo;
  }
  
  public boolean isSentRoutingInfo() {
	return sentRoutingInfo;
  }
}
