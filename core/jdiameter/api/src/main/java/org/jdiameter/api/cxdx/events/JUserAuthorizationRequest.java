package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The User-Authorization-Request (UAR) command, indicated by the Command-Code field set to 300 and the ÔRÕ bit set in
 * the Command Flags field, is sent by a Diameter Multimedia client to a Diameter Multimedia server in order to request
 * the authorization of the registration of a multimedia user. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JUserAuthorizationRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "UAR";
  public static final String _LONG_NAME = "User-Authorization-Request";

  public static final int code = 300;

}
