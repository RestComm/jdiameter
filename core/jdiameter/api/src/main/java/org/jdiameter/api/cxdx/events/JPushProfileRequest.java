package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Push-Profile-Request (PPR) command, indicated by the Command-Code field set to 305 and the ÔRÕ bit set in the
 * Command Flags field, is sent by a Diameter Multimedia server to a Diameter Multimedia client in order to update the
 * subscription data and for SIP Digest authentication the authentication data of a multimedia user in the Diameter
 * Multimedia client whenever a modification has occurred in the subscription data or digest password that constitutes
 * the data used by the client. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JPushProfileRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "PPR";
  public static final String _LONG_NAME = "Push-Profile-Request";

  public static final int code = 305;

}
