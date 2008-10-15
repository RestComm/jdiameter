package org.jdiameter.client.impl.transport.tcp;

import org.jdiameter.client.api.io.NotInitializedException;
import static org.jdiameter.client.impl.helpers.Parameters.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
class TLSTransportClient extends TCPTransportClient {
    
    private TLSClientConnection parentConnection;

    /**
     * Default constructor
     *
     * @param parenConnection connection created this transport
     */
    TLSTransportClient(TLSClientConnection parenConnection) {
        this.parentConnection = parenConnection;
    }

    public void initialize() throws IOException, NotInitializedException {
        if (destAddress == null)
            throw new NotInitializedException("Destination address is not set");

        SSLSocketFactory cltFct = parentConnection.getSSLFactory();
        SSLSocket sck = (SSLSocket) cltFct.createSocket(destAddress.getAddress(), destAddress.getPort()); 
        sck.setEnableSessionCreation(parentConnection.getSSLConfig().getBooleanValue(SDEnableSessionCreation.ordinal(), true));
        sck.setUseClientMode(!parentConnection.getSSLConfig().getBooleanValue(SDUseClientMode.ordinal(), true));
        if (parentConnection.getSSLConfig().getStringValue(CipherSuites.ordinal(), "") != null) {
            sck.setEnabledCipherSuites(parentConnection.getSSLConfig().getStringValue(CipherSuites.ordinal(), "").split(","));
        }

        socketChannel = sck.getChannel();
        socketChannel.connect(destAddress);
        socketChannel.configureBlocking(true);
        parentConnection.onConnected();
    }

    public TCPClientConnection getParent() {
        return parentConnection;
    }    

}