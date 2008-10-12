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
 * This class allows processed stack overloaded notification
 * @version 1.5.1 Final
 */

public interface OverloadListener {

    /**
     * Notifies this OverloadListener that the stack has overload.
     * @param peer listening peer
     * @param value value of overload
     */
    void overloadDetected(URI peer, double value);

    /**
     * Notifies this OverloadListener that the stack has overload cased
     * @param peer listening peer
     */
    void overloadCeased(URI peer);
}
