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

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Realm;

import java.util.Set;

/**
 * This interface describe extends methods of base class
 */
public interface IRouter extends org.jdiameter.client.api.router.IRouter{

    /**
     * Add real to realm table
     * @param name name of realm
     * @param applicationId applicationId of realm
     * @param localAction local action of realm
     * @param dynamic on/off dynamic
     * @param expirationTime experation time of record
     * @param peers array of host names
     * @return Realm instance
     */
    Realm addRealm(String name, ApplicationId applicationId, LocalAction localAction, boolean dynamic, long expirationTime,  String... peers);

    /**
     * Remove realm
     * @param name name of realm
     * @return removed realm
     */
    Realm remRealm(String name);

    /**
     * Return set of realms
     * @return
     */
    Set<Realm> getRealms();

    /**
     * Set network instance
     * @param network network instance
     */
    void setNetWork(INetwork network);
}
