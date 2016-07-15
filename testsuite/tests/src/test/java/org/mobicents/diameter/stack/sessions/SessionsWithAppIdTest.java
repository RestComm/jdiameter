/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.diameter.stack.sessions;

import java.io.InputStream;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.gq.GqClientSession;
import org.jdiameter.api.gq.GqServerSession;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.ServerGxSession;
import org.jdiameter.api.rf.ClientRfSession;
import org.jdiameter.api.rf.ServerRfSession;
import org.jdiameter.api.ro.ClientRoSession;
import org.jdiameter.api.ro.ServerRoSession;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.acc.AccSessionFactoryImpl;
import org.jdiameter.common.impl.app.auth.AuthSessionFactoryImpl;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.jdiameter.common.impl.app.gq.GqSessionFactoryImpl;
import org.jdiameter.common.impl.app.gx.GxSessionFactoryImpl;
import org.jdiameter.common.impl.app.rf.RfSessionFactoryImpl;
import org.jdiameter.common.impl.app.ro.RoSessionFactoryImpl;
import org.jdiameter.common.impl.app.s13.S13SessionFactoryImpl;
import org.jdiameter.common.impl.app.sh.ShSessionFactoryImpl;
import org.jdiameter.server.impl.StackImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for App Sessions created without Application-Id<br />
 * More info @ http://code.google.com/p/mobicents/issues/detail?id=2575
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SessionsWithAppIdTest {

  private static StackImpl stack = new StackImpl();

  private static SessionFactory sessionFactory = null;

  private static final ApplicationId BASE_ACCT_APPID = ApplicationId.createByAccAppId(3);
  private static final ApplicationId BASE_AUTH_APPID = ApplicationId.createByAccAppId(123);
  private static final ApplicationId CCA_APPID = ApplicationId.createByAuthAppId(4);
  private static final ApplicationId RO_APPID = ApplicationId.createByAuthAppId(10415, 4);
  private static final ApplicationId RF_APPID = ApplicationId.createByAccAppId(10415, 3);
  private static final ApplicationId SH_APPID = ApplicationId.createByAuthAppId(10415, 16777217);
  private static final ApplicationId CXDX_APPID = ApplicationId.createByAuthAppId(10415, 16777216);
  private static final ApplicationId GQ_APPID = ApplicationId.createByAuthAppId(10415, 16777222);
  private static final ApplicationId GX_APPID = ApplicationId.createByAuthAppId(10415, 16777224);
  private static final ApplicationId S13_APPID = ApplicationId.createByAuthAppId(10415, 16777252);


  static {
    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = SessionsWithAppIdTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);
      stack.start();

      sessionFactory = stack.getSessionFactory();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Integer lowSessionId = 523;

  @Test
  public void testAccountingClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientAccSession.class, new AccSessionFactoryImpl(sessionFactory));
    ClientAccSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, BASE_ACCT_APPID, ClientAccSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_ACCT_APPID, sessionAppId);
  }

  @Test
  public void testAccountingServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerAccSession.class, new AccSessionFactoryImpl(sessionFactory));
    ServerAccSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, BASE_ACCT_APPID, ServerAccSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_ACCT_APPID, sessionAppId);
  }

  @Test
  public void tesAuthClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientAuthSession.class, new AuthSessionFactoryImpl(sessionFactory));
    ClientAuthSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, BASE_AUTH_APPID, ClientAuthSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_AUTH_APPID, sessionAppId);
  }

  @Test
  public void testAuthServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerAuthSession.class, new AuthSessionFactoryImpl(sessionFactory));
    ServerAuthSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, BASE_AUTH_APPID, ServerAuthSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_AUTH_APPID, sessionAppId);
  }

  @Test
  public void testCCAClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientCCASession.class, new CCASessionFactoryImpl(sessionFactory));
    ClientCCASession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, CCA_APPID, ClientCCASession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CCA_APPID, sessionAppId);
  }

  @Test
  public void testCCAServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerCCASession.class, new CCASessionFactoryImpl(sessionFactory));
    ServerCCASession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, CCA_APPID, ServerCCASession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CCA_APPID, sessionAppId);
  }

  @Test
  public void testRoClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientRoSession.class, new RoSessionFactoryImpl(sessionFactory));
    ClientRoSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, RO_APPID, ClientRoSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RO_APPID, sessionAppId);
  }

  @Test
  public void testRoServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerRoSession.class, new RoSessionFactoryImpl(sessionFactory));
    ServerRoSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, RO_APPID, ServerRoSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RO_APPID, sessionAppId);
  }

  @Test
  public void testRfClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientRfSession.class, new RfSessionFactoryImpl(sessionFactory));
    ClientRfSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, RF_APPID, ClientRfSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RF_APPID, sessionAppId);
  }

  @Test
  public void testRfServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerRfSession.class, new RfSessionFactoryImpl(sessionFactory));
    ServerRfSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, RF_APPID, ServerRfSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RF_APPID, sessionAppId);
  }

  @Test
  public void testShClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientShSession.class, new ShSessionFactoryImpl(sessionFactory));
    ClientShSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, SH_APPID, ClientShSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SH_APPID, sessionAppId);
  }

  @Test
  public void testShServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerShSession.class, new ShSessionFactoryImpl(sessionFactory));
    ServerShSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, SH_APPID, ServerShSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SH_APPID, sessionAppId);
  }

  @Test
  public void testCxDxClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientCxDxSession.class, new CxDxSessionFactoryImpl(sessionFactory));
    ClientCxDxSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, CXDX_APPID, ClientCxDxSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CXDX_APPID, sessionAppId);
  }

  @Test
  public void testCxDxServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerCxDxSession.class, new CxDxSessionFactoryImpl(sessionFactory));
    ServerCxDxSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, CXDX_APPID, ServerCxDxSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CXDX_APPID, sessionAppId);
  }

  @Test
  public void testGqClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(GqClientSession.class, new GqSessionFactoryImpl(sessionFactory));
    GqClientSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, GQ_APPID, GqClientSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GQ_APPID, sessionAppId);
  }

  @Test
  public void testGqServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(GqServerSession.class, new GqSessionFactoryImpl(sessionFactory));
    GqServerSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, GQ_APPID, GqServerSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GQ_APPID, sessionAppId);
  }

  @Test
  public void testGxClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientGxSession.class, new GxSessionFactoryImpl(sessionFactory));
    ClientGxSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, GX_APPID, ClientGxSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GX_APPID, sessionAppId);
  }

  @Test
  public void testGxServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerGxSession.class, new GxSessionFactoryImpl(sessionFactory));
    ServerGxSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, GX_APPID, ServerGxSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GX_APPID, sessionAppId);
  }

  @Test
  public void testS13ClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientS13Session.class, new S13SessionFactoryImpl(sessionFactory));
    ClientS13Session session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, S13_APPID, ClientS13Session.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", S13_APPID, sessionAppId);
  }

  @Test
  public void testS13ServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerS13Session.class, new S13SessionFactoryImpl(sessionFactory));
    ServerS13Session session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, S13_APPID, ServerS13Session.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", S13_APPID, sessionAppId);
  }
}
