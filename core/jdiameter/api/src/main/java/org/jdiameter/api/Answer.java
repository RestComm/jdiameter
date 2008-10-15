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
 * A Answer message is sent by a recipient of Request once it has received and interpreted the Request.
 * Answer are contain a Status-Code and a other AVPs in  message body.
 * @version 1.5.1 Final
 */

public interface Answer extends Message {

    /**
     * @return ResultCode Avp from message
     */
    public Avp getResultCode();

}
