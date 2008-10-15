/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api;

import org.jdiameter.api.MetaData;

/**
 *  This interface describe extends methods of base class
 * Data: $Date: 2008/07/03 19:43:10 $
 * Revision: $Revision: 1.1 $
 * @version 1.5.0.1
 */
public interface IMetaData extends MetaData {

    /**
     * Set new value of host state
     */
    void updateLocalHostStateId();

    /**
     * Return host state value
     * @return host state value
     */
    int getLocalHostStateId();
}
