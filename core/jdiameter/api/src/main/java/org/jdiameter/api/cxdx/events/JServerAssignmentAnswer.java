package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Server-Assignment-Answer (SAA) command, indicated by the Command-Code field set to 301 and the ÔRÕ bit cleared
 * in the Command Flags field, is sent by a server in response to the Server-Assignment-Request command. 
 * If Result-Code or Experimental-Result does not inform about an error, the User-Data AVP shall contain the
 * information that the S-CSCF needs to give service to the user.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JServerAssignmentAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "SAA";
  public static final String _LONG_NAME = "Server-Assignment-Answer";

  public static final int code = 301;

}
