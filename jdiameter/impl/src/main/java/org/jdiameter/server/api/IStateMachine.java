package org.jdiameter.server.api;

import org.jdiameter.api.Statistic;

/**
 * This interface describe extends methods of base class
 */
public interface IStateMachine extends org.jdiameter.client.api.fsm.IStateMachine {

    /**
     * Return statistic of state machine
     * @return statistic of state machine
     */
    Statistic getStatistic();
}
