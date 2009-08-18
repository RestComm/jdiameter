package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * Start time:13:45:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JMultimediaAuthRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "MAR";
  public static final String _LONG_NAME = "Multimedia-Auth-Request";
  public static final int code = 303;
}
