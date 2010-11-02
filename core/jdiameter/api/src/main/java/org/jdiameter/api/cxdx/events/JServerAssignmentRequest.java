package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Server-Assignment-Request (SAR) command, indicated by the Command-Code field set to 301 and the ÔRÕ bit set in
 * the Command Flags field, is sent by a Diameter Multimedia client to a Diameter Multimedia server in order to request
 * it to store the name of the server that is currently serving the user.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JServerAssignmentRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "SAR";
  public static final String _LONG_NAME = "Server-Assignment-Request";

  public static final int code = 301;

}
