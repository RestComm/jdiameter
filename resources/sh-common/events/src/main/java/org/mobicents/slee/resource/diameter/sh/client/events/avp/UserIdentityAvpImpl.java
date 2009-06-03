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
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * Implementation of AVP: {@link UserIdentityAvp} UserIdentityAvp.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UserIdentityAvpImpl extends GroupedAvpImpl implements UserIdentityAvp {

  public UserIdentityAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  public String getMsisdn() {
    return getAvpAsUTF8String(DiameterShAvpCodes.MSISDN, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public String getPublicIdentity() {
    return getAvpAsUTF8String(DiameterShAvpCodes.PUBLIC_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasMsisdn() {
    return hasAvp(DiameterShAvpCodes.MSISDN, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasPublicIdentity() {
    return hasAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public void setMsisdn(String msisdn) {
    addAvp(DiameterShAvpCodes.MSISDN, DiameterShAvpCodes.SH_VENDOR_ID, msisdn);
  }

  public void setPublicIdentity(String publicIdentity) {
    addAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, publicIdentity);
  }

}
