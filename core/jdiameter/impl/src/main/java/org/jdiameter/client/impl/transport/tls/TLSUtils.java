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

import static org.jdiameter.client.impl.helpers.Parameters.KDFile;
import static org.jdiameter.client.impl.helpers.Parameters.KDManager;
import static org.jdiameter.client.impl.helpers.Parameters.KDPwd;
import static org.jdiameter.client.impl.helpers.Parameters.KDStore;
import static org.jdiameter.client.impl.helpers.Parameters.KeyData;
import static org.jdiameter.client.impl.helpers.Parameters.SDName;
import static org.jdiameter.client.impl.helpers.Parameters.SDProtocol;
import static org.jdiameter.client.impl.helpers.Parameters.Security;
import static org.jdiameter.client.impl.helpers.Parameters.TDFile;
import static org.jdiameter.client.impl.helpers.Parameters.TDManager;
import static org.jdiameter.client.impl.helpers.Parameters.TDPwd;
import static org.jdiameter.client.impl.helpers.Parameters.TDStore;
import static org.jdiameter.client.impl.helpers.Parameters.TrustData;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.jdiameter.api.Configuration;

/**
 * Simple utils class just to have one place for common stuff.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TLSUtils {

  public static SSLContext getSecureContext(Configuration sslConfig) throws Exception {
    // TODO: use classloader to fetch files.
    final String contextTransportAlgo = sslConfig.getStringValue(SDProtocol.ordinal(), null);
    final Configuration kdConfig = sslConfig.getChildren(KeyData.ordinal())[0];
    final Configuration tdConfig = sslConfig.getChildren(TrustData.ordinal())[0];
    final String keyManagerAlgo = kdConfig.getStringValue(KDManager.ordinal(), null);
    final String keyStoreType = kdConfig.getStringValue(KDStore.ordinal(), null);
    final String keyStorePassword = kdConfig.getStringValue(KDPwd.ordinal(), null);
    final String keyStoreFile = kdConfig.getStringValue(KDFile.ordinal(), null);
    final String trustManagerAlgo = tdConfig.getStringValue(TDManager.ordinal(), null);
    final String trustStoreType = tdConfig.getStringValue(TDStore.ordinal(), null);
    final String trustStorePassword = tdConfig.getStringValue(TDPwd.ordinal(), null);
    final String trustStoreFile = tdConfig.getStringValue(TDFile.ordinal(), null);
    return TLSUtils.getSecureContext(contextTransportAlgo, keyManagerAlgo, keyStoreType, keyStorePassword, keyStoreFile, trustManagerAlgo, trustStoreType,
        trustStorePassword, trustStoreFile);
  }

  public static SSLContext getSecureContext(String contextTransportAlgo, String keyManagerAlgo, String keyStoreType, String keyStorePassword,
      String keyStoreFile, String trustManagerAlgo, String trustStoreType, String trustStorePassword, String trustStoreFile) throws Exception {
    System.err.println(KeyManagerFactory.getDefaultAlgorithm());
    System.err.println(TrustManagerFactory.getDefaultAlgorithm());
    SSLContext ctx = SSLContext.getInstance(contextTransportAlgo);

    // http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgo);
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    char[] key = keyStorePassword.toCharArray();
    keyStore.load(new FileInputStream(keyStoreFile), key);
    keyManagerFactory.init(keyStore, key);
    KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
    //
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgo);
    KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
    char[] trustKey = trustStorePassword.toCharArray();
    trustKeyStore.load(new FileInputStream(trustStoreFile), trustKey);
    trustManagerFactory.init(trustKeyStore);
    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
    //
    ctx.init(keyManagers, trustManagers, null);
    return ctx;
  }

  public static Configuration getSSLConfiguration(Configuration cnf, String ref) {
    Configuration[] sec = cnf.getChildren(Security.ordinal());// [0].getChildren(SecurityData.ordinal());
    for (Configuration i : sec) {
      if (i.getStringValue(SDName.ordinal(), "").equals(ref)) {
        return i;
      }
    }
    return null;
  }
}
