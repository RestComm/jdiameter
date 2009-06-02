/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
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
import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.ReAuthRequest;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationRequest;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;

/**
 * 
 * <br>Project: mobicents-diameter-server
 * <br>3:37:56 PM Jun 1, 2009 
 * <br>
 *
 * BaseFactoriesTest.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class BaseFactoriesTest {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "21812";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;
  
  private static String serverHost = "localhost";
  private static String serverPort = "1812";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;
  
  private static String realmName = "mobicents.org";

  private static DiameterMessageFactoryImpl messageFactory;
  private static DiameterAvpFactoryImpl avpFactory;
  
  static
  {
    Stack stack = new org.jdiameter.client.impl.StackImpl();
    try
    {
      stack.init(new MyConfiguration());
      AvpDictionary.INSTANCE.parseDictionary( ShClientFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e )
    {
      throw new RuntimeException("");
    }
    
    messageFactory = new DiameterMessageFactoryImpl(stack);
    avpFactory = new DiameterAvpFactoryImpl();
  }
  
  @Test
  public void isRequestASR() throws Exception
  {
    AbortSessionRequest asr = messageFactory.createAbortSessionRequest();
    assertTrue("Request Flag in Abort-Session-Request is not set.", asr.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersASR() throws Exception
  {
    AbortSessionRequest asr = messageFactory.createAbortSessionRequest();
    
    int nFailures = AvpAssistant.testMethods(asr, AbortSessionRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerASA() throws Exception
  {
    AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer();
    assertFalse("Request Flag in Abort-Session-Answer is set.", asa.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersASA() throws Exception
  {
    AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer();
    
    int nFailures = AvpAssistant.testMethods(asa, AbortSessionAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostASA() throws Exception
  {
    AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmASA() throws Exception
  {
    AbortSessionAnswer asa = messageFactory.createAbortSessionAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", asa.getDestinationRealm());    
  }
  
  @Test
  public void isRequestACR() throws Exception
  {
    AccountingRequest acr = messageFactory.createAccountingRequest();
    assertTrue("Request Flag in Accounting-Request is not set.", acr.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersACR() throws Exception
  {
    AccountingRequest acr = messageFactory.createAccountingRequest();
    
    int nFailures = AvpAssistant.testMethods(acr, AccountingRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerACA() throws Exception
  {
    AccountingAnswer aca = messageFactory.createAccountingAnswer();
    assertFalse("Request Flag in Abort-Session-Answer is set.", aca.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersACA() throws Exception
  {
    AccountingAnswer aca = messageFactory.createAccountingAnswer();
    
    int nFailures = AvpAssistant.testMethods(aca, AccountingAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostACA() throws Exception
  {
    AccountingAnswer aca = messageFactory.createAccountingAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aca.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmACA() throws Exception
  {
    AccountingAnswer aca = messageFactory.createAccountingAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aca.getDestinationRealm());    
  }
  
  @Test
  public void isRequestCER() throws Exception
  {
    CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();
    assertTrue("Request Flag in Capabilities-Exchange-Request is not set.", cer.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersCER() throws Exception
  {
    CapabilitiesExchangeRequest cer = messageFactory.createCapabilitiesExchangeRequest();
    
    int nFailures = AvpAssistant.testMethods(cer, CapabilitiesExchangeRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerCEA() throws Exception
  {
    CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer();
    assertFalse("Request Flag in Capabilities-Exchange-Answer is set.", cea.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersCEA() throws Exception
  {
    CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer();
    
    int nFailures = AvpAssistant.testMethods(cea, CapabilitiesExchangeAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostCEA() throws Exception
  {
    CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cea.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmCEA() throws Exception
  {
    CapabilitiesExchangeAnswer cea = messageFactory.createCapabilitiesExchangeAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", cea.getDestinationRealm());    
  }

  @Test
  public void isRequestDWR() throws Exception
  {
    DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();
    assertTrue("Request Flag in Device-Watchdog-Request is not set.", dwr.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersDWR() throws Exception
  {
    DeviceWatchdogRequest dwr = messageFactory.createDeviceWatchdogRequest();
    
    int nFailures = AvpAssistant.testMethods(dwr, DeviceWatchdogRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerDWA() throws Exception
  {
    DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer();
    assertFalse("Request Flag in Device-Watchdog-Answer is set.", dwa.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersDWA() throws Exception
  {
    DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer();
    
    int nFailures = AvpAssistant.testMethods(dwa, DeviceWatchdogAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostDWA() throws Exception
  {
    DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dwa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmDWA() throws Exception
  {
    DeviceWatchdogAnswer dwa = messageFactory.createDeviceWatchdogAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dwa.getDestinationRealm());    
  }

  @Test
  public void isRequestDPR() throws Exception
  {
    DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();
    assertTrue("Request Flag in Disconnect-Peer-Request is not set.", dpr.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersDPR() throws Exception
  {
    DisconnectPeerRequest dpr = messageFactory.createDisconnectPeerRequest();
    
    int nFailures = AvpAssistant.testMethods(dpr, DisconnectPeerRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerDPA() throws Exception
  {
    DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer();
    assertFalse("Request Flag in Disconnect-Peer-Answer is set.", dpa.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersDPA() throws Exception
  {
    DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer();
    
    int nFailures = AvpAssistant.testMethods(dpa, DisconnectPeerAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostDPA() throws Exception
  {
    DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dpa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmDPA() throws Exception
  {
    DisconnectPeerAnswer dpa = messageFactory.createDisconnectPeerAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", dpa.getDestinationRealm());    
  }

  @Test
  public void isRequestRAR() throws Exception
  {
    ReAuthRequest rar = messageFactory.createReAuthRequest();
    assertTrue("Request Flag in Disconnect-Peer-Request is not set.", rar.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersRAR() throws Exception
  {
    ReAuthRequest rar = messageFactory.createReAuthRequest();
    
    int nFailures = AvpAssistant.testMethods(rar, ReAuthRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerRAA() throws Exception
  {
    ReAuthAnswer raa = messageFactory.createReAuthAnswer();
    assertFalse("Request Flag in Disconnect-Peer-Answer is set.", raa.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersRAA() throws Exception
  {
    ReAuthAnswer raa = messageFactory.createReAuthAnswer();
    
    int nFailures = AvpAssistant.testMethods(raa, ReAuthAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostRAA() throws Exception
  {
    ReAuthAnswer raa = messageFactory.createReAuthAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmRAA() throws Exception
  {
    ReAuthAnswer raa = messageFactory.createReAuthAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", raa.getDestinationRealm());    
  }
  
  @Test
  public void isRequestSTR() throws Exception
  {
    SessionTerminationRequest str = messageFactory.createSessionTerminationRequest();
    assertTrue("Request Flag in Disconnect-Peer-Request is not set.", str.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersSTR() throws Exception
  {
    SessionTerminationRequest str = messageFactory.createSessionTerminationRequest();
    
    int nFailures = AvpAssistant.testMethods(str, SessionTerminationRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void isAnswerSTA() throws Exception
  {
    SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer();
    assertFalse("Request Flag in Disconnect-Peer-Answer is set.", sta.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersSTA() throws Exception
  {
    SessionTerminationAnswer str = messageFactory.createSessionTerminationAnswer();
    
    int nFailures = AvpAssistant.testMethods(str, SessionTerminationAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasDestinationHostSTA() throws Exception
  {
    SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmSTA() throws Exception
  {
    SessionTerminationAnswer sta = messageFactory.createSessionTerminationAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sta.getDestinationRealm());    
  }
  
  @Test
  public void testAvpFactoryCreateExperimentalResult()
  {
    ExperimentalResultAvp erAvp1 = avpFactory.createExperimentalResult(10609L, 9999L);
    
    Assert.assertNotNull("Created Experimental-Result AVP from objects should not be null.", erAvp1);

    ExperimentalResultAvp erAvp2 = avpFactory.createExperimentalResult(erAvp1.getExtensionAvps());
    
    Assert.assertEquals("Created Experimental-Result AVP from extension avps should be equal to original.", erAvp1, erAvp2);
    
    ExperimentalResultAvp erAvp3 = avpFactory.createExperimentalResult(erAvp2.getVendorIdAVP(), erAvp2.getExperimentalResultCode());
    
    Assert.assertEquals("Created Experimental-Result AVP from getters should be equal to original.", erAvp1, erAvp3);    
  }
  
  @Test
  public void testAvpFactoryCreateProxyInfo()
  {
    ProxyInfoAvp piAvp1 = avpFactory.createProxyInfo( new DiameterIdentity("diameter.mobicents.org"), "INITIALIZED".getBytes() );
    
    Assert.assertNotNull("Created Proxy-Info AVP from objects should not be null.", piAvp1);

    ProxyInfoAvp piAvp2 = avpFactory.createProxyInfo(piAvp1.getExtensionAvps());
    
    Assert.assertEquals("Created Proxy-Info AVP from extension avps should be equal to original.", piAvp1, piAvp2);
    
    ProxyInfoAvp piAvp3 = avpFactory.createProxyInfo(piAvp2.getProxyHost(), piAvp2.getProxyState());
    
    Assert.assertEquals("Created Proxy-Info AVP from getters should be equal to original.", piAvp1, piAvp3);    
  }
  
  @Test
  public void testAvpFactoryCreateVendorSpecificApplicationId()
  {
    VendorSpecificApplicationIdAvp vsaidAvp1 = avpFactory.createVendorSpecificApplicationId(10609L);
    
    Assert.assertNotNull("Created Vendor-Specific-Application-Id AVP from objects should not be null.", vsaidAvp1);

    VendorSpecificApplicationIdAvp vsaidAvp2 = avpFactory.createVendorSpecificApplicationId(vsaidAvp1.getExtensionAvps());
    
    Assert.assertEquals("Created Vendor-Specific-Application-Id AVP from extension avps should be equal to original.", vsaidAvp1, vsaidAvp2);
    
    VendorSpecificApplicationIdAvp vsaidAvp3 = avpFactory.createVendorSpecificApplicationId(vsaidAvp2.getVendorIdsAvp()[0]);
    
    Assert.assertEquals("Created Vendor-Specific-Application-Id AVP from getters should be equal to original.", vsaidAvp1, vsaidAvp3);    
  }
  
  /**
   * Class representing the Diameter Configuration  
   */
  public static class MyConfiguration extends EmptyConfiguration 
  {
    public MyConfiguration() 
    {
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

}
