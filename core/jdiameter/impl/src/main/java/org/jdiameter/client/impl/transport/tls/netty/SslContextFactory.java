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
 */

package org.jdiameter.client.impl.transport.tls.netty;

import static org.jdiameter.client.impl.helpers.Parameters.KDFile;
import static org.jdiameter.client.impl.helpers.Parameters.KDManager;
import static org.jdiameter.client.impl.helpers.Parameters.KDPwd;
import static org.jdiameter.client.impl.helpers.Parameters.KDStore;
import static org.jdiameter.client.impl.helpers.Parameters.KeyData;
import static org.jdiameter.client.impl.helpers.Parameters.TDFile;
import static org.jdiameter.client.impl.helpers.Parameters.TDManager;
import static org.jdiameter.client.impl.helpers.Parameters.TDPwd;
import static org.jdiameter.client.impl.helpers.Parameters.TDStore;
import static org.jdiameter.client.impl.helpers.Parameters.TrustData;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import org.jdiameter.api.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 *
 * @author <a href="mailto:jqayyum@gmail.com"> Jehanzeb Qayyum </a>
 */
public abstract class SslContextFactory {
  private static final Logger logger = LoggerFactory.getLogger(SslContextFactory.class);

  public static SslContext getSslContextForClient(Configuration config) throws SSLException, Exception {
    SslContext sslContext = SslContextBuilder.forClient().keyManager(getKeyManagerFactory(config))
        .trustManager(getTrustManagerFactory(config)).build();
    return sslContext;
  }

  public static SslContext getSslContextForServer(Configuration config) throws SSLException, Exception {
    SslContext sslContext = SslContextBuilder.forServer(getKeyManagerFactory(config))
        .trustManager(getTrustManagerFactory(config)).build();
    return sslContext;
  }

  public static KeyManagerFactory getKeyManagerFactory(Configuration sslConfig) throws Exception {
    final Configuration kdConfig = sslConfig.getChildren(KeyData.ordinal())[0];
    final String keyManagerAlgo = kdConfig.getStringValue(KDManager.ordinal(), null);
    final String keyStoreType = kdConfig.getStringValue(KDStore.ordinal(), null);
    final String keyStorePassword = kdConfig.getStringValue(KDPwd.ordinal(), null);
    final String keyStoreFile = kdConfig.getStringValue(KDFile.ordinal(), null);

    logger.debug("keyManagerAlgo: {}", keyManagerAlgo);
    logger.debug("keyStoreType: {}", keyStoreType);
    logger.debug("keyStorePassword: {}", keyStorePassword);
    logger.debug("keyStoreFile: {}", keyStoreFile);

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgo);
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    char[] key = keyStorePassword.toCharArray();
    keyStore.load(new FileInputStream(keyStoreFile), key);
    keyManagerFactory.init(keyStore, key);

    return keyManagerFactory;
  }

  public static TrustManagerFactory getTrustManagerFactory(Configuration sslConfig) throws Exception {
    final Configuration tdConfig = sslConfig.getChildren(TrustData.ordinal())[0];
    final String trustManagerAlgo = tdConfig.getStringValue(TDManager.ordinal(), null);
    final String trustStoreType = tdConfig.getStringValue(TDStore.ordinal(), null);
    final String trustStorePassword = tdConfig.getStringValue(TDPwd.ordinal(), null);
    final String trustStoreFile = tdConfig.getStringValue(TDFile.ordinal(), null);

    logger.debug("trustManagerAlgo: {}", trustManagerAlgo);
    logger.debug("trustStoreType: {}", trustStoreType);
    logger.debug("trustStorePassword: {}", trustStorePassword);
    logger.debug("trustStoreFile: {}", trustStoreFile);

    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgo);
    KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
    char[] trustKey = trustStorePassword.toCharArray();
    trustKeyStore.load(new FileInputStream(trustStoreFile), trustKey);
    trustManagerFactory.init(trustKeyStore);

    return trustManagerFactory;
  }

}
