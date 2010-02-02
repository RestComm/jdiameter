package org.jdiameter.server.api;

import org.jdiameter.common.api.statistic.IStatistic;

/**
 * This interface describe extends methods of base class
 */
public interface IStateMachine extends org.jdiameter.client.api.fsm.IStateMachine {

  /**
   * Return statistic of state machine
   * 
   * @return statistic of state machine
   */
  IStatistic getStatistic();
}
