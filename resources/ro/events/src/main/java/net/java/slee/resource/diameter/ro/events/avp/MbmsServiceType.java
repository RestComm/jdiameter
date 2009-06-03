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
 * 17.7.9 MBMS-Service-Type AVP
 * 
 * The MBMS-Service-Type AVP (AVP code 906) is of type Enumerated, and contains explicit information about the type of service that the BM-SC Start Procedure is about to start.
 * 
 * MULTICAST (0)
 *   The Start Procedure signalled by the BM-SC is for a Multicast Service.
 * BROADCAST (1)
 *   The Start Procedure signalled by the BM-SC is for a Broadcast Service.
 * </pre>
 * 
 * Java class to represent the MBMS-Service-Type enumerated type.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum MbmsServiceType implements Enumerated {
  MULTICAST(0), BROADCAST(1);

  private int value = -1;

  private MbmsServiceType(int value) {
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

  public static MbmsServiceType fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return MULTICAST;
    case 1:
      return BROADCAST;

    default:
      throw new IllegalArgumentException();
    }
  }

}
