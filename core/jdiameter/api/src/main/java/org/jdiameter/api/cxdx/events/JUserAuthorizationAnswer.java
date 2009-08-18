package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * Start time:13:45:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JUserAuthorizationAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "UAA";
  public static final String _LONG_NAME = "User-Authorization-Answer";
  public static final int code = 300;
}
