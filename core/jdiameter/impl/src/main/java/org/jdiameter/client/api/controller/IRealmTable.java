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

package org.jdiameter.client.api.controller;

import java.util.Collection;

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

  public Realm matchRealm(IRequest request);

  public Realm matchRealm(IAnswer message, String destRealm);

  public Realm getRealm(String realmName, ApplicationId applicationId);

  public Realm removeRealmApplicationId(String realmName, ApplicationId appId);

  public Collection<Realm> removeRealm(String realmName);

  public Collection<Realm> getRealms(String realm);

  public Collection<Realm> getRealms();

  public String getRealmForPeer(String fqdn);

  public void addLocalApplicationId(ApplicationId ap);

  public void removeLocalApplicationId(ApplicationId a);

  public void addLocalRealm(String localRealm, String fqdn);
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
  public Realm addRealm(String name, ApplicationId appId, LocalAction locAction, IAgentConfiguration agentConfImpl, boolean isDynamic, long expirationTime,
		String[] hosts) throws InternalException;

}
