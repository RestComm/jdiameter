/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc. and individual contributors
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

package org.mobicents.diameter.stack;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class StackConnectMultiTCPTest extends StackConnectMultiBaseTest {

  private String serverConfigName = "multi-tcp-jdiameter-server-two.xml";
  private String clientConfigName1 = "multi-tcp-jdiameter-client-one.xml";
  private String clientConfigName2 = "multi-tcp-jdiameter-client-two.xml";

  @Override
  public String getServerConfigName() {
    return serverConfigName;
  }

  @Override
  public String getClient1ConfigName() {
    return clientConfigName1;
  }

  @Override
  public String getClient2ConfigName() {
    return clientConfigName2;
  }

  // 1. start server
  // 2. start client1 + wait for connection
  // 3. start client2 + wait for connection
  @Override
  @Test
  public void testConnectUndefined() throws Exception {
    super.testConnectUndefined();
  }

}
