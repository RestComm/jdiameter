package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LcsClientId;
import net.java.slee.resource.diameter.ro.events.avp.LcsInformation;
import net.java.slee.resource.diameter.ro.events.avp.LocationType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * LcsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:49:03 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LcsInformationImpl extends GroupedAvpImpl implements LcsInformation {

  private static final Logger logger = Logger.getLogger( LcsInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LcsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#getLcsClientId()
   */
  public LcsClientId getLcsClientId()
  {
    if(hasLcsClientId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new LcsClientIdImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_CLIENT_ID);
        logger.error( "Failure while trying to obtain LCS-Client-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#getLocationEstimate()
   */
  public String getLocationEstimate()
  {
    if(hasLocationEstimate())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LOCATION_ESTIMATE);
        logger.error( "Failure while trying to obtain Location-Estimate AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#getLocationType()
   */
  public LocationType getLocationType()
  {
    if(hasLcsClientId())
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
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#hasLcsClientId()
   */
  public boolean hasLcsClientId()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#hasLocationEstimate()
   */
  public boolean hasLocationEstimate()
  {
    return hasAvp( DiameterRoAvpCodes.LOCATION_ESTIMATE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#hasLocationType()
   */
  public boolean hasLocationType()
  {
    return hasAvp( DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#setLcsClientId(net.java.slee.resource.diameter.ro.events.avp.LcsClientId)
   */
  public void setLcsClientId( LcsClientId lcsClientId )
  {
    if(hasLcsClientId())
    {
      throw new IllegalStateException("AVP LCS-Client-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_CLIENT_ID, lcsClientId.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#setLocationEstimate(java.lang.String)
   */
  public void setLocationEstimate( String locationEstimate )
  {
    if(hasLocationEstimate())
    {
      throw new IllegalStateException("AVP Location-Estimate is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE, locationEstimate, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#setLocationType(net.java.slee.resource.diameter.ro.events.avp.LocationType)
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

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.LOCATION_TYPE, locationType.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
