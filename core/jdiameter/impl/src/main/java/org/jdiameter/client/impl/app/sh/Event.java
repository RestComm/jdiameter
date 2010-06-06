package org.jdiameter.client.impl.app.sh;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

public class Event implements StateEvent {
  enum Type {
    SEND_USER_DATA_REQUEST,
    SEND_PROFILE_UPDATE_REQUEST, 
    SEND_SUBSCRIBE_NOTIFICATIONS_REQUEST, 
    SEND_PUSH_NOTIFICATION_ANSWER, 
    RECEIVE_PUSH_NOTIFICATION_REQUEST, 
    RECEIVE_USER_DATA_ANSWER, 
    RECEIVE_PROFILE_UPDATE_ANSWER, 
    RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER,
    TIMEOUT_EXPIRES
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
