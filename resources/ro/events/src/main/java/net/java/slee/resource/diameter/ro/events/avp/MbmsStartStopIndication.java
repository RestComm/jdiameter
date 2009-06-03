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
 * The MBMS-StartStop-Indication AVP (AVP code 902) is of type Enumerated. The following values are supported:
 * 
 * START (0)
 *   The message containing this AVP is indicating an MBMS session start procedure.
 * STOP  (1) 
 *   The message containing this AVP is indicating an MBMS session stop procedure.
 * UPDATE (2)
 *   The message containing this AVP is indicating an MBMS session update procedure.
 * </pre>
 * 
 * Java class to represent the MBMS-StartStop-Indication enumerated type.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum MbmsStartStopIndication implements Enumerated {
  START(0), STOP(1), UPDATE(2);

  private int value = -1;

  private MbmsStartStopIndication(int value) {
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

  public static MbmsStartStopIndication fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return START;
    case 1:
      return STOP;
    case 2:
      return UPDATE;

    default:
      throw new IllegalArgumentException();
    }
  }

}
