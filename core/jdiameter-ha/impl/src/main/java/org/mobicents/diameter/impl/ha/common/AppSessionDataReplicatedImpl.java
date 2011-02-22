/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
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
package org.mobicents.diameter.impl.ha.common;

import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.common.api.app.IAppSessionData;
import org.mobicents.cache.MobicentsCache;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.cluster.cache.ClusteredCacheData;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AppSessionDataReplicatedImpl extends ClusteredCacheData implements IAppSessionData {

  protected static final String SID = "SID";
  protected static final String APID = "APID";
  protected static final String SIFACE = "SIFACE";

  /**
   * @param nodeFqn
   * @param mobicentsCluster
   */
  public AppSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster) {
    super(nodeFqn, mobicentsCluster);
  }

  public AppSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster) {
    this(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster);
  }

  public static void setAppSessionIface(ClusteredCacheData ccd, Class<? extends AppSession> iface) {
    Node n = ccd.getMobicentsCache().getJBossCache().getNode(ccd.getNodeFqn());
    n.put(SIFACE, iface);
  }

  public static Class<? extends AppSession> getAppSessionIface(MobicentsCache mcCache, String sessionId) {
    Node n = mcCache.getJBossCache().getNode(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId));
    return (Class<AppSession>) n.get(SIFACE);
  }

  @Override
  public String getSessionId() {
    return (String) super.getNodeFqn().getLastElement();
  }

  @Override
  public void setApplicationId(ApplicationId applicationId) {
    if (exists()) {
      getNode().put(APID, applicationId);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public ApplicationId getApplicationId() {
    if (exists()) {
      return (ApplicationId) getNode().get(APID);
    }
    else {
      throw new IllegalStateException();
    }
  }

}
