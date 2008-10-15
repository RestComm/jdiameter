/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.app;

import org.jdiameter.api.OverloadException;
import org.jdiameter.api.InternalException;

/**
 * The StateMachine lets you organize event handling,
 * if the order of the events are important to you.
 * @version 1.5.1 Final
 */

public interface StateMachine {

    /**
     * Add a new state change listener
     * @param listener a reference to the listener that will get information about state changes.
     */
    void addStateChangeNotification(StateChangeListener listener);

    /**
     * Remove a state change listener
     * @param listener a reference to the listener that will get information about state changes.
     */
    void removeStateChangeNotification(StateChangeListener listener);    

    /**
     * Handle an event in the current state.
     * @param event processing event
     * @return true if staterocessed
     * @throws OverloadException if queue of state mashine is full
     * @throws InternalException if FSM has internal error
     */
    boolean handleEvent(StateEvent event) throws InternalException, OverloadException;

    /**
     * Get the current state
     * @param stateType type of state
     * @return current state
     */
    <E> E getState(Class<E> stateType);  
}
