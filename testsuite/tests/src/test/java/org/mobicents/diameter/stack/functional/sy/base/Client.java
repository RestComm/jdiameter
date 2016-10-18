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
import org.jdiameter.api.sy.ClientSySession;
import org.jdiameter.api.sy.events.SpendingLimitAnswer;
import org.jdiameter.api.sy.events.SpendingLimitRequest;
import org.jdiameter.api.sy.events.SpendingStatusNotificationAnswer;
import org.jdiameter.api.sy.events.SpendingStatusNotificationRequest;
import org.jdiameter.common.impl.app.auth.SessionTermRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.sy.AbstractClient;

/**
 * Policy and charging control, Spending Limit Report - Sy tests
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public class Client extends AbstractClient {

  protected boolean sentINITIAL;
  protected boolean sentINTERMEDIATE;
  protected boolean sentSTATUSNOTIFICATION;
  protected boolean sentTERMINATE;
  protected boolean receivedINITIAL;
  protected boolean receivedINTERMEDIATE;
  protected boolean receivedSTATUSNOTIFICATION;
  protected boolean receivedTERMINATE;

  public Client() {
  }

  public void sendInitial() throws Exception {
    SpendingLimitRequest initialRequest = super.createSLR(SL_REQUEST_TYPE_INITIAL, this.slRequestNumber, super.clientSySession);
    this.slRequestNumber++;
    super.clientSySession.sendSpendingLimitRequest(initialRequest);
    Utils.printMessage(log, super.stack.getDictionary(), initialRequest.getMessage(), true);

    this.sentINITIAL = true;
  }

  public void sendIntermediate() throws Exception {
    if (!receivedINITIAL) {
      throw new Exception();
    }

    SpendingLimitRequest initialRequest = super.createSLR(SL_REQUEST_TYPE_INTERMEDIATE, this.slRequestNumber, super.clientSySession);
    this.slRequestNumber++;
    super.clientSySession.sendSpendingLimitRequest(initialRequest);
    Utils.printMessage(log, super.stack.getDictionary(), initialRequest.getMessage(), true);

    this.sentINTERMEDIATE = true;
  }

  public void sendTerminate() throws Exception {
    if (!receivedINTERMEDIATE) {
      throw new Exception();
    }

    SessionTermRequest terminateRequest = new SessionTermRequestImpl(null, 0, "realm.com", "realm.com");
    this.slRequestNumber++;
    super.clientSySession.sendFinalSpendingLimitRequest(terminateRequest);
    Utils.printMessage(log, super.stack.getDictionary(), terminateRequest.getMessage(), true);

    this.sentTERMINATE = true;
  }

  public void sendStatusNotification() throws Exception {
    SpendingStatusNotificationAnswer statusNotificationAnswer = super.createSNR(this.slRequestNumber, super.clientSySession);
    this.slRequestNumber++;
    super.clientSySession.sendSpendingStatusNotificationAnswer(null);
    Utils.printMessage(log, super.stack.getDictionary(), statusNotificationAnswer.getMessage(), true);

    this.sentSTATUSNOTIFICATION = true;
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

  public boolean isReceivedINITIAL() {
    return receivedINITIAL;
  }

  public boolean isReceivedINTERMEDIATE() {
    return receivedINTERMEDIATE;
  }

  public boolean isReceivedTERMINATE() {
    return receivedTERMINATE;
  }

  @Override
  public void doSpendingLimitAnswer(ClientSySession session, SpendingLimitRequest request, SpendingLimitAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {
      Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);
      switch (answer.getRequestSLTypeAVPValue()) {
        case SL_REQUEST_TYPE_INITIAL:
          if (receivedINITIAL) {
            fail("Received INITIAL more than once!", null);
          }
          receivedINITIAL = true;
          break;

        case SL_REQUEST_TYPE_INTERMEDIATE:
          if (receivedINTERMEDIATE) {
            fail("Received INTERIM more than once!", null);
          }
          receivedINTERMEDIATE = true;
          break;

        default:
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doFinalSpendingLimitAnswer(ClientSySession session, SessionTermRequest request, SessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);
    if (receivedTERMINATE) {
      fail("Received TERMINATE more than once!", null);
    }
    receivedTERMINATE = true;
  }

  @Override
  public void doSpendingStatusNotificationRequest(ClientSySession session, SpendingStatusNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), false);
    receivedSTATUSNOTIFICATION = true;
  }
}
