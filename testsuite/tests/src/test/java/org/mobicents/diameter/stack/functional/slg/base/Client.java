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

package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractClient;

public class Client extends AbstractClient {

  protected boolean receivedPLA;
  protected boolean sentPLR;
	protected boolean receivedLRA;
  protected boolean sentLRR;

  public Client() {
  }

  public void sendProvideLocationRequest() throws Exception {
    ProvideLocationRequest plr = super.createPLR(super.clientSLgSession);
    super.clientSLgSession.sendProvideLocationRequest(plr);
    Utils.printMessage(log, super.stack.getDictionary(), plr.getMessage(), true);
    this.sentPLR = true;
  }

	public void sendLocationReportRequest() throws Exception {
    LocationReportRequest lrr = super.createLRR(super.clientSLgSession);
    super.clientSLgSession.sendLocationReportRequest(lrr);
    Utils.printMessage(log, super.stack.getDictionary(), lrr.getMessage(), true);
    this.sentPLR = true;
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.slg.AbstractClient#doProvideLocationAnswerEvent(
   *    org.jdiameter.api.slg.ClientSLgSession, org.jdiameter.api.slg.events.ProvideLocationRequest, org.jdiameter.api.slg.events.ProvideLocationAnswer)
   */
  @Override
  public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedPLA) {
      fail("Received PLA more than once", null);
      return;
    }
    this.receivedPLA = true;
  }

	@Override
  public void doLocationReportAnswerEvent(ClientSLgSession session, LocationReportRequest request, LocationReportAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedPLA) {
      fail("Received PLA more than once", null);
      return;
    }
    this.receivedECA = true;
  }
	//TODO pending methods

  public boolean isReceivedPLA() {
    return receivedPLA;
  }

  public boolean isSentPLR() {
    return sentPLR;
  }

	public boolean isReceivedLRA() {
	  return receivedLRA;
  }

  public boolean isSentLRR() {
    return sentLRR;
  }
}
