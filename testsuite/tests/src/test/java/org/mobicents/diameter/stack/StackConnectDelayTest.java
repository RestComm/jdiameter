/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, TeleStax Inc. and individual contributors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.jdiameter.api.InternalException;
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
import org.mobicents.diameter.stack.functional.acc.base.AccSessionFTFlowTest;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
@RunWith(Parameterized.class)
public class StackConnectDelayTest {

  private static Logger logger = Logger.getLogger(StackConnectDelayTest.class);

  // 1. start server
  // 2. start client + wait for connection
  // 3. stop client, start it again
  // 4. wait for connection
  String serverConfigName;
  String client1ConfigName;
  String client2ConfigName;
  
  public StackConnectDelayTest(String serverConfigName, String client1ConfigName, String client2ConfigName){
   this.serverConfigName =serverConfigName;
   this.client1ConfigName = client1ConfigName;
   this.client2ConfigName = client2ConfigName;
  }

  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { "jdiameter-server-two-delay.xml", "jdiameter-client-two.xml", "jdiameter-client-two-second.xml" },
      { "netty/tcp/jdiameter-server-two-delay.xml", "netty/tcp/jdiameter-client-two.xml", "netty/tcp/jdiameter-client-two-second.xml" },
      { "netty/tls/jdiameter-server-two-delay.xml", "netty/tls/jdiameter-client-two.xml", "netty/tls/jdiameter-client-two-second.xml" }});
  }
  
  @Test
  public void testFirstPeerFailsSecondSucceeds() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client1 = new StackImpl();
    StackImpl client2 = new StackImpl();
    try {
      InputStream serverConfigInputStream = StackConnectDelayTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream client1ConfigInputStream = StackConnectDelayTest.class.getClassLoader().getResourceAsStream("configurations/" + client1ConfigName);
      InputStream client2ConfigInputStream = StackConnectDelayTest.class.getClassLoader().getResourceAsStream("configurations/" + client2ConfigName);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration client1Config = new org.jdiameter.server.impl.helpers.XMLConfiguration(client1ConfigInputStream);
      Configuration client2Config = new org.jdiameter.server.impl.helpers.XMLConfiguration(client2ConfigInputStream);

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

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 0, peers.size());
      client1.init(client1Config);
      client2.init(client2Config);
      client1ConfigInputStream.close();
      client2ConfigInputStream.close();
      network = client1.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));

      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));

      try {
        client1.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);
        fail("Client 1 should not be able to connect since server should not be bound.");
      }
      catch (InternalException ie) {
        // expected.
        assertEquals("Should have thrown a TimeOut InternalException", "TimeOut", ie.getMessage());
      }
      finally {
        try {
          client1.stop(DisconnectCause.REBOOTING);
          client1.destroy();
        }
        catch (Exception e) {
          logger.warn("Failed to stop/destroy CLIENT stack.", e);
        }
      }
      client2.start(Mode.ALL_PEERS, 20000, TimeUnit.MILLISECONDS);

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      IPeer p = (IPeer) peers.get(0);
      assertTrue("Peer not connected. State[" + p.getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertEquals("Peer has wrong realm.","mobicents.org", p.getRealmName());


    }
    finally {
      try {
        client2.stop(DisconnectCause.REBOOTING);
        client2.destroy();
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
