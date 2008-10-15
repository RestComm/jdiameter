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

/**
 *  This interface describe extends methods of base class
 */
public interface IMetaData extends org.jdiameter.client.api.IMetaData {

    /**
     * Add new Application Id to support application list
     * @param applicationId applicationId
     */
    public void addApplicationId(ApplicationId applicationId);

    /**
     * Remove Application id from support application list
     * @param applicationId applicationId
     */
    public void remApplicationId(ApplicationId applicationId);

    /**
     * @deprecated 
     * Reload parameters
     */
    public void reload();
}
