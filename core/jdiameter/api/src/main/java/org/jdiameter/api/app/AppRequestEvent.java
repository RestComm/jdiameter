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
     * @return destination host name
     * @throws org.jdiameter.api.AvpDataException if avp is not string
     */
    String getDestinationHost() throws AvpDataException;

    /**
     * Return destionation realm avp value ( null if avp is empty )
     * @return destination realm name
     * @throws AvpDataException if avp is not string
     */
    String getDestinationRealm() throws AvpDataException;

}
