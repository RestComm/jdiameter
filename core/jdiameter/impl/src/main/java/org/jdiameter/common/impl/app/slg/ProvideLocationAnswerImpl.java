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

package org.jdiameter.common.impl.app.slg;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
 *
 */

public class ProvideLocationAnswerImpl extends AppRequestEventImpl implements ProvideLocationAnswer {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(ProvideLocationRequestImpl.class);

  /**
   *
   * @param answer
   */
  public ProvideLocationAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public ProvideLocationAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  public Avp getResultCodeAvp() throws AvpDataException {
    return null;
  }

  @Override
  public boolean isLocationEstimateAvpPresent(){
    return super.message.getAvps().getAvp(Avp.LOCATION_ESTIMATE) != null;
  }

  @Override
  public byte[] getLocationEstimate() {
    Avp lcsLocationEstimateAvp = super.message.getAvps().getAvp(Avp.LOCATION_ESTIMATE);
    if (lcsLocationEstimateAvp != null) {
      try {
        return lcsLocationEstimateAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Location-Estimate AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isAccuracyFulfilmentIndicatorAvpPresent(){
    return super.message.getAvps().getAvp(Avp.ACCURACY_FULFILMENT_INDICATOR) != null;
  }

  @Override
  public int getAccuracyFulfilmentIndicator() {
    Avp lcsAccuracyFulfilmentIndAvp = super.message.getAvps().getAvp(Avp.ACCURACY_FULFILMENT_INDICATOR);
    if (lcsAccuracyFulfilmentIndAvp != null) {
      try {
        return lcsAccuracyFulfilmentIndAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Accuracy fulfilment indicator AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isAgeOfLocationEstimateAvpPresent(){
    return super.message.getAvps().getAvp(Avp.AGE_OF_LOCATION_ESTIMATE) != null;
  }

  @Override
  public long getAgeOfLocationEstimate() {
    Avp lcsAgeOfLocEstimateAvp = super.message.getAvps().getAvp(Avp.AGE_OF_LOCATION_ESTIMATE);
    if (lcsAgeOfLocEstimateAvp != null) {
      try {
        return lcsAgeOfLocEstimateAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Age of Location Estimate AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isVelocityEstimateAvpPresent(){
    return super.message.getAvps().getAvp(Avp.VELOCITY_ESTIMATE) != null;
  }

  @Override
  public byte[] getVelocityEstimate() {
    Avp lcsVelEstimateAvp = super.message.getAvps().getAvp(Avp.VELOCITY_ESTIMATE);
    if (lcsVelEstimateAvp != null) {
      try {
        return lcsVelEstimateAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Velocity Estimate AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isEUTRANPositioningDataAvpPresent(){
    return super.message.getAvps().getAvp(Avp.EUTRAN_POSITIONING_DATA) != null;
  }

  @Override
  public byte[] getEUTRANPositioningData() {
    Avp lcsEUTRANPosDataAvp = super.message.getAvps().getAvp(Avp.EUTRAN_POSITIONING_DATA);
    if (lcsEUTRANPosDataAvp != null) {
      try {
        return lcsEUTRANPosDataAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS E-UTRAN-Positioning-Data AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isECGIAvpPresent(){
    return super.message.getAvps().getAvp(Avp.ECGI) != null;
  }

  @Override
  public byte[] getECGI(){
    Avp lcsECGIAvp = super.message.getAvps().getAvp(Avp.ECGI);
    if (lcsECGIAvp != null) {
      try {
        return lcsECGIAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS ECGI AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isGERANPositioningInfoAvpPresent(){
    return super.message.getAvps().getAvp(Avp.GERAN_POSITIONING_INFO) != null;
  }

  @Override
  public boolean isGERANPositioningDataAVPPresent(){
    Avp lcsGERANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.GERAN_POSITIONING_INFO);
    if (lcsGERANPositioningInfoAvp != null) {
      try {
        return lcsGERANPositioningInfoAvp.getGrouped().getAvp(Avp.GERAN_POSITIONING_DATA) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Reporting-Interval AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getGERANPositioningData(){
    Avp lcsGERANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.GERAN_POSITIONING_INFO);
    if (lcsGERANPositioningInfoAvp != null) {
      try {
        Avp lcsGERANPositioningDataAvp = lcsGERANPositioningInfoAvp.getGrouped().getAvp(Avp.GERAN_POSITIONING_DATA);
        if (lcsGERANPositioningDataAvp != null){
          return lcsGERANPositioningDataAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS GERAN-Positioning-Data AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isGERANGANSSPositioningDataAVPPresent(){
    Avp lcsGERANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.GERAN_POSITIONING_INFO);
    if (lcsGERANPositioningInfoAvp != null) {
      try {
        return lcsGERANPositioningInfoAvp.getGrouped().getAvp(Avp.GERAN_GANSS_POSITIONING_DATA) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GERAN-GANSS-Positioning-Data AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getGERANGANSSPositioningData(){
    Avp lcsGERANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.GERAN_POSITIONING_INFO);
    if (lcsGERANPositioningInfoAvp != null) {
      try {
        Avp lcsGERANGANSSPositioningDataAvp = lcsGERANPositioningInfoAvp.getGrouped().getAvp(Avp.GERAN_GANSS_POSITIONING_DATA);
        if (lcsGERANGANSSPositioningDataAvp != null){
          return lcsGERANGANSSPositioningDataAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS GERAN-GANSS-Positioning-Data AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isCellGlobalIdentityAvpPresent(){
    return super.message.getAvps().getAvp(Avp.CELL_GLOBAL_IDENTITY) != null;
  }

  @Override
  public byte[] getCellGlobalIdentity(){
    Avp lcsCellGlobalIdAvp = super.message.getAvps().getAvp(Avp.CELL_GLOBAL_IDENTITY);
    if (lcsCellGlobalIdAvp != null) {
      try {
        return lcsCellGlobalIdAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Cell-Global-Identity AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isUTRANPositioningInfoAvpPresent(){
    return super.message.getAvps().getAvp(Avp.UTRAN_POSITIONING_INFO) != null;
  }

  @Override
  public boolean isUTRANPositioningDataAVPPresent(){
    Avp lcsUTRANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.UTRAN_POSITIONING_INFO);
    if (lcsUTRANPositioningInfoAvp != null) {
      try {
        return lcsUTRANPositioningInfoAvp.getGrouped().getAvp(Avp.UTRAN_POSITIONING_DATA) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain UTRAN-Positioning-Data AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getUTRANPositioningData(){
    Avp lcsUTRANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.UTRAN_POSITIONING_INFO);
    if (lcsUTRANPositioningInfoAvp != null) {
      try {
        Avp lcsUTRANPositioningDataAvp = lcsUTRANPositioningInfoAvp.getGrouped().getAvp(Avp.UTRAN_POSITIONING_DATA);
        if (lcsUTRANPositioningDataAvp != null){
          return lcsUTRANPositioningDataAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS UTRAN-Positioning-Data AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isUTRANGANSSPositioningDataAVPPresent(){
    Avp lcsUTRANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.UTRAN_POSITIONING_INFO);
    if (lcsUTRANPositioningInfoAvp != null) {
      try {
        return lcsUTRANPositioningInfoAvp.getGrouped().getAvp(Avp.UTRAN_GANSS_POSITIONING_DATA) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain UTRAN-GANSS-Positioning-Data AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getUTRANGANSSPositioningData(){
    Avp lcsUTRANPositioningInfoAvp = super.message.getAvps().getAvp(Avp.UTRAN_POSITIONING_INFO);
    if (lcsUTRANPositioningInfoAvp != null) {
      try {
        Avp lcsUTRANGANSSPositioningDataAvp = lcsUTRANPositioningInfoAvp.getGrouped().getAvp(Avp.UTRAN_GANSS_POSITIONING_DATA);
        if (lcsUTRANGANSSPositioningDataAvp != null){
          return lcsUTRANGANSSPositioningDataAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS UTRAN-GANSS-Positioning-Data AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isServiceAreaIdentityAvpPresent(){
    return super.message.getAvps().getAvp(Avp.SERVICE_AREA_IDENTITY) != null;
  }

  @Override
  public byte[] getServiceAreaIdentity(){
    Avp lcsServiceAreaIdentityAvp = super.message.getAvps().getAvp(Avp.SERVICE_AREA_IDENTITY);
    if (lcsServiceAreaIdentityAvp != null) {
      try {
        return lcsServiceAreaIdentityAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SERVICE-AREA-IDENTITY AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isServingNodeAvpPresent() {
    return super.message.getAvps().getAvp(Avp.SERVING_NODE) != null;
  }

  @Override
  public boolean isSGSNNumberAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NUMBER) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SGSN-Number AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getSGSNNumber(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsSGSNNumberAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NUMBER);
        if (lcsSGSNNumberAvp != null){
          return lcsSGSNNumberAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SGSN-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isSGSNNameAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SGSN-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getSGSNName(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsSGSNNameAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.SGSN_NAME);
        if (lcsSGSNNameAvp != null){
          return lcsSGSNNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SGSN-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isSGSNRealmAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.SGSN_REALM) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SGSN-Realm AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getSGSNRealm(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsSGSNRealmAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.SGSN_REALM);
        if (lcsSGSNRealmAvp != null){
          return lcsSGSNRealmAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS SGSN-Realm AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMMENameAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.MME_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS MME-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getMMEName(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsMMENameAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.MME_NAME);
        if (lcsMMENameAvp != null){
          return lcsMMENameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS MME-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMMERealmAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.MME_REALM) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS MME-Realm AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getMMERealm(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsMMERealmAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.MME_REALM);
        if (lcsMMERealmAvp != null){
          return lcsMMERealmAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS MME-Realm AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMSCNumberAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.MSC_NUMBER) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS MSC-Number AVP", e);
      }
    }
    return false;
  }

  @Override
  public byte[] getMSCNumber(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsMSCNumberAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.MSC_NUMBER);
        if (lcsMSCNumberAvp != null){
          return lcsMSCNumberAvp.getOctetString();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS MSC-Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean is3GPPAAAServerNameAvpPResent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.TGPP_AAA_SERVER_NAME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS 3GPP-AAA-Server-Name AVP", e);
      }
    }
    return false;
  }

  @Override
  public String get3GPPAAAServerName(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcs3GPPAAAServerNameAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.TGPP_AAA_SERVER_NAME);
        if (lcs3GPPAAAServerNameAvp != null){
          return lcs3GPPAAAServerNameAvp.getDiameterIdentity();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS 3GPP-AAA-Server-Name AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLCSCapabilitiesSetsAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.LCS_CAPABILITIES_SETS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getLCSCapabilitiesSets(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsCapabilitiesSetsAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.LCS_CAPABILITIES_SETS);
        if (lcsCapabilitiesSetsAvp != null){
          return lcsCapabilitiesSetsAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Capabilities-Sets AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isGMLCAddressAvpPresent(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        return lcsServingNodeAvp.getGrouped().getAvp(Avp.GMLC_ADDRESS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS GMLC-Address AVP", e);
      }
    }
    return false;
  }

  @Override
  public java.net.InetAddress getGMLCAddress(){
    Avp lcsServingNodeAvp = super.message.getAvps().getAvp(Avp.SERVING_NODE);
    if (lcsServingNodeAvp != null) {
      try {
        Avp lcsCapabilitiesSetsAvp = lcsServingNodeAvp.getGrouped().getAvp(Avp.GMLC_ADDRESS);
        if (lcsCapabilitiesSetsAvp != null){
          return lcsCapabilitiesSetsAvp.getAddress();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS GMLC-Address AVP value", e);
      }
    }
    return null;
  }


  @Override
  public boolean isPLAFlagsAvpPresent() {
    return super.message.getAvps().getAvp(Avp.PLA_FLAGS) != null;
  }

  @Override
  public long getPLAFlags(){
    Avp lcsPLAFlagsAvp = super.message.getAvps().getAvp(Avp.PLA_FLAGS);
    if (lcsPLAFlagsAvp != null) {
      try {
        return lcsPLAFlagsAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS PLA-Flags AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isESMLCCellInfoAvpPresent(){
    return super.message.getAvps().getAvp(Avp.ESMLC_CELL_INFO) != null;
  }

  @Override
  public long getCellPortionId(){
    Avp lcsCellPortionIdAvp = super.message.getAvps().getAvp(Avp.CELL_PORTION_ID);
    if (lcsCellPortionIdAvp != null) {
      try {
        return lcsCellPortionIdAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Cell-Portion-Id AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isCivicAddressAvpPresent(){
    return super.message.getAvps().getAvp(Avp.CIVIC_ADDRESS) != null;
  }

  @Override
  public String getCivicAddress(){
    Avp lcsCivicAddressAvp = super.message.getAvps().getAvp(Avp.CIVIC_ADDRESS);
    if (lcsCivicAddressAvp != null) {
      try {
        return lcsCivicAddressAvp.getUTF8String();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Civic Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isBarometricPressureAvpPresent(){
    return super.message.getAvps().getAvp(Avp.BAROMETRIC_PRESSURE) != null;
  }

  @Override
  public long getBarometricPressure(){
    Avp lcsBarometricPressureAvp = super.message.getAvps().getAvp(Avp.BAROMETRIC_PRESSURE);
    if (lcsBarometricPressureAvp != null) {
      try {
        return lcsBarometricPressureAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Barometric Pressure AVP value", e);
      }
    }
    return -1;
  }

}
