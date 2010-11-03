/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
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
