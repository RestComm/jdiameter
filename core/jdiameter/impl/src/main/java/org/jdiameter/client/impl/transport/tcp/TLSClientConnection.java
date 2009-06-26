package org.jdiameter.client.impl.transport.tcp;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.parser.IMessageParser;
import static org.jdiameter.client.impl.helpers.Parameters.*;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
public class TLSClientConnection extends TCPClientConnection {

    private TLSTransportClient client;
    private SSLSocketFactory factory;
    private Configuration sslConfig;

    public TLSClientConnection(Configuration config, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IMessageParser parser, String ref) {
        super(parser);
        this.client = new TLSTransportClient(this);
        client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
        client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
        this.parser = parser;
        try {
            if (ref == null) throw new Exception("Can not create connection with out TLS parameters");
            fiilSecurityData(config, ref);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public TLSClientConnection(Configuration config, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, IConnectionListener listener, IMessageParser parser, String ref) {
        super(parser);
        this.client = new TLSTransportClient(this);
        client.setDestAddress(new InetSocketAddress(remoteAddress, remotePort));
        client.setOrigAddress(new InetSocketAddress(localAddress, localPort));
        this.listeners.add(listener);
        this.parser = parser;
        try {
            if (ref == null) throw new Exception("Can not create connection with out TLS parameters");
            fiilSecurityData(config, ref);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public TLSClientConnection(Configuration config, SSLSocket socket, IMessageParser parser, String ref) throws Exception {
        super(parser);
        this.client = new TLSTransportClient(this);
        this.client.initialize(socket);
        this.client.start();
        try {
            if (ref == null) throw new Exception("Can not create connection with out TLS parameters");
            fiilSecurityData(config, ref);
        } catch (Exception e) {
           throw new IllegalArgumentException(e);
        }
    }

    private void fiilSecurityData(Configuration config, String ref) throws Exception {
        Configuration sec[] = config.getChildren( Security.ordinal() )[0].getChildren(SecurityData.ordinal());
        for (Configuration i:sec) {
            if ( i.getStringValue(SDName.ordinal(),"").equals(ref) ) {
                sslConfig = i;
                break;
            }
        }
        if (sslConfig == null)
            throw new Exception("Incorrect reference to secutity data");
        this.factory = getSSLContext(sslConfig);
    }

    protected TCPTransportClient getClient() {
        return client;
    }

    public Configuration getSSLConfig() {
        return sslConfig;
    }

    public SSLSocketFactory getSSLFactory() {
        return factory;
    }

    private SSLSocketFactory getSSLContext(Configuration sslConfig) throws Exception {
        SSLContext ctx = SSLContext.getInstance(sslConfig.getStringValue(SDProtocol.ordinal(), "TLS"));
        //
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(sslConfig.getStringValue(KDManager.ordinal(), ""));
        KeyStore keyStore = KeyStore.getInstance(sslConfig.getStringValue(KDStore.ordinal(), ""));
        char[] key = sslConfig.getStringValue(KDPwd.ordinal(), "").toCharArray();
        keyStore.load(new FileInputStream(sslConfig.getStringValue(KDFile.ordinal(), "")), key);
        keyManagerFactory.init(keyStore, key);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        //
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(sslConfig.getStringValue(TDManager.ordinal(), ""));
        KeyStore trustKeyStore = KeyStore.getInstance(sslConfig.getStringValue(TDStore.ordinal(), ""));
        char[] trustKey = sslConfig.getStringValue(TDPwd.ordinal(), "").toCharArray();
        trustKeyStore.load(new FileInputStream(sslConfig.getStringValue(TDFile.ordinal(), "")), trustKey);
        trustManagerFactory.init(trustKeyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        //
        ctx.init(keyManagers, trustManagers, null);
        return ctx.getSocketFactory();
    }    
}