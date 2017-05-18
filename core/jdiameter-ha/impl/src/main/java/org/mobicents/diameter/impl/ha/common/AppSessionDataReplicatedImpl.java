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

  // Some util methods for handling primitives

  protected boolean toPrimitive(Boolean b, boolean _default) {
    return b == null ? _default : b;
  }

  protected int toPrimitive(Integer i) {
    return i == null ? NON_INITIALIZED : i;
  }

  protected long toPrimitive(Long l) {
    return l == null ? NON_INITIALIZED : l;
  }

}
