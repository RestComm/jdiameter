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

package org.jdiameter.common.api.app.acc;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IAccSessionFactory extends IAppSessionFactory {

  /**
   * @return the serverSessionListener
   */
  public ServerAccSessionListener getServerSessionListener();

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  public void setServerSessionListener(ServerAccSessionListener serverSessionListener);

  /**
   * @return the stateListener
   */
  public StateChangeListener<AppSession> getStateListener();

  /**
   * @param stateListener
   *            the stateListener to set
   */
  public void setStateListener(StateChangeListener<AppSession> stateListener);

  /**
   * @return the clientSessionListener
   */
  public ClientAccSessionListener getClientSessionListener();

  /**
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  public void setClientSessionListener(ClientAccSessionListener clientSessionListener);

  /**
   * @return the clientContextListener
   */
  public IClientAccActionContext getClientContextListener();

  /**
   * @param clientContextListener
   *            the clientContextListener to set
   */
  public void setClientContextListener(IClientAccActionContext clientContextListener);

  /**
   * @return the serverContextListener
   */
  public IServerAccActionContext getServerContextListener();

  /**
   * @param serverContextListener
   *            the serverContextListener to set
   */
  public void setServerContextListener(IServerAccActionContext serverContextListener);

  public void setMessageFactory(IAccMessageFactory messageFactory);

  public IAccMessageFactory getMessageFactory();

  public ApplicationId getApplicationId();

  public void setApplicationId(ApplicationId id);

}
