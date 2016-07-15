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

package org.jdiameter.api;

/**
 * This interface is the extended version of the interface configuration and
 * allows to receive of a notification about reconfigurations
 *
 * @author erick.svenson@yahoo.com
 * @version 1.5.1 Final
 */
public interface MutableConfiguration extends Configuration {

  /**
   * Set byte value to configuration
   * @param key key of value
   * @param value byte value
   */
  void setByteValue(int key, byte value);

  /**
   * Set int value to configuration
   * @param key key of value
   * @param value int value
   */
  void setIntValue(int key, int value);

  /**
   * Set long value to configuration
   * @param key key of value
   * @param value long value
   */
  void setLongValue(int key, long value);

  /**
   * Set double value to configuration
   * @param key key of value
   * @param value double value
   */
  void setDoubleValue(int key, double value);

  /**
   * Set byte array value to configuration
   * @param key key of value
   * @param value byte array value
   */
  void setByteArrayValue(int key, byte[] value);

  /**
   * Set boolean value to configuration
   * @param key key of value
   * @param value boolean value
   */
  void setBooleanValue(int key, boolean value);

  /**
   * Set string value to configuration
   * @param key key of value
   * @param value string value
   */
  void setStringValue(int key, String value);

  /**
   * Set children to configuration
   * @param key key of children
   * @param value children value
   */
  void setChildren(int key, Configuration... value);

  /**
   * Remove defined key
   * @param key array keys of removed entry
   */
  void removeValue(int... key);

  /**
   * Add change configuration listener
   * @param listener instance of listener
   * @param keys array of observed propertie's keys
   * if keys.length == 0 then observed all properties of configuration node
   */
  void addChangeListener(ConfigurationListener listener, int... keys);

  /**
   * Remove change configuration listener
   * @param listener instance of listener
   * @param keys array of removed listener's keys
   */
  void removeChangeListener(ConfigurationListener listener, int... keys);
}
