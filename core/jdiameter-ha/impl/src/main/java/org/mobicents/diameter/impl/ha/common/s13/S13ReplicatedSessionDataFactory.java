/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.diameter.impl.ha.common.s13;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.client.s13.ClientS13SessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.mobicents.diameter.impl.ha.server.s13.ServerS13SessionDataReplicatedImpl;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class S13ReplicatedSessionDataFactory implements IAppSessionDataFactory<IS13SessionData> {

	private ReplicatedSessionDatasource replicatedSessionDataSource;
	private MobicentsCluster mobicentsCluster;

	/**
	 * @param replicatedSessionDataSource
	 */
	public S13ReplicatedSessionDataFactory(ISessionDatasource replicatedSessionDataSource) { // Is this ok?
		super();
		this.replicatedSessionDataSource = (ReplicatedSessionDatasource) replicatedSessionDataSource;
		this.mobicentsCluster = this.replicatedSessionDataSource.getMobicentsCluster();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdiameter.common.api.app.IAppSessionDataFactory#getAppSessionData(java.lang.Class, java.lang.String)
	 */
	public IS13SessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
		if (clazz.equals(ClientS13Session.class)) {
			ClientS13SessionDataReplicatedImpl data = new ClientS13SessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
			return data;
		}
		else if (clazz.equals(ServerS13Session.class)) {
			ServerS13SessionDataReplicatedImpl data = new ServerS13SessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
			return data;
		}
		throw new IllegalArgumentException();
	}

}
