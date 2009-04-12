package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.AddressDomain;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * AddressDomainImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:52:48 AM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AddressDomainImpl extends GroupedAvpImpl implements AddressDomain {

  private static final Logger logger = Logger.getLogger( AddressDomainImpl.class );
  
  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public AddressDomainImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#getDomainName()
   */
  public String getDomainName()
  {
    if(hasDomainName())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.DOMAIN_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.DOMAIN_NAME);
        logger.error( "Failure while trying to obtain Domain-Name AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#getTgppImsiMccMnc()
   */
  public byte[] getTgppImsiMccMnc()
  {
    if(hasDomainName())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC);
        logger.error( "Failure while trying to obtain 3GPP-IMSI-MCC-MNC AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#hasDomainName()
   */
  public boolean hasDomainName()
  {
    return hasAvp(DiameterRoAvpCodes.DOMAIN_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#hasTgppImsiMccMnc()
   */
  public boolean hasTgppImsiMccMnc()
  {
    return hasAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#setDomainName(java.lang.String)
   */
  public void setDomainName( String domainName )
  {
    if(hasDomainName())
    {
      throw new IllegalStateException("AVP Domain-Name is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.DOMAIN_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.DOMAIN_NAME, domainName, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#setTgppImsiMccMnc(byte[])
   */
  public void setTgppImsiMccMnc( byte[] tgppImsiMccMnc )
  {
    if(hasTgppImsiMccMnc())
    {
      throw new IllegalStateException("AVP 3GPP-IMSI-MCC-MNC is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, tgppImsiMccMnc, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
