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
package org.mobicents.diameter.stack.functional.acc.base;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Stack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Simple test for stateful accounting
 *
 * 1 Servers (A), 1 Client (C)
 *
 * Flow:
 * 1. Client C sends INITIAL ACR to Server A;
 * 2. Server A receives INITIAL, creates new session, processes it under session, answers it;
 * 3. Client C sends INTERIM ACR to Server A;
 * 4. Server A receives INTERIM, processes it under session, answers it;
 * 5. Client C sends TERMINATE ACR to Server A;
 * 6. Server A receives TERMINATE, processes it under session, answers it;
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
@RunWith(Parameterized.class)
public class AccSessionStatefulBasicFlowTest {

  private Client clientNode;
  private Server serverNode1;
  private URI clientConfigURI;
  private URI serverNode1ConfigURI;

  /**
   * @param clientNode
   * @param node1
   * @param node2
   * @param serverCount
   */
  public AccSessionStatefulBasicFlowTest(String clientConfigUrl, String serverNode1ConfigURL) throws Exception {
    super();
    this.clientConfigURI = new URI(clientConfigUrl);
    this.serverNode1ConfigURI = new URI(serverNode1ConfigURL);
  }

  @Before
  public void setUp() throws Exception {
    try {
      this.clientNode = new Client();
      this.serverNode1 = new Server();

      this.serverNode1.init(new FileInputStream(new File(this.serverNode1ConfigURI)), "SERVER1");
      this.serverNode1.setStateless(false);
      this.serverNode1.start();

      this.clientNode.init(new FileInputStream(new File(this.clientConfigURI)), "CLIENT");
      this.clientNode.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
      Stack stack = this.clientNode.getStack();
      List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();
      if (peers.size() == 1) {
        // ok
      }
      else {
        throw new Exception("Wrong number of connected peers: " + peers);
      }
    }
    catch (Throwable e) {
      e.printStackTrace();
      fail("Setup failed: " + e.getMessage());
    }
  }

  @After
  public void tearDown() {
    if (this.serverNode1 != null) {
      try {
        this.serverNode1.stop(DisconnectCause.REBOOTING);
      }
      catch (Exception e) {

      }
      this.serverNode1 = null;
    }

    if (this.clientNode != null) {
      try {
        this.clientNode.stop(DisconnectCause.REBOOTING);
      }
      catch (Exception e) {

      }
      this.clientNode = null;
    }
  }

  @Test
  public void testBasicFlow() throws Exception {
    try {
      // pain of parameter tests :) ?
      waitForMessage();
      waitForMessage();
      waitForMessage();
      
      clientNode.sendInitial();
      waitForMessage();

      serverNode1.sendInitial();
      waitForMessage();

      clientNode.sendInterim();
      waitForMessage();

      serverNode1.sendInterim();
      waitForMessage();

      clientNode.sendTermination();
      waitForMessage();

      serverNode1.sendTermination();
      waitForMessage();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.toString());
    }

    if (!clientNode.isReceiveINITIAL()) {
      StringBuilder sb = new StringBuilder("Did not receive INITIAL! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isReceiveINTERIM()) {
      StringBuilder sb = new StringBuilder("Did not receive INTERIM! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isReceiveTERMINATE()) {
      StringBuilder sb = new StringBuilder("Did not receive TERMINATE! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isPassed()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }

    if (!serverNode1.isReceiveINITIAL()) {
      StringBuilder sb = new StringBuilder("Did not receive INITIAL! ");
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
    if (!serverNode1.isReceiveINTERIM()) {
      StringBuilder sb = new StringBuilder("Did not receive INTERIM! ");
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
    if (!serverNode1.isReceiveTERMINATE()) {
      StringBuilder sb = new StringBuilder("Did not receive TERMINATE! ");
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
    if (!serverNode1.isPassed()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    String client = "configurations/functional-acc/config-client.xml";
    String server1 = "configurations/functional-acc/config-server-node1.xml";

    Class<AccSessionStatefulBasicFlowTest> t = AccSessionStatefulBasicFlowTest.class;
    client = t.getClassLoader().getResource(client).toString();
    server1 = t.getClassLoader().getResource(server1).toString();
    
    String nettyTcpClient = "configurations/functional-acc/netty/tcp/config-client.xml";
    String nettyTcpServer1 = "configurations/functional-acc/netty/tcp/config-server-node1.xml";

    nettyTcpClient = t.getClassLoader().getResource(nettyTcpClient).toString();
    nettyTcpServer1 = t.getClassLoader().getResource(nettyTcpServer1).toString();

    String nettyTlsClient = "configurations/functional-acc/netty/tls/config-client.xml";
    String nettyTlsServer1 = "configurations/functional-acc/netty/tls/config-server-node1.xml";

    nettyTlsClient = t.getClassLoader().getResource(nettyTlsClient).toString();
    nettyTlsServer1 = t.getClassLoader().getResource(nettyTlsServer1).toString();

    return Arrays.asList(new Object[][] { { client, server1 }, {nettyTcpClient, nettyTcpServer1}, {nettyTlsClient, nettyTlsServer1} });
  }

  private void waitForMessage() {
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
