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
 * This interface allows to control the stack at overload moment
 * @version 1.5.1 Final
 */

public interface OverloadManager {

    /**
     * Notify stack that parent application has overload
     * @param id Overloaded application id (null for stack owner)
     * @param type type of overload (CPU, Memory..)
     * @param value value of overload
     */
    void parentAppOverloadDetected(ApplicationId id, int type, double value);

    /**
     * Notifies stack that parent application has overload cased
     * @param id Overloaded application id (null for stack owner)
     * @param type type of overload (CPU, Memory..)
     */
    void parentAppOverloadCeased(ApplicationId id, int type);

    /**
     * Append overload listener
     * @param listener overload listener instance
     * @param lowThreshold low value of overload threshold (for example 0.8 - 1.0 is overload Range)
     * @param highThreshold high value of overload threshold (for example 0.8 - 1.0 is overload Range)
     * @param qIndex overload elenent index
     */
    void addOverloadListener(OverloadListener listener, double lowThreshold, double highThreshold, int qIndex);

    /**
     * Remove overload listener
     * @param listener overload listener instance
     * @param qIndex overload elenent index
     */
    void removeOverloadListener(OverloadListener listener, int qIndex);
}
