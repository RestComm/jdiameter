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

package org.jdiameter.server.api.agent;

import java.io.Serializable;
import java.util.Properties;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;

/**
 * Interface through which agent can access configuration options for realm.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IAgentConfiguration extends Serializable {

  public Properties getProperties();

  /**
   * Parse resource and return implementation. May return null if pased argument is null.
   * @param agentConfiguration
   * @return
   * @throws InternalException 
   */
  public IAgentConfiguration parse(String agentConfiguration) throws InternalException;

  /**
   * @param agentConfiguration
   * @return
   */
  public IAgentConfiguration parse(Configuration agentConfiguration) throws InternalException;

}
