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
package org.mobicents.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvp;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;

/**
 * 
 * Start time:15:52:05 2009-05-23<br>
 * Project: diameter-parent<br>
 * Implementation of AVP: {@link SupportedApplicationsAvp} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SupportedApplicationsAvpImpl extends GroupedAvpImpl implements SupportedApplicationsAvp {

  public SupportedApplicationsAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  public long[] getAcctApplicationIds() {
    return getAvpsAsUnsigned32(DiameterAvpCodes.ACCT_APPLICATION_ID);
  }

  public long[] getAuthApplicationIds() {
    return getAvpsAsUnsigned32(DiameterAvpCodes.AUTH_APPLICATION_ID);
  }

  public VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds() {
    return (VendorSpecificApplicationIdAvp[]) getAvpsAsCustom(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID, VendorSpecificApplicationIdAvpImpl.class);
  }

  public void setAcctApplicationId(long acctApplicationId) {
    addAvp(DiameterAvpCodes.ACCT_APPLICATION_ID, acctApplicationId);
  }

  public void setAcctApplicationIds(long[] acctApplicationIds) {
    for (long acctApplicationId : acctApplicationIds) {
      setAcctApplicationId(acctApplicationId);
    }
  }

  public void setAuthApplicationId(long authApplicationId) {
    addAvp(DiameterAvpCodes.AUTH_APPLICATION_ID, authApplicationId);
  }

  public void setAuthApplicationIds(long[] authApplicationIds) {
    for (long authApplicationId : authApplicationIds) {
      setAuthApplicationId(authApplicationId);
    }
  }

  public void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId) {
    addAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID, vendorSpecificApplicationId.byteArrayValue());
  }

  public void setVendorSpecificApplicationIds(VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds) {
    for (VendorSpecificApplicationIdAvp vendorSpecificApplicationId : vendorSpecificApplicationIds) {
      setVendorSpecificApplicationId(vendorSpecificApplicationId);
    }
  }

}
