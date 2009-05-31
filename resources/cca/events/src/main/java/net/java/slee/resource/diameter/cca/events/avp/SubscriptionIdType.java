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
 *<pre> <b>8.47. Subscription-Id-Type AVP</b>
 *
 *
 *   The Subscription-Id-Type AVP (AVP Code 450) is of type Enumerated,
 *   and it is used to determine which type of identifier is carried by
 *   the Subscription-Id AVP.
 *
 *   This specification defines the following subscription identifiers.
 *   However, new Subscription-Id-Type values can be assigned by an IANA
 *   designated expert, as defined in section 12.  A server MUST implement
 *   all the Subscription-Id-Types required to perform credit
 *   authorization for the services it supports, including possible future
 *   values.  Unknown or unsupported Subscription-Id-Types MUST be treated
 *   according to the 'M' flag rule, as defined in [DIAMBASE].
 *
 *   <b>END_USER_E164                   0</b>
 *      The identifier is in international E.164 format (e.g., MSISDN),
 *      according to the ITU-T E.164 numbering plan defined in [E164] and
 *      [CE164].
 *
 *   <b>END_USER_IMSI                   1</b>
 *      The identifier is in international IMSI format, according to the
 *      ITU-T E.212 numbering plan as defined in [E212] and [CE212].
 *
 *   <b>END_USER_SIP_URI                2</b>
 *      The identifier is in the form of a SIP URI, as defined in [SIP].
 *
 *   <b>END_USER_NAI                    3</b>
 *      The identifier is in the form of a Network Access Identifier, as
 *      defined in [NAI].
 *
 *   <b>END_USER_PRIVATE                4</b>
 *      The Identifier is a credit-control server private identifier.
 *      </pre>
 *  
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public enum SubscriptionIdType implements Enumerated {

  END_USER_E164(0),END_USER_IMSI(1),END_USER_SIP_URI(2), END_USER_NAI(3), END_USER_PRIVATE(4);

  private int value = -1;

  private SubscriptionIdType(int value) {
    this.value = value;
  }

  private Object readResolve() throws StreamCorruptedException {
    try {
      return fromInt(value);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: " + value);
    }
  }

  public static SubscriptionIdType fromInt(int presumableValue) throws IllegalArgumentException
  {
    switch (presumableValue) {
    case 0:
      return END_USER_E164;
    case 1:
      return END_USER_IMSI;
    case 2:
      return END_USER_SIP_URI;
    case 3:
      return END_USER_NAI;
    case 4:
      return END_USER_PRIVATE;

    default:
      throw new IllegalArgumentException();
    }
  }

  public int getValue() {
    return this.value;
  }
}
