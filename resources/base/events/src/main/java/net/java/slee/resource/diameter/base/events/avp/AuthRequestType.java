/*
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Java class to represent the AuthRequestType enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * The Auth-Request-Type AVP (AVP Code 274) is of type Enumerated and is included in application-specific auth requests to inform the peers whether a user is to be authenticated only, authorized only or both. Note any value other than both MAY cause RADIUS interoperability issues. 
 *
 * @author Open Cloud
 */
public class AuthRequestType implements Serializable, Enumerated {

  private static final long serialVersionUID = 1L;

  public static final int _AUTHENTICATE_ONLY = 1;
  public static final int _AUTHORIZE_ONLY = 2;
  public static final int _AUTHORIZE_AUTHENTICATE = 3;

  /**
   * The request being sent is for authentication only, and MUST contain the relevant application specific authentication AVPs that are needed by the Diameter server to authenticate the user. 
   */
  public static final AuthRequestType AUTHENTICATE_ONLY = new AuthRequestType(_AUTHENTICATE_ONLY);

  /**
   * The request being sent is for authorization only, and MUST contain the application specific authorization AVPs that are necessary to identify the service being requested/offered. 
   */
  public static final AuthRequestType AUTHORIZE_ONLY = new AuthRequestType(_AUTHORIZE_ONLY);

  /**
   * The request contains a request for both authentication and authorization. The request MUST include both the relevant application specific authentication information, and authorization information necessary to identify the service being requested/offered. 
   */
  public static final AuthRequestType AUTHORIZE_AUTHENTICATE = new AuthRequestType(_AUTHORIZE_AUTHENTICATE);

  private AuthRequestType(int value) {
    this.value = value;
  }

  public static AuthRequestType fromInt(int type) {
    switch(type) {
    case _AUTHENTICATE_ONLY: return AUTHENTICATE_ONLY;
    case _AUTHORIZE_ONLY: return AUTHORIZE_ONLY;
    case _AUTHORIZE_AUTHENTICATE: return AUTHORIZE_AUTHENTICATE;
    default: throw new IllegalArgumentException("Invalid AuthRequestType value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _AUTHENTICATE_ONLY: return "AUTHENTICATE_ONLY";
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
