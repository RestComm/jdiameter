/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
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

import net.java.slee.resource.diameter.ro.events.avp.AddressDomain;
import net.java.slee.resource.diameter.ro.events.avp.AddressType;
import net.java.slee.resource.diameter.ro.events.avp.AddresseeType;
import net.java.slee.resource.diameter.ro.events.avp.RecipientAddress;

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

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public RecipientAddressImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddressData()
   */
  public String getAddressData() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.ADDRESS_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddressDomain()
   */
  public AddressDomain getAddressDomain() {
    return (AddressDomain) getAvpAsCustom(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, AddressDomainImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddressType()
   */
  public AddressType getAddressType() {
    return (AddressType) getAvpAsEnumerated(DiameterRoAvpCodes.ADDRESS_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, AddressType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#getAddresseeType()
   */
  public AddresseeType getAddresseeType() {
    return (AddresseeType) getAvpAsEnumerated(DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, AddresseeType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddressData()
   */
  public boolean hasAddressData() {
    return hasAvp( DiameterRoAvpCodes.ADDRESS_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddressDomain()
   */
  public boolean hasAddressDomain() {
    return hasAvp( DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddressType()
   */
  public boolean hasAddressType() {
    return hasAvp( DiameterRoAvpCodes.ADDRESS_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#hasAddresseeType()
   */
  public boolean hasAddresseeType() {
    return hasAvp( DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddressData(java.lang.String)
   */
  public void setAddressData( String addressData ) {
    addAvp(DiameterRoAvpCodes.ADDRESS_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID, addressData);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddressDomain(net.java.slee.resource.diameter.ro.events.avp.AddressDomain)
   */
  public void setAddressDomain( AddressDomain addressDomain ) {
    addAvp(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, addressDomain.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddressType(net.java.slee.resource.diameter.ro.events.avp.AddressType)
   */
  public void setAddressType( AddressType addressType ) {
    addAvp(DiameterRoAvpCodes.ADDRESS_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, addressType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.RecipientAddress#setAddresseeType(net.java.slee.resource.diameter.ro.events.avp.AddresseeType)
   */
  public void setAddresseeType( AddresseeType addressType ) {
    addAvp(DiameterRoAvpCodes.ADDRESSEE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, addressType.getValue());
  }

}
