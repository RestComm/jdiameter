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
 * A Session Termination Request is a request from a client to a server
 * 
 * @version 1.5.1 Final
 */

public interface SessionTermRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "STR";
  public static final String _LONG_NAME = "Session-Termination-Request";

  public static final int code = 275;

  long getAuthApplicationId() throws AvpDataException;

  boolean hasAuthApplicationId();

  void setAuthApplicationId(long val);

  long getOriginStateId() throws AvpDataException;

  boolean hasOriginStateId();

  void setOriginStateId(long val);

  boolean hasTerminationCause();

  int getTerminationCause() throws AvpDataException;

  void setTerminationCause(int val);

  boolean hasUserName();

  String getUserName() throws AvpDataException;

  void setUserName(String val);
}
