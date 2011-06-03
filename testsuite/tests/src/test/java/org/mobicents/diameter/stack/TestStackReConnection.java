/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack;

import static org.junit.Assert.assertEquals;
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
public class TestStackReConnection {

  private static Logger logger = Logger.getLogger(TestStackReConnection.class);

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

      InputStream serverConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);

      server.init(serverConfig);
      serverConfigInputStream.close();
      Network network = server.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

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

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());

      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());

      client.stop(DisconnectCause.REBOOTING);
      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", !((IPeer) peers.get(0)).isConnected());

      client = new StackImpl();
      clientConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
      client.init(clientConfig);

      network = client.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

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

      InputStream serverConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);

      server.init(serverConfig);
      serverConfigInputStream.close();
      Network network = server.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

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

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);

      List<Peer> peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());

      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", ((IPeer) peers.get(0)).isConnected());

      client.stop(DisconnectCause.REBOOTING);
      _wait();

      peers = server.unwrap(PeerTable.class).getPeerTable();
      assertEquals("Wrong num of connections, initial setup did not succeed. ", 1, peers.size());
      assertTrue("Peer not connected. State[" + ((IPeer) peers.get(0)).getState(PeerState.class) + "]", !((IPeer) peers.get(0)).isConnected());

      client.start(Mode.ALL_PEERS, 15000, TimeUnit.MILLISECONDS);

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

      InputStream serverConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      InputStream clientConfigInputStream2 = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
      Configuration clientConfig2 = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream2);

      server.init(serverConfig);
      serverConfigInputStream.close();
      Network network = server.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

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

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);
      client2.init(clientConfig2);
      clientConfigInputStream2.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);
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

      InputStream serverConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + serverConfigName);
      InputStream clientConfigInputStream = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName);
      InputStream clientConfigInputStream2 = TestStackReConnection.class.getClassLoader().getResourceAsStream("configurations/" + clientConfigName2);

      Configuration serverConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(serverConfigInputStream);
      Configuration clientConfig = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream);
      Configuration clientConfig2 = new org.jdiameter.server.impl.helpers.XMLConfiguration(clientConfigInputStream2);

      server.init(serverConfig);
      serverConfigInputStream.close();
      Network network = server.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

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

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);
      
      Thread.currentThread().sleep(1500);
      
      client2.init(clientConfig2);
      clientConfigInputStream2.close();
      network = client2.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        public Answer processRequest(Request request) {
          return null;
        }
      }, ApplicationId.createByAccAppId(193, 19302));
      client2.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);
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

  private void _wait() throws InterruptedException {
    Thread.sleep(5000);
  }
}