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

package org.jdiameter.server.impl.agent;

import static org.jdiameter.client.impl.helpers.Parameters.Properties;
import static org.jdiameter.client.impl.helpers.Parameters.PropertyName;
import static org.jdiameter.client.impl.helpers.Parameters.PropertyValue;

import java.util.Properties;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.server.api.agent.IAgentConfiguration;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AgentConfigurationImpl implements IAgentConfiguration {

  private static final long serialVersionUID = 1L;

  protected Properties properties;

  /*
   * (non-Javadoc)
   * @see org.jdiameter.server.api.agent.IAgentConfiguration#getProperties()
   */
  @Override
  public Properties getProperties() {
    return this.properties;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.server.api.agent.IAgentConfiguration#parse(java.lang.String )
   */
  @Override
  public IAgentConfiguration parse(String agentConfiguration) throws InternalException {
    if (agentConfiguration == null) {
      return null;
    }
    AgentConfigurationImpl conf = new AgentConfigurationImpl();
    try {

      conf.properties = new Properties();
      String[] split = agentConfiguration.split(";");
      for (String s : split) {
        String[] data = s.split("=");
        conf.properties.put(data[0], data[1]);
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    return conf;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.server.api.agent.IAgentConfiguration#parse(org.jdiameter .api.Configuration)
   */
  @Override
  public IAgentConfiguration parse(Configuration agentConfiguration) throws InternalException {
    if (agentConfiguration == null) {
      return null;
    }
    AgentConfigurationImpl conf = new AgentConfigurationImpl();
    try {

      conf.properties = new Properties();
      Configuration[] propConfs = agentConfiguration.getChildren(Properties.ordinal());
      for (Configuration c : propConfs) {

        conf.properties.put(c.getStringValue(PropertyName.ordinal(), ""), c.getStringValue(PropertyValue.ordinal(), ""));
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    return conf;
  }

}
