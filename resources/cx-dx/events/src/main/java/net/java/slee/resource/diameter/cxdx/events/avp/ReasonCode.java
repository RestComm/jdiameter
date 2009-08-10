package net.java.slee.resource.diameter.cxdx.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre>
 * <b>6.3.17  Reason-Code AVP</b>
 * The Reason-Code AVP is of type Enumerated, and defines the reason for the network initiated 
 * de-registration. The following values are defined:
 * 
 *  PERMANENT_TERMINATION (0)
 *  NEW_SERVER_ASSIGNED (1)
 *  SERVER_CHANGE (2)
 *  REMOVE_S-CSCF (3)
 * 
 * </pre>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ReasonCode implements Enumerated, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int _PERMANENT_TERMINATION = 0;

  public static final int _NEW_SERVER_ASSIGNED = 1;

  public static final int _SERVER_CHANGE = 2;

  public static final int _REMOVE_S_CSCF = 3;

  public static final ReasonCode PERMANENT_TERMINATION = new ReasonCode(_PERMANENT_TERMINATION);

  public static final ReasonCode NEW_SERVER_ASSIGNED = new ReasonCode(_NEW_SERVER_ASSIGNED);

  public static final ReasonCode SERVER_CHANGE = new ReasonCode(_SERVER_CHANGE);

  public static final ReasonCode REMOVE_S_CSCF = new ReasonCode(_REMOVE_S_CSCF);

  private int value = -1;

  private ReasonCode(int value) {
    this.value = value;
  }

  public static ReasonCode fromInt(int type) {
    switch(type) {
    case _PERMANENT_TERMINATION: 
      return PERMANENT_TERMINATION;
    case _NEW_SERVER_ASSIGNED: 
      return NEW_SERVER_ASSIGNED;
    case _SERVER_CHANGE: 
      return SERVER_CHANGE;
    case _REMOVE_S_CSCF: 
      return REMOVE_S_CSCF;
    default: 
      throw new IllegalArgumentException("Invalid Reason-Code value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch(value) {
    case _PERMANENT_TERMINATION: 
      return "PERMANENT_TERMINATION";
    case _NEW_SERVER_ASSIGNED: 
      return "NEW_SERVER_ASSIGNED";
    case _SERVER_CHANGE: 
      return "SERVER_CHANGE";
    case _REMOVE_S_CSCF: 
      return "REMOVE_S_CSCF";
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
