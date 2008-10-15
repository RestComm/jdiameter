/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.impl.helpers;

public class Loggers extends org.jdiameter.client.impl.helpers.Loggers{

    /**
     * Logs for neteork operations
     */
    public static final Loggers NetWork = new Loggers("NetWork", "netWork","Logs the NetWork watcher");

    public Loggers(String name, String fullName, String desc) {
        super(name, fullName, desc);
    }
}
