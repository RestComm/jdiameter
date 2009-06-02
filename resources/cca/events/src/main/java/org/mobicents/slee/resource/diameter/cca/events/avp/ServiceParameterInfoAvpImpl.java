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
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:14:01 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link ServiceParameterInfoAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServiceParameterInfoAvpImpl extends GroupedAvpImpl implements ServiceParameterInfoAvp {

  public ServiceParameterInfoAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#getServiceParameterType()
   */
  public long getServiceParameterType() {
    return getAvpAsUnsigned32(CreditControlAVPCodes.Service_Parameter_Type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#getServiceParameterValue()
   */
  public byte[] getServiceParameterValue() {
    return getAvpAsRaw(CreditControlAVPCodes.Service_Parameter_Value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#hasServiceParameterType()
   */
  public boolean hasServiceParameterType() {
    return hasAvp(CreditControlAVPCodes.Service_Parameter_Type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#hasServiceParameterValue()
   */
  public boolean hasServiceParameterValue() {
    return hasAvp(CreditControlAVPCodes.Service_Parameter_Value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#setServiceParameterType(long)
   */
  public void setServiceParameterType(long serviceParameterType) {
    addAvp(CreditControlAVPCodes.Service_Parameter_Type, serviceParameterType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#setServiceParameterValue(byte[])
   */
  public void setServiceParameterValue(byte[] serviceParameterValue) {
    addAvp(CreditControlAVPCodes.Service_Parameter_Value, serviceParameterValue);
  }

}
