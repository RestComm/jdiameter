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

package org.mobicents.diameter.stack.functional.slh;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slh.ServerSLhSessionListener;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.slh.LCSRoutingInfoAnswerImpl;
import org.jdiameter.common.impl.app.slh.SLhSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public abstract class AbstractSLhServer extends TBase implements ServerSLhSessionListener {

  // NOTE: implementing NetworkReqListener since its required for stack to
  // know we support it... ech.

  protected ServerSLhSession serverSLhSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777291));
      SLhSessionFactoryImpl slhSessionFactory = new SLhSessionFactoryImpl(this.sessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ServerSLhSession.class, slhSessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ClientSLhSession.class, slhSessionFactory);
      slhSessionFactory.setServerSessionListener(this);
    }
    finally {
      try {
        configStream.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  // ----------- delegate methods so

  public void start() throws IllegalDiameterStateException, InternalException {
    stack.start();
  }

  public void start(Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    stack.start(mode, timeOut, timeUnit);
  }

  public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
    stack.stop(timeOut, timeUnit, disconnectCause);
  }

  public void stop(int disconnectCause) {
    stack.stop(disconnectCause);
  }

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException,
    RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  public void doLCSRoutingInfoRequestEvent(ServerSLhSession session, LCSRoutingInfoRequest request) throws InternalException, IllegalDiameterStateException,
    RouteException, OverloadException {
    fail("Received \"RIR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  // -------- conf

  public String getSessionId() {
    return this.serverSLhSession.getSessionId();
  }

  public void fetchSession(String sessionId) throws InternalException {
    this.serverSLhSession = stack.getSession(sessionId, ServerSLhSession.class);
  }

  public ServerSLhSession getSession() {
    return this.serverSLhSession;
  }

// ----------- 3GPP TS 29.173 v14.0.0 reference ----------- //
/*
  5.2.1	Send Routing Information for LCS
  5.2.1.1	General
  This procedure is used between the GMLC and the HSS.  The procedure is invoked by the GMLC and is used:
    -	To retrieve routing information for LCS for a specified user from the HSS.

  5.2.1.2	Detailed Behaviour of the HSS
  Upon reception of the Send Routing Info for LCS request, the HSS shall, in the following order:
  1.	Check whether the requesting GMLC belongs to a network authorized to request UE location information.
        If not, Experimental-Result shall be set to DIAMETER_ERROR_UNAUTHORIZED_REQUESTING_NETWORK in the
        Send Routing Information for LCS Response.
  2.	Check that the User Identity for whom data is asked exists in HSS. If not, Experimental-Result
        shall be set to DIAMETER_ERROR_USER_UNKNOWN in the Send Routing Information for LCS Response.
    2a.	If both IMSI and MSISDN are present in the request, check whether they identify the same User.
        If not, the HSS Result-Code shall be set to DIAMETER_CONTRADICTING_AVPS in the
        Send Routing Information for LCS Response.
  3.	Check that there is at least one serving node associated with the targeted user.
        If not, Experimental-Result shall be set to DIAMETER_ERROR_ABSENT_USER in the
        Send Routing Information for LCS Response.
*/

  protected abstract String getUserName();
  protected abstract byte[] getMSISDN();
  protected abstract byte[] getLMSI();
  protected abstract byte[] getSGSNNumber();
  protected abstract String getSGSNName();
  protected abstract String getSGSNRealm();
  protected abstract String getMMEName();
  protected abstract String getMMERealm();
  protected abstract byte[] getMSCNumber();
  protected abstract String get3GPPAAAServerName();
  protected abstract long getLCSCapabilitiesSets();
  protected abstract java.net.InetAddress getGMLCAddress();
  protected abstract byte[] getAdditionalSGSNNumber();
  protected abstract String getAdditionalSGSNName();
  protected abstract String getAdditionalSGSNRealm();
  protected abstract String getAdditionalMMEName();
  protected abstract String getAdditionalMMERealm();
  protected abstract byte[] getAdditionalMSCNumber();
  protected abstract String getAdditional3GPPAAAServerName();
  protected abstract long getAdditionalLCSCapabilitiesSets();
  protected abstract java.net.InetAddress getAdditionalGMLCAddress();
  protected abstract java.net.InetAddress getPPRAddress();
  protected abstract long getRIAFLags();

  public LCSRoutingInfoAnswer createRIA(LCSRoutingInfoRequest rir, long resultCode) throws Exception {

/*
  3GPP TS 29.173 v14.0.0 reference
  6.2.4	LCS-Routing-Info-Answer (RIA) Command
  The LCS-Routing-Info-Answer (RIA) command, indicated by the Command-Code field set to 8388622 and the 'R' bit cleared in the Command Flags field, is sent from HSS to GMLC.
  Message Format:

  < LCS-Routing-Info-Answer> ::=	< Diameter Header: 8388622, PXY, 16777291 >

    < Session-Id >
	[ Vendor-Specific-Application-Id ]
	[ Result-Code ]
	[ Experimental-Result ]
	{ Auth-Session-State }
	{ Origin-Host }
	{ Origin-Realm }
	*[ Supported-Features ]
	[ User-Name ]
	[ MSISDN ]
	[ LMSI ]
	[ Serving-Node ]
	*[ Additional-Serving-Node ]
	[ GMLC-Address ]
	[ PPR-Address ]
	[ RIA-Flags ]
	*[ AVP ]
	*[ Failed-AVP ]
	*[ Proxy-Info ]
	*[ Route-Record ]

 */
    LCSRoutingInfoAnswer ria = new LCSRoutingInfoAnswerImpl((Request) rir.getMessage(), resultCode);

    AvpSet reqSet = rir.getMessage().getAvps();
    AvpSet set = ria.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    // { Vendor-Specific-Application-Id }
    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }
    // [ Result-Code ]
    // [ Experimental-Result ]
    // { Auth-Session-State }
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }

    // [ User-Name ]
    String userName = getUserName();
    if (userName != null) {
      set.addAvp(Avp.USER_NAME, userName, 10415, true, false, false);
    }

    // [ MSISDN ]
    byte[] msisdn = getMSISDN();
    if (msisdn != null){
      set.addAvp(Avp.MSISDN, msisdn, 10415, true, false);
    }

    // [ LMSI ]
    byte[] lmsi = getLMSI();
    if (lmsi != null){
      set.addAvp(Avp.LMSI, lmsi, 10415, true, false);
    }

/*
    Serving-Node ::= <AVP header: 2401 10415>
    [ SGSN-Number ]
    [ SGSN-Name ]
    [ SGSN-Realm ]
    [ MME-Name ]
    [ MME-Realm ]
    [ MSC-Number ]
    [ 3GPP-AAA-Server-Name ]
    [ LCS-Capabilities-Sets ]
    [ GMLC-Address ]
    *[AVP]

*/
    AvpSet servingNode = set.addGroupedAvp(Avp.SERVING_NODE, 10415, true, false);
    byte[] sgsnNumber = getSGSNNumber();
    String sgsnName= getSGSNName();
    String sgsnRealm = getSGSNRealm();
    String mmeName = getMMEName();
    String mmeRealm = getMMERealm();
    byte[] mscNumber = getMSCNumber();
    String tgppAAAServerName= get3GPPAAAServerName();
    long lcsCapabilitiesSet = getLCSCapabilitiesSets();
    java.net.InetAddress gmlcAddress = getGMLCAddress();

    if (sgsnNumber != null){
      servingNode.addAvp(Avp.SGSN_NUMBER, sgsnNumber, 10415, false, false);
    }
    if (sgsnName != null){
      servingNode.addAvp(Avp.SGSN_NAME, sgsnName, 10415, false, false, false);
    }
    if (sgsnRealm != null){
      servingNode.addAvp(Avp.SGSN_REALM, sgsnRealm, 10415, false, false, false);
    }
    if (mmeName != null){
      servingNode.addAvp(Avp.MME_NAME, mmeName, 10415, false, false, false);
    }
    if (mmeRealm != null){
      servingNode.addAvp(Avp.MME_REALM, mmeRealm, 10415, false, false, false);
    }
    if (mscNumber != null){
      servingNode.addAvp(Avp.MSC_NUMBER, mscNumber, 10415, false, false);
    }
    if (tgppAAAServerName != null){
      servingNode.addAvp(Avp.TGPP_AAA_SERVER_NAME, tgppAAAServerName, 10415, false, false, false);
    }
    if (lcsCapabilitiesSet != -1){
      servingNode.addAvp(Avp.LCS_CAPABILITIES_SETS, lcsCapabilitiesSet, 10415, false, false, true);
    }
    if (gmlcAddress != null){
      servingNode.addAvp(Avp.GMLC_ADDRESS, gmlcAddress, 10415, false, false);
    }

/*
    Additional-Serving-Node ::=	<AVP header: 2406 10415>
    [ SGSN-Number ]
    [ MME-Name ]
    [ SGSN-Name ]
    [ SGSN-Realm ]
    [ MME-Realm ]
    [ MSC-Number ]
    [ 3GPP-AAA-Server-Name ]
    [ LCS-Capabilities-Sets ]
    [ GMLC-Address ]
    *[AVP]
*/
    AvpSet additionalServingNode = set.addGroupedAvp(Avp.ADDITIONAL_SERVING_NODE, 10415, true, false);
    byte[] additionalSGSNNumber = getAdditionalSGSNNumber();
    String additionalSGSNName = getAdditionalSGSNName();
    String additionalSGSNRealm = getAdditionalSGSNRealm();
    String additionalMMEName = getAdditionalMMEName();
    String additionalMMERealm = getAdditionalMMERealm();
    byte[] additionalMSCNumber = getAdditionalMSCNumber();
    String additional3GPPAAAServerName = getAdditional3GPPAAAServerName();
    long additionalLCSCapabilitiesSets = getAdditionalLCSCapabilitiesSets();
    java.net.InetAddress additionalGMLCAddress = getAdditionalGMLCAddress();

    if (additionalSGSNNumber != null){
      additionalServingNode.addAvp(Avp.SGSN_NUMBER, additionalSGSNNumber, 10415, false, false);
    }
    if (additionalSGSNName != null){
      additionalServingNode.addAvp(Avp.SGSN_NAME, additionalSGSNName, 10415, false, false, false);
    }
    if (additionalSGSNRealm != null){
      additionalServingNode.addAvp(Avp.SGSN_REALM, additionalSGSNRealm, 10415, false, false, false);
    }
    if (additionalMMEName != null){
      additionalServingNode.addAvp(Avp.MME_NAME, additionalMMEName, 10415, false, false, false);
    }
    if (additionalMMERealm != null){
      additionalServingNode.addAvp(Avp.MME_REALM, additionalMMERealm, 10415, false, false, false);
    }
    if (additionalMSCNumber != null){
      additionalServingNode.addAvp(Avp.MSC_NUMBER, additionalMSCNumber, 10415, false, false);
    }
    if (additional3GPPAAAServerName != null){
      additionalServingNode.addAvp(Avp.TGPP_AAA_SERVER_NAME, additional3GPPAAAServerName, 10415, false, false, false);
    }
    if (additionalLCSCapabilitiesSets != -1){
      additionalServingNode.addAvp(Avp.LCS_CAPABILITIES_SETS, additionalLCSCapabilitiesSets, 10415, false, false, true);
    }
    if (additionalGMLCAddress != null){
      additionalServingNode.addAvp(Avp.GMLC_ADDRESS, additionalGMLCAddress, 10415, false, false);
    }

    // [ PPR-Address ]
    java.net.InetAddress pprAddress = getPPRAddress();
    if (pprAddress != null){
      set.addAvp(Avp.PPR_ADDRESS, pprAddress, 10415, true, false);
    }

    //[ RIA-Flags ]
    long riafLags = getRIAFLags();
    if (riafLags != -1){
      set.addAvp(Avp.RIA_FLAGS, riafLags, 10415, true, false, true);
    }

    return ria;
  }

}