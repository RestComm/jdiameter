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

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * A ReAuthentication Request is a request from a client to a server
 * @version 1.5.1 Final
 */

public interface ReAuthRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "RAR";
  public static final String _LONG_NAME = "Re-Auth-Request";

  public static final int code = 258;

  /**
   * Return re-authentication request type
   * @return re-authentication request type
   * @throws org.jdiameter.api.AvpDataException if avp is not integer
   */
  int getReAuthRequestType() throws AvpDataException;

  /**
   * Return Auth-Application-Id value of request
   * @return Auth-Application-Id value of request
   * @throws AvpDataException if avp is not integer
   */
  long getAuthApplicationId()  throws AvpDataException;    
}
