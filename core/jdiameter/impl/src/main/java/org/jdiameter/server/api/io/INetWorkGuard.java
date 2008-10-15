/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.api.io;

/**
 * This interface describe INetWorkConnectionListener consumer
 */
public interface INetWorkGuard {

    /**
     * Append new listener
     * @param listener listener instance
     */
    public void addListener(INetWorkConnectionListener listener);

    /**
     * Remove listener
     * @param listener listener instance
     */
    public void remListener(INetWorkConnectionListener listener);

    /**
     * Release all attached resources (socket and etc)
     */
    public void destroy();

}
