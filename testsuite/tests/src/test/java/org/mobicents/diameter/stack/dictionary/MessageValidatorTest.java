/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.dictionary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
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
 * Testsuite for Diameter Dictionary and Validator
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageValidatorTest {

  private static Logger logger = Logger.getLogger(MessageValidatorTest.class);

  private DictionaryImpl instance = null;

  @Before
  public void setUp() {
    this.instance = (DictionaryImpl) DictionaryImpl.INSTANCE;
  }

  @After
  public void tearDown() {
    this.instance = null;
  }

  @Test
  public void testBasicOperations() {

    // Set defaults
    instance.setEnabled(true);
    instance.setReceiveLevel(ValidatorLevel.OFF);
    instance.setSendLevel(ValidatorLevel.ALL);

    Message answer = new MessageParser().createEmptyMessage(271, 19302);
    answer.setRequest(false);
    AvpSet set = answer.getAvps();
    set.addAvp(Avp.SESSION_ID, "SESSION-ID;246t13461346713rfg@#$SD$@#6", false);
    // nooooow, lets try some tests
    MessageRepresentation msgRep = this.instance.getMessage(271, 19302, false);

    // <avp name="Session-Id" code="263" vendor="0" multiplicity="1" index="0"/>
    assertTrue("Session-Id is not allowed in this message, it should be.", msgRep.isAllowed(Avp.SESSION_ID, 0));

    assertTrue("We should allow to add ONE (1) Session-Id AVP, operation indicates that it could not be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.SESSION_ID, 0L));

    // <avp name="Origin-Host" code="264" vendor="0" multiplicity="1" index="-1"/>
    assertTrue("Origin-Host is not allowed in this message, it should be.", msgRep.isAllowed(Avp.ORIGIN_HOST, 0));

    assertFalse("We should allow to add more Origin-Host than zero, operation indicates that it could not be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.ORIGIN_HOST, 0L));

    // <avp name="Acct-Session-Id" code="44" vendor="0" multiplicity="0-1" index="-1" />
    assertTrue("Acct-Session-Id is not allowed in this message, it should be.", msgRep.isAllowed(Avp.ACC_SESSION_ID, 0));
    assertTrue("We should allow to add more Acct-Session-Id than zero, operation indicates that it could not be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.ACC_SESSION_ID, 0L));

    set.addAvp(Avp.ACC_SESSION_ID, 55L, 0L, true, false);
    assertFalse("We should not allow to add more Acct-Session-Id than one, operation indicates that it could be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.ACC_SESSION_ID, 0L, 1));

    // <avp name="Proxy-Info" code="284" vendor="0" multiplicity="0+" index="-1"/>
    assertTrue("Proxy-Info is not allowed in this message, it should be.", msgRep.isAllowed(Avp.PROXY_INFO, 0));
    assertTrue("We should allow to add more Proxy-Info than zero, operation indicates that it could not note be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.PROXY_INFO, 0L));

    set.addAvp(Avp.PROXY_INFO, 284L, 0L, true, false);

    assertTrue("We should  allow to add more Proxy-Info than one, operation indicates that it could note be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.PROXY_INFO, 0L));

    set.addAvp(Avp.PROXY_INFO, 284L, 0L, true, false);
    assertTrue("We should  allow to add more Proxy-Info than two, operation indicates that it could note be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.PROXY_INFO, 0L));

    // <!-- FORBBIDEN -->
    // <avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="0" index="-1"/>
    assertFalse("Auth-Application-Id is  allowed in this message, it should not be.", msgRep.isAllowed(Avp.AUTH_APPLICATION_ID, 0));
    assertTrue("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.AUTH_APPLICATION_ID, 0L));
    assertFalse("We should not  allow to add more Auth-Application-Id than zero, operation indicates that it could be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.AUTH_APPLICATION_ID, 0L, 1));

    // <avp name="Destination-Realm" code="283" vendor="0" multiplicity="0" index="-1"/>
    assertFalse("Destination-Realm is  allowed in this message, it should not be.", msgRep.isAllowed(Avp.DESTINATION_REALM, 0));
    assertTrue("We should not  allow to add more Destination-Realm than zero, operation indicates that it could be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.DESTINATION_REALM, 0L));
    assertFalse("We should not  allow to add more Destination-Realm than zero, operation indicates that it could be done.",
        msgRep.isCountValidForMultiplicity(set, Avp.DESTINATION_REALM, 0L, 1));
  }

  @Test
  public void testGroupedAvpValidationFail() {

    // Set defaults
    instance.setEnabled(true);
    instance.setReceiveLevel(ValidatorLevel.OFF);
    instance.setSendLevel(ValidatorLevel.ALL);

    // Yeah, its awkward :) - it already should have session id.
    Message msg = createMessage();

    try {
      instance.validate(msg, false);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      // we are ok
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    fillTopLevelAvps(msg);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    addTopLevelGroupedAvp(msg);
    try {
      instance.validate(msg, false);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      // we are ok,
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    try {
      // This should pass.
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    fillTopLevelGroupedAvp(msg);

    try {
      // This should pass.
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }
    // This should pass.
    try {
      instance.validate(msg, true);

    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

  }

  @Test
  public void testIncomingValidationLevels() {

    // Set defaults
    instance.setEnabled(true);
    instance.setReceiveLevel(ValidatorLevel.OFF);
    instance.setSendLevel(ValidatorLevel.ALL);

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
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      // we are ok
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    // now ALL
    this.instance.setReceiveLevel(ValidatorLevel.ALL);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    fillTopLevelAvps(msg);

    // set all to off.
    this.instance.setReceiveLevel(ValidatorLevel.OFF);
    this.instance.setConfigured(false);
    this.instance.setEnabled(false);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now ALL
    this.instance.setReceiveLevel(ValidatorLevel.ALL);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    addTopLevelGroupedAvp(msg);

    // set all to off.
    this.instance.setReceiveLevel(ValidatorLevel.OFF);
    this.instance.setConfigured(false);
    this.instance.setEnabled(false);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now ALL
    this.instance.setReceiveLevel(ValidatorLevel.ALL);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    fillTopLevelGroupedAvp(msg);

    // set all to off.
    this.instance.setReceiveLevel(ValidatorLevel.OFF);
    this.instance.setConfigured(false);
    this.instance.setEnabled(false);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setReceiveLevel(ValidatorLevel.MESSAGE);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now ALL
    this.instance.setReceiveLevel(ValidatorLevel.ALL);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }
  }

  @Test
  public void testOutgoingValidationLevels() {

    // Set defaults
    instance.setEnabled(true);
    instance.setReceiveLevel(ValidatorLevel.OFF);
    instance.setSendLevel(ValidatorLevel.ALL);

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
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setSendLevel(ValidatorLevel.MESSAGE);

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, false);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      // we are ok
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    // now ALL
    this.instance.setSendLevel(ValidatorLevel.ALL);
    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, false);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    fillTopLevelAvps(msg);

    // set all to off.
    this.instance.setSendLevel(ValidatorLevel.OFF);
    this.instance.setConfigured(false);
    this.instance.setEnabled(false);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setSendLevel(ValidatorLevel.MESSAGE);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now ALL
    this.instance.setSendLevel(ValidatorLevel.ALL);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    addTopLevelGroupedAvp(msg);

    // set all to off.
    this.instance.setSendLevel(ValidatorLevel.OFF);
    this.instance.setConfigured(false);
    this.instance.setEnabled(false);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setSendLevel(ValidatorLevel.MESSAGE);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now ALL
    this.instance.setSendLevel(ValidatorLevel.ALL);

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, false);
      fail("Validation of message should fail: " + msg);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[*] Validation failed properly with: " + ex.getMessage());
    }

    fillTopLevelGroupedAvp(msg);

    // set all to off.
    this.instance.setSendLevel(ValidatorLevel.OFF);
    this.instance.setConfigured(false);
    this.instance.setEnabled(false);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // enable
    this.instance.setEnabled(true);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // configure
    this.instance.setConfigured(true);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now lets do level magic
    this.instance.setSendLevel(ValidatorLevel.MESSAGE);
    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    // now ALL
    this.instance.setSendLevel(ValidatorLevel.ALL);

    try {
      instance.validate(msg, false);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

    try {
      instance.validate(msg, true);
    }
    catch (AvpNotAllowedException ex) {
      logger.info("[x] Validation failed with: ", ex);
      fail("Validation of message should not fail: " + msg);
    }

  }

  // helper methods, to manipulate msg content.
  /**
   * Creates message which lack some top level AVPs
   */
  protected Message createMessage() {
    Message msg = new MessageParser().createEmptyMessage(272, 4);
    AvpSet set = msg.getAvps();

    // nooooow, lets try some tests
    // we should test grouped AVPs bad behavior.
    // Must
    // avp name="Auth-Application-Id" code="258" vendor="0" multiplicity="1" index="-1" />
    set.addAvp(Avp.AUTH_APPLICATION_ID, 5);
    // This is set by factory.
    // answer.setAuthApplicationId(5);
    // <avp name="CC-Request-Number" code="415" vendor="0" multiplicity="1" index="-1" />
    set.addAvp(Avp.CC_REQUEST_NUMBER, 0);
    // <avp name="Origin-Realm" code="296" vendor="0" multiplicity="1" index="-1" />
    set.addAvp(Avp.ORIGIN_REALM, "ALALAL", false);
    // <avp name="Result-Code" code="268" vendor="0" multiplicity="1" index="-1" />
    set.addAvp(Avp.RESULT_CODE, 2006);
    // <avp name="Session-Id" code="263" vendor="0" multiplicity="1" index="0" />
    set.addAvp(Avp.SESSION_ID, "asdqw64ds", false);

    return msg;
  }

  protected void fillTopLevelAvps(Message msg) {
    AvpSet set = msg.getAvps();
    // <avp name="CC-Request-Type" code="416" vendor="0" multiplicity="1" index="-1" />
    set.addAvp(Avp.CC_REQUEST_TYPE, 1);
    // <avp name="Origin-Host" code="264" vendor="0" multiplicity="1" index="-1" />
    set.addAvp(Avp.ORIGIN_HOST, "124121235", false);
  }

  protected void addTopLevelGroupedAvp(Message msg) {
    AvpSet set = msg.getAvps();
    // MAY - but we will add this, since its multi gropued avp.
    // <avp name="Granted-Service-Unit" code="431" vendor="0" multiplicity="0-1" index="-1" />
    AvpSet gsuAvp = set.addGroupedAvp(Avp.GRANTED_SERVICE_UNIT);

    // <avp name="Granted-Service-Unit" code="431" mandatory="must" vendor-bit="mustnot"
    //      vendor-id="None" may-encrypt="yes" protected="may">
    //   <grouped>
    //     <gavp name="Tariff-Time-Change" multiplicity="0-1"/>
    //     <gavp name="CC-Time" multiplicity="0-1"/>
    //     <gavp name="CC-Money" multiplicity="0-1"/>
    //     <gavp name="CC-Total-Octets" multiplicity="0-1"/>
    //     <gavp name="CC-Input-Octets" multiplicity="0-1"/>
    //     <gavp name="CC-Output-Octets" multiplicity="0-1"/>
    //     <gavp name="Cost-Unit" multiplicity="0-1"/>
    //   </grouped>
    // </avp>

    gsuAvp.addAvp(Avp.CC_INPUT_OCTETS, 8);

    // However CC-Money has mandatory field, which should be present -
    AvpSet ccMoneyAvp = gsuAvp.addGroupedAvp(Avp.CC_MONEY);
    // ccMoney.setCurrencyCode(1);
    ccMoneyAvp.addAvp(Avp.CURRENCY_CODE, 124);

    // Unit-Value, which is grouped avp which also has mandatory Value-Digits
    AvpSet unitValueAvp = ccMoneyAvp.addGroupedAvp(Avp.UNIT_VALUE);
    unitValueAvp.addAvp(Avp.EXPONENT, 12);
    // this avp is bad, unit value does not have defined all fields.
  }

  protected void fillTopLevelGroupedAvp(Message msg) {
    try {
      AvpSet set = msg.getAvps();
      AvpSet gsuAvp = set.getAvp(Avp.GRANTED_SERVICE_UNIT).getGrouped();
      AvpSet ccMoneyAvp = gsuAvp.getAvp(Avp.CC_MONEY).getGrouped();
      AvpSet unitValueAvp = ccMoneyAvp.getAvp(Avp.UNIT_VALUE).getGrouped();
      unitValueAvp.addAvp(Avp.VALUE_DIGITS, 142L);
    }
    catch (AvpDataException e) {
      throw new RuntimeException(e);
    }
  }

}
