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
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.sh.server.ShServerMessageFactoryImpl;

/**
 * 
 * ShServerFactoriesTest.java
 *
 * <br>Project:  mobicents
 * <br>6:49:07 PM Feb 27, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ShServerFactoriesTest {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "21812";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;
  
  private static String serverHost = "localhost";
  private static String serverPort = "1812";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;
  
  private static String realmName = "mobicents.org";

  private static ShServerMessageFactoryImpl shServerFactory;
  private static DiameterShAvpFactory shAvpFactory;
  
  static
  {
    Stack stack = new org.jdiameter.client.impl.StackImpl();
    try
    {
      stack.init(new MyConfiguration());
      AvpDictionary.INSTANCE.parseDictionary( ShServerFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to initialize the stack.");
    }
    
    DiameterMessageFactoryImpl baseMessageFactory = new DiameterMessageFactoryImpl(stack);
    shAvpFactory = new DiameterShAvpFactoryImpl(stack);
    shServerFactory = new ShServerMessageFactoryImpl(baseMessageFactory, null, stack, shAvpFactory);
  }
  
  @Test
  public void isAnswerPUA() throws Exception
  {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer();
    assertFalse("Request Flag in Profile-Update-Answer is set.", pua.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersPUA() throws Exception
  {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer();
    
    int nFailures = AvpAssistant.testMethods(pua, ProfileUpdateAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }
  
  @Test
  public void hasDestinationHostPUA() throws Exception
  {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmPUA() throws Exception
  {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pua.getDestinationRealm());    
  }
  
  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested)
   * http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetPUA() throws Exception
  {
    long originalValue = 5001;

    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer( originalValue, true );
    
    long obtainedValue = pua.getExperimentalResult().getExperimentalResultCode();
    
    assertTrue("Experimental-Result-Code in PUA should be " + originalValue +" and is " + obtainedValue + ".", originalValue == obtainedValue);
  }
  
  @Test
  public void isRequestPNR() throws Exception
  {
    PushNotificationRequest pnr = shServerFactory.createPushNotificationRequest();
    assertTrue("Request Flag in Push-Notification-Request is not set.", pnr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersPNR() throws Exception
  {
    PushNotificationRequest pnr = shServerFactory.createPushNotificationRequest();
    
    int nFailures = AvpAssistant.testMethods(pnr, PushNotificationRequest.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }
  
  @Test
  public void isPNRPublicIdentityAccessibleTwice() throws Exception
  {
    String originalValue = "sip:alexandre@diameter.mobicents.org";

    UserIdentityAvpImpl uiAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, 10415L, 1, 0, new byte[]{});
    uiAvp.setPublicIdentity( originalValue );
    
    PushNotificationRequest udr = shServerFactory.createPushNotificationRequest(uiAvp, new byte[1]);
    
    String obtainedValue1 = udr.getUserIdentity().getPublicIdentity();
    String obtainedValue2 = udr.getUserIdentity().getPublicIdentity();
    
    assertTrue("Obtained value for Public-Identity AVP differs from original.", obtainedValue1.equals( originalValue ));
    assertTrue("Obtained #1 value for Public-Identity AVP differs from Obtained #2.", obtainedValue1.equals( obtainedValue2 ));
  }

  @Test
  public void isAnswerSNA() throws Exception
  {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer();
    assertFalse("Request Flag in Subscribe-Notifications-Answer is set.", sna.getHeader().isRequest());
  }
  
  @Test
  public void testGettersAndSettersSNA() throws Exception
  {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer();
    
    int nFailures = AvpAssistant.testMethods(sna, SubscribeNotificationsAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }
  
  @Test
  public void hasDestinationHostSNA() throws Exception
  {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sna.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmSNA() throws Exception
  {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", sna.getDestinationRealm());    
  }

  
  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested)
   * http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetSNA() throws Exception
  {
    long originalValue = 5001;

    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer( originalValue, true );
    
    long obtainedValue = sna.getExperimentalResult().getExperimentalResultCode();
    
    assertTrue("Experimental-Result-Code in SNA should be " + originalValue +" and is " + obtainedValue + ".", originalValue == obtainedValue);
  }
  
  @Test
  public void isAnswerUDA() throws Exception
  {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer();
    assertFalse("Request Flag in User-Data-Answer is set.", uda.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersUDA() throws Exception
  {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer();
    
    int nFailures = AvpAssistant.testMethods(uda, UserDataAnswer.class);
    
    assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
  }
  
  @Test
  public void hasDestinationHostUDA() throws Exception
  {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", uda.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmUDA() throws Exception
  {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", uda.getDestinationRealm());    
  }

  /**
   * Test for Issue #665 (Diameter Experimental Result AVP is Nested)
   * http://code.google.com/p/mobicents/issues/detail?id=655
   * 
   * @throws Exception
   */
  @Test
  public void isExperimentalResultCorrectlySetUDA() throws Exception
  {
    long originalValue = 5001;

    UserDataAnswer uda = shServerFactory.createUserDataAnswer( originalValue, true );
    
    long obtainedValue = uda.getExperimentalResult().getExperimentalResultCode();
    
    assertTrue("Experimental-Result-Code in UDA should be " + originalValue +" and is " + obtainedValue + ".", originalValue == obtainedValue);
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
