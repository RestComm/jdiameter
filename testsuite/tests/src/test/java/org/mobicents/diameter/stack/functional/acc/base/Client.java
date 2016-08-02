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
package org.mobicents.diameter.stack.functional.acc.base;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.acc.AbstractClient;

/**
 * Base implementation of Client
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Client extends AbstractClient {

  protected boolean sentINITIAL;
  protected boolean sentINTERIM;
  protected boolean sentTERMINATE;
  protected boolean sentEVENT;
  protected boolean receiveINITIAL;
  protected boolean receiveINTERIM;
  protected boolean receiveTERMINATE;
  protected boolean receiveEVENT;

  /**
   *
   */
  public Client() {
    // TODO Auto-generated constructor stub
  }

  public void sendInitial() throws Exception {
    AccountRequest initialRequest = super.createAcc(ACC_REQUEST_TYPE_INITIAL, this.ccRequestNumber, super.clientAccSession);
    this.ccRequestNumber++;
    super.clientAccSession.sendAccountRequest(initialRequest);
    Utils.printMessage(log, super.stack.getDictionary(), initialRequest.getMessage(), true);
    this.sentINITIAL = true;
  }

  public void sendInterim() throws Exception {
    if (!receiveINITIAL) {
      throw new Exception();
    }
    AccountRequest interimRequest = super.createAcc(ACC_REQUEST_TYPE_INTERIM, this.ccRequestNumber, super.clientAccSession);
    this.ccRequestNumber++;
    super.clientAccSession.sendAccountRequest(interimRequest);
    Utils.printMessage(log, super.stack.getDictionary(), interimRequest.getMessage(), true);
    this.sentINTERIM = true;
  }

  public void sendTermination() throws Exception {
    if (!receiveINTERIM) {
      throw new Exception();
    }
    AccountRequest terminateRequest = super.createAcc(ACC_REQUEST_TYPE_TERMINATE, this.ccRequestNumber, super.clientAccSession);
    this.ccRequestNumber++;
    super.clientAccSession.sendAccountRequest(terminateRequest);
    Utils.printMessage(log, super.stack.getDictionary(), terminateRequest.getMessage(), true);
    this.sentTERMINATE = true;
  }

  public void sendEvent() throws Exception {
    AccountRequest eventRequest = super.createAcc(ACC_REQUEST_TYPE_TERMINATE, this.ccRequestNumber, super.clientAccSession);
    this.ccRequestNumber++;
    super.clientAccSession.sendAccountRequest(eventRequest);
    Utils.printMessage(log, super.stack.getDictionary(), eventRequest.getMessage(), true);

    this.sentEVENT = true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.acc.ClientAccSessionListener#doCreditControlAnswer( org.jdiameter.api.acc.ClientAccSession,
   * org.jdiameter.api.acc.events.JCreditControlRequest, org.jdiameter.api.acc.events.JCreditControlAnswer)
   */
  @Override
  public void doAccAnswerEvent(ClientAccSession session, AccountRequest request, AccountAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {
      Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);
      switch (answer.getAccountingRecordType()) {
        case ACC_REQUEST_TYPE_INITIAL:
          if (receiveINITIAL) {
            fail("Received INITIAL more than once!", null);
          }
          receiveINITIAL = true;
          break;

        case ACC_REQUEST_TYPE_INTERIM:
          if (receiveINTERIM) {
            fail("Received INTERIM more than once!", null);
          }
          receiveINTERIM = true;
          break;

        case ACC_REQUEST_TYPE_TERMINATE:
          if (receiveTERMINATE) {
            fail("Received TERMINATE more than once!", null);
          }
          receiveTERMINATE = true;
          break;

        case ACC_REQUEST_TYPE_EVENT:
          if (receiveEVENT) {
            fail("Received EVENT more than once!", null);
          }
          receiveEVENT = true;
          break;

        default:

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.acc.ClientAccSessionListener#doOtherEvent(org.jdiameter .api.app.AppSession,
   * org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  // ------------ getters for some vars;

  public boolean isSentINITIAL() {
    return sentINITIAL;
  }

  public boolean isSentEVENT() {
    return sentEVENT;
  }

  public boolean isReceiveEVENT() {
    return receiveEVENT;
  }

  public boolean isSentINTERIM() {
    return sentINTERIM;
  }

  public boolean isSentTERMINATE() {
    return sentTERMINATE;
  }

  public boolean isReceiveINITIAL() {
    return receiveINITIAL;
  }

  public boolean isReceiveINTERIM() {
    return receiveINTERIM;
  }

  public boolean isReceiveTERMINATE() {
    return receiveTERMINATE;
  }

  // ------------ getters for some vars;

  @Override
  protected int getChargingUnitsTime() {
    return 10;
  }

  @Override
  protected String getServiceContextId() {
    return "tralalalal ID";
  }

}
