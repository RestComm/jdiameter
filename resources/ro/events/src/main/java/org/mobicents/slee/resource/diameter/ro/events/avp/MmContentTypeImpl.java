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

import net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation;
import net.java.slee.resource.diameter.ro.events.avp.MmContentType;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * MmContentTypeImpl.java
 *
 * <br>Project:  mobicents
 * <br>9:16:09 AM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MmContentTypeImpl extends GroupedAvpImpl implements MmContentType {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MmContentTypeImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getAdditionalContentInformations()
   */
  public AdditionalContentInformation[] getAdditionalContentInformations() {
    return (AdditionalContentInformation[]) getAvpsAsCustom(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, AdditionalContentInformation.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getAdditionalTypeInformation()
   */
  public String getAdditionalTypeInformation() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getContentSize()
   */
  public long getContentSize() {
    return getAvpAsUnsigned32(DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#getTypeNumber()
   */
  public int getTypeNumber() {
    return getAvpAsInteger32(DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#hasAdditionalTypeInformation()
   */
  public boolean hasAdditionalTypeInformation() {
    return hasAvp( DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#hasContentSize()
   */
  public boolean hasContentSize() {
    return hasAvp( DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#hasTypeNumber()
   */
  public boolean hasTypeNumber() {
    return hasAvp( DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setAdditionalContentInformation(net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation)
   */
  public void setAdditionalContentInformation( AdditionalContentInformation additionalContentInformation ) {
    addAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, additionalContentInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setAdditionalContentInformations(net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation[])
   */
  public void setAdditionalContentInformations( AdditionalContentInformation[] additionalContentInformations ) {
    for(AdditionalContentInformation additionalContentInformation : additionalContentInformations) {
      setAdditionalContentInformation(additionalContentInformation);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setAdditionalTypeInformation(java.lang.String)
   */
  public void setAdditionalTypeInformation( String additionalTypeInformation ) {
    addAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, additionalTypeInformation);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setContentSize(long)
   */
  public void setContentSize( long contentSize ) {
    addAvp(DiameterRoAvpCodes.CONTENT_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID, contentSize);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmContentType#setTypeNumber(int)
   */
  public void setTypeNumber( int typeNumber ) {
    addAvp(DiameterRoAvpCodes.TYPE_NUMBER, DiameterRoAvpCodes.TGPP_VENDOR_ID, typeNumber);
  }

}
