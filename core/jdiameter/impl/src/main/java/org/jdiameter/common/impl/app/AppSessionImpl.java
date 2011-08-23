/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
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

  protected IAppSessionData appSessionData;

  protected List<Session> sessions;

  protected Session session;

  protected ISessionFactory sf = null;

  protected ScheduledExecutorService scheduler = null;

  protected ITimerFacility timerFacility;

  public AppSessionImpl(ISessionFactory sf, IAppSessionData appSessionData) {
    if (sf == null) {
      throw new IllegalArgumentException("SessionFactory must not be null");
    }
    if (appSessionData == null) {
      throw new IllegalArgumentException("IAppSessionData must not be null");
    }
    try {
      this.sf = sf;
      this.appSessionData = appSessionData;
      IAssembler assembler = ( this.sf).getContainer().getAssemblerFacility();
      this.scheduler = assembler.getComponentInstance(IConcurrentFactory.class).getScheduledExecutorService(IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
      this.timerFacility = assembler.getComponentInstance(ITimerFacility.class);
      this.session = this.sf.getNewSession(this.appSessionData.getSessionId());
      //annoying ;[
      ArrayList<Session> list = new ArrayList<Session>();
      list.add(this.session);
      this.sessions = Collections.unmodifiableList(list);
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
    return this.appSessionData.getApplicationId();
  }

  public List<Session> getSessions() {
    return this.sessions; //....
  }

  public void release() {
    this.session.setRequestListener(null);
    this.session.release();
    this.appSessionData.remove();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.BaseSession#getSessionId()
   */
  public String getSessionId() {
    //use local object, its faster :)
    return this.session.getSessionId();
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((appSessionData == null) ? 0 : appSessionData.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AppSessionImpl other = (AppSessionImpl) obj;
    if (appSessionData == null) {
      if (other.appSessionData != null)
        return false;
    } else if (!appSessionData.equals(other.appSessionData))
      return false;
    return true;
  }

  public abstract void onTimer(String timerName);

}
