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

import java.util.Date;

import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * TimeStampsImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:10:40 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TimeStampsImpl extends GroupedAvpImpl implements TimeStamps {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public TimeStampsImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#getSipRequestTimestamp()
   */
  public Date getSipRequestTimestamp() {
    return getAvpAsTime(DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#getSipResponseTimestamp()
   */
  public Date getSipResponseTimestamp() {
    return getAvpAsTime(DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#hasSipRequestTimestamp()
   */
  public boolean hasSipRequestTimestamp() {
    return hasAvp( DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#hasSipResponseTimestamp()
   */
  public boolean hasSipResponseTimestamp() {
    return hasAvp( DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#setSipRequestTimestamp(java.util.Date)
   */
  public void setSipRequestTimestamp( Date sipRequestTimestamp ) {
    addAvp(DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID, sipRequestTimestamp);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#setSipResponseTimestamp(java.util.Date)
   */
  public void setSipResponseTimestamp( Date sipResponseTimestamp ) {
    addAvp(DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID, sipResponseTimestamp);
  }

}
