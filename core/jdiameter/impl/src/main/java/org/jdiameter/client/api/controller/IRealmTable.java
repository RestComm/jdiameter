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

package org.jdiameter.client.api.controller;

import java.util.Collection;
import java.util.List;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Realm;
import org.jdiameter.api.RealmTable;
import org.jdiameter.client.api.IAnswer;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.server.api.agent.IAgentConfiguration;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IRealmTable extends RealmTable {

  Realm matchRealm(IRequest request);

  Realm matchRealm(IAnswer message, String destRealm);

  Realm getRealm(String realmName, ApplicationId applicationId);

  Realm removeRealmApplicationId(String realmName, ApplicationId appId);

  Collection<Realm> removeRealm(String realmName);

  Collection<Realm> getRealms(String realm);

  Collection<Realm> getRealms();

  String getRealmForPeer(String fqdn);

  void addLocalApplicationId(ApplicationId ap);

  void removeLocalApplicationId(ApplicationId a);

  void addLocalRealm(String localRealm, String fqdn);
  /**
   * Method which accepts IAgentConfiguration to avoid decode, encode, decode sequences
   * @param name
   * @param appId
   * @param locAction
   * @param agentConfImpl
   * @param isDynamic
   * @param expirationTime
   * @param hosts
   * @return
   * @throws InternalException
   */
  Realm addRealm(String name, ApplicationId appId, LocalAction locAction, IAgentConfiguration agentConfImpl, boolean isDynamic, long expirationTime,
      String[] hosts) throws InternalException;

  List<String> getAllRealmSet();
}
