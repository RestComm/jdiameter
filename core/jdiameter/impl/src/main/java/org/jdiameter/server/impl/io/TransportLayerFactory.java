/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, TeleStax Inc. and individual contributors
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

package org.jdiameter.server.impl.io;

import static java.lang.Class.forName;

import java.lang.reflect.Constructor;
import java.net.InetAddress;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.jdiameter.server.api.io.ITransportLayerFactory;
import org.jdiameter.server.impl.helpers.ExtensionPoint;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class TransportLayerFactory extends org.jdiameter.client.impl.transport.TransportLayerFactory implements ITransportLayerFactory {

  private final IConcurrentFactory concurrentFactory;
  private final IMetaData metaData;
  private Class<INetworkGuard> networkGuardClass;
  private Constructor<INetworkGuard> networkGuardConstructor;

  public TransportLayerFactory(Configuration conf, IConcurrentFactory concurrentFactory, IMessageParser parser, IMetaData metaData) throws TransportException {
    super(conf, parser);

    this.concurrentFactory = concurrentFactory;
    this.metaData = metaData;
    String networkGuardClassName = null;
    Configuration[] children = config.getChildren(org.jdiameter.client.impl.helpers.Parameters.Extensions.ordinal());
    // extract network guard class name.
    AppConfiguration internalExtensions = (AppConfiguration) children[org.jdiameter.client.impl.helpers.ExtensionPoint.Internal.id()];
    networkGuardClassName = internalExtensions.getStringValue(ExtensionPoint.InternalNetworkGuard.ordinal(),
        ExtensionPoint.InternalNetworkGuard.defValue());

    try {
      // TODO: this should be enough to check if class has interface!?
      this.networkGuardClass = (Class<INetworkGuard>) forName(networkGuardClassName);

      if (!INetworkGuard.class.isAssignableFrom(this.networkGuardClass)) {
        throw new TransportException("Specified class does not inherit INetworkGuard interface " + this.networkGuardClass, TransportError.Internal);
      }
    }
    catch (Exception e) {
      throw new TransportException("Cannot prepare specified guard class " + this.networkGuardClass, TransportError.Internal, e);
    }

    try {
      // TODO: this is bad practice, IConnection is interface and this code enforces constructor type to be present!
      networkGuardConstructor = this.networkGuardClass.getConstructor(InetAddress[].class, Integer.TYPE, IConcurrentFactory.class,
          IMessageParser.class, IMetaData.class);

    }
    catch (Exception e) {
      throw new TransportException("Cannot find required constructor", TransportError.Internal, e);
    }
  }

  @Override
  public INetworkGuard createNetworkGuard(InetAddress inetAddress, int port) throws TransportException {
    try {
      return networkGuardConstructor.newInstance(inetAddress, port, this.concurrentFactory, this.parser, this.metaData);
    }
    catch (Exception e) {
      throw new TransportException(TransportError.NetWorkError, e);
    }
  }

  @Override
  public INetworkGuard createNetworkGuard(InetAddress inetAddress, final int port, final INetworkConnectionListener listener) throws TransportException {
    INetworkGuard guard;
    try {
      guard = networkGuardConstructor.newInstance(inetAddress, port, this.concurrentFactory, this.parser, this.metaData);
    }
    catch (Exception e) {
      throw new TransportException(TransportError.NetWorkError, e);
    }
    guard.addListener(listener);
    return guard;
  }

  @Override
  public INetworkGuard createNetworkGuard(InetAddress[] inetAddress, int port) throws TransportException {
    try {
      return networkGuardConstructor.newInstance(inetAddress, port, this.concurrentFactory, this.parser, this.metaData);
    }
    catch (Exception e) {
      throw new TransportException(TransportError.NetWorkError, e);
    }
  }

  @Override
  public INetworkGuard createNetworkGuard(InetAddress[] inetAddress, int port, INetworkConnectionListener listener) throws TransportException {
    INetworkGuard guard;
    try {
      guard = networkGuardConstructor.newInstance(inetAddress, port, this.concurrentFactory, this.parser, this.metaData);
    }
    catch (Exception e) {
      throw new TransportException(TransportError.NetWorkError, e);
    }
    guard.addListener(listener);
    return guard;
  }
}
