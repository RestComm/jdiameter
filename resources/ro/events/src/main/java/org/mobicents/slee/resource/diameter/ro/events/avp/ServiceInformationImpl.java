package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.ImsInformation;
import net.java.slee.resource.diameter.ro.events.avp.LcsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MbmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.PocInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsInformation;
import net.java.slee.resource.diameter.ro.events.avp.ServiceInformation;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * ServiceInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:17:26 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServiceInformationImpl extends GroupedAvpImpl implements ServiceInformation {

  private static final Logger logger = Logger.getLogger( ServiceInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public ServiceInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getImsInformation()
   */
  public ImsInformation getImsInformation()
  {
    if(hasImsInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new ImsInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.IMS_INFORMATION);
        logger.error( "Failure while trying to obtain IMS-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getLcsInformation()
   */
  public LcsInformation getLcsInformation()
  {
    if(hasLcsInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new LcsInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.LCS_INFORMATION);
        logger.error( "Failure while trying to obtain LCS-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getMbmsInformation()
   */
  public MbmsInformation getMbmsInformation()
  {
    if(hasMbmsInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new MbmsInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MBMS_INFORMATION);
        logger.error( "Failure while trying to obtain MBMS-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getMmsInformation()
   */
  public MmsInformation getMmsInformation()
  {
    if(hasMmsInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new MmsInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MMS_INFORMATION);
        logger.error( "Failure while trying to obtain MMS-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getPocInformation()
   */
  public PocInformation getPocInformation()
  {
    if(hasPocInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new PocInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.POC_INFORMATION);
        logger.error( "Failure while trying to obtain PoC-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getPsInformation()
   */
  public PsInformation getPsInformation()
  {
    if(hasPsInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new PsInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PS_INFORMATION);
        logger.error( "Failure while trying to obtain PS-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getWlanInformation()
   */
  public WlanInformation getWlanInformation()
  {
    if(hasWlanInformation())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new WlanInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WLAN_INFORMATION);
        logger.error( "Failure while trying to obtain WLAN-Information AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasImsInformation()
   */
  public boolean hasImsInformation()
  {
    return hasAvp( DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasLcsInformation()
   */
  public boolean hasLcsInformation()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasMbmsInformation()
   */
  public boolean hasMbmsInformation()
  {
    return hasAvp( DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasMmsInformation()
   */
  public boolean hasMmsInformation()
  {
    return hasAvp( DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasPocInformation()
   */
  public boolean hasPocInformation()
  {
    return hasAvp( DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasPsInformation()
   */
  public boolean hasPsInformation()
  {
    return hasAvp( DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasWlanInformation()
   */
  public boolean hasWlanInformation()
  {
    return hasAvp( DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setImsInformation(net.java.slee.resource.diameter.ro.events.avp.ImsInformation)
   */
  public void setImsInformation( ImsInformation imsInformation )
  {
    if(hasImsInformation())
    {
      throw new IllegalStateException("AVP IMS-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.IMS_INFORMATION, imsInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setLcsInformation(net.java.slee.resource.diameter.ro.events.avp.LcsInformation)
   */
  public void setLcsInformation( LcsInformation lcsInformation )
  {
    if(hasLcsInformation())
    {
      throw new IllegalStateException("AVP LCS-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.LCS_INFORMATION, lcsInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setMbmsInformation(net.java.slee.resource.diameter.ro.events.avp.MbmsInformation)
   */
  public void setMbmsInformation( MbmsInformation mbmsInformation )
  {
    if(hasMbmsInformation())
    {
      throw new IllegalStateException("AVP MBMS-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MBMS_INFORMATION, mbmsInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setMmsInformation(net.java.slee.resource.diameter.ro.events.avp.MmsInformation)
   */
  public void setMmsInformation( MmsInformation mmsInformation )
  {
    if(hasMmsInformation())
    {
      throw new IllegalStateException("AVP MMS-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MMS_INFORMATION, mmsInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setPocInformation(net.java.slee.resource.diameter.ro.events.avp.PocInformation)
   */
  public void setPocInformation( PocInformation pocInformation )
  {
    if(hasPocInformation())
    {
      throw new IllegalStateException("AVP PoC-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.POC_INFORMATION, pocInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setPsInformation(net.java.slee.resource.diameter.ro.events.avp.PsInformation)
   */
  public void setPsInformation( PsInformation psInformation )
  {
    if(hasPsInformation())
    {
      throw new IllegalStateException("AVP PS-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PS_INFORMATION, psInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setWlanInformation(net.java.slee.resource.diameter.ro.events.avp.WlanInformation)
   */
  public void setWlanInformation( WlanInformation wlanInformation )
  {
    if(hasWlanInformation())
    {
      throw new IllegalStateException("AVP WLAN-Information is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WLAN_INFORMATION, wlanInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
