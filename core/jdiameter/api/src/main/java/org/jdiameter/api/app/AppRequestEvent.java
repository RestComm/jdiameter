/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.app;

import org.jdiameter.api.AvpDataException;

/**
 * Basic class for application specific request event (Sx, Rx, Gx)
 * @version 1.5.1 Final
 */
public interface AppRequestEvent extends AppEvent {

  /**
   * Return destination host avp value ( null if avp is empty )
   * 
   * @return destination host avp value
   * @throws AvpDataException if avp is not string
   */
  public String getDestinationHost() throws AvpDataException;

  /**
   * Return destination realm avp value ( null if avp is empty )
   * 
   * @return origination realm avp value
   * @throws AvpDataException if avp is not string
   */
  public String getDestinationRealm() throws AvpDataException;

}
