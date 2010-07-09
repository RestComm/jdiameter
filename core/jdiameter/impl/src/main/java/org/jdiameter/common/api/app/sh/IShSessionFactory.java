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
package org.jdiameter.common.api.app.sh;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.ServerShSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * Diameter Sh Session Factory
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IShSessionFactory extends IAppSessionFactory {

  public void setClientShSessionListener(ClientShSessionListener v);

  public ClientShSessionListener getClientShSessionListener();

  public void setServerShSessionListener(ServerShSessionListener v);

  public ServerShSessionListener getServerShSessionListener();

  public void setStateChangeListener(StateChangeListener<AppSession> v);

  public StateChangeListener<AppSession> getStateChangeListener();

  public void setMessageFactory(IShMessageFactory factory);

  public IShMessageFactory getMessageFactory();

}
