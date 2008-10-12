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

import org.jdiameter.api.StatisticRecord;

/**
 * This interface describe extends methods of base class
 */
public interface IStatisticRecord extends StatisticRecord {

    /**
     * Increment counter
     */
    public void inc();

    /**
     * Decrement counter
     */
    public void dec();

    /**
     * Enable/Disable counter
     * @param e on/off parameter
     */
    public void enable(boolean e);
}
