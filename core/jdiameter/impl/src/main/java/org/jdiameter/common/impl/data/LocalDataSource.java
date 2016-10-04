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

package org.jdiameter.common.impl.data;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.jdiameter.api.BaseSession;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISession;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.acc.IAccSessionData;
import org.jdiameter.common.api.app.auth.IAuthSessionData;
import org.jdiameter.common.api.app.cca.ICCASessionData;
import org.jdiameter.common.api.app.cxdx.ICxDxSessionData;
import org.jdiameter.common.api.app.gx.IGxSessionData;
import org.jdiameter.common.api.app.rf.IRfSessionData;
import org.jdiameter.common.api.app.ro.IRoSessionData;
import org.jdiameter.common.api.app.rx.IRxSessionData;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.app.s6a.IS6aSessionData;
import org.jdiameter.common.api.app.sh.IShSessionData;
import org.jdiameter.common.api.app.slg.ISLgSessionData;
import org.jdiameter.common.api.app.slh.ISLhSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.acc.AccLocalSessionDataFactory;
import org.jdiameter.common.impl.app.auth.AuthLocalSessionDataFactory;
import org.jdiameter.common.impl.app.cca.CCALocalSessionDataFactory;
import org.jdiameter.common.impl.app.cxdx.CxDxLocalSessionDataFactory;
import org.jdiameter.common.impl.app.gx.GxLocalSessionDataFactory;
import org.jdiameter.common.impl.app.rf.RfLocalSessionDataFactory;
import org.jdiameter.common.impl.app.ro.RoLocalSessionDataFactory;
import org.jdiameter.common.impl.app.rx.RxLocalSessionDataFactory;
import org.jdiameter.common.impl.app.s13.S13LocalSessionDataFactory;
import org.jdiameter.common.impl.app.s6a.S6aLocalSessionDataFactory;
import org.jdiameter.common.impl.app.sh.ShLocalSessionDataFactory;
import org.jdiameter.common.impl.app.slg.SLgLocalSessionDataFactory;
import org.jdiameter.common.impl.app.slh.SLhLocalSessionDataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local implementation of session datasource for {@link ISessionDatasource}
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LocalDataSource implements ISessionDatasource {

  //provided by impl, no way to change that, no conf! :)
  protected HashMap<Class<? extends IAppSessionData>, IAppSessionDataFactory<? extends IAppSessionData>> appSessionDataFactories =
      new HashMap<Class<? extends IAppSessionData>, IAppSessionDataFactory<? extends IAppSessionData>>();

  private ConcurrentHashMap<String, SessionEntry> sessionIdToEntry = new ConcurrentHashMap<String, LocalDataSource.SessionEntry>();

  private static final Logger logger = LoggerFactory.getLogger(LocalDataSource.class);

  public LocalDataSource() {
    appSessionDataFactories.put(ICCASessionData.class, new CCALocalSessionDataFactory());
    appSessionDataFactories.put(IRoSessionData.class, new RoLocalSessionDataFactory());
    appSessionDataFactories.put(IRfSessionData.class, new RfLocalSessionDataFactory());
    appSessionDataFactories.put(IGxSessionData.class, new GxLocalSessionDataFactory());
    appSessionDataFactories.put(IAccSessionData.class, new AccLocalSessionDataFactory());
    appSessionDataFactories.put(IAuthSessionData.class, new AuthLocalSessionDataFactory());
    appSessionDataFactories.put(IShSessionData.class, new ShLocalSessionDataFactory());
    appSessionDataFactories.put(ICxDxSessionData.class, new CxDxLocalSessionDataFactory());
    appSessionDataFactories.put(IRxSessionData.class, new RxLocalSessionDataFactory());
    appSessionDataFactories.put(IS6aSessionData.class, new S6aLocalSessionDataFactory());
    appSessionDataFactories.put(IS13SessionData.class, new S13LocalSessionDataFactory());
    appSessionDataFactories.put(ISLhSessionData.class, new SLhLocalSessionDataFactory());
    appSessionDataFactories.put(ISLgSessionData.class, new SLgLocalSessionDataFactory());
  }

  public LocalDataSource(IContainer container) {
    this();
  }

  @Override
  public boolean exists(String sessionId) {
    return this.sessionIdToEntry.containsKey(sessionId);
  }

  @Override
  public void setSessionListener(String sessionId, NetworkReqListener data) {
    logger.debug("setSessionListener({}, {})", sessionId, data);

    SessionEntry se = sessionIdToEntry.get(sessionId);
    if (se != null) {
      se.listener = data;
    }
    else {
      throw new IllegalArgumentException("No Session entry for id: " + sessionId);
    }
  }

  @Override
  public NetworkReqListener getSessionListener(String sessionId) {
    SessionEntry se = sessionIdToEntry.get(sessionId);
    logger.debug("getSessionListener({}) => {}", sessionId, se);
    return se != null ? se.listener : null;
  }

  @Override
  public NetworkReqListener removeSessionListener(String sessionId) {
    SessionEntry se = sessionIdToEntry.get(sessionId);
    logger.debug("removeSessionListener({}) => {}", sessionId, se);
    if (se != null) {
      NetworkReqListener lst = se.listener;
      se.listener = null;
      return lst;
    }
    else {
      return null;
    }
  }

  @Override
  public void addSession(BaseSession session) {
    logger.debug("addSession({})", session);
    SessionEntry se = null;

    String sessionId = session.getSessionId();
    //FIXME: check here replicable vs not replicable?
    if (this.sessionIdToEntry.containsKey(sessionId)) {
      se = this.sessionIdToEntry.get(sessionId);
      if ( !(se.session instanceof ISession) || se.session.isReplicable()) { //must be not replicable so we can "overwrite"
        throw new IllegalArgumentException("Sessin with id: " + sessionId + ", already exists!");
      }
      else {
        this.sessionIdToEntry.put(sessionId, se);
      }
    }
    else {
      se = new SessionEntry();
    }
    se.session = session;
    this.sessionIdToEntry.put(session.getSessionId(), se);
  }

  @Override
  public BaseSession getSession(String sessionId) {
    SessionEntry se = sessionIdToEntry.get(sessionId);
    logger.debug("getSession({}) => {}", sessionId, se);
    return se != null ? se.session : null;
  }

  @Override
  public void removeSession(String sessionId) {
    SessionEntry se = this.sessionIdToEntry.remove(sessionId);
    logger.debug("removeSession({}) => {}", sessionId, se);
  }


  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#start()
   */
  @Override
  public void start() {
    // NOP
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#stop()
   */
  @Override
  public void stop() {
    // NOP
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.data.ISessionDatasource#isClustered()
   */
  @Override
  public boolean isClustered() {
    return false;
  }

  @Override
  public String toString() {
    return "LocalDataSource [sessionIdToEntry=" + sessionIdToEntry + "]";
  }

  //simple class to reduce collections overhead.
  private class SessionEntry {
    BaseSession session;
    NetworkReqListener listener;

    @Override
    public String toString() {
      return "SessionEntry [session=" + session + ", listener=" + listener + "]";
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.common.api.data.ISessionDatasource#getDataFactory(java.
   * lang.Class)
   */
  @Override
  public IAppSessionDataFactory<? extends IAppSessionData> getDataFactory(Class<? extends IAppSessionData> x) {
    return this.appSessionDataFactories.get(x);
  }

}
