package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Push-Profile-Answer (PPA) command, indicated by the Command-Code field set to 305 and the ÔRÕ bit cleared in the
 * Command Flags field, is sent by a client in response to the Push-Profile-Request command. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JPushProfileAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "PPA";
  public static final String _LONG_NAME = "Push-Profile-Answer";

  public static final int code = 305;

}
