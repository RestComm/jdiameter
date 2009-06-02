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
    rfAvpFactory = new RoAvpFactoryImpl(baseAvpFactory, stack);
    
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
      
      session = ((ISessionFactory) sf).getNewAppSession(null, null, ServerAccSession.class, ((DiameterMessageImpl)acr).getGenericData());
      rfServerSession = new RfServerSessionImpl((DiameterMessageFactoryImpl)rfMessageFactory.getBaseMessageFactory(), (DiameterAvpFactoryImpl)rfAvpFactory.getBaseFactory(), session, 5000L, null, null, null, stack);
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