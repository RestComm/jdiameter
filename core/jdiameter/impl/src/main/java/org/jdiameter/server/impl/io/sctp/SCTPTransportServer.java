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

package org.jdiameter.server.impl.io.sctp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.io.NotInitializedException;
import org.jdiameter.client.impl.transport.sctp.SCTPTransportClient;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.Management;
import org.mobicents.protocols.api.PayloadData;
import org.mobicents.protocols.api.Server;
import org.mobicents.protocols.api.ServerListener;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SCTPTransportServer {

  private Management management = null;
  private Association serverAssociation = null;
  private Association remoteClientAssociation = null;
  private SCTPServerConnection parentConnection;
  private String serverAssociationName;
  private String remoteClientAssociationName;
  private String serverName;
  protected InetSocketAddress destAddress;
  protected InetSocketAddress origAddress;
  private Server server = null;
  private static final Logger logger = LoggerFactory.getLogger(SCTPTransportServer.class);
  private int payloadProtocolId = 0;
  private int streamNumber = 0;

  public SCTPTransportServer() {
  }

  /**
   * Default constructor
   *
   * @param parentConnectionConnection
   *          connection created this transport
   */
  SCTPTransportServer(SCTPServerConnection parentConnectionConnection) {
    this.parentConnection = parentConnectionConnection;
  }

  public SCTPServerConnection getParent() {
    return parentConnection;
  }

  public Management getManagement() {
    return management;
  }

  public void setManagement(Management management) {
    this.management = management;
  }

  public void startNewRemoteConnection(Server server, Association association, String peerAddress, int peerPort) {
    logger.debug("Initializing new Remote Connection '{}' -> '{}' ---> '{}:{}'", new Object[]{this.origAddress, this.destAddress, peerAddress, peerPort});
    remoteClientAssociationName = peerAddress + ":" + peerPort;
    serverName = server.getName();

    try {
      logger.debug("Adding new server association for [{}:{}]", peerAddress, peerPort);

      //remoteClientAssociation = management.addServerAssociation(peerAddress, peerPort, serverName, remoteClientAssociationName,
      //    IpChannelType.SCTP);

      //logger.debug("Setting new Association Listener");
      remoteClientAssociation = association;
      remoteClientAssociation.acceptAnonymousAssociation(new ServerAssociationListener());
      //remoteClientAssociation.setAssociationListener(new ServerAssociationListener());
      //logger.debug("Starting Association: {}", remoteClientAssociationName);
      //management.startAssociation(remoteClientAssociationName);

      // ammendonca: this is managed, no need to do it manually now.

      // logger.debug("Setting association socket channel");
      // remoteClientAssociation.setSocketChannel(socketChannel);
      // Accept the connection and make it non-blocking
      // socketChannel.configureBlocking(false);
      // Register the new SocketChannel with our Selector,
      // indicating we'd like to be notified when there's data
      // waiting to be read
      // logger.debug("registering socketchannel");
      // SelectionKey key1 = socketChannel.register(selector, SelectionKey.OP_READ);
      // logger.debug("Attaching server association to key1");
      // key1.attach(((Association) remoteClientAssociation));

      logger.info(String.format("Connected to {}", remoteClientAssociation));
    }
    catch (Exception e) {
      // ammendonca: this is managed, no need to do it manually now.
      // try {
      //   socketChannel.close();
      // }
      // catch (IOException ex) {
      //   logger.error("Error closing channel: " + ex.getMessage());
      // }

      logger.error("Failed to initialize new remote connection.", e);
    }
  }

  public void startServer() throws NotInitializedException {
    logger.debug("Initializing SCTP server");
    try {
      if (this.management == null) {
        this.management = new ManagementImpl("server-management-" + origAddress.getAddress().getHostAddress() + "."
            + origAddress.getPort());
        this.management.setBufferSize(Integer.valueOf(System.getProperty(
            SCTPTransportClient.SCTP_BUFFER_SIZE_PARAMETER, SCTPTransportClient.SCTP_BUFFER_SIZE_DEFAULT)));
        logger.debug("SCTP Server Buffer Size set to {}", this.management.getBufferSize());
        this.management.setSingleThread(true);
        this.management.start();
        // Clear any saved connections, we will get them from jdiameter-config.xml
        this.management.removeAllResourses();
      }

      logger.debug("Orig Address: '{}:{}'", origAddress.getAddress().getHostAddress(), origAddress.getPort());
      logger.debug("Dest Address: '{}'", this.destAddress);
      serverAssociationName = origAddress.getHostName() + ":" + origAddress.getPort();
      serverName = serverAssociationName;

      // Let's check if we already have the server configured
      for (Server s : management.getServers()) {
        if (s.getName().equals(serverName)) {
          server = s;
          break;
        }
      }

      // We don't have any, let's create it
      if (server == null) {
        server = this.management.addServer(serverName, origAddress.getAddress().getHostAddress(), origAddress.getPort(),
            IpChannelType.SCTP, true, 10, null);
      }

      for (String assocName : server.getAssociations()) {
        Association a = management.getAssociation(assocName);
        if (a.getName().equals(serverAssociationName)) {
          serverAssociation = a;
          break;
        }
      }

      if (serverAssociation == null) {
        serverAssociation = this.management.addServerAssociation(origAddress.getAddress().getHostAddress(), origAddress.getPort(), serverName,
            serverAssociationName, IpChannelType.SCTP);
      }

      this.management.setServerListener(new ServerEventListener());
      serverAssociation.setAssociationListener(new ServerAssociationListener());
      this.management.startAssociation(serverAssociationName);
      if (!server.isStarted()) {
        logger.debug("Starting server");
        this.management.startServer(serverName);
      }
    }
    catch (Exception e) {
      logger.error("Failed to initialize client ", e);
    }

    if (getParent() == null) {
      throw new NotInitializedException("No parent connection is set is set");
    }

    logger.debug("Successfuly initialized SCTP Server Host[{}:{}] Peer[{}:{}]",
        new Object[] { serverAssociation.getHostAddress(), serverAssociation.getHostPort(), serverAssociation.getPeerAddress(),
            serverAssociation.getPeerPort() });
    logger.debug("Server Association Status: Started[{}] Connected[{}] Up[{}] ",
        new Object[]{serverAssociation.isStarted(), serverAssociation.isConnected(), serverAssociation.isUp()});
    logger.trace("Server Association [{}]", serverAssociation);
  }

  private class ServerAssociationListener implements AssociationListener {

    private final Logger logger = LoggerFactory.getLogger(ServerAssociationListener.class);

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.api.AssociationListener#onCommunicationUp(org.mobicents.protocols.api.Association, int, int)
     */
    @Override
    public void onCommunicationUp(Association association, int maxInboundStreams, int maxOutboundStreams) {
      logger.debug("onCommunicationUp called for [{}]", this);
      getParent().onConnected();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.api.AssociationListener#onCommunicationShutdown(org.mobicents.protocols.api.Association)
     */
    @Override
    public void onCommunicationShutdown(Association association) {
      logger.debug("onCommunicationShutdown called for [{}]", this);
      try {
        getParent().onDisconnect();

        if (remoteClientAssociation != null) {
          management.stopAssociation(remoteClientAssociationName);
          management.removeAssociation(remoteClientAssociationName);
          remoteClientAssociation = null;
        }
      }
      catch (Exception e) {
        logger.debug("Error", e);
      }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.api.AssociationListener#onCommunicationLost(org.mobicents.protocols.api.Association)
     */
    @Override
    public void onCommunicationLost(Association association) {
      logger.debug("onCommunicationLost called for [{}]", this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.api.AssociationListener#onCommunicationRestart(org.mobicents.protocols.api.Association)
     */
    @Override
    public void onCommunicationRestart(Association association) {
      logger.debug("onCommunicationRestart called for [{}]", this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.api.AssociationListener#onPayload(org.mobicents.protocols.api.Association,
     * org.mobicents.protocols.api.PayloadData)
     */
    @Override
    public void onPayload(Association association, PayloadData payloadData) {
      // set payload and stream number values;
      payloadProtocolId = payloadData.getPayloadProtocolId();
      streamNumber = payloadData.getStreamNumber();

      byte[] data = new byte[payloadData.getDataLength()];
      System.arraycopy(payloadData.getData(), 0, data, 0, payloadData.getDataLength());
      logger.debug("SCTP Server received a message of length: [{}] ", data.length);

      try {
        // make a message out of data and process it
        getParent().onMessageReceived(ByteBuffer.wrap(data));
      }
      catch (AvpDataException e) {
        logger.debug("Garbage was received. Discarding.");
        // storage.clear();
        getParent().onAvpDataException(e);
      }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.api.AssociationListener#inValidStreamId(org.mobicents.protocols.api.PayloadData)
     */
    @Override
    public void inValidStreamId(PayloadData payloadData) {
      // NOP ?
    }
  }

  private class ServerEventListener implements ServerListener {

    private final Logger logger = LoggerFactory.getLogger(ServerEventListener.class);

    @Override
    public void onNewRemoteConnection(Server server, Association association) {
      logger.debug("Received notfification of a new remote connection!");

      try {
        // notify network guard that new remote connection is done!
        getParent().onNewRemoteConnection(server, association);

      }
      catch (Exception e) {
        try {
          // ammendonca: changed. is it right ?
          // socketChannel.close();
          association.stopAnonymousAssociation();
        }
        catch (Exception ex) {
          logger.error("Error closing channel: " + ex.getMessage());
        }
      }
    }
  }

  public void destroy() throws Exception {
    // Stop the SCTP
    logger.debug("Destroying SCTP Server");
    if (remoteClientAssociation != null) {
      this.management.stopAssociation(remoteClientAssociationName);
      this.management.removeAssociation(remoteClientAssociationName);
      remoteClientAssociation = null;
    }
    if (serverAssociation != null) {
      this.management.stopAssociation(serverAssociationName);
      this.management.removeAssociation(serverAssociationName);
      this.management.stopServer(serverName);
      this.management.removeServer(serverName);
      this.management.stop();
      serverAssociation = null;
    }
  }

  public void stop() throws Exception {
    logger.debug("Stopping SCTP Server");

    // Note we never stop the server association as it is always listening - we only stop the remote client association
    if (remoteClientAssociation != null) {
      this.management.stopAssociation(remoteClientAssociationName);
    }

  }

  public void release() throws Exception {
    logger.debug("Releasing SCTP Server");

    // Note we never release the server association as it is always listening - we only stop the remote client association
    this.stop();

    if (remoteClientAssociation != null) {
      this.management.removeAssociation(remoteClientAssociationName);
      remoteClientAssociation = null;
    }
    // destAddress = null;
  }

  public InetSocketAddress getDestAddress() {
    return this.destAddress;
  }

  public void setDestAddress(InetSocketAddress address) {
    this.destAddress = address;
    if (logger.isDebugEnabled()) {
      logger.debug("Destination address is set to [{}:{}]", destAddress.getHostName(), destAddress.getPort());
    }
  }

  public void setOrigAddress(InetSocketAddress address) {
    this.origAddress = address;
    if (logger.isDebugEnabled()) {
      logger.debug("Origin address is set to [{}:{}]", origAddress.getHostName(), origAddress.getPort());
    }
  }

  public InetSocketAddress getOrigAddress() {
    return this.origAddress;
  }

  public void sendMessage(ByteBuffer bytes) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("About to send a byte buffer of size [{}] over the SCTP", bytes.array().length);
    }

    PayloadData payloadData = new PayloadData(bytes.array().length, bytes.array(), true, false, payloadProtocolId, streamNumber);

    try {
      this.remoteClientAssociation.send(payloadData);
    }
    catch (Exception e) {
      logger.error("Failed sending byte buffer over SCTP", e);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Sent a byte buffer of size [{}] over SCTP", bytes.array().length);
    }
  }

  boolean isConnected() {
    if (remoteClientAssociation == null) {
      return false;
    }
    return this.remoteClientAssociation.isConnected();
  }
}