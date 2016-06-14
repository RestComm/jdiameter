/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
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

package org.jdiameter.common.impl.app.slh;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.client.impl.app.slh.ClientSLhSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.slh.ISLhSessionData;
import org.jdiameter.server.impl.app.slh.ServerSLhSessionDataLocalImpl;

/**
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
 *
 */

public class SLhLocalSessionDataFactory implements IAppSessionDataFactory<ISLhSessionData> {

    public ISLhSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
        if (clazz.equals(ClientSLhSession.class)) {
            ClientSLhSessionDataLocalImpl data = new ClientSLhSessionDataLocalImpl();
            data.setSessionId(sessionId);
            return data;
        } else if (clazz.equals(ServerSLhSession.class)) {
            ServerSLhSessionDataLocalImpl data = new ServerSLhSessionDataLocalImpl();
            data.setSessionId(sessionId);
            return data;
        } else {
            throw new IllegalArgumentException("Invalid Session Class: " + clazz.toString());
        }
    }
}
