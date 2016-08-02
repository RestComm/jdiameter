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


/**
 * This class provide pluggable features
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ExtensionPoint extends org.jdiameter.client.impl.helpers.ExtensionPoint {

  private static final long serialVersionUID = -8220684081025349561L;

  /**
   * Network implementation class name
   */
  public static final ExtensionPoint InternalNetWork = new ExtensionPoint("InternalNetWork", "org.jdiameter.server.impl.NetworkImpl", true);

  //false - so its not added to extension point so Assembler does not try to create instance!
  /**
   * Class name of network guard
   */
  public static final ExtensionPoint InternalNetworkGuard = new ExtensionPoint("InternalNetworkGuard", "org.jdiameter.server.impl.io.tcp.NetworkGuard", false);

  /**
   * Overload manager implementation class name
   */
  public static final ExtensionPoint InternalOverloadManager =
      new ExtensionPoint("InternalOverloadManager", "org.jdiameter.server.impl.OverloadManagerImpl", true);

  protected ExtensionPoint(String name, String defaultValue, boolean appendToInternal) {
    super(name, defaultValue);
    if (appendToInternal) {
      Internal.appendElements(this);
    }
  }

  protected ExtensionPoint(String name, org.jdiameter.client.impl.helpers.ExtensionPoint... parameters) {
    super(name, parameters);
  }

  protected ExtensionPoint(String name, int id, org.jdiameter.client.impl.helpers.ExtensionPoint... parameters) {
    super(name, id, parameters);
  }
}
