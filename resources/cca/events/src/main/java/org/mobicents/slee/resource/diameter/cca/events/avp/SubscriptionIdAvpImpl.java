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
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:25:13 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link SubscriptionIdAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscriptionIdAvpImpl extends GroupedAvpImpl implements SubscriptionIdAvp {

  public SubscriptionIdAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#getSubscriptionIdData()
   */
  public String getSubscriptionIdData() {
    return getAvpAsOctetString(CreditControlAVPCodes.Subscription_Id_Data);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#getSubscriptionIdType()
   */
  public SubscriptionIdType getSubscriptionIdType() {
    return (SubscriptionIdType) getAvpAsEnumerated(CreditControlAVPCodes.Subscription_Id_Type, SubscriptionIdType.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#hasSubscriptionIdData()
   */
  public boolean hasSubscriptionIdData() {
    return hasAvp(CreditControlAVPCodes.Subscription_Id_Data);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#hasSubscriptionIdType()
   */
  public boolean hasSubscriptionIdType() {
    return hasAvp(CreditControlAVPCodes.Subscription_Id_Type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#setSubscriptionIdData(java.lang.String)
   */
  public void setSubscriptionIdData(String data) {
    addAvp(CreditControlAVPCodes.Subscription_Id_Data, data);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#setSubscriptionIdType(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType)
   */
  public void setSubscriptionIdType(SubscriptionIdType type) {
    addAvp(CreditControlAVPCodes.Subscription_Id_Type, (long)type.getValue());
  }

}
