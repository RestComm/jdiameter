/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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

package org.mobicents.diameter.stack.functional.sy.base;

import org.jdiameter.api.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * Policy and charging control, Spending Limit Report - Sy tests
 *
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */

@RunWith(Parameterized.class)
public class SySessionBasicFlowTest {

  private URI clientConfigUri;
  private URI serverConfigUri;

  private Client clientNode;
  private Server serverNode;

  /**
   * @param clientConfigUri
   * @param serverConfigUri
   */
  public SySessionBasicFlowTest(String clientConfigUri, String serverConfigUri) throws Exception {
    super();

    this.clientConfigUri = new URI(clientConfigUri);
    this.serverConfigUri = new URI(serverConfigUri);
  }

  @Before
  public void setUp() throws Exception {
    try {
      this.clientNode = new Client();
      this.serverNode = new Server();

      this.serverNode.init(new FileInputStream(new File(this.serverConfigUri)), "SERVER-SY");
      this.serverNode.start();

      this.clientNode.init(new FileInputStream(new File(this.clientConfigUri)), "CLIENT-SY");
      this.clientNode.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
      Stack stack = this.clientNode.getStack();

      List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();
      if (peers.size() == 1) {
        // ok
      } else if (peers.size() > 1) {
        // works better with replicated, since disconnected peers are also listed
        boolean foundConnected = false;
        for (Peer p : peers) {
          if (p.getState(PeerState.class).equals(PeerState.OKAY)) {
            if (foundConnected) {
              throw new Exception("Wrong number of connected peers: " + peers);
            }
            foundConnected = true;
          }
        }
      }
      else {
        throw new Exception("Wrong number of connected peers: " + peers);
      }
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() {
    if (this.serverNode != null) {
      try {
        this.serverNode.stop(DisconnectCause.REBOOTING);
      }
      catch (Exception e) {
      }
      this.serverNode = null;
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
      clientNode.sendInitial();
      waitForMessage();

      serverNode.sendInitial();
      waitForMessage();

      clientNode.sendIntermediate();
      waitForMessage();

      serverNode.sendIntermediate();
      waitForMessage();

      clientNode.sendTerminate();
      waitForMessage();

      serverNode.sendTerminate();
      waitForMessage();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.toString());
    }

    if (!clientNode.isReceivedINITIAL()) {
      StringBuilder sb = new StringBuilder("Did not receive INITIAL! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isReceivedINTERMEDIATE()) {
      StringBuilder sb = new StringBuilder("Did not receive INTERIM! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isReceivedTERMINATE()) {
      StringBuilder sb = new StringBuilder("Did not receive TERMINATE! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isPassed()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }

    if (!serverNode.isReceiveINITIAL()) {
      StringBuilder sb = new StringBuilder("Did not receive INITIAL! ");
      sb.append("Server ER:\n").append(serverNode.createErrorReport(this.serverNode.getErrors()));

      fail(sb.toString());
    }
    else if (!serverNode.isReceiveINTERMEDIATE()) {
      StringBuilder sb = new StringBuilder("Did not receive INTERIM! ");
      sb.append("Server ER:\n").append(serverNode.createErrorReport(this.serverNode.getErrors()));

      fail(sb.toString());
    }
    else if (!serverNode.isReceiveTERMINATE()) {
      StringBuilder sb = new StringBuilder("Did not receive TERMINATE! ");
      sb.append("Server ER:\n").append(serverNode.createErrorReport(this.serverNode.getErrors()));

      fail(sb.toString());
    }
    if (!serverNode.isPassed()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Server ER:\n").append(serverNode.createErrorReport(this.serverNode.getErrors()));

      fail(sb.toString());
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    String client = "configurations/functional-sy/config-client.xml";
    String server = "configurations/functional-sy/config-server.xml";

    //String replicatedClient = "configurations/functional-sy/replicated-config-client.xml";
    //String replicatedServer = "configurations/functional-sy/replicated-config-server.xml";

    Class<SySessionBasicFlowTest> t = SySessionBasicFlowTest.class;
    client = t.getClassLoader().getResource(client).toString();
    server = t.getClassLoader().getResource(server).toString();
    //replicatedClient = t.getClassLoader().getResource(replicatedClient).toString();
    //replicatedServer = t.getClassLoader().getResource(replicatedServer).toString();

    // return Arrays.asList(new Object[][] { { client, server }, { replicatedClient, replicatedServer } });
    return Arrays.asList(new Object[][] { { client, server } });
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
