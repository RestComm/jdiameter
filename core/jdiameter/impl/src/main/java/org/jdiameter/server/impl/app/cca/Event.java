package org.jdiameter.server.impl.app.cca;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

/**
 * 
 * Event.java
 *
 * <br>Super project:  mobicents
 * <br>5:10:31 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class Event implements StateEvent {

  EventType type;
  AppRequestEvent request;
  AppAnswerEvent answer;
  
  Event(EventType type)
  {
    this.type = type;
  }

  Event(EventType type, AppRequestEvent request, AppAnswerEvent answer)
  {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  Event(boolean isRequest, JCreditControlRequest request, JCreditControlAnswer answer)
  {

    this.answer = answer;
    this.request = request;
    
    /**
     * <pre>
     * 8.3.  CC-Request-Type AVP
     * 
     *     The CC-Request-Type AVP (AVP Code 416) is of type Enumerated and
     *     contains the reason for sending the credit-control request message.
     *     It MUST be present in all Credit-Control-Request messages.  The
     *     following values are defined for the CC-Request-Type AVP:
     * 
     *     INITIAL_REQUEST                 1
     *     UPDATE_REQUEST                  2
     *     TERMINATION_REQUEST             3
     *     EVENT_REQUEST                   4
     *     </pre>
     */
    if(isRequest)
    {
      switch(request.getRequestTypeAVPValue())
      {
      case 1: 
        type=EventType.RECEIVED_INITIAL;
        break;
      case 2:
        type=EventType.RECEIVED_UPDATE;
        break;
      case 3:
        type=EventType.RECEIVED_TERMINATE;
        break;
      case 4:
        type=EventType.RECEIVED_EVENT;
        break;
      default:
        throw new IllegalArgumentException("Wrong value off avp code 416 or avp not present!!!");
      }
    }
    else
    {
      switch(request.getRequestTypeAVPValue())
      {
      case 1: 
        type=EventType.SENT_INITIAL_RESPONSE;
        break;
      case 2:
        type=EventType.SENT_UPDATE_RESPONSE;
        break;
      case 3:
        type=EventType.SENT_TERMINATE_RESPONSE;
        break;
      case 4:
        type=EventType.SENT_EVENT_RESPONSE;
        break;
      default:
        throw new IllegalArgumentException("Wrong value off avp code 416 or avp not present");
      }
    }
  }

  public <E> E encodeType(Class<E> eClass)
  {
    return eClass == EventType.class ? (E) type : null;
  }

  public Enum getType() 
  {
    return type;
  }

  public int compareTo(Object o)
  {
    return 0;
  }

  public Object getData()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public void setData(Object data)
  {
    // TODO Auto-generated method stub
  }

  public AppEvent getRequest()
  {
    return request;
  }

  public AppEvent getAnswer()
  {
    return answer;
  }

}
