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
 * This interface is used to inform about changes in the state.
 * @version 1.5.1 Final
 */

public interface StateChangeListener {

    /**
     * A change of state has occured for a fsm.
     * @param oldState Old state of fsm
     * @param newState New state of fsm
     */
    void stateChanged(Enum oldState, Enum newState);
}
