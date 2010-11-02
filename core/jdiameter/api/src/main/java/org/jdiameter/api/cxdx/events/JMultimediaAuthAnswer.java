package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Multimedia-Auth-Answer (MAA) command, indicated by the Command-Code field set to 303 and the ÔRÕ bit cleared in
 * the Command Flags field, is sent by a server in response to the Multimedia-Auth-Request command.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JMultimediaAuthAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "MAA";
  public static final String _LONG_NAME = "Multimedia-Auth-Answer";

  public static final int code = 303;

}
