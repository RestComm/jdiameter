package net.java.slee.resource.diameter.cxdx.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre>
 * <b>6.3.15  Server-Assignment-Type AVP</b>
 * The Server-Assignment-Type AVP is of type Enumerated, and indicates the type of server update
 * being performed in a Server-Assignment-Request operation. The following values are defined:
 * 
 * NO_ASSIGNMENT (0)
 *   This value is used to request from HSS the user profile assigned to one or more public
 *   identities and to retrieve the S-CSCF restoration information for a registered Public User 
 *   Identity, without affecting the registration state of those identities.
 * REGISTRATION (1)
 *   The request is generated as a consequence of a first registration of an identity.
 * RE_REGISTRATION (2)
 *   The request corresponds to the re-registration of an identity or update of the S-CSCF 
 *   Restoration Information.
 * UNREGISTERED_USER (3)
 *   The request is generated because the S-CSCF received an INVITE for a public identity that is 
 *   not registered.
 * TIMEOUT_DEREGISTRATION (4)
 *   The SIP registration timer of an identity has expired.
 * USER_DEREGISTRATION (5)
 *   The S-CSCF has received a user initiated de-registration request.
 * TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME (6)
 *   The SIP registration timer of an identity has expired. The S-CSCF keeps the user data stored
 *   in the S-CSCF and requests HSS to store the S-CSCF name.
 * USER_DEREGISTRATION_STORE_SERVER_NAME (7)
 *   The S-CSCF has received a user initiated de-registration request. The S-CSCF keeps the user 
 *   data stored in the S-CSCF and requests HSS to store the S-CSCF name.
 * ADMINISTRATIVE_DEREGISTRATION (8)
 *   The S-CSCF, due to administrative reasons, has performed the de-registration of an identity.
 * AUTHENTICATION_FAILURE (9)
 *   The authentication of a user has failed.
 * AUTHENTICATION_TIMEOUT (10)
 *   The authentication timeout has occurred.
 * DEREGISTRATION_TOO_MUCH_DATA (11)
 *   The S-CSCF has requested user profile information from the HSS and has received a volume of 
 *   data higher than it can accept.
 * AAA_USER_DATA_REQUEST (12)
 *   Used in the SWx protocol, defined in 3GPP TS 29.273 [18]. This value is not used in the Cx 
 *   protocol.
 * PGW_UPDATE (13)
 *   Used in the SWx protocol, defined in 3GPP TS 29.273 [18]. This value is not used in the Cx 
 *   protocol.
 * </pre>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerAssignmentType implements Enumerated, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _NO_ASSIGNMENT = 0;

  public static final int _REGISTRATION = 1;

  public static final int _RE_REGISTRATION = 2;

  public static final int _UNREGISTERED_USER = 3;

  public static final int _TIMEOUT_DEREGISTRATION = 4;

  public static final int _USER_DEREGISTRATION = 5;

  public static final int _TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME = 6;

  public static final int _USER_DEREGISTRATION_STORE_SERVER_NAME = 7;

  public static final int _ADMINISTRATIVE_DEREGISTRATION = 8;

  public static final int _AUTHENTICATION_FAILURE = 9;

  public static final int _AUTHENTICATION_TIMEOUT = 10;

  public static final int _DEREGISTRATION_TOO_MUCH_DATA = 11;

  public static final int _AAA_USER_DATA_REQUEST = 12;

  public static final int _PGW_UPDATE = 13;

  public static final ServerAssignmentType NO_ASSIGNMENT = new ServerAssignmentType(_NO_ASSIGNMENT);

  public static final ServerAssignmentType REGISTRATION = new ServerAssignmentType(_REGISTRATION);

  public static final ServerAssignmentType RE_REGISTRATION = new ServerAssignmentType(_RE_REGISTRATION);

  public static final ServerAssignmentType UNREGISTERED_USER = new ServerAssignmentType(_UNREGISTERED_USER);

  public static final ServerAssignmentType TIMEOUT_DEREGISTRATION = new ServerAssignmentType(_TIMEOUT_DEREGISTRATION);

  public static final ServerAssignmentType USER_DEREGISTRATION = new ServerAssignmentType(_USER_DEREGISTRATION);

  public static final ServerAssignmentType TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME = new ServerAssignmentType(_TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME);

  public static final ServerAssignmentType USER_DEREGISTRATION_STORE_SERVER_NAME = new ServerAssignmentType(_USER_DEREGISTRATION_STORE_SERVER_NAME);

  public static final ServerAssignmentType ADMINISTRATIVE_DEREGISTRATION = new ServerAssignmentType(_ADMINISTRATIVE_DEREGISTRATION);

  public static final ServerAssignmentType AUTHENTICATION_FAILURE = new ServerAssignmentType(_AUTHENTICATION_FAILURE);

  public static final ServerAssignmentType AUTHENTICATION_TIMEOUT = new ServerAssignmentType(_AUTHENTICATION_TIMEOUT);

  public static final ServerAssignmentType DEREGISTRATION_TOO_MUCH_DATA = new ServerAssignmentType(_DEREGISTRATION_TOO_MUCH_DATA);

  public static final ServerAssignmentType AAA_USER_DATA_REQUEST = new ServerAssignmentType(_AAA_USER_DATA_REQUEST);

  public static final ServerAssignmentType PGW_UPDATE = new ServerAssignmentType(_PGW_UPDATE);

  private int value = -1;

  private ServerAssignmentType(int value) {
    this.value = value;
  }

  public static ServerAssignmentType fromInt(int type) {
    switch(type) {
    case _NO_ASSIGNMENT: 
      return NO_ASSIGNMENT;
    case _REGISTRATION: 
      return REGISTRATION;
    case _RE_REGISTRATION: 
      return RE_REGISTRATION;
    case _UNREGISTERED_USER: 
      return UNREGISTERED_USER;
    case _TIMEOUT_DEREGISTRATION: 
      return TIMEOUT_DEREGISTRATION;
    case _USER_DEREGISTRATION: 
      return USER_DEREGISTRATION;
    case _TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME: 
      return TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME;
    case _USER_DEREGISTRATION_STORE_SERVER_NAME: 
      return USER_DEREGISTRATION_STORE_SERVER_NAME;
    case _ADMINISTRATIVE_DEREGISTRATION: 
      return ADMINISTRATIVE_DEREGISTRATION;
    case _AUTHENTICATION_FAILURE: 
      return AUTHENTICATION_FAILURE;
    case _AUTHENTICATION_TIMEOUT: 
      return AUTHENTICATION_TIMEOUT;
    case _DEREGISTRATION_TOO_MUCH_DATA: 
      return DEREGISTRATION_TOO_MUCH_DATA;
    case _AAA_USER_DATA_REQUEST: 
      return AAA_USER_DATA_REQUEST;
    case _PGW_UPDATE: 
      return PGW_UPDATE;
    default: 
      throw new IllegalArgumentException("Invalid User-Authorization-Type value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _NO_ASSIGNMENT: 
      return "NO_ASSIGNMENT";
    case _REGISTRATION: 
      return "REGISTRATION";
    case _RE_REGISTRATION: 
      return "RE_REGISTRATION";
    case _UNREGISTERED_USER: 
      return "UNREGISTERED_USER";
    case _TIMEOUT_DEREGISTRATION: 
      return "TIMEOUT_DEREGISTRATION";
    case _USER_DEREGISTRATION: 
      return "USER_DEREGISTRATION";
    case _TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME: 
      return "TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME";
    case _USER_DEREGISTRATION_STORE_SERVER_NAME: 
      return "USER_DEREGISTRATION_STORE_SERVER_NAME";
    case _ADMINISTRATIVE_DEREGISTRATION: 
      return "ADMINISTRATIVE_DEREGISTRATION";
    case _AUTHENTICATION_FAILURE: 
      return "AUTHENTICATION_FAILURE";
    case _AUTHENTICATION_TIMEOUT: 
      return "AUTHENTICATION_TIMEOUT";
    case _DEREGISTRATION_TOO_MUCH_DATA: 
      return "DEREGISTRATION_TOO_MUCH_DATA";
    case _AAA_USER_DATA_REQUEST: 
      return "AAA_USER_DATA_REQUEST";
    case _PGW_UPDATE: 
      return "PGW_UPDATE";
    default: 
      return "<Invalid Value>";
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

}
