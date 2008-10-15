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
 * The Event class holds information about the different events that can be handled
 * by the state machine. Events are prioritized depending on the importance of the event.
 * The priority model tries to ensure that old messages are handled before any new ones.
 * @version 1.5.1 Final
 */

public interface StateEvent extends Comparable {

    /**
     * This method should be adapted by any subclass 
     * to return the type corresponding to the actual event.
     * @return type of this StateEvent
     */
    public <E> E encodeType(Class<E> enumType);

    /**
     * Return type of this StateEvent
     * @return type of this StateEvent
     */
    public Enum getType();

    /**
     * Returns a negative value if the priority for this object
     * is higher than the priority for the supplied object.
     * @param obj the Event to compare to.
     * @return compare result
     */

    /**
     * Set information object to this StateEvent
     * @param data information object
     */
    public void setData(Object data);

    /**
     * Return information object of this StateEvent
     * @return information object of this StateEvent
     */
    public Object getData();

}
