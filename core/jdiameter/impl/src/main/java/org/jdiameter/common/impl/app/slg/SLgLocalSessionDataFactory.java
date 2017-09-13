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

package org.jdiameter.common.impl.app.slg;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.client.impl.app.slg.ClientSLgSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.slg.ISLgSessionData;
import org.jdiameter.server.impl.app.slg.ServerSLgSessionDataLocalImpl;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

public class SLgLocalSessionDataFactory implements IAppSessionDataFactory<ISLgSessionData> {

  public ISLgSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientSLgSession.class)) {
      ClientSLgSessionDataLocalImpl data = new ClientSLgSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    } else if (clazz.equals(ServerSLgSession.class)) {
      ServerSLgSessionDataLocalImpl data = new ServerSLgSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    } else {
      throw new IllegalArgumentException("Invalid Session Class: " + clazz.toString());
    }
  }
}