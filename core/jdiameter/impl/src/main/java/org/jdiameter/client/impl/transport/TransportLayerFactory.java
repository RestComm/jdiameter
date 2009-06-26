package org.jdiameter.client.impl.transport;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.client.api.io.*;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.helpers.Parameters;

import static java.lang.Class.forName;
import java.lang.reflect.Constructor;
import java.net.InetAddress;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
public class TransportLayerFactory implements ITransportLayerFactory {

    protected Class<IConnection> connectionClass;
    protected Constructor<IConnection> constructorIAi, constructorIAiCL;
    protected IMessageParser parser;
    protected Configuration config;

    public TransportLayerFactory(Configuration config, IMessageParser parser) throws TransportException {
        this.config = config;
        String implName = config.getStringValue(
                Parameters.ConnectionImplClass.ordinal(), (String) Parameters.ConnectionImplClass.defValue()
        );
        try {
            this.connectionClass = (Class<IConnection>) forName(implName);
            Class[] interf = this.connectionClass.getInterfaces();
            boolean isIConnection = false;
            for (Class c : interf)
                if (c.equals(IConnection.class)) {
                    isIConnection = true;
                    break;
                }
            if (!isIConnection)
                throw new TransportException("Specified class does not inherit IConnection interface " + this.connectionClass, TransportError.Internal);
        } catch (Exception e) {
            throw new TransportException("Cannot prepare specified connection class " + this.connectionClass, TransportError.Internal, e);
        }
        try {
            constructorIAiCL = connectionClass.getConstructor(
                    Configuration.class, InetAddress.class, Integer.TYPE, InetAddress.class, Integer.TYPE, IConnectionListener.class, IMessageParser.class, String.class
            );
            constructorIAi = connectionClass.getConstructor(
                    Configuration.class, InetAddress.class, Integer.TYPE, InetAddress.class, Integer.TYPE, IMessageParser.class, String.class
            );
        } catch (Exception e) {
            throw new TransportException("Cannot find required constructor", TransportError.Internal, e);
        }
        this.parser = parser;
    }

    public IConnection createConnection(InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, String ref) throws TransportException {
        try {
            return constructorIAi.newInstance(config, remoteAddress, remotePort, localAddress, localPort,  parser, ref);
        } catch (Exception e) {
            throw new TransportException("Cannot create an instance of " + connectionClass, TransportError.Internal, e);
        }
    }

    public IConnection createConnection(InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, String ref) throws TransportException {
        try {
            return constructorIAiCL.newInstance(config, remoteAddress, remotePort, localAddress, localPort, listener, parser, ref);
        } catch (Exception e) {
            throw new TransportException("Cannot create an instance of " + connectionClass, TransportError.Internal, e);
        }
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null;  
    }
}
