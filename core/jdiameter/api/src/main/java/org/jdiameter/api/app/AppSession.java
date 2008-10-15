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

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Session;

import java.io.Serializable;
import java.util.List;

/**
 * Basic class for application specific session (Sx, Rx, Gx)
 * @version 1.5.1 Final
 */
public interface AppSession extends Serializable {

    /**
     * Returns the time when this session was created (milliseconds)
     * Start point of time January 1, 1970 GMT.
     * @return long specifying when this session was created
     */
    long getCreationTime();

    /**
     * Returns the last time an event occurred on this session (milliseconds)
     * Start point of time January 1, 1970 GMT.
     * @return long specifying when last time an event occurred on this session
     */
    long getLastAccessedTime();

    /**
     * Return true if session has stateless fsm
     * @return true if session has stateless fsm
     */
    boolean isStateless();

    /**
     * Return true if session has valid state (for example it retur true if session did not have timeout)
     * @return true if session has valid state
     */
    boolean isValid();

    /**
     * Return current value of applicationId of application session.
     * @return applicationId of application session.
     */
    ApplicationId getSessionAppId();

    /**
     * @return Set of child delivery sessions
     */
    List<Session> getSessions();

    /**
     * Release all attachment resources
     */
    void release();
}
