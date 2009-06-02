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
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:14:12:35 2009-05-23<br>
 * Project: diameter-parent<br>
 * Super avp for avps of certain structure:
 * 
 * <pre>
 *                HEADER NAME ::= &lt; AVP Header: CODE &gt;
 *                                   { Unit-Value }
 *                                   [ Currency-Code ]
 * </pre>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MoneyLikeAvpImpl extends GroupedAvpImpl {

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MoneyLikeAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#getCurrencyCode()
   */
  public long getCurrencyCode() {
    return getAvpAsUnsigned32(CreditControlAVPCodes.Currency_Code);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#getUnitValue()
   */
  public UnitValueAvp getUnitValue() {
    return (UnitValueAvp) getAvpAsCustom(CreditControlAVPCodes.Unit_Value, UnitValueAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasCurrencyCode()
   */
  public boolean hasCurrencyCode() {
    return hasAvp(CreditControlAVPCodes.Currency_Code);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#hasUnitValue()
   */
  public boolean hasUnitValue() {
    return hasAvp(CreditControlAVPCodes.Unit_Value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setCurrencyCode(long)
   */
  public void setCurrencyCode(long code) {
    addAvp(CreditControlAVPCodes.Currency_Code, code);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp#setUnitValue(net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
   */
  public void setUnitValue(UnitValueAvp unitValue) {
    addAvp(CreditControlAVPCodes.Unit_Value, unitValue.byteArrayValue());
  }
}
