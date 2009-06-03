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

import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * TrunkGroupIdImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:52:51 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TrunkGroupIdImpl extends GroupedAvpImpl implements TrunkGroupId {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public TrunkGroupIdImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#getIncomingTrunkGroupId()
   */
  public String getIncomingTrunkGroupId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#getOutgoingTrunkGroupId()
   */
  public String getOutgoingTrunkGroupId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#hasIncomingTrunkGroupId()
   */
  public boolean hasIncomingTrunkGroupId() {
    return hasAvp( DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#hasOutgoingTrunkGroupId()
   */
  public boolean hasOutgoingTrunkGroupId() {
    return hasAvp( DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#setIncomingTrunkGroupId(java.lang.String)
   */
  public void setIncomingTrunkGroupId( String incomingTrunkGroupId ) {
    addAvp(DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, incomingTrunkGroupId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#setOutgoingTrunkGroupId(java.lang.String)
   */
  public void setOutgoingTrunkGroupId( String outgoingTrunkGroupId ) {
    addAvp(DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, outgoingTrunkGroupId);
  }

}
