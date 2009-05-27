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

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;

import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
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
public class AvpUtilitiesTest {

	private static String clientHost = "127.0.0.1";
	private static String clientPort = "21812";
	private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

	private static String serverHost = "localhost";
	private static String serverPort = "1812";
	private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

	private static String realmName = "mobicents.org";

	private static DiameterMessageFactoryImpl baseFactory;

	private DiameterMessageValidator instance = null;
	private static Stack stack = null;
	private final static String validatorOnFile = "validator.xml";
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
		instance.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream(validatorOnFile));
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
		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1"
		// index="-1"/>
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

			e.printStackTrace();
			fail("Failed to create Diam session");
		}

		// <avp name="Accounting-Record-Number" code="485" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

		// Here it should be ok. since we are here, validation works,
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.InternalException e) {
			// its ok :)
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}

		// <avp name="Acct-Application-Id" code="259" vendor="0"
		// multiplicity="0-1" index="-1"/>

		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
		// Here it should be ok. since we are here, validation works,
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.InternalException e) {
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
			fail("We should nto get here.");
		} catch (AvpNotAllowedException e) {
			if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {

			e.printStackTrace();
			fail("Faield, we should get AvpNotAllowedException, not: "+e);
		}
	}

	@Test
	public void testOperationsAddWithValidatorOnAndRemovalNotAllowed() {
		AvpUtilities.allowRemove(false);
		instance.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream(validatorOnFile));
		// It has session id
		AccountingRequestImpl request = (AccountingRequestImpl) baseFactory.createAccountingRequest();

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
			fail("Failed, add operation should fail with AvpNotAllowedException, not with: "+e);
		}
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 264, request.getGenericData().getAvps(), clientURI);
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1"
		// index="-1"/>
		AvpUtilities.setAvpAsOctetString(request.getGenericData(), 296, request.getGenericData().getAvps(), realmName);
		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="1"
		// index="-1"/>
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

			e.printStackTrace();
			fail("Failed to create Diam session");
		}

		// <avp name="Accounting-Record-Number" code="485" vendor="0"
		// multiplicity="1" index="-1"/>
		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 485, request.getGenericData().getAvps(), 1);

		// Here it should be ok. since we are here, validation works,
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.InternalException e) {
			// its ok :)
		} catch (Exception e) {
			// this includes validation error
			e.printStackTrace();
			fail("Received wrong exception...: " + e);
		}

		// <avp name="Acct-Application-Id" code="259" vendor="0"
		// multiplicity="0-1" index="-1"/>

		AvpUtilities.setAvpAsUnsigned32(request.getGenericData(), 259, request.getGenericData().getAvps(), 1);
		// Here it should be ok. since we are here, validation works,
		try {
			localSession.send(request.getGenericData());
			// this will fail, as there is no route
			fail("Should not allow to send");
		} catch (org.jdiameter.api.InternalException e) {
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
			fail("We should nto get here.");
		} catch (AvpNotAllowedException e) {
			if (e.getAvpCode() != 258 && e.getVendorId() != 0) {
				fail("Wrong AVP code and vendorId in exception.");
			}
		} catch (Exception e) {

			e.printStackTrace();
			fail("Failed, add operation should fail with AvpNotAllowedException, not with: "+e);
		}
	}

	static {
		stack = new org.jdiameter.client.impl.StackImpl();
		try {
			stack.init(new MyConfiguration());
			stack.start();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize the stack.");
		}

		baseFactory = new DiameterMessageFactoryImpl(stack);
		// DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();

		try {
			AvpDictionary.INSTANCE.parseDictionary(AvpUtilitiesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse dictionary file.");
		}
	}

	/**
	 * Class representing the Diameter Configuration
	 */
	public static class MyConfiguration extends EmptyConfiguration {
		public MyConfiguration() {
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
					getInstance().add(PeerRating, 1).add(PeerName, serverURI));
			// Set realm table
			add(RealmTable,
			// Realm 1
					getInstance().add(RealmEntry, realmName + ":" + clientHost + "," + serverHost));
		}
	}

}
