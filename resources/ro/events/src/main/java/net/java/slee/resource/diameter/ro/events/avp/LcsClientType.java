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
 * The LCS-Client-Type AVP (AVP code 1241) is of type Enumerated and contains an estimate 
 * of the location of an MS in universal coordinates and the accuracy of the estimate.
 * 
 * It can be one of the following values:
 *  EMERGENCY_SERVICES          0
 *  VALUE_ADDED_SERVICES        1
 *  PLMN_OPERATOR_SERVICES      2
 *  LAWFUL_INTERCEPT_SERVICES   3
 * </pre>
 * 
 * Java class to represent the LCS-Client-Type enumerated type.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum LcsClientType implements Enumerated {
  EMERGENCY_SERVICES(0), VALUE_ADDED_SERVICES(1), PLMN_OPERATOR_SERVICES(3), LAWFUL_INTERCEPT_SERVICES(4);

  private int value = -1;

  private LcsClientType(int value) {
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

  public static LcsClientType fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return EMERGENCY_SERVICES;
    case 1:
      return VALUE_ADDED_SERVICES;
    case 2:
      return PLMN_OPERATOR_SERVICES;
    case 3:
      return LAWFUL_INTERCEPT_SERVICES;

    default:
      throw new IllegalArgumentException();

    }
  }
  
}
