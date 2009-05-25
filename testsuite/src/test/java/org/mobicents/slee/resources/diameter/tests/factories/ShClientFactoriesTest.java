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
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.sh.client.ShClientMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

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
  //private static DiameterShAvpFactoryImpl shAvpFactory;
  
  static
  {
    Stack stack = new org.jdiameter.client.impl.StackImpl();
    try
    {
      stack.init(new MyConfiguration());
      AvpDictionary.INSTANCE.parseDictionary( ShClientFactoriesTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
    }
    catch ( Exception e ) {
      throw new RuntimeException("Failed to initialize the stack.");
    }
    
    shClientFactory = new ShClientMessageFactoryImpl(stack);
    //shAvpFactory = new DiameterShAvpFactoryImpl(stack);
  }
  
  @Test
  public void isRequestPUR() throws Exception
  {
    ProfileUpdateRequest pur = shClientFactory.createProfileUpdateRequest();
    assertTrue("Request Flag in Profile-Update-Request is not set.", pur.getHeader().isRequest());
  }
  
  @Test
  public void isAnswerPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer();
    assertFalse("Request Flag in Push-Notification-Answer is set.", pna.getHeader().isRequest());
  }

  @Test
  public void hasDestinationHostPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer();
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", pna.getDestinationHost());    
  }

  @Test
  public void hasDestinationRealmPNA() throws Exception
  {
    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer();
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

    PushNotificationAnswer pna = shClientFactory.createPushNotificationAnswer(originalValue, true );
    
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
  public void isRequestUDR() throws Exception
  {
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    assertTrue("Request Flag in User-Data-Request is not set.", udr.getHeader().isRequest());
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
