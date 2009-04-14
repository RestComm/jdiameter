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
import static org.junit.Assert.assertTrue;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;
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
  public void isRequestPUA() throws Exception
  {
    ProfileUpdateAnswer pua = shServerFactory.createProfileUpdateAnswer();
    assertFalse("Request Flag in Profile-Update-Answer is set.", pua.getHeader().isRequest());
  }
  
  @Test
  public void isAnswerPNR() throws Exception
  {
    PushNotificationRequest pnr = shServerFactory.createPushNotificationRequest();
    assertTrue("Request Flag in Push-Notification-Request is not set.", pnr.getHeader().isRequest());
  }

  @Test
  public void isRequestSNA() throws Exception
  {
    SubscribeNotificationsAnswer sna = shServerFactory.createSubscribeNotificationsAnswer();
    assertFalse("Request Flag in Subscribe-Notifications-Answer is set.", sna.getHeader().isRequest());
  }
  
  @Test
  public void isAnswerUDA() throws Exception
  {
    UserDataAnswer uda = shServerFactory.createUserDataAnswer();
    assertFalse("Request Flag in User-Data-Answer is set.", uda.getHeader().isRequest());
  }

  @Test
  public void isExperimentalResultCorrectlySet() throws Exception
  {
    long originalValue = 5001;

    UserDataAnswer uda = shServerFactory.createUserDataAnswer( originalValue, true );
    
    long obtainedValue = uda.getExperimentalResult().getExperimentalResultCode();
    
    assertTrue("Experimental-Result-Code should be " + originalValue +" and is " + obtainedValue + ".", originalValue == obtainedValue);
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
