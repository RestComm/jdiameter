/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

import java.util.Collection;
import java.util.List;

/**
 * This class implements counter of statistic
 * @version 1.5.1 Final
 */

public interface StatisticRecord {

    /**
     * Return name of counter
     * @return  name of counter
     */
    String getName();

    /**
     * Retrurn description of counter
     * @return description of counter
     */
    String getDescription();

    /**
     * Return value of counter as integer
     * @return value of counter
     */
    int getValueAsInt();

    /**
     *  Return value of counter as double
     * @return value of counter
     */
    double getValueAsDouble();

    /**
     *  Return value of counter as long
     * @return value of counter
     */
    long getValueAsLong();

    /**
     * Return childs counters
     * @return array of childs countres
     */
    StatisticRecord[] getChilds();

    /**
     * Reset counter and all child counters
     */
    void reset();
    
    /**
     * Enable/Disable counter
     *
     * @param e on/off parameter
     */
    public void enable(boolean e);
    
    public boolean isEnabled();
}
