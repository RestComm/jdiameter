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

/**
 * The listener interface for receiving runtime configuration changes events.
 * @version 1.5.1 Final
 */

public interface ConfigurationListener {

    /**
     * Invoked when an changes is occurs.
     * @param key index of changed elemen
     * @param newValue new value
     * @return true if new value is applay
     */
    boolean elementChanged(int key, Object newValue);

}
