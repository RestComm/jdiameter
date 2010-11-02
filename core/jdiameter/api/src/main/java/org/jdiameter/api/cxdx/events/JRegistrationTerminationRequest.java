package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Registration-Termination-Request (RTR) command, indicated by the Command-Code field set to 304 and the ÔRÕ bit
 * set in the Command Flags field, is sent by a Diameter Multimedia server to a Diameter Multimedia client in order to
 * request the de-registration of a user.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JRegistrationTerminationRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "RTR";
  public static final String _LONG_NAME = "Registration-Termination-Request";

  public static final int code = 304;

}
