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
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic;
import static org.jdiameter.server.impl.helpers.Parameters.RealmHosts;
import static org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction;
import static org.jdiameter.server.impl.helpers.Parameters.RealmName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mobicents.slee.resources.diameter.tests.factories.BaseFactoriesTest.*;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.ro.RoAvpFactory;
import net.java.slee.resource.diameter.ro.RoClientSessionActivity;
import net.java.slee.resource.diameter.ro.RoMessageFactory;
import net.java.slee.resource.diameter.ro.RoServerSessionActivity;
import net.java.slee.resource.diameter.ro.events.RoCreditControlAnswer;
import net.java.slee.resource.diameter.ro.events.RoCreditControlRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Stack;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.ro.ClientRoSession;
import org.jdiameter.api.ro.ClientRoSessionListener;
import org.jdiameter.api.ro.ServerRoSession;
import org.jdiameter.api.ro.ServerRoSessionListener;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.ro.ClientRoSessionDataLocalImpl;
import org.jdiameter.client.impl.app.ro.ClientRoSessionImpl;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.api.app.ro.IRoMessageFactory;
import org.jdiameter.server.impl.app.ro.ServerRoSessionDataLocalImpl;
import org.jdiameter.server.impl.app.ro.ServerRoSessionImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.ro.RoAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.ro.RoClientSessionActivityImpl;
import org.mobicents.slee.resource.diameter.ro.RoMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.ro.RoServerSessionActivityImpl;

/**
 * Test class for JAIN SLEE Diameter Ro (Online Charging) RA Message and AVP Factories
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoFactoriesTest implements IRoMessageFactory, ServerRoSessionListener, ClientRoSessionListener {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static RoMessageFactory roMessageFactory;
  private static RoAvpFactory roAvpFactory;

  private static Stack stack;

  private static ServerRoSession serverSession;
  private static ClientRoSession clientSession;

  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    DiameterMessageFactoryImpl baseFactory = new DiameterMessageFactoryImpl(stack);
    DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

    roAvpFactory = new RoAvpFactoryImpl(baseAvpFactory);
    try {
      roMessageFactory = new RoMessageFactoryImpl(baseFactory, stack.getSessionFactory().getNewSession().getSessionId(), stack);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      AvpDictionary.INSTANCE.parseDictionary(RoFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  private RoServerSessionActivity roServerSession = null;
  private RoClientSessionActivity roClientSession = null;

  public RoFactoriesTest() {
    try {
      serverSession = new ServerRoSessionImpl(new ServerRoSessionDataLocalImpl(), this, (ISessionFactory) stack.getSessionFactory(), this, null, null);
      clientSession = new ClientRoSessionImpl(new ClientRoSessionDataLocalImpl(), this, (ISessionFactory) stack.getSessionFactory(), this, null, null);
      roServerSession = new RoServerSessionActivityImpl(roMessageFactory, roAvpFactory, serverSession, new DiameterIdentity("127.0.0.2"), new DiameterIdentity("mobicents.org"), stack);
      roClientSession = new RoClientSessionActivityImpl(roMessageFactory, roAvpFactory, clientSession, new DiameterIdentity("127.0.0.2"), new DiameterIdentity("mobicents.org"), stack);
      ((RoServerSessionActivityImpl) roServerSession).fetchCurrentState(roMessageFactory.createRoCreditControlRequest());
    }
    catch (IllegalDiameterStateException e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  @Test
  public void isRequestCCR() throws Exception {
    RoCreditControlRequest ccr = roMessageFactory.createRoCreditControlRequest();
    assertTrue("Request Flag in Credit-Control-Request is not set.", ccr.getHeader().isRequest());
  }

  @Test
  public void isProxiableCCR() throws Exception {
    RoCreditControlRequest acr = roMessageFactory.createRoCreditControlRequest();
    assertTrue("The 'P' bit is not set by default in Ro Credit-Control-Request, it should.", acr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersCCR() throws Exception {
    RoCreditControlRequest ccr = roMessageFactory.createRoCreditControlRequest();

    int nFailures = AvpAssistant.testMethods(ccr, RoCreditControlRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasRoApplicationIdCCR() throws Exception {
    RoCreditControlRequest ccr = roMessageFactory.createRoCreditControlRequest();
    assertTrue("Auth-Application-Id AVP in Ro CCR must be 4, it is " + ccr.getAuthApplicationId(), ccr.getAuthApplicationId() == 4);
  }

  @Test
  public void isAnswerCCA() throws Exception {
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();
    assertFalse("Request Flag in Credit-Control-Answer is set.", cca.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedCCA() throws Exception {
    RoCreditControlRequest ccr = roMessageFactory.createRoCreditControlRequest();
    ((RoServerSessionActivityImpl) roServerSession).fetchCurrentState(ccr);
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();
    assertEquals("The 'P' bit is not copied from request in Ro Credit-Control-Answer, it should. [RFC3588/6.2]", ccr.getHeader().isProxiable(), cca.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) ccr).getGenericData().setProxiable(!ccr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Ro Credit-Control-Request, it should.", ccr.getHeader().isProxiable() != cca.getHeader().isProxiable());
    ((RoServerSessionActivityImpl) roServerSession).fetchCurrentState(ccr);

    cca = roServerSession.createRoCreditControlAnswer();
    assertEquals("The 'P' bit is not copied from request in Ro Credit-Control-Answer, it should. [RFC3588/6.2]", ccr.getHeader().isProxiable(), cca.getHeader().isProxiable());
  }

  @Test
  public void hasTFlagSetCCA() throws Exception {
    RoCreditControlRequest ccr = roMessageFactory.createRoCreditControlRequest();
    ((DiameterMessageImpl) ccr).getGenericData().setReTransmitted(true);

    assertTrue("The 'T' flag should be set in Credit-Control-Request", ccr.getHeader().isPotentiallyRetransmitted());

    ((RoServerSessionActivityImpl) roServerSession).fetchCurrentState(ccr);
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();
    assertFalse("The 'T' flag should not be set in Credit-Control-Answer", cca.getHeader().isPotentiallyRetransmitted());
  }

  @Test
  public void testGettersAndSettersCCA() throws Exception {
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();

    int nFailures = AvpAssistant.testMethods(cca, RoCreditControlAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasRoApplicationIdCCA() throws Exception {
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();
    assertTrue("Auth-Application-Id AVP in Ro CCA must be 4, it is " + cca.getAuthApplicationId(), cca.getAuthApplicationId() == 4);
  }

  @Test
  public void hasDestinationHostCCA() throws Exception {
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cca.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmCCA() throws Exception {
    RoCreditControlAnswer cca = roServerSession.createRoCreditControlAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cca.getDestinationRealm());
  }


  @Test
  public void testMessageFactoryApplicationIdChangeCCR() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((RoMessageFactoryImpl)roMessageFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Ro is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    RoCreditControlRequest originalCCR = roMessageFactory.createRoCreditControlRequest();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalCCR);

    // now we switch..
    originalCCR = null;
    isVendor = !isVendor;
    ((RoMessageFactoryImpl)roMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    RoCreditControlRequest changedCCR = roMessageFactory.createRoCreditControlRequest();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedCCR);

    // revert back to default
    ((RoMessageFactoryImpl)roMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testClientSessionApplicationIdChangeCCR() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((RoMessageFactoryImpl)roMessageFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Ro is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    RoCreditControlRequest originalCCR = roClientSession.createRoCreditControlRequest(CcRequestType.EVENT_REQUEST);
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalCCR);

    // now we switch..
    originalCCR = null;
    isVendor = !isVendor;
    ((RoMessageFactoryImpl)roMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    RoCreditControlRequest changedCCR = roClientSession.createRoCreditControlRequest(CcRequestType.EVENT_REQUEST);
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedCCR);

    // revert back to default
    ((RoMessageFactoryImpl)roMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testServerSessionApplicationIdChangeCCA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((RoMessageFactoryImpl)roMessageFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Ro is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    RoCreditControlRequest ccr = roMessageFactory.createRoCreditControlRequest();
    ((RoServerSessionActivityImpl)roServerSession).fetchCurrentState(ccr);
    RoCreditControlAnswer originalCCA = roServerSession.createRoCreditControlAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalCCA);

    // now we switch..
    originalCCA = null;
    isVendor = !isVendor;
    ((RoMessageFactoryImpl)roMessageFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    RoCreditControlAnswer changedCCA = roServerSession.createRoCreditControlAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedCCA);

    // revert back to default
    ((RoMessageFactoryImpl)roMessageFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
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
      // add(UseUriAsFqdn, true);
      // Set Common Applications
      add(ApplicationId,
          // AppId 1
          getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L));
      // Set peer table
      add(PeerTable,
          // Peer 1
          getInstance().add(PeerRating, 1).add(PeerName, serverURI));
      // Set realm table
      add(RealmTable,
          // Realm 1
          getInstance().add(RealmEntry, getInstance().
              add(RealmName, realmName).
              add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L)).
              add(RealmHosts, clientHost + ", " + serverHost).
              add(RealmLocalAction, "LOCAL").
              add(RealmEntryIsDynamic, false).
              add(RealmEntryExpTime, 1000L)));
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
    return new long[] { RoMessageFactory._RO_AUTH_APP_ID };
  }

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doReAuthAnswer(ServerRoSession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doCreditControlRequest(ServerRoSession session, org.jdiameter.api.ro.events.RoCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public org.jdiameter.api.ro.events.RoCreditControlRequest createCreditControlRequest(Request request) {
    // NO-OP
    return null;
  }

  public org.jdiameter.api.ro.events.RoCreditControlAnswer createCreditControlAnswer(Answer answer) {
    // NO-OP
    return null;
  }

  public void doCreditControlAnswer(ClientRoSession session, org.jdiameter.api.ro.events.RoCreditControlRequest request,
      org.jdiameter.api.ro.events.RoCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // NO-OP
  }

  public void doReAuthRequest(ClientRoSession session, ReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException,
      OverloadException {
    // NO-OP
  }

  public int getDefaultDDFHValue() {
    // NO-OP
    return 0;
  }

  public int getDefaultCCFHValue() {
    // NO-OP
    return 0;
  }

}
