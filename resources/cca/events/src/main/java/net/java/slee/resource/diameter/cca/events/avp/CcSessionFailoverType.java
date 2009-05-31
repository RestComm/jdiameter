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
 * <pre> <b>8.4. CC-Session-Failover AVP</b>
 *
 *
 *   The CC-Session-Failover AVP (AVP Code 418) is type of Enumerated and
 *   contains information as to whether moving the credit-control message
 *   stream to a backup server during an ongoing credit-control session is
 *   supported.  In communication failures, the credit-control message
 *   streams can be moved to an alternative destination if the credit-
 *   control server supports failover to an alternative server.  The
 *   secondary credit-control server name, if received from the home
 *   Diameter AAA server, can be used as an address of the backup server.
 *   An implementation is not required to support moving a credit-control
 *   message stream to an alternative server, as this also requires moving
 *   information related to the credit-control session to backup server.
 *
 *   The following values are defined for the CC-Session-Failover AVP:
 *
 *   <b>FAILOVER_NOT_SUPPORTED          0</b>
 *      When the CC-Session-Failover AVP is set to FAILOVER_NOT_SUPPORTED,
 *      the credit-control message stream MUST NOT to be moved to an
 *      alternative destination in the case of communication failure.
 *
 *      This is the default behavior if the AVP isn't included in the
 *      reply from the authorization or credit-control server.
 *
 *   <b>FAILOVER_SUPPORTED              1</b>
 *      When the CC-Session-Failover AVP is set to FAILOVER_SUPPORTED, the
 *      credit-control message stream SHOULD be moved to an alternative
 *      destination in the case of communication failure.  Moving the
 *      credit-control message stream to a backup server MAY require that
 *      information related to the credit-control session should also be
 *      forwarded to alternative server.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public enum CcSessionFailoverType implements Enumerated {

  FAILOVER_NOT_SUPPORTED(0),FAILOVER_SUPPORTED(1);

  private int value = -1;

  private CcSessionFailoverType(int value) {
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

  public static CcSessionFailoverType fromInt(int type) throws IllegalArgumentException
  {
    switch (type) {
    case 0:
      return FAILOVER_NOT_SUPPORTED;
    case 1:
      return FAILOVER_SUPPORTED;

    default:
      throw new IllegalArgumentException();
    }
  }

}
