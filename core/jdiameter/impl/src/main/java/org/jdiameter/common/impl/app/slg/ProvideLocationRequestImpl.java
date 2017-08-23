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

package org.jdiameter.common.impl.app.slg;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ProvideLocationRequestImpl extends AppRequestEventImpl implements ProvideLocationRequest {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(ProvideLocationRequestImpl.class);

  public ProvideLocationRequestImpl(Message message) {
    super(message);
    message.setRequest(true);
  }

  @Override
  public boolean isSLgLocationTypeAvpPresent() {
    return super.message.getAvps().getAvp(Avp.SLG_LOCATION_TYPE) != null;
  }

  @Override
  public int getSLgLocationType() {
    Avp slgLocationTypeAvp = super.message.getAvps().getAvp(Avp.SLG_LOCATION_TYPE);
    if (slgLocationTypeAvp != null) {
      try {
        return slgLocationTypeAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain SLg-Location-Type AVP value", e);
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
  public boolean isVelocityRequestedAvpPresent() {
    return super.message.getAvps().getAvp(Avp.VELOCITY_REQUESTED) != null;
  }

  @Override
  public int getVelocityRequested() {
    Avp lcsVelocityRequestedAvp = super.message.getAvps().getAvp(Avp.VELOCITY_REQUESTED);
    if (lcsVelocityRequestedAvp != null) {
      try {
        return lcsVelocityRequestedAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Velocity-Requested AVP value", e);
      }
    }
    return-1;
  }

  @Override
  public boolean isDeferredLocationTypeAvpPresent() {
    return super.message.getAvps().getAvp(Avp.DEFERRED_LOCATION_TYPE) != null;
  }

  @Override
  public long getDeferredLocationType() {
    Avp lcsDeferredLocationTypeAvp = super.message.getAvps().getAvp(Avp.DEFERRED_LOCATION_TYPE);
    if (lcsDeferredLocationTypeAvp != null) {
      try {
        return lcsDeferredLocationTypeAvp.getUnsigned32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain LCS Deferred Location Type AVP value", e);
      }
    }
    return -1;
  }

  @Override
  public boolean isLCSReferenceNumberAvpPresent() {
    return super.message.getAvps().getAvp(Avp.LCS_REFERENCE_NUMBER) != null;
  }

  @Override
  public byte[] getLCSReferenceNumber() {
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
  public boolean isGMLCAddressAvpPresent() {
    return super.message.getAvps().getAvp(Avp.GMLC_ADDRESS) != null;
  }

  @Override
  public java.net.InetAddress getGMLCAddress() {
    Avp lcsGMLCAddressAvp = super.message.getAvps().getAvp(Avp.GMLC_ADDRESS);
    if (lcsGMLCAddressAvp != null) {
      try {
        return lcsGMLCAddressAvp.getAddress();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain GMLC Address AVP value", e);
      }
    }
    return null;
  }

  @Override
  public boolean isPeriodicLDRInfoAvpPresent() {
    return super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION) != null;
  }

  @Override
  public boolean isReportingAmountAvpPresent() {
    Avp lcsPeriodicLDRInfo = super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION);
    if (lcsPeriodicLDRInfo != null) {
      try {
        return lcsPeriodicLDRInfo.getGrouped().getAvp(Avp.REPORTING_AMOUNT) != null;
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Reporting-Amount AVP", e);
      }
    }
    return false;
  }

  @Override
  public long getReportingAmount() {
    Avp lcsPeriodicLDRInfo = super.message.getAvps().getAvp(Avp.PERIODIC_LDR_INFORMATION);
    if (lcsPeriodicLDRInfo != null) {
      try {
        Avp lcsReportingAmountAvp = lcsPeriodicLDRInfo.getGrouped().getAvp(Avp.REPORTING_AMOUNT);
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
  public boolean isReportingIntervalAvpPresent() {
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
  public long getReportingInterval() {
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

}