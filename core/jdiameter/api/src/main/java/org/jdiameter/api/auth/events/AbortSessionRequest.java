/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.auth.events;

import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.AvpDataException;

/**
 * A Abort Session Request is a request from a client to a server
 * @version 1.5.1 Final
 */

public interface AbortSessionRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "ASR";
  public static final String _LONG_NAME = "Abort-Session-Request";

  public static final int code = 274;

  /**
   * Return Auth-Application-Id value of request
   * @return Auth-Application-Id value of request
   * @throws org.jdiameter.api.AvpDataException if avp is not integer
   */
  long getAuthApplicationId()  throws AvpDataException;
}
