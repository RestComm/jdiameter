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
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:17:27:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link RedirectServerAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RedirectServerAvpImpl extends GroupedAvpImpl implements RedirectServerAvp {

  public RedirectServerAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#getRedirectAddressType()
   */
  public RedirectAddressType getRedirectAddressType() {
    return (RedirectAddressType) getAvpAsEnumerated(CreditControlAVPCodes.Redirect_Address_Type, RedirectAddressType.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
   * getRedirectServerAddress()
   */
  public String getRedirectServerAddress() {
    return getAvpAsUTF8String(CreditControlAVPCodes.Redirect_Server_Address);
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
   * hasRedirectAddressType()
   */
  public boolean hasRedirectAddressType() {
    return hasAvp(CreditControlAVPCodes.Redirect_Address_Type);
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
   * hasRedirectServerAddress()
   */
  public boolean hasRedirectServerAddress() {
    return hasAvp(CreditControlAVPCodes.Redirect_Server_Address);
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
   * setRedirectAddressType
   * (net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType)
   */
  public void setRedirectAddressType(RedirectAddressType redirectAddressType) {
    addAvp(CreditControlAVPCodes.Redirect_Address_Type, (long)redirectAddressType.getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
   * setRedirectServerAddress(java.lang.String)
   */
  public void setRedirectServerAddress(String redirectServerAddress) {
    addAvp(CreditControlAVPCodes.Redirect_Server_Address, redirectServerAddress);
  }

}
