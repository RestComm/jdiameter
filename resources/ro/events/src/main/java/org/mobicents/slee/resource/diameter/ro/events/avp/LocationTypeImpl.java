package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LocationEstimateType;
import net.java.slee.resource.diameter.ro.events.avp.LocationType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * LocationTypeImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:00:23 PM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LocationTypeImpl extends GroupedAvpImpl implements LocationType {

  private static final Logger logger = Logger.getLogger( LocationTypeImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LocationTypeImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LocationType#getDeferredLocationEventType()
   */
  public String getDeferredLocationEventType()
  {
    if(hasDeferredLocationEventType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.DEFERRED_LOCATION_EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.DEFERRED_LOCATION_EVENT_TYPE);
        logger.error( "Failure while trying to obtain Deferred-Location-Event-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LocationType#getLocationEstimateType()
   */
  public LocationEstimateType getLocationEstimateType()
  {
    if(hasLocationEstimateType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return LocationEstimateType.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LOCATION_ESTIMATE_TYPE);
        logger.error( "Failure while trying to obtain Location-Estimate-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LocationType#hasDeferredLocationEventType()
   */
  public boolean hasDeferredLocationEventType()
  {
    return hasAvp( DiameterRoAvpCodes.DEFERRED_LOCATION_EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LocationType#hasLocationEstimateType()
   */
  public boolean hasLocationEstimateType()
  {
    return hasAvp( DiameterRoAvpCodes.LOCATION_ESTIMATE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LocationType#setDeferredLocationEventType(java.lang.String)
   */
  public void setDeferredLocationEventType( String deferredLocationEventType )
  {
    if(hasDeferredLocationEventType())
    {
      throw new IllegalStateException("AVP Deferred-Location-Event-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.DEFERRED_LOCATION_EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.DEFERRED_LOCATION_EVENT_TYPE, deferredLocationEventType, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LocationType#setLocationEstimateType(net.java.slee.resource.diameter.ro.events.avp.LocationEstimateType)
   */
  public void setLocationEstimateType( LocationEstimateType locationEstimateType )
  {
    if(hasLocationEstimateType())
    {
      throw new IllegalStateException("AVP Location-Estimate-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE_TYPE, locationEstimateType.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
