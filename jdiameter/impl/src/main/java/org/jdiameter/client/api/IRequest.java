/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api;

import org.jdiameter.api.Request;

/**
 * This interface describe extends methods of base class
 * Data: $Date: 2008/07/03 19:43:10 $
 * Revision: $Revision: 1.1 $
 * @version 1.5.0.1
 */
public interface IRequest extends Request {
    /**
     * Set network request flag
     * @param isNetwork true if this request is neteork
     */
    void setNetworkRequest(boolean isNetwork);
}
