package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported;
import net.java.slee.resource.diameter.ro.events.avp.MbmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * MbmsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:22:57 PM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */ 
public class MbmsInformationImpl extends GroupedAvpImpl implements MbmsInformation {

  private static final Logger logger = Logger.getLogger( MbmsInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MbmsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getFileRepairSupported()
   */
  public FileRepairSupported getFileRepairSupported()
  {
    if(hasFileRepairSupported())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return FileRepairSupported.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED);
        logger.error( "Failure while trying to obtain File-Repair-Supported AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbms2g3gIndicator()
   */
  public byte[] getMbms2g3gIndicator()
  {
    if(hasMbms2g3gIndicator())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR);
        logger.error( "Failure while trying to obtain MBMS-2G-3G-Indicator AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsServiceAreas()
   */
  public byte[][] getMbmsServiceAreas()
  {
    byte[][] mbmsServiceAreas = null;
    
    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.MBMS_SERVICE_AREA, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    mbmsServiceAreas = new byte[rawAvps.size()][];
    
    int i = 0;      
    
    try
    {
      for(Avp rawAvp : rawAvps)
      {
        mbmsServiceAreas[i++] = rawAvp.getRaw();
      }
    }
    catch (AvpDataException e) {
      reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MBMS_SERVICE_AREA);
      logger.error( "Failure while trying to obtain MBMS-Service-Area AVP.", e );
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsServiceType()
   */
  public byte[] getMbmsServiceType()
  {
    if(hasMbmsServiceType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MBMS_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MBMS_SERVICE_TYPE);
        logger.error( "Failure while trying to obtain MBMS-Service-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsSessionIdentity()
   */
  public byte[] getMbmsSessionIdentity()
  {
    if(hasMbmsSessionIdentity())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MBMS_SESSION_IDENTITY);
        logger.error( "Failure while trying to obtain MBMS-Session-Identity AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsUserServiceType()
   */
  public MbmsUserServiceType getMbmsUserServiceType()
  {
    if(hasMbmsUserServiceType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return MbmsUserServiceType.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE);
        logger.error( "Failure while trying to obtain MBMS-User-Service-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getRai()
   */
  public byte[] getRai()
  {
    if(hasRai())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.RAI, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.RAI);
        logger.error( "Failure while trying to obtain RAI AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getRequiredMbmsBearerCapabilities()
   */
  public byte[] getRequiredMbmsBearerCapabilities()
  {
    if(hasRequiredMbmsBearerCapabilities())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES);
        logger.error( "Failure while trying to obtain Required-MBMS-Bearer-Capabilities AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getTmgi()
   */
  public byte[] getTmgi()
  {
    if(hasTmgi())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TMGI, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TMGI);
        logger.error( "Failure while trying to obtain TMGI AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasFileRepairSupported()
   */
  public boolean hasFileRepairSupported()
  {
    return hasAvp( DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbms2g3gIndicator()
   */
  public boolean hasMbms2g3gIndicator()
  {
    return hasAvp( DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbmsServiceType()
   */
  public boolean hasMbmsServiceType()
  {
    return hasAvp( DiameterRoAvpCodes.MBMS_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbmsSessionIdentity()
   */
  public boolean hasMbmsSessionIdentity()
  {
    return hasAvp( DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbmsUserServiceType()
   */
  public boolean hasMbmsUserServiceType()
  {
    return hasAvp( DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasRai()
   */
  public boolean hasRai()
  {
    return hasAvp( DiameterRoAvpCodes.RAI, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasRequiredMbmsBearerCapabilities()
   */
  public boolean hasRequiredMbmsBearerCapabilities()
  {
    return hasAvp( DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasTmgi()
   */
  public boolean hasTmgi()
  {
    return hasAvp( DiameterRoAvpCodes.TMGI, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setFileRepairSupported(net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported)
   */
  public void setFileRepairSupported( FileRepairSupported fileRepairSupported )
  {
    if(hasFileRepairSupported())
    {
      throw new IllegalStateException("AVP File-Repair-Supported is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, fileRepairSupported.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbms2g3gIndicator(byte[])
   */
  public void setMbms2g3gIndicator( byte[] mbms2g3gIndicator )
  {
    if(hasMbms2g3gIndicator())
    {
      throw new IllegalStateException("AVP MBMS-2G-3G-Indicator is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, mbms2g3gIndicator, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsServiceArea(byte[])
   */
  public void setMbmsServiceArea( byte[] mbmsServiceArea )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_SERVICE_AREA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_SERVICE_AREA, mbmsServiceArea, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsServiceAreas(byte[][])
   */
  public void setMbmsServiceAreas( byte[][] mbmsServiceAreas )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_SERVICE_AREA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(byte[] mbmsServiceArea : mbmsServiceAreas)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_SERVICE_AREA, mbmsServiceArea, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsServiceType(byte[])
   */
  public void setMbmsServiceType( byte[] mbmsServiceType )
  {
    if(hasMbmsServiceType())
    {
      throw new IllegalStateException("AVP MBMS-Service-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_SERVICE_TYPE, mbmsServiceType, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsSessionIdentity(byte[])
   */
  public void setMbmsSessionIdentity( byte[] mbmsSessionIdentity )
  {
    if(hasMbmsSessionIdentity())
    {
      throw new IllegalStateException("AVP MBMS-Session-Identity is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, mbmsSessionIdentity, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsUserServiceType(net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType)
   */
  public void setMbmsUserServiceType( MbmsUserServiceType mbmsUserServiceType )
  {
    if(hasMbmsUserServiceType())
    {
      throw new IllegalStateException("AVP MBMS-User-Service-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, mbmsUserServiceType.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setRai(byte[])
   */
  public void setRai( byte[] rai )
  {
    if(hasRai())
    {
      throw new IllegalStateException("AVP RAI is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.RAI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.RAI, rai, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setRequiredMbmsBearerCapabilities(byte[])
   */
  public void setRequiredMbmsBearerCapabilities( byte[] requiredMbmsBearerCapabilities )
  {
    if(hasRequiredMbmsBearerCapabilities())
    {
      throw new IllegalStateException("AVP Required-MBMS-Bearer-Capabilities is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, requiredMbmsBearerCapabilities, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setTmgi(byte[])
   */
  public void setTmgi( byte[] tmgi )
  {
    if(hasTmgi())
    {
      throw new IllegalStateException("AVP TMGI is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TMGI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TMGI, tmgi, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
