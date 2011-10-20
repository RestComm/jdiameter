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

package org.jdiameter.common.api.app.s6a;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.s6a.ClientS6aSessionListener;
import org.jdiameter.api.s6a.ServerS6aSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * Session Factory interface for Diameter S6a application.  
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:paul.carter-brown@smilecoms.com"> Paul Carter-Brown </a>
 */
public interface IS6aSessionFactory extends IAppSessionFactory {

  /**
   * Get stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   * 
   * @return the serverSessionListener
   */
  public ServerS6aSessionListener getServerSessionListener();

  /**
   * Set stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   * 
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  public void setServerSessionListener(ServerS6aSessionListener serverSessionListener);

  /**
   * Get stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   * 
   * @return the clientSessionListener
   */
  public ClientS6aSessionListener getClientSessionListener();

  /**
   * Set stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   * 
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  public void setClientSessionListener(ClientS6aSessionListener clientSessionListener);

  /**
   * @return the messageFactory
   */
  public IS6aMessageFactory getMessageFactory();

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  public void setMessageFactory(IS6aMessageFactory messageFactory);

  /**
   * @return the stateListener
   */
  public StateChangeListener<AppSession> getStateListener();

  /**
   * @param stateListener
   *            the stateListener to set
   */
  public void setStateListener(StateChangeListener<AppSession> stateListener);

}
