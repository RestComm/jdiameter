/*
 *
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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
 */

package org.mobicents.diameter.impl.ha.server.slh;

import org.restcomm.cache.FqnWrapper;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.app.slh.SLhSessionState;
import org.jdiameter.server.impl.app.slh.IServerSLhSessionData;
import org.restcomm.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.slh.SLhSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class ServerSLhSessionDataReplicatedImpl extends SLhSessionDataReplicatedImpl implements IServerSLhSessionData {

  /**
    * @param nodeFqnWrapper
    * @param mobicentsCluster
    * @param container
    */
  public ServerSLhSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqnWrapper, mobicentsCluster, container);

    if (super.create()) {
      setAppSessionIface(this, ServerSLhSession.class);
      setSLhSessionState(SLhSessionState.IDLE);
    }
  }

  /**
    * @param sessionId
    * @param mobicentsCluster
    * @param container
    */
  public ServerSLhSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(
        FqnWrapper.fromRelativeElementsWrapper(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }

}