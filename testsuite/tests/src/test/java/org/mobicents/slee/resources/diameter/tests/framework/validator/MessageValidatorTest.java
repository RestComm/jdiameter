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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.MessageRepresentation;
import org.jdiameter.api.validation.ValidatorLevel;
import org.jdiameter.client.impl.parser.MessageParser;
import org.jdiameter.common.impl.validation.DictionaryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Start time:14:15:19 2009-05-27<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageValidatorTest extends TestCase {
	
	private static Logger logger = Logger.getLogger(MessageValidatorTest.class);

	private DictionaryImpl instance = null;
	
	@Before
	public void setUp() {

		this.instance = (DictionaryImpl) DictionaryImpl.INSTANCE;
		InputStream is = MessageValidatorTest.class.getClassLoader().getResourceAsStream("sdictionary.xml"); //use smaller file, so its easier to check contents.

		try {
			this.instance.configure(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@After
	public void tearDown() {
		this.instance = null;
	}

	@Test
	public void testBasicOperations() {

		Message answer = new MessageParser().createEmptyMessage(271, 0);
		answer.setRequest(false);
		AvpSet set = answer.getAvps();
		set.addAvp(263, "SESSION-ID;246t13461346713rfg@#$SD$@#6", false);
		// nooooow, lets try some tests
		MessageRepresentation msgRep = this.instance.getMessage(271, 0, false);

		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1"
		// index="0"/>
		assertTrue("Session-Id is not allowed in this message, it should be.", this.instance.getMessage(271, 0, false).isAllowed(263, 0));

		assertTrue("We should allow to add ONE (1) Session-Id AVP, operation indicates that it could not be done.",
				msgRep.isCountValidForMultiplicity(set, 263, 0L));

		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1"
		// index="-1"/>
		assertTrue("Origin-Host is not allowed in this message, it should be.", msgRep.isAllowed(264, 0));

		assertFalse("We should allow to add more Origin-Host than zero, operation indicates that it could not be done.",
				msgRep.isCountValidForMultiplicity(set, 264, 0L));

		// <avp name="Event-Timestamp" code="55" vendor="0" multiplicity="0-1"
		// index="-1"/>
		assertTrue("Event-Timestamp is not allowed in this message, it should be.", msgRep.isAllowed(55, 0));
		assertTrue("We should allow to add more Event-Timestamp than zero, operation indicates that it could not be done.",
				msgRep.isCountValidForMultiplicity(set, 55, 0L));

		set.addAvp(55, 55L, 0L, true, false);
		assertFalse("We should not allow to add more Event-Timestamp than one, operation indicates that it could be done.",
				msgRep.isCountValidForMultiplicity(set, 55, 0L, 1));

		// <avp name="Proxy-Info" code="284" vendor="0" multiplicity="0+"
		// index="-1"/>
		assertTrue("Proxy-Info is not allowed in this message, it should be.", msgRep.isAllowed(284, 0));
		assertTrue("We should allow to add more Proxy-Info than zero, operation indicates that it could not note be done.",
				msgRep.isCountValidForMultiplicity(set, 284, 0L));

		set.addAvp(284, 284L, 0L, true, false);

		assertTrue("We should  allow to add more Proxy-Info than one, operation indicates that it could note be done.",
				msgRep.isCountValidForMultiplicity(set, 284, 0L));

		set.addAvp(284, 284L, 0L, true, false);
		assertTrue("We should  allow to add more Proxy-Info than two, operation indicates that it could note be done.",
				msgRep.isCountValidForMultiplicity(set, 284, 0L));

		// <!-- FORBBIDEN -->
		// <avp name="Auth-Application-Id" code="258" vendor="0"
		// multiplicity="0" index="-1"/>
		assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", msgRep.isAllowed(258, 0));
		assertTrue("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.",
				msgRep.isCountValidForMultiplicity(set, 258, 0L));
		assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.",
				msgRep.isCountValidForMultiplicity(set, 258, 0L, 1));

		// <avp name="Destination-Realm" code="283" vendor="0" multiplicity="0"
		// index="-1"/>
		assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", msgRep.isAllowed(258, 0));
		assertTrue("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.",
				msgRep.isCountValidForMultiplicity(set, 258, 0L));
		assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.",
				msgRep.isCountValidForMultiplicity(set, 258, 0L, 1));
	}

	@Test
	public void testGroupedAvpValidationFail() {
		// Yeah, its awkward :) - it already should have session id.
		Message msg = createMessage();

		try {
			instance.validate(msg, false);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {
			// we are ok
			logger.info("[*]Valdiation failed properly with: ", ex);
		}

		fillTopLevelAvps(msg);

		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		addTopLevelGroupedAvp(msg);
		try {
			instance.validate(msg, false);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {
			// we are ok,
			logger.info("[*]Valdiation failed properly with: ", ex);
		}
		// This should pass.
		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		fillTopLevelGroupedAvp(msg);

		try {
			// this should pass
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}
		// This should pass.
		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

	}

	@Test
	public void testIncomingValidationLevels() {
		// here lets test how different levels affect
		Message msg = createMessage();

		// here we will fail on all levels

		// set all to off.
		this.instance.setReceiveLevel(ValidatorLevel.OFF);
		this.instance.setSendLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {

		}

		// now ALL
		this.instance.setReceiveLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {
			logger.info("[*]Valdiation failed properly with: ", ex);
		}

		fillTopLevelAvps(msg);
		// set all to off.
		this.instance.setReceiveLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now ALL
		this.instance.setReceiveLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		addTopLevelGroupedAvp(msg);

		// set all to off.
		this.instance.setReceiveLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now ALL
		this.instance.setReceiveLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {
			logger.info("[*]Valdiation failed properly with: ", ex);
		}

		fillTopLevelGroupedAvp(msg);

		// set all to off.
		this.instance.setReceiveLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now ALL
		this.instance.setReceiveLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

	}
	
	@Test
	public void testOutgoingValidationLevels() {
		// here lets test how different levels affect
		Message msg = createMessage();

		// here we will fail on all levels

		// set all to off.
		this.instance.setReceiveLevel(ValidatorLevel.OFF);
		this.instance.setSendLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setSendLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, false);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {

		}

		// now ALL
		this.instance.setSendLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, false);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {
			logger.info("[*]Valdiation failed properly with: ", ex);
		}

		fillTopLevelAvps(msg);
		// set all to off.
		this.instance.setSendLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setSendLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now ALL
		this.instance.setSendLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		addTopLevelGroupedAvp(msg);

		// set all to off.
		this.instance.setSendLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setSendLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now ALL
		this.instance.setSendLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, false);
			fail("Validation of message should fail: " + msg);
		} catch (AvpNotAllowedException ex) {
			logger.info("[*]Valdiation failed properly with: ", ex);
		}

		fillTopLevelGroupedAvp(msg);

		// set all to off.
		this.instance.setSendLevel(ValidatorLevel.OFF);
		this.instance.setConfigured(false);
		this.instance.setEnabled(false);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// enable
		this.instance.setEnabled(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// configure
		this.instance.setConfigured(true);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now lets do level magic
		this.instance.setSendLevel(ValidatorLevel.MESSAGE);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);
		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		// now ALL
		this.instance.setSendLevel(ValidatorLevel.ALL);
		try {
			instance.validate(msg, false);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

		try {
			instance.validate(msg, true);

		} catch (AvpNotAllowedException ex) {
			logger.info("[x]Valdiation failed with: ", ex);
			fail("Validation of message should not fail: " + msg);
		}

	}
	
	
	//helper methods, to manipulate msg content.
	/**
	 * Creates message which lack some top level AVPs
	 */
	protected Message createMessage()
	{
		Message msg = new MessageParser().createEmptyMessage(272, 0);
		AvpSet set = msg.getAvps();

		// nooooow, lets try some tests
		// we should test grouped AVPs bad behavior.
		// Must
		// avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="1" index="-1" />
		set.addAvp(258, 5);
		// This is set by factory.
		// answer.setAuthApplicationId(5);
		// <avp name="CC-Request-Number" code="415" vendor="0" multiplicity="1" index="-1" />
		set.addAvp(415, 0);
		// <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1" index="-1" />
		set.addAvp(296, "ALALAL", false);
		// <avp name="Result-Code" code="268" vendor="0" multiplicity="1" index="-1" />
		set.addAvp(268, 2006);
		// <avp name="Session-Id" code="263" vendor="0" multiplicity="1" index="0" />
		set.addAvp(263, "asdqw64ds", false);
		
		return msg;

	}
	
	protected void fillTopLevelAvps(Message msg)
	{
		AvpSet set = msg.getAvps();
		// <avp name="CC-Request-Type" code="416" vendor="0" multiplicity="1" index="-1" />
		set.addAvp(416, 1);
		// <avp name="Origin-Host" code="264" vendor="0" multiplicity="1" index="-1" />
		set.addAvp(264, "124121235", false);
	}
	
	protected void addTopLevelGroupedAvp(Message msg)
	{
		
		AvpSet set = msg.getAvps();
		// MAY - but we will add this, since its multi gropued avp.
		// <avp name="Granted-Service-Unit" code="431" vendor="0" multiplicity="0-1" index="-1" />
		AvpSet gsuAvp = set.addGroupedAvp(431);
		// <avp name="Granted-Service-Unit" code="431" mandatory="must" vendor-bit="mustnot" vendor-id="None" may-encrypt="yes"
		// protected="may">
		// <grouped>
		// <gavp name="Tariff-Time-Change" multiplicity="0-1"/>
		// <gavp name="CC-Time" multiplicity="0-1"/>
		// <gavp name="CC-Money" multiplicity="0-1"/>
		// <gavp name="CC-Total-Octets" multiplicity="0-1"/>
		// <gavp name="CC-Input-Octets" multiplicity="0-1"/>
		// <gavp name="CC-Output-Octets" multiplicity="0-1"/>
		// <gavp name="Cost-Unit" multiplicity="0-1"/>
		// </grouped>
		// </avp>
		// However CC-Money has mandatory field, which should be present -
		
		// gsuAVP.setCreditControlInputOctets(8);
		gsuAvp.addAvp(412, 8);
		// CcMoneyAvp ccMoney = ccaAvpFactory.createCcMoney(unitValue);
		AvpSet ccMoneyAvp = gsuAvp.addGroupedAvp(413);
		// ccMoney.setCurrencyCode(1);
		ccMoneyAvp.addAvp(425, 124);

		// Unit-Value, which is grouped avp which also has mandatory
		// Value-Digits
		// unitValue.setExponent(12);
		AvpSet unitValueAvp = ccMoneyAvp.addGroupedAvp(445);
		unitValueAvp.addAvp(429, 12);
		// this avp is bad, unit value does not have defined all fields.
	}
	
	protected void fillTopLevelGroupedAvp(Message msg)
	{
		try {
			AvpSet set = msg.getAvps();
			AvpSet gsuAvp = set.getAvp(431).getGrouped();
			AvpSet ccMoneyAvp = gsuAvp.getAvp(413).getGrouped();
			AvpSet unitValueAvp = ccMoneyAvp.getAvp(445).getGrouped();
			unitValueAvp.addAvp(447,142L);
		} catch (AvpDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
