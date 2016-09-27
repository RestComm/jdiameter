/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.server.impl.sy;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.sy.events.SpendingLimitAnswer;
import org.jdiameter.api.sy.events.SpendingLimitRequest;

/**
 * Policy and charging control, Spending Limit Report - Sy session implementation
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

public class Event implements StateEvent {

  public enum Type {
    RECEIVED_INITIAL,
    RECEIVED_INTERMEDIATE,
    RECEIVED_TERMINATION
  }

  Type type;
  AppRequestEvent request;
  AppAnswerEvent answer;

  public Event(boolean isRequest, SpendingLimitRequest request, SpendingLimitAnswer answer) {

    this.request = request;
    this.answer = answer;

    if (isRequest) {
    } else {
    }
  }

  @Override
  public <E> E encodeType(Class<E> enumType) {
    return null;
  }

  @Override
  public Enum getType() {
    return null;
  }

  @Override
  public void setData(Object data) {

  }

  @Override
  public Object getData() {
    return null;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }
}
