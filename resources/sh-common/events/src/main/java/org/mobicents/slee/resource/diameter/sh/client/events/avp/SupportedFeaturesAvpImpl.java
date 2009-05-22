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

import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * SupportedFeaturesAvpImpl.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 5:34:33 PM Dec 18, 2008 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public class SupportedFeaturesAvpImpl extends GroupedAvpImpl implements SupportedFeaturesAvp {

	public SupportedFeaturesAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	public long getFeatureList() {
		if (hasFeatureList())
			return super.getAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST);
		else
			return -1;
	}

	public long getFeatureListId() {
		if (hasFeatureListId())
			return super.getAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST_ID);
		else
			return -1;
	}

	public boolean hasFeatureList() {
		return super.avpSet.getAvp(DiameterShAvpCodes.FEATURE_LIST) != null;
	}

	public boolean hasFeatureListId() {
		return super.avpSet.getAvp(DiameterShAvpCodes.FEATURE_LIST_ID) != null;
	}

	public void setFeatureList(long featureList) {
		super.setAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST, featureList, true);
	}

	public void setFeatureListId(long featureListId) {
		super.setAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST_ID, featureListId, true);
	}

	public boolean hasVendorId() {
		return super.avpSet.getAvp(Avp.VENDOR_ID) != null;
	}

	public void setVendorId(long vendorId) {
		if (hasVendorId()) {
			throw new IllegalStateException("Unable to set Vendor-Id AVP, it is already present in this message.");
		}

		super.setAvpAsUInt32(Avp.VENDOR_ID, vendorId, true);
	}
	
}
