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
 *<pre> <b>8.35. Final-Unit-Action AVP</b>
 *
 *
 *   The Final-Unit-Action AVP (AVP Code 449) is of type Enumerated and
 *   indicates to the credit-control client the action to be taken when
 *   the user's account cannot cover the service cost.
 *
 *   The Final-Unit-Action can be one of the following:
 *
 *   <b>TERMINATE                       0</b>
 *      The credit-control client MUST terminate the service session.
 *      This is the default handling, applicable whenever the credit-
 *      control client receives an unsupported Final-Unit-Action value,
 *      and it MUST be supported by all the Diameter credit-control client
 *      implementations conforming to this specification.
 *
 *   <b>REDIRECT                        1</b>
 *      The service element MUST redirect the user to the address
 *      specified in the Redirect-Server-Address AVP.  The redirect action
 *      is defined in section 5.6.2.
 *
 *   <b>RESTRICT_ACCESS                 2</b>
 *      The access device MUST restrict the user access according to the
 *      IP packet filters defined in the Restriction-Filter-Rule AVP or
 *      according to the IP packet filters identified by the Filter-Id
 *      AVP.  All the packets not matching the filters MUST be dropped
 *      (see section 5.6.3).
 *      </pre>
 *  
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public enum FinalUnitActionType implements Enumerated {
  TERMINATE(0),REDIRECT(1),RESTRICT_ACCESS(2);

  private int value = -1;

  private FinalUnitActionType(int value)
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

  public static FinalUnitActionType fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return TERMINATE;
    case 1:
      return REDIRECT;
    case 2:
      return RESTRICT_ACCESS;

    default:
      throw new IllegalArgumentException();
    }
  }

  public int getValue() {
    return this.value;
  }

}
