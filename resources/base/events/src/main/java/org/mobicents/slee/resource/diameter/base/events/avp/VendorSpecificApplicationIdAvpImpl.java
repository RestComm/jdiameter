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
package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import org.jdiameter.api.Avp;

/**
 * 
 * <br>Project: mobicents-diameter-server
 * <br>3:12:41 PM May 25, 2009 
 * <br>
 *
 * Implemntation of {@link VendorSpecificApplicationIdAvp} interface
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class VendorSpecificApplicationIdAvpImpl extends GroupedAvpImpl implements VendorSpecificApplicationIdAvp {

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public VendorSpecificApplicationIdAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#getVendorIds()
   */
  public long[] getVendorIds() {
    return getAllAvpAsUInt32(Avp.VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#setVendorId(long)
   */
  public void setVendorId(long vendorId) {
    addAvp(Avp.VENDOR_ID, vendorId);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#setVendorIds(long[])
   */
  public void setVendorIds(long[] vendorIds) {
    for (long vendorId : vendorIds) {
      setVendorId(vendorId);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#hasAuthApplicationId()
   */
  public boolean hasAuthApplicationId() {
    return hasAvp(Avp.AUTH_APPLICATION_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#getAuthApplicationId()
   */
  public long getAuthApplicationId() {
    return (Long) getAvp(Avp.AUTH_APPLICATION_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#setAuthApplicationId(long)
   */
  public void setAuthApplicationId(long authApplicationId) {
    addAvp(Avp.AUTH_APPLICATION_ID, authApplicationId);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#hasAcctApplicationId()
   */
  public boolean hasAcctApplicationId() {
    return hasAvp(Avp.ACCT_APPLICATION_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#getAcctApplicationId()
   */
  public long getAcctApplicationId() {
    return (Long) getAvp(Avp.ACCT_APPLICATION_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp#setAcctApplicationId(long)
   */
  public void setAcctApplicationId(long acctApplicationId) {
    addAvp(Avp.ACCT_APPLICATION_ID, acctApplicationId);
  }
}
