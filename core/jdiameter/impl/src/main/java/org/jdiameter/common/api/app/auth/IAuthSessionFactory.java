 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
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
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.common.api.app.auth;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * Interface for Diameter Authentication Session Factories
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IAuthSessionFactory extends IAppSessionFactory {

  IAuthMessageFactory getMessageFactory();

  void setMessageFactory(IAuthMessageFactory v);

  ServerAuthSessionListener getServerSessionListener();

  void setServerSessionListener(ServerAuthSessionListener v);

  StateChangeListener<AppSession> getStateListener();

  void setStateListener(StateChangeListener<AppSession> v);

  ClientAuthSessionListener getClientSessionListener();

  void setClientSessionListener(ClientAuthSessionListener v);

  IServerAuthActionContext getServerSessionContext();

  void setServerSessionContext(IServerAuthActionContext v);

  IClientAuthActionContext getClientSessionContext();

  void setClientSessionContext(IClientAuthActionContext v);

  boolean isStateles();

  void setStateles(boolean stateless);

  /**
   * @return the messageTimeout
   */
  long getMessageTimeout();

  /**
   * @param messageTimeout
   *            the messageTimeout to set
   */
  void setMessageTimeout(long messageTimeout);

}
