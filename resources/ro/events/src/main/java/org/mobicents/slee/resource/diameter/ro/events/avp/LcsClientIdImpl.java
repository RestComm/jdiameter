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

import net.java.slee.resource.diameter.ro.events.avp.LcsClientId;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientName;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientType;
import net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId;

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
  public String getLcsApn() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.LCS_APN, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientDialedByMs()
   */
  public String getLcsClientDialedByMs() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientExternalId()
   */
  public String getLcsClientExternalId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientName()
   */
  public LcsClientName getLcsClientName() {
    return (LcsClientName) getAvpAsCustom(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, LcsClientNameImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsClientType()
   */
  public LcsClientType getLcsClientType() {
    return (LcsClientType) getAvpAsEnumerated(DiameterRoAvpCodes.LCS_CLIENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, LcsClientType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#getLcsRequestorId()
   */
  public LcsRequestorId getLcsRequestorId() {
    return (LcsRequestorId) getAvpAsCustom(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, LcsRequestorIdImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsApn()
   */
  public boolean hasLcsApn() {
    return hasAvp(DiameterRoAvpCodes.LCS_APN, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientDialedByMs()
   */
  public boolean hasLcsClientDialedByMs() {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientExternalId()
   */
  public boolean hasLcsClientExternalId() {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientName()
   */
  public boolean hasLcsClientName() {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsClientType()
   */
  public boolean hasLcsClientType() {
    return hasAvp(DiameterRoAvpCodes.LCS_CLIENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#hasLcsRequestorId()
   */
  public boolean hasLcsRequestorId() {
    return hasAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsApn(java.lang.String)
   */
  public void setLcsApn( String lcsApn ) {
    addAvp(DiameterRoAvpCodes.LCS_APN, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsApn);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientDialedByMs(java.lang.String)
   */
  public void setLcsClientDialedByMs( String lcsClientDialedByMs ) {
    addAvp(DiameterRoAvpCodes.LCS_CLIENT_DIALED_BY_MS, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsClientDialedByMs);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientExternalId(java.lang.String)
   */
  public void setLcsClientExternalId( String lcsClientExternalId ) {
    addAvp(DiameterRoAvpCodes.LCS_CLIENT_EXTERNAL_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsClientExternalId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientName(net.java.slee.resource.diameter.ro.events.avp.LcsClientName)
   */
  public void setLcsClientName( LcsClientName lcsClientName ) {
    addAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsClientName.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsClientType(java.lang.String)
   */
  public void setLcsClientType( LcsClientType lcsClientType ) {
    addAvp(DiameterRoAvpCodes.LCS_CLIENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsClientType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsClientId#setLcsRequestorId(net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId)
   */
  public void setLcsRequestorId( LcsRequestorId lcsRequestorId ) {
    addAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsRequestorId.byteArrayValue());
  }

}
