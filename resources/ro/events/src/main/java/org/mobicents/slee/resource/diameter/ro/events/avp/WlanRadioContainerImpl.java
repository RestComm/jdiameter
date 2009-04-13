package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LocationType;
import net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * WlanRadioContainerImpl.java
 *
 * <br>Project:  mobicents
 * <br>4:33:06 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class WlanRadioContainerImpl extends GroupedAvpImpl implements WlanRadioContainer {

  private static final Logger logger = Logger.getLogger( WlanRadioContainerImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public WlanRadioContainerImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer#getLocationType()
   */
  public LocationType getLocationType()
  {
    if(hasLocationType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new LocationTypeImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LOCATION_TYPE);
        logger.error( "Failure while trying to obtain Location-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer#getWlanTechnology()
   */
  public long getWlanTechnology()
  {
    if(hasWlanTechnology())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WLAN_TECHNOLOGY, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WLAN_TECHNOLOGY);
        logger.error( "Failure while trying to obtain WLAN-Technology AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer#hasLocationType()
   */
  public boolean hasLocationType()
  {
    return hasAvp( DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer#hasWlanTechnology()
   */
  public boolean hasWlanTechnology()
  {
    return hasAvp( DiameterRoAvpCodes.WLAN_TECHNOLOGY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer#setLocationType(net.java.slee.resource.diameter.ro.events.avp.LocationType)
   */
  public void setLocationType( LocationType locationType )
  {
    if(hasLocationType())
    {
      throw new IllegalStateException("AVP Location-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LOCATION_TYPE, locationType.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer#setWlanTechnology(long)
   */
  public void setWlanTechnology( long wlanTechnology )
  {
    if(hasWlanTechnology())
    {
      throw new IllegalStateException("AVP WLAN-Technology is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WLAN_TECHNOLOGY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WLAN_TECHNOLOGY, wlanTechnology, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, true);
    }
  }

}
