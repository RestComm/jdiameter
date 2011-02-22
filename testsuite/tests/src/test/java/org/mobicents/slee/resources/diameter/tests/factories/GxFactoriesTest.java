/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.slee.resources.diameter.tests.factories;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.Assembler;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.OwnVendorID;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.gx.GxAvpFactory;
import net.java.slee.resource.diameter.gx.GxMessageFactory;
import net.java.slee.resource.diameter.gx.GxServerSessionActivity;
import net.java.slee.resource.diameter.gx.events.GxCreditControlAnswer;
import net.java.slee.resource.diameter.gx.events.GxCreditControlRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.api.gx.ServerGxSession;
import org.jdiameter.api.gx.ServerGxSessionListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.api.app.gx.IGxMessageFactory;
import org.jdiameter.server.impl.app.gx.ServerGxSessionDataLocalImpl;
import org.jdiameter.server.impl.app.gx.ServerGxSessionImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.gx.GxAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.gx.GxMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.gx.GxServerSessionActivityImpl;

/**
 * GxFactoriesTest.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GxFactoriesTest implements IGxMessageFactory, ServerGxSessionListener {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static GxMessageFactory gxMessageFactory;
  private static GxAvpFactory gxAvpFactory;

  private static Stack stack;

  private static ServerGxSession session; 


  static
  {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    DiameterMessageFactoryImpl baseFactory = new DiameterMessageFactoryImpl(stack);
    DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

    gxAvpFactory = new GxAvpFactoryImpl(baseAvpFactory);
    try {
      gxMessageFactory = new GxMessageFactoryImpl(baseFactory, stack.getSessionFactory().getNewSession().getSessionId(), stack);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      AvpDictionary.INSTANCE.parseDictionary(GxFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  private GxServerSessionActivity gxServerSession = null;

  public GxFactoriesTest() {
    try {
      session = new ServerGxSessionImpl(new ServerGxSessionDataLocalImpl(), this, (ISessionFactory) stack.getSessionFactory(), this, null, null);
      gxServerSession = new GxServerSessionActivityImpl(gxMessageFactory.getBaseMessageFactory(), gxAvpFactory.getBaseFactory(), session, new DiameterIdentity("127.0.0.2"), new DiameterIdentity("mobicents.org"), stack);
      ((GxServerSessionActivityImpl)gxServerSession).fetchCurrentState(gxMessageFactory.createGxCreditControlRequest());
    }
    catch (IllegalDiameterStateException e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  @Test
  public void isRequestCCR() throws Exception {
    GxCreditControlRequest ccr = gxMessageFactory.createGxCreditControlRequest();
    assertTrue("Request Flag in Credit-Control-Request is not set.", ccr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersCCR() throws Exception {
    GxCreditControlRequest ccr = gxMessageFactory.createGxCreditControlRequest();

    int nFailures = AvpAssistant.testMethods(ccr, GxCreditControlRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGxApplicationIdCCR() throws Exception {
    GxCreditControlRequest ccr = gxMessageFactory.createGxCreditControlRequest();
    assertTrue("Auth-Application-Id AVP in Gx CCR must be 16777224, it is " + ccr.getAuthApplicationId(), ccr.getAuthApplicationId() == 16777224);
  }

  @Test
  public void isAnswerCCA() throws Exception {
    GxCreditControlAnswer cca = gxServerSession.createGxCreditControlAnswer();
    assertFalse("Request Flag in Credit-Control-Answer is set.", cca.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersCCA() throws Exception {
    GxCreditControlAnswer cca = gxServerSession.createGxCreditControlAnswer();

    int nFailures = AvpAssistant.testMethods(cca, GxCreditControlAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGxApplicationIdCCA() throws Exception {
    GxCreditControlAnswer cca = gxServerSession.createGxCreditControlAnswer();
    assertTrue("Auth-Application-Id AVP in Gx CCA must be 16777224, it is " + cca.getAuthApplicationId(), cca.getAuthApplicationId() == 16777224);
  }

  @Test
  public void hasDestinationHostCCA() throws Exception {
    GxCreditControlAnswer cca = gxServerSession.createGxCreditControlAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cca.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmCCA() throws Exception {
    GxCreditControlAnswer cca = gxServerSession.createGxCreditControlAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cca.getDestinationRealm());    
  }


  /**
   * Class representing the Diameter Configuration  
   */
  public static class MyConfiguration extends EmptyConfiguration {
    public MyConfiguration() {
      super();

      add(Assembler, Assembler.defValue());
      add(OwnDiameterURI, clientURI);
      add(OwnRealm, realmName);
      add(OwnVendorID, 193L);
      // Set Ericsson SDK feature
      //add(UseUriAsFqdn, true);
      // Set Common Applications
      add(ApplicationId,
          // AppId 1
          getInstance().
          add(VendorId,   193L).
          add(AuthApplId, 0L).
          add(AcctApplId, 19302L)
      );
      // Set peer table
      add(PeerTable,
          // Peer 1
          getInstance().
          add(PeerRating, 1).
          add(PeerName, serverURI));
      // Set realm table
      add(RealmTable,
          // Realm 1
          getInstance().
          add(RealmEntry, realmName + ":" + clientHost + "," + serverHost)
      );
    }
  }

  public ReAuthAnswer createReAuthAnswer(Answer answer) {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthRequest createReAuthRequest(Request req) {
    // TODO Auto-generated method stub
    return null;
  }

  public long[] getApplicationIds() {
    return new long[]{GxMessageFactory._GX_AUTH_APP_ID};
  }

  public void doAbortSessionAnswer(ServerCCASession session, AbortSessionRequest request, AbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doAbortSessionRequest(ServerCCASession session, AbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doAccountingAnswer(ServerCCASession session, AccountRequest request, AccountAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doAccountingRequest(ServerCCASession session, AccountRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doSessionTerminationAnswer(ServerCCASession session, SessionTermRequest request, SessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doSessionTerminationRequest(ServerCCASession session, SessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doCreditControlRequest(ServerGxSession session, GxCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doReAuthAnswer(ServerGxSession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doCreditControlRequest(ServerGxSession session, org.jdiameter.api.gx.events.GxCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public org.jdiameter.api.gx.events.GxCreditControlRequest createCreditControlRequest(Request request) {
    return null;
  }

  public org.jdiameter.api.gx.events.GxCreditControlAnswer createCreditControlAnswer(Answer answer) {
    return null;
  }

}
