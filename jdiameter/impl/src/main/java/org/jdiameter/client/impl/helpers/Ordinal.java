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

import java.io.Serializable;

/**
 * This class provide ordinality properies to childs
 */
public abstract class Ordinal implements Serializable {

    protected String name;
    protected int ordinal;

    /**
     * Return name of element
     * @return name of element
     */
    public String name() {
	    return name;
    }

    /**
     * Return id of element
     * @return id of element
     */
    public int ordinal() {
	    return ordinal;
    }
}
