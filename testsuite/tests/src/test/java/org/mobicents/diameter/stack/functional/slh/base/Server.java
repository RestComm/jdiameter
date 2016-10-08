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

import java.net.UnknownHostException;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public class Server extends AbstractServer {

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

  @Override
  protected String getUserName(){
    // Information Element IMSI Mapped to AVP User-Name
    String imsi = "748039876543210";
    return imsi;
  }

  @Override
  protected byte[] getMSISDN(){
    String msisdnString = "59899077937";
    byte[] msisdn = msisdnString.getBytes();
    return msisdn;
  }

  @Override
  protected byte[] getLMSI(){
    String lmsiString = "748031234567890";
    byte[] lmsi = lmsiString.getBytes();
    return lmsi;
  }

  @Override
  protected byte[] getAdditionalSGSNNumber(){
    String sgsnNumString = "59899004502";
    byte[] sgsnNumber = sgsnNumString.getBytes();
    return sgsnNumber;
  }

  @Override
  protected String getAdditionalSGSNName(){
    String sgsnName = "SGSN02";
    return sgsnName;
  }

  @Override
  protected String getAdditionalSGSNRealm(){
    String sgsnRealm = "sgsn2.restcomm.com";
    return sgsnRealm;
  }

  @Override
  protected String getAdditionalMMEName(){
    String mmeName = "MME712";
    return mmeName;
  }

  @Override
  protected String getAdditionalMMERealm(){
    String mmeRealm = "mme2.restcomm.com";
    return mmeRealm;
  }

  @Override
  protected byte[] getAdditionalMSCNumber(){
    String mscNumString = "59899001210";
    byte[] mscNumber = mscNumString.getBytes();
    return mscNumber;
  }

  @Override
  protected String getAdditional3GPPAAAServerName(){
    String tgppAAAServerName = "aaa2.restcomm.com";
    return tgppAAAServerName;
  }

  @Override
  protected long getAdditionalLCSCapabilitiesSets(){
    long lcsCapabilitiesSets = 88800123;
    return lcsCapabilitiesSets;
  }

  @Override
  protected java.net.InetAddress getAdditionalGMLCAddress(){
    try {
      java.net.InetAddress gmlcAddress = java.net.InetAddress.getByName("Restcomm-GMLC02");
      return gmlcAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected byte[] getSGSNNumber(){
    String sgsnNumString = "59899004501";
    byte[] sgsnNumber = sgsnNumString.getBytes();
    return sgsnNumber;
  }

  @Override
  protected String getSGSNName(){
    String sgsnName = "SGSN01";
    return sgsnName;
  }

  @Override
  protected String getSGSNRealm(){
    String sgsnRealm = "sgsn.restcomm.com";
    return sgsnRealm;
  }

  @Override
  protected String getMMEName(){
    String mmeName = "MME710";
    return mmeName;
  }

  @Override
  protected String getMMERealm(){
    String mmeRealm = "mme.restcomm.com";
    return mmeRealm;
  }

  @Override
  protected byte[] getMSCNumber(){
    String mscNumString = "59899001207";
    byte[] mscNumber = mscNumString.getBytes();
    return mscNumber;
  }

  @Override
  protected String get3GPPAAAServerName(){
    String tgppAAAServerName = "aaa.restcomm.com";
    return tgppAAAServerName;
  }

  @Override
  protected long getLCSCapabilitiesSets(){
    long lcsCapabilitiesSets = 99900123;
    return lcsCapabilitiesSets;
  }

  @Override
  protected java.net.InetAddress getGMLCAddress(){
    try {
      java.net.InetAddress gmlcAddress = java.net.InetAddress.getByName("Restcomm-GMLC");
      return gmlcAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected java.net.InetAddress getPPRAddress(){
    try {
      java.net.InetAddress pprAddress = java.net.InetAddress.getByName("Restcomm-PPR");
      return pprAddress;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected long getRIAFLags(){
    long riaFlags = 7890123;
    return riaFlags;
  }

  public boolean isReceivedRIR() {
    return receivedRIR;
  }

  public boolean isSentRIA() {
    return sentRIA;
  }

}
