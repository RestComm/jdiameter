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

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;

/**
 * Basic class for application specific answer event (Sx, Rx, Gx)
 * @version 1.5.1 Final
 */
public interface AppAnswerEvent extends AppEvent {

    /**
     * Return result code of snswer message
     * @return result code of snswer message
     * @throws AvpDataException if result code avp absent
     */
     Avp getResultCodeAvp() throws AvpDataException;
    
}
