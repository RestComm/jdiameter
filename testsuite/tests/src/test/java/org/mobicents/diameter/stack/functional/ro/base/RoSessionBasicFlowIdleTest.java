/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, TeleStax Inc. and individual contributors
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

package org.mobicents.diameter.stack.functional.ro.base;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Stack;
import org.junit.After;
import org.junit.Before;
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
public class RoSessionBasicFlowIdleTest {

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
  public RoSessionBasicFlowIdleTest(String clientConfigUrl, String serverNode1ConfigURL) throws Exception {
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
      this.serverNode1.start();

      this.clientNode.init(new FileInputStream(new File(this.clientConfigURI)), "CLIENT");
      this.clientNode.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
      Stack stack = this.clientNode.getStack();
      List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();
      if (peers.size() == 1) {
        // ok
      }
      else if (peers.size() > 1) {
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
      clientNode.sendInitial();
      waitForMessage();

      serverNode1.sendInitial();
      waitForMessage();

      clientNode.sendInterim();
      waitForMessage();

      serverNode1.sendInterim();
      waitForMessage(5500); // we wait a bit more so session expires with idle

      clientNode.sendTermination();
      waitForMessage();

      serverNode1.sendTermination();
      waitForMessage();
    }
    catch (Exception e) {
      // in case the session expired even before receiving the termination
      if (!"Request: null".equals(e.getMessage())) {
        e.printStackTrace();
        fail(e.toString());
      }
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
      // we don't fail here, we wanted session to be terminated when server tries to receive/send TERMINATE
    }
    if (clientNode.isReceiveTERMINATE()) {
      StringBuilder sb = new StringBuilder("Did receive TERMINATE, should not, session should have been terminated by idle! ");
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
    else if (!serverNode1.isReceiveINTERIM()) {
      StringBuilder sb = new StringBuilder("Did not receive INTERIM! ");
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
    else if (!serverNode1.isReceiveTERMINATE()) {
      // we don't fail here, we wanted session to be terminated when server tries to receive/send TERMINATE
    }
    if (!serverNode1.isPassed()) {
      // we don't fail here, we wanted session to be terminated when server tries to receive/send TERMINATE
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    String client = "configurations/functional-ro/config-client.xml";
    String server1 = "configurations/functional-ro/config-server-node1-session-idle.xml";

    String replicatedClient = "configurations/functional-ro/replicated-config-client.xml";
    String replicatedServer1 = "configurations/functional-ro/replicated-config-server-node1.xml";

    Class<RoSessionBasicFlowIdleTest> t = RoSessionBasicFlowIdleTest.class;
    client = t.getClassLoader().getResource(client).toString();
    server1 = t.getClassLoader().getResource(server1).toString();
    replicatedClient = t.getClassLoader().getResource(replicatedClient).toString();
    replicatedServer1 = t.getClassLoader().getResource(replicatedServer1).toString();

    return Arrays.asList(new Object[][] { { client, server1 }/*, { replicatedClient, replicatedServer1 }*/ });
  }

  private void waitForMessage(long time) {
    try {
      Thread.sleep(time);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void waitForMessage() {
    waitForMessage(2000);
  }

}
