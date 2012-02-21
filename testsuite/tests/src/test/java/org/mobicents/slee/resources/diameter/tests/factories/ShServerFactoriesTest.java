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
import static org.mobicents.slee.resources.diameter.tests.factories.BaseFactoriesTest.checkCorrectApplicationIdAVPs;
import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.events.UserDataRequest;
import net.java.slee.resource.diameter.sh.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.events.avp.UserIdentityAvp;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.app.sh.ShSessionFactoryImpl;
import org.jdiameter.server.impl.app.sh.ShServerSessionImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.handlers.DiameterRAInterface;
import org.mobicents.slee.resource.diameter.sh.DiameterShAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.ShClientMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.events.avp.UserIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerActivityImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerSubscriptionActivityImpl;

/**
 * Test class for JAIN SLEE Diameter Sh (Server) RA Message and AVP Factories
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ShServerFactoriesTest implements DiameterRAInterface {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static ShServerMessageFactoryImpl shServerFactory;
  private static ShClientMessageFactoryImpl shClientFactory;
  private static DiameterShAvpFactory shAvpFactory;

  //private static ShClientActivityImpl clientSession;
  private static ShServerActivityImpl serverSession;
  private static ShServerSubscriptionActivityImpl serverSubsSession;

  static {
    Stack stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
      AvpDictionary.INSTANCE.parseDictionary(ShServerFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    Session session = null;
    ShSessionFactoryImpl sf = null;
    try {
      session = stack.getSessionFactory().getNewSession();
      sf = new ShSessionFactoryImpl(stack.getSessionFactory());
    }
    catch (Exception e) {
      // let's go with null
      e.printStackTrace();
    }

    DiameterMessageFactoryImpl baseMessageFactory = new DiameterMessageFactoryImpl(stack);
    shAvpFactory = new DiameterShAvpFactoryImpl(new DiameterAvpFactoryImpl());
    shServerFactory = new ShServerMessageFactoryImpl(baseMessageFactory, session, stack, shAvpFactory);
    shClientFactory = new ShClientMessageFactoryImpl(session, stack);

    ApplicationId shAppId = org.jdiameter.api.ApplicationId.createByAuthAppId(ShClientMessageFactory._SH_VENDOR_ID, ShClientMessageFactory._SH_APP_ID);

    ShServerSessionImpl stackServerSession = (ShServerSessionImpl) sf.getNewSession("123", ServerShSession.class, shAppId, new Object[0]);
    serverSession = new ShServerActivityImpl(shServerFactory, shAvpFactory, stackServerSession, new DiameterIdentity("127.0.0.1"), new DiameterIdentity("mobicents.org"));
    serverSession.setSessionListener(new ShServerFactoriesTest());
    serverSubsSession = new ShServerSubscriptionActivityImpl(shServerFactory, shAvpFactory, stackServerSession, new DiameterIdentity("127.0.0.1"), new DiameterIdentity("mobicents.org"));
    serverSubsSession.setSessionListener(new ShServerFactoriesTest());

    //ShClientSessionImpl stackClientSession = (ShClientSessionImpl) sf.getNewSession("321", ClientShSession.class, shAppId, new Object[0]);
    //clientSession = new ShClientActivityImpl(shClientFactory, shAvpFactory, stackClientSession, null, null);
  }

  @Test
  public void isAnswerPUA() throws Exception {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest());
    assertFalse("Request Flag in Profile-Update-Answer is set.", pua.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedPUA() throws Exception {
    ProfileUpdateRequest pur = shClientFactory.createProfileUpdateRequest();
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(pur);
    assertEquals("The 'P' bit is not copied from request in Profile-Update-Answer, it should. [RFC3588/6.2]", pur.getHeader().isProxiable(), pua.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) pur).getGenericData().setProxiable(!pur.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Profile-Update-Request, it should.", pur.getHeader().isProxiable() != pua.getHeader().isProxiable());

    pua = shServerFactory.createProfileUpdateAnswer(pur);
    assertEquals("The 'P' bit is not copied from request in Profile-Update-Answer, it should. [RFC3588/6.2]", pur.getHeader().isProxiable(), pua.getHeader().isProxiable());
  }

  @Test
  public void hasTFlagSetPUA() throws Exception {
    ProfileUpdateRequest pur = shClientFactory.createProfileUpdateRequest();
    ((DiameterMessageImpl) pur).getGenericData().setReTransmitted(true);

    assertTrue("The 'T' flag should be set in Profile-Update-Request", pur.getHeader().isPotentiallyRetransmitted());

    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(pur);
    assertFalse("The 'T' flag should not be set in Profile-Update-Answer", pua.getHeader().isPotentiallyRetransmitted());
  }

  @Test
  public void testGettersAndSettersPUA() throws Exception {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest());

    int nFailures = AvpAssistant.testMethods(pua, ProfileUpdateAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostPUA() throws Exception {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmPUA() throws Exception {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationRealm());
  }

  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested) http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetPUA() throws Exception {
    long originalValue = 5001;

    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest(), originalValue, true);

    long obtainedValue = pua.getExperimentalResult().getExperimentalResultCode();

    assertTrue("Experimental-Result-Code in PUA should be " + originalValue + " and is " + obtainedValue + ".", originalValue == obtainedValue);
  }

  @Test
  public void isRequestPNR() throws Exception {
    PushNotificationRequest pnr = shServerFactory.createPushNotificationRequest();
    assertTrue("Request Flag in Push-Notification-Request is not set.", pnr.getHeader().isRequest());
  }

  @Test
  public void isProxiableRAR() throws Exception {
    PushNotificationRequest pnr = shServerFactory.createPushNotificationRequest();
    assertTrue("The 'P' bit is not set by default in Push-Notification-Request, it should.", pnr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersPNR() throws Exception {
    PushNotificationRequest pnr = shServerFactory.createPushNotificationRequest();

    int nFailures = AvpAssistant.testMethods(pnr, PushNotificationRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isPNRPublicIdentityAccessibleTwice() throws Exception {
    String originalValue = "sip:alexandre@diameter.mobicents.org";

    UserIdentityAvpImpl uiAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, 10415L, 1, 0, new byte[] {});
    uiAvp.setPublicIdentity(originalValue);

    PushNotificationRequest udr = shServerFactory.createPushNotificationRequest(uiAvp, new byte[1]);

    String obtainedValue1 = udr.getUserIdentity().getPublicIdentity();
    String obtainedValue2 = udr.getUserIdentity().getPublicIdentity();

    assertTrue("Obtained value for Public-Identity AVP differs from original.", obtainedValue1.equals(originalValue));
    assertTrue("Obtained #1 value for Public-Identity AVP differs from Obtained #2.", obtainedValue1.equals(obtainedValue2));
  }

  @Test
  public void isAnswerSNA() throws Exception {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest());
    assertFalse("Request Flag in Subscribe-Notifications-Answer is set.", sna.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedSNA() throws Exception {
    SubscribeNotificationsRequest snr = shClientFactory.createSubscribeNotificationsRequest();
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(snr);
    assertEquals("The 'P' bit is not copied from request in Subscribe-Notifications-Answer, it should. [RFC3588/6.2]", snr.getHeader().isProxiable(), sna.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) snr).getGenericData().setProxiable(!snr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Subscribe-Notifications-Request, it should.", snr.getHeader().isProxiable() != sna.getHeader().isProxiable());

    sna = shServerFactory.createSubscribeNotificationsAnswer(snr);
    assertEquals("The 'P' bit is not copied from request in Subscribe-Notifications-Answer, it should. [RFC3588/6.2]", snr.getHeader().isProxiable(), sna.getHeader().isProxiable());
  }

  @Test
  public void hasTFlagSetSNA() throws Exception {
    SubscribeNotificationsRequest snr = shClientFactory.createSubscribeNotificationsRequest();
    ((DiameterMessageImpl) snr).getGenericData().setReTransmitted(true);

    assertTrue("The 'T' flag should be set in Subscribe-Notifications-Request", snr.getHeader().isPotentiallyRetransmitted());

    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(snr);
    assertFalse("The 'T' flag should not be set in Subscribe-Notifications-Answer", sna.getHeader().isPotentiallyRetransmitted());
  }

  @Test
  public void testGettersAndSettersSNA() throws Exception {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest());

    int nFailures = AvpAssistant.testMethods(sna, SubscribeNotificationsAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostSNA() throws Exception {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sna.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmSNA() throws Exception {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sna.getDestinationRealm());
  }

  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested) http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetSNA() throws Exception {
    long originalValue = 5001;

    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest(), originalValue, true);

    long obtainedValue = sna.getExperimentalResult().getExperimentalResultCode();

    assertTrue("Experimental-Result-Code in SNA should be " + originalValue + " and is " + obtainedValue + ".", originalValue == obtainedValue);
  }

  @Test
  public void isAnswerUDA() throws Exception {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest());
    assertFalse("Request Flag in User-Data-Answer is set.", uda.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedUDA() throws Exception {
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    UserDataAnswer uda = shServerFactory.createUserDataAnswer(udr);
    assertEquals("The 'P' bit is not copied from request in User-Data-Answer, it should. [RFC3588/6.2]", udr.getHeader().isProxiable(), uda.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) udr).getGenericData().setProxiable(!udr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in User-Data-Request, it should.", udr.getHeader().isProxiable() != uda.getHeader().isProxiable());

    uda = shServerFactory.createUserDataAnswer(udr);
    assertEquals("The 'P' bit is not copied from request in User-Data-Answer, it should. [RFC3588/6.2]", udr.getHeader().isProxiable(), uda.getHeader().isProxiable());
  }

  @Test
  public void hasTFlagSetUDA() throws Exception {
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    ((DiameterMessageImpl) udr).getGenericData().setReTransmitted(true);

    assertTrue("The 'T' flag should be set in User-Data-Request", udr.getHeader().isPotentiallyRetransmitted());

    UserDataAnswer uda = shServerFactory.createUserDataAnswer(udr);
    assertFalse("The 'T' flag should not be set in User-Data-Answer", uda.getHeader().isPotentiallyRetransmitted());
  }

  @Test
  public void testGettersAndSettersUDA() throws Exception {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest());

    int nFailures = AvpAssistant.testMethods(uda, UserDataAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostUDA() throws Exception {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", uda.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmUDA() throws Exception {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", uda.getDestinationRealm());
  }

  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested) http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetUDA() throws Exception {
    long originalValue = 5001;

    UserDataAnswer uda = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest(), originalValue, true);

    long obtainedValue = uda.getExperimentalResult().getExperimentalResultCode();

    assertTrue("Experimental-Result-Code in UDA should be " + originalValue + " and is " + obtainedValue + ".", originalValue == obtainedValue);
  }

  @Test
  public void testMessageFactoryApplicationIdChangePUA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    ProfileUpdateAnswer originalPUA = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest());
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalPUA);

    // now we switch..
    originalPUA = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    ProfileUpdateAnswer changedPUA = shServerFactory.createProfileUpdateAnswer(shClientFactory.createProfileUpdateRequest());
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedPUA);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testServerSessionApplicationIdChangePUA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    ProfileUpdateRequest pur = shClientFactory.createProfileUpdateRequest();
    UserIdentityAvp uiAvp = shAvpFactory.createUserIdentity();
    uiAvp.setPublicIdentity("alexandre@mobicents.org");
    pur.setUserIdentity(uiAvp);
    pur.setAuthSessionState(AuthSessionStateType.NO_STATE_MAINTAINED);
    serverSession.fetchSessionData(pur, true);
    ProfileUpdateAnswer originalPUA = serverSession.createProfileUpdateAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalPUA);

    // now we switch..
    originalPUA = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    ProfileUpdateAnswer changedPUA = serverSession.createProfileUpdateAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedPUA);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testMessageFactoryApplicationIdChangePNR() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    PushNotificationRequest originalPNR = shServerFactory.createPushNotificationRequest();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalPNR);

    // now we switch..
    originalPNR = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    PushNotificationRequest changedPNR = shServerFactory.createPushNotificationRequest();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedPNR);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testMessageFactoryApplicationIdChangeSNA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    SubscribeNotificationsAnswer originalSNA = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest());
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalSNA);

    // now we switch..
    originalSNA = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    SubscribeNotificationsAnswer changedSNA = shServerFactory.createSubscribeNotificationsAnswer(shClientFactory.createSubscribeNotificationsRequest());
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedSNA);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testServerSessionApplicationIdChangeSNA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    SubscribeNotificationsRequest snr = shClientFactory.createSubscribeNotificationsRequest();
    UserIdentityAvp uiAvp = shAvpFactory.createUserIdentity();
    uiAvp.setPublicIdentity("alexandre@mobicents.org");
    snr.setUserIdentity(uiAvp);
    snr.setAuthSessionState(AuthSessionStateType.NO_STATE_MAINTAINED);
    serverSession.fetchSessionData(snr, true);
    SubscribeNotificationsAnswer originalSNA = serverSession.createSubscribeNotificationsAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalSNA);

    // now we switch..
    originalSNA = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    SubscribeNotificationsAnswer changedSNA = serverSession.createSubscribeNotificationsAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedSNA);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testMessageFactoryApplicationIdChangeUDA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    UserDataAnswer originalUDA = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest());
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalUDA);

    // now we switch..
    originalUDA = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    UserDataAnswer changedUDA = shServerFactory.createUserDataAnswer(shClientFactory.createUserDataRequest());
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedUDA);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
  }

  @Test
  public void testServerSessionApplicationIdChangeUDA() throws Exception {
    long vendor = 10415L;
    ApplicationId originalAppId = ((ShServerMessageFactoryImpl)shServerFactory).getApplicationId();

    boolean isAuth = originalAppId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;
    boolean isAcct = originalAppId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE;

    boolean isVendor = originalAppId.getVendorId() != 0L;

    assertTrue("Invalid Application-Id (" + originalAppId + "). Should only, and at least, contain either Auth or Acct value.", (isAuth && !isAcct) || (!isAuth && isAcct));

    System.out.println("Default VENDOR-ID for Sh is " + originalAppId.getVendorId());
    // let's create a message and see how it comes...
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    UserIdentityAvp uiAvp = shAvpFactory.createUserIdentity();
    uiAvp.setPublicIdentity("alexandre@mobicents.org");
    udr.setUserIdentity(uiAvp);
    udr.setAuthSessionState(AuthSessionStateType.NO_STATE_MAINTAINED);
    serverSession.fetchSessionData(udr, true);
    UserDataAnswer originalUDA = serverSession.createUserDataAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, originalUDA);

    // now we switch..
    originalUDA = null;
    isVendor = !isVendor;
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(isVendor ? vendor : 0L, isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());

    // create a new message and see how it comes...
    UserDataAnswer changedUDA = serverSession.createUserDataAnswer();
    checkCorrectApplicationIdAVPs(isVendor, isAuth, isAcct, changedUDA);

    // revert back to default
    ((ShServerMessageFactoryImpl)shServerFactory).setApplicationId(originalAppId.getVendorId(), isAuth ? originalAppId.getAuthAppId() : originalAppId.getAcctAppId());
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
          getInstance().add(
              RealmEntry,
              getInstance().add(RealmName, realmName).add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L)).add(RealmHosts, clientHost + ", " + serverHost)
                  .add(RealmLocalAction, "LOCAL").add(RealmEntryIsDynamic, false).add(RealmEntryExpTime, 1000L)));
    }
  }

  public void fireEvent(String sessionId, Message message) {
    // NO-OP
  }

  public void endActivity(DiameterActivityHandle activityHandle) {
    // NO-OP
  }

  public ApplicationId[] getSupportedApplications() {
    // NO-OP
    return null;
  }

  public void update(DiameterActivityHandle activityHandle, DiameterActivity da) {
    // NO-OP
  }

  public void startActivityRemoveTimer(DiameterActivityHandle handle) {
    // NO-OP
  }

  public void stopActivityRemoveTimer(DiameterActivityHandle handle) {
    // NO-OP
  }

}
