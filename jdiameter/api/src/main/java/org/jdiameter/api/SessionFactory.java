/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

import org.jdiameter.api.app.AppSession;

/**
 * This class created session instance (Factory)
 *
 * @version 1.5.1 Final
 */

public interface SessionFactory {

    /**
     * Create new raw session instance
     *
     * @return session instance of session
     * @throws InternalException if a error occurs
     */
    RawSession getNewRawSession() throws InternalException;

    /**
     * Create new session with new session id
     *
     * @return session instance of session
     * @throws InternalException if a error occurs
     */
    Session getNewSession() throws InternalException;


    /**
     * Create new session with predefined sessionId
     * You can create special sessions to work on distributed systems
     *
     * @param sessionId  instance of session
     * @return session instance of session
     * @throws InternalException if a error occurs
     */
    Session getNewSession(String sessionId) throws InternalException;

    /**
     * Create new vendor specific application session
     * Use this method for create specific application sessions
     * Example: ClientShSession session = factory.getNewSession(appId, ClientShSession.class)
     *
     * @param applicationId predefined application id
     * @param userSession   A Class defining an interface that the result must implement.
     * @return session instance
     * @throws InternalException if a error occurs
     */

    <T extends AppSession> T getNewAppSession(ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException;

    /**
     * Create new vendor specific application session with predefined sessionId, origination host/realm names.
     * You can create special sessions to work on distributed systems
     * Use this method for create specific application sessions
     * Example: ClientShSession session = factory.getNewSession(appId, ClientShSession.class)
     *
     * @param sessionId instance of session
     * @param applicationId predefined application id
     * @param userSession A Class defining an interface that the result must implement.
     * @return session instance
     * @throws InternalException if a error occurs
     */

    <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException;
}
