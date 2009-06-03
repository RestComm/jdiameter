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
package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre>
 * 17.7.18 MBMS-User-Data-Mode-Indication AVP
 * The MBMS-User-Data-Mode-Indication AVP (AVP code 915) is of type Enumerated. The meaning of the message containing this AVP depends on the sending entity. The absence of this AVP indicates unicast mode of operation.
 * 
 * The following values are supported:
 * Unicast (0)
 *   When BM-SC sends this value, that indicates to GGSN that BM-SC supports only unicast mode (IP multicast packets encapsulated by IP unicast header).
 *   When GGSN sends this value, that indicates to BM-SC that BM-SC shall send user plane data with unicast mode (IP multicast packets encapsulated by IP unicast header).
 * Multicast and Unicast (1)
 *   When BM-SC sends this value, that indicates to GGSN that BM-SC supports both modes of operation.
 *   When GGSN sends this value, that indicates to BM-SC that BM-SC shall send user plane data with multicast mode.
 * </pre>
 * 
 * Java class to represent the MBMS-2G-3G-Indicator enumerated type.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum MbmsUserDataModeIndication implements Enumerated {
  UNICAST(0), MULTICAST_AND_UNICAST(1);

  private int value = -1;

  private MbmsUserDataModeIndication(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  private Object readResolve() throws StreamCorruptedException {
    try {
      return fromInt(value);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: " + value);
    }
  }

  public static MbmsUserDataModeIndication fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return UNICAST;
    case 1:
      return MULTICAST_AND_UNICAST;

    default:
      throw new IllegalArgumentException();
    }
  }

}
