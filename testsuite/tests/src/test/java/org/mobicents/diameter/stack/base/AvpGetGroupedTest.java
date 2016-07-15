/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.diameter.stack.base;

import static org.junit.Assert.fail;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.client.impl.parser.MessageImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.diameter.stack.functional.StackConfig;
import org.mobicents.diameter.stack.functional.StackCreator;

/**
 * JUnit tests for Avp.getGrouped() method. Started due to issue #2519
 * (http://code.google.com/p/mobicents/issues/detail?id=2519)
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpGetGroupedTest {

  private static final String AVP_VALUE = "mobicents-diameter";

  private Message message;

  private static StackCreator stackCreator = new StackCreator();
  static {
    try {
      stackCreator.init(new StackConfig());
    }
    catch (IllegalDiameterStateException e) {
      e.printStackTrace();
    }
    catch (InternalException e) {
      e.printStackTrace();
    }
  }

  @Before
  public void initializeMessage() throws InternalException, IllegalDiameterStateException {
    // Create any message
    message = stackCreator.getSessionFactory().getNewRawSession().createMessage(123, ApplicationId.createByAccAppId(3));
    message.setRequest(false);

    // Add AVPs
    AvpSet messageAvps = message.getAvps();

    // Experimental-Result    297  7.6     Grouped
    AvpSet erAvp = messageAvps.addGroupedAvp(297);
    // Experimental-Result-Code    298  7.7     Unsigned32
    erAvp.addAvp(298, 2001);

    // User-Name          1  8.14    UTF8String
    /*Avp unAvp = */messageAvps.addAvp(1, AVP_VALUE, false);
  }

  @Test(timeout = 500)
  public void testGroupedAvpOK() {
    // Forcing message -> bytes -> message with clone
    AvpSet messageAvps = ((Message) ((MessageImpl) message).clone()).getAvps();

    Avp erAvp = messageAvps.getAvp(297);

    if (erAvp == null) {
      fail("Unable to retrieve avp Experimental-Result.");
    }

    AvpSet erAvpSet = null;

    try {
      erAvpSet = erAvp.getGrouped();
    }
    catch (AvpDataException e) {
      fail("Unable to retrieve avp Experimental-Result as Grouped.");
    }

    Avp ercAvp = erAvpSet.getAvp(298);

    if (ercAvp == null) {
      fail("Unable to retrieve avp Experimental-Result-Code from Experimental-Result.");
    }

    try {
      if (ercAvp.getUnsigned32() != 2001) {
        fail("Avp Experimental-Result-Code has unexpected value: " + ercAvp.getUnsigned32() + "; Expected: 2001.");
      }
    }
    catch (AvpDataException e) {
      fail("Unable to retrieve avp Experimental-Result-Code value.");
    }
  }

  @Test(timeout = 500)
  public void testGroupedAvpNotOK() {
    // Forcing message -> bytes -> message with clone
    AvpSet messageAvps = ((Message) ((MessageImpl) message).clone()).getAvps();

    Avp unAvp = messageAvps.getAvp(1);

    if (unAvp == null) {
      fail("Unable to retrieve avp User-Name.");
    }

    try {
      if (!unAvp.getUTF8String().equals(AVP_VALUE)) {
        fail("Avp User-Name has unexpected value: " + unAvp.getUTF8String() + "; Expected: mobicents-diameter.");
      }
    }
    catch (AvpDataException e) {
      fail("Unable to retrieve avp User-Name value.");
    }

    AvpSet unAvpSet = null;

    try {
      unAvpSet = unAvp.getGrouped();
      fail("Able to retrieve as grouped from a non-grouped avp, User-Name: " + unAvpSet);
    }
    catch (AvpDataException e) {
      // We're good, we wanted this.
    }
  }

  @Test(timeout = 500)
  public void testGetGroupedAndValueAfter() {
    // Forcing message -> bytes -> message with clone
    AvpSet messageAvps = ((Message) ((MessageImpl) message).clone()).getAvps();

    Avp unAvp = messageAvps.getAvp(1);

    if (unAvp == null) {
      fail("Unable to retrieve avp User-Name.");
    }

    try {
      if (!unAvp.getUTF8String().equals(AVP_VALUE)) {
        fail("Avp User-Name has unexpected value: " + unAvp.getUTF8String() + "; Expected: mobicents-diameter.");
      }
    }
    catch (AvpDataException e) {
      fail("Unable to retrieve avp User-Name value.");
    }

    AvpSet unAvpSet = null;

    try {
      unAvpSet = unAvp.getGrouped();
      fail("Able to retrieve as grouped from a non-grouped avp, User-Name: " + unAvpSet);
    }
    catch (AvpDataException e) {
      // We're good, we wanted this.
      try {
        String value = unAvp.getUTF8String();
        Assert.assertEquals("Value retrieved as UTF8String after tried is not correct", AVP_VALUE, value);
      }
      catch (AvpDataException e1) {
        fail("Unable to retrieve as UTF8String after tried to retrieve as grouped.");
      }
    }
  }

}
