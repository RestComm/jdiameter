/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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

package org.jdiameter.client.api.io;

import org.jdiameter.api.Wrapper;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

import java.net.InetAddress;

/**
 * Factory of Network Layer elements.
 * Configuration and message parser instances injection by constructor
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ITransportLayerFactory extends Wrapper {

  /**
   * Create new IConnection instance with predefined parameters
   * 
   * @param remoteAddress destination host address
   * @param factory concurrent factory
   * @param remotePort destination port address
   * @param localAddress local network adapter address
   * @param localPort local socket port
   * @param ref reference to additional parameters
   * @return IConnection instance
   * @throws TransportException
   */
  IConnection createConnection(InetAddress remoteAddress, IConcurrentFactory factory, int remotePort, InetAddress localAddress, int localPort, String ref) throws TransportException;

  /**
   * Create new IConnection instance with predefined parameters
   * 
   * @param remoteAddress destination host address
   * @param factory concurrent factory
   * @param remotePort  destination port address
   * @param localAddress local network adapter address
   * @param localPort local socket port
   * @param listener connection listener instance
   * @param ref reference to additional parameters
   * @return IConnection instance
   * @throws TransportException
   */
  IConnection createConnection(InetAddress remoteAddress, IConcurrentFactory factory, int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, String ref) throws TransportException;
}
