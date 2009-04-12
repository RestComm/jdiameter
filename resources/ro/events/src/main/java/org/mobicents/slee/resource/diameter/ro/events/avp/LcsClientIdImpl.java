package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LcsClientId;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientName;
import net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * LcsClientIdImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:24:25 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */ 
public class LcsClientIdImpl extends GroupedAvpImpl implements LcsClientId {

  private static final Logger logger = Logger.getLogger( LcsClientIdImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LcsClientIdImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsApn()
   */
  public String getLcsApn()
  {
    if(hasLcsApn())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_APN, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_APN);
        logger.error( "Failure while trying to obtain LCS-APN AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientDialedByMs()
   */
  public String getLcsClientDialedByMs()
  {
    if(hasLcsClientDialedByMs())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS);
        logger.error( "Failure while trying to obtain LCS-Client-Dialed-By-MS AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientExternalId()
   */
  public String getLcsClientExternalId()
  {
    if(hasLcsClientExternalId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID);
        logger.error( "Failure while trying to obtain LCS-Client-External-ID AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientName()
   */
  public LcsClientName getLcsClientName()
  {
    if(hasLcsClientName())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new LcsClientNameImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_CLIENT_NAME);
        logger.error( "Failure while trying to obtain LCS-Client-Name AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientType()
   */
  public String getLcsClientType()
  {
    if(hasLcsClientType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_CLIENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_CLIENT_TYPE);
        logger.error( "Failure while trying to obtain LCS-Client-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsRequestorId()
   */
  public LcsRequestorId getLcsRequestorId()
  {
    if(hasLcsRequestorId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new LcsRequestorIdImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_REQUESTOR_ID);
        logger.error( "Failure while trying to obtain LCS-Client-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsApn()
   */
  public boolean hasLcsApn()
  {
    return hasAvp(DiameterRoAvpCodes.LCS_APN, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientDialedByMs()
   */
  public boolean hasLcsClientDialedByMs()
  {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientExternalId()
   */
  public boolean hasLcsClientExternalId()
  {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientName()
   */
  public boolean hasLcsClientName()
  {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientType()
   */
  public boolean hasLcsClientType()
  {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsRequestorId()
   */
  public boolean hasLcsRequestorId()
  {
    return hasAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsApn(java.lang.String)
   */
  public void setLcsApn( String lcsApn )
  {
    if(hasLcsApn())
    {
      throw new IllegalStateException("AVP LCS-APN is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_APN, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_APN, lcsApn, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientDialedByMs(java.lang.String)
   */
  public void setLcsClientDialedByMs( String lcsClientDialedByMs )
  {
    if(hasLcsClientDialedByMs())
    {
      throw new IllegalStateException("AVP LCS-Client-Dialed-By-MS is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, lcsClientDialedByMs, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientExternalId(java.lang.String)
   */
  public void setLcsClientExternalId( String lcsClientExternalId )
  {
    if(hasLcsClientExternalId())
    {
      throw new IllegalStateException("AVP LCS-Client-External-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, lcsClientExternalId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientName(net.java.slee.resource.diameter.ro.events.avp.LcsClientName)
   */
  public void setLcsClientName( LcsClientName lcsClientName )
  {
    if(hasLcsClientName())
    {
      throw new IllegalStateException("AVP LCS-Client-Name is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, lcsClientName.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientType(java.lang.String)
   */
  public void setLcsClientType( String lcsClientType )
  {
    if(hasLcsClientType())
    {
      throw new IllegalStateException("AVP LCS-Client-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_CLIENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_CLIENT_TYPE, lcsClientType, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsRequestorId(net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId)
   */
  public void setLcsRequestorId( LcsRequestorId lcsRequestorId )
  {
    if(hasLcsClientName())
    {
      throw new IllegalStateException("AVP LCS-Requestor-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, lcsRequestorId.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
