package org.jdiameter.server.impl.app.sh;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

public class Event implements StateEvent {

  enum Type {
    RECEIVE_USER_DATA_REQUEST,
    RECEIVE_PROFILE_UPDATE_REQUEST, 
    RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST, 
    RECEIVE_PUSH_NOTIFICATION_ANSWER, 
    SEND_PUSH_NOTIFICATION_REQUEST, 
    SEND_USER_DATA_ANSWER, 
    SEND_PROFILE_UPDATE_ANSWER, 
    SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER,
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
