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

/**
 * This class provide pluggable features
 */
public class ExtensionPoint extends org.jdiameter.client.impl.helpers.ExtensionPoint {

    /**
     * Network implementation class name
     */
    public static final ExtensionPoint InternalNetWork = new ExtensionPoint("InternalNetWork", "org.jdiameter.server.impl.NetworkImpl", true);

    /**
     * Overload manager implementation class name 
     */
    public static final ExtensionPoint InternalOverloadManager = new ExtensionPoint("InternalOverloadManager", "org.jdiameter.server.impl.OverloadManagerImpl", true);

    protected ExtensionPoint(String name, String defaultValue, boolean appendToInternal) {
        super(name, defaultValue);
        if (appendToInternal) {
            Internal.appendElements(this);
        }
    }

    protected ExtensionPoint(String name, org.jdiameter.client.impl.helpers.ExtensionPoint... parameters) {
        super(name, parameters);
    }

    protected ExtensionPoint(String name, int id, org.jdiameter.client.impl.helpers.ExtensionPoint... parameters) {
        super(name, id, parameters);
    }
}
