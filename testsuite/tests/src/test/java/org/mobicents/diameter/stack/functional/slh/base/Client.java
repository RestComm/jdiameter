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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   JBoss, Home of Professional Open Source
 *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
 *   by the @authors tag. See the copyright.txt in the distribution for a
 *   full listing of individual contributors.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */
public class Client extends AbstractClient {

  protected boolean receivedRIA;
  protected boolean sentRIR;

  public Client() {
  }

  public void sendLCSRoutingInfoRequest() throws Exception {
    LCSRoutingInfoRequest rir = super.createRIR(super.clientSLhSession);
    super.clientSLhSession.sendLCSRoutingInfoRequest(rir);
    Utils.printMessage(log, super.stack.getDictionary(), rir.getMessage(), true);
    this.sentRIR = true;
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.slh.AbstractImmediateClient#doLCSRoutingInfoAnswerEvent(
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

  @Override
  protected String getUserName() {
    // Information Element IMSI Mapped to AVP User-Name
    String imsi = "748039876543210";
    return imsi;
  }

  @Override
  protected byte[] getMSISDN() {
    String msisdnString = "59899077937";
    byte[] msisdn = msisdnString.getBytes();
    return msisdn;
  }

  @Override
  protected byte[] getGMLCNumber() {
    String gmlcNumberString = "759834279";
    byte[] gmlcNumber = gmlcNumberString.getBytes();
    return gmlcNumber;
  }

  public boolean isReceivedRIA() {
    return receivedRIA;
  }

  public boolean isSentRIR() {
    return sentRIR;
  }
}
