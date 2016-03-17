/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.api.app.rf;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.rf.ClientRfSessionListener;
import org.jdiameter.api.rf.ServerRfSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IRfSessionFactory extends IAppSessionFactory {

  /**
   * @return the serverSessionListener
   */
  public ServerRfSessionListener getServerSessionListener();

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  public void setServerSessionListener(ServerRfSessionListener serverSessionListener);

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
  public ClientRfSessionListener getClientSessionListener();

  /**
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  public void setClientSessionListener(ClientRfSessionListener clientSessionListener);

  /**
   * @return the clientContextListener
   */
  public IClientRfActionContext getClientContextListener();

  /**
   * @param clientContextListener
   *            the clientContextListener to set
   */
  public void setClientContextListener(IClientRfActionContext clientContextListener);

  /**
   * @return the serverContextListener
   */
  public IServerRfActionContext getServerContextListener();

  /**
   * @param serverContextListener
   *            the serverContextListener to set
   */
  public void setServerContextListener(IServerRfActionContext serverContextListener);

  /**
   * @return the messageTimeout
   */
  public long getMessageTimeout();

  /**
   * @param messageTimeout
   *            the messageTimeout to set
   */
  public void setMessageTimeout(long messageTimeout);

  public ApplicationId getApplicationId();

  public void setApplicationId(ApplicationId id);

}
