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

package org.jdiameter.api;

/**
 * This interface is implemented by sack vendors to let users know the local properties of a Diameter Stack implementation
 * and current instance.
 * 
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @version 1.5.1 Final
 */
public interface MetaData extends Wrapper {

  /**
   * @return  Retrieves the stack's major version number.
   */
  int getMajorVersion();

  /**
   * @return Retrieves the stack's minor version number.
   */
  int getMinorVersion();

  /**
   * @return stack type
   */
  StackType getStackType();

  /**
   * @return information about local instance of peer
   */
  Peer getLocalPeer();

  /**
   * Return configuration parameters
   * @return configuration 
   */
  Configuration getConfiguration();
}
