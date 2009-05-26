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
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:20:36:25 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link UserEquipmentInfoAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class UserEquipmentInfoAvpImpl extends GroupedAvpImpl implements UserEquipmentInfoAvp {

  public UserEquipmentInfoAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#getUserEquipmentInfoType()
   */
  public UserEquipmentInfoType getUserEquipmentInfoType() {
    int v = (Integer) getAvp(CreditControlAVPCodes.User_Equipment_Info_Type);
    return v != Integer.MIN_VALUE ? UserEquipmentInfoType.EUI64.fromInt(v) : null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#getUserEquipmentInfoValue()
   */
  public byte[] getUserEquipmentInfoValue() {
    return (byte[]) getAvp(CreditControlAVPCodes.User_Equipment_Info_Value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#hasUserEquipmentInfoType()
   */
  public boolean hasUserEquipmentInfoType() {
    return hasAvp(CreditControlAVPCodes.User_Equipment_Info_Type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#hasUserEquipmentInfoValue()
   */
  public boolean hasUserEquipmentInfoValue() {
    return hasAvp(CreditControlAVPCodes.User_Equipment_Info_Value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#setUserEquipmentInfoType
   * (net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoType)
   */
  public void setUserEquipmentInfoType(UserEquipmentInfoType type) {
    addAvp(CreditControlAVPCodes.User_Equipment_Info_Type, type.getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp#setUserEquipmentInfoValue(byte[])
   */
  public void setUserEquipmentInfoValue(byte[] value) {
    addAvp(CreditControlAVPCodes.User_Equipment_Info_Value, value);
  }

}
