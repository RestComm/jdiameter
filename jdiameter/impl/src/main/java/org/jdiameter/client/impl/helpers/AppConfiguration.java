/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

package org.jdiameter.client.impl.helpers;

import org.jdiameter.api.Configuration;

/**
 * This interface provide methods for change configuration object
 */
public interface AppConfiguration extends Configuration {

    /**
     * Add elements to configuration
     * @param e elements identifier
     * @param value array of elements
     * @return instance of configuration
     */
    public AppConfiguration add(Ordinal e, Configuration... value);

    /**
     *
     * @param e element identifier
     * @param value parameter value
     * @return instance of configuration
     */
    public AppConfiguration add(Ordinal e, Object value);
}
