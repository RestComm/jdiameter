/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;
import net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * WlanInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>4:15:14 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class WlanInformationImpl extends GroupedAvpImpl implements WlanInformation {

  private static final Logger logger = Logger.getLogger( WlanInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public WlanInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getPdgAddress()
   */
  public Address getPdgAddress()
  {
    if(hasPdgAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PDG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PDG_ADDRESS);
        logger.error( "Failure while trying to obtain PDG-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getPdgChargingId()
   */
  public long getPdgChargingId()
  {
    if(hasPdgChargingId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PDG_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PDG_CHARGING_ID);
        logger.error( "Failure while trying to obtain PDG-Address AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWagAddress()
   */
  public Address getWagAddress()
  {
    if(hasWagAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WAG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WAG_ADDRESS);
        logger.error( "Failure while trying to obtain WAG-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWagPlmnId()
   */
  public byte[] getWagPlmnId()
  {
    if(hasWagPlmnId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WAG_PLMN_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WAG_PLMN_ID);
        logger.error( "Failure while trying to obtain WAG-PLMN-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWlanRadioContainer()
   */
  public WlanRadioContainer getWlanRadioContainer()
  {
    if(hasWlanRadioContainer())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new WlanRadioContainerImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WLAN_RADIO_CONTAINER);
        logger.error( "Failure while trying to obtain WLAN-Radio-Container AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWlanSessionId()
   */
  public String getWlanSessionId()
  {
    if(hasWlanSessionId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WLAN_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WLAN_SESSION_ID);
        logger.error( "Failure while trying to obtain WLAN-Session-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWlanUeLocalIpaddress()
   */
  public Address getWlanUeLocalIpaddress()
  {
    if(hasWlanUeLocalIpaddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode( rawAvp.getRaw() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS);
        logger.error( "Failure while trying to obtain WLAN-UE-Local-IPAddress AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasPdgAddress()
   */
  public boolean hasPdgAddress()
  {
    return hasAvp( DiameterRoAvpCodes.PDG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasPdgChargingId()
   */
  public boolean hasPdgChargingId()
  {
    return hasAvp( DiameterRoAvpCodes.PDG_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWagAddress()
   */
  public boolean hasWagAddress()
  {
    return hasAvp( DiameterRoAvpCodes.WAG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWagPlmnId()
   */
  public boolean hasWagPlmnId()
  {
    return hasAvp( DiameterRoAvpCodes.WAG_PLMN_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWlanRadioContainer()
   */
  public boolean hasWlanRadioContainer()
  {
    return hasAvp( DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWlanSessionId()
   */
  public boolean hasWlanSessionId()
  {
    return hasAvp( DiameterRoAvpCodes.WLAN_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWlanUeLocalIpaddress()
   */
  public boolean hasWlanUeLocalIpaddress()
  {
    return hasAvp( DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setPdgAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setPdgAddress( Address pdgAddress )
  {
    if(hasPdgAddress())
    {
      throw new IllegalStateException("AVP PDG-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PDG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PDG_ADDRESS, pdgAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setPdgChargingId(long)
   */
  public void setPdgChargingId( long pdgChargingId )
  {
    if(hasPdgChargingId())
    {
      throw new IllegalStateException("AVP PDG-Charging-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PDG_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PDG_CHARGING_ID, pdgChargingId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, true);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWagAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setWagAddress( Address wagAddress )
  {
    if(hasWagAddress())
    {
      throw new IllegalStateException("AVP WAG-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WAG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WAG_ADDRESS, wagAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWagPlmnId(byte[])
   */
  public void setWagPlmnId( byte[] wagPlmnId )
  {
    if(hasWagPlmnId())
    {
      throw new IllegalStateException("AVP WAG-PLMN-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WAG_PLMN_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WAG_PLMN_ID, wagPlmnId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWlanRadioContainer(net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer)
   */
  public void setWlanRadioContainer( WlanRadioContainer wlanRadioContainer )
  {
    if(hasWlanRadioContainer())
    {
      throw new IllegalStateException("AVP WLAN-Radio-Container is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, wlanRadioContainer.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWlanSessionId(java.lang.String)
   */
  public void setWlanSessionId( String wlanSessionId )
  {
    if(hasWlanSessionId())
    {
      throw new IllegalStateException("AVP WLAN-Session-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WLAN_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WLAN_SESSION_ID, wlanSessionId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWlanUeLocalIpaddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setWlanUeLocalIpaddress( Address wlanUeLocalIpaddress )
  {
    if(hasWlanUeLocalIpaddress())
    {
      throw new IllegalStateException("AVP WLAN-UE-Local-IPAddress is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, wlanUeLocalIpaddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
