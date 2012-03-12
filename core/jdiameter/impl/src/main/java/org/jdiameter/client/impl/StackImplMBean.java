/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

package org.jdiameter.client.impl;

import org.jdiameter.api.InternalException;

/**
 * Stack MBean interface.
 *  
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface StackImplMBean {

  /**
   * Return string representation of stack instanceconfiguration
   * @return string representation of stack instance configuration
   */
  String configuration();

  /**
   * Return string representation of stack instance metadata
   * @return string representation of stack instance metadata
   */
  String metaData();

  /**
   * Reurn description (include state) of defined peer
   * @param name peer host name
   * @return description of defined peer
   */
  String peerDescription(String name);

  /**
   * Return list of peer
   * @return list of peer
   */
  String peerList();

  /**
   * Return true if stack is started
   * @return true if stack is started
   */
  boolean isActive();

  /**
   * Run stop procedure
   */
  void stop(int disconnectCause);

  /**
   * Run startd procedure
   * @throws org.jdiameter.api.IllegalDiameterStateException
   * @throws InternalException
   */
  void start()  throws org.jdiameter.api.IllegalDiameterStateException, InternalException;

}
