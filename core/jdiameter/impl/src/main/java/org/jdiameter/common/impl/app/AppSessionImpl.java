package org.jdiameter.common.impl.app;

import java.util.Arrays;
import java.util.List;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AppSessionImpl implements AppSession {

  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(AppSessionImpl.class);

  protected Session session;
  protected ApplicationId appId;

  public long getCreationTime() {
    return session.getCreationTime();
  }

  public long getLastAccessedTime() {
    return session.getLastAccessedTime();
  }

  public boolean isValid() {
    return session == null ? false : session.isValid();
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
