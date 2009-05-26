/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
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
package net.java.slee.resource.diameter.base.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Java class to represent the ReAuthRequestType enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Re-Auth-Request-Type AVP (AVP Code 285) is of type Enumerated and is included in application-specific auth answers to inform the client of the action expected upon expiration of the Authorization-Lifetime. If the answer message contains an Authorization-Lifetime AVP with a positive value, the Re-Auth-Request-Type AVP MUST be present in an answer message. 
 *
 * @author Open Cloud
 */
public class ReAuthRequestType implements Serializable, Enumerated {

  private static final long serialVersionUID = 1L;
  
  public static final int _AUTHORIZE_ONLY = 0;
  public static final int _AUTHORIZE_AUTHENTICATE = 1;

  /**
   * An authorization only re-auth is expected upon expiration of the Authorization-Lifetime. This is the default value if the AVP is not present in answer messages that include the Authorization- Lifetime. 
   */
  public static final ReAuthRequestType AUTHORIZE_ONLY = new ReAuthRequestType(_AUTHORIZE_ONLY);

  /**
   * An authentication and authorization re-auth is expected upon expiration of the Authorization-Lifetime. 
   */
  public static final ReAuthRequestType AUTHORIZE_AUTHENTICATE = new ReAuthRequestType(_AUTHORIZE_AUTHENTICATE);

  private ReAuthRequestType(int value) {
    this.value = value;
  }

  public static ReAuthRequestType fromInt(int type) {
    switch(type) {
    case _AUTHORIZE_ONLY: return AUTHORIZE_ONLY;
    case _AUTHORIZE_AUTHENTICATE: return AUTHORIZE_AUTHENTICATE;
    default: throw new IllegalArgumentException("Invalid ReAuthRequestType value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _AUTHORIZE_ONLY: return "AUTHORIZE_ONLY";
    case _AUTHORIZE_AUTHENTICATE: return "AUTHORIZE_AUTHENTICATE";
    default: return "<Invalid Value>";
    }
  }

  private Object readResolve() throws StreamCorruptedException {
    try {
      return fromInt(value);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: " + value);
    }
  }

  private int value;
}
