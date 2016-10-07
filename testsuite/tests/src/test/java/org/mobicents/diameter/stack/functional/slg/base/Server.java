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

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractServer;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public class Server extends AbstractServer {

  protected boolean receivedPLR;
  protected boolean sentPLA;
  protected boolean receivedLRR;
  protected boolean sentLRA;

  protected ProvideLocationRequest plrRequest;
  protected LocationReportRequest lrrRequest;

  public void sendProvideLocationAnswer() throws Exception {
    if (!receivedPLR || plrRequest == null) {
      fail("Did not receive PLR or answer already sent.", null);
      throw new Exception("Did not receive PLR or answer already sent. Request: " + this.plrRequest);
    }

    ProvideLocationAnswer pla = super.createPLA(plrRequest, 2001);

    super.serverSLgSession.sendProvideLocationAnswer(pla);

    this.sentPLA = true;
    plrRequest = null;
    Utils.printMessage(log, super.stack.getDictionary(), pla.getMessage(), true);
  }

	public void sendLocationReportAnswer() throws Exception {
    if (!receivedLRR || lrrRequest == null) {
      fail("Did not receive LRR or answer already sent.", null);
      throw new Exception("Did not receive LRR or answer already sent. Request: " + this.lrrRequest);
    }

    LocationReportAnswer lra = super.createLRA(lrrRequest, 2001);

    super.serverSLgSession.sendLocationReportAnswer(lra);

    this.sentLRA = true;
    lrrRequest = null;
    Utils.printMessage(log, super.stack.getDictionary(), lra.getMessage(), true);
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.TBase#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != ProvideLocationRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (code != LocationReportRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverSLgSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    } else {
      try {

        super.serverSLgSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerSLgSession.class, (Object) null);
        ((NetworkReqListener) this.serverSLgSession).processRequest(request);

      } catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  @Override
  public void doProvideLocationRequestEvent(ServerSLgSession session, ProvideLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedPLR) {
      fail("Received PLR more than once", null);
      return;
    }
    this.receivedPLR = true;
    this.plrRequest = request;
  }

  @Override
  public void doLocationReportRequestEvent(ServerSLgSession session, LocationReportRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedLRR) {
      fail("Received LRR more than once", null);
      return;
    }
    this.receivedLRR = true;
    this.lrrRequest = request;
  }

  public boolean isReceivedPLR() {
    return receivedPLR;
  }

  public boolean isSentPLA() {
    return sentPLA;
  }

  public boolean isReceivedLRR() {
    return receivedLRR;
  }

  public boolean isSentLRA() {
    return sentLRA;
  }
}
