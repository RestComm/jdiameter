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
import static org.junit.Assert.*;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;
import net.java.slee.resource.diameter.gq.GqAvpFactory;
import net.java.slee.resource.diameter.gq.GqMessageFactory;
import net.java.slee.resource.diameter.gq.GqServerSessionActivity;
import net.java.slee.resource.diameter.gq.events.GqAAAnswer;
import net.java.slee.resource.diameter.gq.events.GqAARequest;
import net.java.slee.resource.diameter.gq.events.GqAbortSessionAnswer;
import net.java.slee.resource.diameter.gq.events.GqAbortSessionRequest;
import net.java.slee.resource.diameter.gq.events.GqReAuthAnswer;
import net.java.slee.resource.diameter.gq.events.GqReAuthRequest;
import net.java.slee.resource.diameter.gq.events.GqSessionTerminationAnswer;
import net.java.slee.resource.diameter.gq.events.GqSessionTerminationRequest;
import net.java.slee.resource.diameter.gq.events.avp.BindingInformation;
import net.java.slee.resource.diameter.gq.events.avp.BindingInputList;
import net.java.slee.resource.diameter.gq.events.avp.BindingOutputList;
import net.java.slee.resource.diameter.gq.events.avp.FlowGrouping;
import net.java.slee.resource.diameter.gq.events.avp.FlowStatus;
import net.java.slee.resource.diameter.gq.events.avp.FlowUsage;
import net.java.slee.resource.diameter.gq.events.avp.Flows;
import net.java.slee.resource.diameter.gq.events.avp.GloballyUniqueAddress;
import net.java.slee.resource.diameter.gq.events.avp.MediaComponentDescription;
import net.java.slee.resource.diameter.gq.events.avp.MediaSubComponent;
import net.java.slee.resource.diameter.gq.events.avp.ReservationPriority;
import net.java.slee.resource.diameter.gq.events.avp.V4TransportAddress;
import net.java.slee.resource.diameter.gq.events.avp.V6TransportAddress;

import org.jdiameter.api.Answer;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.Request;
import org.jdiameter.api.Stack;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.gq.GqServerSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.app.gq.GqSessionFactoryImpl;
import org.jdiameter.server.impl.app.auth.ServerAuthSessionDataLocalImpl;
import org.jdiameter.server.impl.app.gq.GqServerSessionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.gq.GqAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.gq.GqMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.gq.GqServerSessionActivityImpl;


/**
 * Diameter Gq SLEE Factories Tests
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GqFactoriesTest {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static GqMessageFactory gqMessageFactory;
  private static GqAvpFactory gqAvpFactory;

  private static Stack stack;

  private static GqServerSession session; 

  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    DiameterMessageFactoryImpl baseFactory = new DiameterMessageFactoryImpl(stack);
    DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

    gqAvpFactory = new GqAvpFactoryImpl(baseAvpFactory);
    try {
      gqMessageFactory = new GqMessageFactoryImpl(baseFactory, stack.getSessionFactory().getNewSession().getSessionId(), stack);
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    try {
      AvpDictionary.INSTANCE.parseDictionary( GqFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  private GqServerSessionActivity gqServerSession = null;

  public GqFactoriesTest() {
    try {
      GqSessionFactoryImpl gqSessionFactory = new GqSessionFactoryImpl(stack.getSessionFactory());
      session = new GqServerSessionImpl(new ServerAuthSessionDataLocalImpl(), (ISessionFactory) stack.getSessionFactory(), gqSessionFactory, gqSessionFactory, gqSessionFactory, gqSessionFactory, 30000L, true);
      gqServerSession = new GqServerSessionActivityImpl(gqMessageFactory.getBaseMessageFactory(), gqAvpFactory.getBaseFactory(), session, new DiameterIdentity("127.0.0.2"), new DiameterIdentity("mobicents.org"), stack);
      //((GqServerSessionActivityImpl)roServerSession).fetchCurrentState(roMessageFactory.createGqAARequest());
    }
    catch (IllegalDiameterStateException e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  // AA-Request

  @Test
  public void isRequestAAR() throws Exception {
    GqAARequest aar = gqMessageFactory.createGqAARequest();
    assertTrue("Request Flag in AA-Request is not set.", aar.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersAAR() throws Exception {
    GqAARequest aar = gqMessageFactory.createGqAARequest();

    int nFailures = AvpAssistant.testMethods(aar, GqAARequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGqApplicationIdAAR() throws Exception {
    GqAARequest aar = gqMessageFactory.createGqAARequest();
    assertTrue("Auth-Application-Id AVP in Gq AAR must be " + GqMessageFactory._GQ_AUTH_APP_ID + ", it is " + aar.getAuthApplicationId(), aar.getAuthApplicationId() == GqMessageFactory._GQ_AUTH_APP_ID);
  }

  // AA-Answer

  @Test
  public void isAnswerAAA() throws Exception {
    GqAAAnswer aaa = gqServerSession.createGqAAAnswer(gqMessageFactory.createGqAARequest());
    assertFalse("Request Flag in AA-Answer is set.", aaa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersAAA() throws Exception {
    GqAAAnswer aaa = gqServerSession.createGqAAAnswer(gqMessageFactory.createGqAARequest());

    int nFailures = AvpAssistant.testMethods(aaa, GqAAAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGqApplicationIdAAA() throws Exception {
    GqAAAnswer aaa = gqServerSession.createGqAAAnswer(gqMessageFactory.createGqAARequest());
    assertTrue("Auth-Application-Id AVP in Gq AAA must be " + GqMessageFactory._GQ_AUTH_APP_ID + ", it is " + aaa.getAuthApplicationId(), aaa.getAuthApplicationId() == GqMessageFactory._GQ_AUTH_APP_ID);
  }

  @Test
  public void hasDestinationHostAAA() throws Exception {
    GqAAAnswer aaa = gqServerSession.createGqAAAnswer(gqMessageFactory.createGqAARequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aaa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmAAA() throws Exception {
    GqAAAnswer aaa = gqServerSession.createGqAAAnswer(gqMessageFactory.createGqAARequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aaa.getDestinationRealm());    
  }

  // Abort-Session-Request

  @Test
  public void isRequestASR() throws Exception {
    GqAbortSessionRequest asr = gqMessageFactory.createGqAbortSessionRequest();
    assertTrue("Request Flag in Abort-Session-Request is not set.", asr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersASR() throws Exception {
    GqAbortSessionRequest asr = gqMessageFactory.createGqAbortSessionRequest();

    int nFailures = AvpAssistant.testMethods(asr, GqAbortSessionRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGqApplicationIdASR() throws Exception {
    GqAbortSessionRequest asr = gqMessageFactory.createGqAbortSessionRequest();
    assertTrue("Auth-Application-Id AVP in Gq ASR must be " + GqMessageFactory._GQ_AUTH_APP_ID + ", it is " + asr.getAuthApplicationId(), asr.getAuthApplicationId() == GqMessageFactory._GQ_AUTH_APP_ID);
  }

  // Abort-Session-Answer

  @Test
  public void isAnswerASA() throws Exception {
    GqAbortSessionAnswer asa = gqMessageFactory.createGqAbortSessionAnswer(gqMessageFactory.createGqAbortSessionRequest());
    assertFalse("Request Flag in Abort-Session-Answer is set.", asa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersASA() throws Exception {
    GqAbortSessionAnswer asa = gqMessageFactory.createGqAbortSessionAnswer(gqMessageFactory.createGqAbortSessionRequest());

    int nFailures = AvpAssistant.testMethods(asa, GqAbortSessionAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasDestinationHostASA() throws Exception {
    GqAbortSessionAnswer asa = gqMessageFactory.createGqAbortSessionAnswer(gqMessageFactory.createGqAbortSessionRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmASA() throws Exception {
    GqAbortSessionAnswer asa = gqMessageFactory.createGqAbortSessionAnswer(gqMessageFactory.createGqAbortSessionRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationRealm());    
  }

  // Re-Auth-Request

  @Test
  public void isRequestRAR() throws Exception {
    GqReAuthRequest rar = gqMessageFactory.createGqReAuthRequest();
    assertTrue("Request Flag in Re-Auth-Request is not set.", rar.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersRAR() throws Exception {
    GqReAuthRequest rar = gqMessageFactory.createGqReAuthRequest();

    int nFailures = AvpAssistant.testMethods(rar, GqReAuthRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGqApplicationIdRAR() throws Exception {
    GqReAuthRequest rar = gqMessageFactory.createGqReAuthRequest();
    assertTrue("Auth-Application-Id AVP in Gq RAR must be " + GqMessageFactory._GQ_AUTH_APP_ID + ", it is " + rar.getAuthApplicationId(), rar.getAuthApplicationId() == GqMessageFactory._GQ_AUTH_APP_ID);
  }

  // Re-Auth-Answer

  @Test
  public void isAnswerRAA() throws Exception {
    GqReAuthAnswer raa = gqMessageFactory.createGqReAuthAnswer(gqMessageFactory.createGqReAuthRequest());
    assertFalse("Request Flag in Re-Auth-Answer is set.", raa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersRAA() throws Exception {
    GqReAuthAnswer raa = gqMessageFactory.createGqReAuthAnswer(gqMessageFactory.createGqReAuthRequest());

    int nFailures = AvpAssistant.testMethods(raa, GqReAuthAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasDestinationHostRAA() throws Exception {
    GqReAuthAnswer raa = gqMessageFactory.createGqReAuthAnswer(gqMessageFactory.createGqReAuthRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmRAA() throws Exception {
    GqReAuthAnswer raa = gqMessageFactory.createGqReAuthAnswer(gqMessageFactory.createGqReAuthRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationRealm());    
  }

  // Session-Termination-Request

  @Test
  public void isRequestSTR() throws Exception {
    GqSessionTerminationRequest str = gqMessageFactory.createGqSessionTerminationRequest();
    assertTrue("Request Flag in Session-Termination-Request is not set.", str.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersSTR() throws Exception {
    GqSessionTerminationRequest str = gqMessageFactory.createGqSessionTerminationRequest();

    int nFailures = AvpAssistant.testMethods(str, GqSessionTerminationRequest.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasGqApplicationIdSTR() throws Exception {
    GqSessionTerminationRequest str = gqMessageFactory.createGqSessionTerminationRequest();
    assertTrue("Auth-Application-Id AVP in Gq STR must be " + GqMessageFactory._GQ_AUTH_APP_ID + ", it is " + str.getAuthApplicationId(), str.getAuthApplicationId() == GqMessageFactory._GQ_AUTH_APP_ID);
  }

  // Re-Auth-Answer

  @Test
  public void isAnswerSTA() throws Exception {
    GqSessionTerminationAnswer sta = gqMessageFactory.createGqSessionTerminationAnswer(gqMessageFactory.createGqSessionTerminationRequest());
    assertFalse("Request Flag in Re-Auth-Answer is set.", sta.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersSTA() throws Exception {
    GqSessionTerminationAnswer sta = gqMessageFactory.createGqSessionTerminationAnswer(gqMessageFactory.createGqSessionTerminationRequest());

    int nFailures = AvpAssistant.testMethods(sta, GqSessionTerminationAnswer.class);

    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void hasDestinationHostSTA() throws Exception {
    GqSessionTerminationAnswer sta = gqMessageFactory.createGqSessionTerminationAnswer(gqMessageFactory.createGqSessionTerminationRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmSTA() throws Exception {
    GqSessionTerminationAnswer sta = gqMessageFactory.createGqSessionTerminationAnswer(gqMessageFactory.createGqSessionTerminationRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationRealm());    
  }

  // Gq AVP Factory Tests 

  private static BindingInformation BI_AVP_DEFAULT = gqAvpFactory.createBindingInformation();
  private static BindingInputList BIL_AVP_DEFAULT = gqAvpFactory.createBindingInputList();
  private static BindingOutputList BOL_AVP_DEFAULT = gqAvpFactory.createBindingOutputList();
  private static FlowGrouping FG_AVP_DEFAULT = gqAvpFactory.createFlowGrouping();
  private static Flows F_AVP_DEFAULT = gqAvpFactory.createFlows();
  private static GloballyUniqueAddress GUA_AVP_DEFAULT = gqAvpFactory.createGloballyUniqueAddress();
  private static MediaComponentDescription MCD_AVP_DEFAULT = gqAvpFactory.createMediaComponentDescription();
  private static MediaSubComponent MSC_AVP_DEFAULT = gqAvpFactory.createMediaSubComponent();
  private static V4TransportAddress V4TA_AVP_DEFAULT = gqAvpFactory.createV4TransportAddress();
  private static V6TransportAddress V6TA_AVP_DEFAULT = gqAvpFactory.createV6TransportAddress();

  static {
    V4TA_AVP_DEFAULT.setFramedIPAddress("255.255.255.254");
    V4TA_AVP_DEFAULT.setPortNumber(13579);

    V6TA_AVP_DEFAULT.setFramedIPV6Prefix("A123:B456:C789:DE80::/57");
    V6TA_AVP_DEFAULT.setPortNumber(24680);

    BIL_AVP_DEFAULT.setV4TransportAddress(V4TA_AVP_DEFAULT);
    BIL_AVP_DEFAULT.setV6TransportAddress(V6TA_AVP_DEFAULT);

    BI_AVP_DEFAULT.setBindingInputList(BIL_AVP_DEFAULT);
    BI_AVP_DEFAULT.setBindingOutputList(BOL_AVP_DEFAULT);

    F_AVP_DEFAULT.setFlowNumber(1);
    F_AVP_DEFAULT.setMediaComponentNumber(2);

    FG_AVP_DEFAULT.setFlow(F_AVP_DEFAULT);

    GUA_AVP_DEFAULT.setAddressRealm("mobicents.org");
    GUA_AVP_DEFAULT.setFramedIPAddress("255.255.255.254");
    GUA_AVP_DEFAULT.setFramedIPV6Prefix("A123:B456:C789:DE80::/57");

    MSC_AVP_DEFAULT.setFlowDescription(new IPFilterRule("deny in ip from 1.2.3.4/24 to any"));
    MSC_AVP_DEFAULT.setFlowNumber(7);
    MSC_AVP_DEFAULT.setFlowStatus(FlowStatus.ENABLED);
    MSC_AVP_DEFAULT.setFlowUsage(FlowUsage.RTCP);
    MSC_AVP_DEFAULT.setMaxRequestedBandwidthDL(555);
    MSC_AVP_DEFAULT.setMaxRequestedBandwidthUL(222);

    MCD_AVP_DEFAULT.setAFApplicationIdentifier("AFApplicationIdentifier");
    MCD_AVP_DEFAULT.setCodecData("codecData");
    MCD_AVP_DEFAULT.setFlowStatus(FlowStatus.DISABLED);
    MCD_AVP_DEFAULT.setMaxRequestedBandwidthDL(999);
    MCD_AVP_DEFAULT.setMaxRequestedBandwidthUL(111);
    MCD_AVP_DEFAULT.setMediaAuthorizationContextId("mediaAuthorizationContextId");
    MCD_AVP_DEFAULT.setMediaComponentNumber(1);
    MCD_AVP_DEFAULT.setMediaSubComponent(MSC_AVP_DEFAULT);
    MCD_AVP_DEFAULT.setReservationClass(4);
    MCD_AVP_DEFAULT.setReservationPriority(ReservationPriority.PRIORITYSEVEN);
    MCD_AVP_DEFAULT.setRRBandwidth(65);
    MCD_AVP_DEFAULT.setRSBandwidth(56);
    MCD_AVP_DEFAULT.setTransportClass(76576);
  }

  @Test
  public void testAvpFactoryCreateBindingInformation() throws Exception {

    String avpName = "Binding-Information";

    // Create AVP with mandatory values
    BindingInformation biAvp1 = gqAvpFactory.createBindingInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", biAvp1);

    // Create AVP with default constructor
    BindingInformation biAvp2 = gqAvpFactory.createBindingInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", biAvp1, biAvp2);
    
    // Make new copy
    biAvp2 = gqAvpFactory.createBindingInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(biAvp2);
    
    // Create empty...
    BindingInformation biAvp3 = gqAvpFactory.createBindingInformation();
    
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
  public void testAvpFactoryCreateBindingInputList() throws Exception {

    String avpName = "Binding-Input-List";

    // Create AVP with mandatory values
    BindingInputList bilAvp1 = gqAvpFactory.createBindingInputList();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", bilAvp1);

    // Create AVP with default constructor
    BindingInputList bilAvp2 = gqAvpFactory.createBindingInputList();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", bilAvp1, bilAvp2);
    
    // Make new copy
    bilAvp2 = gqAvpFactory.createBindingInputList();
    
    // And set all values using setters
    AvpAssistant.testSetters(bilAvp2);
    
    // Create empty...
    BindingInputList bilAvp3 = gqAvpFactory.createBindingInputList();
    
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
  public void testAvpFactoryCreateBindingOutputList() throws Exception {

    String avpName = "Binding-Output-List";

    // Create AVP with mandatory values
    BindingOutputList bolAvp1 = gqAvpFactory.createBindingOutputList();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", bolAvp1);

    // Create AVP with default constructor
    BindingOutputList bolAvp2 = gqAvpFactory.createBindingOutputList();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", bolAvp1, bolAvp2);
    
    // Make new copy
    bolAvp2 = gqAvpFactory.createBindingOutputList();
    
    // And set all values using setters
    AvpAssistant.testSetters(bolAvp2);
    
    // Create empty...
    BindingOutputList bolAvp3 = gqAvpFactory.createBindingOutputList();
    
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
  public void testAvpFactoryCreateFlowGrouping() throws Exception {

    String avpName = "Flow-Grouping";

    // Create AVP with mandatory values
    FlowGrouping fgAvp1 = gqAvpFactory.createFlowGrouping();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", fgAvp1);

    // Create AVP with default constructor
    FlowGrouping fgAvp2 = gqAvpFactory.createFlowGrouping();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", fgAvp1, fgAvp2);
    
    // Make new copy
    fgAvp2 = gqAvpFactory.createFlowGrouping();
    
    // And set all values using setters
    AvpAssistant.testSetters(fgAvp2);
    
    // Create empty...
    FlowGrouping fgAvp3 = gqAvpFactory.createFlowGrouping();
    
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

  @Test
  public void testAvpFactoryCreateFlows() throws Exception {

    String avpName = "Flows";

    // Create AVP with mandatory values
    Flows fAvp1 = gqAvpFactory.createFlows();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", fAvp1);

    // Create AVP with default constructor
    Flows fAvp2 = gqAvpFactory.createFlows();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", fAvp1, fAvp2);
    
    // Make new copy
    fAvp2 = gqAvpFactory.createFlows();
    
    // And set all values using setters
    AvpAssistant.testSetters(fAvp2);
    
    // Create empty...
    Flows fAvp3 = gqAvpFactory.createFlows();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(fAvp3, false);

    // Set all previous values
    fAvp3.setExtensionAvps(fAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(fAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(fAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", fAvp2, fAvp3);
  }

  @Test
  public void testAvpFactoryCreateGloballyUniqueAddress() throws Exception {

    String avpName = "Globally-Unique-Address";

    // Create AVP with mandatory values
    GloballyUniqueAddress guaAvp1 = gqAvpFactory.createGloballyUniqueAddress();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", guaAvp1);

    // Create AVP with default constructor
    GloballyUniqueAddress guaAvp2 = gqAvpFactory.createGloballyUniqueAddress();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", guaAvp1, guaAvp2);
    
    // Make new copy
    guaAvp2 = gqAvpFactory.createGloballyUniqueAddress();
    
    // And set all values using setters
    AvpAssistant.testSetters(guaAvp2);
    
    // Create empty...
    GloballyUniqueAddress guaAvp3 = gqAvpFactory.createGloballyUniqueAddress();
    
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
  public void testAvpFactoryCreateMediaComponentDescription() throws Exception {

    String avpName = "Media-Component-Description";

    // Create AVP with mandatory values
    MediaComponentDescription mcdAvp1 = gqAvpFactory.createMediaComponentDescription();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mcdAvp1);

    // Create AVP with default constructor
    MediaComponentDescription mcdAvp2 = gqAvpFactory.createMediaComponentDescription();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mcdAvp1, mcdAvp2);
    
    // Make new copy
    mcdAvp2 = gqAvpFactory.createMediaComponentDescription();
    
    // And set all values using setters
    AvpAssistant.testSetters(mcdAvp2);
    
    // Create empty...
    MediaComponentDescription mcdAvp3 = gqAvpFactory.createMediaComponentDescription();
    
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

  @Test
  public void testAvpFactoryCreateMediaSubComponent() throws Exception {

    String avpName = "Media-Sub-Component";

    // Create AVP with mandatory values
    MediaSubComponent mscAvp1 = gqAvpFactory.createMediaSubComponent();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mscAvp1);

    // Create AVP with default constructor
    MediaSubComponent mscAvp2 = gqAvpFactory.createMediaSubComponent();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mscAvp1, mscAvp2);
    
    // Make new copy
    mscAvp2 = gqAvpFactory.createMediaSubComponent();
    
    // And set all values using setters
    AvpAssistant.testSetters(mscAvp2);
    
    // Create empty...
    MediaSubComponent mscAvp3 = gqAvpFactory.createMediaSubComponent();
    
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
  public void testAvpFactoryCreateV4TransportAddress() throws Exception {

    String avpName = "V4-Transport-Address";

    // Create AVP with mandatory values
    V4TransportAddress v4taAvp1 = gqAvpFactory.createV4TransportAddress();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", v4taAvp1);

    // Create AVP with default constructor
    V4TransportAddress v4taAvp2 = gqAvpFactory.createV4TransportAddress();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", v4taAvp1, v4taAvp2);
    
    // Make new copy
    v4taAvp2 = gqAvpFactory.createV4TransportAddress();
    
    // And set all values using setters
    AvpAssistant.testSetters(v4taAvp2);
    
    // Create empty...
    V4TransportAddress v4taAvp3 = gqAvpFactory.createV4TransportAddress();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(v4taAvp3, false);

    // Set all previous values
    v4taAvp3.setExtensionAvps(v4taAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(v4taAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(v4taAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", v4taAvp2, v4taAvp3);
  }

  @Test
  public void testAvpFactoryCreateV6TransportAddress() throws Exception {

    String avpName = "V6-Transport-Address";

    // Create AVP with mandatory values
    V6TransportAddress v6taAvp1 = gqAvpFactory.createV6TransportAddress();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", v6taAvp1);

    // Create AVP with default constructor
    V6TransportAddress v6taAvp2 = gqAvpFactory.createV6TransportAddress();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", v6taAvp1, v6taAvp2);
    
    // Make new copy
    v6taAvp2 = gqAvpFactory.createV6TransportAddress();
    
    // And set all values using setters
    AvpAssistant.testSetters(v6taAvp2);
    
    // Create empty...
    V6TransportAddress v6taAvp3 = gqAvpFactory.createV6TransportAddress();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(v6taAvp3, false);

    // Set all previous values
    v6taAvp3.setExtensionAvps(v6taAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(v6taAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(v6taAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", v6taAvp2, v6taAvp3);
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

  public ReAuthAnswer createReAuthAnswer( Answer answer ) {
    // TODO Auto-generated method stub
    return null;
  }

  public ReAuthRequest createReAuthRequest( Request req ) {
    // TODO Auto-generated method stub
    return null;
  }

  public long[] getApplicationIds() {
    return new long[]{GqMessageFactory._GQ_AUTH_APP_ID};
  }

}
