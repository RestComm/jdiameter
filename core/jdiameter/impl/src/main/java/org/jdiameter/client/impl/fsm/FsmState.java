package org.jdiameter.client.impl.fsm;

import org.jdiameter.api.PeerState;

public class FsmState {

  protected static int index;

  public static FsmState OKAY = new FsmState("OKAY", PeerState.OKAY);
  public static FsmState SUSPECT = new FsmState("SUSPECT", PeerState.SUSPECT);
  public static FsmState DOWN = new FsmState("DOWN", PeerState.DOWN);
  public static FsmState REOPEN = new FsmState("REOPEN", PeerState.REOPEN);
  public static FsmState INITIAL = new FsmState("INITIAL", PeerState.INITIAL);
  public static FsmState STOPPING = new FsmState("STOPPING", PeerState.DOWN, true);

  private String name;
  private int ordinal;

  private Enum publicState;
  private boolean isInternal;

  public FsmState(String name, Enum publicState) {
    this.name = name;
    this.publicState = publicState;
    this.ordinal = index++;
  }

  public FsmState(String name, Enum publicState, boolean isInternal) {
    this.name = name;
    this.publicState = publicState;
    this.isInternal = isInternal;
    this.ordinal = index++;
  }

  public int ordinal() {
    return ordinal;
  }

  public String name() {
    return name;
  }

  public Enum getPublicState() {
    return publicState;
  }

  public boolean isInternal() {
    return isInternal;
  }


  public String toString() {
    return name;
  }
}