package org.jdiameter.client.impl.app.cca;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

public class Event implements StateEvent {

  public enum Type {
    SEND_INITIAL_REQUEST, RECEIVED_INITIAL_ANSWER,
    SEND_UPDATE_REQUEST, RECEIVED_UPDATE_ANSWER,
    SEND_TERMINATE_REQUEST,RECEIVED_TERMINATED_ANSWER,
    RECEIVED_RAR,SEND_RAA,Tx_TIMER_FIRED, 
    SEND_EVENT_REQUEST, RECEIVE_EVENT_ANSWER;
  }

  Type type;
  AppRequestEvent request;
  AppAnswerEvent answer;

  Event(Type type) {
    this.type = type;
  }

  Event(Type type, AppRequestEvent request, AppAnswerEvent answer) {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  Event(boolean isRequest, JCreditControlRequest request, JCreditControlAnswer answer) {

    this.answer = answer;
    this.request = request;

    if(isRequest) {
      switch(request.getRequestTypeAVPValue())
      {
      case 1:
        type = Type.SEND_INITIAL_REQUEST;
        break;
      case 2:
        type = Type.SEND_UPDATE_REQUEST;
        break;
      case 3:
        type = Type.SEND_TERMINATE_REQUEST;
        break;
      case 4:
        type = Type.SEND_EVENT_REQUEST;
        break;
      default:
        throw new RuntimeException("Wrong CC-Request-Type value: " + request.getRequestTypeAVPValue());

      }

    }
    else {
      switch(answer.getRequestTypeAVPValue())
      {
      case 1:
        type = Type.RECEIVED_INITIAL_ANSWER;
        break;
      case 2:
        type = Type.RECEIVED_UPDATE_ANSWER;
        break;
      case 3:
        type = Type.RECEIVED_TERMINATED_ANSWER;
        break;
      case 4:
        type = Type.RECEIVE_EVENT_ANSWER;
        break;
      default:
        throw new RuntimeException("Wrong CC-Request-Type value: " + answer.getRequestTypeAVPValue());
      }
    }
  }

  public Enum getType() {
    return type;
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

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  public <E> E encodeType(Class<E> eClass) {
    return eClass == Event.Type.class ? (E) type : null;
  }
}
