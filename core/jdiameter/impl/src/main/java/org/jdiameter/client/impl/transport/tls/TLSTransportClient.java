 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
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
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.client.impl.transport.tls;

import static org.jdiameter.client.impl.helpers.Parameters.CipherSuites;
import static org.jdiameter.client.impl.helpers.Parameters.SDEnableSessionCreation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.io.NotInitializedException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TLSTransportClient {
  // NOTE: SSL Does not provide channels, need to do plain old sync R/W :/
  // So SSLSocket.getChannel() returns NULL!
  private static final Logger logger = LoggerFactory.getLogger(TLSTransportClient.class);
  private TLSClientConnection parentConnection;
  private IConcurrentFactory concurrentFactory;
  private boolean stop = false;

  // flag to indicate that initial shake did happen
  private boolean shaken;
  // flag indicating that SSL handshake is going on, while this is set to true, no messages can be exchanged.
  private boolean shaking;

  private Thread readThread;

  private InetSocketAddress destAddress;
  private InetSocketAddress origAddress;

  private String socketDescription = null;

  // sync streams to get data.
  private InputStream inputStream;
  private OutputStream outputStream;

  //private SSLSocket sslSocket;
  private Socket plainSocket;

  public static final int DEFAULT_BUFFER_SIZE = 4096;
  public static final int DEFAULT_STORAGE_SIZE = 4096;

  private int bufferSize = DEFAULT_BUFFER_SIZE;
  private ByteBuffer buffer = ByteBuffer.allocate(this.bufferSize);
  private int storageSize = DEFAULT_STORAGE_SIZE;
  private ByteBuffer storage = ByteBuffer.allocate(storageSize);

  private Lock lock = new ReentrantLock();

  private IMessageParser parser;

  private final DiameterSSLHandshakeListener handshakeListener = new DiameterSSLHandshakeListener();
  private final ReadTask readTash = new ReadTask();

  //tell weather we are in a client mode
  private boolean client;
  private boolean receivedInband;
  /**
   * Default constructor
   *
   * @param parenConnection
   *          connection created this transport
   */
  public TLSTransportClient(TLSClientConnection parenConnection, IConcurrentFactory concurrentFactory, IMessageParser parser) {
    this.parentConnection = parenConnection;
    this.concurrentFactory = concurrentFactory;
    this.parser = parser;
  }

  public void initialize() throws IOException, NotInitializedException {
    if (destAddress == null) {
      throw new NotInitializedException("Destination address is not set");
    }
    this.client = true;
    // SSLSocketFactory cltFct = parentConnection.getSSLFactory();
    // this.sslSocket = (SSLSocket) cltFct.createSocket();
    //
    // this.sslSocket.setEnableSessionCreation(parentConnection.getSSLConfig().getBooleanValue(SDEnableSessionCreation.ordinal(), true));
    // this.sslSocket.setUseClientMode(true);
    // if (parentConnection.getSSLConfig().getStringValue(CipherSuites.ordinal(), null) != null) {
    //   this.sslSocket.setEnabledCipherSuites(parentConnection.getSSLConfig().getStringValue(CipherSuites.ordinal(), null).split(","));
    // }
    //
    // if (this.origAddress != null) {
    //   this.sslSocket.bind(this.origAddress);
    // }
    // this.sslSocket.connect(this.destAddress);
    //
    // // now lets get streams.
    // this.sslInputStream = this.sslSocket.getInputStream();
    // this.sslOutputStream = this.sslSocket.getOutputStream();
    this.plainSocket = new Socket();
    if (this.origAddress != null) {
      this.plainSocket.bind(this.origAddress);
    }
    this.plainSocket.connect(this.destAddress);
    this.inputStream = this.plainSocket.getInputStream();
    this.outputStream = this.plainSocket.getOutputStream();
    // now, we need to notify parent, this will START CER/CEA exchange
    // on CEA 2xxx we can enable TLS
    parentConnection.onConnected();
  }

  public void initialize(Socket socket) throws IOException, NotInitializedException {
    logger.debug("Initialising TLSTransportClient for a socket on [{}]", socket);
    this.client = false;
    this.plainSocket = socket;
    this.socketDescription = socket.toString();

    this.destAddress = new InetSocketAddress(socket.getInetAddress(), socket.getPort());

    this.inputStream = this.plainSocket.getInputStream();
    this.outputStream = this.plainSocket.getOutputStream();
  }

  public void start() throws NotInitializedException {
    // for client
    if (this.socketDescription == null) {
      this.socketDescription = this.plainSocket.toString();
    }
    logger.debug("Starting transport. Socket is {}", socketDescription);

    if (!this.plainSocket.isConnected()) {
      throw new NotInitializedException("Socket is not connected");
    }
    if (getParent() == null) {
      throw new NotInitializedException("No parent connection is set is set");
    }
    if (this.readThread == null || !this.readThread.isAlive()) {
      this.readThread = this.concurrentFactory.getThread("TLSReader", this.readTash);
    }

    if (!this.readThread.isAlive()) {
      this.readThread.setDaemon(true);
      this.readThread.start();
    }
  }

  // ---------------- getters & setters ---------------------

  public TLSClientConnection getParent() {
    return parentConnection;
  }

  public InetSocketAddress getDestAddress() {
    return this.destAddress;
  }

  public void setDestAddress(InetSocketAddress address) {
    this.destAddress = address;
    if (logger.isDebugEnabled()) {
      logger.debug("Destination address is set to [{}] : [{}]", destAddress.getHostName(), destAddress.getPort());
    }
  }

  public void setOrigAddress(InetSocketAddress address) {
    this.origAddress = address;
    if (logger.isDebugEnabled()) {
      logger.debug("Origin address is set to [{}] : [{}]", origAddress.getHostName(), origAddress.getPort());
    }
  }

  public InetSocketAddress getOrigAddress() {
    return this.origAddress;
  }

  // ---------------- helper methods ---------------------
  void sendMessage(IMessage message) throws IOException, AvpDataException, NotInitializedException, ParseException {

    if (!isConnected()) {
      throw new IOException("Failed to send message over [" + socketDescription + "]");
    }

    //switch to wait for SSL handshake to workout.
    if (!isExchangeAllowed()) {
      //TODO: do more?
      return;
    }

    doTLSPreSendProcessing(message);

    final ByteBuffer messageBuffer = this.parser.encodeMessage(message);
    if (logger.isDebugEnabled()) {
      logger.debug("About to send a byte buffer of size [{}] over the TLS socket [{}]", messageBuffer.array().length, socketDescription);
    }
    lock.lock();
    try {
      this.outputStream.write(messageBuffer.array(), messageBuffer.position(), messageBuffer.limit());
      doTLSPostSendProcessing(message);

    } catch (Exception e) {
      logger.debug("Unable to send message", e);
      throw new IOException("Error while sending message: " + e);
    }
    finally {
      lock.unlock();
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Sent a byte buffer of size [{}] over the TLS nio socket [{}]", messageBuffer.array().length, socketDescription);
    }
  }


  boolean isConnected() {
    return this.plainSocket != null && this.plainSocket.isConnected();
  }

  void stop() throws Exception {
    logger.debug("Stopping transport. Socket is [{}]", socketDescription);
    stop = true;
    if (plainSocket != null && !plainSocket.isClosed()) {
      plainSocket.close();
    }
    if (this.readThread != null) {
      this.readThread.join(100);
    }
    clearBuffer();
    logger.debug("Transport is stopped. Socket is [{}]", socketDescription);
  }

  public void release() throws Exception {
    stop();
    destAddress = null;
  }

  void append(byte[] data) {
    if (storage.position() + data.length >= storage.capacity()) {
      ByteBuffer tmp = ByteBuffer.allocate(storage.limit() + data.length * 2);
      byte[] tmpData = new byte[storage.position()];
      storage.flip();
      storage.get(tmpData);
      tmp.put(tmpData);
      storage = tmp;
      logger.warn("Increase storage size. Current size is {}", storage.array().length);
    }

    try {
      storage.put(data);
    }
    catch (BufferOverflowException boe) {
      logger.error("Buffer overflow occured", boe);
    }
    boolean messageReseived;
    do {
      messageReseived = seekMessage(storage);
    } while (messageReseived);
  }

  private boolean isExchangeAllowed() {
    this.lock.lock();
    try {
      return !this.shaking;
    } finally {
      this.lock.unlock();
    }
  }

  private boolean isSuccess(IMessage message) throws AvpDataException {
    Avp resultAvp = message.getResultCode();
    if (resultAvp == null) {
      resultAvp = message.getAvps().getAvp(Avp.EXPERIMENTAL_RESULT);
      if (resultAvp == null) {
        // bad message, ignore
        if (logger.isDebugEnabled()) {
          logger.debug("Discarding message since SSL handshake has not been performed on [{}], dropped message [{}]. No result type avp.",
              socketDescription, message);
        }
        // TODO: anything else?
        return false;

      }
      resultAvp = resultAvp.getGrouped().getAvp(Avp.EXPERIMENTAL_RESULT_CODE);
      if (resultAvp == null) {
        // bad message, ignore
        if (logger.isDebugEnabled()) {
          logger.debug("Discarding message since SSL handshake has not been performed on [{}], dropped message [{}]. No result avp.",
              socketDescription, message);
        }
      }
    }
    long resultCode = resultAvp.getUnsigned32();
    return resultCode >= 2000 && resultCode < 3000;
  }

  private boolean seekMessage(ByteBuffer localStorage) {
    if (storage.position() == 0) {
      return false;
    }

    storage.flip();
    int tmp = localStorage.getInt();
    localStorage.position(0);

    byte vers = (byte) (tmp >> 24);
    if (vers != 1) {
      return false;
    }
    int dataLength = (tmp & 0xFFFFFF);

    if (localStorage.limit() < dataLength) {
      localStorage.position(localStorage.limit());
      localStorage.limit(localStorage.capacity());
      return false;
    }

    byte[] data = new byte[dataLength];
    localStorage.get(data);
    localStorage.position(dataLength);
    localStorage.compact();

    ByteBuffer messageBuffer = ByteBuffer.wrap(data);
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Received message of size [{}]", data.length);
      }

      IMessage message = this.parser.createMessage(messageBuffer);
      // check if
      if (isExchangeAllowed()) {
        doTLSPreReceiveProcessing(message);
        getParent().onMessageReceived(message);
      }
    }
    catch (Exception e) {
      logger.debug("Garbage was received. Discarding.");
      storage.clear();
      // not a best way.
      getParent().onAvpDataException(new AvpDataException(e));
    }
    return true;
  }

  /**
   * @param message
   * @throws AvpDataException
   * @throws NotInitializedException
   */
  private void doTLSPreReceiveProcessing(IMessage message) throws AvpDataException, NotInitializedException {
    if (this.shaken) {
      return;
    }
    if (this.client) {
      // if (CEA && message.isSuccess && message.has(inband)) {
      // startTLS();
      // }
      if (message.isRequest()) {
        return;
      }
      if (message.getCommandCode() == Message.CAPABILITIES_EXCHANGE_ANSWER && isSuccess(message)) {
        AvpSet set = message.getAvps();
        Avp inbandAvp = set.getAvp(Avp.INBAND_SECURITY_ID);
        if (inbandAvp != null && inbandAvp.getUnsigned32() == 1) {
          startTLS();
        }
      }

    } else {
      // if (CER && message.has(inband)) {
      // this.receveidInband = true;
      // }
      if (!message.isRequest()) {
        return;
      }
      AvpSet set = message.getAvps();
      Avp inbandAvp = set.getAvp(Avp.INBAND_SECURITY_ID);
      if (inbandAvp != null && inbandAvp.getUnsigned32() == 1) {
        this.receivedInband = true;
      }

    }
  }

  /**
   * @param message
   */
  private void doTLSPreSendProcessing(IMessage message) {
    if (message.getCommandCode() == Message.CAPABILITIES_EXCHANGE_REQUEST) {
      AvpSet set = message.getAvps();
      set.removeAvp(Avp.INBAND_SECURITY_ID);
      set.addAvp(Avp.INBAND_SECURITY_ID, 1);
    }
  }

  /**
   * @param message
   * @throws AvpDataException
   * @throws NotInitializedException
   */
  private void doTLSPostSendProcessing(IMessage message) throws AvpDataException, NotInitializedException {
    // if ( !client && !shaken && CEA && message.isSuccess() && receivedInband) {
    // startTLS;
    // }

    if (this.shaken || this.client || this.plainSocket instanceof SSLSocket || message.isRequest()
        || message.getCommandCode() != Message.CAPABILITIES_EXCHANGE_ANSWER) {
      return;
    }

    if (this.receivedInband && isSuccess(message)) {
      this.receivedInband = false;
      startTLS();
    }
  }

  /**
   * @throws NotInitializedException
   *
   */
  private void startTLS() throws NotInitializedException {
    try {
      this.shaking = true;
      SSLSocketFactory cltFct = parentConnection.getSSLFactory();
      SSLSocket sslSocket = (SSLSocket) cltFct.createSocket(this.plainSocket, null, this.plainSocket.getPort(), false);

      sslSocket.setEnableSessionCreation(parentConnection.getSSLConfig().getBooleanValue(
          SDEnableSessionCreation.ordinal(), true));
      // only clients start shake
      if (parentConnection.getSSLConfig().getStringValue(CipherSuites.ordinal(), null) != null) {
        sslSocket.setEnabledCipherSuites(parentConnection.getSSLConfig().getStringValue(CipherSuites.ordinal(), null)
            .split(","));
      }

      this.inputStream = sslSocket.getInputStream();
      this.outputStream = sslSocket.getOutputStream();
      this.plainSocket = sslSocket;

      if (this.client) {
        sslSocket.setUseClientMode(true);
        // TODO: catch this to check for failure
        sslSocket.addHandshakeCompletedListener(this.handshakeListener);
        sslSocket.startHandshake();
      } else {
        sslSocket.addHandshakeCompletedListener(this.handshakeListener);
        sslSocket.setUseClientMode(false);
      }

    } catch (Exception e) {
      // TODO: ensure close?
      throw new NotInitializedException(e);
    }
  }

  private void clearBuffer() throws IOException {
    bufferSize = DEFAULT_BUFFER_SIZE;
    buffer = ByteBuffer.allocate(bufferSize);
  }

  // ---------------- helper classes ---------------------
  private class DiameterSSLHandshakeListener implements HandshakeCompletedListener {

    @Override
    public void handshakeCompleted(HandshakeCompletedEvent event) {
      // connected comes from here!
      try {
        lock.lock();
        shaking = false;
        shaken = true;
        ((SSLSocket) plainSocket).removeHandshakeCompletedListener(this);
        getParent().onConnected();
      }
      finally {
        lock.unlock();
      }
    }
  }

  private class ReadTask implements Runnable {

    @Override
    public void run() {
      logger.debug("Transport is started. Socket is [{}]", socketDescription);
      try {
        while (!stop) {
          int dataLength = inputStream.read(buffer.array());
          logger.debug("Just read [{}] bytes on [{}]", dataLength, socketDescription);
          if (dataLength == -1) {
            break;
          }
          buffer.position(dataLength);
          buffer.flip();
          byte[] data = new byte[buffer.limit()];
          buffer.get(data);
          append(data);
          buffer.clear();
        }
      }
      catch (ClosedByInterruptException e) {
        logger.debug("Transport exception ", e);
      }
      catch (AsynchronousCloseException e) {
        logger.debug("Transport exception ", e);
      }
      catch (Throwable e) {
        logger.debug("Transport exception ", e);
      }
      finally {
        try {
          clearBuffer();
          if (plainSocket != null && !plainSocket.isClosed()) {
            plainSocket.close();
          }
          getParent().onDisconnect();
        }
        catch (Exception e) {
          logger.debug("Error", e);
        }
        stop = false;
        logger.info("Read thread is stopped for socket [{}]", socketDescription);
      }
    }
  }

}