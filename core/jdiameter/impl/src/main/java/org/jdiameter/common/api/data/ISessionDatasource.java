/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.api.data;

import org.jdiameter.api.BaseSession;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.common.api.app.IAppSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ISessionDatasource {

  void start();

  void stop();

  public boolean exists(String sessionId);

  public NetworkReqListener getSessionListener(String sessionId);

  public void setSessionListener(String sessionId, NetworkReqListener data);

  public NetworkReqListener removeSessionListener(String sessionId);

  public void removeSession(String sessionId);

  public void addSession(BaseSession session);

  public BaseSession getSession(String sessionId);

  public boolean isClustered();

  public  IAppSessionDataFactory<? extends IAppSessionData> getDataFactory(Class<? extends IAppSessionData> x);

}
