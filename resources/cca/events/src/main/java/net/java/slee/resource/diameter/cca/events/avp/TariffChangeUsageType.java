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

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 *<pre> <b>8.27. Tariff-Change-Usage AVP</b>
 *
 *
 *   The Tariff-Change-Usage AVP (AVP Code 452) is of type Enumerated and
 *   defines whether units are used before or after a tariff change, or
 *   whether the units straddled a tariff change during the reporting
 *   period.  Omission of this AVP means that no tariff change has
 *   occurred.
 *
 *   In addition, when present in answer messages as part of the
 *   Multiple-Services-Credit-Control AVP, this AVP defines whether units
 *   are allocated to be used before or after a tariff change event.
 *
 *   When the Tariff-Time-Change AVP is present, omission of this AVP in
 *   answer messages means that the single quota mechanism applies.
 *
 *   Tariff-Change-Usage can be one of the following:
 *
 *   <b>UNIT_BEFORE_TARIFF_CHANGE       0</b>
 *      When present in the Multiple-Services-Credit-Control AVP, this
 *      value indicates the amount of the units allocated for use before a
 *      tariff change occurs.
 *      When present in the Used-Service-Unit AVP, this value indicates
 *      the amount of resource units used before a tariff change had
 *      occurred.
 *
 *   <b>UNIT_AFTER_TARIFF_CHANGE        1</b>
 *      When present in the Multiple-Services-Credit-Control AVP, this
 *      value indicates the amount of the units allocated for use after a
 *      tariff change occurs.
 *
 *      When present in the Used-Service-Unit AVP, this value indicates
 *      the amount of resource units used after tariff change had
 *      occurred.
 *
 *   <b>UNIT_INDETERMINATE              2</b>
 *      The used unit contains the amount of units that straddle the
 *      tariff change (e.g., the metering process reports to the credit-
 *      control client in blocks of n octets, and one block straddled the
 *      tariff change).  This value is to be used only in the Used-
 *      Service-Unit AVP.
 *      </pre>
 *  
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public enum TariffChangeUsageType implements Enumerated {

  UNIT_BEFORE_TARIFF_CHANGE(0),UNIT_AFTER_TARIFF_CHANGE(1),UNIT_INDETERMINATE(2); 

  private int value = -1;

  private TariffChangeUsageType(int value)
  {
    this.value=value;
  }

  private Object readResolve() throws StreamCorruptedException {
    try {
      return fromInt(value);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: " + value);
    }
  }

  public static TariffChangeUsageType fromInt(int presumableValue) throws IllegalArgumentException
  {
    switch (presumableValue) {
    case 0:
      return UNIT_BEFORE_TARIFF_CHANGE;
    case 1:
      return UNIT_AFTER_TARIFF_CHANGE;
    case 2:
      return UNIT_INDETERMINATE;

    default:
      throw new IllegalArgumentException();
    }
  }

  public int getValue() {
    return this.value;
  }

}
