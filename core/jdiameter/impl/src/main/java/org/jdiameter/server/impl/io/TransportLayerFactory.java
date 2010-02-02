package org.jdiameter.server.impl.io;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.io.INetworkConnectionListener;
import org.jdiameter.server.api.io.INetworkGuard;
import org.jdiameter.server.api.io.ITransportLayerFactory;
import org.jdiameter.server.impl.io.tcp.NetworkGuard;

import java.net.InetAddress;

public class TransportLayerFactory extends org.jdiameter.client.impl.transport.TransportLayerFactory implements ITransportLayerFactory {

  private IConcurrentFactory concurrentFactory;

  public TransportLayerFactory(Configuration conf, IConcurrentFactory concurrentFactory, IMessageParser parser) throws TransportException {
    super(conf, parser);
    this.concurrentFactory = concurrentFactory;
  }

  public INetworkGuard createNetworkGuard(InetAddress inetAddress, int port) throws TransportException {
    INetworkGuard guard;
    try {
      guard = new NetworkGuard(inetAddress, port, concurrentFactory, parser);
    }
    catch (Exception e) {
      throw new TransportException(TransportError.NetWorkError, e);
    }
    return guard;
  }

  public INetworkGuard createNetworkGuard(InetAddress inetAddress, final int port, final INetworkConnectionListener listener) throws TransportException {
    INetworkGuard guard;
    try {
      guard = new NetworkGuard(inetAddress, port, concurrentFactory, parser);
    }
    catch (Exception e) {
      throw new TransportException(TransportError.NetWorkError, e);
    }
    guard.addListener(listener);
    return guard;
  }
}
