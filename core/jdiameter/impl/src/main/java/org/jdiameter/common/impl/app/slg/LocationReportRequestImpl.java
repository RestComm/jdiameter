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

package org.jdiameter.common.impl.app.slg;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */
public class LocationReportRequestImpl extends AppRequestEventImpl implements LocationReportRequest {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(LocationReportRequestImpl.class);

  public LocationReportRequestImpl(Message message) {
    super(message);
    message.setRequest(true);
  }

  @Override
  public boolean isLocationEventAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LOCATION_EVENT) != null;
  }

  @Override
  public int getLocationEvent(){
    Avp slgLocationEventAvp = super.message.getAvps().getAvp(Avp.LOCATION_EVENT);
    if (slgLocationEventAvp != null) {
      try {
        return slgLocationEventAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Location-Event AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isUserNameAvpPresent() {
    return super.message.getAvps().getAvp(Avp.USER_NAME) != null; // IE: IMSI
  }

  @Override
  public String getUserName() {
    Avp userNameAvp = super.message.getAvps().getAvp(Avp.USER_NAME);
    if (userNameAvp != null) {
      try {
        return userNameAvp.getUTF8String(); // IE: IMSI mapped to User-Name
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain User-Name AVP value (IMSI)", e);
      }
    }
    return null;
  }

  @Override
  public boolean isMSISDNAvpPresent() {
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
  public boolean isIMEIAvpPresent() {
    return super.message.getAvps().getAvp(Avp.TGPP_IMEI) != null;
  }

  @Override
  public String getIMEI() {
    Avp imeiAvp = super.message.getAvps().getAvp(Avp.TGPP_IMEI);
    if (imeiAvp != null) {
      try {
        return imeiAvp.getUTF8String();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain IMEI AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLCSEPSClientNameAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LCS_EPS_CLIENT_NAME) != null;
  }

  @Override
  public boolean isLSCNameStringAvpPresent() {
    Avp lcsEPSClientNameAvp = super.message.getAvps().getAvp(Avp.LCS_EPS_CLIENT_NAME);
    if (lcsEPSClientNameAvp != null) {
      try {
        return lcsEPSClientNameAvp.getGrouped().getAvp(Avp.LCS_NAME_STRING) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Name-String AVP", e);
      }
    }
    return false;
  }

  @Override
  public String getLSCNameString() {
    Avp lcsEPSClientNameAvp = super.message.getAvps().getAvp(Avp.LCS_EPS_CLIENT_NAME);
    if (lcsEPSClientNameAvp != null) {
      try {
        Avp lcsNameStringAvp = lcsEPSClientNameAvp.getGrouped().getAvp(Avp.LCS_NAME_STRING);
        if (lcsNameStringAvp != null){
          return lcsNameStringAvp.getUTF8String();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Name-String AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isLCSFormatIndicatorAvpPresent() {
    Avp lcsEPSClientNameAvp = super.message.getAvps().getAvp(Avp.LCS_EPS_CLIENT_NAME);
    if (lcsEPSClientNameAvp != null) {
      try {
        return lcsEPSClientNameAvp.getGrouped().getAvp(Avp.LCS_FORMAT_INDICATOR) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Format-Indicator AVP", e);
      }
    }
    return false;
  }

  @Override
  public int getLCSFormatIndicator() {
    Avp lcsEPSClientNameAvp = super.message.getAvps().getAvp(Avp.LCS_EPS_CLIENT_NAME);
    if (lcsEPSClientNameAvp != null) {
      try {
        Avp lcsFormatIndicatorAvp = lcsEPSClientNameAvp.getGrouped().getAvp(Avp.LCS_FORMAT_INDICATOR);
        if (lcsFormatIndicatorAvp != null){
          return lcsFormatIndicatorAvp.getInteger32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-Format-Indicator AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isLocationEstimateAvpPresent() {
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
  public boolean isAccuracyFulfilmentIndicatorAvpPresent() {
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
  public boolean isVelocityEstimateAvpPresent() {
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
  public boolean isEUTRANPositioningDataAvpPresent() {
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
  public boolean isECGIAvpPresent() {
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
  public boolean isGERANPositioningInfoAvpPresent() {
    return super.message.getAvps().getAvp(Avp.GERAN_POSITIONING_INFO) != null;
  }

  @Override
  public boolean isGERANPositioningDataAvpPresent() {
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
  public byte[] getGERANPositioningData() {
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
  public boolean isGERANGANSSPositioningDataAvpPresent() {
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
  public byte[] getGERANGANSSPositioningData() {
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
  public boolean isCellGlobalIdentityAvpPresent() {
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
  public boolean isUTRANPositioningDataAvpPresent(){
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
  public boolean isUTRANGANSSPositioningDataAvpPresent(){
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
  public boolean isLCSServiceTypeIDAvpPresent(){
    return super.message.getAvps().getAvp(Avp.LCS_SERVICE_TYPE_ID) != null;
  }

  @Override
  public long getLCSServiceTypeID(){
    Avp lcsLCSServiceTypeIDAvp = super.message.getAvps().getAvp(Avp.LCS_SERVICE_TYPE_ID);
    if (lcsLCSServiceTypeIDAvp != null) {
      try {
        return lcsLCSServiceTypeIDAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Service Type ID AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isPseudonymIndicatorAvpPresent(){
    return super.message.getAvps().getAvp(Avp.PSEUDONYM_INDICATOR) != null;
  }

  @Override
  public int getPseudonymIndicator(){
    Avp lcsPseudonymIndicatorAvp = super.message.getAvps().getAvp(Avp.PSEUDONYM_INDICATOR);
    if (lcsPseudonymIndicatorAvp != null) {
      try {
        return lcsPseudonymIndicatorAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Pseudonym Indicator AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isLCSQoSAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LCS_QOS) != null;
  }

  @Override
  public boolean isLCSQoSClassAvpPresent(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        return lcsQoSAvp.getGrouped().getAvp(Avp.LCS_QOS_CLASS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-QoS-Class AVP", e);
      }
    }
    return false;
  }

  @Override
  public int getLCSQoSClass(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        Avp lcsQoSClassAvp = lcsQoSAvp.getGrouped().getAvp(Avp.LCS_QOS_CLASS);
        if (lcsQoSClassAvp != null){
          return lcsQoSClassAvp.getInteger32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS-QoS-Class AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isHorizontalAccuracyAvpPresent(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        return lcsQoSAvp.getGrouped().getAvp(Avp.HORIZONTAL_ACCURACY) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Horizontal-Accuracy AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getHorizontalAccuracy(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        Avp lcsQoSHorizontalAccuracyAvp = lcsQoSAvp.getGrouped().getAvp(Avp.HORIZONTAL_ACCURACY);
        if (lcsQoSHorizontalAccuracyAvp != null){
          return lcsQoSHorizontalAccuracyAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Horizontal-Accuracy AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isVerticalAccuracyAvpPresent(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        return lcsQoSAvp.getGrouped().getAvp(Avp.VERTICAL_ACCURACY) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Vertical-Accuracy AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getVerticalAccuracy(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        Avp lcsQoSVerticalAccuracyAvp = lcsQoSAvp.getGrouped().getAvp(Avp.HORIZONTAL_ACCURACY);
        if (lcsQoSVerticalAccuracyAvp != null){
          return lcsQoSVerticalAccuracyAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Vertical-Accuracy AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isVerticalRequestedAvpPresent(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        return lcsQoSAvp.getGrouped().getAvp(Avp.VERTICAL_REQUESTED) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Vertical-Requested AVP", e);
      }
    }
    return false;
  }

  public int getVerticalRequested(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        Avp lcsQoSVerticalRequestedAvp = lcsQoSAvp.getGrouped().getAvp(Avp.VERTICAL_REQUESTED);
        if (lcsQoSVerticalRequestedAvp != null){
          return lcsQoSVerticalRequestedAvp.getInteger32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Vertical-Requested AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isResponseTimeAvpPresent(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        return lcsQoSAvp.getGrouped().getAvp(Avp.RESPONSE_TIME) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Response-Time AVP", e);
      }
    }
    return false;
  }

  @Override
  public int getResponseTime(){
    Avp lcsQoSAvp = super.message.getAvps().getAvp(Avp.LCS_QOS);
    if (lcsQoSAvp != null) {
      try {
        Avp lcsQoSResponseTimeAvp = lcsQoSAvp.getGrouped().getAvp(Avp.RESPONSE_TIME);
        if (lcsQoSResponseTimeAvp != null){
          return lcsQoSResponseTimeAvp.getInteger32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Response-Time AVP value", e);
      }
    }
    return -1;
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
  public boolean is3GPPAAAServerNameAvpPresent(){
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
  public boolean isLRRFlagsAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LRR_FLAGS) != null;
  }

  @Override
  public long getLRRFLags(){
    Avp lcsLRRFlagsAvp = super.message.getAvps().getAvp(Avp.LRR_FLAGS);
    if (lcsLRRFlagsAvp != null) {
      try {
        return lcsLRRFlagsAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS LRR Flags AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isLCSReferenceNumberAvpPresent(){
    return super.message.getAvps().getAvp(Avp.LCS_REFERENCE_NUMBER) != null;
  }

  @Override
  public byte[] getLCSReferenceNumber(){
    Avp lcsLCSReferenceNumberAvp = super.message.getAvps().getAvp(Avp.LCS_REFERENCE_NUMBER);
    if (lcsLCSReferenceNumberAvp != null) {
      try {
        return lcsLCSReferenceNumberAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Reference Number AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isDeferredMTLRDataAvpPresent(){
    return super.message.getAvps().getAvp(Avp.DEFERRED_MT_LR_DATA) != null;
  }

  @Override
  public boolean isDeferredLocationTypeAvpPresent(){
    Avp lcsDeferredMTLRDataAvp = super.message.getAvps().getAvp(Avp.DEFERRED_MT_LR_DATA);
    if (lcsDeferredMTLRDataAvp != null) {
      try {
        return lcsDeferredMTLRDataAvp.getGrouped().getAvp(Avp.DEFERRED_LOCATION_TYPE) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Deferred-Location-Type AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getDeferredLocationType(){
    Avp lcsDeferredMTLRDataAvp = super.message.getAvps().getAvp(Avp.DEFERRED_MT_LR_DATA);
    if (lcsDeferredMTLRDataAvp != null) {
      try {
        Avp deferredLocationType = lcsDeferredMTLRDataAvp.getGrouped().getAvp(Avp.DEFERRED_LOCATION_TYPE);
        if (deferredLocationType != null){
          return deferredLocationType.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Deferred Location Type AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isTerminationCauseAvpPresent(){
    Avp lcsDeferredMTLRDataAvp = super.message.getAvps().getAvp(Avp.DEFERRED_MT_LR_DATA);
    if (lcsDeferredMTLRDataAvp != null) {
      try {
        return lcsDeferredMTLRDataAvp.getGrouped().getAvp(Avp.TERMINATION_CAUSE_LCS) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Termination-Cause AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getTerminationCause(){
    Avp lcsDeferredMTLRDataAvp = super.message.getAvps().getAvp(Avp.DEFERRED_MT_LR_DATA);
    if (lcsDeferredMTLRDataAvp != null) {
      try {
        Avp lcsTerminationCause = lcsDeferredMTLRDataAvp.getGrouped().getAvp(Avp.TERMINATION_CAUSE_LCS);
        if (lcsTerminationCause != null){
          return lcsTerminationCause.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Termination-Cause AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isHGMLCAddressAvpPresent(){
    return super.message.getAvps().getAvp(Avp.GMLC_ADDRESS) != null; // IE: H-GMLC Address mapped to GMLC-Address AVP
  }

  @Override
  public java.net.InetAddress getHGMLCAddress(){
    Avp lcsHGMLCAddressAvp = super.message.getAvps().getAvp(Avp.GMLC_ADDRESS); // IE: H-GMLC Address mapped to GMLC-Address AVP
    if (lcsHGMLCAddressAvp != null) {
      try {
        return lcsHGMLCAddressAvp.getAddress();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain H-GMLC-Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isPeriodicLDRInfoAvpPresent(){
    return super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION) != null;
  }

  @Override
  public boolean isReportingAmountAvpPresent(){
    Avp lcsPeriodicLDRInfoAvp = super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION);
    if (lcsPeriodicLDRInfoAvp != null) {
      try {
        return lcsPeriodicLDRInfoAvp.getGrouped().getAvp(Avp.REPORTING_AMOUNT) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Reporting-Amount AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getReportingAmount(){
    Avp lcsPeriodicLDRInfoAvp = super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION);
    if (lcsPeriodicLDRInfoAvp != null) {
      try {
        Avp lcsReportingAmountAvp = lcsPeriodicLDRInfoAvp.getGrouped().getAvp(Avp.REPORTING_AMOUNT);
        if (lcsReportingAmountAvp != null){
          return lcsReportingAmountAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Reporting amount AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isReportingIntervalAvpPresent(){
    Avp lcsPeriodicLDRInfo = super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION);
    if (lcsPeriodicLDRInfo != null) {
      try {
        return lcsPeriodicLDRInfo.getGrouped().getAvp(Avp.REPORTING_INTERVAL) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Reporting-Interval AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getReportingInterval(){
    Avp lcsPeriodicLDRInfo = super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION);
    if (lcsPeriodicLDRInfo != null) {
      try {
        Avp lcsReportingIntervalAvp = lcsPeriodicLDRInfo.getGrouped().getAvp(Avp.REPORTING_INTERVAL);
        if (lcsReportingIntervalAvp != null){
          return lcsReportingIntervalAvp.getUnsigned32();
        }
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Reporting-Interval AVP value", e);
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
  public boolean is1xRTTRCIDAvpPresent(){
    return super.message.getAvps().getAvp(Avp.ONEXRTT_RCID) != null;
  }

  @Override
  public byte[] get1xRTTRCID(){
    Avp lcs1xRTTRCIDAvp = super.message.getAvps().getAvp(Avp.ONEXRTT_RCID);
    if (lcs1xRTTRCIDAvp != null) {
      try {
        return lcs1xRTTRCIDAvp.getOctetString();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS 1xRTT_RCID AVP value", e);
      }
    }
    return null;
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
