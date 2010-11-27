package org.mobicents.slee.resources.diameter.tests.framework.validator;

import static org.jdiameter.client.impl.helpers.Parameters.*;
import static org.jdiameter.server.impl.helpers.Parameters.*;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Mode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.client.impl.DictionarySingleton;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
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

  private Dictionary instance = null;
  private static Stack stack = null;
  private static Stack serverStack = null;
  private final static String validatorOnFile = "dictionary.xml";
  private final static String validatorOffFile = "validatorOff.xml";

  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    serverStack = new org.jdiameter.client.impl.StackImpl();
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(validatorOnFile);
    try {

    	
    	
      MyConfigurationClient clientConf= new MyConfigurationClient();
      MyConfigurationServer serverConf = new MyConfigurationServer();

      System.out.println("[SERVER] Configured. Starting ...");
      serverStack.init(serverConf);
      serverStack.start();
      Thread.sleep(2000);
      System.out.println("[SERVER] Started!");

      System.out.println("[CLIENT] Configured. Starting ...");
      stack.init(clientConf);
      //conf dict, after stack.
      DictionarySingleton.getDictionary().configure(is);
      stack.start(Mode.ANY_PEER, 5000, TimeUnit.MILLISECONDS);
      System.out.println("[CLIENT] Started");
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.", e);
    }finally
    {
    	if(is!=null)
    	{
    		try{
    			is.close();
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }

    baseFactory = new DiameterMessageFactoryImpl(stack);
    // DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

    try {
      AvpDictionary.INSTANCE.parseDictionary(AvpUtilitiesTest.class.getClassLoader().getResourceAsStream(validatorOnFile));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  @Before
  public void setUp() {
    this.instance = DictionarySingleton.getDictionary();
  }

  @After
  public void tearDown() {
    this.instance = null;
  }

  @Test
  public void testOperationsAddWithValidatorOnAndRemovalAllowed() {
    AvpUtilities.allowRemove(true);
    instance.configure(this.getClass().getClassLoader().getResourceAsStream(validatorOnFile));
    instance.setEnabled(true);
    
    // It has session id
    AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest();

    // <avp name="Session-Id" code="263" vendor="0" multiplicity="1" index="0"/>
    AvpUtilities.setAvpAsUTF8String(request.getGenericData(), 263, request.getGenericData().getAvps(), "1346ferg5y");
    // <avp name="Origin-Host" code="264" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
    // <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
    // <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 283, request.getGenericData().getAvps(), realmName);
    // <avp name="Destination-Host" code="293" vendor="0" multiplicity="0-1" index="-1"/>
    //AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverURI);
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverHost);
    // <avp name="Accounting-Record-Type" code="480" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 480, request.getGenericData().getAvps(), 1);

    String sessionId = AvpUtilities.getAvpAsUTF8String(263, request.getGenericData().getAvps());
    Session localSession = null;

    try {
      localSession = stack.getSessionFactory().getNewSession(sessionId);

      sendMessage(localSession, request);

      System.out.println(request);
      fail("Should not send this message. Message MUST contain Accounting-Record-Number AVP (485), and it is not present.");
    }
    catch (org.jdiameter.api.validation.AvpNotAllowedException e) {
      if (e.getAvpCode() != 485 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (485:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
    }
    catch (Exception e) {
      Throwable cause = e;
      boolean wasAvpNotAllowed = false;

      while((cause = cause.getCause()) != null) {
        if(cause instanceof org.jdiameter.api.validation.AvpNotAllowedException) {
          wasAvpNotAllowed = true;
          org.jdiameter.api.validation.AvpNotAllowedException exc = (org.jdiameter.api.validation.AvpNotAllowedException)cause;
          if (exc.getAvpCode() != 485 && exc.getVendorId() != 0) {
            fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (485:0), Received (" + exc.getAvpCode() + ":" + exc.getVendorId() + ").");
          }
        }
      }

      if(!wasAvpNotAllowed) {
        fail("Message failed to be sent for wrong reason. Expected AvpNotAllowedException, Received " + e);
      }
    }

    // <avp name="Accounting-Record-Number" code="485" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

    // In here we send the message actually. It is OK!
    try {
      sendMessage(localSession, request);
    }
    catch (Exception e) {
      fail("Failed to send message when it should succeed! Exception: " + e);
    }

    // <avp name="Acct-Application-Id" code="259" vendor="0" multiplicity="0-1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);

    // Again, in here we send the message actually. It is OK!
    try {
      sendMessage(localSession, request);
    }
    catch (Exception e) {
      fail("Failed to send message when it should succeed! Exception: " + e);
    }

    // <!-- FORBBIDEN -->
    // <avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="0" index="-1"/>
    try {
      AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 258, request.getGenericData().getAvps(), 1);

      // Now send should fail. Message has AVP "Auth-Application-Id", not valid in ACR.
      sendMessage(localSession, request);

      fail("Should not send this message. Message MUST NOT contain Auth-Application-Id AVP (258), and it is present.");
    }
    catch (AvpNotAllowedException e) {
      if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (258:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
    }
    catch (Exception e) {
      fail("Message failed to be sent for wrong reason. Expected AvpNotAllowedException, Received " + e);
    }

    // This is just in case
    Map<ExpectedAvp, ExpectedAvp> expectedAvps = new HashMap<ExpectedAvp, ExpectedAvp>();
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
    testPresentAvps(request.getGenericData().getAvps(), expectedAvps);
  }

  @Test
  public void testOperationsAddWithValidatorOnAndRemovalNotAllowed() {
    AvpUtilities.allowRemove(false);

    instance.configure(this.getClass().getClassLoader().getResourceAsStream(validatorOnFile));
    instance.setEnabled(true);
    
    // It has session id
    AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest(new DiameterAvpImpl[]{new DiameterAvpImpl(263, 0L, 0, 1, "xxx".getBytes(), DiameterAvpType.UTF8_STRING)});

    // <avp name="Session-Id" code="263" vendor="0" multiplicity="1" index="0"/>
    try {
      AvpUtilities.setAvpAsUTF8String(request.getGenericData(), 263, request.getGenericData().getAvps(), "1346ferg5y");
      fail("Session-Id can not be set twice (AVP Allow Remove is OFF).");
    }
    catch (AvpNotAllowedException e) {
      if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (258:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
    }
    catch (Exception e) {
      fail("Message failed to be sent for wrong reason. Expected AvpNotAllowedException, Received " + e);
    }

    // <avp name="Origin-Host" code="264" vendor="0" multiplicity="1" index="-1"/>
    if(!request.hasOriginHost())
      AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
    // <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1" index="-1"/>
    if(!request.hasOriginRealm())
      AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
    // <avp name="Destination-Host" code="293" vendor="0" multiplicity="0-1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverURI);
    // <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 283, request.getGenericData().getAvps(), realmName);
    // <avp name="Accounting-Record-Type" code="480" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 480, request.getGenericData().getAvps(), 1);

    String sessionId = AvpUtilities.getAvpAsUTF8String(263, request.getGenericData().getAvps());
    Session localSession = null;

    try {
      localSession = stack.getSessionFactory().getNewSession(sessionId);

      sendMessage(localSession, request);

      fail("Should not send this message. Message MUST contain Accounting-Record-Number AVP (485), and it is not present.");
    }
    catch (org.jdiameter.api.validation.AvpNotAllowedException e) {
      if (e.getAvpCode() != 485 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (485:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
    }
    catch (Exception e) {
      Throwable cause = e;
      boolean wasAvpNotAllowed = false;

      while((cause = cause.getCause()) != null) {
        if(cause instanceof org.jdiameter.api.validation.AvpNotAllowedException) {
          wasAvpNotAllowed = true;
          org.jdiameter.api.validation.AvpNotAllowedException exc = (org.jdiameter.api.validation.AvpNotAllowedException)cause;
          if (exc.getAvpCode() != 485 && exc.getVendorId() != 0) {
            fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (485:0), Received (" + exc.getAvpCode() + ":" + exc.getVendorId() + ").");
          }
        }
      }

      if(!wasAvpNotAllowed) {
        fail("Message failed to be sent for wrong reason. Expected AvpNotAllowedException, Received " + e);
      }
    }

    // <avp name="Accounting-Record-Number" code="485" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

    // In here we send the message actually. It is OK!
    try {
      sendMessage(localSession, request);
    }
    catch (Exception e) {
      fail("Failed to send message when it should succeed! Exception: " + e);
    }

    // Message should already come with Acct-Application-Id.. but still..
    try {
      // <avp name="Acct-Application-Id" code="259" vendor="0" multiplicity="0-1" index="-1"/>
      AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
    }
    catch (AvpNotAllowedException e) {
      if (e.getAvpCode() != 259 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (259:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
    }

    // Again, in here we send the message actually. It is OK!
    try {
      sendMessage(localSession, request);
    }
    catch (Exception e) {
      fail("Failed to send message when it should succeed! Exception: " + e);
    }

    // <!-- FORBBIDEN -->
    // <avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="0" index="-1"/>
    try {
      AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 258, request.getGenericData().getAvps(), 1);

      // Now send should fail. Message has AVP "Auth-Application-Id", not valid in ACR.
      sendMessage(localSession, request);

      fail("Should not send this message. Message MUST NOT contain Auth-Application-Id AVP (258), and it is present.");
    }
    catch (AvpNotAllowedException e) {
      if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (258:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
    }
    catch (Exception e) {
      fail("Message failed to be sent for wrong reason. Expected AvpNotAllowedException, Received " + e);
    }

    // Just in case.
    Map<ExpectedAvp, ExpectedAvp> expectedAvps = new HashMap<ExpectedAvp, ExpectedAvp>();
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

    testPresentAvps(request.getGenericData().getAvps(), expectedAvps);
  }

  @Test
  public void testOperationsAddWithValidatorOffAndRemovalAllowed() {
    AvpUtilities.allowRemove(true);
    instance.configure(this.getClass().getClassLoader().getResourceAsStream(validatorOffFile));
    instance.setEnabled(false);
    // It has session id
    AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest(new DiameterAvpImpl[]{new DiameterAvpImpl(263, 0L, 0, 1, "xxx".getBytes(), DiameterAvpType.UTF8_STRING)});

    // <avp name="Session-Id" code="263" vendor="0" multiplicity="1" index="0"/>
    AvpUtilities.setAvpAsUTF8String(request.getGenericData(), 263, request.getGenericData().getAvps(), "1346ferg5y");
    // <avp name="Origin-Host" code="264" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
    // <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
    // <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1" index="-1"/>
    // We don't add this one, make it fail
    // <avp name="Destination-Host" code="293" vendor="0" multiplicity="0" index="-1" />
    //AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverURI);
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 293, request.getGenericData().getAvps(), serverHost);
    // <avp name="Accounting-Record-Type" code="480" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 480, request.getGenericData().getAvps(), 1);

    String sessionId = AvpUtilities.getAvpAsUTF8String(263, request.getGenericData().getAvps());
    Session localSession = null;

    try {
      localSession = stack.getSessionFactory().getNewSession(sessionId);

      sendMessage(localSession, request);

      // this should fail eve so, but just in case
      fail("Should not send this message. Message MUST contain Destination-Realm AVP (283), and it is not present.");
    }
    catch (RouteException e) {
      // We want to come here, since we lack Destination-Realm AVP (283).
    }
    catch (Exception e) {
      fail("Message failed to be sent for wrong reason. Expected RouteException due to lack of Destination-Realm AVP (283), Received " + e);
    }

    // <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsOctetString(request.getGenericData(), 283, request.getGenericData().getAvps(), realmName);

    // <avp name="Accounting-Record-Number" code="485" vendor="0" multiplicity="1" index="-1"/>
    AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

    // In here we send the message actually. It is OK!
    try {
      sendMessage(localSession, request);
    }
    catch (Exception e) {
      fail("Failed to send message when it should succeed! Exception: " + e);
    }

    int expectedAcctApplicationIdAvps = request.hasAcctApplicationId() ? 1 : 0;

    // Message should already come with Acct-Application-Id.. but still..
    try {
      // <avp name="Acct-Application-Id" code="259" vendor="0" multiplicity="0-1" index="-1"/>
      AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
      expectedAcctApplicationIdAvps++;
    }
    catch (AvpNotAllowedException e) {
      if (e.getAvpCode() != 259 && e.getVendorId() != 0) {
        fail("Message Validation failed with wrong AVP Code/Vendor-Id in Exception. Expected (259:0), Received (" + e.getAvpCode() + ":" + e.getVendorId() + ").");
      }
      else {
        fail("Wrong AVP not allowed to be added, with Validator in OFF state. It's smart, but not what we want.");
      }
    }

    // Again, in here we send the message actually. It is OK!
    try {
      sendMessage(localSession, request);
    }
    catch (Exception e) {
      fail("Failed to send message when it should succeed! Exception: " + e);
    }

    // <!-- FORBBIDEN -->
    // <avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="0" index="-1"/>
    try {
      AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 258, request.getGenericData().getAvps(), 1);
      // this should fail eve so, but just in case
    }
    catch (AvpNotAllowedException e) {
      fail("Wrong AVP not allowed to be added, with Validator in OFF state. It's smart, but not what we want.");
    }
    catch (Exception e) {
      fail("Failed to add AVP, when it should succeed, even being a not allowed AVP.");
    }

    // This is just in case
    Map<ExpectedAvp, ExpectedAvp> expectedAvps = new HashMap<ExpectedAvp, ExpectedAvp>();
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
    a.count = expectedAcctApplicationIdAvps;
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

    testPresentAvps(request.getGenericData().getAvps(), expectedAvps);
  }

  private void testPresentAvps(AvpSet set, Map<ExpectedAvp, ExpectedAvp> expected) {
    for(ExpectedAvp e: expected.values()) {
      AvpSet innerSet  = set.removeAvp(e.code);
      if(innerSet.size() != e.count) {
        fail("Wrong count of avps, code: "+e.code+", vendor:"+e.vendor+". Expected: "+e.count+", present: "+innerSet.size());
      }

      if(e.count>0) {
        Avp avp = innerSet.getAvpByIndex(0);
        if(avp.getVendorId() != e.vendor) {
          fail("Wrong vendor of avp, code: "+e.code+". Expected: "+e.vendor+", present: "+avp.getVendorId());
        }
      }
    }

    if(set.size() > 0) {
      StringBuffer buf = new StringBuffer();
      for(Avp a: set) {
        buf.append("Code[").append(a.getCode()).append("] Vendor[").append("], ");
      }
      fail("Wrong count of avps, removed all expected, left overs: " + set.size() + " -- " + buf.toString());
    }
  }

  private void sendMessage(Session localSession, DiameterMessageImpl message) throws Exception {
    localSession.send(message.getGenericData());
    Thread.sleep(1000);
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
          getInstance().add(VendorId, 0L).add(AuthApplId, 0L).add(AcctApplId, 3L));
      // Set peer table
      add(PeerTable, 
          // Peer 1
          getInstance().add(PeerRating, 1).add(PeerName, serverURI).add(PeerAttemptConnection, true));
      // Set realm table
      add(RealmTable, 
          // Realm 1
          getInstance().add(RealmEntry, realmName + ":" + clientHost + ", " + serverHost));
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
          getInstance().add(VendorId, 0L).add(AuthApplId, 0L).add(AcctApplId, 3L));
      // Set peer table
      add(PeerTable, 
          // Peer 1
          getInstance().add(PeerRating, 1).add(PeerAttemptConnection, false).add(PeerName, clientURI));
      // Set realm table
      add(RealmTable, 
          // Realm 1
          getInstance().add(RealmEntry, getInstance().
              add(RealmName, realmName).
              add(ApplicationId, getInstance().add(VendorId, 0L).add(AuthApplId, 0L).add(AcctApplId, 3L)).
              add(RealmHosts, clientHost + ", " + serverHost).
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
