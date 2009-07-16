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

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * This interface describe extends methods of base class
 * Data: $Date: 2009/07/12 18:35:35 $
 * Revision: $Revision: 1.2 $ * @version 1.5.0.1
 */
public interface ISessionFactory extends SessionFactory {

    <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, java.lang.Class<? extends AppSession> aClass, Object... args) throws InternalException;

    void registerAppFacory(Class<? extends AppSession> sessionClass, IAppSessionFactory factory);

    void unRegisterAppFacory(Class<? extends AppSession> sessionClass);    

}
