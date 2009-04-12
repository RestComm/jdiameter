package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag;
import net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * SdpMediaComponentImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:19:56 PM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SdpMediaComponentImpl extends GroupedAvpImpl implements SdpMediaComponent {

  private static final Logger logger = Logger.getLogger( SdpMediaComponentImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public SdpMediaComponentImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#getAuthorizedQos()
   */
  public String getAuthorizedQos()
  {
    if(hasAuthorizedQos())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.AUTHORIZED_QOS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.AUTHORIZED_QOS);
        logger.error( "Failure while trying to obtain Authorized-QOS AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#getMediaInitiatorFlag()
   */
  public MediaInitiatorFlag getMediaInitiatorFlag()
  {
    if(hasMediaInitiatorFlag())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return MediaInitiatorFlag.fromInt(rawAvp.getInteger32());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG);
        logger.error( "Failure while trying to obtain Media-Initiator-Flag AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#getSdpMediaDescriptions()
   */
  public String[] getSdpMediaDescriptions()
  {
    String[] sdpMediaDescriptions = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      sdpMediaDescriptions = new String[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          sdpMediaDescriptions[i] = rawAvps.getAvpByIndex(i).getUTF8String();
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION);
          logger.error( "Failure while trying to obtain SDP-Media-Descriptions AVP (index:" + i + ").", e );
        }
      }
    }

    return sdpMediaDescriptions;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#getSdpMediaName()
   */
  public String getSdpMediaName()
  {
    if(hasSdpMediaName())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SDP_MEDIA_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SDP_MEDIA_NAME);
        logger.error( "Failure while trying to obtain SDP-Media-Name AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#getTgppChargingId()
   */
  public byte[] getTgppChargingId()
  {
    if(hasSdpMediaName())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TGPP_CHARGING_ID);
        logger.error( "Failure while trying to obtain 3GPP-Charging-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#hasAuthorizedQos()
   */
  public boolean hasAuthorizedQos()
  {
    return hasAvp(DiameterRoAvpCodes.AUTHORIZED_QOS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#hasMediaInitiatorFlag()
   */
  public boolean hasMediaInitiatorFlag()
  {
    return hasAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#hasSdpMediaName()
   */
  public boolean hasSdpMediaName()
  {
    return hasAvp(DiameterRoAvpCodes.SDP_MEDIA_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#hasTgppChargingId()
   */
  public boolean hasTgppChargingId()
  {
    return hasAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#setAuthorizedQos(java.lang.String)
   */
  public void setAuthorizedQos( String authorizedQos )
  {
    if(hasAuthorizedQos())
    {
      throw new IllegalStateException("AVP Authorized-QOS is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.AUTHORIZED_QOS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.AUTHORIZED_QOS);
      super.avpSet.addAvp(DiameterRoAvpCodes.AUTHORIZED_QOS, authorizedQos, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#setMediaInitiatorFlag(net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag)
   */
  public void setMediaInitiatorFlag( MediaInitiatorFlag mediaInitiatorFlag )
  {
    if(hasMediaInitiatorFlag())
    {
      throw new IllegalStateException("AVP Media-Initiator-Flag is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG);
      super.avpSet.addAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG, mediaInitiatorFlag.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#setSdpMediaDescription(java.lang.String)
   */
  public void setSdpMediaDescription( String sdpMediaDescription )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION);
    super.avpSet.addAvp(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION, sdpMediaDescription, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#setSdpMediaDescriptions(java.lang.String[])
   */
  public void setSdpMediaDescriptions( String[] sdpMediaDescriptions )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    for(String sdpMediaDescription : sdpMediaDescriptions)
    {
      //super.avpSet.removeAvp(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION);
      super.avpSet.addAvp(DiameterRoAvpCodes.SDP_MEDIA_DESCRIPTION, sdpMediaDescription, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#setSdpMediaName(java.lang.String)
   */
  public void setSdpMediaName( String sdpMediaName )
  {
    if(hasSdpMediaName())
    {
      throw new IllegalStateException("AVP SDP-Media-Name is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_MEDIA_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG);
      super.avpSet.addAvp(DiameterRoAvpCodes.SDP_MEDIA_NAME, sdpMediaName, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent#setTgppChargingId(byte[])
   */
  public void setTgppChargingId( byte[] tgppChargingId )
  {
    if(hasTgppChargingId())
    {
      throw new IllegalStateException("AVP 3GPP-Charging-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.MEDIA_INITIATOR_FLAG);
      super.avpSet.addAvp(DiameterRoAvpCodes.TGPP_CHARGING_ID, tgppChargingId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
