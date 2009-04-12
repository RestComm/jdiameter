package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * AdditionalContentInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>8:17:22 PM Apr 10, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AdditionalContentInformationImpl extends GroupedAvpImpl implements AdditionalContentInformation {

  private static final Logger logger = Logger.getLogger( AdditionalContentInformationImpl.class );
  
  public AdditionalContentInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#getAdditionalTypeInformation()
   */
  public String getAdditionalTypeInformation()
  {
    if(hasAdditionalTypeInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION);
        logger.error( "Failure while trying to obtain Additional-Type-Information AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#getContentSize()
   */
  public long getContentSize()
  {
    if(hasContentSize())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CONTENT_SIZE);
        logger.error( "Failure while trying to obtain Content-Size AVP.", e );
      }
    }

    return -1;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#getTypeNumber()
   */
  public int getTypeNumber()
  {
    if(hasTypeNumber())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getInteger32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TYPE_NUMBER);
        logger.error( "Failure while trying to obtain Type-Number AVP.", e );
      }
    }

    return -1;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#hasAdditionalTypeInformation()
   */
  public boolean hasAdditionalTypeInformation()
  {
    return hasAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#hasContentSize()
   */
  public boolean hasContentSize()
  {
    return hasAvp(DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#hasTypeNumber()
   */
  public boolean hasTypeNumber()
  {
    return hasAvp(DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#setAdditionalTypeInformation(java.lang.String)
   */
  public void setAdditionalTypeInformation( String additionalTypeInformation )
  {
    if(hasAdditionalTypeInformation())
    {
      throw new IllegalStateException("AVP Additional-Type-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, additionalTypeInformation, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#setContentSize(long)
   */
  public void setContentSize( long contentSize )
  {
    if(hasContentSize())
    {
      throw new IllegalStateException("AVP Content-Size is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.CONTENT_SIZE);
      super.avpSet.addAvp(DiameterRoAvpCodes.CONTENT_SIZE, contentSize, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, true);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation#setTypeNumber(int)
   */
  public void setTypeNumber( int typeNumber )
  {
    if(hasTypeNumber())
    {
      throw new IllegalStateException("AVP Type-Number is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.TYPE_NUMBER);
      super.avpSet.addAvp(DiameterRoAvpCodes.TYPE_NUMBER, typeNumber, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, true);
    }
  }

}
