/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.common.impl.app;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation for {@link AppSession}
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class AppSessionImpl implements AppSession {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(AppSessionImpl.class);

  protected String sessionId;

  protected ApplicationId appId;

  protected transient Session session;

  protected transient SessionFactory sf = null;

  protected transient ScheduledExecutorService scheduler = null;

  protected transient ISessionDatasource sessionDataSource;

  protected transient ITimerFacility timerFacility;

  /**
   * @deprecated
   */
  public AppSessionImpl() {

  }

  public AppSessionImpl(SessionFactory sf, String sessionId) {
    if (sf == null) {
      throw new IllegalArgumentException("SessionFactory must not be null");
    }
    try {
      this.sf = sf;
      IAssembler assembler = ((ISessionFactory) this.sf).getContainer().getAssemblerFacility();
      this.sessionDataSource = assembler.getComponentInstance(ISessionDatasource.class);
      this.scheduler = assembler.getComponentInstance(IConcurrentFactory.class).getScheduledExecutorService(
          IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
      this.timerFacility = assembler.getComponentInstance(ITimerFacility.class);
      if (sessionId == null) {
        session = sf.getNewSession();
        this.sessionId = session.getSessionId();
      }
      else {
        this.sessionId = sessionId;
        this.session = this.sf.getNewSession(this.sessionId);
      }
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

  }

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

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.BaseSession#getSessionId()
   */
  public String getSessionId() {
    return this.sessionId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.BaseSession#isAppSession()
   */
  public boolean isAppSession() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.BaseSession#isReplicable()
   */
  public boolean isReplicable() {
    // FIXME: make this true?
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jdiameter.api.app.AppSession#relink(org.jdiameter.client.api.IContainer
   * )
   */
  public void relink(IContainer stack) {
    if(stack == null) {
      throw new NullPointerException("Can not link session to not defined stack: " + stack);
    }
    IAssembler assembler = stack.getAssemblerFacility();
    this.sf = assembler.getComponentInstance(ISessionFactory.class);
    this.scheduler = ((ISessionFactory) this.sf).getContainer().getAssemblerFacility().getComponentInstance(IConcurrentFactory.class).getScheduledExecutorService(
        IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
    this.sessionDataSource = assembler.getComponentInstance(ISessionDatasource.class);
    this.timerFacility = assembler.getComponentInstance(ITimerFacility.class);

    try {
      this.session = this.sf.getNewSession(sessionId);
    }
    catch (InternalException e) {
      logger.error("Failure relinking app session.", e);
    }
  }

  // FIXME: make this abstract!
  public void onTimer(String timerName) {
    // TODO Auto-generated method stub
  }

}
