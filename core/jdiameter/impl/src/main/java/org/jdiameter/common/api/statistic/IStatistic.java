/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.statistic;

import org.jdiameter.api.Statistic;

/**
 * This interface describe extends methods of base class
 */
public interface IStatistic extends Statistic{

	
	
  enum Groups {
    Peer("Peer statistic"),
    PeerFSM("Peer FSM statistic"),
    Network("Network statistic"),
    Concurrent(" Concurrent factory statistics"),
    ScheduledExecService("ScheduledExecutorService statistic");

    private String description;

    Groups(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }


  /**
   * Merge statistic
   *
   * @param rec external statistic
   */
  public void appendCounter(IStatisticRecord... rec);

  public IStatisticRecord getRecordByName(String name);

  public IStatisticRecord getRecordByName(IStatisticRecord.Counters name);

}
