package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LcsClientName;
import net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * LcsClientNameImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:28:17 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LcsClientNameImpl extends GroupedAvpImpl implements LcsClientName {

  private static final Logger logger = Logger.getLogger( LcsClientNameImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LcsClientNameImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#getLcsDataCodingScheme()
   */
  public String getLcsDataCodingScheme()
  {
    if(hasLcsDataCodingScheme())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME);
        logger.error( "Failure while trying to obtain LCS-Data-Coding-Scheme AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#getLcsFormatIndicator()
   */
  public LcsFormatIndicator getLcsFormatIndicator()
  {
    if(hasLcsFormatIndicator())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_FORMAT_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return LcsFormatIndicator.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_FORMAT_INDICATOR);
        logger.error( "Failure while trying to obtain LCS-Format-Indicator AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#getLcsNameString()
   */
  public String getLcsNameString()
  {
    if(hasLcsDataCodingScheme())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_NAME_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_NAME_STRING);
        logger.error( "Failure while trying to obtain LCS-Name-String AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#hasLcsDataCodingScheme()
   */
  public boolean hasLcsDataCodingScheme()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#hasLcsFormatIndicator()
   */
  public boolean hasLcsFormatIndicator()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_FORMAT_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#hasLcsNameString()
   */
  public boolean hasLcsNameString()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_NAME_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#setLcsDataCodingScheme(java.lang.String)
   */
  public void setLcsDataCodingScheme( String lcsDataCodingScheme )
  {
    if(hasLcsDataCodingScheme())
    {
      throw new IllegalStateException("AVP LCS-Data-Coding-Scheme is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, lcsDataCodingScheme, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#setLcsFormatIndicator(net.java.slee.resource.diameter.ro.events.avp.LcsFormatIndicator)
   */
  public void setLcsFormatIndicator( LcsFormatIndicator lcsFormatIndicator )
  {
    if(hasLcsFormatIndicator())
    {
      throw new IllegalStateException("AVP LCS-Format-Indicator is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_FORMAT_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_FORMAT_INDICATOR, lcsFormatIndicator.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientName#setLcsNameString(java.lang.String)
   */
  public void setLcsNameString( String lcsNameString )
  {
    if(hasLcsNameString())
    {
      throw new IllegalStateException("AVP LCS-Name-String is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_NAME_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_NAME_STRING, lcsNameString, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
