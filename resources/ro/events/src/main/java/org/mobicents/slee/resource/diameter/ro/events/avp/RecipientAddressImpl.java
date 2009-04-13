package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.AddressDomain;
import net.java.slee.resource.diameter.ro.events.avp.AddressType;
import net.java.slee.resource.diameter.ro.events.avp.AddresseeType;
import net.java.slee.resource.diameter.ro.events.avp.RecipientAddress;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * RecipientAddressImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:06:03 AM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RecipientAddressImpl extends GroupedAvpImpl implements RecipientAddress {

  private static final Logger logger = Logger.getLogger( RecipientAddressImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public RecipientAddressImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddressData()
   */
  public String getAddressData()
  {
    if(hasAddressData())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ADDRESS_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADDRESS_DATA);
        logger.error( "Failure while trying to obtain Address-Data AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddressDomain()
   */
  public AddressDomain getAddressDomain()
  {
    if(hasAddressDomain())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new AddressDomainImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADDRESS_DOMAIN);
        logger.error( "Failure while trying to obtain Address-Domain AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddressType()
   */
  public AddressType getAddressType()
  {
    if(hasAddresseeType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ADDRESS_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return AddressType.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADDRESS_TYPE);
        logger.error( "Failure while trying to obtain Address-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddresseeType()
   */
  public AddresseeType getAddresseeType()
  {
    if(hasAddresseeType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return AddresseeType.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADDRESSEE_TYPE);
        logger.error( "Failure while trying to obtain Addressee-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddressData()
   */
  public boolean hasAddressData()
  {
    return hasAvp( DiameterRoAvpCodes.ADDRESS_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddressDomain()
   */
  public boolean hasAddressDomain()
  {
    return hasAvp( DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddressType()
   */
  public boolean hasAddressType()
  {
    return hasAvp( DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddresseeType()
   */
  public boolean hasAddresseeType()
  {
    return hasAvp( DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddressData(java.lang.String)
   */
  public void setAddressData( String addressData )
  {
    if(hasAddressData())
    {
      throw new IllegalStateException("AVP Address-Data is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDRESS_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDRESS_DATA, addressData, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddressDomain(net.java.slee.resource.diameter.ro.events.avp.AddressDomain)
   */
  public void setAddressDomain( AddressDomain addressDomain )
  {
    if(hasAddressDomain())
    {
      throw new IllegalStateException("AVP Address-Domain is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDRESS_DOMAIN, addressDomain.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddressType(net.java.slee.resource.diameter.ro.events.avp.AddressType)
   */
  public void setAddressType( AddressType addressType )
  {
    if(hasAddressType())
    {
      throw new IllegalStateException("AVP Address-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDRESS_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDRESS_TYPE, addressType.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddresseeType(net.java.slee.resource.diameter.ro.events.avp.AddresseeType)
   */
  public void setAddresseeType( AddresseeType addresseeType )
  {
    if(hasAddresseeType())
    {
      throw new IllegalStateException("AVP Addressee-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDRESSEE_TYPE, addresseeType.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
