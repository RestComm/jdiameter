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
public class SySessionFTFlowTest {

  private Client clientNode;
  private Server serverNode1;
  private Server serverNode2;
  private URI clientConfigUri;
  private URI serverNode1ConfigUri;
  private URI serverNode2ConfigUri;

  /**
   *
   */
  public SySessionFTFlowTest(String clientConfigUri, String serverNode1ConfigUri, String serverNode2ConfigUri) throws Exception {
    super();
    this.clientConfigUri = new URI(clientConfigUri);
    this.serverNode1ConfigUri = new URI(serverNode1ConfigUri);
    if (!serverNode2ConfigUri.equals("")) {
      this.serverNode2ConfigUri = new URI(serverNode2ConfigUri);
    }
  }

  @Before
  public void setUp() throws Exception {
    try {
      this.clientNode = new Client();
      this.serverNode1 = new Server();

      if (this.serverNode2ConfigUri != null) {
        this.serverNode2 = new Server();
        this.serverNode2.init(new FileInputStream(new File(this.serverNode2ConfigUri)), "SERVER2");
        this.serverNode2.start();
      }

      this.serverNode1.init(new FileInputStream(new File(this.serverNode1ConfigUri)), "SERVER1");
      this.serverNode1.start();

      this.clientNode.init(new FileInputStream(new File(this.clientConfigUri)), "CLIENT");
      this.clientNode.start(Mode.ALL_PEERS, 10, TimeUnit.SECONDS);

      Stack stack = this.clientNode.getStack();
      List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();
      if (this.serverNode2 == null && peers.size() == 1) {
        // ok
      }
      else if (this.serverNode2 != null && peers.size() == 2) {
        // ok
      }
      else {
        throw new Exception("Wrong number of connected peers: " + peers);
      }
      // give a wait time for cluster, it should be up and running without that, but... :)
      // ammendonca: commented, was throwing java.lang.IllegalMonitorStateException
      // Thread.currentThread().wait(15000);
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() {
    if (this.serverNode2 != null) {
      try {
        this.serverNode2.stop(DisconnectCause.REBOOTING);
      }
      catch (Exception e) {

      }
      this.serverNode2 = null;
    }

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
    Server backupServer = null; // server which we will use.
    Server serverToKill = null;
    try {

      clientNode.sendInitial();
      waitForMessage();

      // now lets check which server node got the msg.
      if (serverNode1.isReceiveINITIAL()) {
        backupServer = serverNode2;
        serverToKill = serverNode1;
      }
      else {
        backupServer = serverNode1;
        serverToKill = serverNode2;
      }

      serverToKill.sendInitial();
      waitForMessage();

      // kill
      serverToKill.stop(15, TimeUnit.SECONDS, DisconnectCause.REBOOTING);

      // now we have to update second server, so it gets session;
      //backupServer.fetchSession(clientNode.getSessionId());

      clientNode.sendIntermediate();
      waitForMessage();

      clientNode.sendTerminate();
      waitForMessage();

      backupServer.sendTerminate();
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
      StringBuilder sb = new StringBuilder("Did not receive INTERMEDIATE! ");
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

    if (backupServer != null) {
      if (backupServer.isReceiveINITIAL()) {
        StringBuilder sb = new StringBuilder("Received INITIAL! ");
        sb.append("Server ER:\n").append(backupServer.createErrorReport(backupServer.getErrors()));

        fail(sb.toString());
      }

      if (!backupServer.isReceiveTERMINATE()) {
        StringBuilder sb = new StringBuilder("Did not receive TERMINATE! ");
        sb.append("Server ER:\n").append(backupServer.createErrorReport(backupServer.getErrors()));

        fail(sb.toString());
      }

      if (!backupServer.isPassed()) {
        StringBuilder sb = new StringBuilder();
        sb.append("Server ER:\n").append(backupServer.createErrorReport(backupServer.getErrors()));

        fail(sb.toString());
      }

    }
    if (serverToKill != null) {
      if (!serverToKill.isReceiveINITIAL()) {
        StringBuilder sb = new StringBuilder("Did not receive INITIAL! ");
        sb.append("Server ER:\n").append(serverToKill.createErrorReport(serverToKill.getErrors()));

        fail(sb.toString());
      }

      if (serverToKill.isReceiveTERMINATE()) {
        StringBuilder sb = new StringBuilder("Received TERMINATE! ");
        sb.append("Server ER:\n").append(serverToKill.createErrorReport(serverToKill.getErrors()));

        fail(sb.toString());
      }

      if (!serverToKill.isPassed()) {
        StringBuilder sb = new StringBuilder();
        sb.append("Server ER:\n").append(serverToKill.createErrorReport(backupServer.getErrors()));

        fail(sb.toString());
      }
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    String replicatedClient = "configurations/functional-sy/replicated-config-client.xml";
    String replicatedServer1 = "configurations/functional-sy/replicated-config-server-node1.xml";
    String replicatedServer2 = "configurations/functional-sy/replicated-config-server-node2.xml";

    Class<SySessionFTFlowTest> t = SySessionFTFlowTest.class;

    replicatedClient = t.getClassLoader().getResource(replicatedClient).toString();
    replicatedServer1 = t.getClassLoader().getResource(replicatedServer1).toString();
    replicatedServer2 = t.getClassLoader().getResource(replicatedServer2).toString();

    return Arrays.asList(new Object[][] { { replicatedClient, replicatedServer1, replicatedServer2 } });
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
