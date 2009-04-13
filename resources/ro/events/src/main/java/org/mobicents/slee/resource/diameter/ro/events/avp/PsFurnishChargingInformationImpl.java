package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.PsAppendFreeFormatData;
import net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * PsFurnishChargingInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:46:28 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PsFurnishChargingInformationImpl extends GroupedAvpImpl implements PsFurnishChargingInformation {

  private static final Logger logger = Logger.getLogger( PsFurnishChargingInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public PsFurnishChargingInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#getPsAppendFreeFormatData()
   */
  public PsAppendFreeFormatData getPsAppendFreeFormatData()
  {
    if(hasPsAppendFreeFormatData())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PS_APPEND_FREE_FORMAT_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return PsAppendFreeFormatData.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PS_APPEND_FREE_FORMAT_DATA);
        logger.error( "Failure while trying to obtain PS-Append-Free-Format-Data AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#getPsFreeFormatData()
   */
  public byte[] getPsFreeFormatData()
  {
    if(hasPsFreeFormatData())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PS_FREE_FORMAT_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PS_FREE_FORMAT_DATA);
        logger.error( "Failure while trying to obtain PS-Free-Format-Data AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#getTgppChargingId()
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
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#hasPsAppendFreeFormatData()
   */
  public boolean hasPsAppendFreeFormatData()
  {
    return hasAvp( DiameterRoAvpCodes.PS_APPEND_FREE_FORMAT_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#hasPsFreeFormatData()
   */
  public boolean hasPsFreeFormatData()
  {
    return hasAvp( DiameterRoAvpCodes.PS_FREE_FORMAT_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#hasTgppChargingId()
   */
  public boolean hasTgppChargingId()
  {
    return hasAvp( DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#setPsAppendFreeFormatData(net.java.slee.resource.diameter.ro.events.avp.PsAppendFreeFormatData)
   */
  public void setPsAppendFreeFormatData( PsAppendFreeFormatData psAppendFreeFormatData )
  {
    if(hasPsAppendFreeFormatData())
    {
      throw new IllegalStateException("AVP PS-Append-Free-Format-Data is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PS_APPEND_FREE_FORMAT_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PS_APPEND_FREE_FORMAT_DATA, psAppendFreeFormatData.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#setPsFreeFormatData(byte[])
   */
  public void setPsFreeFormatData( byte[] psFreeFormatData )
  {
    if(hasPsFreeFormatData())
    {
      throw new IllegalStateException("AVP PS-Free-Format-Data is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PS_FREE_FORMAT_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PS_FREE_FORMAT_DATA, psFreeFormatData, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation#setTgppChargingId(byte[])
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

}
