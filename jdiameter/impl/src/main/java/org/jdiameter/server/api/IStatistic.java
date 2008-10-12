/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.api;

import org.jdiameter.api.Statistic;
import org.jdiameter.api.StatisticRecord;

import java.util.Set;

/**
 * This interface describe extends methods of base class
 */
public interface IStatistic extends Statistic {

    /**
     * Merge statistic
     * @param e external statistic
     */
    void appendCounter(Set<StatisticRecord> e);

}
