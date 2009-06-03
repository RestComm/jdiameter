package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the Adaptations enumerated type.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Adaptations implements Enumerated, java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _NO = 1;
  public static final int _YES = 0;

  public static final net.java.slee.resource.diameter.ro.events.avp.Adaptations NO=new Adaptations(_NO);
  public static final net.java.slee.resource.diameter.ro.events.avp.Adaptations YES=new Adaptations(_YES);

  private Adaptations(int v)
  {
    value = v;
  }

  /**
   * Return the value of this instance of this enumerated type.
   */
  public static Adaptations fromInt(int type) {
    switch(type) {
    case _NO: return NO;
    case _YES: return YES;

    default: throw new IllegalArgumentException("Invalid DisconnectCause value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _NO: return "NO";
    case _YES: return "YES";
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

  private int value=0;

}
