/**
 * Start time:14:15:19 2009-05-27<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resources.diameter.tests.framework.validator;

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

import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;

import static org.junit.Assert.*;
/**
 * Start time:14:15:19 2009-05-27<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageValidatorTest {

	  private static String clientHost = "127.0.0.1";
	  private static String clientPort = "21812";
	  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;
	  
	  private static String serverHost = "localhost";
	  private static String serverPort = "1812";
	  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;
	  
	  private static String realmName = "mobicents.org";

	  private static DiameterMessageFactoryImpl baseFactory ;
	  
	  
	  
	  
	  private DiameterMessageValidator instance = null;
		
		@Before
		public void setUp() {
			this.instance = DiameterMessageValidator.getInstance();
			
		}

		@After
		public void tearDown() {
			this.instance = null;
		}

	  
	  
	  
	  @Test
	  public void testBasicOperations()
	  {
		//Yeah, its akward :) - it laready shoudl have session id.  
		  AccountingAnswerImpl answer = (AccountingAnswerImpl) baseFactory.createAccountingAnswer();
		  
		  AvpSet set  = answer.getGenericData().getAvps();
		  
		  //nooooow, lets try some tests
//		  <avp name="Session-Id" 					code="263" vendor="0" multiplicity="1" index="0"/>
		assertTrue("Session-Id is not allowed in this message, it should be.", this.instance.isAllowed(answer.commandCode, answer.getHeader().getApplicationId(), false, 263, 0));
		assertFalse("We should not allow to add more SessionIds than one, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer.getHeader()
				.getApplicationId(), false, set, 263, 0L));
//		  <avp name="Origin-Host" 				code="264" vendor="0" multiplicity="1" index="-1"/>
		assertTrue("Origin-Host is not allowed in this message, it should be.", this.instance.isAllowed(answer.commandCode, answer.getHeader().getApplicationId(), false, 264, 0));
		assertTrue("We should allow to add more Origin-Host than zero, operation indicates that it could not be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 264, 0L));
//		  <avp name="Event-Timestamp" 				code="55" 		vendor="0" multiplicity="0-1" index="-1"/>
		assertTrue("Event-Timestamp is not allowed in this message, it should be.", this.instance.isAllowed(answer.commandCode, answer.getHeader().getApplicationId(), false, 55, 0));
		assertTrue("We should allow to add more Event-Timestamp than zero, operation indicates that it could not be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 55, 0L));

		set.addAvp(55, 55L, 0L, true, false);
		assertFalse("We should not allow to add more Event-Timestamp than one, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 55, 0L));
//		  <avp name="Proxy-Info" 						code="284" 	vendor="0" multiplicity="0+"  index="-1"/>
		assertTrue("Proxy-Info is not allowed in this message, it should be.", this.instance.isAllowed(answer.commandCode, answer.getHeader().getApplicationId(), false, 284, 0));
		assertTrue("We should allow to add more Proxy-Info than zero, operation indicates that it could not note be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 284, 0L));

		set.addAvp(284, 284L, 0L, true, false);
		
		assertTrue("We should  allow to add more Proxy-Info than one, operation indicates that it could note be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 284, 0L));
		set.addAvp(284, 284L, 0L, true, false);
		assertTrue("We should  allow to add more Proxy-Info than two, operation indicates that it could note be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 284, 0L));
//		  <!-- FORBBIDEN -->
//		  <avp name="Auth-Application-Id" 	code="258" 	vendor="0" multiplicity="0"  index="-1"/>
		assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", this.instance.isAllowed(answer.commandCode, answer.getHeader().getApplicationId(), false, 258, 0));
		assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 258, 0L));
//		  <avp name="Destination-Realm" 		code="283" 	vendor="0" multiplicity="0"  index="-1"/>
		assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", this.instance.isAllowed(answer.commandCode, answer.getHeader().getApplicationId(), false, 258, 0));
		assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(answer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 258, 0L));
	  }
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
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
	    
	     baseFactory = new DiameterMessageFactoryImpl(stack);
	    //DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();
	    
	   
	    
	    try
	    {
	      AvpDictionary.INSTANCE.parseDictionary( MessageValidatorTest.class.getClassLoader().getResourceAsStream( "dictionary.xml" ) );
	    }
	    catch ( Exception e ) {
	      throw new RuntimeException("Failed to parse dictionary file.");
	    }
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
