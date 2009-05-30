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
import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.jdiameter.api.Avp;
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

  public long[] getAcctApplicationIds() {
    return getAvpsAsUnsigned32(Avp.ACCT_APPLICATION_ID);
  }

  public long[] getAuthApplicationIds() {
    return getAvpsAsUnsigned32(Avp.AUTH_APPLICATION_ID);
  }

  public long getFirmwareRevision() {
    return getAvpAsUnsigned32(Avp.FIRMWARE_REVISION);
  }

  public Address[] getHostIpAddresses() {
    return getAvpsAsAddress(Avp.HOST_IP_ADDRESS);
  }

  public long[] getInbandSecurityIds() {
    return getAvpsAsUnsigned32(Avp.INBAND_SECURITY_ID);
  }

  public String getProductName() {
    return getAvpAsUTF8String(Avp.PRODUCT_NAME);
  }

  public long[] getSupportedVendorIds() {
    return getAvpsAsUnsigned32(Avp.SUPPORTED_VENDOR_ID);
  }

  public long getVendorId() {
    return getAvpAsUnsigned32(Avp.VENDOR_ID);
  }

  public VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds() {
    return (VendorSpecificApplicationIdAvp[]) getAvpsAsCustom(Avp.VENDOR_SPECIFIC_APPLICATION_ID, VendorSpecificApplicationIdAvpImpl.class);
  }

  public boolean hasFirmwareRevision() {
    return hasAvp(Avp.FIRMWARE_REVISION);
  }

  public boolean hasProductName() {
    return hasAvp(Avp.PRODUCT_NAME);
  }

  public boolean hasVendorId() {
    return hasAvp(Avp.VENDOR_ID);
  }

  public void setAcctApplicationIds(long[] acctApplicationIds) {
    for(long acctApplicationId : acctApplicationIds) {
      addAvp(Avp.ACCT_APPLICATION_ID, acctApplicationId);
    }
  }

  public void setAuthApplicationIds(long[] authApplicationIds) {
    for(long authApplicationId : authApplicationIds) {
      addAvp(Avp.AUTH_APPLICATION_ID, authApplicationId);
    }
  }

  public void setFirmwareRevision(long firmwareRevision) {
    addAvp(Avp.FIRMWARE_REVISION, firmwareRevision);
  }

  public void setHostIpAddress(Address hostIpAddress) {
    addAvp(Avp.HOST_IP_ADDRESS, hostIpAddress.encode());
  }

  public void setHostIpAddresses(Address[] hostIpAddresses) {
    for(Address hostIpAddress : hostIpAddresses) {
      setHostIpAddress(hostIpAddress);
    }
  }

  public void setInbandSecurityId(long inbandSecurityId) {
    addAvp(Avp.INBAND_SECURITY_ID, inbandSecurityId);
  }

  public void setInbandSecurityIds(long[] inbandSecurityIds) {
    for(long inbandSecurityId : inbandSecurityIds) {
      setInbandSecurityId(inbandSecurityId);
    }
  }

  public void setProductName(String productName) {
    addAvp(Avp.PRODUCT_NAME, productName);
  }

  public void setSupportedVendorId(long supportedVendorId) {
    addAvp(Avp.SUPPORTED_VENDOR_ID, supportedVendorId);
  }

  public void setSupportedVendorIds(long[] supportedVendorIds) {
    for(long supportedVendorId : supportedVendorIds) {
      addAvp(Avp.SUPPORTED_VENDOR_ID, supportedVendorId);
    }
  }

  public void setVendorId(long vendorId) {
    addAvp(Avp.VENDOR_ID, vendorId);
  }

  public void setVendorSpecificApplicationIds(VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds) {
    for(VendorSpecificApplicationIdAvp vendorSpecificApplicationId : vendorSpecificApplicationIds) {
      setVendorSpecificApplicationId(vendorSpecificApplicationId);
    }
  }

  public Address getHostIpAddress() {
    return getAvpAsAddress(Avp.HOST_IP_ADDRESS);
  }

  public long getInbandSecurityId() {
    return getAvpAsUnsigned32(Avp.INBAND_SECURITY_ID);
  }

  public boolean hasHostIpAddress() {
    return hasAvp(Avp.HOST_IP_ADDRESS);
  }

  public boolean hasInbandSecurityId() {
    return hasAvp(Avp.INBAND_SECURITY_ID);
  }

  public boolean hasSupportedVendorId() {
    return hasAvp(Avp.SUPPORTED_VENDOR_ID);
  }

}
