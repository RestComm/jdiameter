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

import java.util.Set;

/**
 * This class is conntainer for statistics information.
 * For example: Count Request/Answer messages
 * @version 1.5.1 Final
 */

public interface Statistic extends Wrapper {

    /**
     * Return name of statistic
     * @return  name of statistic
     */
    String getName();

    /**
     * Retrurn description of statistic
     * @return description of statistic
     */
    String getDescription();

    /**
     * Enable/Disable collecting statistics
     * @param value true for enable statistic
     */
    void enable(boolean value);

    /**
     * Return true is statistic is collecting
     * @return  true is statistic is collecting
     */
    boolean isEnable();

    /**
     * Reset all counter in statistic
     */
    void reset();

    /**
     * Return counters of statistics
     * @return counters of statistics
     */
    Set<StatisticRecord> getRecords();
}
