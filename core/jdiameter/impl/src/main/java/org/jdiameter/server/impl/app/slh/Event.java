/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc. and individual contributors
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

package org.jdiameter.server.impl.app.slh;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class Event implements StateEvent {

  enum Type {
    RECEIVE_LCS_ROUTING_INFO_REQUEST,
    SEND_LCS_ROUTING_INFO_ANSWER, 
    TIMEOUT_EXPIRES,
    //Add this to allow app to respond, and in case of app error not to leave it behind
    TX_TIMER_EXPIRED;
  }

  Type type;
  AppEvent request;
  AppEvent answer;

  Event(Type type, AppEvent request, AppEvent answer) {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  public <E> E encodeType(Class<E> eClass) {
    return eClass == Type.class ? (E) type : null;
  }

  public Enum getType() {
    return type;
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  public int compareTo(Object o) {
    return 0;
  }

  public Object getData() {
    return request != null ? request : answer;
  }

  public void setData(Object data) {
    // FIXME: What should we do here?! Is it request or answer?
  }
}
