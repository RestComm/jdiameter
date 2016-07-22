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

package org.jdiameter.client.api.fsm;

import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IMessage;

/**
 * This class extends behaviour of FSM StateEvent
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class FsmEvent implements StateEvent {

  private String key;
  private EventTypes type;
  private Object value;
  private final long createdTime = System.currentTimeMillis();

  /**
   * Create instance of class
   *
   * @param type type of event
   */
  public FsmEvent(EventTypes type) {
    this.type = type;
  }

  /**
   * Create instance of class with predefined parameters
   *
   * @param type type of event
   * @param key event key
   */
  public FsmEvent(EventTypes type, String key) {
    this(type);
    this.key = key;
  }

  /**
   * Create instance of class with predefined parameters
   *
   * @param type type of event
   * @param value attached message
   */
  public FsmEvent(EventTypes type, IMessage value) {
    this(type);
    this.value = value;
  }

  /**
   * Create instance of class with predefined parameters
   *
   * @param type type of event
   * @param value  attached message
   * @param key event key
   */
  public FsmEvent(EventTypes type, IMessage value, String key) {
    this(type, value);
    this.key = key;
  }

  /**
   * Return key value
   *
   * @return key value
   */
  public String getKey() {
    return key;
  }

  /**
   * Return attached message
   *
   * @return diameter message
   */
  public IMessage getMessage() {
    return (IMessage) getData();
  }

  /**
   * Return created time
   *
   * @return created time
   */
  public long getCreatedTime() {
    return createdTime;
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return (E) type;
  }

  @Override
  public Enum getType() {
    return type;
  }

  @Override
  public void setData(Object o) {
    value = o;
  }

  @Override
  public Object getData() {
    return value;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }

  /**
   * Return string representation of instance
   *
   * @return string representation of instance
   */
  @Override
  public String toString() {
    return "Event{name:" + type.name() + ", key:" + key + ", object:" + value + "}";
  }
}
