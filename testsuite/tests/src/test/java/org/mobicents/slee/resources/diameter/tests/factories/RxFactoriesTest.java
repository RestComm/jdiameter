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
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;
import net.java.slee.resource.diameter.rx.RxAvpFactory;
import net.java.slee.resource.diameter.rx.RxMessageFactory;
import net.java.slee.resource.diameter.rx.events.*;

import net.java.slee.resource.diameter.rx.events.avp.AcceptableServiceInfoAvp;
import net.java.slee.resource.diameter.rx.events.avp.AccessNetworkChargingIdentifierAvp;
import net.java.slee.resource.diameter.rx.events.avp.FlowUsage;
import net.java.slee.resource.diameter.rx.events.avp.FlowsAvp;
import net.java.slee.resource.diameter.rx.events.avp.MediaComponentDescriptionAvp;
import net.java.slee.resource.diameter.rx.events.avp.MediaSubComponentAvp;
import net.java.slee.resource.diameter.rx.events.avp.SponsoredConnectivityDataAvp;
import net.java.slee.resource.diameter.rx.events.avp.SupportedFeaturesAvp;
import static org.junit.Assert.*;
import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.Request;
import org.jdiameter.api.Stack;
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.app.rx.RxSessionFactoryImpl;
import org.jdiameter.server.impl.app.rx.ServerRxSessionDataLocalImpl;
import org.jdiameter.server.impl.app.rx.ServerRxSessionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.rx.RxAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.rx.RxMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.rx.RxServerSessionActivityImpl;

/**
 * Test class for JAIN SLEE Diameter Rx' RA Message and AVP Factories
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RxFactoriesTest {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static RxMessageFactory rxMessageFactory;
  private static RxAvpFactory rxAvpFactory;

  private static Stack stack;

  private static ServerRxSession session; 

  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch ( Exception e ) {
    	
      throw new RuntimeException("Failed to initialize the stack.",e);
    }

    DiameterMessageFactoryImpl baseFactory = new DiameterMessageFactoryImpl(stack);
    DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

    rxAvpFactory = new RxAvpFactoryImpl(baseAvpFactory);
    try {
      rxMessageFactory = new RxMessageFactoryImpl(baseFactory, stack.getSessionFactory().getNewSession().getSessionId(), stack);
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    try {
      AvpDictionary.INSTANCE.parseDictionary( RxFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  private RxServerSessionActivityImpl rxServerSession = null;

  public RxFactoriesTest() {
    try {
      RxSessionFactoryImpl rxSessionFactory = new RxSessionFactoryImpl(stack.getSessionFactory());
      session = new ServerRxSessionImpl(new ServerRxSessionDataLocalImpl(), rxSessionFactory, (ISessionFactory) stack.getSessionFactory(), rxSessionFactory, rxSessionFactory, rxSessionFactory);
      rxServerSession = new RxServerSessionActivityImpl(rxMessageFactory.getBaseMessageFactory(), rxAvpFactory.getBaseFactory(), session, new DiameterIdentity("127.0.0.2"), new DiameterIdentity("mobicents.org"), stack);
      //????
      //((RxServerSessionActivityImpl)roServerSession).fetchCurrentState(roMessageFactory.createRxAARequest());
    }
    catch (IllegalDiameterStateException e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

//  // AA-Request
//
//  @Test
//  public void isRequestAAR() throws Exception {
//    AARequest aar = rxMessageFactory.createAARequest();
//    assertTrue("Request Flag in AA-Request is not set.", aar.getHeader().isRequest());
//  }
//
//  @Test
//  public void isProxiableAAR() throws Exception {
//    AARequest rar = rxMessageFactory.createAARequest();
//    assertTrue("The 'P' bit is not set by default in Rx' AA-Request, it should.", rar.getHeader().isProxiable());
//  }
//
//  @Test
//  public void testGettersAndSettersAAR() throws Exception {
//    AARequest aar = rxMessageFactory.createAARequest();
//    int nFailures = AvpAssistant.testMethods(aar, AARequest.class);
//
//    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
//  }  
//
  @Test
  public void hasRxApplicationIdAAR() throws Exception {
    AARequest aar = rxMessageFactory.createAARequest();
    assertTrue("Auth-Application-Id AVP in Rx AAR must be " + RxMessageFactory._Rx_AUTH_APP_ID + ", it is " + aar.getAuthApplicationId(), aar.getAuthApplicationId() == RxMessageFactory._Rx_AUTH_APP_ID);
  }

  // AA-Answer
  @Test
  public void isAnswerAAA() throws Exception {
	  rxServerSession.fetchCurrentState(rxMessageFactory.createAARequest());
    AAAnswer aaa = rxServerSession.createAAAnswer();
    assertFalse("Request Flag in AA-Answer is set.", aaa.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedAAA() throws Exception {
    AARequest rar = rxMessageFactory.createAARequest();
    AAAnswer raa = rxMessageFactory.createAAAnswer(rar);
    assertEquals("The 'P' bit is not copied from request in Rx' AA-Answer, it should. [RFC3588/6.2]", rar.getHeader().isProxiable(), raa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) rar).getGenericData().setProxiable(!rar.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Rx' AA-Request, it should.", rar.getHeader().isProxiable() != raa.getHeader().isProxiable());

    raa = rxMessageFactory.createAAAnswer(rar);
    assertEquals("The 'P' bit is not copied from request in Rx' AA-Answer, it should. [RFC3588/6.2]", rar.getHeader().isProxiable(), raa.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersAAA() throws Exception {
	  rxServerSession.fetchCurrentState(rxMessageFactory.createAARequest());
    AAAnswer aaa = rxServerSession.createAAAnswer();

    int nFailures = AvpAssistant.testMethods(aaa, AAAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasRxApplicationIdAAA() throws Exception {
	  rxServerSession.fetchCurrentState(rxMessageFactory.createAARequest());
    AAAnswer aaa = rxServerSession.createAAAnswer();
    assertTrue("Auth-Application-Id AVP in Rx AAA must be " + RxMessageFactory._Rx_AUTH_APP_ID + ", it is " + aaa.getAuthApplicationId(), aaa.getAuthApplicationId() == RxMessageFactory._Rx_AUTH_APP_ID);
  }

  @Test
  public void hasDestinationHostAAA() throws Exception {
	  rxServerSession.fetchCurrentState(rxMessageFactory.createAARequest());
    AAAnswer aaa = rxServerSession.createAAAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aaa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmAAA() throws Exception {
	  rxServerSession.fetchCurrentState(rxMessageFactory.createAARequest());
    AAAnswer aaa = rxServerSession.createAAAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aaa.getDestinationRealm());    
  }

  // Abort-Session-Request

  @Test
  public void isRequestASR() throws Exception {
    AbortSessionRequest asr = rxMessageFactory.createAbortSessionRequest();
    assertTrue("Request Flag in Abort-Session-Request is not set.", asr.getHeader().isRequest());
  }

  @Test
  public void isProxiableASR() throws Exception {
    AbortSessionRequest rar = rxMessageFactory.createAbortSessionRequest();
    assertTrue("The 'P' bit is not set by default in Rx' Abort-Session-Request, it should.", rar.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersASR() throws Exception {
    AbortSessionRequest asr = rxMessageFactory.createAbortSessionRequest();

    int nFailures = AvpAssistant.testMethods(asr, AbortSessionRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasRxApplicationIdASR() throws Exception {
    AbortSessionRequest asr = rxMessageFactory.createAbortSessionRequest();
    assertTrue("Auth-Application-Id AVP in Rx ASR must be " + RxMessageFactory._Rx_AUTH_APP_ID + ", it is " + asr.getAuthApplicationId(), asr.getAuthApplicationId() == RxMessageFactory._Rx_AUTH_APP_ID);
  }

  // Abort-Session-Answer

  @Test
  public void isAnswerASA() throws Exception {
    AbortSessionAnswer asa = rxMessageFactory.createAbortSessionAnswer(rxMessageFactory.createAbortSessionRequest());
    assertFalse("Request Flag in Abort-Session-Answer is set.", asa.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedASA() throws Exception {
    AbortSessionRequest rar = rxMessageFactory.createAbortSessionRequest();
    AbortSessionAnswer raa = rxMessageFactory.createAbortSessionAnswer(rar);
    assertEquals("The 'P' bit is not copied from request in Rx' Abort-Session-Answer, it should. [RFC3588/6.2]", rar.getHeader().isProxiable(), raa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) rar).getGenericData().setProxiable(!rar.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Rx' Abort-Session-Request, it should.", rar.getHeader().isProxiable() != raa.getHeader().isProxiable());

    raa = rxMessageFactory.createAbortSessionAnswer(rar);
    assertEquals("The 'P' bit is not copied from request in Rx' Abort-Session-Answer, it should. [RFC3588/6.2]", rar.getHeader().isProxiable(), raa.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersASA() throws Exception {
    AbortSessionAnswer asa = rxMessageFactory.createAbortSessionAnswer(rxMessageFactory.createAbortSessionRequest());

    int nFailures = AvpAssistant.testMethods(asa, AbortSessionAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasDestinationHostASA() throws Exception {
    AbortSessionAnswer asa = rxMessageFactory.createAbortSessionAnswer(rxMessageFactory.createAbortSessionRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmASA() throws Exception {
    AbortSessionAnswer asa = rxMessageFactory.createAbortSessionAnswer(rxMessageFactory.createAbortSessionRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationRealm());    
  }

  // Re-Auth-Request

  @Test
  public void isRequestRAR() throws Exception {
    ReAuthRequest rar = rxMessageFactory.createReAuthRequest();
    assertTrue("Request Flag in Re-Auth-Request is not set.", rar.getHeader().isRequest());
  }

  @Test
  public void isProxiableRAR() throws Exception {
    ReAuthRequest rar = rxMessageFactory.createReAuthRequest();
    assertTrue("The 'P' bit is not set by default in Rx' Re-Auth-Request, it should.", rar.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersRAR() throws Exception {
    ReAuthRequest rar = rxMessageFactory.createReAuthRequest();

    int nFailures = AvpAssistant.testMethods(rar, ReAuthRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasRxApplicationIdRAR() throws Exception {
    ReAuthRequest rar = rxMessageFactory.createReAuthRequest();
    assertTrue("Auth-Application-Id AVP in Rx RAR must be " + RxMessageFactory._Rx_AUTH_APP_ID + ", it is " + rar.getAuthApplicationId(), rar.getAuthApplicationId() == RxMessageFactory._Rx_AUTH_APP_ID);
  }

  // Re-Auth-Answer

  @Test
  public void isAnswerRAA() throws Exception {
    ReAuthAnswer raa = rxMessageFactory.createReAuthAnswer(rxMessageFactory.createReAuthRequest());
    assertFalse("Request Flag in Re-Auth-Answer is set.", raa.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedRAA() throws Exception {
    ReAuthRequest rar = rxMessageFactory.createReAuthRequest();
    ReAuthAnswer raa = rxMessageFactory.createReAuthAnswer(rar);
    assertEquals("The 'P' bit is not copied from request in Rx' Re-Auth-Answer, it should. [RFC3588/6.2]", rar.getHeader().isProxiable(), raa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) rar).getGenericData().setProxiable(!rar.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Rx' Re-Auth-Request, it should.", rar.getHeader().isProxiable() != raa.getHeader().isProxiable());

    raa = rxMessageFactory.createReAuthAnswer(rar);
    assertEquals("The 'P' bit is not copied from request in Rx' Re-Auth-Answer, it should. [RFC3588/6.2]", rar.getHeader().isProxiable(), raa.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersRAA() throws Exception {
    ReAuthAnswer raa = rxMessageFactory.createReAuthAnswer(rxMessageFactory.createReAuthRequest());

    int nFailures = AvpAssistant.testMethods(raa, ReAuthAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasDestinationHostRAA() throws Exception {
    ReAuthAnswer raa = rxMessageFactory.createReAuthAnswer(rxMessageFactory.createReAuthRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmRAA() throws Exception {
    ReAuthAnswer raa = rxMessageFactory.createReAuthAnswer(rxMessageFactory.createReAuthRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationRealm());    
  }

  // Session-Termination-Request

  @Test
  public void isRequestSTR() throws Exception {
    SessionTerminationRequest str = rxMessageFactory.createSessionTerminationRequest();
    assertTrue("Request Flag in Session-Termination-Request is not set.", str.getHeader().isRequest());
  }

  @Test
  public void isProxiableSTR() throws Exception {
    SessionTerminationRequest str = rxMessageFactory.createSessionTerminationRequest();
    assertTrue("The 'P' bit is not set by default in Session-Termination-Request, it should.", str.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersSTR() throws Exception {
    SessionTerminationRequest str = rxMessageFactory.createSessionTerminationRequest();

    int nFailures = AvpAssistant.testMethods(str, SessionTerminationRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasRxApplicationIdSTR() throws Exception {
    SessionTerminationRequest str = rxMessageFactory.createSessionTerminationRequest();
    assertTrue("Auth-Application-Id AVP in Rx STR must be " + RxMessageFactory._Rx_AUTH_APP_ID + ", it is " + str.getAuthApplicationId(), str.getAuthApplicationId() == RxMessageFactory._Rx_AUTH_APP_ID);
  }

  // Session-Termination-Answer

  @Test
  public void isAnswerSTA() throws Exception {
    SessionTerminationAnswer sta = rxMessageFactory.createSessionTerminationAnswer(rxMessageFactory.createSessionTerminationRequest());
    assertFalse("Request Flag in Re-Auth-Answer is set.", sta.getHeader().isRequest());
  }

  @Test
  public void isProxiableCopiedSTA() throws Exception {
    SessionTerminationRequest str = rxMessageFactory.createSessionTerminationRequest();
    SessionTerminationAnswer sta = rxMessageFactory.createSessionTerminationAnswer(str);
    assertEquals("The 'P' bit is not copied from request in Session-Termination-Answer, it should. [RFC3588/6.2]", str.getHeader().isProxiable(), sta.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) str).getGenericData().setProxiable(!str.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Session-Termination-Request, it should.", str.getHeader().isProxiable() != sta.getHeader().isProxiable());

    sta = rxMessageFactory.createSessionTerminationAnswer(str);
    assertEquals("The 'P' bit is not copied from request in Session-Termination-Answer, it should. [RFC3588/6.2]", str.getHeader().isProxiable(), sta.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersSTA() throws Exception {
    SessionTerminationAnswer sta = rxMessageFactory.createSessionTerminationAnswer(rxMessageFactory.createSessionTerminationRequest());

    int nFailures = AvpAssistant.testMethods(sta, SessionTerminationAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }  

  @Test
  public void hasDestinationHostSTA() throws Exception {
    SessionTerminationAnswer sta = rxMessageFactory.createSessionTerminationAnswer(rxMessageFactory.createSessionTerminationRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmSTA() throws Exception {
    SessionTerminationAnswer sta = rxMessageFactory.createSessionTerminationAnswer(rxMessageFactory.createSessionTerminationRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationRealm());    
  }

  // Rx AVP Factory Tests 

//  private static BindingInformation BI_AVP_DEFAULT = rxAvpFactory.createBindingInformation();
//  private static BindingInputList BIL_AVP_DEFAULT = rxAvpFactory.createBindingInputList();
//  private static BindingOutputList BOL_AVP_DEFAULT = rxAvpFactory.createBindingOutputList();
//  private static FlowGrouping FG_AVP_DEFAULT = rxAvpFactory.createFlowGrouping();
//  private static Flows F_AVP_DEFAULT = rxAvpFactory.createFlows();
//  private static GloballyUniqueAddress GUA_AVP_DEFAULT = rxAvpFactory.createGloballyUniqueAddress();
//  private static MediaComponentDescription MCD_AVP_DEFAULT = rxAvpFactory.createMediaComponentDescription();
//  private static MediaSubComponent MSC_AVP_DEFAULT = rxAvpFactory.createMediaSubComponent();
//  private static V4TransportAddress V4TA_AVP_DEFAULT = rxAvpFactory.createV4TransportAddress();
//  private static V6TransportAddress V6TA_AVP_DEFAULT = rxAvpFactory.createV6TransportAddress();
//
//  static {
//    V4TA_AVP_DEFAULT.setFramedIPAddress("255.255.255.254");
//    V4TA_AVP_DEFAULT.setPortNumber(13579);
//
//    V6TA_AVP_DEFAULT.setFramedIPV6Prefix("A123:B456:C789:DE80::/57");
//    V6TA_AVP_DEFAULT.setPortNumber(24680);
//
//    BIL_AVP_DEFAULT.setV4TransportAddress(V4TA_AVP_DEFAULT);
//    BIL_AVP_DEFAULT.setV6TransportAddress(V6TA_AVP_DEFAULT);
//
//    BI_AVP_DEFAULT.setBindingInputList(BIL_AVP_DEFAULT);
//    BI_AVP_DEFAULT.setBindingOutputList(BOL_AVP_DEFAULT);
//
//    F_AVP_DEFAULT.setFlowNumber(1);
//    F_AVP_DEFAULT.setMediaComponentNumber(2);
//
//    FG_AVP_DEFAULT.setFlow(F_AVP_DEFAULT);
//
//    GUA_AVP_DEFAULT.setAddressRealm("mobicents.org");
//    GUA_AVP_DEFAULT.setFramedIPAddress("255.255.255.254");
//    GUA_AVP_DEFAULT.setFramedIPV6Prefix("A123:B456:C789:DE80::/57");
//
//    MSC_AVP_DEFAULT.setFlowDescription(new IPFilterRule("deny in ip from 1.2.3.4/24 to any"));
//    MSC_AVP_DEFAULT.setFlowNumber(7);
//    MSC_AVP_DEFAULT.setFlowStatus(FlowStatus.ENABLED);
//    MSC_AVP_DEFAULT.setFlowUsage(FlowUsage.RTCP);
//    MSC_AVP_DEFAULT.setMaxRequestedBandwidthDL(555);
//    MSC_AVP_DEFAULT.setMaxRequestedBandwidthUL(222);
//
//    MCD_AVP_DEFAULT.setAFApplicationIdentifier("AFApplicationIdentifier");
//    MCD_AVP_DEFAULT.setCodecData("codecData");
//    MCD_AVP_DEFAULT.setFlowStatus(FlowStatus.DISABLED);
//    MCD_AVP_DEFAULT.setMaxRequestedBandwidthDL(999);
//    MCD_AVP_DEFAULT.setMaxRequestedBandwidthUL(111);
//    MCD_AVP_DEFAULT.setMediaAuthorizationContextId("mediaAuthorizationContextId");
//    MCD_AVP_DEFAULT.setMediaComponentNumber(1);
//    MCD_AVP_DEFAULT.setMediaSubComponent(MSC_AVP_DEFAULT);
//    MCD_AVP_DEFAULT.setReservationClass(4);
//    MCD_AVP_DEFAULT.setReservationPriority(ReservationPriority.PRIORITYSEVEN);
//    MCD_AVP_DEFAULT.setRRBandwidth(65);
//    MCD_AVP_DEFAULT.setRSBandwidth(56);
//    MCD_AVP_DEFAULT.setTransportClass(76576);
//  }
//
  @Test
  public void testAvpFactoryCreateAcceptableServiceInfo() throws Exception {

    String avpName = "Acceptable-Service-Info";

    // Create AVP with mandatory values
    AcceptableServiceInfoAvp biAvp1 = rxAvpFactory.createAcceptableServiceInfo();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", biAvp1);

    // Create AVP with default constructor
    AcceptableServiceInfoAvp biAvp2 = rxAvpFactory.createAcceptableServiceInfo();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", biAvp1, biAvp2);
    
    // Make new copy
    biAvp2 = rxAvpFactory.createAcceptableServiceInfo();
    
    // And set all values using setters
    AvpAssistant.testSetters(biAvp2);
    
    // Create empty...
    AcceptableServiceInfoAvp biAvp3 = rxAvpFactory.createAcceptableServiceInfo();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(biAvp3, false);

    // Set all previous values
    biAvp3.setExtensionAvps(biAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(biAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(biAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", biAvp2, biAvp3);
  }

  @Test
  public void testAvpFactoryAccessNetworkChargingIdentifier() throws Exception {

    String avpName = "Access-Network-Charging-Identifier";

    // Create AVP with mandatory values
    AccessNetworkChargingIdentifierAvp bilAvp1 = rxAvpFactory.createAccessNetworkChargingIdentifier();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", bilAvp1);

    // Create AVP with default constructor
    AccessNetworkChargingIdentifierAvp bilAvp2 = rxAvpFactory.createAccessNetworkChargingIdentifier();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", bilAvp1, bilAvp2);
    
    // Make new copy
    bilAvp2 = rxAvpFactory.createAccessNetworkChargingIdentifier();
    
    // And set all values using setters
    AvpAssistant.testSetters(bilAvp2);
    
    // Create empty...
    AccessNetworkChargingIdentifierAvp bilAvp3 = rxAvpFactory.createAccessNetworkChargingIdentifier();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(bilAvp3, false);

    // Set all previous values
    bilAvp3.setExtensionAvps(bilAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(bilAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(bilAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", bilAvp2, bilAvp3);
  }

  @Test
  public void testAvpFactoryFlows() throws Exception {

    String avpName = "Flows";

    // Create AVP with mandatory values
    FlowsAvp bolAvp1 = rxAvpFactory.createFlows();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", bolAvp1);

    // Create AVP with default constructor
    FlowsAvp bolAvp2 = rxAvpFactory.createFlows();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", bolAvp1, bolAvp2);
    
    // Make new copy
    bolAvp2 = rxAvpFactory.createFlows();
    
    // And set all values using setters
    AvpAssistant.testSetters(bolAvp2);
    
    // Create empty...
    FlowsAvp bolAvp3 = rxAvpFactory.createFlows();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(bolAvp3, false);

    // Set all previous values
    bolAvp3.setExtensionAvps(bolAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(bolAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(bolAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", bolAvp2, bolAvp3);
  }

  @Test
  public void testAvpFactoryMediaComponentDescription() throws Exception {

    String avpName = "Media-Component-Description";

    // Create AVP with mandatory values
    MediaComponentDescriptionAvp fgAvp1 = rxAvpFactory.createMediaComponentDescription();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", fgAvp1);

    // Create AVP with default constructor
    MediaComponentDescriptionAvp fgAvp2 = rxAvpFactory.createMediaComponentDescription();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", fgAvp1, fgAvp2);
    
    // Make new copy
    fgAvp2 = rxAvpFactory.createMediaComponentDescription();
    
    // And set all values using setters
    AvpAssistant.testSetters(fgAvp2);
    
    // Create empty...
    MediaComponentDescriptionAvp fgAvp3 = rxAvpFactory.createMediaComponentDescription();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(fgAvp3, false);

    // Set all previous values
    fgAvp3.setExtensionAvps(fgAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(fgAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(fgAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", fgAvp2, fgAvp3);
  }

  //Commented out since Gx has different definition.
  @Test
  public void testAvpFactoryMediaSubComponent() throws Exception {

	  String avpName = "Media-Sub-Component";

	    // Create AVP with mandatory values
	    MediaSubComponentAvp mscAvp1 = rxAvpFactory.createMediaSubComponent();
	    
	    // Make sure it's not null
	    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mscAvp1);

	    // Create AVP with default constructor
	    MediaSubComponentAvp mscAvp2 = rxAvpFactory.createMediaSubComponent();
	    
	    // Should not contain mandatory values

	    // Set mandatory values
	    
	    // Make sure it's equal to the one created with mandatory values constructor
	    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mscAvp1, mscAvp2);
	    
	    // Make new copy
	    mscAvp2 = rxAvpFactory.createMediaSubComponent();
	    
	    // And set all values using setters
	    AvpAssistant.testSetters(mscAvp2);
	    
	    // Create empty...
	    MediaSubComponentAvp mscAvp3 = rxAvpFactory.createMediaSubComponent();
	    
	    // Verify that no values have been set
	    AvpAssistant.testHassers(mscAvp3, false);

	    // Set all previous values
	    mscAvp3.setExtensionAvps(mscAvp2.getExtensionAvps());
	    
	    // Verify if values have been set
	    AvpAssistant.testHassers(mscAvp3, true);
	    
	    // Verify if values have been correctly set
	    AvpAssistant.testGetters(mscAvp3);
	    
	    // Make sure they match!
	    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", mscAvp2, mscAvp3);
  }

  @Test
  public void testAvpFactorySponsoredConnectivityData() throws Exception {

    String avpName = "Sponsored-Connectivity-Data";

    // Create AVP with mandatory values
    SponsoredConnectivityDataAvp guaAvp1 = rxAvpFactory.createSponsoredConnectivityData();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", guaAvp1);

    // Create AVP with default constructor
    SponsoredConnectivityDataAvp guaAvp2 = rxAvpFactory.createSponsoredConnectivityData();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", guaAvp1, guaAvp2);
    
    // Make new copy
    guaAvp2 = rxAvpFactory.createSponsoredConnectivityData();
    
    // And set all values using setters
    AvpAssistant.testSetters(guaAvp2);
    
    // Create empty...
    SponsoredConnectivityDataAvp guaAvp3 = rxAvpFactory.createSponsoredConnectivityData();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(guaAvp3, false);

    // Set all previous values
    guaAvp3.setExtensionAvps(guaAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(guaAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(guaAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", guaAvp2, guaAvp3);
  }

  @Test
  public void testAvpFactorySupportedFeatures() throws Exception {

    String avpName = "SupportedFeatures";

    // Create AVP with mandatory values
    SupportedFeaturesAvp mcdAvp1 = rxAvpFactory.createSupportedFeatures();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mcdAvp1);

    // Create AVP with default constructor
    SupportedFeaturesAvp mcdAvp2 = rxAvpFactory.createSupportedFeatures();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mcdAvp1, mcdAvp2);
    
    // Make new copy
    mcdAvp2 = rxAvpFactory.createSupportedFeatures();
    
    // And set all values using setters
    AvpAssistant.testSetters(mcdAvp2);
    
    // Create empty...
    SupportedFeaturesAvp mcdAvp3 = rxAvpFactory.createSupportedFeatures();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(mcdAvp3, false);

    // Set all previous values
    mcdAvp3.setExtensionAvps(mcdAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(mcdAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(mcdAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", mcdAvp2, mcdAvp3);
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
          getInstance().add(RealmEntry, getInstance().
              add(RealmName, realmName).
              add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L)).
              add(RealmHosts, clientHost + ", " + serverHost).
              add(RealmLocalAction, "LOCAL").
              add(RealmEntryIsDynamic, false).
              add(RealmEntryExpTime, 1000L)));
    }
  }

  @Test
  public void testMediaComponentDescription() {
    AARequest aar = rxMessageFactory.createAARequest();

    MediaComponentDescriptionAvp mcdAvp = rxAvpFactory.createMediaComponentDescription();
    
    MediaSubComponentAvp mscAvp = rxAvpFactory.createMediaSubComponent();
    mscAvp.setFlowUsage(FlowUsage.AF_SIGNALLING);

    IPFilterRule fd1Avp = new IPFilterRule("permit in ip from 192.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established");
    IPFilterRule fd2Avp = new IPFilterRule("permit out 2 from 192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss");
    IPFilterRule fd3Avp = new IPFilterRule("permit out 4 from 10.0.1.91 4060 to 10.0.0.190 43772 ");
    mscAvp.setFlowDescriptions(new IPFilterRule[]{fd1Avp, fd2Avp, fd3Avp});
    
    mcdAvp.setMediaSubComponent(mscAvp);
    
    aar.setMediaComponentDescription(mcdAvp);

    // from Richard Good
    if(aar.hasMediaComponentDescription()) {
      System.err.println("Has media component description"); 
      MediaComponentDescriptionAvp[] mcd = aar.getMediaComponentDescriptions();
      if(mcd[0].hasMediaSubComponent()) {
        System.err.println("Has media sub component"); 
        MediaSubComponentAvp[] msc = mcd[0].getMediaSubComponents();

        if (msc[0].hasFlowUsage()) {
          System.err.println("Has flow usage"); 
        }
        System.err.println("msc[0] flow usage: " + msc[0].getFlowUsage());
        System.err.println("msc[0] flow usage value: " + msc[0].getFlowUsage().getValue());
        
        // other from Richarg Good
        IPFilterRule[] ipf = msc[0].getFlowDescriptions();
        System.err.println("msc[0] flow descriptions " + ipf.toString());
        System.err.println("msc[0] flow description length " + ipf.length);
        for (int b = 0; b < ipf.length; b++) {
          System.err.println("msc[0] flow description " + ipf[b].toString());      // THIS DOES NOT PRINT!
        }
      }
    }
    
  }
  
  public ReAuthAnswer createReAuthAnswer( Answer answer ) {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthRequest createReAuthRequest( Request req ) {
    // TODO Auto-generated method stub
    return null;
  }

  public long[] getApplicationIds() {
    return new long[]{RxMessageFactory._Rx_AUTH_APP_ID};
  }
}
