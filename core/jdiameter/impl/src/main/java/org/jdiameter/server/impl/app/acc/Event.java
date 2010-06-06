/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.impl.app.acc;

import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

class Event implements StateEvent {

  enum Type{
    RECEIVED_EVENT_RECORD,
    RECEIVED_START_RECORD,
    RECEIVED_INTERIM_RECORD,
    RECEIVED_STOP_RECORD
  }

  Type type;
  AppEvent data;

  Event(Type type) {
    this.type = type;
  }

  Event(AccountRequest accountRequest) throws Exception {
    data = accountRequest;
    int type = accountRequest.getAccountingRecordType();
    switch (type) {
    case 1:
      this.type = Type.RECEIVED_EVENT_RECORD;
      break;
    case 2:
      this.type = Type.RECEIVED_START_RECORD;
      break;
    case 3:
      this.type = Type.RECEIVED_INTERIM_RECORD;
      break;
    case 4:
      this.type = Type.RECEIVED_STOP_RECORD;
      break;
    default:
      throw new Exception("Unknown type " + type);
    }
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

  public int compareTo(Object other) {
    return equals(other) ? 0 : -1;
  }

  public boolean equals(Object other) {
    return this == other;
  }
}
