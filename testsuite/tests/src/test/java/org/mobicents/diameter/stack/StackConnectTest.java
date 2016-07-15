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

package org.mobicents.diameter.stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.server.impl.StackImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
@RunWith(Parameterized.class)
public class StackConnectTest {

  private static Logger logger = Logger.getLogger(StackConnectTest.class);

  // 1. start server
  // 2. start client + wait for connection
  // 3. stop client, start it again
  // 4. wait for connection
  String serverConfigName = "jdiameter-server-two.xml";
  String clientConfigName = "jdiameter-client-two.xml";

  public StackConnectTest(String serverConfigName, String clientConfigName){
    this.serverConfigName = serverConfigName;
    this.clientConfigName = clientConfigName;
  }
  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { "jdiameter-server-two.xml", "jdiameter-client-two.xml" },
      { "netty/tcp/jdiameter-server-two.xml", "netty/tcp/jdiameter-client-two.xml" },
      { "netty/tls/jdiameter-server-two.xml", "netty/tls/jdiameter-client-two.xml" }});
  }
  
  @Test
  public void testConnectUndefined() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    try {

      InputStream serverConfigInputStream = StackConnectTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackConnectTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);

      server.init(serverConfig);
      serverConfigInputStream.close();
      Network network = server.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      server.start();
      _wait();

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 0, peers.size());
      client.init(clientConfig);
      clientConfigInputStream.close();
      network = client.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      IPeer p = (IPeer) peers.get(0);
      assertTrue("Peer not connected. State[" + p.getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertEquals("Peer has wrong realm.","mobicents.org", p.getRealmName());


    }
    finally {
      try {
        client.stop(DisconnectCause.REBOOTING);
        client.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT stack.", e);
      }

      try {
        server.stop(DisconnectCause.REBOOTING);
        server.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy SERVER stack.", e);
      }
    }
  }


  private void _wait() throws InterruptedException {
    Thread.sleep(5000);
  }
}
