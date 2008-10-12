package org.jdiameter.server.impl.io;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.io.TransportError;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.server.api.io.INetWorkConnectionListener;
import org.jdiameter.server.api.io.INetWorkGuard;
import org.jdiameter.server.api.io.ITransportLayerFactory;
import org.jdiameter.server.impl.io.tcp.NetWorkGuard;

import java.net.InetAddress;

public class TransportLayerFactory extends org.jdiameter.client.impl.transport.TransportLayerFactory implements ITransportLayerFactory {

    public TransportLayerFactory(Configuration conf, IMessageParser parser) throws TransportException {
        super(conf, parser);
    }

    public INetWorkGuard createNetWorkGuard(InetAddress inetAddress, int port) throws TransportException {
        INetWorkGuard guard;
        try {
            guard = new NetWorkGuard(inetAddress, port, parser);
        } catch (Exception e) {
            throw new TransportException(TransportError.NetWorkError, e);
        }
        return guard;
    }

    public INetWorkGuard createNetWorkGuard(InetAddress inetAddress, final int port, final INetWorkConnectionListener listener) throws TransportException {
        INetWorkGuard guard;
        try {
            guard = new NetWorkGuard(inetAddress, port, parser);
        } catch (Exception e) {
            throw new TransportException(TransportError.NetWorkError, e);
        }
        guard.addListener(listener);
        return guard;
    }
}
