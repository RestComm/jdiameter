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
import org.mobicents.diameter.stack.functional.slh.AbstractSLhServer;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ServerSLh extends AbstractSLhServer {

  protected boolean receivedRIR;
  protected boolean sentRIA;

  protected LCSRoutingInfoRequest request;

  public void sendLCSRoutingInfoAnswer() throws Exception {
    if (!receivedRIR || request == null) {
      fail("Did not receive RIR or answer already sent.", null);
      throw new Exception("Did not receive RIR or answer already sent. Request: " + this.request);
    }

    LCSRoutingInfoAnswer ria = super.createRIA(request, 2001);

    super.serverSLhSession.sendLCSRoutingInfoAnswer(ria);

    this.sentRIA = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), ria.getMessage(), isSentRIA());
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


  //*********************************************************//
  //***************** RIA methods ***************************//
  //*********************************************************//

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
  protected byte[] getLMSI() {
  /*
     3GPP TS 29.173 v13.0.0 section 6.4.2
       The LMSI AVP is of type OctetString and it shall contain the Local Mobile Station Identity (LMSI) allocated by the VLR, as defined in 3GPP TS 23.003
  */
    String lmsiString = "748031234567890";
    byte[] lmsi = lmsiString.getBytes();
    return lmsi;
  }

  @Override
  protected byte[] getSGSNNumber() {
    String sgsnNumString = "59899004501";
    byte[] sgsnNumber = sgsnNumString.getBytes();
    return sgsnNumber;
  }

  @Override
  protected String getSGSNName() {
    String sgsnName = "SGSN01";
    return sgsnName;
  }

  @Override
  protected String getSGSNRealm() {
    String sgsnRealm = "sgsn.restcomm.com";
    return sgsnRealm;
  }

  @Override
  protected String getMMEName() {
  /*
     3GPP TS 29.173 v13.0.0 section 6.4.4
       TThe MME-Name AVP is of type DiameterIdentity and it shall contain the Diameter identity of the serving MME.
  */
    String mmeName = "MME710";
    return mmeName;
  }

  @Override
  protected String getMMERealm() {
  /*
     3GPP TS 29.173 v13.0.0 section 6.4.12
       The MME-Realm AVP is of type DiameterIdentity and it shall contain the Diameter Realm Identity of the serving MME.
  */
    String mmeRealm = "mme.restcomm.com";
    return mmeRealm;
  }

  @Override
  protected byte[] getMSCNumber() {
    String mscNumString = "59899001207";
    byte[] mscNumber = mscNumString.getBytes();
    return mscNumber;
  }

  @Override
  protected String get3GPPAAAServerName() {
    String tgppAAAServerName = "aaa.restcomm.com";
    return tgppAAAServerName;
  }

  @Override
  protected long getLCSCapabilitiesSets() {
    long lcsCapabilitiesSets = 99900123L;
    return lcsCapabilitiesSets;
  }

  @Override
  protected byte[] getAdditionalSGSNNumber() {
    String sgsnNumString = "59899004502";
    byte[] sgsnNumber = sgsnNumString.getBytes();
    return sgsnNumber;
  }

  @Override
  protected String getAdditionalSGSNName() {
    String sgsnName = "SGSN02";
    return sgsnName;
  }

  @Override
  protected String getAdditionalSGSNRealm() {
    String sgsnRealm = "sgsn2.restcomm.com";
    return sgsnRealm;
  }

  @Override
  protected String getAdditionalMMEName() {
    String mmeName = "MME712";
    return mmeName;
  }

  @Override
  protected String getAdditionalMMERealm() {
    String mmeRealm = "mme2.restcomm.com";
    return mmeRealm;
  }

  @Override
  protected byte[] getAdditionalMSCNumber() {
    String mscNumString = "59899001210";
    byte[] mscNumber = mscNumString.getBytes();
    return mscNumber;
  }

  @Override
  protected String getAdditional3GPPAAAServerName() {
    String tgppAAAServerName = "aaa2.restcomm.com";
    return tgppAAAServerName;
  }

  @Override
  protected long getAdditionalLCSCapabilitiesSets() {
    long lcsCapabilitiesSets = 88800123L;
    return lcsCapabilitiesSets;
  }

  @Override
  protected java.net.InetAddress getAdditionalGMLCAddress() {
    try {
      java.net.InetAddress gmlcAddress = java.net.InetAddress.getLocalHost();
      return gmlcAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
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
  protected java.net.InetAddress getPPRAddress() {
  /*
     3GPP TS 29.173 v13.0.0 section 6.4.9
       The PPR-Address AVP is of type Address and contains the IPv4 or IPv6 address of the Privacy Profile Register for the targeted user
  */
    try {
      java.net.InetAddress pprAddress = java.net.InetAddress.getLocalHost();
      return pprAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected long getRIAFLags() {
  /*
  3GPP TS 29.173 v13.0.0 section 6.4.15
    Bit	Event Type                                        Description
    0   Combined-MME/SGSN-Supporting-Optimized-LCS-Proc   This bit, when set, indicates that the UE is served by the MME and the SGSN parts of the same
                                                          combined MME/SGSN and this combined MME/SGSN supports the optimized LCS procedure.
  */
    long riaFlags = 1L;
    return riaFlags;
  }

  public boolean isReceivedRIR() {
    return receivedRIR;
  }

  public boolean isSentRIA() {
    return sentRIA;
  }

}