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

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public AddressDomainImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super( code, vendorId, mnd, prt, value );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#getDomainName()
   */
  public String getDomainName() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.DOMAIN_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#getTgppImsiMccMnc()
   */
  public byte[] getTgppImsiMccMnc() {
    return getAvpAsRaw(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#hasDomainName()
   */
  public boolean hasDomainName() {
    return hasAvp(DiameterRoAvpCodes.DOMAIN_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#hasTgppImsiMccMnc()
   */
  public boolean hasTgppImsiMccMnc() {
    return hasAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#setDomainName(java.lang.String)
   */
  public void setDomainName(String domainName) {
    addAvp(DiameterRoAvpCodes.DOMAIN_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, domainName);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.AddressDomain#setTgppImsiMccMnc(byte[])
   */
  public void setTgppImsiMccMnc(byte[] tgppImsiMccMnc)
  {
    addAvp(DiameterRoAvpCodes.TGPP_IMSI_MCC_MNC, DiameterRoAvpCodes.TGPP_VENDOR_ID, tgppImsiMccMnc);
  }

}
