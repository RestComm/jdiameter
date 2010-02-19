package org.mobicents.slee.resources.diameter.tests.framework.validator;

import static org.jdiameter.client.impl.helpers.Parameters.*;
import static org.jdiameter.server.impl.helpers.Parameters.*;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;

/**
 * Start time:14:15:19 2009-05-27<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpUtilitiesTest {

	private static String clientHost = "127.0.0.1";
	private static String clientPort = "31812";
	private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

	private static String serverHost = "127.0.0.1";
	private static String serverPort = "41812";
	private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

	private static String realmName = "mobicentsXYZ.org";
	
	private static DiameterMessageFactoryImpl baseFactory;

	private DiameterMessageValidator instance = null;
	private static Stack stack = null;
	private static Stack serverStack = null;
	private final static String validatorOnFile = "dictionary.xml";
	private final static String validatorOffFile = "validatorOff.xml";

	@Before
	public void setUp() {
		this.instance = DiameterMessageValidator.getInstance();

	}

	@After
	public void tearDown() {
		this.instance = null;
	}

	@Test
	public void testOperationsAddWithValidatorOnAndRemovalAllowed() {
		AvpUtilities.allowRemove(true);
		instance.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream(validatorOnFile),true);
		// It has session id
		AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest();

		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0"/>
		AvpUtilities.setAvpAsUTF8String(request.getGenericData(), 263, request.getGenericData().getAvps(), "1346ferg5y");
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 283, request.getGenericData().getAvps(), realmName);
		//This host./fqdn is really annoying!!!!!!!!!!
		//AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverURI);
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverHost);
		// <avp name="Accounting-Record-Type" code="480" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 480, request.getGenericData().getAvps(), 1);
		String sessionId = AvpUtilities.getAvpAsUTF8String(263, request.getGenericData().getAvps());
		Session localSession = null;
		try {
			localSession = stack.getSessionFactory().getNewSession(sessionId);
			
			localSession.send(request.getGenericData());

			// this should fail eve so, but just in case
			fail("We should not send this message.");
		} catch (JAvpNotAllowedException e) {
			if (e.getAvpCode() != 485 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {
		  Throwable cause = e;
		  boolean wasAvpNotAllowed = false;
		  
		  while((cause = cause.getCause()) != null) {
		    if(cause instanceof JAvpNotAllowedException) {
		      wasAvpNotAllowed = true;
		      JAvpNotAllowedException exc = (JAvpNotAllowedException)cause;
		      if (exc.getAvpCode() != 485 && exc.getVendorId() != 0) {
		        fail("Wrong AVP code and vendorId in exception.");
		      }
		    }
		  }
		  
			e.printStackTrace();
			if(!wasAvpNotAllowed) {
			  fail("Failed to create Diam session");
			}
		}

		// <avp name="Accounting-Record-Number" code="485" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

		// Here it should be ok. since we are here, validation works,
		
		/* FIXME: Why should this fail?
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.RouteException e) {
			// its ok :)
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}
    */
		
		// <avp name="Acct-Application-Id" code="259" vendor="0"
		// multiplicity="0-1" index="-1"/>

		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
		// Here it should be ok. since we are here, validation works,
    /* FIXME: Again ... Why should this fail?
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.RouteException e) {
			// its ok :)
	
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}
    */

		// <!-- FORBBIDEN -->
		// <avp name="Auth-Application-Id" code="258" vendor="0"
		// multiplicity="0" index="-1"/>

		try {
			AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 258, request.getGenericData().getAvps(), 1);
			// this should fail eve so, but just in case
			fail("We should not get here.");
		} catch (AvpNotAllowedException e) {
			if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {

			e.printStackTrace();
			fail("Faield, we should get AvpNotAllowedException, not: " + e);
		}

		//This is just in case
		
		Map<ExpectedAvp,ExpectedAvp> expectedAvps = new HashMap<ExpectedAvp, ExpectedAvp>();
		ExpectedAvp a = new ExpectedAvp();
		a.code = 263;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 264;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 296;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 283;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 480;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 485;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 259;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 293;
		a.count = 1;
		expectedAvps.put(a, a);
		testPresentAvps(request.getGenericData().getAvps(),expectedAvps);
		
	}

	@Test
	public void testOperationsAddWithValidatorOnAndRemovalNotAllowed() {
		AvpUtilities.allowRemove(false);
		instance.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream(validatorOnFile),true);
		// It has session id
		AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest(new DiameterAvpImpl[]{new DiameterAvpImpl(263, 0L, 0, 1, "xxx".getBytes(), DiameterAvpType.UTF8_STRING)});

		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0"/>
		try {
			AvpUtilities.setAvpAsUTF8String(request.getGenericData(), 263, request.getGenericData().getAvps(), "1346ferg5y");
			fail("We should not get here: Session-Id can not be set twice if remove is off.");
		} catch (AvpNotAllowedException e) {
			if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {

			e.printStackTrace();
			fail("Failed, add operation should fail with AvpNotAllowedException, not with: " + e);
		}
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1"/>
		if(!request.hasOriginHost())
		  AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1"
		// index="-1"/>
    if(!request.hasOriginRealm())
      AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1"
		// index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverURI);
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 283, request.getGenericData().getAvps(), realmName);
   
		// <avp name="Accounting-Record-Type" code="480" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 480, request.getGenericData().getAvps(), 1);
		String sessionId = AvpUtilities.getAvpAsUTF8String(263, request.getGenericData().getAvps());
		Session localSession = null;

		try {
			localSession = stack.getSessionFactory().getNewSession(sessionId);

			localSession.send(request.getGenericData());

			// this should fail eve so, but just in case
			fail("We should nto send this message.");
		} catch (JAvpNotAllowedException e) {
			if (e.getAvpCode() != 485 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {
      Throwable cause = e;
      boolean wasAvpNotAllowed = false;
      
      while((cause = cause.getCause()) != null) {
        if(cause instanceof JAvpNotAllowedException) {
          wasAvpNotAllowed = true;
          JAvpNotAllowedException exc = (JAvpNotAllowedException)cause;
          if (exc.getAvpCode() != 485 && exc.getVendorId() != 0) {
            fail("Wrong AVP code and vendorId in exception.");
          }
        }
      }
      
      e.printStackTrace();
      if(!wasAvpNotAllowed) {
        fail("Failed to create Diam session");
      }
		}

		// <avp name="Accounting-Record-Number" code="485" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

		// Here it should be ok. since we are here, validation works,
    /* FIXME: Again ... Why should this fail?
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.RouteException e) {
			// its ok :)
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}
    */
		// <avp name="Acct-Application-Id" code="259" vendor="0"
		// multiplicity="0-1" index="-1"/>

		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
		// Here it should be ok. since we are here, validation works,
    /* FIXME: Again ... Why should this fail?
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.RouteException e) {
			// its ok :)
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}
    */
		
		// <!-- FORBBIDEN -->
		// <avp name="Auth-Application-Id" code="258" vendor="0"
		// multiplicity="0" index="-1"/>

		try {
			AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 258, request.getGenericData().getAvps(), 1);
			// this should fail eve so, but just in case
			fail("We should not get here.");
		} catch (AvpNotAllowedException e) {
			if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {

			e.printStackTrace();
			fail("Failed, add operation should fail with AvpNotAllowedException, not with: " + e);
		}
		
		//Just in case.
		
		Map<ExpectedAvp,ExpectedAvp> expectedAvps = new HashMap<ExpectedAvp, ExpectedAvp>();
		ExpectedAvp a = new ExpectedAvp();
		a.code = 263;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 264;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 296;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 283;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 480;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 485;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 259;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 293;
		a.count = 1;
		expectedAvps.put(a, a);
		testPresentAvps(request.getGenericData().getAvps(),expectedAvps);
		
		
	}

	
	
	@Test
	public void testOperationsAddWithValidatorOffAndRemovalAllowed() {
		AvpUtilities.allowRemove(true);
		instance.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream(validatorOffFile),true);
		
		// It has session id
		AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest(new DiameterAvpImpl[]{new DiameterAvpImpl(263, 0L, 0, 1, "xxx".getBytes(), DiameterAvpType.UTF8_STRING)});

		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0"/>
		AvpUtilities.setAvpAsUTF8String(request.getGenericData(), 263, request.getGenericData().getAvps(), "1346ferg5y");
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1"
		// index="-1"/>
		
		//This host./fqdn is really annoying!!!!!!!!!!
		//AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverURI);
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverHost);

		// <avp name="Accounting-Record-Type" code="480" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 480, request.getGenericData().getAvps(), 1);
		String sessionId = AvpUtilities.getAvpAsUTF8String(263, request.getGenericData().getAvps());
		Session localSession = null;
		try {
			localSession = stack.getSessionFactory().getNewSession(sessionId);

			localSession.send(request.getGenericData());

			// this should fail eve so, but just in case
			fail("We should not send this message.");
		} catch (org.jdiameter.api.RouteException e) {
			
		} catch (Exception e) {

			e.printStackTrace();
			fail("Failed to create session/send request due to wrong reason: "+e);
		}

		// <avp name="Accounting-Record-Number" code="485" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

		// Here it should be ok. since we are here, validation works,
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.RouteException e) {
			// its ok :)
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Failed to create session/send request due to wrong reason: "+e);
		}

		// <avp name="Acct-Application-Id" code="259" vendor="0"
		// multiplicity="0-1" index="-1"/>

		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
		// Here it should be ok. since we are here, validation works,
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.RouteException e) {
			// its ok :)
	
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}

		// <!-- FORBBIDEN -->
		// <avp name="Auth-Application-Id" code="258" vendor="0"
		// multiplicity="0" index="-1"/>

		try {
			AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 258, request.getGenericData().getAvps(), 1);
			// this should fail eve so, but just in case
			
		} catch (AvpNotAllowedException e) {
			fail("We should not get here.");
		} catch (Exception e) {

			e.printStackTrace();
			fail("Faield, we should get AvpNotAllowedException, not: " + e);
		}

		//This is just in case
		
		Map<ExpectedAvp,ExpectedAvp> expectedAvps = new HashMap<ExpectedAvp, ExpectedAvp>();
		ExpectedAvp a = new ExpectedAvp();
		a.code = 263;
		a.count = 2;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 264;
		a.count = 2; // was 1 but request comes with one already...
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 296;
		//cause its legal in this case.
		a.count = 3; // was 2 but request comes with one already...
		expectedAvps.put(a, a);
//		a = new ExpectedAvp();
//		a.code = 283;
//		a.count = 1;
//		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 480;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 485;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 259;
		a.count = 1;
		expectedAvps.put(a, a);
		// yes, its legal also. we dont check
		a = new ExpectedAvp();
		a.code = 258;
		a.count = 1;
		expectedAvps.put(a, a);
		a = new ExpectedAvp();
		a.code = 293;
		a.count = 1;
		expectedAvps.put(a, a);
		testPresentAvps(request.getGenericData().getAvps(),expectedAvps);
		
		
	}
	
	static {
		stack = new org.jdiameter.client.impl.StackImpl();
		serverStack = new org.jdiameter.client.impl.StackImpl();
		try {
			
			
			MyConfigurationClient  clientConf= new MyConfigurationClient();
			MyConfigurationServer serverConf = new MyConfigurationServer();
			System.err.println("\n\nServer configuration:\n"+serverConf.toString());
			
			System.err.println("\n\nClient configuration:\n"+clientConf.toString());
			serverStack.init(serverConf);
			serverStack.start();
			
			System.err.println("Started server!!");
			Thread.currentThread().sleep(5000);
			System.err.println("Startin client");
			
			
			stack.init(clientConf);
			stack.start();

			Thread.currentThread().sleep(10000);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize the stack.",e);
		}

		baseFactory = new DiameterMessageFactoryImpl(stack);
		// DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

		try {
			AvpDictionary.INSTANCE.parseDictionary(AvpUtilitiesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse dictionary file.");
		}
	}

	private void testPresentAvps(AvpSet set,Map<ExpectedAvp,ExpectedAvp> expected) {
		for(ExpectedAvp e: expected.values())
		{
			AvpSet innerSet  = set.removeAvp(e.code);
			if(innerSet.size()!=e.count)
			{
				fail("Wrong count of avps, code: "+e.code+", vendor:"+e.vendor+". Expected: "+e.count+", present: "+innerSet.size());
			}
			
			if(e.count>0)
			{
				Avp avp = innerSet.getAvpByIndex(0);
				if(avp.getVendorId()!=e.vendor)
				{
					fail("Wrong vendor of avp, code: "+e.code+". Expected: "+e.vendor+", present: "+avp.getVendorId());
				}
			}
		}
		
		if(set.size()>0)
		{
			String s="";
			for(Avp a: set)
			{
				s+="Code["+a.getCode()+"] Vendor["+a.getVendorId()+"],";
			}
			fail("Wrong count of avps, removed all expected, left overs: "+set.size()+" -- "+s);
		}
		
	}

	/**
	 * Class representing the Diameter Configuration
	 */
	 public static class MyConfigurationClient extends EmptyConfiguration {
		    public MyConfigurationClient() {
		      super();

		      add(Assembler, Assembler.defValue());
		      add(OwnDiameterURI, clientURI);
		      add(OwnRealm, realmName);
		      add(OwnVendorID, 193L);
		      // Set Ericsson SDK feature
		      // add(UseUriAsFqdn, true);
		      // Set Common Applications
		      add(ApplicationId,
		          // AppId 1
		          getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L));
		      // Set peer table
		      add(PeerTable,
		          // Peer 1
		          getInstance().add(PeerRating, 1).add(PeerName, serverURI).add(PeerAttemptConnection, true));
		      // Set realm table
		      add(RealmTable,
		          // Realm 1
		          getInstance().add(RealmEntry, realmName + ":" + clientHost + "," + serverHost));
		    }
		  }

		  /**
		   * Class representing the Diameter Configuration
		   */
		  public static class MyConfigurationServer extends org.jdiameter.server.impl.helpers.EmptyConfiguration {
		    public MyConfigurationServer() {
		      super();

		      add(Assembler, Assembler.defValue());
		      add(OwnDiameterURI, serverURI);
		      add(OwnRealm, realmName);
		      add(OwnVendorID, 193L);
		      // Set Ericsson SDK feature
		      // add(UseUriAsFqdn, true);
		      // Set Common Applications
		      add(ApplicationId,
		          // AppId 1
		          getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L));
		      // Set peer table
		      add(PeerTable,
		          // Peer 1
		          getInstance().add(PeerRating, 1).add(PeerAttemptConnection, false).add(PeerName, clientURI));
		      // Set realm table
		      add(RealmTable,
		          // Realm 1
		          getInstance().add(RealmEntry, getInstance().
		              add(RealmName, realmName).
		              add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L)).
		              add(RealmHosts, clientHost + "," + serverHost).
		              add(RealmLocalAction, "LOCAL").
		              add(RealmEntryIsDynamic, false).
		              add(RealmEntryExpTime, 1000L)));
		    }
		  }
		
}

class ExpectedAvp {
	int code = 0;
	long vendor = 0;
	int count = 0;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + (int) (vendor ^ (vendor >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpectedAvp other = (ExpectedAvp) obj;
		if (code != other.code)
			return false;
		if (vendor != other.vendor)
			return false;
		return true;
	}

}
