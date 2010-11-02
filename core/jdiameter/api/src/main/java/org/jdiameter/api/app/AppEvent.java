/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.app;

import org.jdiameter.api.Message;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.AvpDataException;

import java.io.Serializable;

/**
 * Basic class for application specific event (Sx, Rx, Gx)
 * 
 * @version 1.5.1 Final
 */
public interface AppEvent extends Serializable {

  /**
   * @return commCode of parent message
   */
  public int getCommandCode();

  /**
   * @return set of associated message
   * @throws InternalException signals that internal message is not set.
   */
  public Message getMessage() throws InternalException;

  /**
   * Return origination host avp value ( null if avp is empty )
   * 
   * @return origination host avp value
   * @throws AvpDataException if avp is not string
   */
  public String getOriginHost() throws AvpDataException;

  /**
   * Return origination realm avp value ( null if avp is empty )
   * 
   * @return origination realm avp value
   * @throws AvpDataException if avp is not string
   */
  public String getOriginRealm() throws AvpDataException;

}
