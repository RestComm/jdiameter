/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
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

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlAnswerImpl;

import static org.junit.Assert.*;

/**
 * Start time:14:15:19 2009-05-27<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageValidatorTest {

	private static String clientHost = "127.0.0.1";
	private static String clientPort = "21812";
	private static String clientURI = "aaa://" + clientHost + ":" + clientPort;

	private static String serverHost = "localhost";
	private static String serverPort = "1812";
	private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

	private static String realmName = "mobicents.org";

	private static DiameterMessageFactoryImpl baseFactory;
	private static CreditControlMessageFactoryImpl ccaFactory;
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
	public void testBasicOperations() {
		// Yeah, its akward :) - it laready shoudl have session id.
		AccountingAnswerImpl answer = (AccountingAnswerImpl) baseFactory.createAccountingAnswer(baseFactory.createAccountingRequest(new DiameterAvpImpl[] { new DiameterAvpImpl(263, 0L, 0, 1, "xxx"
				.getBytes(), DiameterAvpType.UTF8_STRING) }));

		AvpSet set = answer.getGenericData().getAvps();

		// nooooow, lets try some tests

		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0"/>
		assertTrue("Session-Id is not allowed in this message, it should be.", this.instance.isAllowed(AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, 263, 0));

		if (answer.hasSessionId()) {
			assertFalse("We should not allow to add more SessionIds than one, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode,
					answer.getHeader().getApplicationId(), false, set, 263, 0L));
		} else {
			assertTrue("We should allow to add ONE (1) Session-Id AVP, operation indicates that it could not be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode, answer
					.getHeader().getApplicationId(), false, set, 263, 0L));
		}

		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1"/>
		assertTrue("Origin-Host is not allowed in this message, it should be.", this.instance.isAllowed(AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, 264, 0));
		if (answer.hasOriginHost()) {
			assertFalse("We should allow to add more Origin-Host than zero, operation indicates that it could not be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode,
					answer.getHeader().getApplicationId(), false, set, 264, 0L));
		} else {
			assertTrue("We should not allow to add more than one Origin-Host, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode,
					answer.getHeader().getApplicationId(), false, set, 264, 0L));
		}

		// <avp name="Event-Timestamp" code="55" vendor="0" multiplicity="0-1"
		// index="-1"/>
		assertTrue("Event-Timestamp is not allowed in this message, it should be.", this.instance.isAllowed(AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, 55, 0));
		assertTrue("We should allow to add more Event-Timestamp than zero, operation indicates that it could not be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode,
				answer.getHeader().getApplicationId(), false, set, 55, 0L));

		set.addAvp(55, 55L, 0L, true, false);
		assertFalse("We should not allow to add more Event-Timestamp than one, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode,
				answer.getHeader().getApplicationId(), false, set, 55, 0L));

		// <avp name="Proxy-Info" code="284" vendor="0" multiplicity="0+"
		// index="-1"/>
		assertTrue("Proxy-Info is not allowed in this message, it should be.", this.instance.isAllowed(AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, 284, 0));
		assertTrue("We should allow to add more Proxy-Info than zero, operation indicates that it could not note be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode,
				answer.getHeader().getApplicationId(), false, set, 284, 0L));

		set.addAvp(284, 284L, 0L, true, false);

		assertTrue("We should  allow to add more Proxy-Info than one, operation indicates that it could note be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 284, 0L));
		set.addAvp(284, 284L, 0L, true, false);
		assertTrue("We should  allow to add more Proxy-Info than two, operation indicates that it could note be done.", this.instance.isCountValidForMultiplicity(AccountingAnswer.commandCode, answer
				.getHeader().getApplicationId(), false, set, 284, 0L));

		// <!-- FORBBIDEN -->
		// <avp name="Auth-Application-Id" code="258" vendor="0"
		// multiplicity="0" index="-1"/>
		assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", this.instance.isAllowed(AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, 258, 0));
		assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(
				AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, set, 258, 0L));

		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="0"
		// index="-1"/>
		assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", this.instance.isAllowed(AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, 258, 0));
		assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.", this.instance.isCountValidForMultiplicity(
				AccountingAnswer.commandCode, answer.getHeader().getApplicationId(), false, set, 258, 0L));
	}

	@Test
	public void testGroupedAvpValidationFail() {
		// Yeah, its akward :) - it laready shoudl have session id.
		CreditControlAnswerImpl answer = (CreditControlAnswerImpl) ccaFactory.createCreditControlAnswer(ccaFactory.createCreditControlRequest("xxxxxxxxxxxx"));
		DiameterMessageValidator instance = DiameterMessageValidator.getInstance();
		AvpSet set = answer.getGenericData().getAvps();

		// nooooow, lets try some tests
		// we shoudl test gropued avps bad behaviour.
		// Must
		// avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="1"
		// index="-1" />
		
		//This is set by factory.
		//answer.setAuthApplicationId(5);
		// <avp name="CC-Request-Number" code="415" vendor="0" multiplicity="1"
		// index="-1" />

		
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1"
		// index="-1" />
		if (!answer.hasOriginRealm()) {
			answer.setOriginRealm(new DiameterIdentity("aaa://mobicents.org"));
		}
		// <avp name="Result-Code" code="268" vendor="0" multiplicity="1"
		// index="-1" />
		answer.setResultCode(2066);
		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0" />
		// should be there

		// MAY, but we add this, since its multi grouped avp
		// <avp name="Granted-Service-Unit" code="431" vendor="0"
		// multiplicity="0-1" index="-1" />
		
		// here we should fail.
		try {
			instance.validate(answer.getGenericData());
			fail("Validation of message should fails: "+answer);
		} catch (JAvpNotAllowedException ex) {
			//we are ok,
			//ex.printStackTrace();
		}
		answer.setCcRequestNumber(1);
		// <avp name="CC-Request-Type" code="416" vendor="0" multiplicity="1"
		// index="-1" />
		answer.setCcRequestType(CcRequestType.EVENT_REQUEST);
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1" />
		if (!answer.hasOriginHost()) {
			answer.setOriginHost(new DiameterIdentity("aaa://127.0.0.1:1818"));
		}
		
		// here we should not fail.
		try {
			instance.validate(answer.getGenericData());
			
		} catch (JAvpNotAllowedException ex) {
			ex.printStackTrace();
			fail("Validation of message should not fails: "+answer);
			return;
		}
		//MAY - but we will add this, since its multi gropued avp.
		//<avp name="Granted-Service-Unit" code="431" vendor="0" multiplicity="0-1" index="-1" />
		CreditControlAVPFactoryImpl ccaAvpFactory = new CreditControlAVPFactoryImpl(new DiameterAvpFactoryImpl());
		GrantedServiceUnitAvp gsuAVP=ccaAvpFactory.createGrantedServiceUnit();

//		<avp name="Granted-Service-Unit" code="431" mandatory="must" vendor-bit="mustnot" vendor-id="None" may-encrypt="yes" protected="may">
//		<grouped>
//			<gavp name="Tariff-Time-Change" multiplicity="0-1"/>
//			<gavp name="CC-Time"  multiplicity="0-1"/>
//			<gavp name="CC-Money"  multiplicity="0-1"/>
//			<gavp name="CC-Total-Octets"  multiplicity="0-1"/>
//			<gavp name="CC-Input-Octets"  multiplicity="0-1"/>
//			<gavp name="CC-Output-Octets"  multiplicity="0-1"/>
//			<gavp name="Cost-Unit"  multiplicity="0-1"/>
//		</grouped>
//		</avp>
		// However CC-Money has mandatory field, which should be present - Unit-Value, which is grouped avp which also has mandatory Value-Digits
		gsuAVP.setCreditControlInputOctets(8);
		UnitValueAvp unitValue= ccaAvpFactory.createUnitValue();
		unitValue.setExponent(12);
		CcMoneyAvp ccMoney = ccaAvpFactory.createCcMoney(unitValue);
		ccMoney.setCurrencyCode(1);
		gsuAVP.setCreditControlMoneyAvp(ccMoney);
		
		//this avp is bad, unit value does not have defined all fields.
		answer.setGrantedServiceUnit(gsuAVP);
		
		try {
			instance.validate(answer.getGenericData());
			fail("Validation of message should fails: "+answer);
		} catch (JAvpNotAllowedException ex) {
			//we are ok,
			ex.printStackTrace();
		}

		
	}
	@Test
	public void testGroupedAvpValidationSuccess() {
		// Yeah, its akward :) - it laready shoudl have session id.
		CreditControlAnswerImpl answer = (CreditControlAnswerImpl) ccaFactory.createCreditControlAnswer(ccaFactory.createCreditControlRequest("xxxxxxxxxxxx"));
		DiameterMessageValidator instance = DiameterMessageValidator.getInstance();
		AvpSet set = answer.getGenericData().getAvps();

		// nooooow, lets try some tests
		// we shoudl test gropued avps bad behaviour.
		// Must
		// avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="1"
		// index="-1" />
		
		//This is set by factory.
		//answer.setAuthApplicationId(5);
		// <avp name="CC-Request-Number" code="415" vendor="0" multiplicity="1"
		// index="-1" />

		
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1"
		// index="-1" />
		if (!answer.hasOriginRealm()) {
			answer.setOriginRealm(new DiameterIdentity("aaa://mobicents.org"));
		}
		// <avp name="Result-Code" code="268" vendor="0" multiplicity="1"
		// index="-1" />
		answer.setResultCode(2066);
		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0" />
		// should be there

		// MAY, but we add this, since its multi grouped avp
		// <avp name="Granted-Service-Unit" code="431" vendor="0"
		// multiplicity="0-1" index="-1" />
		
		// here we should fail.
		try {
			instance.validate(answer.getGenericData());
			fail("Validation of message should fails: "+answer);
		} catch (JAvpNotAllowedException ex) {
			//we are ok,
			//ex.printStackTrace();
		}
		answer.setCcRequestNumber(1);
		// <avp name="CC-Request-Type" code="416" vendor="0" multiplicity="1"
		// index="-1" />
		answer.setCcRequestType(CcRequestType.EVENT_REQUEST);
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1" />
		if (!answer.hasOriginHost()) {
			answer.setOriginHost(new DiameterIdentity("aaa://127.0.0.1:1818"));
		}
		
		// here we should not fail.
		try {
			instance.validate(answer.getGenericData());
			
		} catch (JAvpNotAllowedException ex) {
			ex.printStackTrace();
			fail("Validation of message should not fails: "+answer);
			return;
		}
		//MAY - but we will add this, since its multi gropued avp.
		//<avp name="Granted-Service-Unit" code="431" vendor="0" multiplicity="0-1" index="-1" />
		CreditControlAVPFactoryImpl ccaAvpFactory = new CreditControlAVPFactoryImpl(new DiameterAvpFactoryImpl());
		GrantedServiceUnitAvp gsuAVP=ccaAvpFactory.createGrantedServiceUnit();

//		<avp name="Granted-Service-Unit" code="431" mandatory="must" vendor-bit="mustnot" vendor-id="None" may-encrypt="yes" protected="may">
//		<grouped>
//			<gavp name="Tariff-Time-Change" multiplicity="0-1"/>
//			<gavp name="CC-Time"  multiplicity="0-1"/>
//			<gavp name="CC-Money"  multiplicity="0-1"/>
//			<gavp name="CC-Total-Octets"  multiplicity="0-1"/>
//			<gavp name="CC-Input-Octets"  multiplicity="0-1"/>
//			<gavp name="CC-Output-Octets"  multiplicity="0-1"/>
//			<gavp name="Cost-Unit"  multiplicity="0-1"/>
//		</grouped>
//		</avp>
		// However CC-Money has mandatory field, which should be present - Unit-Value, which is grouped avp which also has mandatory Value-Digits
		gsuAVP.setCreditControlInputOctets(8);
		UnitValueAvp unitValue= ccaAvpFactory.createUnitValue();
		unitValue.setExponent(12);
		CcMoneyAvp ccMoney = ccaAvpFactory.createCcMoney(unitValue);
		ccMoney.setCurrencyCode(1);
		gsuAVP.setCreditControlMoneyAvp(ccMoney);
		
		//this avp is bad, unit value does not have defined all fields.
		answer.setGrantedServiceUnit(gsuAVP);
		
		try {
			instance.validate(answer.getGenericData());
			fail("Validation of message should fails: "+answer);
		} catch (JAvpNotAllowedException ex) {
			//we are ok,
			ex.printStackTrace();
		}

		
	}
	static {
		Stack stack = new org.jdiameter.client.impl.StackImpl();
		try {
			stack.init(new MyConfiguration());
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize the stack.");
		}

		baseFactory = new DiameterMessageFactoryImpl(stack);
		// DiameterAvpFactoryImpl baseAvpFactory = new DiameterAvpFactoryImpl();
		try {
			ccaFactory = new CreditControlMessageFactoryImpl(baseFactory, stack.getSessionFactory().getNewSession(), stack, new CreditControlAVPFactoryImpl(new DiameterAvpFactoryImpl()));
		} catch (InternalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalDiameterStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
      AvpDictionary.INSTANCE.parseDictionary(MessageValidatorTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
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
