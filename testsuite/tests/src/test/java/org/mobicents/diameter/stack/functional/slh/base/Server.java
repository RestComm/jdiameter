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

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slh.AbstractServer;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public class Server extends AbstractServer {

  protected boolean receivedRIR;
  protected boolean sentRIA;

  protected LCSRoutingInfoRequest request;

  public void sendLCSRoutingInfoCheckAnswer() throws Exception {
    if (!receivedRIR || request == null) {
      fail("Did not receive RIR or answer already sent.", null);
      throw new Exception("Did not receive RIR or answer already sent. Request: " + this.request);
    }

    LCSRoutingInfoAnswer ria = super.createRIA(request, 2001);

    super.serverSLhSession.sendLCSRoutingInfoCheckAnswer(ria);

    this.sentRIA = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), ria.getMessage(), true);
  }


  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.TBase#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != LCSRoutingInfoRequest.code) {
      fail("Received Request with code not used by SLh!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverSLhSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    } else {
      try {

        super.serverSLhSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerSLhSession.class, (Object) null);
        ((NetworkReqListener) this.serverSLhSession).processRequest(request);

      } catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }
  @Override
  public void doLCSRoutingInfoRequestEvent(ServerSLhSession session, LCSRoutingInfoRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedRIR) {
      fail("Received RIR more than once", null);
      return;
    }
    this.receivedRIR = true;
    this.request = request;
  }


  public boolean isReceivedRIR() {
    return receivedRIR;
  }

  public boolean isSentRIA() {
    return sentRIA;
  }

}
