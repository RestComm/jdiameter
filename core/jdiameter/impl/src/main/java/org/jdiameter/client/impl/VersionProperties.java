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

package org.jdiameter.client.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class VersionProperties {

  /**
   * The single instance.
   */
  public static final VersionProperties instance = new VersionProperties();

  /**
   * The version properties.
   */
  private Properties props;

  /**
   * Do not allow direct public construction.
   */
  private VersionProperties() {
    props = loadProperties();
  }

  /**
   * Returns an unmodifiable map of version properties.
   *
   * @return
   */
  public Map getProperties() {
    return Collections.unmodifiableMap(props);
  }

  /**
   * Returns the value for the given property name.
   *
   * @param name
   *          - The name of the property.
   * @return The property value or null if the property is not set.
   */
  public String getProperty(final String name) {
    return props.getProperty(name);
  }

  /**
   * Returns the version information as a string.
   *
   * @return Basic information as a string.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object key : props.keySet()) {
      if (first) {
        first = false;
      }
      else {
        sb.append(" , ");
      }
      sb.append(key).append(" = ").append(props.get(key));
    }
    return sb.toString();
  }

  /**
   * Load the version properties from a resource.
   */
  private Properties loadProperties() {

    props = new Properties();

    try {
      InputStream in = VersionProperties.class.getResourceAsStream("/META-INF/version.properties");
      props.load(in);
      in.close();
    }
    catch (Exception e) {
      // failed to load version properties. go with defaults
      props.put("vendor", "Mobicents");
      props.put("version", "UN.DEFINED");
    }

    return props;
  }

}