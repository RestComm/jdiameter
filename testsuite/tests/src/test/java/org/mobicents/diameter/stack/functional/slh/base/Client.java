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

package org.mobicents.diameter.stack.functional.slh.base;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slh.AbstractClient;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public class Client extends AbstractClient {

  protected boolean receivedRIA;
  protected boolean sentRIR;

  public Client() {
  }

  public void sendLCSRoutingInfoRequest() throws Exception {
    LCSRoutingInfoRequest rir = super.createLRR(super.clientSLhSession);
    super.clientSLhSession.sendLCSRoutingInfoRequest(rir);
    Utils.printMessage(log, super.stack.getDictionary(), rir.getMessage(), true);
    this.sentRIR = true;
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.slh.AbstractClient#doLCSRoutingInfoAnswerEvent(
   *    org.jdiameter.api.slh.ClientSLhSession, org.jdiameter.api.slh.events.LCSRoutingInfoRequest, org.jdiameter.api.slh.events.LCSRoutingInfoAnswer)
   */
  @Override
  public void doLCSRoutingInfoAnswerEvent(ClientSLhSession session, LCSRoutingInfoRequest request, LCSRoutingInfoAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedRIA) {
      fail("Received RIA more than once", null);
      return;
    }
    this.receivedRIA = true;
  }

	//TODO pending methods

  public boolean isReceivedRIA() {
    return receivedRIA;
  }

  public boolean isSentRIR() {
    return sentRIR;
  }
}
