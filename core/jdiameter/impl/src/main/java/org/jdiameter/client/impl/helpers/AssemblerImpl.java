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
import org.jdiameter.client.api.IAssembler;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.*;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensioinName;
import static org.jdiameter.client.impl.helpers.Parameters.Extensions;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * IOC for stack
 */
public class AssemblerImpl implements IAssembler {

    AssemblerImpl parent;
    final AssemblerImpl[] childs = new AssemblerImpl[ExtensionPoint.COUNT];
    final  MutablePicoContainer pico = new DefaultPicoContainer();

    /**
     * Create instance of class with predefined configuration
     * 
     * @param config configuration of stack
     * @throws Exception if generated internal exception
     */
    public AssemblerImpl(Configuration config) throws Exception {
        Configuration[] ext = config.getChildren(Extensions.ordinal());
        for (Configuration e : ext) {
            String extName = e.getStringValue(ExtensioinName.ordinal(), "");
            // Create structure of containers
            if (extName.equals(ExtensionPoint.Internal.name())) {
                fill(ExtensionPoint.Internal.getArrayOfParameters(), e, true);
            }
            else if (extName.equals(ExtensionPoint.StackLayer.name())) {
                updatePicoContainer(config, StackLayer, InternalMetaData, InternalSessionFactory, InternalMessageParser, InternalElementParser);
            }
            else if (extName.equals(ExtensionPoint.ControllerLayer.name())) {
                updatePicoContainer(config, ControllerLayer, InternalPeerController, InternalPeerFsmFactory, InternalRouterEngine);
            }
            else if (extName.equals(ExtensionPoint.TransportLayer.name())) {
                updatePicoContainer(config, TransportLayer, InternalTransportFactory);
            }
        }
    }

    private void updatePicoContainer(Configuration config, ExtensionPoint pointType, ExtensionPoint... updEntries) throws ClassNotFoundException {
        for (ExtensionPoint e : updEntries) {
            String oldValue = config.getChildren(Extensions.ordinal())[Internal.id()].getStringValue(e.ordinal(), null);
            String newValue = config.getChildren(Extensions.ordinal())[pointType.id()].getStringValue(e.ordinal(), null);
            if (oldValue != null && newValue != null) {
                pico.unregisterComponent(Class.forName(oldValue));
                pico.registerComponentImplementation(Class.forName(newValue));
            }
        }
    }

    /**
     * Create child Assembler
     * 
     * @param parent parent assembler
     * @param e child configuration
     * @param p extension poit
     * @throws Exception
     */
    protected AssemblerImpl(AssemblerImpl parent, Configuration e, ExtensionPoint p) throws Exception {
        this.parent = parent;
        fill(p.getArrayOfParameters(), e, false);
    }

    private void fill(ExtensionPoint[] codes, Configuration e, boolean check) throws Exception {
        for (ExtensionPoint c : codes) {
            String value = e.getStringValue(c.ordinal(), c.defValue());
            if (!check && (value == null || value.trim().length() == 0))
                return;
            try {
                pico.registerComponentImplementation(Class.forName(value));
            } catch (NoClassDefFoundError exc) {
                throw new Exception(exc);
            }
        }
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    public <T> T getComponentInstance(Class<T> aClass) {
        return (T) pico.getComponentInstanceOfType(aClass);
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    public void registerComponentInstance(Object object) {
        pico.registerComponentInstance(object);
    }

    public void registerComponentImplementation(Class aClass) {
         pico.registerComponentImplementation(aClass);
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    public void registerComponentImplementation(Class<?> aClass, Object object) {
        pico.registerComponentImplementation(object, aClass);
    }

    public void unregister(Class aClass) {
        pico.unregisterComponent(aClass);
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    public void destroy() {
        pico.dispose();
    }

    /**
     * return parent IOC
     */
    public IAssembler getParent() {
        return parent;
    }

    /**
     * Get childs IOCs
     * 
     * @return childs IOCs
     */
    public IAssembler[] getChilds() {
        return childs;
    }
}
