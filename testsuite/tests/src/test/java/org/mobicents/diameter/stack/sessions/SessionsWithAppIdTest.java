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
import org.jdiameter.api.InternalException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.app.AppSession;
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
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;
import org.jdiameter.common.api.app.acc.ServerAccSessionState;
import org.jdiameter.common.api.app.auth.ClientAuthSessionState;
import org.jdiameter.common.api.app.auth.ServerAuthSessionState;
import org.jdiameter.common.api.app.cca.ClientCCASessionState;
import org.jdiameter.common.api.app.cca.ServerCCASessionState;
import org.jdiameter.common.api.app.cxdx.CxDxSessionState;
import org.jdiameter.common.api.app.gx.ClientGxSessionState;
import org.jdiameter.common.api.app.gx.ServerGxSessionState;
import org.jdiameter.common.api.app.rf.ClientRfSessionState;
import org.jdiameter.common.api.app.rf.ServerRfSessionState;
import org.jdiameter.common.api.app.ro.ClientRoSessionState;
import org.jdiameter.common.api.app.ro.ServerRoSessionState;
import org.jdiameter.common.api.app.s13.S13SessionState;
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
import org.jdiameter.common.impl.app.slh.SLhSessionFactoryImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
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
  private static final ApplicationId SLh_APPID = ApplicationId.createByAuthAppId(10415, 16777291);
  private static final ApplicationId SLg_APPID = ApplicationId.createByAuthAppId(10415, 16777255);


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
  
  private <T extends AppSession> T getAppSession(Class<? extends AppSession> sessionClass, IAppSessionFactory appSessionFactory, ApplicationId appId) throws InternalException {
    ((ISessionFactory) sessionFactory).registerAppFacory(sessionClass, appSessionFactory);
    return sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, appId, sessionClass);
  }

  @Test
  public void testAccountingClientSessionHasAppId() throws Exception {
    ClientAccSession session = getAppSession(ClientAccSession.class, new AccSessionFactoryImpl(sessionFactory), BASE_ACCT_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_ACCT_APPID, sessionAppId);
  }
  
  @Test
  public void testAccountingClientSessionStartsIdleState() throws Exception {
    ClientAccSession session = getAppSession(ClientAccSession.class, new AccSessionFactoryImpl(sessionFactory), BASE_ACCT_APPID);

    ClientAccSessionState state = session.getState(ClientAccSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testAccountingServerSessionHasAppId() throws Exception {
    ServerAccSession session = getAppSession(ServerAccSession.class, new AccSessionFactoryImpl(sessionFactory), BASE_ACCT_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_ACCT_APPID, sessionAppId);
  }

  @Test
  public void testAccountingServerSessionStartsIdleState() throws Exception {
    ServerAccSession session = getAppSession(ServerAccSession.class, new AccSessionFactoryImpl(sessionFactory), BASE_ACCT_APPID);

    ServerAccSessionState state = session.getState(ServerAccSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testAuthClientSessionHasAppId() throws Exception {
    ClientAuthSession session = getAppSession(ClientAuthSession.class, new AuthSessionFactoryImpl(sessionFactory), BASE_AUTH_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_AUTH_APPID, sessionAppId);
  }

  @Test
  public void testAuthClientSessionStartsIdleState() throws Exception {
    ClientAuthSession session = getAppSession(ClientAuthSession.class, new AuthSessionFactoryImpl(sessionFactory), BASE_AUTH_APPID);

    ClientAuthSessionState state = session.getState(ClientAuthSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testAuthServerSessionHasAppId() throws Exception {
    ServerAuthSession session = getAppSession(ServerAuthSession.class, new AuthSessionFactoryImpl(sessionFactory), BASE_AUTH_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", BASE_AUTH_APPID, sessionAppId);
  }

  @Test
  public void testAuthServerSessionStartsIdleState() throws Exception {
    ServerAuthSession session = getAppSession(ServerAuthSession.class, new AuthSessionFactoryImpl(sessionFactory), BASE_AUTH_APPID);

    ServerAuthSessionState state = session.getState(ServerAuthSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testCCAClientSessionHasAppId() throws Exception {
    ClientCCASession session = getAppSession(ClientCCASession.class, new CCASessionFactoryImpl(sessionFactory), CCA_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CCA_APPID, sessionAppId);
  }

  @Test
  public void testCCAClientSessionStartsIdleState() throws Exception {
    ClientCCASession session = getAppSession(ClientCCASession.class, new CCASessionFactoryImpl(sessionFactory), CCA_APPID);

    ClientCCASessionState state = session.getState(ClientCCASessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testCCAServerSessionHasAppId() throws Exception {
    ServerCCASession session = getAppSession(ServerCCASession.class, new CCASessionFactoryImpl(sessionFactory), CCA_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CCA_APPID, sessionAppId);
  }

  @Test
  public void testCCAServerSessionStartsIdleState() throws Exception {
    ServerCCASession session = getAppSession(ServerCCASession.class, new CCASessionFactoryImpl(sessionFactory), CCA_APPID);

    ServerCCASessionState state = session.getState(ServerCCASessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testRoClientSessionHasAppId() throws Exception {
    ClientRoSession session = getAppSession(ClientRoSession.class, new RoSessionFactoryImpl(sessionFactory), RO_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RO_APPID, sessionAppId);
  }

  @Test
  public void testRoClientSessionStartsIdleState() throws Exception {
    ClientRoSession session = getAppSession(ClientRoSession.class, new RoSessionFactoryImpl(sessionFactory), RO_APPID);

    ClientRoSessionState state = session.getState(ClientRoSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testRoServerSessionHasAppId() throws Exception {
    ServerRoSession session = getAppSession(ServerRoSession.class, new RoSessionFactoryImpl(sessionFactory), RO_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RO_APPID, sessionAppId);
  }

  @Test
  public void testRoServerSessionStartsIdleState() throws Exception {
    ServerRoSession session = getAppSession(ServerRoSession.class, new RoSessionFactoryImpl(sessionFactory), RO_APPID);

    ServerRoSessionState state = session.getState(ServerRoSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testRfClientSessionHasAppId() throws Exception {
    ClientRfSession session = getAppSession(ClientRfSession.class, new RfSessionFactoryImpl(sessionFactory), RF_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RF_APPID, sessionAppId);
  }

  @Test
  public void testRfClientSessionStartsIdleState() throws Exception {
    ClientRfSession session = getAppSession(ClientRfSession.class, new RfSessionFactoryImpl(sessionFactory), RF_APPID);

    ClientRfSessionState state = session.getState(ClientRfSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testRfServerSessionHasAppId() throws Exception {
    ServerRfSession session = getAppSession(ServerRfSession.class, new RfSessionFactoryImpl(sessionFactory), RF_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", RF_APPID, sessionAppId);
  }

  @Test
  public void testRfServerSessionStartsIdleState() throws Exception {
    ServerRfSession session = getAppSession(ServerRfSession.class, new RfSessionFactoryImpl(sessionFactory), RF_APPID);

    ServerRfSessionState state = session.getState(ServerRfSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testShClientSessionHasAppId() throws Exception {
    ClientShSession session = getAppSession(ClientShSession.class, new ShSessionFactoryImpl(sessionFactory), SH_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SH_APPID, sessionAppId);
  }

  // Session State N/A for Sh

  @Test
  public void testShServerSessionHasAppId() throws Exception {
    ServerShSession session = getAppSession(ServerShSession.class, new ShSessionFactoryImpl(sessionFactory), SH_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SH_APPID, sessionAppId);
  }

  // Session State N/A for Sh

  @Test
  public void testCxDxClientSessionHasAppId() throws Exception {
    ClientCxDxSession session = getAppSession(ClientCxDxSession.class, new CxDxSessionFactoryImpl(sessionFactory), CXDX_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CXDX_APPID, sessionAppId);
  }

  @Test
  public void testCxDxClientSessionStartsIdleState() throws Exception {
    ClientCxDxSession session = getAppSession(ClientCxDxSession.class, new CxDxSessionFactoryImpl(sessionFactory), CXDX_APPID);

    CxDxSessionState state = session.getState(CxDxSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testCxDxServerSessionHasAppId() throws Exception {
    ServerCxDxSession session = getAppSession(ServerCxDxSession.class, new CxDxSessionFactoryImpl(sessionFactory), CXDX_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", CXDX_APPID, sessionAppId);
  }

  @Test
  public void testCxDxServerSessionStartsIdleState() throws Exception {
    ServerCxDxSession session = getAppSession(ServerCxDxSession.class, new CxDxSessionFactoryImpl(sessionFactory), CXDX_APPID);

    CxDxSessionState state = session.getState(CxDxSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testGqClientSessionHasAppId() throws Exception {
    GqClientSession session = getAppSession(GqClientSession.class, new GqSessionFactoryImpl(sessionFactory), GQ_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GQ_APPID, sessionAppId);
  }

  // Session State N/A for Gq

  @Test
  public void testGqServerSessionHasAppId() throws Exception {
    GqServerSession session = getAppSession(GqServerSession.class, new GqSessionFactoryImpl(sessionFactory), GQ_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GQ_APPID, sessionAppId);
  }

  // Session State N/A for Gq

  @Test
  public void testGxClientSessionHasAppId() throws Exception {
    ClientGxSession session = getAppSession(ClientGxSession.class, new GxSessionFactoryImpl(sessionFactory), GX_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GX_APPID, sessionAppId);
  }

  @Test
  public void testGxClientSessionStartsIdleState() throws Exception {
    ClientGxSession session = getAppSession(ClientGxSession.class, new GxSessionFactoryImpl(sessionFactory), GX_APPID);

    ClientGxSessionState state = session.getState(ClientGxSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testGxServerSessionHasAppId() throws Exception {
    ServerGxSession session = getAppSession(ServerGxSession.class, new GxSessionFactoryImpl(sessionFactory), GX_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", GX_APPID, sessionAppId);
  }

  @Test
  public void testGxServerSessionStartsIdleState() throws Exception {
    ServerGxSession session = getAppSession(ServerGxSession.class, new GxSessionFactoryImpl(sessionFactory), GX_APPID);

    ServerGxSessionState state = session.getState(ServerGxSessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testS13ClientSessionHasAppId() throws Exception {
    ClientS13Session session = getAppSession(ClientS13Session.class, new S13SessionFactoryImpl(sessionFactory), S13_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", S13_APPID, sessionAppId);
  }

  @Test
  public void testS13ClientSessionStartsIdleState() throws Exception {
    ClientS13Session session = getAppSession(ClientS13Session.class, new S13SessionFactoryImpl(sessionFactory), S13_APPID);

    S13SessionState state = session.getState(S13SessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testS13ServerSessionHasAppId() throws Exception {
    ServerS13Session session = getAppSession(ServerS13Session.class, new S13SessionFactoryImpl(sessionFactory), S13_APPID);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", S13_APPID, sessionAppId);
  }

  @Test
  public void testS13ServerSessionStartsIdleState() throws Exception {
    ServerS13Session session = getAppSession(ServerS13Session.class, new S13SessionFactoryImpl(sessionFactory), S13_APPID);

    S13SessionState state = session.getState(S13SessionState.class);
    Assert.assertEquals(state.IDLE, state);
  }

  @Test
  public void testSLhClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientSLhSession.class, new SLhSessionFactoryImpl(sessionFactory));
    ClientSLhSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, SLh_APPID, ClientSLhSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SLh_APPID, sessionAppId);
  }

  @Test
  public void testSLhServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerSLhSession.class, new SLhSessionFactoryImpl(sessionFactory));
    ServerSLhSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, SLh_APPID, ServerSLhSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SLh_APPID, sessionAppId);
  }

  @Test
  public void testSLgClientSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ClientSLgSession.class, new SLgSessionFactoryImpl(sessionFactory));
    ClientSLgSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, SLg_APPID, ClientSLgSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SLg_APPID, sessionAppId);
  }

  @Test
  public void testSLgServerSessionHasAppId() throws Exception {
    ((ISessionFactory) sessionFactory).registerAppFacory(ServerSLgSession.class, new SLgSessionFactoryImpl(sessionFactory));
    ServerSLgSession session = sessionFactory.getNewAppSession("accesspoint7.acme.com;1876543210;" + lowSessionId++, SLg_APPID, ServerSLgSession.class);

    ApplicationId sessionAppId = session.getSessionAppId();
    Assert.assertEquals("Session Application-Id should be the same as indicated.", SLg_APPID, sessionAppId);
  }

}
