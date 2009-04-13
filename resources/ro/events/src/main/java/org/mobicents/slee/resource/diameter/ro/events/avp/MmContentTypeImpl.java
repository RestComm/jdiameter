package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation;
import net.java.slee.resource.diameter.ro.events.avp.MmContentType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * MmContentTypeImpl.java
 *
 * <br>Project:  mobicents
 * <br>9:16:09 AM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MmContentTypeImpl extends GroupedAvpImpl implements MmContentType {

  private static final Logger logger = Logger.getLogger( MmContentTypeImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MmContentTypeImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getAdditionalContentInformations()
   */
  public AdditionalContentInformation[] getAdditionalContentInformations()
  {
    AdditionalContentInformation[] additionalContentInformations = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      additionalContentInformations = new AdditionalContentInformation[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          additionalContentInformations[i] = new AdditionalContentInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION);
          logger.error( "Failure while trying to obtain Additional-Content-Information AVP (index:" + i + ").", e );
        }
      }
    }

    return additionalContentInformations;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getAdditionalTypeInformation()
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getContentSize()
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getTypeNumber()
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

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#hasAdditionalTypeInformation()
   */
  public boolean hasAdditionalTypeInformation()
  {
    return hasAvp( DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#hasContentSize()
   */
  public boolean hasContentSize()
  {
    return hasAvp( DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#hasTypeNumber()
   */
  public boolean hasTypeNumber()
  {
    return hasAvp( DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setAdditionalContentInformation(net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation)
   */
  public void setAdditionalContentInformation( AdditionalContentInformation additionalContentInformation )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, additionalContentInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setAdditionalContentInformations(net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation[])
   */
  public void setAdditionalContentInformations( AdditionalContentInformation[] additionalContentInformations )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(AdditionalContentInformation additionalContentInformation : additionalContentInformations)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, additionalContentInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setAdditionalTypeInformation(java.lang.String)
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

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, additionalTypeInformation, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setContentSize(long)
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

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CONTENT_SIZE, contentSize, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, true);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setTypeNumber(int)
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

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TYPE_NUMBER, typeNumber, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
