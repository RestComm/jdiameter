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
package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.client.impl.app.sh.ShClientSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.sh.IShSessionData;
import org.jdiameter.server.impl.app.sh.ShServerSessionDataLocalImpl;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShLocalSessionDataFactory implements IAppSessionDataFactory<IShSessionData>{

  /* (non-Javadoc)
   * @see org.jdiameter.common.api.app.IAppSessionDataFactory#getAppSessionData(java.lang.Class, java.lang.String)
   */
  @Override
  public IShSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if(clazz.equals(ClientShSession.class)) {
      ShClientSessionDataLocalImpl data = new ShClientSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    else if(clazz.equals(ServerShSession.class)) {
      ShServerSessionDataLocalImpl data = new ShServerSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    throw new IllegalArgumentException(clazz.toString());
  }

}
