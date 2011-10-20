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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.java.slee.resource.diameter.s6a.S6aAVPFactory;
import net.java.slee.resource.diameter.s6a.S6aMessageFactory;
import net.java.slee.resource.diameter.s6a.events.AuthenticationInformationAnswer;
import net.java.slee.resource.diameter.s6a.events.AuthenticationInformationRequest;
import net.java.slee.resource.diameter.s6a.events.CancelLocationAnswer;
import net.java.slee.resource.diameter.s6a.events.CancelLocationRequest;
import net.java.slee.resource.diameter.s6a.events.DeleteSubscriberDataAnswer;
import net.java.slee.resource.diameter.s6a.events.DeleteSubscriberDataRequest;
import net.java.slee.resource.diameter.s6a.events.InsertSubscriberDataAnswer;
import net.java.slee.resource.diameter.s6a.events.InsertSubscriberDataRequest;
import net.java.slee.resource.diameter.s6a.events.NotifyAnswer;
import net.java.slee.resource.diameter.s6a.events.NotifyRequest;
import net.java.slee.resource.diameter.s6a.events.PurgeUEAnswer;
import net.java.slee.resource.diameter.s6a.events.PurgeUERequest;
import net.java.slee.resource.diameter.s6a.events.ResetAnswer;
import net.java.slee.resource.diameter.s6a.events.ResetRequest;
import net.java.slee.resource.diameter.s6a.events.UpdateLocationAnswer;
import net.java.slee.resource.diameter.s6a.events.UpdateLocationRequest;
import net.java.slee.resource.diameter.s6a.events.avp.AMBRAvp;
import net.java.slee.resource.diameter.s6a.events.avp.APNConfigurationAvp;
import net.java.slee.resource.diameter.s6a.events.avp.APNConfigurationProfileAvp;
import net.java.slee.resource.diameter.s6a.events.avp.ActiveAPNAvp;
import net.java.slee.resource.diameter.s6a.events.avp.AllocationRetentionPriorityAvp;
import net.java.slee.resource.diameter.s6a.events.avp.AuthenticationInfoAvp;
import net.java.slee.resource.diameter.s6a.events.avp.DiameterS6aAvpCodes;
import net.java.slee.resource.diameter.s6a.events.avp.EPSLocationInformationAvp;
import net.java.slee.resource.diameter.s6a.events.avp.EPSSubscribedQoSProfileAvp;
import net.java.slee.resource.diameter.s6a.events.avp.EPSUserStateAvp;
import net.java.slee.resource.diameter.s6a.events.avp.EUTRANVectorAvp;
import net.java.slee.resource.diameter.s6a.events.avp.MIP6AgentInfoAvp;
import net.java.slee.resource.diameter.s6a.events.avp.MIPHomeAgentHostAvp;
import net.java.slee.resource.diameter.s6a.events.avp.MMELocationInformationAvp;
import net.java.slee.resource.diameter.s6a.events.avp.MMEUserStateAvp;
import net.java.slee.resource.diameter.s6a.events.avp.RequestedEUTRANAuthenticationInfoAvp;
import net.java.slee.resource.diameter.s6a.events.avp.RequestedUTRANGERANAuthenticationInfoAvp;
import net.java.slee.resource.diameter.s6a.events.avp.SGSNLocationInformationAvp;
import net.java.slee.resource.diameter.s6a.events.avp.SGSNUserStateAvp;
import net.java.slee.resource.diameter.s6a.events.avp.SpecificAPNInfoAvp;
import net.java.slee.resource.diameter.s6a.events.avp.SubscriptionDataAvp;
import net.java.slee.resource.diameter.s6a.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.s6a.events.avp.TerminalInformationAvp;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Stack;
import org.jdiameter.api.s6a.ClientS6aSession;
import org.jdiameter.api.s6a.ServerS6aSession;
import org.jdiameter.common.impl.app.s6a.S6aSessionFactoryImpl;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aClientSessionImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.s6a.S6aServerSessionImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.AMBRAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.APNConfigurationAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.APNConfigurationProfileAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.ActiveAPNAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.AllocationRetentionPriorityAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.AuthenticationInfoAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.EPSLocationInformationAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.EPSSubscribedQoSProfileAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.EPSUserStateAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.EUTRANVectorAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.MIP6AgentInfoAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.MIPHomeAgentHostAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.MMELocationInformationAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.MMEUserStateAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.RequestedEUTRANAuthenticationInfoAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.RequestedUTRANGERANAuthenticationInfoAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.SGSNLocationInformationAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.SGSNUserStateAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.SpecificAPNInfoAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.SubscriptionDataAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.SupportedFeaturesAvpImpl;
import org.mobicents.slee.resource.diameter.s6a.events.avp.TerminalInformationAvpImpl;
import org.mobicents.slee.resources.diameter.tests.factories.CCAFactoriesTest.MyConfiguration;

/**
 * Test class for JAIN SLEE Diameter S6a RA Message and AVP Factories
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class S6aFactoriesTest {

  private static S6aMessageFactory s6aMessageFactory;
  private static S6aAVPFactory s6aAvpFactory;

  private static Stack stack;

  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    s6aAvpFactory = new S6aAVPFactoryImpl(new DiameterAvpFactoryImpl());
    try {
      s6aMessageFactory = new S6aMessageFactoryImpl(stack);

      S6aSessionFactoryImpl sf = new S6aSessionFactoryImpl(stack.getSessionFactory());
      ApplicationId s6aAppId = ApplicationId.createByAuthAppId(DiameterS6aAvpCodes.S6A_VENDOR_ID, DiameterS6aAvpCodes.S6A_AUTH_APP_ID);
      org.jdiameter.server.impl.app.s6a.S6aServerSessionImpl stackServerSession = (org.jdiameter.server.impl.app.s6a.S6aServerSessionImpl) sf.getNewSession("123", ServerS6aSession.class, s6aAppId, new Object[0]);
      org.jdiameter.client.impl.app.s6a.S6aClientSessionImpl stackClientSession = (org.jdiameter.client.impl.app.s6a.S6aClientSessionImpl) sf.getNewSession("321", ClientS6aSession.class, s6aAppId, new Object[0]);
      serverSession = new S6aServerSessionImpl(s6aMessageFactory, s6aAvpFactory, stackServerSession, stackServerSession, null, null, stack);
      clientSession = new S6aClientSessionImpl(s6aMessageFactory, s6aAvpFactory, stackClientSession, stackClientSession, null, null, null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      AvpDictionary.INSTANCE.parseDictionary(S6aFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  private static S6aServerSessionImpl serverSession;
  private static S6aClientSessionImpl clientSession;


  @Test
  public void isRequestULR() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();

    assertTrue("Request Flag in Update-Location-Request is not set.", ulr.getHeader().isRequest());
  }

  @Test
  public void isProxiableULR() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
    assertTrue("The 'P' bit is not set by default in Update-Location-Request it should.", ulr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersULR() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();

    int nFailures = AvpAssistant.testMethods(ulr, UpdateLocationRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerULA() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
    serverSession.fetchSessionData(ulr);
    UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

    assertFalse("Request Flag in Update-Location-Answer is set.", ula.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersULA() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
    serverSession.fetchSessionData(ulr);
    UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

    int nFailures = AvpAssistant.testMethods(ula, UpdateLocationAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostULA() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
    serverSession.fetchSessionData(ulr);
    UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ula.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmULA() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
    serverSession.fetchSessionData(ulr);
    UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ula.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedULA() throws Exception {
    UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
    serverSession.fetchSessionData(ulr);
    UpdateLocationAnswer ula = serverSession.createUpdateLocationAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", ulr.getHeader().isProxiable(), ula.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) ulr).getGenericData().setProxiable(!ulr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Update-Location-Request, it should.",  ulr.getHeader().isProxiable() != ula.getHeader().isProxiable());
    serverSession.fetchSessionData(ulr);

    ula = serverSession.createUpdateLocationAnswer();
    assertEquals("The 'P' bit is not copied from request in Update-Location-Answer, it should. [RFC3588/6.2]", ulr.getHeader().isProxiable(), ula.getHeader().isProxiable());
  }

  @Test
  public void isRequestAIR() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();

    assertTrue("Request Flag in Authentication-Information-Request is not set.", air.getHeader().isRequest());
  }

  @Test
  public void isProxiableAIR() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
    assertTrue("The 'P' bit is not set by default in Authentication-Information-Request it should.", air.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersAIR() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();

    int nFailures = AvpAssistant.testMethods(air, AuthenticationInformationRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerAIA() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
    serverSession.fetchSessionData(air);
    AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

    assertFalse("Request Flag in Authentication-Information-Answer is set.", aia.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersAIA() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
    serverSession.fetchSessionData(air);
    AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

    int nFailures = AvpAssistant.testMethods(aia, AuthenticationInformationAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostAIA() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
    serverSession.fetchSessionData(air);
    AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aia.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmAIA() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
    serverSession.fetchSessionData(air);
    AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aia.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedAIA() throws Exception {
    AuthenticationInformationRequest air = s6aMessageFactory.createAuthenticationInformationRequest();
    serverSession.fetchSessionData(air);
    AuthenticationInformationAnswer aia = serverSession.createAuthenticationInformationAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", air.getHeader().isProxiable(), aia.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) air).getGenericData().setProxiable(!air.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Authentication-Information-Request, it should.",  air.getHeader().isProxiable() != aia.getHeader().isProxiable());
    serverSession.fetchSessionData(air);

    aia = serverSession.createAuthenticationInformationAnswer();
    assertEquals("The 'P' bit is not copied from request in Authentication-Information-Answer, it should. [RFC3588/6.2]", air.getHeader().isProxiable(), aia.getHeader().isProxiable());
  }

  @Test
  public void isRequestCLR() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();

    assertTrue("Request Flag in Cancel-Location-Request is not set.", clr.getHeader().isRequest());
  }

  @Test
  public void isProxiableCLR() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
    assertTrue("The 'P' bit is not set by default in Cancel-Location-Request it should.", clr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersCLR() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();

    int nFailures = AvpAssistant.testMethods(clr, CancelLocationRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerCLA() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
    clientSession.fetchSessionData(clr);
    CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

    assertFalse("Request Flag in Cancel-Location-Answer is set.", cla.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersCLA() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
    clientSession.fetchSessionData(clr);
    CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

    int nFailures = AvpAssistant.testMethods(cla, CancelLocationAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostCLA() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
    clientSession.fetchSessionData(clr);
    CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cla.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmCLA() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
    clientSession.fetchSessionData(clr);
    CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cla.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedCLA() throws Exception {
    CancelLocationRequest clr = s6aMessageFactory.createCancelLocationRequest();
    clientSession.fetchSessionData(clr);
    CancelLocationAnswer cla = clientSession.createCancelLocationAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", clr.getHeader().isProxiable(), cla.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) clr).getGenericData().setProxiable(!clr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Cancel-Location-Request, it should.",  clr.getHeader().isProxiable() != cla.getHeader().isProxiable());
    clientSession.fetchSessionData(clr);

    cla = clientSession.createCancelLocationAnswer();
    assertEquals("The 'P' bit is not copied from request in Cancel-Location-Answer, it should. [RFC3588/6.2]", clr.getHeader().isProxiable(), cla.getHeader().isProxiable());
  }

  @Test
  public void isRequestIDR() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();

    assertTrue("Request Flag in Insert-Subscriber-Data-Request is not set.", idr.getHeader().isRequest());
  }

  @Test
  public void isProxiableIDR() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
    assertTrue("The 'P' bit is not set by default in Insert-Subscriber-Data-Request it should.", idr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersIDR() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();

    int nFailures = AvpAssistant.testMethods(idr, InsertSubscriberDataRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerIDA() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
    clientSession.fetchSessionData(idr);
    InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

    assertFalse("Request Flag in Insert-Subscriber-Data-Answer is set.", ida.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersIDA() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
    clientSession.fetchSessionData(idr);
    InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

    int nFailures = AvpAssistant.testMethods(ida, InsertSubscriberDataAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostIDA() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
    clientSession.fetchSessionData(idr);
    InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ida.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmIDA() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
    clientSession.fetchSessionData(idr);
    InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", ida.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedIDA() throws Exception {
    InsertSubscriberDataRequest idr = s6aMessageFactory.createInsertSubscriberDataRequest();
    clientSession.fetchSessionData(idr);
    InsertSubscriberDataAnswer ida = clientSession.createInsertSubscriberDataAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", idr.getHeader().isProxiable(), ida.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) idr).getGenericData().setProxiable(!idr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Insert-Subscriber-Data-Request, it should.",  idr.getHeader().isProxiable() != ida.getHeader().isProxiable());
    clientSession.fetchSessionData(idr);

    ida = clientSession.createInsertSubscriberDataAnswer();
    assertEquals("The 'P' bit is not copied from request in Insert-Subscriber-Data-Answer, it should. [RFC3588/6.2]", idr.getHeader().isProxiable(), ida.getHeader().isProxiable());
  }

  @Test
  public void isRequestDSR() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();

    assertTrue("Request Flag in Delete-Subscriber-Data-Request is not set.", dsr.getHeader().isRequest());
  }

  @Test
  public void isProxiableDSR() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
    assertTrue("The 'P' bit is not set by default in Delete-Subscriber-Data-Request it should.", dsr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersDSR() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();

    int nFailures = AvpAssistant.testMethods(dsr, DeleteSubscriberDataRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerDSA() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
    clientSession.fetchSessionData(dsr);
    DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

    assertFalse("Request Flag in Delete-Subscriber-Data-Answer is set.", dsa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersDSA() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
    clientSession.fetchSessionData(dsr);
    DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

    int nFailures = AvpAssistant.testMethods(dsa, DeleteSubscriberDataAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostDSA() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
    clientSession.fetchSessionData(dsr);
    DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dsa.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmDSA() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
    clientSession.fetchSessionData(dsr);
    DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dsa.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedDSA() throws Exception {
    DeleteSubscriberDataRequest dsr = s6aMessageFactory.createDeleteSubscriberDataRequest();
    clientSession.fetchSessionData(dsr);
    DeleteSubscriberDataAnswer dsa = clientSession.createDeleteSubscriberDataAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", dsr.getHeader().isProxiable(), dsa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) dsr).getGenericData().setProxiable(!dsr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Delete-Subscriber-Data-Request, it should.",  dsr.getHeader().isProxiable() != dsa.getHeader().isProxiable());
    clientSession.fetchSessionData(dsr);

    dsa = clientSession.createDeleteSubscriberDataAnswer();
    assertEquals("The 'P' bit is not copied from request in Delete-Subscriber-Data-Answer, it should. [RFC3588/6.2]", dsr.getHeader().isProxiable(), dsa.getHeader().isProxiable());
  }

  @Test
  public void isRequestPUR() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();

    assertTrue("Request Flag in Purge-UE-Request is not set.", pur.getHeader().isRequest());
  }

  @Test
  public void isProxiablePUR() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
    assertTrue("The 'P' bit is not set by default in Purge-UE-Request it should.", pur.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersPUR() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();

    int nFailures = AvpAssistant.testMethods(pur, PurgeUERequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerPUA() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
    serverSession.fetchSessionData(pur);
    PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

    assertFalse("Request Flag in Purge-UE-Answer is set.", pua.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersPUA() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
    serverSession.fetchSessionData(pur);
    PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

    int nFailures = AvpAssistant.testMethods(pua, PurgeUEAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostPUA() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
    serverSession.fetchSessionData(pur);
    PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmPUA() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
    serverSession.fetchSessionData(pur);
    PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedPUA() throws Exception {
    PurgeUERequest pur = s6aMessageFactory.createPurgeUERequest();
    serverSession.fetchSessionData(pur);
    PurgeUEAnswer pua = serverSession.createPurgeUEAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", pur.getHeader().isProxiable(), pua.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) pur).getGenericData().setProxiable(!pur.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Purge-UE-Request, it should.",  pur.getHeader().isProxiable() != pua.getHeader().isProxiable());
    serverSession.fetchSessionData(pur);

    pua = serverSession.createPurgeUEAnswer();
    assertEquals("The 'P' bit is not copied from request in Purge-UE-Answer, it should. [RFC3588/6.2]", pur.getHeader().isProxiable(), pua.getHeader().isProxiable());
  }

  @Test
  public void isRequestRSR() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();

    assertTrue("Request Flag in Reset-Request is not set.", rsr.getHeader().isRequest());
  }

  @Test
  public void isProxiableRSR() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();
    assertTrue("The 'P' bit is not set by default in Reset-Request it should.", rsr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersRSR() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();

    int nFailures = AvpAssistant.testMethods(rsr, ResetRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerRSA() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();
    clientSession.fetchSessionData(rsr);
    ResetAnswer rsa = clientSession.createResetAnswer();

    assertFalse("Request Flag in Reset-Answer is set.", rsa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersRSA() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();
    clientSession.fetchSessionData(rsr);
    ResetAnswer rsa = clientSession.createResetAnswer();

    int nFailures = AvpAssistant.testMethods(rsa, ResetAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostRSA() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();
    clientSession.fetchSessionData(rsr);
    ResetAnswer rsa = clientSession.createResetAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", rsa.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmRSA() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();
    clientSession.fetchSessionData(rsr);
    ResetAnswer rsa = clientSession.createResetAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", rsa.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedRSA() throws Exception {
    ResetRequest rsr = s6aMessageFactory.createResetRequest();
    clientSession.fetchSessionData(rsr);
    ResetAnswer rsa = clientSession.createResetAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", rsr.getHeader().isProxiable(), rsa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) rsr).getGenericData().setProxiable(!rsr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Reset-Request, it should.",  rsr.getHeader().isProxiable() != rsa.getHeader().isProxiable());
    clientSession.fetchSessionData(rsr);

    rsa = clientSession.createResetAnswer();
    assertEquals("The 'P' bit is not copied from request in Reset-Answer, it should. [RFC3588/6.2]", rsr.getHeader().isProxiable(), rsa.getHeader().isProxiable());
  }

  @Test
  public void isRequestNOR() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();

    assertTrue("Request Flag in Notify-Request is not set.", nor.getHeader().isRequest());
  }

  @Test
  public void isProxiableNOR() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
    assertTrue("The 'P' bit is not set by default in Notify-Request it should.", nor.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersNOR() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();

    int nFailures = AvpAssistant.testMethods(nor, NotifyRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerNOA() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
    serverSession.fetchSessionData(nor);
    NotifyAnswer noa = serverSession.createNotifyAnswer();

    assertFalse("Request Flag in Notify-Answer is set.", noa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersNOA() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
    serverSession.fetchSessionData(nor);
    NotifyAnswer noa = serverSession.createNotifyAnswer();

    int nFailures = AvpAssistant.testMethods(noa, NotifyAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostNOA() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
    serverSession.fetchSessionData(nor);
    NotifyAnswer noa = serverSession.createNotifyAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", noa.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmNOA() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
    serverSession.fetchSessionData(nor);
    NotifyAnswer noa = serverSession.createNotifyAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", noa.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedNOA() throws Exception {
    NotifyRequest nor = s6aMessageFactory.createNotifyRequest();
    serverSession.fetchSessionData(nor);
    NotifyAnswer noa = serverSession.createNotifyAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", nor.getHeader().isProxiable(), noa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) nor).getGenericData().setProxiable(!nor.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Notify-Request, it should.",  nor.getHeader().isProxiable() != noa.getHeader().isProxiable());
    serverSession.fetchSessionData(nor);

    noa = serverSession.createNotifyAnswer();
    assertEquals("The 'P' bit is not copied from request in Notify-Answer, it should. [RFC3588/6.2]", nor.getHeader().isProxiable(), noa.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersActiveAPN() throws Exception {
    ActiveAPNAvp avp = s6aAvpFactory.createActiveAPN();

    int nFailures = AvpAssistant.testMethods(avp, ActiveAPNAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersAMBR() throws Exception {
    AMBRAvp avp = s6aAvpFactory.createAMBR();

    int nFailures = AvpAssistant.testMethods(avp, AMBRAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersAPNConfiguration() throws Exception {
    APNConfigurationAvp avp = s6aAvpFactory.createAPNConfiguration();

    int nFailures = AvpAssistant.testMethods(avp, APNConfigurationAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersAPNConfigurationProfile() throws Exception {
    APNConfigurationProfileAvp avp = s6aAvpFactory.createAPNConfigurationProfile();

    int nFailures = AvpAssistant.testMethods(avp, APNConfigurationProfileAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersAllocationRetentionPriority() throws Exception {
    AllocationRetentionPriorityAvp avp = s6aAvpFactory.createAllocationRetentionPriority();

    int nFailures = AvpAssistant.testMethods(avp, AllocationRetentionPriorityAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersAuthenticationInfo() throws Exception {
    AuthenticationInfoAvp avp = s6aAvpFactory.createAuthenticationInfo();

    int nFailures = AvpAssistant.testMethods(avp, AuthenticationInfoAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersEPSLocationInformation() throws Exception {
    EPSLocationInformationAvp avp = s6aAvpFactory.createEPSLocationInformation();

    int nFailures = AvpAssistant.testMethods(avp, EPSLocationInformationAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersEPSSubscribedQoSProfile() throws Exception {
    EPSSubscribedQoSProfileAvp avp = s6aAvpFactory.createEPSSubscribedQoSProfile();

    int nFailures = AvpAssistant.testMethods(avp, EPSSubscribedQoSProfileAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersEPSUserState() throws Exception {
    EPSUserStateAvp avp = s6aAvpFactory.createEPSUserState();

    int nFailures = AvpAssistant.testMethods(avp, EPSUserStateAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersEUTRANVector() throws Exception {
    EUTRANVectorAvp avp = s6aAvpFactory.createEUTRANVector();

    int nFailures = AvpAssistant.testMethods(avp, EUTRANVectorAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersMIP6AgentInfo() throws Exception {
    MIP6AgentInfoAvp avp = s6aAvpFactory.createMIP6AgentInfo();

    int nFailures = AvpAssistant.testMethods(avp, MIP6AgentInfoAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersMIPHomeAgentHost() throws Exception {
    MIPHomeAgentHostAvp avp = s6aAvpFactory.createMIPHomeAgentHost();

    int nFailures = AvpAssistant.testMethods(avp, MIPHomeAgentHostAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersMMELocationInformation() throws Exception {
    MMELocationInformationAvp avp = s6aAvpFactory.createMMELocationInformation();

    int nFailures = AvpAssistant.testMethods(avp, MMELocationInformationAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersMMEUserState() throws Exception {
    MMEUserStateAvp avp = s6aAvpFactory.createMMEUserState();

    int nFailures = AvpAssistant.testMethods(avp, MMEUserStateAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersRequestedEUTRANAuthenticationInfo() throws Exception {
    RequestedEUTRANAuthenticationInfoAvp avp = s6aAvpFactory.createRequestedEUTRANAuthenticationInfo();

    int nFailures = AvpAssistant.testMethods(avp, RequestedEUTRANAuthenticationInfoAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersRequestedUTRANGERANAuthenticationInfo() throws Exception {
    RequestedUTRANGERANAuthenticationInfoAvp avp = s6aAvpFactory.createRequestedUTRANGERANAuthenticationInfo();

    int nFailures = AvpAssistant.testMethods(avp, RequestedUTRANGERANAuthenticationInfoAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersSGSNLocationInformation() throws Exception {
    SGSNLocationInformationAvp avp = s6aAvpFactory.createSGSNLocationInformation();

    int nFailures = AvpAssistant.testMethods(avp, SGSNLocationInformationAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersSGSNUserState() throws Exception {
    SGSNUserStateAvp avp = s6aAvpFactory.createSGSNUserState();

    int nFailures = AvpAssistant.testMethods(avp, SGSNUserStateAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersSpecificAPNInfo() throws Exception {
    SpecificAPNInfoAvp avp = s6aAvpFactory.createSpecificAPNInfo();

    int nFailures = AvpAssistant.testMethods(avp, SpecificAPNInfoAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersSubscriptionData() throws Exception {
    SubscriptionDataAvp avp = s6aAvpFactory.createSubscriptionData();

    int nFailures = AvpAssistant.testMethods(avp, SubscriptionDataAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersSupportedFeatures() throws Exception {
    SupportedFeaturesAvp avp = s6aAvpFactory.createSupportedFeatures();

    int nFailures = AvpAssistant.testMethods(avp, SupportedFeaturesAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void testGettersAndSettersTerminalInformation() throws Exception {
    TerminalInformationAvp avp = s6aAvpFactory.createTerminalInformation();

    int nFailures = AvpAssistant.testMethods(avp, TerminalInformationAvpImpl.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

}
