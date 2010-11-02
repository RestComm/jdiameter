package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The User-Authorization-Answer (UAA) command, indicated by the Command-Code field set to 300 and the ÔRÕ bit cleared
 * in the Command Flags field, is sent by a server in response to the User-Authorization-Request command.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JUserAuthorizationAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "UAA";
  public static final String _LONG_NAME = "User-Authorization-Answer";

  public static final int code = 300;

}
