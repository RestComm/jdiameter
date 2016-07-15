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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
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

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class StackReConnectionTest {

  private static Logger logger = Logger.getLogger(StackReConnectionTest.class);

  // 1. start server
  // 2. start client + wait for connection
  // 3. stop client, start it again
  // 4. wait for connection

  @Test
  public void testReconnectionWithNewClient() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    try {
      String serverConfigName = "jdiameter-server-two.xml";
      String clientConfigName = "jdiameter-client-two.xml";

      InputStream serverConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);

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

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());

      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());

      client.stop(DisconnectCause.REBOOTING);
      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", !((IPeer) peers.get(0)).isConnected());

      client = new StackImpl();
      clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
      client.init(clientConfig);

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

      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
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

  @Test
  public void testReconnectionWithStoppedClient() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    try {
      String serverConfigName = "jdiameter-server-two.xml";
      String clientConfigName = "jdiameter-client-two.xml";

      InputStream serverConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);

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

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());

      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());

      client.stop(DisconnectCause.REBOOTING);
      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", !((IPeer) peers.get(0)).isConnected());

      client.start(Mode.ALL_PEERS, 15000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());

      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
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

  @Test
  public void testConnectTwoClients() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    StackImpl client2 = new StackImpl();
    try {
      String serverConfigName = "jdiameter-server-two.xml";
      String clientConfigName = "jdiameter-client-two.xml";
      String clientConfigName2 = "jdiameter-client-two-second.xml";

      InputStream serverConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      InputStream clientConfigInputStream2 = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
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

      client2.init(clientConfig2);
      clientConfigInputStream2.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());

      assertTrue("Peer1 not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertTrue("Peer2 not connected. State[" + ((IPeer) peers.get(1)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());

    }
    finally {
      try {
        client.stop(DisconnectCause.REBOOTING);
        client.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (1) stack.", e);
      }
      try {
        client2.stop(DisconnectCause.REBOOTING);
        client2.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (2) stack.", e);
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

  @Test
  public void testReConnectTwoClientsReused() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    StackImpl client2 = new StackImpl();
    try {
      String serverConfigName = "jdiameter-server-two.xml";
      String clientConfigName = "jdiameter-client-two.xml";
      String clientConfigName2 = "jdiameter-client-two-second.xml";

      InputStream serverConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      InputStream clientConfigInputStream2 = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
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

      client2.init(clientConfig2);
      clientConfigInputStream2.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());

      assertTrue("Peer1 not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertTrue("Peer2 not connected. State[" + ((IPeer) peers.get(1)).getState(PeerState.class) + "]", ((IPeer) peers.get(1)).isConnected());

      client.stop(DisconnectCause.REBOOTING);
      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());
      boolean foundConnected = false;
      boolean foundNotConnected = false;
      for (Peer p : peers) {
        if (((IPeer) p).isConnected()) {
          foundConnected = true;
        }
        else {
          foundNotConnected = true;
        }
      }

      assertTrue("Did not find connected client peer", foundConnected);
      assertTrue("Did not find not connected client peer", foundNotConnected);

      client.start(Mode.ALL_PEERS, 15000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());

      assertTrue("Peer1 not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertTrue("Peer2 not connected. State[" + ((IPeer) peers.get(1)).getState(PeerState.class) + "]", ((IPeer) peers.get(1)).isConnected());

    }
    finally {
      try {
        client.stop(DisconnectCause.REBOOTING);
        client.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (1) stack.", e);
      }
      try {
        client2.stop(DisconnectCause.REBOOTING);
        client2.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (2) stack.", e);
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

  @Test
  public void testClientReconnectOnServerRestart() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    StackImpl client2 = new StackImpl();
    try {
      String serverConfigName = "jdiameter-server-two.xml";
      String clientConfigName = "jdiameter-client-two.xml";
      String clientConfigName2 = "jdiameter-client-two-second.xml";

      InputStream serverConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      InputStream clientConfigInputStream2 = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
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

      client2.init(clientConfig2);
      clientConfigInputStream2.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());

      assertTrue("Peer1 not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertTrue("Peer2 not connected. State[" + ((IPeer) peers.get(1)).getState(PeerState.class) + "]", ((IPeer) peers.get(1)).isConnected());

      server.stop(DisconnectCause.REBOOTING);
      _wait();

      server.start();
      _wait();
      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());

      assertTrue("Peer1 not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertTrue("Peer2 not connected. State[" + ((IPeer) peers.get(1)).getState(PeerState.class) + "]", ((IPeer) peers.get(1)).isConnected());
    }
    finally {
      try {
        client.stop(DisconnectCause.REBOOTING);
        client.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (1) stack.", e);
      }
      try {
        client2.stop(DisconnectCause.REBOOTING);
        client2.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (2) stack.", e);
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

  @Test
  public void testClientNotReconnectingOnServerRestart() throws Exception {
    StackImpl server = new StackImpl();
    StackImpl client = new StackImpl();
    StackImpl client2 = new StackImpl();
    try {
      String serverConfigName = "jdiameter-server-two.xml";
      String clientConfigName = "jdiameter-client-two.xml";
      String clientConfigName2 = "jdiameter-client-two-second.xml";

      InputStream serverConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      InputStream clientConfigInputStream2 = StackReConnectionTest.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
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

      client2.init(clientConfig2);
      clientConfigInputStream2.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      _wait(); // FIXME: This should not be needed. We are checking before peer state is updated...

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 2, peers.size());

      assertTrue("Peer1 not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());
      assertTrue("Peer2 not connected. State[" + ((IPeer) peers.get(1)).getState(PeerState.class) + "]", ((IPeer) peers.get(1)).isConnected());

      server.stop(DisconnectCause.DO_NOT_WANT_TO_TALK_TO_YOU);

      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      int stoppedPeerTableSize = peers.size();

      server.start();

      _wait();
      _wait(); // just to make sure...

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong number of peers in table, probably clients tried to reconnect.", stoppedPeerTableSize, peers.size());

      for (Peer p : peers) {
        assertFalse("Peer [" + p.getUri() + "] is connected. State[" + p.getState(PeerState.class) + "]", ((IPeer) p).isConnected());
      }
    }
    finally {
      try {
        client.stop(DisconnectCause.REBOOTING);
        client.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (1) stack.", e);
      }
      try {
        client2.stop(DisconnectCause.REBOOTING);
        client2.destroy();
      }
      catch (Exception e) {
        logger.warn("Failed to stop/destroy CLIENT (2) stack.", e);
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
    Thread.sleep(4000);
  }
}
