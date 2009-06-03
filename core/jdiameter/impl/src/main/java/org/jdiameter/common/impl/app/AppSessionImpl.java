package org.jdiameter.common.impl.app;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppSession;

import java.util.Arrays;
import java.util.List;


public abstract class AppSessionImpl implements AppSession {

    protected static final Logger logger = Logger.getLogger(AppSessionImpl.class);
    protected Session session;
    protected ApplicationId appId;

    public long getCreationTime() {
        return session.getCreationTime();
    }

    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public boolean isValid() {
    	if(session==null)
    		return false;
        return session.isValid();
    }

    public ApplicationId getSessionAppId() {
        return appId;
    }

    public List<Session> getSessions() {
        return Arrays.asList(session);
    }


    public void release() {
        session.release();
    }
}
