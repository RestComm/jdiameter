package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * LcsRequestorIdImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:41:44 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LcsRequestorIdImpl extends GroupedAvpImpl implements LcsRequestorId {

  private static final Logger logger = Logger.getLogger( LcsRequestorIdImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LcsRequestorIdImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#getLcsDataCodingScheme()
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
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#getLcsRequestorIdString()
   */
  public String getLcsRequestorIdString()
  {
    if(hasLcsRequestorIdString())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING);
        logger.error( "Failure while trying to obtain LCS-Requestor-Id-String AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#hasLcsDataCodingScheme()
   */
  public boolean hasLcsDataCodingScheme()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#hasLcsRequestorIdString()
   */
  public boolean hasLcsRequestorIdString()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#setLcsDataCodingScheme(java.lang.String)
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
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#setLcsRequestorIdString(java.lang.String)
   */
  public void setLcsRequestorIdString( String lcsRequestorIdString )
  {
    if(hasLcsDataCodingScheme())
    {
      throw new IllegalStateException("AVP LCS-Requestor-Id-String is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, lcsRequestorIdString, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
