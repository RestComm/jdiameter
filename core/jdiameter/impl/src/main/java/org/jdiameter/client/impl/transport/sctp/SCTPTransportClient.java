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

package org.jdiameter.client.impl.transport.sctp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.io.NotInitializedException;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.PayloadData;
import org.mobicents.protocols.sctp.AssociationImpl;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SCTPTransportClient {

  // How long to wait for the association to be connected
  private static final int CONNECT_TIMEOUT = 30000;
  // The waiting time between checks for association connection
  private static final int DELAY = 100;

  public static final String SCTP_BUFFER_SIZE_PARAMETER = "org.restcomm.sctp.bufferSize";
  public static final String SCTP_BUFFER_SIZE_DEFAULT = "65535";

  private ManagementImpl management = null;
  private AssociationImpl clientAssociation = null;
  private SCTPClientConnection parentConnection;
  private String clientAssociationName;
  protected InetSocketAddress destAddress;
  protected InetSocketAddress origAddress;
  private int payloadProtocolId = 0;
  private int streamNumber = 0;

  private static final Logger logger = LoggerFactory.getLogger(SCTPTransportClient.class);

  public SCTPTransportClient() {
  }

  /**
   * Default constructor
   *
   * @param concurrentFactory
   *          factory for create threads
   * @param parenConnection
   *          connection created this transport
   */
  SCTPTransportClient(SCTPClientConnection parenConnection) {
    this.parentConnection = parenConnection;
  }

  public void initialize() throws IOException, NotInitializedException {
    logger.debug("Initializing SCTPTransportClient. Origin address is [{}] and destination address is [{}]", origAddress,
        destAddress);
    if (destAddress == null) {
      throw new NotInitializedException("Destination address is not set");
    }

    logger.debug("Initializing SCTP client");

    clientAssociationName = origAddress.getAddress().getHostAddress() + "." + origAddress.getPort() + "_" +
        destAddress.getAddress().getHostAddress() + "." + destAddress.getPort();

    try {

      if (this.management == null) {
        this.management = new ManagementImpl(clientAssociationName);
        this.management.setSingleThread(true);
        this.management.setBufferSize(Integer.valueOf(System.getProperty(SCTP_BUFFER_SIZE_PARAMETER, SCTP_BUFFER_SIZE_DEFAULT)));
        logger.debug("SCTP Client Buffer Size set to {}", this.management.getBufferSize());
        this.management.start();
        this.management.setConnectDelay(1000); // Try connecting every 1 secs -- Note: 1st attempt is also delayed!
        // Clear any saved connections, we will get them from jdiameter-config.xml
        this.management.removeAllResourses();
        logger.debug("Management initialized.");
      }
      else {
        logger.debug("Management already initialized.");
      }

      if (this.clientAssociation == null) {
        logger.debug("Creating CLIENT ASSOCIATION '{}'. Origin Address [{}] <=> Dest Address [{}]", new Object[] {
            clientAssociationName, origAddress, destAddress });
        this.clientAssociation = this.management.addAssociation(origAddress.getAddress().getHostAddress(),
            origAddress.getPort(), destAddress.getAddress().getHostAddress(), destAddress.getPort(), clientAssociationName,
            IpChannelType.SCTP, null);
      }
      else {
        logger.debug("CLIENT ASSOCIATION '{}'. Origin Address [{}:{}] <=> Dest Address [{}:{}] already present. Re-using it.",
            new Object[] { clientAssociation.getName(), clientAssociation.getHostAddress(), clientAssociation.getHostPort(),
                clientAssociation.getPeerAddress(), clientAssociation.getPeerPort() });
      }

    }
    catch (Exception e) {
      logger.error("Failed to initialize client ", e);
    }
  }

  public SCTPClientConnection getParent() {
    return parentConnection;
  }

  public void start() throws NotInitializedException, IOException {
    // for client
    logger.debug("Starting SCTP client");
    try {
      this.clientAssociation.setAssociationListener(new ClientAssociationListener());
      this.management.startAssociation(clientAssociationName);
    }
    catch (Exception e) {
      logger.error("Failed to start client ", e);
    }

    if (getParent() == null) {
      throw new NotInitializedException("No parent connection is set");
    }

    logger.debug("Successfuly initialized SCTP Client Host [{}:{}] Peer [{}:{}]", new Object[] { clientAssociation.getHostAddress(),
        clientAssociation.getHostPort(), clientAssociation.getPeerAddress(), clientAssociation.getPeerPort() });
    logger.debug("Client Association Status: Started[{}] Connected[{}] Up[{}] ",
        new Object[]{clientAssociation.isStarted(), clientAssociation.isConnected(), clientAssociation.isUp()});
    logger.trace("Client Association [{}]", clientAssociation);
    defer();
  }

  private void defer() throws IOException {
    final long endTStamp = System.currentTimeMillis() + CONNECT_TIMEOUT;
    while (clientAssociation.isStarted() && !clientAssociation.isConnected() && !clientAssociation.isUp()) {
      try {
        Thread.sleep(DELAY);
      }
      catch (InterruptedException e) {
        // clear flag and proceed
        Thread.interrupted();
        throw new IOException("Failed to establish SCTP connection, thread was interrupted waiting for connection.");
      }
      if (endTStamp < System.currentTimeMillis()) {
        throw new IOException("Failed to establish SCTP connection!");
      }
    }
    logger.debug("Client Association Status: Started[{}] Connected[{}] Up[{}] ",
        new Object[]{clientAssociation.isStarted(), clientAssociation.isConnected(), clientAssociation.isUp()});
    logger.trace("Client Association [{}]", clientAssociation);
  }

  private class ClientAssociationListener implements AssociationListener {

    private final Logger logger = LoggerFactory.getLogger(ClientAssociationListener.class);

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
      byte[] data = new byte[payloadData.getDataLength()];
      System.arraycopy(payloadData.getData(), 0, data, 0, payloadData.getDataLength());
      logger.debug("SCTP Client received data of length [{}]", data.length);

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

  public void stop() throws Exception {
    // Stop the SCTP
    this.management.stopAssociation(clientAssociationName);
  }

  public void release() throws Exception {
    this.stop();
    this.management.removeAssociation(clientAssociationName);
    this.management.stop();
    this.clientAssociation = null;
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
      this.clientAssociation.send(payloadData);
    }
    catch (Exception e) {
      logger.error("Failed sending byte buffer over SCTP", e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Sent a byte buffer of size [{}] over SCTP", bytes.array().length);
    }
  }

  boolean isConnected() {
    return clientAssociation != null && this.clientAssociation.isConnected();
  }
}
