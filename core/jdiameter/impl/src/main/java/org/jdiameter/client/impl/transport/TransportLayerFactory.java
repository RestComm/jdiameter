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

package org.jdiameter.client.impl.transport;

import static java.lang.Class.forName;

import java.lang.reflect.Constructor;
import java.net.InetAddress;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.client.impl.helpers.ExtensionPoint;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.DummyConcurrentFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TransportLayerFactory implements ITransportLayerFactory {

  private Class<IConnection> connectionClass;
  private Constructor<IConnection> constructorIAi, constructorIAiCL;
  protected IMessageParser parser;
  protected Configuration config = null;

  public TransportLayerFactory(Configuration config, IMessageParser parser) throws TransportException {
    this.config = config;
    Configuration[] children = config.getChildren(Parameters.Extensions.ordinal());

    AppConfiguration internalExtensions = (AppConfiguration) children[ExtensionPoint.Internal.id()];
    String implName = internalExtensions.getStringValue(
        ExtensionPoint.InternalConnectionClass.ordinal(), ExtensionPoint.InternalConnectionClass.defValue()
        );
    try {
      //TODO: this should be enough to check if class has interface!?
      this.connectionClass = (Class<IConnection>) forName(implName);

      if (!IConnection.class.isAssignableFrom(this.connectionClass)) {
        throw new TransportException("Specified class does not inherit IConnection interface " + this.connectionClass, TransportError.Internal);
      }
    } catch (Exception e) {
      throw new TransportException("Cannot prepare specified connection class " + this.connectionClass, TransportError.Internal, e);
    }
    try {
      //TODO: this is bad practice, IConnection is interface and this code enforces constructor type to be present!
      constructorIAiCL = connectionClass.getConstructor(
          Configuration.class, IConcurrentFactory.class, InetAddress.class, Integer.TYPE, InetAddress.class,
          Integer.TYPE, IConnectionListener.class, IMessageParser.class, String.class);
      constructorIAi = connectionClass.getConstructor(
          Configuration.class, IConcurrentFactory.class, InetAddress.class, Integer.TYPE, InetAddress.class,
          Integer.TYPE, IMessageParser.class, String.class);
    }
    catch (Exception e) {
      throw new TransportException("Cannot find required constructor", TransportError.Internal, e);
    }
    this.parser = parser;
  }

  @Override
  public IConnection createConnection(InetAddress remoteAddress, IConcurrentFactory factory, int remotePort, InetAddress localAddress, int localPort,
      String ref) throws TransportException {
    try {
      factory = factory == null ? new DummyConcurrentFactory() : factory;
      return constructorIAi.newInstance(config, factory, remoteAddress, remotePort, localAddress, localPort, parser, ref);
    } catch (Exception e) {
      throw new TransportException("Cannot create an instance of " + connectionClass, TransportError.Internal, e);
    }
  }

  @Override
  public IConnection createConnection(InetAddress remoteAddress, IConcurrentFactory factory, int remotePort, InetAddress localAddress, int localPort,
      IConnectionListener listener, String ref) throws TransportException {
    try {
      factory = factory == null ? new DummyConcurrentFactory() : factory;
      return constructorIAiCL.newInstance(config, factory, remoteAddress, remotePort, localAddress, localPort, listener, parser, ref);
    } catch (Exception e) {
      throw new TransportException("Cannot create an instance of " + connectionClass, TransportError.Internal, e);
    }
  }

  @Override
  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;
  }
}
