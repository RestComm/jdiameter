/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
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
		AvpSet set = super.avpSet.getAvps(DiameterAvpCodes.ACCT_APPLICATION_ID);
		if (set != null && set.size() > 0) {
			long[] returnValue = new long[set.size()];

			int counter = 0;
			for (Avp raw : set) {
				try {
					returnValue[counter++] = raw.getUnsigned32();
				} catch (AvpDataException e) {

					e.printStackTrace();
					return null;
				}
			}

			return returnValue;
		}
		return null;
	}

	public long[] getAuthApplicationIds() {
		AvpSet set = super.avpSet.getAvps(DiameterAvpCodes.AUTH_APPLICATION_ID);
		if (set != null && set.size() > 0) {
			long[] returnValue = new long[set.size()];

			int counter = 0;
			for (Avp raw : set) {
				try {
					returnValue[counter++] = raw.getUnsigned32();
				} catch (AvpDataException e) {

					e.printStackTrace();
					return null;
				}
			}

			return returnValue;
		}
		return null;
	}

	public VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds() {

		AvpSet set = super.avpSet.getAvps(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID);
		if (set != null && set.size() > 0) {
			VendorSpecificApplicationIdAvp[] returnValue = new VendorSpecificApplicationIdAvp[set.size()];

			int counter = 0;
			for (Avp raw : set) {
				try {
					VendorSpecificApplicationIdAvpImpl vsai = new VendorSpecificApplicationIdAvpImpl(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID, raw.getVendorId(), raw.isMandatory() ? 1 : 0, raw
							.isEncrypted() ? 1 : 0, raw.getRaw());
					returnValue[counter++] = vsai;
				} catch (AvpDataException e) {

					e.printStackTrace();
					return null;
				}
			}

			return returnValue;
		}
		return null;
	}

	public void setAcctApplicationId(long acctApplicationId) {
		super.avpSet.removeAvp(DiameterAvpCodes.ACCT_APPLICATION_ID);
		super.avpSet.addAvp(DiameterAvpCodes.ACCT_APPLICATION_ID, acctApplicationId, false);

	}

	public void setAcctApplicationIds(long[] acctApplicationIds) {
		super.avpSet.removeAvp(DiameterAvpCodes.ACCT_APPLICATION_ID);
		for (long acctApplicationId : acctApplicationIds)
			super.avpSet.addAvp(DiameterAvpCodes.ACCT_APPLICATION_ID, acctApplicationId, false);

	}

	public void setAuthApplicationId(long authApplicationId) {
		super.avpSet.removeAvp(DiameterAvpCodes.AUTH_APPLICATION_ID);
		super.avpSet.addAvp(DiameterAvpCodes.AUTH_APPLICATION_ID, authApplicationId, false);

	}

	public void setAuthApplicationIds(long[] authApplicationIds) {
		super.avpSet.removeAvp(DiameterAvpCodes.AUTH_APPLICATION_ID);
		for (long acctApplicationId : authApplicationIds)
			super.avpSet.addAvp(DiameterAvpCodes.AUTH_APPLICATION_ID, acctApplicationId, false);

	}

	public void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId) {
		super.avpSet.removeAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID);
		super.avpSet.addAvp(vendorSpecificApplicationId.getCode(), vendorSpecificApplicationId.byteArrayValue(), vendorSpecificApplicationId.getMandatoryRule() == 1, vendorSpecificApplicationId
				.getProtectedRule() == 1);

	}

	public void setVendorSpecificApplicationIds(VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds) {
		super.avpSet.removeAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID);
		for (VendorSpecificApplicationIdAvp vendorSpecificApplicationId : vendorSpecificApplicationIds)
			super.avpSet.addAvp(vendorSpecificApplicationId.getCode(), vendorSpecificApplicationId.byteArrayValue(), vendorSpecificApplicationId.getMandatoryRule() == 1, vendorSpecificApplicationId
					.getProtectedRule() == 1);
	}

}
