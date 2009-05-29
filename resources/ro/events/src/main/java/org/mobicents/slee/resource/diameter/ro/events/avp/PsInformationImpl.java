/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsInformation;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * PsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:18:52 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PsInformationImpl extends GroupedAvpImpl implements PsInformation {

  private static final Logger logger = Logger.getLogger( PsInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public PsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getCgAddress()
   */
  public Address getCgAddress()
  {
    if(hasCgAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CG_ADDRESS);
        logger.error( "Failure while trying to obtain CG-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getChargingRuleBaseName()
   */
  public byte[] getChargingRuleBaseName()
  {
    if(hasChargingRuleBaseName())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CHARGING_RULE_BASE_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CHARGING_RULE_BASE_NAME);
        logger.error( "Failure while trying to obtain Charging-Rule-Base-Name AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getGgsnAddress()
   */
  public Address getGgsnAddress()
  {
    if(hasGgsnAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.GGSN_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.GGSN_ADDRESS);
        logger.error( "Failure while trying to obtain GGSN-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getPdpAddress()
   */
  public Address getPdpAddress()
  {
    if(hasPdpAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PDP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PDP_ADDRESS);
        logger.error( "Failure while trying to obtain PDP-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getPsFurnishChargingInformation()
   */
  public PsFurnishChargingInformation getPsFurnishChargingInformation()
  {
    if(hasPsFurnishChargingInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new PsFurnishChargingInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION);
        logger.error( "Failure while trying to obtain PS-Furnish-Charging-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getSgsnAddress()
   */
  public Address getSgsnAddress()
  {
    if(hasSgsnAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SGSN_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SGSN_ADDRESS);
        logger.error( "Failure while trying to obtain SGSN-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppCamelChargingInfo()
   */
  public byte[] getTgppCamelChargingInfo()
  {
    if(hasTgppCamelChargingInfo())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_CAMEL_CHARGING_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_CAMEL_CHARGING_INFO);
        logger.error( "Failure while trying to obtain 3GPP-Camel-Charging-Info  AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppChargingCharacteristics()
   */
  public byte[] getTgppChargingCharacteristics()
  {
    if(hasTgppChargingCharacteristics())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_CHARGING_CHARACTERISTICS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_CHARGING_CHARACTERISTICS);
        logger.error( "Failure while trying to obtain 3GPP-Charging-Characteristics AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppChargingId()
   */
  public byte[] getTgppChargingId()
  {
    if(hasTgppChargingId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_CHARGING_ID);
        logger.error( "Failure while trying to obtain 3GPP-Charging-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppGgsnMccMnc()
   */
  public byte[] getTgppGgsnMccMnc()
  {
    if(hasTgppGgsnMccMnc())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_GGSN_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_GGSN_MCC_MNC);
        logger.error( "Failure while trying to obtain 3GPP-GGSN-MCC-MNC AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppGprsNegotiatedQosProfile()
   */
  public byte[] getTgppGprsNegotiatedQosProfile()
  {
    if(hasTgppGprsNegotiatedQosProfile())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_GPRS_NEGOTIATED_QOS_PROFILE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_GPRS_NEGOTIATED_QOS_PROFILE);
        logger.error( "Failure while trying to obtain 3GPP-GPRS-Negotiated-QoS-Profile AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppImsiMccMnc()
   */
  public byte[] getTgppImsiMccMnc()
  {
    if(hasTgppImsiMccMnc())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC);
        logger.error( "Failure while trying to obtain 3GPP-IMSI-MCC-MNC AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppMsTimezone()
   */
  public byte[] getTgppMsTimezone()
  {
    if(hasTgppMsTimezone())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_MS_TIMEZONE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_MS_TIMEZONE);
        logger.error( "Failure while trying to obtain 3GPP-MS-TimeZone AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppNsapi()
   */
  public byte[] getTgppNsapi()
  {
    if(hasTgppNsapi())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_NSAPI, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_NSAPI);
        logger.error( "Failure while trying to obtain 3GPP-NSAPI AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppPdpType()
   */
  public byte[] getTgppPdpType()
  {
    if(hasTgppPdpType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_PDP_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_PDP_TYPE);
        logger.error( "Failure while trying to obtain 3GPP-PDP-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppRatType()
   */
  public byte[] getTgppRatType()
  {
    if(hasTgppRatType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_RAT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_RAT_TYPE);
        logger.error( "Failure while trying to obtain 3GPP-RAT-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppSelectionMode()
   */
  public byte[] getTgppSelectionMode()
  {
    if(hasTgppSelectionMode())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_SELECTION_MODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_SELECTION_MODE);
        logger.error( "Failure while trying to obtain 3GPP-Selection-Mode AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppSessionStopIndicator()
   */
  public byte[] getTgppSessionStopIndicator()
  {
    if(hasTgppSessionStopIndicator())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_SESSION_STOP_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_SESSION_STOP_INDICATOR);
        logger.error( "Failure while trying to obtain 3GPP-Session-Stop-Indicator AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppSgsnMccMnc()
   */
  public byte[] getTgppSgsnMccMnc()
  {
    if(hasTgppSgsnMccMnc())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_SGSN_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_SGSN_MCC_MNC);
        logger.error( "Failure while trying to obtain 3GPP-SGSN-MCC-MNC AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#getTgppUserLocationInfo()
   */
  public byte[] getTgppUserLocationInfo()
  {
    if(hasTgppUserLocationInfo())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_USER_LOCATION_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_USER_LOCATION_INFO);
        logger.error( "Failure while trying to obtain 3GPP-User-Location-Info AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasCgAddress()
   */
  public boolean hasCgAddress()
  {
    return hasAvp( DiameterRoAvpCodes.CG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasChargingRuleBaseName()
   */
  public boolean hasChargingRuleBaseName()
  {
    return hasAvp( DiameterRoAvpCodes.CHARGING_RULE_BASE_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasGgsnAddress()
   */
  public boolean hasGgsnAddress()
  {
    return hasAvp( DiameterRoAvpCodes.GGSN_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasPdpAddress()
   */
  public boolean hasPdpAddress()
  {
    return hasAvp( DiameterRoAvpCodes.PDP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasPsFurnishChargingInformation()
   */
  public boolean hasPsFurnishChargingInformation()
  {
    return hasAvp( DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasSgsnAddress()
   */
  public boolean hasSgsnAddress()
  {
    return hasAvp( DiameterRoAvpCodes.SGSN_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppCamelChargingInfo()
   */
  public boolean hasTgppCamelChargingInfo()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_CAMEL_CHARGING_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppChargingCharacteristics()
   */
  public boolean hasTgppChargingCharacteristics()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_CHARGING_CHARACTERISTICS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppChargingId()
   */
  public boolean hasTgppChargingId()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppGgsnMccMnc()
   */
  public boolean hasTgppGgsnMccMnc()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_GGSN_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppGprsNegotiatedQosProfile()
   */
  public boolean hasTgppGprsNegotiatedQosProfile()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_GPRS_NEGOTIATED_QOS_PROFILE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppImsiMccMnc()
   */
  public boolean hasTgppImsiMccMnc()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppMsTimezone()
   */
  public boolean hasTgppMsTimezone()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_MS_TIMEZONE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppNsapi()
   */
  public boolean hasTgppNsapi()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_NSAPI, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppPdpType()
   */
  public boolean hasTgppPdpType()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_PDP_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppRatType()
   */
  public boolean hasTgppRatType()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_RAT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppSelectionMode()
   */
  public boolean hasTgppSelectionMode()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_SELECTION_MODE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppSessionStopIndicator()
   */
  public boolean hasTgppSessionStopIndicator()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_SESSION_STOP_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppSgsnMccMnc()
   */
  public boolean hasTgppSgsnMccMnc()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_SGSN_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#hasTgppUserLocationInfo()
   */
  public boolean hasTgppUserLocationInfo()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_USER_LOCATION_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setCgAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setCgAddress( Address cgAddress )
  {
    if(hasCgAddress())
    {
      throw new IllegalStateException("AVP CG-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CG_ADDRESS, cgAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setChargingRuleBaseName(byte[])
   */
  public void setChargingRuleBaseName( byte[] chargingRuleBaseName )
  {
    if(hasChargingRuleBaseName())
    {
      throw new IllegalStateException("AVP Charging-Rule-Base-Name is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CHARGING_RULE_BASE_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CHARGING_RULE_BASE_NAME, chargingRuleBaseName, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setGgsnAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setGgsnAddress( Address ggsnAddress )
  {
    if(hasGgsnAddress())
    {
      throw new IllegalStateException("AVP GGSN-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.GGSN_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.GGSN_ADDRESS, ggsnAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setPdpAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setPdpAddress( Address pdpAddress )
  {
    if(hasPdpAddress())
    {
      throw new IllegalStateException("AVP PDP-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PDP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PDP_ADDRESS, pdpAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setPsFurnishChargingInformation(net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation)
   */
  public void setPsFurnishChargingInformation( PsFurnishChargingInformation psFurnishChargingInformation )
  {
    if(hasPsFurnishChargingInformation())
    {
      throw new IllegalStateException("AVP PS-Furnish-Charging-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, psFurnishChargingInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setSgsnAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setSgsnAddress( Address sgsnAddress )
  {
    if(hasSgsnAddress())
    {
      throw new IllegalStateException("AVP SGSN-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SGSN_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.SGSN_ADDRESS, sgsnAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppCamelChargingInfo(byte[])
   */
  public void setTgppCamelChargingInfo( byte[] tgppCamelChargingInfo )
  {
    if(hasTgppCamelChargingInfo())
    {
      throw new IllegalStateException("AVP 3GPP-Camel-Charging-Info is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_CAMEL_CHARGING_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_CAMEL_CHARGING_INFO, tgppCamelChargingInfo, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppChargingCharacteristics(byte[])
   */
  public void setTgppChargingCharacteristics( byte[] tgppChargingCharacteristics )
  {
    if(hasTgppChargingCharacteristics())
    {
      throw new IllegalStateException("AVP 3GPP-Charging-Characteristics is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_CHARGING_CHARACTERISTICS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_CHARGING_CHARACTERISTICS, tgppChargingCharacteristics, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppChargingId(byte[])
   */
  public void setTgppChargingId( byte[] tgppChargingId )
  {
    if(hasTgppChargingId())
    {
      throw new IllegalStateException("AVP 3GPP-Charging-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, tgppChargingId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppGgsnMccMnc(byte[])
   */
  public void setTgppGgsnMccMnc( byte[] tgppGgsnMccMnc )
  {
    if(hasTgppGgsnMccMnc())
    {
      throw new IllegalStateException("AVP 3GPP-GGSN-MCC-MNC is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_GGSN_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_GGSN_MCC_MNC, tgppGgsnMccMnc, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppGprsNegotiatedQosProfile(byte[])
   */
  public void setTgppGprsNegotiatedQosProfile( byte[] tgppGprsNegotiatedQosProfile )
  {
    if(hasTgppGprsNegotiatedQosProfile())
    {
      throw new IllegalStateException("AVP 3GPP-GPRS-Negotiated-QoS-Profile is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_GPRS_NEGOTIATED_QOS_PROFILE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_GPRS_NEGOTIATED_QOS_PROFILE, tgppGprsNegotiatedQosProfile, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppImsiMccMnc(byte[])
   */
  public void setTgppImsiMccMnc( byte[] tgppImsiMccMnc )
  {
    if(hasTgppImsiMccMnc())
    {
      throw new IllegalStateException("AVP 3GPP-IMSI-MCC-MNC is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, tgppImsiMccMnc, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppMsTimezone(byte[])
   */
  public void setTgppMsTimezone( byte[] tgppMsTimezone )
  {
    if(hasTgppMsTimezone())
    {
      throw new IllegalStateException("AVP 3GPP-MS-TimeZone is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_MS_TIMEZONE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_MS_TIMEZONE, tgppMsTimezone, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppNsapi(byte[])
   */
  public void setTgppNsapi( byte[] tgppNsapi )
  {
    if(hasTgppNsapi())
    {
      throw new IllegalStateException("AVP 3GPP-NSAPI is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_NSAPI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_NSAPI, tgppNsapi, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppPdpType(byte[])
   */
  public void setTgppPdpType( byte[] tgppPdpType )
  {
    if(hasTgppPdpType())
    {
      throw new IllegalStateException("AVP 3GPP-PDP-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_PDP_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_PDP_TYPE, tgppPdpType, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppRatType(byte[])
   */
  public void setTgppRatType( byte[] tgppRatType )
  {
    if(hasTgppRatType())
    {
      throw new IllegalStateException("AVP 3GPP-RAT-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_RAT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_RAT_TYPE, tgppRatType, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppSelectionMode(byte[])
   */
  public void setTgppSelectionMode( byte[] tgppSelectionMode )
  {
    if(hasTgppSelectionMode())
    {
      throw new IllegalStateException("AVP 3GPP-Selection-Mode is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_SELECTION_MODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_SELECTION_MODE, tgppSelectionMode, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppSessionStopIndicator(byte[])
   */
  public void setTgppSessionStopIndicator( byte[] tgppSessionStopIndicator )
  {
    if(hasTgppSessionStopIndicator())
    {
      throw new IllegalStateException("AVP 3GPP-Session-Stop-Indicator is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_SESSION_STOP_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_SESSION_STOP_INDICATOR, tgppSessionStopIndicator, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppSgsnMccMnc(byte[])
   */
  public void setTgppSgsnMccMnc( byte[] tgppSgsnMccMnc )
  {
    if(hasTgppSgsnMccMnc())
    {
      throw new IllegalStateException("AVP 3GPP-SGSN-MCC-MNC is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_SGSN_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_SGSN_MCC_MNC, tgppSgsnMccMnc, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsInformation#setTgppUserLocationInfo(byte[])
   */
  public void setTgppUserLocationInfo( byte[] tgppUserLocationInfo )
  {
    if(hasTgppUserLocationInfo())
    {
      throw new IllegalStateException("AVP 3GPP-User-Location-Info is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_USER_LOCATION_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_USER_LOCATION_INFO, tgppUserLocationInfo, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
