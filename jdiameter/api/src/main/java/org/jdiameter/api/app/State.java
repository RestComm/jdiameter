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

/**
 * This interface must be extended by any class that should implement a certain state in the state machine.
 * @version 1.5.1 Final
 */

public interface State {

    /**
     *  Action that should be taken each time this state is entered
     */
    void entryAction();

    /**
     * Action that should be taken each time this state is exited
     */
    void exitAction();

    /**
     * This method processed received event.
     * @param event the event to process.
     * @return true if event is processed
     */
    boolean processEvent(StateEvent event);
}
