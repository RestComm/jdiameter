package org.mobicents.diameter.stack.functional.slg.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.slg.AbstractDeferredServer;

import static sun.jdbc.odbc.JdbcOdbcObject.hexStringToByteArray;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public class ServerDeferredSLg extends AbstractDeferredServer {

  protected boolean receivedLRR;
  protected boolean sentLRA;
  protected boolean receivedPLA;

  protected ClientSLgSession clientSLgSession;
  protected LocationReportRequest locationReportRequest;

  public void sendLocationReportAnswer() throws Exception {
    if (!receivedLRR || locationReportRequest == null) {
      fail("Did not receive LRR or answer already sent.", null);
      throw new Exception("Did not receive LRR or answer already sent. Request: " + this.locationReportRequest);
    }

    LocationReportAnswer lra = super.createLRA(locationReportRequest, 2001);

    this.clientSLgSession.sendLocationReportAnswer(lra);

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
  public void doProvideLocationAnswerEvent(ClientSLgSession session, ProvideLocationRequest request, ProvideLocationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);

    if (this.receivedPLA) {
      fail("Received PLA more than once", null);
      return;
    }
    this.receivedPLA = true;
  }

  // PLA methods

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