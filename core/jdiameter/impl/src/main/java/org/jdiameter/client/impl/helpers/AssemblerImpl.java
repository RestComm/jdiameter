/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors by the
 * @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jdiameter.client.impl.helpers;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.IAssembler;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.*;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensionName;
import static org.jdiameter.client.impl.helpers.Parameters.Extensions;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * IoC for stack
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
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
      String extName = e.getStringValue(ExtensionName.ordinal(), "");
      // Create structure of containers
      if (extName.equals(ExtensionPoint.Internal.name())) {
        fill(ExtensionPoint.Internal.getExtensionPoints(), e, true);
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
      Configuration[] internalConf = config.getChildren(Extensions.ordinal()); 
      String oldValue = internalConf[Internal.id()].getStringValue(e.ordinal(), null);
      String newValue = internalConf[pointType.id()].getStringValue(e.ordinal(), null);
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
    fill(p.getExtensionPoints(), e, false);
  }

  private void fill(ExtensionPoint[] codes, Configuration e, boolean check) throws Exception {
    for (ExtensionPoint c : codes) {
      String value = e.getStringValue(c.ordinal(), c.defValue());
      if (!check && (value == null || value.trim().length() == 0)) {
        return;
      }

      try {
        pico.registerComponentImplementation(Class.forName(value));
      }
      catch (NoClassDefFoundError exc) {
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
