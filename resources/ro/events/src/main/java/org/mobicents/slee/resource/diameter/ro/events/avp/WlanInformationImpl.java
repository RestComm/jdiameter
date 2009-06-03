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

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public WlanInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getPdgAddress()
   */
  public Address getPdgAddress() {
    return getAvpAsAddress(DiameterRoAvpCodes.PDG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getPdgChargingId()
   */
  public long getPdgChargingId() {
    return getAvpAsUnsigned32(DiameterRoAvpCodes.PDG_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWagAddress()
   */
  public Address getWagAddress() {
    return getAvpAsAddress(DiameterRoAvpCodes.WAG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWagPlmnId()
   */
  public String getWagPlmnId() {
    return getAvpAsOctetString(DiameterRoAvpCodes.WAG_PLMN_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWlanRadioContainer()
   */
  public WlanRadioContainer getWlanRadioContainer() {
    return (WlanRadioContainer) getAvpAsCustom(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, WlanRadioContainerImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWlanSessionId()
   */
  public String getWlanSessionId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.WLAN_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#getWlanUeLocalIpaddress()
   */
  public Address getWlanUeLocalIpaddress() {
    return getAvpAsAddress(DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasPdgAddress()
   */
  public boolean hasPdgAddress() {
    return hasAvp( DiameterRoAvpCodes.PDG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasPdgChargingId()
   */
  public boolean hasPdgChargingId() {
    return hasAvp( DiameterRoAvpCodes.PDG_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWagAddress()
   */
  public boolean hasWagAddress() {
    return hasAvp( DiameterRoAvpCodes.WAG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWagPlmnId()
   */
  public boolean hasWagPlmnId() {
    return hasAvp( DiameterRoAvpCodes.WAG_PLMN_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWlanRadioContainer()
   */
  public boolean hasWlanRadioContainer() {
    return hasAvp( DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWlanSessionId()
   */
  public boolean hasWlanSessionId() {
    return hasAvp( DiameterRoAvpCodes.WLAN_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#hasWlanUeLocalIpaddress()
   */
  public boolean hasWlanUeLocalIpaddress() {
    return hasAvp( DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setPdgAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setPdgAddress( Address pdgAddress ) {
    addAvp(DiameterRoAvpCodes.PDG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, pdgAddress.encode());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setPdgChargingId(long)
   */
  public void setPdgChargingId( long pdgChargingId ) {
    addAvp(DiameterRoAvpCodes.PDG_CHARGING_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, pdgChargingId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWagAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setWagAddress( Address wagAddress ) {
    addAvp(DiameterRoAvpCodes.WAG_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, wagAddress.encode());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWagPlmnId(byte[])
   */
  public void setWagPlmnId( String wagPlmnId ) {
    addAvp(DiameterRoAvpCodes.WAG_PLMN_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, wagPlmnId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWlanRadioContainer(net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer)
   */
  public void setWlanRadioContainer( WlanRadioContainer wlanRadioContainer ) {
    addAvp(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, wlanRadioContainer.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWlanSessionId(java.lang.String)
   */
  public void setWlanSessionId( String wlanSessionId ) {
    addAvp(DiameterRoAvpCodes.WLAN_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, wlanSessionId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.WlanInformation#setWlanUeLocalIpaddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setWlanUeLocalIpaddress( Address wlanUeLocalIpaddress ) {
    addAvp(DiameterRoAvpCodes.WLAN_UE_LOCAL_IPADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, wlanUeLocalIpaddress.encode());
  }

}
