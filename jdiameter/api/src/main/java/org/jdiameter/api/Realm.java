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
 * The Realm class implements rows in the Diameter Realm routing table.
 * @version 1.5.1 Final
 */

public abstract class Realm {

    protected String name;
    protected ApplicationId appId;
    protected LocalAction action;
    protected boolean dynamic;
    protected long expirationTime;

    protected Realm(String name, ApplicationId appId, LocalAction action, boolean dynamic, long expirationTime) {
        this.name = name;
        this.appId = appId;
        this.action = action;
        this.dynamic = dynamic;
        this.expirationTime = expirationTime;
    }

    /**
     * Return name of this realm
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Return applicationId associated with this realm
     * @return applicationId
     */
    public ApplicationId getApplicationId() {
        return appId;
    }

    /**
     * Return realm local action for this realm
     * @return realm local action
     */
    public LocalAction getLocalAction() {
        return action;
    }

    /**
     * Return list of real peers
     * @return array of realm peers
     */
    public abstract String[] getPeerHosts();

    /**
     * Append new host (peer) to this realm
     * @param host name of peer host
     */
    public abstract void addPeerName(String host);

    /**
     * Remove peer from this realm
     * @param host name of peer host
     */
    public abstract void removePeerName(String host);

    /**
     * Return true if this realm is dynamic updated
     * @return true if this realm is dynamic updated
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Return expiration time for this realm in milisec
     * @return expiration time
     */
    public long getExpirationTime() {
        return expirationTime;
    }
}
