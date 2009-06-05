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
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.ShClientMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerMessageFactoryImpl;

/**
 * 
 * ShClientFactoriesTest.java
 *
 * <br>Project:  mobicents
 * <br>6:39:33 PM Feb 27, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ShClientFactoriesTest {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "21812";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "1812";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static ShClientMessageFactoryImpl shClientFactory;
  private static ShServerMessageFactoryImpl shServerFactory;
  private static DiameterShAvpFactoryImpl shAvpFactory;
  private static Stack stack;

  static
  {
    stack = new org.jdiameter.client.impl.StackImpl();
    try
    {
      stack.init(new MyConfiguration());
      AvpDictionary.INSTANCE.parseDictionary( ShClientFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    DiameterMessageFactoryImpl baseMessageFactory = new DiameterMessageFactoryImpl(stack);
    
    shClientFactory = new ShClientMessageFactoryImpl(stack);
    shServerFactory = new ShServerMessageFactoryImpl(baseMessageFactory, null, stack, shAvpFactory);
    shAvpFactory = new DiameterShAvpFactoryImpl(new DiameterAvpFactoryImpl());
  }

  @Test
  public void isRequestPUR() throws Exception
  {
    ProfileUpdateRequest pur = shClientFactory.createProfileUpdateRequest();
    assertTrue("Request Flag in Profile-Update-Request is not set.", pur.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersPUR() throws Exception
  {
    ProfileUpdateRequest pur = shClientFactory.createProfileUpdateRequest();
    
    int nFailures = AvpAssistant.testMethods(pur, ProfileUpdateRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isAnswerPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer(shServerFactory.createPushNotificationRequest());
    assertFalse("Request Flag in Push-Notification-Answer is set.", pna.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer(shServerFactory.createPushNotificationRequest());
    
    int nFailures = AvpAssistant.testMethods(pna, PushNotificationAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void hasDestinationHostPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer(shServerFactory.createPushNotificationRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pna.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer(shServerFactory.createPushNotificationRequest());
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pna.getDestinationRealm());    
  }

  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested)
   * http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetPNA() throws Exception
  {
    long originalValue = 5001;

    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer(shServerFactory.createPushNotificationRequest(), originalValue, true );

    long obtainedValue = pna.getExperimentalResult().getExperimentalResultCode();

    assertTrue("Experimental-Result-Code in PNA should be " + originalValue +" and is " + obtainedValue + ".", originalValue == obtainedValue);
  }

  @Test
  public void isRequestSNR() throws Exception
  {
    SubscribeNotificationsRequest snr = shClientFactory.createSubscribeNotificationsRequest();
    assertTrue("Request Flag in Subscribe-Notifications-Request is not set.", snr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersSNR() throws Exception
  {
    SubscribeNotificationsRequest snr = shClientFactory.createSubscribeNotificationsRequest();
    
    int nFailures = AvpAssistant.testMethods(snr, SubscribeNotificationsRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isRequestUDR() throws Exception
  {
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    assertTrue("Request Flag in User-Data-Request is not set.", udr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersUDR() throws Exception
  {
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    
    int nFailures = AvpAssistant.testMethods(udr, UserDataRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }

  @Test
  public void isUDRPublicIdentityAccessibleTwice() throws Exception
  {
    String originalValue = "sip:alexandre@diameter.mobicents.org";

    UserIdentityAvpImpl uiAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, 10415L, 1, 0, new byte[]{});
    uiAvp.setPublicIdentity( originalValue );

    UserDataRequest udr = shClientFactory.createUserDataRequest( uiAvp, DataReferenceType.IMS_PUBLIC_IDENTITY );

    String obtainedValue1 = udr.getUserIdentity().getPublicIdentity();
    String obtainedValue2 = udr.getUserIdentity().getPublicIdentity();

    assertTrue("Obtained value for Public-Identity AVP differs from original.", obtainedValue1.equals( originalValue ));
    assertTrue("Obtained #1 value for Public-Identity AVP differs from Obtained #2.", obtainedValue1.equals( obtainedValue2 ));
  }

  @Test
  public void isPURPublicIdentityAccessibleTwice() throws Exception
  {
    String originalValue = "sip:alexandre@diameter.mobicents.org";

    UserIdentityAvpImpl uiAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, 10415L, 1, 0, new byte[]{});
    uiAvp.setPublicIdentity( originalValue );

    ProfileUpdateRequest udr = shClientFactory.createProfileUpdateRequest( uiAvp, DataReferenceType.IMS_PUBLIC_IDENTITY, new byte[1] );

    String obtainedValue1 = udr.getUserIdentity().getPublicIdentity();
    String obtainedValue2 = udr.getUserIdentity().getPublicIdentity();

    assertTrue("Obtained value for Public-Identity AVP differs from original.", obtainedValue1.equals( originalValue ));
    assertTrue("Obtained #1 value for Public-Identity AVP differs from Obtained #2.", obtainedValue1.equals( obtainedValue2 ));
  }

  @Test
  public void isSNRPublicIdentityAccessibleTwice() throws Exception
  {
    String originalValue = "sip:alexandre@diameter.mobicents.org";

    UserIdentityAvpImpl uiAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, 10415L, 1, 0, new byte[]{});
    uiAvp.setPublicIdentity( originalValue );

    SubscribeNotificationsRequest udr = shClientFactory.createSubscribeNotificationsRequest( uiAvp, DataReferenceType.IMS_PUBLIC_IDENTITY,SubsReqType.SUBSCRIBE );

    String obtainedValue1 = udr.getUserIdentity().getPublicIdentity();
    String obtainedValue2 = udr.getUserIdentity().getPublicIdentity();

    assertTrue("Obtained value for Public-Identity AVP differs from original.", obtainedValue1.equals( originalValue ));
    assertTrue("Obtained #1 value for Public-Identity AVP differs from Obtained #2.", obtainedValue1.equals( obtainedValue2 ));
  }

  // AVP Factory Testing
  
  @Test
  public void testAvpFactoryCreateSupportedApplications() throws Exception
  {
    String avpName = "Supported-Applications";
    
    // Create AVP with mandatory values
    SupportedApplicationsAvp saAvp1 = shAvpFactory.createSupportedApplications( 123L, 456L, shAvpFactory.getBaseFactory().createVendorSpecificApplicationId(999L));
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", saAvp1);

    // Create AVP with default constructor
    SupportedApplicationsAvp saAvp2 = shAvpFactory.createSupportedApplications();
    
    // Should not contain mandatory values

    // Set mandatory values
    saAvp2.setAuthApplicationId(123L);
    saAvp2.setAcctApplicationId(456L);
    saAvp2.setVendorSpecificApplicationId(shAvpFactory.getBaseFactory().createVendorSpecificApplicationId(999L));
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", saAvp1, saAvp2);
    
    // Make new copy
    saAvp2 = shAvpFactory.createSupportedApplications();
    
    // And set all values using setters
    AvpAssistant.testSetters(saAvp2);
    
    // Create empty...
    SupportedApplicationsAvp saAvp3 = shAvpFactory.createSupportedApplications();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(saAvp3, false);

    // Set all previous values
    saAvp3.setExtensionAvps(saAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(saAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(saAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", saAvp2, saAvp3);
  }

  @Test
  public void testAvpFactoryCreateSupportedFeatures() throws Exception
  {
    String avpName = "Supported-Features";
    
    // Create AVP with mandatory values
    SupportedFeaturesAvp sfAvp1 = shAvpFactory.createSupportedFeatures(123L, 456L, 789L);
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", sfAvp1);

    // Create AVP with default constructor
    SupportedFeaturesAvp sfAvp2 = shAvpFactory.createSupportedFeatures();
    
    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Vendor-Id AVP.", sfAvp2.hasVendorId());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Feature-List-Id AVP.", sfAvp2.hasFeatureListId());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Feature-List AVP.", sfAvp2.hasFeatureList());
    
    // Set mandatory values
    sfAvp2.setVendorId(123L);
    sfAvp2.setFeatureListId(456L);
    sfAvp2.setFeatureList(789L);
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", sfAvp1, sfAvp2);
    
    // Make new copy
    sfAvp2 = shAvpFactory.createSupportedFeatures();
    
    // And set all values using setters
    AvpAssistant.testSetters(sfAvp2);
    
    // Create empty...
    SupportedFeaturesAvp sfAvp3 = shAvpFactory.createSupportedFeatures();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(sfAvp3, false);

    // Set all previous values
    sfAvp3.setExtensionAvps(sfAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(sfAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(sfAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", sfAvp2, sfAvp3);
  }

  @Test
  public void testAvpFactoryCreateUserIdentity() throws Exception
  {
    String avpName = "User-Identity";
    
    // Create AVP with mandatory values
    UserIdentityAvp uiAvp1 = shAvpFactory.createUserIdentity();
    
    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", uiAvp1);

    // Create AVP with default constructor
    UserIdentityAvp uiAvp2 = shAvpFactory.createUserIdentity();
    
    // Should not contain mandatory values
    
    // Set mandatory values
    
    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", uiAvp1, uiAvp2);
    
    // Make new copy
    uiAvp2 = shAvpFactory.createUserIdentity();
    
    // And set all values using setters
    AvpAssistant.testSetters(uiAvp2);
    
    // Create empty...
    UserIdentityAvp uiAvp3 = shAvpFactory.createUserIdentity();
    
    // Verify that no values have been set
    AvpAssistant.testHassers(uiAvp3, false);

    // Set all previous values
    uiAvp3.setExtensionAvps(uiAvp2.getExtensionAvps());
    
    // Verify if values have been set
    AvpAssistant.testHassers(uiAvp3, true);
    
    // Verify if values have been correctly set
    AvpAssistant.testGetters(uiAvp3);
    
    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setExtensionAvps should be equal to original.", uiAvp2, uiAvp3);
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
