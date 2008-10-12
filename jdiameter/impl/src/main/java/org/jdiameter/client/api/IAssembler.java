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

/**
 * This interface provide IOC functionality
 * Data: $Date: 2008/07/03 19:43:10 $
 * Revision: $Revision: 1.1 $
 * @version 1.5.0.1
 */
public interface IAssembler {
    /**
     * Return parent IOC
     * @return IOC instance
     */
    IAssembler getParent();

    /**
     * Return all children
     * @return all children
     */
    IAssembler[] getChilds();

    /**
     * Register new component
     * @param aClass class of component
     * @return instcne of component
     */
    Object getComponentInstance(Class<?> aClass);

    /**
     * Register new component
     * @param object instance of component
     */
    void registerComponentInstance(Object object);

    /**
     * Register new component
     * @param aClass class of component
     * @param object instance of component
     */
    void registerComponentImplementation(Class<?> aClass, Object object);

    /**
     * Release all attached resources
     */
    void destroy();
}
