package net.java.slee.resource.diameter.cxdx.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre>
 * <b>6.3.51  Multiple-Registration-Indication</b>
 * The Multiple-Registration-Indication AVP is of type Enumerated and indicates to the HSS whether
 * or not the request is related to a multiple registration. The following values are defined:
 * 
 * NOT_MULTIPLE_REGISTRATION (0)
 * 
 * MULTIPLE_REGISTRATION (1)
 * </pre>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MultipleRegistrationIndication implements Enumerated, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _NOT_MULTIPLE_REGISTRATION = 0;

  public static final int _MULTIPLE_REGISTRATION = 1;

  public static final MultipleRegistrationIndication NOT_MULTIPLE_REGISTRATION = new MultipleRegistrationIndication(_NOT_MULTIPLE_REGISTRATION);

  public static final MultipleRegistrationIndication MULTIPLE_REGISTRATION = new MultipleRegistrationIndication(_MULTIPLE_REGISTRATION);

  private int value = -1;

  private MultipleRegistrationIndication(int value) {
    this.value = value;
  }

  public static MultipleRegistrationIndication fromInt(int type) {
    switch(type) {
    case _NOT_MULTIPLE_REGISTRATION: 
      return NOT_MULTIPLE_REGISTRATION;
    case _MULTIPLE_REGISTRATION: 
      return MULTIPLE_REGISTRATION;
    default: 
      throw new IllegalArgumentException("Invalid User-Authorization-Type value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _NOT_MULTIPLE_REGISTRATION: 
      return "NOT_MULTIPLE_REGISTRATION";
    case _MULTIPLE_REGISTRATION: 
      return "MULTIPLE_REGISTRATION";
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
