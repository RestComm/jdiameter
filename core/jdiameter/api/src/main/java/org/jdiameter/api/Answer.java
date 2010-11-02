/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 */
package org.jdiameter.api;

/**
 * An Answer message is sent by a recipient of Request once it has received and interpreted the Request.
 * Answers contain a Result-Code AVP and other AVPs in message body.
 * @version 1.5.1 Final
 */

public interface Answer extends Message {

  /**
   * @return ResultCode Avp from message
   */
  public Avp getResultCode();

}
