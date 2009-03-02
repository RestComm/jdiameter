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
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Test;
import org.mobicents.slee.resource.diameter.sh.client.ShClientMessageFactoryImpl;

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
    
    shClientFactory = new ShClientMessageFactoryImpl(stack);
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
  public void isRequestSNR() throws Exception
  {
    SubscribeNotificationsRequest snr = shClientFactory.createSubscribeNotificationsRequest();
    assertTrue("Request Flag in Subscribe-Notifications-Request is not set.", snr.getHeader().isRequest());
  }
  
  @Test
  public void isAnswerUDR() throws Exception
  {
    UserDataRequest udr = shClientFactory.createUserDataRequest();
    assertTrue("Request Flag in User-Data-Request is not set.", udr.getHeader().isRequest());
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
