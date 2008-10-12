/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app.acc;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.Request;

/**
 * Additional listener
 * Actions for FSM
 */
public interface IClientAccActionContext {

    /**
     * Filling nested avp into interim message
     * @param interimRequest instance of interim message which will be sent to server
     */
    void interimIntervalElapses(Request interimRequest) throws InternalException;

    /**
     * Call back for failed_send_record event
     * @param accRequest accounting request record
     * @return true if you want put message to buffer and false if you want to stop processing
     */
    boolean failedSendRecord(Request accRequest) throws InternalException;

    /**
     * Filling nested avp into STR
     * @param sessionTermRequest instance of STR which will be sent to server
     */
    void disconnectUserOrDev(Request sessionTermRequest) throws InternalException;
}
