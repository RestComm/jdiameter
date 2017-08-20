/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractSLgDeferredClient;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ClientSLgLRA extends AbstractSLgDeferredClient {

  protected boolean receivedLRR;
  protected boolean sentLRA;

  protected LocationReportRequest locationReportRequest;

  public ClientSLgLRA() {
  }

  public void sendLocationReportAnswer() throws Exception {
    if (!receivedLRR || locationReportRequest == null) {
      fail("Did not receive LRR or answer already sent.", null);
      throw new Exception("Did not receive LRR or answer already sent. Request: " + this.locationReportRequest);
    }

    LocationReportAnswer lra = super.createLRA(locationReportRequest, 2001);

    this.clientSLgSession.sendLocationReportAnswer(lra);

    this.sentLRA = true;
    locationReportRequest = null;
    Utils.printMessage(log, super.stack.getDictionary(), lra.getMessage(), isSentLRA());
  }

  @Override
  public void doLocationReportRequestEvent(ClientSLgSession session, LocationReportRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedLRR) {
      fail("Received LRR more than once", null);
      return;
    }
    this.receivedLRR = true;
    this.locationReportRequest = request;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != LocationReportRequest.code) {
      fail("Received Request with code not used by SLg!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientSLgSession.getSessionId().equals(request.getSessionId())) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    } else {
      super.clientSLgSession.release();
      try {
        super.clientSLgSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ClientSLgSession.class, (Object) null);
        ((NetworkReqListener) this.clientSLgSession).processRequest(request);
      } catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  // this doesn't belong here, but needed to be declared due to class inheritance
  @Override
  public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
  }

  //*********************************************************//
  //***************** LRA methods ***************************//
  //*********************************************************//

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
    String vPlmnIdList = "471800";
    byte[] visitedPlmnIdList = vPlmnIdList.getBytes();
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
