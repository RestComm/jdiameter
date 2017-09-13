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

package org.mobicents.diameter.impl.ha.data;

import java.util.HashMap;

import javax.transaction.TransactionManager;

import org.jdiameter.api.BaseSession;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.acc.IAccSessionData;
import org.jdiameter.common.api.app.auth.IAuthSessionData;
import org.jdiameter.common.api.app.cca.ICCASessionData;
import org.jdiameter.common.api.app.cxdx.ICxDxSessionData;
import org.jdiameter.common.api.app.gx.IGxSessionData;
import org.jdiameter.common.api.app.rf.IRfSessionData;
import org.jdiameter.common.api.app.ro.IRoSessionData;
import org.jdiameter.common.api.app.rx.IRxSessionData;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.app.sh.IShSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.data.LocalDataSource;
import org.jdiameter.common.api.app.slh.ISLhSessionData;
import org.jdiameter.common.api.app.slg.ISLgSessionData;
import org.restcomm.cache.FqnWrapper;
import org.restcomm.cache.MobicentsCache;
import org.restcomm.cluster.DataRemovalListener;
import org.restcomm.cluster.DefaultMobicentsCluster;
import org.restcomm.cluster.MobicentsCluster;
import org.restcomm.cluster.election.DefaultClusterElector;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.common.acc.AccReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.auth.AuthReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.cca.CCAReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.cxdx.CxDxReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.gx.GxReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.rf.RfReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.ro.RoReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.rx.RxReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.s13.S13ReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.sh.ShReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.slh.SLhReplicatedSessionDataFactory;
import org.mobicents.diameter.impl.ha.common.slg.SLgReplicatedSessionDataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replicated datasource implementation for {@link ISessionDatasource}
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ReplicatedSessionDatasource implements ISessionDatasource, DataRemovalListener {

  private static final Logger logger = LoggerFactory.getLogger(ReplicatedSessionDatasource.class);
  public static final String CLUSTER_DS_DEFAULT_FILE = "jdiameter-cache.xml";
  private IContainer container;
  private ISessionDatasource localDataSource;

  private DefaultMobicentsCluster mobicentsCluster;
  private boolean localMode;

  // provided by impl, no way to change that, no conf! :)
  protected HashMap<Class<? extends IAppSessionData>, IAppSessionDataFactory<? extends IAppSessionData>> appSessionDataFactories =
      new HashMap<Class<? extends IAppSessionData>, IAppSessionDataFactory<? extends IAppSessionData>>();

  // Constants
  // ----------------------------------------------------------------
  public static final String SESSIONS = "/diameter/appsessions";
  public static final FqnWrapper SESSIONS_FQN = FqnWrapper.fromStringWrapper(SESSIONS);

  public ReplicatedSessionDatasource(IContainer container) {
    this(container, new LocalDataSource(), ReplicatedSessionDatasource.class.getClassLoader().getResource(CLUSTER_DS_DEFAULT_FILE) == null ?
        "config/" + CLUSTER_DS_DEFAULT_FILE : CLUSTER_DS_DEFAULT_FILE);
  }

  public ReplicatedSessionDatasource(IContainer container, ISessionDatasource localDataSource, String cacheConfigFilename) {
    super();
    this.localDataSource = localDataSource;

    MobicentsCache mcCache = null;
    try {
      mcCache = new MobicentsCache(cacheConfigFilename);
    } catch (Exception e) {
      logger.debug("Could not create MobicentsCache: ", e);
    }

    TransactionManager txMgr = null;
    if (mcCache != null) {
      txMgr = mcCache.getTxManager();
    }
    this.mobicentsCluster = new DefaultMobicentsCluster(mcCache, txMgr, new DefaultClusterElector());
    this.mobicentsCluster.addDataRemovalListener(this); // register, so we know WHEN some other node removes session.
    this.mobicentsCluster.startCluster();

    this.container = container;
    // this is coded, it is tied to specific impl of SessionDatasource
    appSessionDataFactories.put(IAuthSessionData.class, new AuthReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IAccSessionData.class, new AccReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(ICCASessionData.class, new CCAReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IRoSessionData.class, new RoReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IRfSessionData.class, new RfReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IShSessionData.class, new ShReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(ICxDxSessionData.class, new CxDxReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IGxSessionData.class, new GxReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IRxSessionData.class, new RxReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(IS13SessionData.class, new S13ReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(ISLhSessionData.class, new SLhReplicatedSessionDataFactory(this));
    appSessionDataFactories.put(ISLgSessionData.class, new SLgReplicatedSessionDataFactory(this));

  }

  @Override
  public boolean exists(String sessionId) {
    return this.localDataSource.exists(sessionId) ? true : this.existReplicated(sessionId);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.ha.ISessionDatasource#addSession(org.jdiameter .api.BaseSession)
   */
  @Override
  public void addSession(BaseSession session) {
    // Simple as is, if its replicated, it will be already there :)
    this.localDataSource.addSession(session);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.ha.ISessionDatasource#getSession(java.lang.String )
   */
  @Override
  public BaseSession getSession(String sessionId) {
    if (this.localDataSource.exists(sessionId)) {
      return this.localDataSource.getSession(sessionId);
    }
    else if (!this.localMode && this.existReplicated(sessionId)) {
      this.makeLocal(sessionId);
      return this.localDataSource.getSession(sessionId);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.ha.ISessionDatasource#getSessionListener(java .lang.String)
   */
  @Override
  public NetworkReqListener getSessionListener(String sessionId) {
    if (this.localDataSource.exists(sessionId)) {
      return this.localDataSource.getSessionListener(sessionId);
    }
    else if (!this.localMode && this.existReplicated(sessionId)) {
      this.makeLocal(sessionId);
      return this.localDataSource.getSessionListener(sessionId);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.ha.ISessionDatasource#removeSession(java.lang .String)
   */
  @Override
  public void removeSession(String sessionId) {
    logger.debug("removeSession({}) in Local DataSource", sessionId);

    if (this.localDataSource.exists(sessionId)) {
      this.localDataSource.removeSession(sessionId);
    }
    else if (!this.localMode && this.existReplicated(sessionId)) {
      // FIXME: remove node.
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.ha.ISessionDatasource#removeSessionListener( java.lang.String)
   */
  @Override
  public NetworkReqListener removeSessionListener(String sessionId) {
    if (this.localDataSource.exists(sessionId)) {
      return this.localDataSource.removeSessionListener(sessionId);
    }
    else if (!this.localMode && this.existReplicated(sessionId)) {
      // does not make much sense ;[
      this.makeLocal(sessionId);
      return this.localDataSource.removeSessionListener(sessionId);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.ha.ISessionDatasource#setSessionListener(java .lang.String, org.jdiameter.api.NetworkReqListener)
   */
  @Override
  public void setSessionListener(String sessionId, NetworkReqListener data) {
    if (this.localDataSource.exists(sessionId)) {
      this.localDataSource.setSessionListener(sessionId, data);
    }
    else if (!this.localMode && this.existReplicated(sessionId)) {
      // does not make much sense ;[
      this.makeLocal(sessionId);
      this.localDataSource.setSessionListener(sessionId, data);
    }
  }

  @Override
  public void start() {
    mobicentsCluster.getMobicentsCache().startCache();
    localMode = mobicentsCluster.getMobicentsCache().isLocalMode();
  }

  @Override
  public void stop() {
    mobicentsCluster.getMobicentsCache().stopCache();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.data.ISessionDatasource#isClustered()
   */
  @Override
  public boolean isClustered() {
    return !localMode;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.data.ISessionDatasource#getDataFactory(java. lang.Class)
   */
  @Override
  public IAppSessionDataFactory<? extends IAppSessionData> getDataFactory(Class<? extends IAppSessionData> x) {
    return this.appSessionDataFactories.get(x);
  }

  // remove lst;

  public MobicentsCluster getMobicentsCluster() {
    return this.mobicentsCluster;
  }

  @Override
  public void dataRemoved(FqnWrapper sessionFqn) {
    String sessionId = (String) sessionFqn.getLastElement();
    this.localDataSource.removeSession(sessionId);
  }

  @Override
  public FqnWrapper getBaseFqn() {
    return SESSIONS_FQN;
  }

  /**
   * @param sessionId
   * @return
   */
  private boolean existReplicated(String sessionId) {
    if (!this.localMode &&
        this.mobicentsCluster.getMobicentsCache()
          .getCacheNode(FqnWrapper.fromRelativeElementsWrapper(SESSIONS_FQN, sessionId)) != null) {
      return true;
    }
    return false;
  }

  /**
   * @param sessionId
   */
  private void makeLocal(String sessionId) {
    try {
      // this is APP session, always
      Class<? extends AppSession> appSessionInterfaceClass =
          AppSessionDataReplicatedImpl.getAppSessionIface(this.mobicentsCluster.getMobicentsCache(), sessionId);
      // get factory;
      // FIXME: make it a field?
      IAppSessionFactory fct = ((ISessionFactory) this.container.getSessionFactory()).getAppSessionFactory(appSessionInterfaceClass);
      if (fct == null) {
        logger.warn("Session with id:{}, is in replicated data source, but no Application Session Factory for:{}.", sessionId, appSessionInterfaceClass);
        return;
      }
      else {
        BaseSession session = fct.getSession(sessionId, appSessionInterfaceClass);
        this.localDataSource.addSession(session);
        // hmmm
        this.localDataSource.setSessionListener(sessionId, (NetworkReqListener) session);
        return;
      }
    }
    catch (IllegalDiameterStateException e) {
      if (logger.isErrorEnabled()) {
        logger.error("Failed to obtain factory from stack...");
      }
    }
  }

  // ------- local getter

  public IContainer getContainer() {
    return this.container;
  }
}
