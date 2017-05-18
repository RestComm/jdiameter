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

package org.jdiameter.common.impl.app.slh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import java.net.InetAddress;

/**
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */
public class LCSRoutingInfoAnswerImpl extends AppRequestEventImpl implements LCSRoutingInfoAnswer {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(LCSRoutingInfoAnswer.class);

  /**
   *
   * @param answer
   */
  public LCSRoutingInfoAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public LCSRoutingInfoAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  @Override
  public boolean isUserNameAVPPresent() {
    return super.message.getAvps().getAvp(Avp.USER_NAME) != null; // IE: IMSI mapped to User-Name AVP
  }

  @Override
  public String getUserName() {
    Avp userNameAvp = super.message.getAvps().getAvp(Avp.USER_NAME);
    if (userNameAvp != null) {
      try {
        return userNameAvp.getUTF8String(); // IE: IMSI mapped to User-Name AVP
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain User-Name AVP value (IMSI)", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMSISDNAVPPresent() {
    return super.message.getAvps().getAvp(Avp.MSISDN) != null;
  }

  @Override
  public byte[] getMSISDN() {
    Avp msisdnAvp = super.message.getAvps().getAvp(Avp.MSISDN);
    if (msisdnAvp != null) {
      try {
        return msisdnAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSISDN AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLMSIAVPPresent() {
    return super.message.getAvps().getAvp(Avp.LMSI) != null;
  }

  @Override
  public byte[] getLMSI() {
    Avp localMobileStationIdentityAvp = super.message.getAvps().getAvp(Avp.LMSI);
    if (localMobileStationIdentityAvp != null) {
      try {
        return localMobileStationIdentityAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LMSI AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isServingNodeAVPPresent() {
    return super.message.getAvps().getAvp(Avp.SERVING_NODE) != null;
  }

  @Override
  public boolean isSGSNNumberAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.SGSN_NUMBER) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Number AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getSGSNNumber(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp sgsnNumberAvp = servingNodeAvp.getGrouped().getAvp(Avp.SGSN_NUMBER);
        if (sgsnNumberAvp != null){
          return sgsnNumberAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isSGSNNameAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.SGSN_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getSGSNName(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp sgsnNameAvp = servingNodeAvp.getGrouped().getAvp(Avp.SGSN_NAME);
        if (sgsnNameAvp != null){
          return sgsnNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isSGSNRealmAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.SGSN_REALM) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Realm AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getSGSNRealm(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp sgsnRealmAvp = servingNodeAvp.getGrouped().getAvp(Avp.SGSN_REALM);
        if (sgsnRealmAvp != null){
          return sgsnRealmAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Realm AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMMENameAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.MME_NAME) != null;
      } catch (AvpDataException ex) {
        logger.debug("Failure trying to obtain MME-Name AVP", ex);
      }
    }
    return false;
  }

  @Override
  public String getMMEName(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp mmeNameAvp = servingNodeAvp.getGrouped().getAvp(Avp.MME_NAME);
        if (mmeNameAvp != null){
          return mmeNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MME-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMMERealmAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.MME_REALM) != null;
      } catch (AvpDataException ex) {
        logger.debug("Failure trying to obtain MME-Realm AVP", ex);
      }
    }
    return false;
  }

  @Override
  public String getMMERealm(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp mmeRealmAvp = servingNodeAvp.getGrouped().getAvp(Avp.MME_REALM);
        if (mmeRealmAvp != null){
          return mmeRealmAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MME-Realm AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMSCNumberAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.MSC_NUMBER) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getMSCNumber(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp mscNumberAvp = servingNodeAvp.getGrouped().getAvp(Avp.MSC_NUMBER);
        if (mscNumberAvp != null){
          return mscNumberAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean is3GPPAAAServerNameAVPPresent() {
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.TGPP_AAA_SERVER_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain 3GPP-AAA-Server-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String get3GPPAAAServerName(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp tgppAAAServerNameAvp = servingNodeAvp.getGrouped().getAvp(Avp.TGPP_AAA_SERVER_NAME);
        if (tgppAAAServerNameAvp != null){
          return tgppAAAServerNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLCSCapabilitiesSetsAVPPresent(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.LCS_CAPABILITIES_SETS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getLCSCapabilitiesSets(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp lcsCapabilitiesSetsAvp = servingNodeAvp.getGrouped().getAvp(Avp.LCS_CAPABILITIES_SETS);
        if (lcsCapabilitiesSetsAvp != null){
          return lcsCapabilitiesSetsAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isGMLCAddressAVPPresent(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        return servingNodeAvp.getGrouped().getAvp(Avp.GMLC_ADDRESS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC-Address AVP", e);
      }
    }
    return false;
  }

  @Override
  public java.net.InetAddress getGMLCAddress(){
    Avp servingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (servingNodeAvp != null) {
      try {
        Avp gmlcAddressAvp = servingNodeAvp.getGrouped().getAvp(Avp.GMLC_ADDRESS);
        if (gmlcAddressAvp != null){
          return gmlcAddressAvp.getAddress();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC-Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalServingNodeAVPPresent(){
    return super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE) != null;
  }

  @Override
  public boolean isAdditionalSGSNNumberAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NUMBER) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Number AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getAdditionalSGSNNumber(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp sgsnNumberAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NUMBER);
        if (sgsnNumberAvp != null){
          return sgsnNumberAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalSGSNNameAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getAdditionalSGSNName(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp sgsnNameAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NAME);
        if (sgsnNameAvp != null){
          return sgsnNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalSGSNRealmAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.SGSN_REALM) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Realm AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getAdditionalSGSNRealm(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp sgsnRealmAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.SGSN_REALM);
        if (sgsnRealmAvp != null){
          return sgsnRealmAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-Realm AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalMMENameAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.MME_NAME) != null;
      } catch (AvpDataException ex) {
        logger.debug("Failure trying to obtain MME-Name AVP", ex);
      }
    }
    return false;
  }

  @Override
  public String getAdditionalMMEName(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp mmeNameAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.MME_NAME);
        if (mmeNameAvp != null){
          return mmeNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MME-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalMMERealmAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.MME_REALM) != null;
      } catch (AvpDataException ex) {
        logger.debug("Failure trying to obtain MME-Realm AVP", ex);
      }
    }
    return false;
  }

  @Override
  public String getAdditionalMMERealm(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp mmeRealmAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.MME_REALM);
        if (mmeRealmAvp != null){
          return mmeRealmAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MME-Realm AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalMSCNumberAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.MSC_NUMBER) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getAdditionalMSCNumber(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp mscNumberAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.MSC_NUMBER);
        if (mscNumberAvp != null){
          return mscNumberAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditional3GPPAAAServerNameAVPPresent() {
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.TGPP_AAA_SERVER_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain 3GPP-AAA-Server-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getAdditional3GPPAAAServerName(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp tgppAAAServerNameAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.TGPP_AAA_SERVER_NAME);
        if (tgppAAAServerNameAvp != null){
          return tgppAAAServerNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAdditionalLCSCapabilitiesSetsAVPPresent(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.LCS_CAPABILITIES_SETS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getAdditionalLCSCapabilitiesSets(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp lcsCapabilitiesSetsAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.LCS_CAPABILITIES_SETS);
        if (lcsCapabilitiesSetsAvp != null){
          return lcsCapabilitiesSetsAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isAdditionalGMLCAddressAVPPresent(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        return additionalServingNodeAvp.getGrouped().getAvp(Avp.GMLC_ADDRESS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC-Address AVP", e);
      }
    }
    return false;
  }

  @Override
  public java.net.InetAddress getAdditionalGMLCAddress(){
    Avp additionalServingNodeAvp = super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE);
    if (additionalServingNodeAvp != null) {
      try {
        Avp gmlcAddressAvp = additionalServingNodeAvp.getGrouped().getAvp(Avp.GMLC_ADDRESS);
        if (gmlcAddressAvp != null){
          return gmlcAddressAvp.getAddress();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC-Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isPPRAddressAVPPresent(){
    return super.message.getAvps().getAvp(Avp.PPR_ADDRESS) != null;
  }

  @Override
  public java.net.InetAddress getPPRAddress(){
    Avp pprAddressAvp = super.message.getAvps().getAvp(Avp.PPR_ADDRESS);
    if (pprAddressAvp != null) {
      try {
        return pprAddressAvp.getAddress();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain PPR-Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isRIAFlagsAVPPresent() {
    return super.message.getAvps().getAvp(Avp.RIA_FLAGS) != null;
  }

  @Override
  public long getRIAFLags(){
    Avp riaFlagsAvp = super.message.getAvps().getAvp(Avp.RIA_FLAGS);
    if (riaFlagsAvp != null) {
      try {
        return riaFlagsAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain RIA-FLAGS AVP value", e);
      }
    }
    return -1;
  }

  public Avp getResultCodeAvp() throws AvpDataException {
    return null;
  }

}
