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
 */

package org.jdiameter.server.impl;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IRouter;

/**
 * Just a simple counterpart of failure aware router defined for a client role.
 */
public class FailureAwareRouter extends org.jdiameter.client.impl.router.FailureAwareRouter implements IRouter {

  /**
   * Parameterized constructor. Should be called by any subclasses.
   */
  public FailureAwareRouter(IContainer container, IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config, MetaData aMetaData) {
    super(container, concurrentFactory, realmTable, config, aMetaData);
  }

}
