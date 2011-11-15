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

package org.jdiameter.server.impl;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IRouter;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RouterImpl extends org.jdiameter.client.impl.router.RouterImpl implements IRouter {

  //private static final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

  public RouterImpl(IContainer container,IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config, MetaData metaData) {
    super(container,concurrentFactory, realmTable, config, metaData);
  }

}
