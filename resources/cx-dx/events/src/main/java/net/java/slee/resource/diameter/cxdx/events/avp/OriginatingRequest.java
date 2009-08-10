package net.java.slee.resource.diameter.cxdx.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre>
 * <b>6.3.34 Originating-Request AVP</b>
 * The Originating-Request AVP is of type Enumerated and indicates to the HSS that the request is 
 * related to an AS originating SIP request in the Location-Information-Request operation. The 
 * following value is defined:
 * 
 *  ORIGINATING (0)
 *    This value informs the HSS that it should check originating unregistered services for the public identity.
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class OriginatingRequest implements Enumerated, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _ORIGINATING = 0;

  public static final OriginatingRequest ORIGINATING = new OriginatingRequest(_ORIGINATING);

  private int value = 0;

  private OriginatingRequest(int value) {
    this.value = value;
  }

  public static OriginatingRequest fromInt(int type) {
    switch(type) {
    case _ORIGINATING: 
      return ORIGINATING;
    default: 
      throw new IllegalArgumentException("Invalid Originating-Request value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _ORIGINATING: 
      return "ORIGINATING";
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
