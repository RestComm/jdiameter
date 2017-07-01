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

package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractDeferredServer;

import static sun.jdbc.odbc.JdbcOdbcObject.hexStringToByteArray;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ServerLRA extends AbstractDeferredServer {

  protected boolean receivedLRR;
  protected boolean sentLRA;
  protected boolean receivedPLR;

  protected LocationReportRequest locationReportRequest;
  protected ProvideLocationRequest provideLocationRequest;

  public void sendLocationReportAnswer() throws Exception {
    if (!receivedLRR || locationReportRequest == null) {
      fail("Did not receive LRR or answer already sent.", null);
      throw new Exception("Did not receive LRR or answer already sent. Request: " + this.locationReportRequest);
    }

    LocationReportAnswer lra = super.createLRA(locationReportRequest, 2001);

    super.serverSLgSession.sendLocationReportAnswer(lra);

    this.sentLRA = true;
    locationReportRequest = null;
    Utils.printMessage(log, super.stack.getDictionary(), lra.getMessage(), true);
  }

  /* (non-Javadoc)
   * @see org.mobicents.diameter.stack.functional.TBase#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
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
  public void doLocationReportRequestEvent(ServerSLgSession session, LocationReportRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedLRR) {
      fail("Received LRR more than once", null);
      return;
    }
    this.receivedLRR = true;
    this.locationReportRequest = request;
  }

  @Override
  public void doProvideLocationRequestEvent(ServerSLgSession session, ProvideLocationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedPLR) {
      fail("Received PLR more than once", null);
      return;
    }
    this.receivedPLR = true;
    this.provideLocationRequest = request;
  }

  @Override
  protected java.net.InetAddress getGMLCAddress() {
  /*
    3GPP TS 29.173 v13.0.0 section 6.4.7
      The GMLC-Address AVP is of type Address and shall contain the IPv4 or IPv6 address of H-GMLC or the V-GMLC associated with the serving node.
  */
    try {
      java.net.InetAddress gmlcAddress = java.net.InetAddress.getLocalHost();
      return gmlcAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected long getLRAFLags() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.56
    Bit	Event Type                    Description
    0   MO-LR-ShortCircuit-Indicator  This bit, when set, indicates that the MO-LR short circuit feature is used for obtaining location estimate.
                                      This bit is applicable only when the message is sent over Lgd interface.
  */
    long lraFlags = 0;
    return lraFlags;
  }

  @Override
  protected int getPrioritizedListIndicator() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.51
    The Prioritized-List-Indicator AVP is of type Enumerated and it indicates if the PLMN-ID-List is provided in prioritized order or not.
      NOT_PRIORITIZED  (0)
      PRIORITIZED (1)
  */
    int prioritizedListIndicator = 0;
    return prioritizedListIndicator;
  }

  @Override
  protected byte[] getVisitedPLMNId() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.49
    The PLMN-ID-List AVP is of type Grouped.
      AVP format:
      PLMN-ID-List ::= <AVP header: 2544 10415>
        { Visited-PLMN-Id }
        [ Periodic-Location-Support-Indicator ]
        *[ AVP ]
    If not included, the default value of Periodic-Location-Support-Indicator shall be considered as "NOT_SUPPORTED" (0).
  */
    byte[] visitedPlmnIdList = hexStringToByteArray("471800");
    return visitedPlmnIdList;
  }

  @Override
  protected int getPeriodicLocationSupportIndicator() {
  /*
  3GPP TS 29.172 v13.0.0 section 7.4.50
    The Periodic-Location-Support-Indicator AVP is of type Enumerated and it indicates if the given PLMN-ID (indicated by Visited-PLMN-Id)
    supports periodic location or not.
      NOT_SUPPORTED (0)
      SUPPORTED (1)
  */
    int periodicLocationSupportIndicator = 1;
    return periodicLocationSupportIndicator;
  }

  @Override
  protected byte[] getLCSReferenceNumber() {
  /*
    3GPP TS 29.172 v13.0.0 section 7.4.37
      The LCS-Reference-Number AVP is of type OctetString of length 1. It shall contain the reference number identifying the deferred location request.
  */
    String lcsRefNumber = "4C4353353739";
    byte[] lcsRefNum = lcsRefNumber.getBytes();
    return lcsRefNum;
  }

  public boolean isReceivedLRR() {
    return receivedLRR;
  }

  public boolean isSentLRA() {
    return sentLRA;
  }

}

