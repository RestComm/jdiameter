/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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

package org.mobicents.diameter.stack.functional.sy.base;

import org.jdiameter.api.*;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.sy.ServerSySession;
import org.jdiameter.api.sy.events.SpendingLimitAnswer;
import org.jdiameter.api.sy.events.SpendingLimitRequest;

import org.jdiameter.api.sy.events.SpendingStatusNotificationAnswer;
import org.jdiameter.api.sy.events.SpendingStatusNotificationRequest;
import org.jdiameter.common.impl.app.auth.SessionTermAnswerImpl;
import org.jdiameter.common.impl.app.sy.SpendingLimitAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.sy.AbstractServer;

/**
 * Policy and charging control, Spending Limit Report - Sy tests
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public class Server extends AbstractServer {

  protected boolean sentINITIAL;
  protected boolean sentINTERMEDIATE;
  protected boolean sentTERMINATE;
  protected boolean receiveINITIAL;
  protected boolean receiveINTERMEDIATE;
  protected boolean receiveTERMINATE;

  protected SpendingLimitRequest request;

  public void sendInitial() throws Exception {
    if (!this.receiveINITIAL || this.request == null) {
      fail("Did not receive INITIAL or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }

    SpendingLimitAnswer answer = new SpendingLimitAnswerImpl((Request) request.getMessage(), 2001);
    /*AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));*/

    super.serverSySession.sendSpendingLimitAnswer(answer);

    sentINITIAL = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  public void sendIntermediate() throws Exception {
    if (!this.receiveINTERMEDIATE || this.request == null) {
      fail("Did not receive INTERIM or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }

    SpendingLimitAnswer answer = new SpendingLimitAnswerImpl((Request) request.getMessage(), 2001);

    /*AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));*/

    super.serverSySession.sendSpendingLimitAnswer(answer);
    sentINTERMEDIATE = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  public void sendTerminate() throws Exception {
    if (!this.receiveTERMINATE || this.request == null) {
      fail("Did not receive TERMINATE or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }

    SessionTermAnswer answer = new SessionTermAnswerImpl((Request) request.getMessage(), 2001);

    /*AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));*/

    super.serverSySession.sendFinalSpendingLimitAnswer(answer);

    sentTERMINATE = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  @Override
  public Answer processRequest(Request request) {
    int commandCode = request.getCommandCode();
    switch(commandCode) {
      case 8388635:
      case 8388636:
      case 275:
        break;
      default:
        fail("Received Request with code not equal 272!. Code[" + request.getCommandCode() + "]", null);
        return null;
    }

    if (super.serverSySession == null) {
      try {
        super.serverSySession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerSySession.class, (Object) null);
        ((NetworkReqListener) this.serverSySession).processRequest(request);
      }
      catch (Exception e) {
        fail(null, e);
      }
    } else {
      // do fail?
      fail("Received Request in base listener, not in app specific!", null);
    }

    return null;
  }

  public boolean isSentINITIAL() {
    return sentINITIAL;
  }

  public boolean isSentINTERMEDIATE() {
    return sentINTERMEDIATE;
  }

  public boolean isSentTERMINATE() {
    return sentTERMINATE;
  }

  public boolean isReceiveINITIAL() {
    return receiveINITIAL;
  }

  public boolean isReceiveINTERMEDIATE() {
    return receiveINTERMEDIATE;
  }

  public boolean isReceiveTERMINATE() {
    return receiveTERMINATE;
  }

  public SpendingLimitRequest getRequest() {
    return request;
  }

  @Override
  public void doSpendingLimitRequest(ServerSySession session, SpendingLimitRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doFinalSpendingLimitRequest(ServerSySession session, SessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }

  @Override
  public void doSpendingStatusNotificationAnswer(ServerSySession session, SpendingStatusNotificationRequest request, SpendingStatusNotificationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

  }
}
