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
 * Data: $Date: 2009/10/10 20:17:57 $
 * Revision: $Revision: 1.2 $
 *  
 * @version 1.5.0.1
 */
public interface IAssembler {
    /**
     * Return parent IOC
     * 
     * @return IOC instance
     */
    IAssembler getParent();

    /**
     * Return all children
     * 
     * @return all children
     */
    IAssembler[] getChilds();

    /**
     * Register new component
     * 
     * @param aClass class of component
     * @return instance of component
     */
    <T> T getComponentInstance(Class<T> aClass);

    /**
     * Register new component
     * 
     * @param object instance of component
     */
    void registerComponentInstance(Object object);

    /**
     * Register new component
     * 
     * @param aClass class of component
     * @param object instance of component
     */
    void registerComponentImplementation(Class<?> aClass, Object object);

    /**
     * Release all attached resources
     */
    void destroy();
}
