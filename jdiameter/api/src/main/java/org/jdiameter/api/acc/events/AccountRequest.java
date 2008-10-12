/**
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.acc.events;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppRequestEvent;

/**
 *  A Account Request is a request from a client to a server
 * @version 1.5.1 Final
 */

public interface AccountRequest extends AppRequestEvent {

    /**
     * @return Record type of request
     * @throws AvpDataException if result code avp is not integer
     */
    int getAccountingRecordType() throws AvpDataException;

    /**
     * @return record number
     * @throws AvpDataException if result code avp is not integer
     */
    long getAccountingRecordNumber() throws AvpDataException;
}
