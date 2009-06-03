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
 * 17.7.17  MBMS-Counting-Information AVP
 * The MBMS-Counting-Information AVP (AVP code 914) is of type Enumerated, and contains explicit information about whether the MBMS Counting procedures are applicable for the MBMS Service that is about to start. See 3GPP TS 25.346 [72].
 * This AVP is only valid for UTRAN access type.
 * 
 * The following values are supported:
 * COUNTING-NOT-APPLICABLE (0)
 *   The MBMS Session Start Procedure signalled by the BM-SC is for a MBMS Service where MBMS Counting procedures are not applicable.
 * COUNTING-APPLICABLE (1)
 *   The MBMS Session Start Procedure signalled by the BM-SC is for a MBMS Service where MBMS Counting procedures are applicable.
 * </pre>
 * 
 * Java class to represent the MBMS-2G-3G-Indicator enumerated type.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum MbmsCountingInformation implements Enumerated {
  COUNTING_NOT_APPLICABLE(0), COUNTING_APPLICABLE(1);

  private int value = -1;

  private MbmsCountingInformation(int value) {
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

  public static MbmsCountingInformation fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return COUNTING_NOT_APPLICABLE;
    case 1:
      return COUNTING_APPLICABLE;

    default:
      throw new IllegalArgumentException();
    }
  }

}
