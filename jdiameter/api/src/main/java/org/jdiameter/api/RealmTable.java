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

import java.util.List;

/**
 * This interface introduces a capability to work with a network.
 * You can get instance of this interface over stack instance:
 * <code>
 * if (stack.isWrapperFor(RealmTable.class)) {
 *       RealmTable realmTabke = stack.unwrap(RealmTable.class);
 *       .....
 * }
 * </code>
 * @version 1.5.1 Final
 */

public interface RealmTable extends Wrapper {

    /**
     * Return different network statistics
     * @param realmName realmName
     * @return network statistics
     */
    Statistic getStatistic(String realmName);

    /**
     * Return realm entry
     * @param realmName realm name
     * @param applicationId application id associated with realm
     * @return realm entry
     */
    Realm getRealm(String realmName, ApplicationId applicationId);

    /**
     * Return no mutable list of elements realm table
     * @return list of elements realm table
     */
    List<Realm> getAllRealms();

    /**
     * Add new realm to realm table
     * @param realmName name of realm
     * @param applicationId application id of realm
     * @param action action of realm
     * @param dynamic commCode of realm
     * @param expirationTime expiration time of realm
     * @return instance of created realm
     */
    Realm addRealm(String realmName, ApplicationId applicationId, LocalAction action, boolean dynamic, long expirationTime);

    /**
     * Remove realm from realm table
     * @param realmName name of realm
     * @return realm
     */
    Realm removeRealm(String realmName);
}
