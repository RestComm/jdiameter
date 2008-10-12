package org.jdiameter.common.api.app;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.app.AppSession;

public interface IAppSessionFactory {

    AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args);
    
}
