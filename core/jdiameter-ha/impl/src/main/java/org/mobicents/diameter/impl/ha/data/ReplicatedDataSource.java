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
package org.mobicents.diameter.impl.ha.data;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.config.Configuration.CacheMode;
import org.jdiameter.api.BaseSession;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.jdiameter.common.impl.data.LocalDataSource;
import org.mobicents.cache.MobicentsCache;
import org.mobicents.cluster.DefaultMobicentsCluster;
import org.mobicents.cluster.election.DefaultClusterElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replicated datasource implementation for {@link ISessionDatasource}
 *  
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ReplicatedDataSource implements ISessionDatasource {

  private static final Logger logger = LoggerFactory.getLogger(ReplicatedDataSource.class);
  public static final String CLUSTER_DS_DEFAULT_FILE = "config/jdiameter-jbc.xml";
  private IContainer container;

  private ISessionDatasource localDataSource;
  private DefaultMobicentsCluster mobicentsCluster;

  private boolean localMode;

  // Constants ----------------------------------------------------------------
  final static String SESSION = "session";
  final static String _SESSIONS = "/diameter/appsessions";
  @SuppressWarnings("unchecked")
  final static Fqn SESSIONS = Fqn.fromString(_SESSIONS);

  public ReplicatedDataSource(IContainer container) {
    this(container, new LocalDataSource(), CLUSTER_DS_DEFAULT_FILE);
  }

  @SuppressWarnings("unchecked")
  public ReplicatedDataSource(IContainer container, ISessionDatasource localDataSource, String cacheConffileName) {
    super();
    this.localDataSource = localDataSource;
    CacheFactory factory = new DefaultCacheFactory();
    Cache cache = factory.createCache(cacheConffileName, false);
    cache.start();

    MobicentsCache mcCache = new MobicentsCache(cache, "MC_JDiameter");
    // this requires JTA
    this.mobicentsCluster = new DefaultMobicentsCluster(mcCache, null, new DefaultClusterElector());
    this.container = container;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.ha.ISessionDatasource#addSession(org.jdiameter .api.BaseSession)
   */
  public void addSession(BaseSession session) {
	  SessionClusteredData scd = new SessionClusteredData(session.getSessionId(), mobicentsCluster);
    if (session.isReplicable()) {
      if (scd.exists()) {
        // ups?
        logger.warn("Session with id {} already present in clustered data.", session.getSessionId());
      }
      else {
        logger.debug("addSession({}) -\"{}\" in Replicated DataSource", new Object[]{session.getSessionId(),session});

        scd.create();
        scd.setSession(session);
      }
      this.localDataSource.removeSession(session.getSessionId());
    }
    else {
    	//check if there is such session in replicable source, this will hapen on recreating of replicable session.
    	if(scd.exists()) {
    		logger.debug("addSession({}) in Local DataSource, skipping, since replicable DS has this session.", session.getSessionId());
    	}
    	else {
    		logger.debug("addSession({}) in Local DataSource", session.getSessionId());
    		this.localDataSource.addSession(session);
    	}
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.ha.ISessionDatasource#getSession(java.lang.String )
   */
  public BaseSession getSession(String sessionId) {
    // first check local?
    BaseSession session = this.localDataSource.getSession(sessionId);
    if (session == null) {
      SessionClusteredData scd = new SessionClusteredData(sessionId, mobicentsCluster);
      if (scd.exists()) {
        logger.debug("getSession({}) in Replicated DataSource", sessionId);
        session = scd.getSession();
        logger.debug("getSession({}) in Replicated DataSource retrieved {}", sessionId, session);
      }
      else {
        logger.debug("getSession({}) in Replicated DataSource not found, returning null.", sessionId);
        return null;
      }
      if (session != null && session.isAppSession()) {
        AppSessionImpl appSession = (AppSessionImpl) session;
        appSession.relink(container);
      }
    }

    return session;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.ha.ISessionDatasource#getSessionListener(java .lang.String)
   */
  public NetworkReqListener getSessionListener(String sessionId) {
    logger.debug("getSessionListener({}) in Local DataSource", sessionId);
    NetworkReqListener lst = this.localDataSource.getSessionListener(sessionId);
    if (lst == null) {
      logger.debug("getSessionListener({}) in Replicated DataSource", sessionId);
      SessionClusteredData scd = new SessionClusteredData(sessionId, mobicentsCluster);
      if (scd.exists()) {
        lst = scd.getSessionListener();
        logger.debug("getSessionListener({}) in Replicated DataSource retrieved {}", sessionId, lst);
      }
      else {
        logger.debug("getSessionListener({}) in Replicated DataSource not found. Returning null.", sessionId);
        return null;
      }

      // now we must ensure session this listener points to is properly linked to local stack
      // this is expensive...
      if (lst != null && lst instanceof AppSessionImpl) {
        AppSessionImpl as = (AppSessionImpl) lst;
        as.relink(container);
      }
    }

    return lst;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.ha.ISessionDatasource#removeSession(java.lang .String)
   */
  public void removeSession(String sessionId) {
    logger.debug("removeSession({}) in Local DataSource", sessionId);
    this.localDataSource.removeSession(sessionId);
    SessionClusteredData scd = new SessionClusteredData(sessionId, mobicentsCluster);
    if (scd.exists()) {
      logger.debug("removeSession({}) in Replicated DataSource", sessionId);
      boolean removed = scd.remove();
      logger.debug("removeSession({}) in Replicated DataSource result: {}", sessionId, String.valueOf(removed));
    }
    else {
      // ups?
      logger.debug("removeSession({}) in Replicated DataSource. Not found.", sessionId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.ha.ISessionDatasource#removeSessionListener( java.lang.String)
   */
  public NetworkReqListener removeSessionListener(String sessionId) {
    logger.debug("removeSessionListener({}) in Local DataSource", sessionId);
    NetworkReqListener lst = this.localDataSource.removeSessionListener(sessionId);
    if (lst == null) {
      SessionClusteredData scd = new SessionClusteredData(sessionId, mobicentsCluster);
      if (scd.exists()) {
        logger.debug("removeSessionListener({}) in Replicated DataSource", sessionId);
        lst = scd.getSessionListener();
        scd.setSessionListener(null);
      }
      else {
        // ups?
        logger.debug("removeSessionListener({}) in Replicated DataSource. Not found.", sessionId);
      }
    }

    return lst;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.ha.ISessionDatasource#setSessionListener(java .lang.String, org.jdiameter.api.NetworkReqListener)
   */
  public void setSessionListener(String sessionId, NetworkReqListener data) {
    if (localDataSource.getSession(sessionId) != null) {
      logger.debug("setSessionListener({}, {}) in Local DataSource", sessionId, data);
      this.localDataSource.setSessionListener(sessionId, data);
    }
    else {
      logger.debug("setSessionListener({}, {}) in Replicated DataSource", sessionId, data);
      SessionClusteredData scd = new SessionClusteredData(sessionId, mobicentsCluster);
      if (scd.exists()) {
        scd.setSessionListener(data);
      }
      else {
        // ups?
        logger.warn("setSessionListener({}, {}) in Replicated DataSource. Session not found.", sessionId, data);
      }
    }
  }

  public void updateSession(BaseSession session) {
    SessionClusteredData scd = new SessionClusteredData(session.getSessionId(), mobicentsCluster);

    if (scd.exists()) {
      // scd.create();
      logger.debug("Update called on existing session: {}, Session: {}",new Object[]{session.getSessionId(),session});
      scd.setSession(session);
    }
    else {
      // ups?
      logger.warn("Update called on non existing session: {}",session.getSessionId());
    }
  }

  public void start() {
    getJBossCache().start();
    if (getJBossCache().getConfiguration().getCacheMode() == CacheMode.LOCAL) {
      localMode = true;
    }
  }

  public void stop() {
    getJBossCache().stop();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.data.ISessionDatasource#isClustered()
   */
  public boolean isClustered() {
    return !localMode;
  }

  // remove lst;

  @SuppressWarnings("unchecked")
  public Cache getJBossCache() {
    return this.mobicentsCluster.getMobicentsCache().getJBossCache();
  }

  @SuppressWarnings("unchecked")
  public void dataRemoved(Fqn sessionFqn) {
    String sessionId = (String) sessionFqn.getLastElement();
    this.localDataSource.removeSession(sessionId);
  }

  @SuppressWarnings("unchecked")
  public Fqn getBaseFqn() {
    return SESSIONS;
  }

}
