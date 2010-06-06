/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.impl.app.auth;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

class Event implements StateEvent {

  enum Type{
    SEND_AUTH_REQUEST,
    SEND_AUTH_ANSWER,
    SEND_SESSION_TERMINATION_REQUEST,
    SEND_SESSION_ABORT_ANSWER,
    RECEIVE_AUTH_ANSWER,
    RECEIVE_FAILED_AUTH_ANSWER,
    RECEIVE_ABORT_SESSION_REQUEST,
    RECEIVE_SESSION_TERINATION_ANSWER,
    TIMEOUT_EXPIRES
  }

  Type type;
  AppEvent data;

  Event(Type type, AppEvent data) {
    this.type = type;
    this.data = data;
  }

  public <E> E encodeType(Class<E> eClass) {
    return eClass == Type.class ? (E) type : null;
  }

  public Enum getType() {
    return type;
  }

  public void setData(Object o) {
    data = (AppEvent) o;
  }

  public Object getData() {
    return data;
  }

  public int compareTo(Object o) {
    return 0;
  }
}