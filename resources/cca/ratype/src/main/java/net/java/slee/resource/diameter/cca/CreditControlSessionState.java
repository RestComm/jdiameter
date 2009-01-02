package net.java.slee.resource.diameter.cca;

import java.io.StreamCorruptedException;

/**
 * 
 * Enum representing Credit-Control FSM States.
 *
 * <br>Super project:  mobicents
 * <br>11:00:51 AM Dec 30, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public enum CreditControlSessionState {

  IDLE(0), PENDING_EVENT(1), PENDING_INITIAL(2), PENDING_UPDATE(3), PENDING_TERMINATION(4), PENDING_BUFFERED(5), OPEN(6), TERMINATED(7);

  private int value = -1;

  /**
   * Constructor.
   * @param val the value to be initialized with.
   */
  private CreditControlSessionState(int val)
  {
    this.value = val;
  }

  /**
   * Getter for the current FSM state as int.
   * @return an int corresponding to the actual FSM state.
   */
  public int getValue()
  {
    return this.value;
  }

  /**
   * Getter for the current FSM state as enum.
   * @return enum object corresponding to the actual FSM state.
   * @throws StreamCorruptedException
   */
  private Object readResolve() throws StreamCorruptedException
  {
    try
    {
      return fromInt(value);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: " + value);
    }
  }

  /**
   * Converts a given state value into it's equivalent.
   * @param value int to be converted
   * @return the corresponding CreditControlSessionState enum value
   * @throws IllegalArgumentException if value is not applicable for this enum
   */
  public CreditControlSessionState fromInt(int value) throws IllegalArgumentException
  {
    switch (value)
    {
    case 0:
      return IDLE;
    case 1:
      return PENDING_EVENT;
    case 2:
      return PENDING_INITIAL;
    case 3:
      return PENDING_UPDATE;
    case 4:
      return PENDING_TERMINATION;
    case 5:
      return PENDING_BUFFERED;
    case 6:
      return OPEN;
    case 7:
      return TERMINATED;

    default:
      throw new IllegalArgumentException("Unknown value for Credit-Control Session State: " + value);
    }
  }

}
