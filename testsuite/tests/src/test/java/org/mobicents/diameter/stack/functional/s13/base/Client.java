/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, TeleStax Inc. and individual contributors
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

package org.mobicents.diameter.stack.functional.s13.base;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s13.AbstractClient;

public class Client extends AbstractClient {

  protected boolean receivedECA;
  protected boolean sentECR;

  public Client() {
  }

  public void sendMEIdentityCheckRequest() throws Exception {
    JMEIdentityCheckRequest ecr = super.createECR(super.clientS13Session);
    super.clientS13Session.sendMEIdentityCheckRequest(ecr);
    Utils.printMessage(log, super.stack.getDictionary(), ecr.getMessage(), true);
    this.sentECR = true;
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.s13.AbstractClient#doMEIdentityCheckAnswerEvent(
   *    org.jdiameter.api.s13.ClientS13Session, org.jdiameter.api.s13.events.JMEIdentityCheckRequest, org.jdiameter.api.s13.events.JMEIdentityCheckAnswer)
   */
  @Override
  public void doMEIdentityCheckAnswerEvent(ClientS13Session session, JMEIdentityCheckRequest request, JMEIdentityCheckAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedECA) {
      fail("Received ECA more than once", null);
      return;
    }
    this.receivedECA = true;
  }

  @Override
  protected String getIMEI() {
    // International Mobile Equipment Identity, as specified in 3GPP TS 23.003
    return "356810040685283";
  }

  @Override
  protected String getTgpp2MEID() {
    return "A1000017F1000D";
  }

  @Override
  protected String getSoftwareVersion() {
    // 2-digit Software Version Number (SVN) of the International Mobile Equipment Identity, as specified in 3GPP TS 23.003
    return "01";
  }

  @Override
  protected String getUserName() {
    // User IMSI, formatted according to 3GPP TS 23.003 [3], clause 2.2.
    return "260021234567890";
  }

  public boolean isReceivedECA() {
    return receivedECA;
  }

  public boolean isSentECR() {
    return sentECR;
  }
}
