/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.fsm;

import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.common.api.statistic.IStatistic;

/**
 * This interface extends StateMachine interface
 * 
 */
public interface IStateMachine extends StateMachine {

    /**
     * This method returns occupancy of event queue
     * @return occupancy of event queue
     */
    double getQueueInfo();

    void remStateChangeNotification(StateChangeListener listener);
    
    public IStatistic getStatistic();
}
