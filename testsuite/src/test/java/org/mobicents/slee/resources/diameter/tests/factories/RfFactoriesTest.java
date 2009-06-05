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
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;
import net.java.slee.resource.diameter.rf.RfMessageFactory;
import net.java.slee.resource.diameter.rf.RfServerSession;
import net.java.slee.resource.diameter.ro.RoAvpFactory;
import net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation;
import net.java.slee.resource.diameter.ro.events.avp.AddressDomain;
import net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation;
import net.java.slee.resource.diameter.ro.events.avp.EventType;
import net.java.slee.resource.diameter.ro.events.avp.ImsInformation;
import net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientId;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientName;
import net.java.slee.resource.diameter.ro.events.avp.LcsInformation;
import net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId;
import net.java.slee.resource.diameter.ro.events.avp.LocationType;
import net.java.slee.resource.diameter.ro.events.avp.MbmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MbmsServiceType;
import net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType;
import net.java.slee.resource.diameter.ro.events.avp.MessageBody;
import net.java.slee.resource.diameter.ro.events.avp.MessageClass;
import net.java.slee.resource.diameter.ro.events.avp.MmContentType;
import net.java.slee.resource.diameter.ro.events.avp.MmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality;
import net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress;
import net.java.slee.resource.diameter.ro.events.avp.PocInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsInformation;
import net.java.slee.resource.diameter.ro.events.avp.RecipientAddress;
import net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent;
import net.java.slee.resource.diameter.ro.events.avp.ServiceInformation;
import net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange;
import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;
import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.handlers.AccountingSessionFactory;
import org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener;
import org.mobicents.slee.resource.diameter.rf.RfMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.rf.RfServerSessionImpl;
import org.mobicents.slee.resource.diameter.ro.RoAvpFactoryImpl;

/**
 * 
 * RfFactoriesTest.java
 *
 * <br>Project:  mobicents
 * <br>2:10:37 PM Apr 14, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfFactoriesTest implements BaseSessionCreationListener {
  
  private static String clientHost = "127.0.0.1";
  private static String clientPort = "21812";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;
  
  private static String serverHost = "localhost";
  private static String serverPort = "1812";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;
  
  private static String realmName = "mobicents.org";

  private static RfMessageFactory rfMessageFactory;
  private static RoAvpFactory rfAvpFactory;
  
  private static Stack stack;
  
  private static ServerAccSession session; 

  
  static
  {
    stack = new org.jdiameter.client.impl.StackImpl();
    try
    {
      stack.init(new MyConfiguration());
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to initialize the stack.");
    }
    
    DiameterMessageFactoryImpl baseFactory = new DiameterMessageFactoryImpl(stack);
    DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();
    
    rfMessageFactory = new RfMessageFactoryImpl(baseFactory, stack);
    rfAvpFactory = new RoAvpFactoryImpl(baseAvpFactory);
    
    try
    {
      AvpDictionary.INSTANCE.parseDictionary( RfFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }
  
  private RfServerSession rfServerSession = null;
  private AccountingSessionFactory accSessionFactory;
  
  public RfFactoriesTest()
  {
    try
    {
      SessionFactory sf = stack.getSessionFactory();
      this.accSessionFactory = AccountingSessionFactory.INSTANCE;
      this.accSessionFactory.registerListener(this, 5000L, sf);
      
      ((ISessionFactory) sf).registerAppFacory(ServerAccSession.class, accSessionFactory);

      AccountingRequest acr = rfMessageFactory.createRfAccountingRequest( AccountingRecordType.EVENT_RECORD );
      
      acr.setAccountingRecordNumber( 5L );
      
      session = ((ISessionFactory) sf).getNewAppSession(null, null, ServerAccSession.class, ((DiameterMessageImpl)acr).getGenericData());
      rfServerSession = new RfServerSessionImpl((DiameterMessageFactoryImpl)rfMessageFactory.getBaseMessageFactory(), (DiameterAvpFactoryImpl)rfAvpFactory.getBaseFactory(), session, 5000L, null, null, null, stack);
      
      ((RfServerSessionImpl)rfServerSession).fetchSessionData( acr, true );
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void isRequestACR() throws Exception
  {
    AccountingRequest acr = rfMessageFactory.createRfAccountingRequest( AccountingRecordType.EVENT_RECORD );
    assertTrue("Request Flag in Accounting-Request is not set.", acr.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersACR() throws Exception
  {
    AccountingRequest acr = rfMessageFactory.createRfAccountingRequest( AccountingRecordType.EVENT_RECORD );
    
    int nFailures = AvpAssistant.testMethods(acr, AccountingRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
  @Test
  public void hasRfApplicationIdACR() throws Exception
  {
    AccountingRequest acr = rfMessageFactory.createRfAccountingRequest( AccountingRecordType.EVENT_RECORD );
    assertTrue("Acct-Application-Id AVP in Rf ACR must be 3, it is " + acr.getAcctApplicationId(), acr.getAcctApplicationId() == 3);
  }
  
  @Test
  public void isAnswerACA() throws Exception
  {
    AccountingAnswer aca = rfServerSession.createRfAccountingAnswer();
    assertFalse("Request Flag in Accounting-Answer is set.", aca.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersACA() throws Exception
  {
    AccountingAnswer aca = rfServerSession.createRfAccountingAnswer();
    
    int nFailures = AvpAssistant.testMethods(aca, AccountingAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  
  
 @Test
  public void hasRfApplicationIdACA() throws Exception
  {
    AccountingAnswer aca = rfServerSession.createRfAccountingAnswer();
    assertTrue("Acct-Application-Id AVP in Ro ACA must be 3, it is " + aca.getAcctApplicationId(), aca.getAcctApplicationId() == 3);
  }
  
  @Test
  public void hasDestinationHostACA() throws Exception
  {
    AccountingAnswer aca = rfServerSession.createRfAccountingAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aca.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmACA() throws Exception
  {
    AccountingAnswer aca = rfServerSession.createRfAccountingAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", aca.getDestinationRealm());    
  }

  // AVP Factory
  
  @Test
  public void testAvpFactoryCreateAdditionalContentInformation() throws Exception
  {
    String avpName = "Additional-Content-Information";
    
    // Create AVP with mandatory values
    AdditionalContentInformation aciAvp1 = rfAvpFactory.createAdditionalContentInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", aciAvp1);

    // Create AVP with default constructor
    AdditionalContentInformation aciAvp2 = rfAvpFactory.createAdditionalContentInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", aciAvp1, aciAvp2);
    
    // Make new copy
    aciAvp2 = rfAvpFactory.createAdditionalContentInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(aciAvp2);
    
    // Create empty...
    AdditionalContentInformation aciAvp3 = rfAvpFactory.createAdditionalContentInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(aciAvp3, false);

    // Set all previous values
    aciAvp3.setExtensionAvps(aciAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(aciAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(aciAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", aciAvp2, aciAvp3);
  }

  @Test
  public void testAvpFactoryCreateAddressDomain() throws Exception
  {
    String avpName = "Address-Domain";
    
    // Create AVP with mandatory values
    AddressDomain adAvp1 = rfAvpFactory.createAddressDomain();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", adAvp1);

    // Create AVP with default constructor
    AddressDomain adAvp2 = rfAvpFactory.createAddressDomain();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", adAvp1, adAvp2);
    
    // Make new copy
    adAvp2 = rfAvpFactory.createAddressDomain();
    
    // And set all values using setters
    AvpAssistant.testSetters(adAvp2);
    
    // Create empty...
    AddressDomain adAvp3 = rfAvpFactory.createAddressDomain();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(adAvp3, false);

    // Set all previous values
    adAvp3.setExtensionAvps(adAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(adAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(adAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", adAvp2, adAvp3);
  }

  @Test
  public void testAvpFactoryCreateApplicationServerInformation() throws Exception
  {
    String avpName = "Application-Server-Information";
    
    // Create AVP with mandatory values
    ApplicationServerInformation asiAvp1 = rfAvpFactory.createApplicationServerInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", asiAvp1);

    // Create AVP with default constructor
    ApplicationServerInformation asiAvp2 = rfAvpFactory.createApplicationServerInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", asiAvp1, asiAvp2);
    
    // Make new copy
    asiAvp2 = rfAvpFactory.createApplicationServerInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(asiAvp2);
    
    // Create empty...
    ApplicationServerInformation asiAvp3 = rfAvpFactory.createApplicationServerInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(asiAvp3, false);

    // Set all previous values
    asiAvp3.setExtensionAvps(asiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(asiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(asiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", asiAvp2, asiAvp3);
  }

  @Test
  public void testAvpFactoryCreateEventType() throws Exception
  {
    String avpName = "Event-Type";
    
    // Create AVP with mandatory values
    EventType etAvp1 = rfAvpFactory.createEventType();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", etAvp1);

    // Create AVP with default constructor
    EventType etAvp2 = rfAvpFactory.createEventType();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", etAvp1, etAvp2);
    
    // Make new copy
    etAvp2 = rfAvpFactory.createEventType();
    
    // And set all values using setters
    AvpAssistant.testSetters(etAvp2);
    
    // Create empty...
    EventType etAvp3 = rfAvpFactory.createEventType();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(etAvp3, false);

    // Set all previous values
    etAvp3.setExtensionAvps(etAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(etAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(etAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", etAvp2, etAvp3);
  }

  @Test
  public void testAvpFactoryCreateImsInformation() throws Exception
  {
    String avpName = "IMS-Information";
    
    // Create AVP with mandatory values
    ImsInformation iiAvp1 = rfAvpFactory.createImsInformation(NodeFunctionality.AS);
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", iiAvp1);

    // Create AVP with default constructor
    ImsInformation iiAvp2 = rfAvpFactory.createImsInformation();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Node-Functionality AVP.", iiAvp2.hasNodeFunctionality());

    // Set mandatory values
    iiAvp2.setNodeFunctionality(NodeFunctionality.AS);
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", iiAvp1, iiAvp2);
    
    // Make new copy
    iiAvp2 = rfAvpFactory.createImsInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(iiAvp2);
    
    // Create empty...
    ImsInformation iiAvp3 = rfAvpFactory.createImsInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(iiAvp3, false);

    // Set all previous values
    iiAvp3.setExtensionAvps(iiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(iiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(iiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", iiAvp2, iiAvp3);
  }

  @Test
  public void testAvpFactoryCreateInterOperatorIdentifier() throws Exception
  {
    String avpName = "Inter-Operator-Identifier";
    
    // Create AVP with mandatory values
    InterOperatorIdentifier ioiAvp1 = rfAvpFactory.createInterOperatorIdentifier();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", ioiAvp1);

    // Create AVP with default constructor
    InterOperatorIdentifier ioiAvp2 = rfAvpFactory.createInterOperatorIdentifier();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", ioiAvp1, ioiAvp2);
    
    // Make new copy
    ioiAvp2 = rfAvpFactory.createInterOperatorIdentifier();
    
    // And set all values using setters
    AvpAssistant.testSetters(ioiAvp2);
    
    // Create empty...
    InterOperatorIdentifier ioiAvp3 = rfAvpFactory.createInterOperatorIdentifier();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(ioiAvp3, false);

    // Set all previous values
    ioiAvp3.setExtensionAvps(ioiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(ioiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(ioiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", ioiAvp2, ioiAvp3);
  }

  @Test
  public void testAvpFactoryCreateLcsClientId() throws Exception
  {
    String avpName = "LCS-Client-Id";
    
    // Create AVP with mandatory values
    LcsClientId lcidAvp1 = rfAvpFactory.createLcsClientId();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", lcidAvp1);

    // Create AVP with default constructor
    LcsClientId lcidAvp2 = rfAvpFactory.createLcsClientId();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", lcidAvp1, lcidAvp2);
    
    // Make new copy
    lcidAvp2 = rfAvpFactory.createLcsClientId();
    
    // And set all values using setters
    AvpAssistant.testSetters(lcidAvp2);
    
    // Create empty...
    LcsClientId lcidAvp3 = rfAvpFactory.createLcsClientId();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(lcidAvp3, false);

    // Set all previous values
    lcidAvp3.setExtensionAvps(lcidAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(lcidAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(lcidAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", lcidAvp2, lcidAvp3);
  }

  @Test
  public void testAvpFactoryCreateLcsClientName() throws Exception
  {
    String avpName = "LCS-Client-Name";
    
    // Create AVP with mandatory values
    LcsClientName lcnAvp1 = rfAvpFactory.createLcsClientName();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", lcnAvp1);

    // Create AVP with default constructor
    LcsClientName lcnAvp2 = rfAvpFactory.createLcsClientName();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", lcnAvp1, lcnAvp2);
    
    // Make new copy
    lcnAvp2 = rfAvpFactory.createLcsClientName();
    
    // And set all values using setters
    AvpAssistant.testSetters(lcnAvp2);
    
    // Create empty...
    LcsClientName lcnAvp3 = rfAvpFactory.createLcsClientName();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(lcnAvp3, false);

    // Set all previous values
    lcnAvp3.setExtensionAvps(lcnAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(lcnAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(lcnAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", lcnAvp2, lcnAvp3);
  }

  @Test
  public void testAvpFactoryCreateLcsInformation() throws Exception
  {
    String avpName = "LCS-Information";
    
    // Create AVP with mandatory values
    LcsInformation liAvp1 = rfAvpFactory.createLcsInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", liAvp1);

    // Create AVP with default constructor
    LcsInformation liAvp2 = rfAvpFactory.createLcsInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", liAvp1, liAvp2);
    
    // Make new copy
    liAvp2 = rfAvpFactory.createLcsInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(liAvp2);
    
    // Create empty...
    LcsInformation liAvp3 = rfAvpFactory.createLcsInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(liAvp3, false);

    // Set all previous values
    liAvp3.setExtensionAvps(liAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(liAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(liAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", liAvp2, liAvp3);
  }

  @Test
  public void testAvpFactoryCreateLcsRequestorId() throws Exception
  {
    String avpName = "LCS-Requestor-Id";
    
    // Create AVP with mandatory values
    LcsRequestorId lriAvp1 = rfAvpFactory.createLcsRequestorId();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", lriAvp1);

    // Create AVP with default constructor
    LcsRequestorId lriAvp2 = rfAvpFactory.createLcsRequestorId();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", lriAvp1, lriAvp2);
    
    // Make new copy
    lriAvp2 = rfAvpFactory.createLcsRequestorId();
    
    // And set all values using setters
    AvpAssistant.testSetters(lriAvp2);
    
    // Create empty...
    LcsRequestorId lriAvp3 = rfAvpFactory.createLcsRequestorId();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(lriAvp3, false);

    // Set all previous values
    lriAvp3.setExtensionAvps(lriAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(lriAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(lriAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", lriAvp2, lriAvp3);
  }

  @Test
  public void testAvpFactoryCreateLocationType() throws Exception
  {
    String avpName = "Location-Type";
    
    // Create AVP with mandatory values
    LocationType ltAvp1 = rfAvpFactory.createLocationType();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", ltAvp1);

    // Create AVP with default constructor
    LocationType ltAvp2 = rfAvpFactory.createLocationType();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", ltAvp1, ltAvp2);
    
    // Make new copy
    ltAvp2 = rfAvpFactory.createLocationType();
    
    // And set all values using setters
    AvpAssistant.testSetters(ltAvp2);
    
    // Create empty...
    LocationType ltAvp3 = rfAvpFactory.createLocationType();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(ltAvp3, false);

    // Set all previous values
    ltAvp3.setExtensionAvps(ltAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(ltAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(ltAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", ltAvp2, ltAvp3);
  }

  @Test
  public void testAvpFactoryCreateMbmsInformation() throws Exception
  {
    String avpName = "MBMS-Information";
    
    // Create AVP with mandatory values
    MbmsInformation miAvp1 = rfAvpFactory.createMbmsInformation("abc", MbmsServiceType.MULTICAST, MbmsUserServiceType.DOWNLOAD);
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", miAvp1);

    // Create AVP with default constructor
    MbmsInformation miAvp2 = rfAvpFactory.createMbmsInformation();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have TMGI AVP.", miAvp2.hasTmgi());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have MBMS-Service-Type AVP.", miAvp2.hasMbmsServiceType());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have MBMS-User-Service-Type AVP.", miAvp2.hasMbmsUserServiceType());

    // Set mandatory values
    miAvp2.setTmgi("abc");
    miAvp2.setMbmsServiceType(MbmsServiceType.MULTICAST);
    miAvp2.setMbmsUserServiceType(MbmsUserServiceType.DOWNLOAD);
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", miAvp1, miAvp2);
    
    // Make new copy
    miAvp2 = rfAvpFactory.createMbmsInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(miAvp2);
    
    // Create empty...
    MbmsInformation miAvp3 = rfAvpFactory.createMbmsInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(miAvp3, false);

    // Set all previous values
    miAvp3.setExtensionAvps(miAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(miAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(miAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", miAvp2, miAvp3);
  }

  @Test
  public void testAvpFactoryCreateMessageBody() throws Exception
  {
    String avpName = "Message-Body";
    
    // Create AVP with mandatory values
    MessageBody mbAvp1 = rfAvpFactory.createMessageBody();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mbAvp1);

    // Create AVP with default constructor
    MessageBody mbAvp2 = rfAvpFactory.createMessageBody();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mbAvp1, mbAvp2);
    
    // Make new copy
    mbAvp2 = rfAvpFactory.createMessageBody();
    
    // And set all values using setters
    AvpAssistant.testSetters(mbAvp2);
    
    // Create empty...
    MessageBody mbAvp3 = rfAvpFactory.createMessageBody();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(mbAvp3, false);

    // Set all previous values
    mbAvp3.setExtensionAvps(mbAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(mbAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(mbAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", mbAvp2, mbAvp3);
  }

  @Test
  public void testAvpFactoryCreateMessageClass() throws Exception
  {
    String avpName = "Message-Class";
    
    // Create AVP with mandatory values
    MessageClass mbAvp1 = rfAvpFactory.createMessageClass();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mbAvp1);

    // Create AVP with default constructor
    MessageClass mbAvp2 = rfAvpFactory.createMessageClass();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mbAvp1, mbAvp2);
    
    // Make new copy
    mbAvp2 = rfAvpFactory.createMessageClass();
    
    // And set all values using setters
    AvpAssistant.testSetters(mbAvp2);
    
    // Create empty...
    MessageClass mbAvp3 = rfAvpFactory.createMessageClass();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(mbAvp3, false);

    // Set all previous values
    mbAvp3.setExtensionAvps(mbAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(mbAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(mbAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", mbAvp2, mbAvp3);
  }

  @Test
  public void testAvpFactoryCreateMmContentType() throws Exception
  {
    String avpName = "MM-Content-Type";
    
    // Create AVP with mandatory values
    MmContentType mmctAvp1 = rfAvpFactory.createMmContentType();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mmctAvp1);

    // Create AVP with default constructor
    MmContentType mmctAvp2 = rfAvpFactory.createMmContentType();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mmctAvp1, mmctAvp2);
    
    // Make new copy
    mmctAvp2 = rfAvpFactory.createMmContentType();
    
    // And set all values using setters
    AvpAssistant.testSetters(mmctAvp2);
    
    // Create empty...
    MmContentType mmctAvp3 = rfAvpFactory.createMmContentType();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(mmctAvp3, false);

    // Set all previous values
    mmctAvp3.setExtensionAvps(mmctAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(mmctAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(mmctAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", mmctAvp2, mmctAvp3);
  }

  @Test
  public void testAvpFactoryCreateMmsInformation() throws Exception
  {
    String avpName = "MMS-Information";
    
    // Create AVP with mandatory values
    MmsInformation mmsiAvp1 = rfAvpFactory.createMmsInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", mmsiAvp1);

    // Create AVP with default constructor
    MmsInformation mmsiAvp2 = rfAvpFactory.createMmsInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", mmsiAvp1, mmsiAvp2);
    
    // Make new copy
    mmsiAvp2 = rfAvpFactory.createMmsInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(mmsiAvp2);
    
    // Create empty...
    MmsInformation mmsiAvp3 = rfAvpFactory.createMmsInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(mmsiAvp3, false);

    // Set all previous values
    mmsiAvp3.setExtensionAvps(mmsiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(mmsiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(mmsiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", mmsiAvp2, mmsiAvp3);
  }

  @Test
  public void testAvpFactoryCreateOriginatorAddress() throws Exception
  {
    String avpName = "Originator-Address";
    
    // Create AVP with mandatory values
    OriginatorAddress oaAvp1 = rfAvpFactory.createOriginatorAddress();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", oaAvp1);

    // Create AVP with default constructor
    OriginatorAddress oaAvp2 = rfAvpFactory.createOriginatorAddress();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", oaAvp1, oaAvp2);
    
    // Make new copy
    oaAvp2 = rfAvpFactory.createOriginatorAddress();
    
    // And set all values using setters
    AvpAssistant.testSetters(oaAvp2);
    
    // Create empty...
    OriginatorAddress oaAvp3 = rfAvpFactory.createOriginatorAddress();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(oaAvp3, false);

    // Set all previous values
    oaAvp3.setExtensionAvps(oaAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(oaAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(oaAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", oaAvp2, oaAvp3);
  }

  @Test
  public void testAvpFactoryCreatePocInformation() throws Exception
  {
    String avpName = "PoC-Information";
    
    // Create AVP with mandatory values
    PocInformation piAvp1 = rfAvpFactory.createPocInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    PocInformation piAvp2 = rfAvpFactory.createPocInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createPocInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    PocInformation piAvp3 = rfAvpFactory.createPocInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreatePsFurnishChargingInformation() throws Exception
  {
    String avpName = "PS-Furnish-Charging-Information";
    
    // Create AVP with mandatory values
    PsFurnishChargingInformation piAvp1 = rfAvpFactory.createPsFurnishChargingInformation("123", "456");
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    PsFurnishChargingInformation piAvp2 = rfAvpFactory.createPsFurnishChargingInformation();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have 3GPP-Charging-Id AVP.", piAvp2.hasTgppChargingId());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have PS-Free-Format-Data AVP.", piAvp2.hasPsFreeFormatData());

    // Set mandatory values
    piAvp2.setTgppChargingId("123");
    piAvp2.setPsFreeFormatData("456");
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createPsFurnishChargingInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    PsFurnishChargingInformation piAvp3 = rfAvpFactory.createPsFurnishChargingInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreatePsInformation() throws Exception
  {
    String avpName = "PS-Information";
    
    // Create AVP with mandatory values
    PsInformation piAvp1 = rfAvpFactory.createPsInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    PsInformation piAvp2 = rfAvpFactory.createPsInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createPsInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    PsInformation piAvp3 = rfAvpFactory.createPsInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreateRecipientAddress() throws Exception
  {
    String avpName = "Recipient-Address";
    
    // Create AVP with mandatory values
    RecipientAddress piAvp1 = rfAvpFactory.createRecipientAddress();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    RecipientAddress piAvp2 = rfAvpFactory.createRecipientAddress();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createRecipientAddress();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    RecipientAddress piAvp3 = rfAvpFactory.createRecipientAddress();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreateSdpMediaComponent() throws Exception
  {
    String avpName = "SDP-Media-Component";
    
    // Create AVP with mandatory values
    SdpMediaComponent piAvp1 = rfAvpFactory.createSdpMediaComponent();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    SdpMediaComponent piAvp2 = rfAvpFactory.createSdpMediaComponent();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createSdpMediaComponent();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    SdpMediaComponent piAvp3 = rfAvpFactory.createSdpMediaComponent();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreateServiceInformation() throws Exception
  {
    String avpName = "SDP-Media-Component";
    
    // Create AVP with mandatory values
    ServiceInformation piAvp1 = rfAvpFactory.createServiceInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    ServiceInformation piAvp2 = rfAvpFactory.createServiceInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createServiceInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    ServiceInformation piAvp3 = rfAvpFactory.createServiceInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreateTalkBurstExchange() throws Exception
  {
    String avpName = "Talk-Burst-Exchange";
    
    // Create AVP with mandatory values
    TalkBurstExchange piAvp1 = rfAvpFactory.createTalkBurstExchange();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", piAvp1);

    // Create AVP with default constructor
    TalkBurstExchange piAvp2 = rfAvpFactory.createTalkBurstExchange();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", piAvp1, piAvp2);
    
    // Make new copy
    piAvp2 = rfAvpFactory.createTalkBurstExchange();
    
    // And set all values using setters
    AvpAssistant.testSetters(piAvp2);
    
    // Create empty...
    TalkBurstExchange piAvp3 = rfAvpFactory.createTalkBurstExchange();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(piAvp3, false);

    // Set all previous values
    piAvp3.setExtensionAvps(piAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(piAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(piAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", piAvp2, piAvp3);
  }

  @Test
  public void testAvpFactoryCreateTimeStamps() throws Exception
  {
    String avpName = "Time-Stamps";
    
    // Create AVP with mandatory values
    TimeStamps tsAvp1 = rfAvpFactory.createTimeStamps();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", tsAvp1);

    // Create AVP with default constructor
    TimeStamps tsAvp2 = rfAvpFactory.createTimeStamps();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", tsAvp1, tsAvp2);
    
    // Make new copy
    tsAvp2 = rfAvpFactory.createTimeStamps();
    
    // And set all values using setters
    AvpAssistant.testSetters(tsAvp2);
    
    // Create empty...
    TimeStamps tsAvp3 = rfAvpFactory.createTimeStamps();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(tsAvp3, false);

    // Set all previous values
    tsAvp3.setExtensionAvps(tsAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(tsAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(tsAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", tsAvp2, tsAvp3);
  }

  @Test
  public void testAvpFactoryCreateTrunkGroupId() throws Exception
  {
    String avpName = "Trunk-Group-Id";
    
    // Create AVP with mandatory values
    TrunkGroupId tsAvp1 = rfAvpFactory.createTrunkGroupId();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", tsAvp1);

    // Create AVP with default constructor
    TrunkGroupId tsAvp2 = rfAvpFactory.createTrunkGroupId();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", tsAvp1, tsAvp2);
    
    // Make new copy
    tsAvp2 = rfAvpFactory.createTrunkGroupId();
    
    // And set all values using setters
    AvpAssistant.testSetters(tsAvp2);
    
    // Create empty...
    TrunkGroupId tsAvp3 = rfAvpFactory.createTrunkGroupId();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(tsAvp3, false);

    // Set all previous values
    tsAvp3.setExtensionAvps(tsAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(tsAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(tsAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", tsAvp2, tsAvp3);
  }

  @Test
  public void testAvpFactoryCreateWlanInformation() throws Exception
  {
    String avpName = "WLAN-Information";
    
    // Create AVP with mandatory values
    WlanInformation tsAvp1 = rfAvpFactory.createWlanInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", tsAvp1);

    // Create AVP with default constructor
    WlanInformation tsAvp2 = rfAvpFactory.createWlanInformation();
    
    // Should not contain mandatory values

    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", tsAvp1, tsAvp2);
    
    // Make new copy
    tsAvp2 = rfAvpFactory.createWlanInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(tsAvp2);
    
    // Create empty...
    WlanInformation tsAvp3 = rfAvpFactory.createWlanInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(tsAvp3, false);

    // Set all previous values
    tsAvp3.setExtensionAvps(tsAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(tsAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(tsAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", tsAvp2, tsAvp3);
  }

  @Test
  public void testAvpFactoryCreateWlanRadioContainer() throws Exception
  {
    //1
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
      add(org.jdiameter.client.impl.helpers.Parameters.ApplicationId,
          // AppId 1
          getInstance().
          add(VendorId,   0L).
          add(AuthApplId, 0L).
          add(AcctApplId, 3L)
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

  public void fireEvent( String sessionId, String name, Request request, Answer answer )
  {
    // TODO Auto-generated method stub
    
  }

  public void sessionCreated( ServerAccSession session )
  {
    // TODO Auto-generated method stub
    
  }

  public void sessionCreated( ServerAuthSession session )
  {
    // TODO Auto-generated method stub
    
  }

  public void sessionCreated( ClientAuthSession session )
  {
    // TODO Auto-generated method stub
    
  }

  public void sessionCreated( ClientAccSession session )
  {
    // TODO Auto-generated method stub
    
  }

  public void sessionCreated( Session session )
  {
    // TODO Auto-generated method stub
    
  }

  public void sessionDestroyed( String sessionId, Object appSession )
  {
    // TODO Auto-generated method stub
    
  }

  public boolean sessionExists( String sessionId )
  {
    // TODO Auto-generated method stub
    return false;
  }

  public ApplicationId[] getSupportedApplications()
  {
    return new ApplicationId[]{ApplicationId.createByAccAppId(0L, 3L)};
  }

}