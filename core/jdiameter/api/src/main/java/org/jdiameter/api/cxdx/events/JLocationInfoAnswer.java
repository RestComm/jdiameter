package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Location-Info-Answer (LIA) command, indicated by the Command-Code field set to 302 and the ÔRÕ bit cleared in
 * the Command Flags field, is sent by a server in response to the Location-Info-Request command. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JLocationInfoAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "LIA";
  public static final String _LONG_NAME = "Location-Info-Answer";

  public static final int code = 302;

}
