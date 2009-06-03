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

import java.util.Date;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:14:32:32 2009-05-23<br>
 * Project: diameter-parent<br>
 * Super class for avps of structure similar to:
 * <pre>
 *           HDR NAME   ::= < AVP Header: 431 >
 *                          [ Tariff-Time-Change ]
 *                          [ CC-Time ]
 *                          [ CC-Money ]
 *                          [ CC-Total-Octets ]
 *                          [ CC-Input-Octets ]
 *                          [ CC-Output-Octets ]
 *                          [ CC-Service-Specific-Units ]
 *                         *[ AVP ]
 *
 * </pre>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see RequestedServiceUnitAvpImpl
 * @see GrantedServiceUnitAvpImpl
 * @see UsedServiceUnitAvpImpl
 */
public class ServiceUnitAvpTypeImpl extends GroupedAvpImpl {

  /**
   * 	
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public ServiceUnitAvpTypeImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlInputOctets()
   */
  public long getCreditControlInputOctets() {
    return getAvpAsUnsigned64(CreditControlAVPCodes.CC_Input_Octets);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlMoneyAvp()
   */
  public CcMoneyAvp getCreditControlMoneyAvp()
  {
    return (CcMoneyAvp) getAvpAsCustom(CreditControlAVPCodes.CC_Money, CcMoneyAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlOutputOctets()
   */
  public long getCreditControlOutputOctets()
  {
    return getAvpAsUnsigned64(CreditControlAVPCodes.CC_Output_Octets);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlServiceSpecificUnits()
   */
  public long getCreditControlServiceSpecificUnits()
  {
    return getAvpAsUnsigned64(CreditControlAVPCodes.CC_Service_Specific_Units);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTime()
   */
  public long getCreditControlTime()
  {
    return getAvpAsUnsigned32(CreditControlAVPCodes.CC_Time);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTotalOctets()
   */
  public long getCreditControlTotalOctets()
  {
    return getAvpAsUnsigned64(CreditControlAVPCodes.CC_Total_Octets);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getTariffTimeChange()
   */
  public Date getTariffTimeChange()
  {
    return getAvpAsTime(CreditControlAVPCodes.Tariff_Time_Change);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlInputOctets()
   */
  public boolean hasCreditControlInputOctets()
  {
    return hasAvp(CreditControlAVPCodes.CC_Input_Octets);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlMoneyAvp()
   */
  public boolean hasCreditControlMoneyAvp()
  {
    return hasAvp(CreditControlAVPCodes.CC_Money);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlOutputOctets()
   */
  public boolean hasCreditControlOutputOctets()
  {
    return hasAvp(CreditControlAVPCodes.CC_Output_Octets);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlServiceSpecificUnits()
   */
  public boolean hasCreditControlServiceSpecificUnits()
  {
    return hasAvp(CreditControlAVPCodes.CC_Service_Specific_Units);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTime()
   */
  public boolean hasCreditControlTime()
  {
    return hasAvp(CreditControlAVPCodes.CC_Time);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTotalOctets()
   */
  public boolean hasCreditControlTotalOctets()
  {
    return hasAvp(CreditControlAVPCodes.CC_Total_Octets);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasTariffTimeChange()
   */
  public boolean hasTariffTimeChange()
  {
    return hasAvp(CreditControlAVPCodes.Tariff_Time_Change);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlInputOctets(long)
   */
  public void setCreditControlInputOctets(long ttc)
  {
    addAvp(CreditControlAVPCodes.CC_Input_Octets, ttc);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlMoneyAvp(net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp)
   */
  public void setCreditControlMoneyAvp(CcMoneyAvp ccm)
  {
    addAvp(CreditControlAVPCodes.CC_Money, ccm.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlOutputOctets(long)
   */
  public void setCreditControlOutputOctets(long ccoo)
  {
    addAvp(CreditControlAVPCodes.CC_Output_Octets, ccoo);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlServiceSpecificUnits(long)
   */
  public void setCreditControlServiceSpecificUnits(long ccssu)
  {
    addAvp(CreditControlAVPCodes.CC_Service_Specific_Units, ccssu);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTime(long)
   */
  public void setCreditControlTime(long cct)
  {
    addAvp(CreditControlAVPCodes.CC_Time, cct);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTotalOctets(long)
   */
  public void setCreditControlTotalOctets(long ccto)
  {
    addAvp(CreditControlAVPCodes.CC_Total_Octets, ccto);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setTariffTimeChange(java.util.Date)
   */
  public void setTariffTimeChange(Date ttc)
  {
    addAvp(CreditControlAVPCodes.Tariff_Time_Change	, ttc);
  }

}
