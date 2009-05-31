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
package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
/**
 * <pre><b>8.22. CC-Money AVP</b>
 *
 *
 *   The CC-Money AVP (AVP Code 413) is of type Grouped and specifies the
 *   monetary amount in the given currency.  The Currency-Code AVP SHOULD
 *   be included.  It is defined as follows (per the grouped-avp-def of
 *   RFC 3588 [DIAMBASE]):
 *
 *      CC-Money ::= < AVP Header: 413 >
 *                   { Unit-Value }
 *                   [ Currency-Code ]
 *   <pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface CcMoneyAvp extends GroupedAvp {

  /**
   * Sets value of Init-Value avp of type Grouped AVP.
   * See: {@link UnitValueAvp} .
   * @param unitValue
   */
  public void setUnitValue(UnitValueAvp unitValue);

  /**
   * Return value of Unit-Value avp. Return value of null indicates its not present.
   * See: {@link UnitValueAvp} .
   * @return
   */
  public UnitValueAvp getUnitValue();

  /**
   * Returns true if Unit-Value avp is present
   * @return
   */
  public boolean hasUnitValue();

  /**
   * Returns Currency-Code avp (Unsigned32) value in use, if present value is greater than 0.
   * @return
   */
  public long getCurrencyCode();

  /**
   * Sets Currency-Code avp of type Unsigned32
   * @param code
   */
  public void setCurrencyCode(long code);

  /**
   * Returns true if Currency-Code avp is present.
   * @return
   */
  public boolean hasCurrencyCode();

}
