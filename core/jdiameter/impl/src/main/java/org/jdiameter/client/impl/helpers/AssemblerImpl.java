 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.client.impl.helpers;

import static org.jdiameter.client.impl.helpers.ExtensionPoint.ControllerLayer;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.Internal;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalElementParser;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalMessageParser;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalMetaData;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalPeerController;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalPeerFsmFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalRouterEngine;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalSessionFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalTransportFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.StackLayer;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.TransportLayer;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensionName;
import static org.jdiameter.client.impl.helpers.Parameters.Extensions;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.IAssembler;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

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
  final MutablePicoContainer pico = new PicoBuilder().withCaching().build();

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
      // TODO: server?
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
        pico.removeComponent(Class.forName(oldValue));
        pico.addComponent(Class.forName(newValue));
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
    //NOTE: this installs components, but no instances created!
    for (ExtensionPoint c : codes) {
      String value = e.getStringValue(c.ordinal(), c.defValue());
      if (!check && (value == null || value.trim().length() == 0)) {
        return;
      }

      try {
        pico.addComponent(Class.forName(value));
      }
      catch (NoClassDefFoundError exc) {
        throw new Exception(exc);
      }
    }
  }

  /**
   * @see org.picocontainer.MutablePicoContainer
   */
  @Override
  public <T> T getComponentInstance(Class<T> aClass) {
    return pico.getComponent(aClass);
  }

  /**
   * @see org.picocontainer.MutablePicoContainer
   */
  @Override
  public void registerComponentInstance(Object object) {
    pico.addComponent(object);
  }

  public void registerComponentImplementation(Class aClass) {
    pico.addComponent(aClass);
  }

  /**
   * @see org.picocontainer.MutablePicoContainer
   */
  @Override
  public void registerComponentImplementation(Class<?> aClass, Object object) {
    pico.addComponent(object, aClass);
  }

  public void unregister(Class aClass) {
    pico.removeComponent(aClass);
  }

  /**
   * @see org.picocontainer.MutablePicoContainer
   */
  @Override
  public void destroy() {
    pico.dispose();
  }

  /**
   * return parent IOC
   */
  @Override
  public IAssembler getParent() {
    return parent;
  }

  /**
   * Get childs IOCs
   *
   * @return childs IOCs
   */
  @Override
  public IAssembler[] getChilds() {
    return childs;
  }
}
