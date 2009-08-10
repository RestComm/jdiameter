package net.java.slee.resource.diameter.cxdx.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre>
 * <b>6.3.24  User-Authorization-Type AVP</b>
 * The User-Authorization-Type AVP is of type Enumerated, and indicates the type of user 
 * authorization being performed in a User Authorization operation, i.e. UAR command. 
 * The following values are defined:
 * PERMANENT_TERMINATION (0)
 *   This value is used in case of the initial registration or re-registration. I-CSCF determines 
 *   this from the Expires field or expires parameter in Contact field in the SIP REGISTER method 
 *   if it is not equal to zero.
 *   
 *   This is the default value.
 * NEW_SERVER_ASSIGNED (1)
 *   This value is used in case of the de-registration. I-CSCF determines this from the Expires 
 *   field or expires parameter in Contact field in the SIP REGISTER method if it is equal to zero.
 * SERVER_CHANGE (2)
 *   This value is used in case of initial registration, re-registration or terminating SIP request and when the I-CSCF explicitly requests S-CSCF capability information from the HSS. The I-CSCF shall use this value when the user's current S-CSCF, which is stored in the HSS, cannot be contacted and a new S-CSCF needs to be selected
 * </pre>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class UserAuthorizationType implements Enumerated, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _REGISTRATION = 0;

  public static final int _DE_REGISTRATION = 1;

  public static final int _REGISTRATION_AND_CAPABILITIES = 2;

  public static final UserAuthorizationType REGISTRATION = new UserAuthorizationType(_REGISTRATION);

  public static final UserAuthorizationType DE_REGISTRATION = new UserAuthorizationType(_DE_REGISTRATION);

  public static final UserAuthorizationType REGISTRATION_AND_CAPABILITIES = new UserAuthorizationType(_REGISTRATION_AND_CAPABILITIES);

  private int value = -1;

  private UserAuthorizationType(int value) {
    this.value = value;
  }

  public static UserAuthorizationType fromInt(int type) {
    switch(type) {
    case _REGISTRATION: 
      return REGISTRATION;
    case _DE_REGISTRATION: 
      return DE_REGISTRATION;
    case _REGISTRATION_AND_CAPABILITIES: 
      return REGISTRATION_AND_CAPABILITIES;
    default: 
      throw new IllegalArgumentException("Invalid User-Authorization-Type value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _REGISTRATION: 
      return "REGISTRATION";
    case _DE_REGISTRATION: 
      return "DE_REGISTRATION";
    case _REGISTRATION_AND_CAPABILITIES: 
      return "REGISTRATION_AND_CAPABILITIES";
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
