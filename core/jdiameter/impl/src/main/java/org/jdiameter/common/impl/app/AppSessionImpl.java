 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.common.impl.app;

import static org.jdiameter.client.impl.helpers.Parameters.SessionTimeOut;

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

  private static final Logger logger = LoggerFactory.getLogger(AppSessionImpl.class);

  protected IAppSessionData appSessionData;

  protected List<Session> sessions;

  protected Session session;

  protected ISessionFactory sf = null;

  protected ScheduledExecutorService scheduler = null;

  protected ITimerFacility timerFacility;

  protected long maxIdleTime = 0;

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
      this.scheduler = assembler.getComponentInstance(IConcurrentFactory.class).
          getScheduledExecutorService(IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
      this.timerFacility = assembler.getComponentInstance(ITimerFacility.class);
      this.maxIdleTime = this.sf.getContainer().getConfiguration().getLongValue(SessionTimeOut.ordinal(), (Long) SessionTimeOut.defValue());
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

  @Override
  public long getCreationTime() {
    return session.getCreationTime();
  }

  @Override
  public long getLastAccessedTime() {
    return session.getLastAccessedTime();
  }

  @Override
  public boolean isValid() {
    return session == null ? false : session.isValid();
  }

  @Override
  public ApplicationId getSessionAppId() {
    return this.appSessionData.getApplicationId();
  }

  @Override
  public List<Session> getSessions() {
    return this.sessions; //....
  }

  @Override
  public void release() {
    logger.debug("Releasing application session for Session ID '{}' ({}).", getSessionId(), getSessionAppId());
    this.session.setRequestListener(null);
    this.session.release();
    this.appSessionData.remove();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.BaseSession#getSessionId()
   */
  @Override
  public String getSessionId() {
    //use local object, its faster :)
    return this.session.getSessionId();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.BaseSession#isAppSession()
   */
  @Override
  public boolean isAppSession() {
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.BaseSession#isReplicable()
   */
  @Override
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
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AppSessionImpl other = (AppSessionImpl) obj;
    if (appSessionData == null) {
      if (other.appSessionData != null) {
        return false;
      }
    } else if (!appSessionData.equals(other.appSessionData)) {
      return false;
    }
    return true;
  }

  public abstract void onTimer(String timerName);

  protected void checkIdleAppSession() {
    if (!isValid() || (maxIdleTime > 0 && System.currentTimeMillis() - getLastAccessedTime() >= maxIdleTime)) {
      logger.debug("Terminating idle/invalid application session [{}] with SID[{}]", this, getSessionId());
      release();
    }
  }


}
