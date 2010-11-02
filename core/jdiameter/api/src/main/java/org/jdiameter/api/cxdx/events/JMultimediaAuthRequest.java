package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Multimedia-Auth-Request (MAR) command, indicated by the Command-Code field set to 303 and the ÔRÕ bit set in the
 * Command Flags field, is sent by a Diameter Multimedia client to a Diameter Multimedia server in order to request
 * security information.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JMultimediaAuthRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "MAR";
  public static final String _LONG_NAME = "Multimedia-Auth-Request";

  public static final int code = 303;

}
