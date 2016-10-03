/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import java.net.InetAddress;

/**
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
 *
 */
public class LCSRoutingInfoAnswerImpl extends AppRequestEventImpl implements LCSRoutingInfoAnswer {

  private static final long serialVersionUID = 1L;

  protected final static Logger logger = LoggerFactory.getLogger(LCSRoutingInfoAnswer.class);

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
  public boolean isAdditionalServingNodeAVPPresent(){
    return super.message.getAvps().getAvp(Avp.ADDITIONAL_SERVING_NODE) != null;
  }

  @Override
  public boolean isGMLCAddressAVPPresent(){
    return super.message.getAvps().getAvp(Avp.GMLC_ADDRESS) != null;
  }

  @Override
  public java.net.InetAddress getGMLCAddress(){
    Avp gmlcAddressAvp = super.message.getAvps().getAvp(Avp.GMLC_ADDRESS);
    if (gmlcAddressAvp != null) {
      try {
        return gmlcAddressAvp.getAddress();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC-Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMMENameAVPPresent() {
    return super.message.getAvps().getAvp(Avp.MME_NAME) != null;
  }

  @Override
  public String getMMEName(){
    Avp mmeNameAvp = super.message.getAvps().getAvp(Avp.MME_NAME);
    if (mmeNameAvp != null) {
        try {
          return mmeNameAvp.getDiameterIdentity();
        } catch (AvpDataException e) {
          logger.debug("Failure trying to obtain MME-Name AVP value", e);
        }
    }
    return null;
  }

  @Override
  public boolean isMSCNumberAVPPresent(){
    return super.message.getAvps().getAvp(Avp.MSC_NUMBER) != null;
  }

  @Override
  public byte[] getMSCNumber(){
    Avp mscNumberAvp = super.message.getAvps().getAvp(Avp.MSC_NUMBER);
    if (mscNumberAvp != null) {
      try {
        return mscNumberAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MSC-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLCSCapabilitiesSetsAVPPresent(){
      return super.message.getAvps().getAvp(Avp.LCS_CAPABILITIES_SETS) != null;
  }

  @Override
  public long getLCSCapabilitiesSets(){
    Avp lcsCapabilitiesSetsAvp = super.message.getAvps().getAvp(Avp.LCS_CAPABILITIES_SETS);
    if (lcsCapabilitiesSetsAvp != null) {
      try {
        return lcsCapabilitiesSetsAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets AVP value", e);
      }
    }
    return -1;
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
  public boolean isMMERealmAVPPresent(){
    return super.message.getAvps().getAvp(Avp.MME_REALM) != null;
  }

  @Override
  public String getMMERealm(){
    Avp mmeRealmAvp = super.message.getAvps().getAvp(Avp.MME_REALM);
    if (mmeRealmAvp != null) {
      try {
        return mmeRealmAvp.getDiameterIdentity();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain MME-REALM AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isSGSNNameAVPPresent() {
    return super.message.getAvps().getAvp(Avp.SGSN_NAME) != null;
  }

  @Override
  public String getSGSNName(){
    Avp sgsnNameAvp = super.message.getAvps().getAvp(Avp.SGSN_NAME);
    if (sgsnNameAvp != null) {
      try {
        return sgsnNameAvp.getDiameterIdentity();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-NAME AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isSGSNRealmAVPPresent() {
    return super.message.getAvps().getAvp(Avp.SGSN_REALM) != null;
  }

  @Override
  public String getSGSNRealm(){
    Avp sgsnRealmAvp = super.message.getAvps().getAvp(Avp.SGSN_REALM);
    if (sgsnRealmAvp != null) {
      try {
        return sgsnRealmAvp.getDiameterIdentity();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SGSN-REALM AVP value", e);
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
