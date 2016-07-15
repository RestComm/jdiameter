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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
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

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class StackConnectMultiBaseTest {

  private static Logger logger = Logger.getLogger(StackConnectMultiBaseTest.class);

  public abstract String getServerConfigName();
  public abstract String getClient1ConfigName();
  public abstract String getClient2ConfigName();

  // 1. start server
  // 2. start client1 + wait for connection
  // 3. start client2 + wait for connection
  public void testConnectUndefined() throws Exception {
    Logger.getLogger("org.jdiameter").setLevel(Level.DEBUG);
    StackImpl server = new StackImpl();
    StackImpl client1 = new StackImpl();
    StackImpl client2 = new StackImpl();
    try {
      String serverConfigName = getServerConfigName();
      String clientConfigName1 = getClient1ConfigName();
      String clientConfigName2 = getClient2ConfigName();

      InputStream serverConfigInputStream = StackConnectMultiBaseTest.class.getClassLoader().getResourceAsStream(
          "configurations/" + serverConfigName);
      InputStream clientConfigInputStream1 = StackConnectMultiBaseTest.class.getClassLoader().getResourceAsStream(
          "configurations/" + clientConfigName1);
      InputStream clientConfigInputStream2 = StackConnectMultiBaseTest.class.getClassLoader().getResourceAsStream(
          "configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig1 = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream1);
      Configuration clientConfig2 = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream2);

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
      client1.init(clientConfig1);
      clientConfigInputStream1.close();
      network = client1.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client1.start(Mode.ALL_PEERS, 10000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      IPeer p = (IPeer) peers.get(0);
      assertTrue("Peer not connected. State[" + p.getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertEquals("Peer has wrong realm.", "mobicents.org", p.getRealmName());

      client2.init(clientConfig2);
      clientConfigInputStream1.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 10000000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());
      p = (IPeer) peers.get(0);
      assertTrue("Peer not connected. State[" + p.getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertEquals("Peer has wrong realm.", "mobicents.org", p.getRealmName());
      p = (IPeer) peers.get(1);
      assertTrue("Peer not connected. State[" + p.getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertEquals("Peer has wrong realm.", "mobicents.org", p.getRealmName());

    }
    finally {
      try {
        client1.stop(DisconnectCause.REBOOTING);
        client1.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT stack.", e);
      }

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
    Thread.sleep(3500);
  }
}
