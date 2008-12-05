package org.jdiameter.server.impl.app.cca;

/**
 * 
 * EventType.java
 *
 * <br>Super project:  mobicents
 * <br>5:12:16 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public enum EventType {
  RECEIVED_EVENT,SENT_EVENT_RESPONSE,
  RECEIVED_INITIAL, SENT_INITIAL_RESPONSE,
  RECEIVED_UPDATE, SENT_UPDATE_RESPONSE ,
  RECEIVED_TERMINATE, SENT_TERMINATE_RESPONSE,
  //These are minigles, no transition happens, no state resources, timers
  SENT_RAR, RECEIVED_RAA;
}
