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

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * A Answer message is sent by a recipient of Request once it has received and interpreted the Request.
 * @version 1.5.1 Final
 */

public interface SessionTermAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "STA";
  public static final String _LONG_NAME = "Session-Termination-Answer";

  public static final int code = 275;

}