package org.mobicents.slee.resources.diameter.tests.factories;

import static org.junit.Assert.assertTrue;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;
import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.cxdx.events.avp.ReasonCode;
import net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo;

import org.jdiameter.api.Stack;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.cxdx.CxDxAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.cxdx.CxDxMessageFactoryImpl;
import org.mobicents.slee.resources.diameter.tests.factories.CCAFactoriesTest.MyConfiguration;

/**
 * 
 * CxDxFactoriesTest.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxFactoriesTest {

  private static CxDxMessageFactory cxdxMessageFactory;
  private static CxDxAVPFactory cxdxAvpFactory;

  static
  {
    Stack stack = new org.jdiameter.client.impl.StackImpl();
    try
    {
      stack.init(new MyConfiguration());
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    cxdxAvpFactory = new CxDxAVPFactoryImpl();
    try
    {
      cxdxMessageFactory = new CxDxMessageFactoryImpl(stack);
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }

    try
    {
      AvpDictionary.INSTANCE.parseDictionary( CxDxFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  @Test
  public void isRequestLIR() throws Exception
  {
    LocationInfoRequest lir = cxdxMessageFactory.createLocationInfoRequest();
    assertTrue("Request Flag in Location-Info-Request is not set.", lir.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersLIR() throws Exception
  {
    LocationInfoRequest lir = cxdxMessageFactory.createLocationInfoRequest();
    
    int nFailures = AvpAssistant.testMethods(lir, LocationInfoRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void isRequestMAR() throws Exception
  {
    MultimediaAuthenticationRequest mar = cxdxMessageFactory.createMultimediaAuthenticationRequest();
    assertTrue("Request Flag in Multimedia-Authentication-Request is not set.", mar.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersMAR() throws Exception
  {
    MultimediaAuthenticationRequest mar = cxdxMessageFactory.createMultimediaAuthenticationRequest();
    
    int nFailures = AvpAssistant.testMethods(mar, MultimediaAuthenticationRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void isRequestPPR() throws Exception
  {
    PushProfileRequest ppr = cxdxMessageFactory.createPushProfileRequest();
    assertTrue("Request Flag in Push-Profile-Request is not set.", ppr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersPPR() throws Exception
  {
    PushProfileRequest ppr = cxdxMessageFactory.createPushProfileRequest();
    
    int nFailures = AvpAssistant.testMethods(ppr, PushProfileRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void isRequestRTR() throws Exception
  {
    RegistrationTerminationRequest rtr = cxdxMessageFactory.createRegistrationTerminationRequest();
    assertTrue("Request Flag in Registration-Termination-Request is not set.", rtr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersRTR() throws Exception
  {
    RegistrationTerminationRequest rtr = cxdxMessageFactory.createRegistrationTerminationRequest();
    
    int nFailures = AvpAssistant.testMethods(rtr, RegistrationTerminationRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void isRequestSAR() throws Exception
  {
    ServerAssignmentRequest sar = cxdxMessageFactory.createServerAssignmentRequest();
    assertTrue("Request Flag in Server-Assignment-Request is not set.", sar.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersSAR() throws Exception
  {
    ServerAssignmentRequest sar = cxdxMessageFactory.createServerAssignmentRequest();
    
    int nFailures = AvpAssistant.testMethods(sar, ServerAssignmentRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void isRequestUAR() throws Exception
  {
    UserAuthorizationRequest uar = cxdxMessageFactory.createUserAuthorizationRequest();
    assertTrue("Request Flag in User-Authorization-Request is not set.", uar.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersUAR() throws Exception
  {
    UserAuthorizationRequest uar = cxdxMessageFactory.createUserAuthorizationRequest();
    
    int nFailures = AvpAssistant.testMethods(uar, UserAuthorizationRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }  

  @Test
  public void testAvpFactoryCreateAssociatedIdentities() throws Exception
  {
    String avpName = "Associated-Identities";
    
    // Create AVP with mandatory values
    AssociatedIdentities aiAvp1 = cxdxAvpFactory.createAssociatedIdentities();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", aiAvp1);

    // Create AVP with default constructor
    AssociatedIdentities aiAvp2 = cxdxAvpFactory.createAssociatedIdentities();
    
    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", aiAvp1, aiAvp2);
    
    // Make new copy
    aiAvp2 = cxdxAvpFactory.createAssociatedIdentities();
    
    // And set all values using setters
    AvpAssistant.testSetters(aiAvp2);
    
    // Create empty...
    AssociatedIdentities aiAvp3 = cxdxAvpFactory.createAssociatedIdentities();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(aiAvp3, false);

    // Set all previous values
    aiAvp3.setExtensionAvps(aiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(aiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(aiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", aiAvp2, aiAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateAssociatedRegisteredIdentities() throws Exception
  {
    String avpName = "Associated-Registered-Identities";
    
    // Create AVP with mandatory values
    AssociatedRegisteredIdentities ariAvp1 = cxdxAvpFactory.createAssociatedRegisteredIdentities();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", ariAvp1);

    // Create AVP with default constructor
    AssociatedRegisteredIdentities ariAvp2 = cxdxAvpFactory.createAssociatedRegisteredIdentities();
    
    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", ariAvp1, ariAvp2);
    
    // Make new copy
    ariAvp2 = cxdxAvpFactory.createAssociatedRegisteredIdentities();
    
    // And set all values using setters
    AvpAssistant.testSetters(ariAvp2);
    
    // Create empty...
    AssociatedRegisteredIdentities ariAvp3 = cxdxAvpFactory.createAssociatedRegisteredIdentities();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(ariAvp3, false);

    // Set all previous values
    ariAvp3.setExtensionAvps(ariAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(ariAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(ariAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", ariAvp2, ariAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateChargingInformation() throws Exception
  {
    String avpName = "Charging-Information";
    
    // Create AVP with mandatory values
    ChargingInformation ciAvp1 = cxdxAvpFactory.createChargingInformation();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", ciAvp1);

    // Create AVP with default constructor
    ChargingInformation ciAvp2 = cxdxAvpFactory.createChargingInformation();
    
    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", ciAvp1, ciAvp2);
    
    // Make new copy
    ciAvp2 = cxdxAvpFactory.createChargingInformation();
    
    // And set all values using setters
    AvpAssistant.testSetters(ciAvp2);
    
    // Create empty...
    ChargingInformation ciAvp3 = cxdxAvpFactory.createChargingInformation();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(ciAvp3, false);

    // Set all previous values
    ciAvp3.setExtensionAvps(ciAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(ciAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(ciAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", ciAvp2, ciAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateDeregistrationReason() throws Exception
  {
    String avpName = "Deregistration-Reason";
    
    // Create AVP with mandatory values
    DeregistrationReason drAvp1 = cxdxAvpFactory.createDeregistrationReason(ReasonCode.NEW_SERVER_ASSIGNED);
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", drAvp1);

    // Create AVP with default constructor
    DeregistrationReason drAvp2 = cxdxAvpFactory.createDeregistrationReason();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Reason-Code AVP.", drAvp2.hasReasonCode());

    // Set mandatory values
    drAvp2.setReasonCode(ReasonCode.NEW_SERVER_ASSIGNED);

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", drAvp1, drAvp2);
    
    // Make new copy
    drAvp2 = cxdxAvpFactory.createDeregistrationReason();
    
    // And set all values using setters
    AvpAssistant.testSetters(drAvp2);
    
    // Create empty...
    DeregistrationReason drAvp3 = cxdxAvpFactory.createDeregistrationReason();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(drAvp3, false);

    // Set all previous values
    drAvp3.setExtensionAvps(drAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(drAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(drAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", drAvp2, drAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateRestorationInfo() throws Exception
  {
    String avpName = "Restoration-Info";
    
    // Create AVP with mandatory values
    RestorationInfo riAvp1 = cxdxAvpFactory.createRestorationInfo("SAMPLE.PATH", "SAMPLE.CONTACT");
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", riAvp1);

    // Create AVP with default constructor
    RestorationInfo riAvp2 = cxdxAvpFactory.createRestorationInfo();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Path AVP.", riAvp2.hasPath());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Contact AVP.", riAvp2.hasContact());

    // Set mandatory values
    riAvp2.setPath("SAMPLE.PATH");
    riAvp2.setContact("SAMPLE.CONTACT");

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", riAvp1, riAvp2);
    
    // Make new copy
    riAvp2 = cxdxAvpFactory.createRestorationInfo();
    
    // And set all values using setters
    AvpAssistant.testSetters(riAvp2);
    
    // Create empty...
    RestorationInfo riAvp3 = cxdxAvpFactory.createRestorationInfo();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(riAvp3, false);

    // Set all previous values
    riAvp3.setExtensionAvps(riAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(riAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(riAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", riAvp2, riAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateSCSCFRestorationInfo() throws Exception
  {
    String avpName = "SCSCF-Restoration-Info";
    
    // Create AVP with mandatory values
    SCSCFRestorationInfo scscfriAvp1 = cxdxAvpFactory.createSCSCFRestorationInfo("SAMPLE.USERNAME", new RestorationInfo[]{cxdxAvpFactory.createRestorationInfo("SAMPLE.PATH", "SAMPLE.CONTACT")});
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", scscfriAvp1);

    // Create AVP with default constructor
    SCSCFRestorationInfo scscfriAvp2 = cxdxAvpFactory.createSCSCFRestorationInfo();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have User-Name AVP.", scscfriAvp2.hasUserName());
    Assert.assertTrue("Created " + avpName + " AVP from default constructor should not have Restoration-Info AVP.", scscfriAvp2.getRestorationInfos() == null ||  scscfriAvp2.getRestorationInfos().length == 0);

    // Set mandatory values
    scscfriAvp2.setUserName("SAMPLE.USERNAME");
    scscfriAvp2.setRestorationInfo(cxdxAvpFactory.createRestorationInfo("SAMPLE.PATH", "SAMPLE.CONTACT"));

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", scscfriAvp1, scscfriAvp2);
    
    // Make new copy
    scscfriAvp2 = cxdxAvpFactory.createSCSCFRestorationInfo();
    
    // And set all values using setters
    AvpAssistant.testSetters(scscfriAvp2);
    
    // Create empty...
    SCSCFRestorationInfo scscfriAvp3 = cxdxAvpFactory.createSCSCFRestorationInfo();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(scscfriAvp3, false);

    // Set all previous values
    scscfriAvp3.setExtensionAvps(scscfriAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(scscfriAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(scscfriAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", scscfriAvp2, scscfriAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateSIPAuthDataItem() throws Exception
  {
    String avpName = "SIP-Auth-Data-Item";
    
    // Create AVP with mandatory values
    SIPAuthDataItem sadiAvp1 = cxdxAvpFactory.createSIPAuthDataItem();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", sadiAvp1);

    // Create AVP with default constructor
    SIPAuthDataItem sadiAvp2 = cxdxAvpFactory.createSIPAuthDataItem();
    
    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", sadiAvp1, sadiAvp2);
    
    // Make new copy
    sadiAvp2 = cxdxAvpFactory.createSIPAuthDataItem();
    
    // And set all values using setters
    AvpAssistant.testSetters(sadiAvp2);
    
    // Create empty...
    SIPAuthDataItem sadiAvp3 = cxdxAvpFactory.createSIPAuthDataItem();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(sadiAvp3, false);

    // Set all previous values
    sadiAvp3.setExtensionAvps(sadiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(sadiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(sadiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", sadiAvp2, sadiAvp3);
  }
  
  @Test
  public void testAvpFactoryCreateSubscriptionInfo() throws Exception
  {
    String avpName = "Subscription-Info";
    
    // Create AVP with mandatory values
    SubscriptionInfo siAvp1 = cxdxAvpFactory.createSubscriptionInfo("cid", "fsh", "tsh", "rr", "c");
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", siAvp1);

    // Create AVP with default constructor
    SubscriptionInfo siAvp2 = cxdxAvpFactory.createSubscriptionInfo();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Call-ID-SIP-Header AVP.", siAvp2.hasCallIDSIPHeader());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have From-SIP-Header AVP.", siAvp2.hasFromSIPHeader());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have To-SIP-Header AVP.", siAvp2.hasToSIPHeader());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Record-Route AVP.", siAvp2.hasRecordRoute());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Contact AVP.", siAvp2.hasContact());

    // Set mandatory values
    siAvp2.setCallIDSIPHeader("cid");
    siAvp2.setFromSIPHeader("fsh");
    siAvp2.setToSIPHeader("tsh");
    siAvp2.setRecordRoute("rr");
    siAvp2.setContact("c");

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", siAvp1, siAvp2);
    
    // Make new copy
    siAvp2 = cxdxAvpFactory.createSubscriptionInfo();
    
    // And set all values using setters
    AvpAssistant.testSetters(siAvp2);
    
    // Create empty...
    SubscriptionInfo siAvp3 = cxdxAvpFactory.createSubscriptionInfo();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(siAvp3, false);

    // Set all previous values
    siAvp3.setExtensionAvps(siAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(siAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(siAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", siAvp2, siAvp3);
  }
  
}
