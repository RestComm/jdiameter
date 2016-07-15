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

package org.jdiameter.server.impl.helpers;

import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalAgentConfiguration;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalAgentProxy;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalAgentRedirect;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalConnectionClass;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalElementParser;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalMessageParser;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalMetaData;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalPeerController;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalPeerFsmFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalRealmController;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalRouterEngine;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalSessionDatasource;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalSessionFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalStatisticFactory;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalTimerFacility;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.InternalTransportFactory;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensionName;
import static org.jdiameter.client.impl.helpers.Parameters.Extensions;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.InternalNetWork;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.InternalNetworkGuard;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.InternalOverloadManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdiameter.api.ConfigurationListener;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.client.impl.helpers.ExtensionPoint;

/**
 * This class allow create configuration class for stack
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class EmptyConfiguration extends org.jdiameter.client.impl.helpers.EmptyConfiguration implements MutableConfiguration {

  private final ConcurrentHashMap<Integer, List<ConfigurationListener>> listeners = new ConcurrentHashMap<Integer, List<ConfigurationListener>>();

  protected EmptyConfiguration() {
    this(true);
  }

  /**
   * Create instance of class
   *
   * @param callInit true if need append default parameters
   */
  public EmptyConfiguration(boolean callInit) {
    if (callInit) {
      add(Extensions, getInstance(). // Internal extension point
          add(ExtensionName, ExtensionPoint.Internal.name()).
          add(InternalMetaData, "org.jdiameter.server.impl.MetaDataImpl").
          add(InternalMessageParser, InternalMessageParser.defValue()).
          add(InternalElementParser, InternalElementParser.defValue()).
          add(InternalTransportFactory, "org.jdiameter.server.impl.io.TransportLayerFactory").
          add(InternalConnectionClass, InternalConnectionClass.defValue()).
          add(InternalNetworkGuard, InternalNetworkGuard.defValue()).
          add(InternalPeerFsmFactory, "org.jdiameter.server.impl.fsm.FsmFactoryImpl").
          add(InternalSessionFactory, InternalSessionFactory.defValue()).
          add(InternalRouterEngine, "org.jdiameter.server.impl.RouterImpl").
          add(InternalNetWork, "org.jdiameter.server.impl.NetworkImpl").
          add(InternalStatisticFactory, InternalStatisticFactory.defValue()).
          add(InternalOverloadManager, "org.jdiameter.server.impl.OverloadManagerImpl").
          add(InternalRealmController, InternalRealmController.defValue()).
          add(InternalAgentRedirect, InternalAgentRedirect.defValue()).
          add(InternalAgentConfiguration, InternalAgentConfiguration.defValue()).
          add(InternalAgentProxy, InternalAgentProxy.defValue()).
          add(InternalSessionDatasource, InternalSessionDatasource.defValue()).
          add(InternalTimerFacility, InternalTimerFacility.defValue()).
          add(InternalPeerController, "org.jdiameter.server.impl.MutablePeerTableImpl"),
          getInstance().  // StackLayer extension point
          add(ExtensionName, ExtensionPoint.StackLayer.name()),
          getInstance().  // ControllerLayer extension point
          add(ExtensionName, ExtensionPoint.ControllerLayer.name()),
          getInstance().  // TransportLayer extension point
          add(ExtensionName, ExtensionPoint.TransportLayer.name())
          );
    }
  }
  //

  @Override
  public void setByteValue(int key, byte value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setIntValue(int key, int value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setLongValue(int key, long value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setDoubleValue(int key, double value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setByteArrayValue(int key, byte[] value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setBooleanValue(int key, boolean value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setStringValue(int key, java.lang.String value) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, value);
      }
      if (commit) {
        putValue(key, value);
      }
    }
    else {
      putValue(key, value);
    }
  }

  @Override
  public void setChildren(int key, org.jdiameter.api.Configuration... values) {
    List<ConfigurationListener> list = listeners.get(key);
    if (list != null)  {
      boolean commit = true;
      for (ConfigurationListener l : list) {
        commit &= l.elementChanged(key, values);
      }
      if (commit) {
        putValue(key, values);
      }
      // Removed due to issue #1009 (http://code.google.com/p/mobicents/issues/detail?id=1009)
      // putValue(key, new EmptyConfiguration(false).add(key, values));
    }
    else {
      putValue(key, values);
      // Removed due to issue #1009 (http://code.google.com/p/mobicents/issues/detail?id=1009)
      // putValue(key, new EmptyConfiguration(false).add(key, values));
    }
  }

  @Override
  public void removeValue(int... keys) {
    for (int i:keys) {
      List<ConfigurationListener> list = listeners.get(i);
      if (list != null) {
        boolean rem = true;
        for (ConfigurationListener l : list) {
          rem &= l.elementChanged(i, null);
        }
        if (rem) {
          removeValue(i);
        }
      }
    }
  }

  /**
   * @see org.jdiameter.api.MutableConfiguration class
   */
  @Override
  public void addChangeListener(ConfigurationListener listener, int... ints) {
    for (int i:ints) {
      List<ConfigurationListener> list = listeners.get(i);
      if (list == null) {
        list = new CopyOnWriteArrayList<ConfigurationListener>();
        list.add(listener);
      }
      listeners.put(i, list);
    }
  }

  /**
   * @see org.jdiameter.api.MutableConfiguration class
   */
  @Override
  public void removeChangeListener(ConfigurationListener listener, int... ints) {
    for (int i:ints) {
      List<ConfigurationListener> list = listeners.get(i);
      if (list != null) {
        list.remove(listener);
        if (list.size() == 0) {
          listeners.remove(i);
        }
      }
    }
  }
}
