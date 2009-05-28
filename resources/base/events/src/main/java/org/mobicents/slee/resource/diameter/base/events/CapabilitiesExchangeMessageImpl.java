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
package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeMessage;
import net.java.slee.resource.diameter.base.events.avp.AddressAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;

/**
 * Start time:18:16:51 2009-05-22<br>
 * Project: diameter-parent<br>
 * Super class definnig common methods for CER and CEA. Implmenets methods {@link CapabilitiesExchangeMessage}
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see DiameterMessageImpl
 */
public abstract class CapabilitiesExchangeMessageImpl extends DiameterMessageImpl implements CapabilitiesExchangeMessage {

	/**
	 * 
	 * @param message
	 */
	public CapabilitiesExchangeMessageImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public long[] getAcctApplicationIds()
	{
		return super.getAvpsAsUInt32(Avp.ACCT_APPLICATION_ID);
	}

	public long[] getAuthApplicationIds()
	{
		return super.getAvpsAsUInt32(Avp.AUTH_APPLICATION_ID);
	}

	public long getFirmwareRevision()
	{
		return super.getAvpAsUInt32(Avp.FIRMWARE_REVISION);
	}

	public AddressAvp[] getHostIpAddresses()
	{
		return super.getAvpAsAddress(Avp.HOST_IP_ADDRESS);
	}

	public long[] getInbandSecurityIds()
	{
		return super.getAvpsAsUInt32(Avp.INBAND_SECURITY_ID);
	}

	public String getProductName()
	{
		return super.getAvpAsUtf8(Avp.PRODUCT_NAME);
	}

	public long[] getSupportedVendorIds()
	{
		return super.getAvpsAsUInt32(Avp.SUPPORTED_VENDOR_ID);
	}

	public long getVendorId()
	{
		return super.getAvpAsUInt32(Avp.VENDOR_ID);
	}

	public VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds()
	{
		AvpSet avps = super.message.getAvps().getAvps(Avp.VENDOR_SPECIFIC_APPLICATION_ID);
		if (avps == null)
			return null;

		VendorSpecificApplicationIdAvp[] r = new VendorSpecificApplicationIdAvp[avps.size()];
		Avp avp = null;
		for (int i = 0; i < avps.size(); i++) {
			// FIXME:baranowb ; setting prt to 0
			avp = avps.getAvpByIndex(i);
			try {
				r[i] = new VendorSpecificApplicationIdAvpImpl(Avp.VENDOR_SPECIFIC_APPLICATION_ID, avp.getVendorId(), avp.isMandatory() ? 1 : 0, 0, avp.getRaw());
			} catch (AvpDataException e) {

				e.printStackTrace();
				return null;
			}
		}
		return r;
	}

	public boolean hasFirmwareRevision()
	{
		return getFirmwareRevision() > 0;
	}

	public boolean hasProductName()
	{
		return getProductName() != null;
	}

	public boolean hasVendorId()
	{
		return getVendorId() > 0;
	}

	public void setAcctApplicationIds(long[] acctApplicationIds)
	{
	  for(long acctApplicationId : acctApplicationIds) {
		  addAvp(Avp.ACCT_APPLICATION_ID, acctApplicationId);
	  }
	}

	public void setAuthApplicationIds(long[] authApplicationIds)
	{
    for(long authApplicationId : authApplicationIds) {
      addAvp(Avp.AUTH_APPLICATION_ID, authApplicationId);
    }
	}

	public void setFirmwareRevision(long firmwareRevision)
	{
		addAvp(Avp.FIRMWARE_REVISION, firmwareRevision);
	}

	public void setHostIpAddress(AddressAvp hostIpAddress)
	{
    addAvp(Avp.HOST_IP_ADDRESS, hostIpAddress);
	}

	public void setHostIpAddresses(AddressAvp[] hostIpAddresses)
	{
	  for(AddressAvp hostIpAddress : hostIpAddresses) {
	    setHostIpAddress(hostIpAddress);
	  }
	}

	public void setInbandSecurityId(long inbandSecurityId)
	{
		addAvp(Avp.INBAND_SECURITY_ID, inbandSecurityId);
	}

	public void setInbandSecurityIds(long[] inbandSecurityIds)
	{
		for(long inbandSecurityId : inbandSecurityIds) {
		  setInbandSecurityId(inbandSecurityId);
		}
	}

	public void setProductName(String productName)
	{
		addAvp(Avp.PRODUCT_NAME, productName);
	}

	public void setSupportedVendorId(long supportedVendorId)
	{
    addAvp(Avp.SUPPORTED_VENDOR_ID, supportedVendorId);
	}

	public void setSupportedVendorIds(long[] supportedVendorIds)
	{
	  for(long supportedVendorId : supportedVendorIds) {
	    addAvp(Avp.SUPPORTED_VENDOR_ID, supportedVendorId);
	  }
	}

	public void setVendorId(long vendorId)
	{
		addAvp(Avp.VENDOR_ID, vendorId);
	}

	public void setVendorSpecificApplicationIds(VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds)
	{
	  for(VendorSpecificApplicationIdAvp vendorSpecificApplicationId : vendorSpecificApplicationIds) {
	    addAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, vendorSpecificApplicationId);
	  }
	}

	public AddressAvp getHostIpAddress()
	{
		if (hasAcctApplicationId()) {
			return super.getAvpAsAddress(Avp.HOST_IP_ADDRESS)[0];
		}
		else {
			return null;
		}
	}

	public long getInbandSecurityId() {
		if (hasAcctApplicationId()) {
			return super.getAvpAsUInt32(Avp.INBAND_SECURITY_ID);
		} else {
			return Long.MIN_VALUE;
		}
	}

	public boolean hasHostIpAddress() {
		return super.hasAvp(Avp.HOST_IP_ADDRESS);
	}

	public boolean hasInbandSecurityId() {
		return super.hasAvp(Avp.INBAND_SECURITY_ID);
	}

	public boolean hasSupportedVendorId() {
		return super.hasAvp(Avp.SUPPORTED_VENDOR_ID);
	}

}
