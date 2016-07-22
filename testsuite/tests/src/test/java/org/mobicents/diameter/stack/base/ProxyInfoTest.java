/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc. and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.mobicents.diameter.stack.base;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.impl.parser.MessageParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.diameter.stack.functional.StackConfig;
import org.mobicents.diameter.stack.functional.StackCreator;

/**
 * JUnit tests for verifying if Proxy-Info AVP is properly copied.
 * Reported at <a href="https://code.google.com/p/jdiameter/issues/detail?id=40">Issue #40</a>.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ProxyInfoTest {

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
    message.setRequest(true);

    // Add AVPs
    AvpSet messageAvps = message.getAvps();

    AvpSet proxyInfoOne = messageAvps.addGroupedAvp(Avp.PROXY_INFO);
    proxyInfoOne.addAvp(Avp.PROXY_HOST, "one.mobicents.org", true);
    proxyInfoOne.addAvp(Avp.PROXY_STATE, "STATE1", true);

    AvpSet proxyInfoTwo = messageAvps.addGroupedAvp(Avp.PROXY_INFO);
    proxyInfoTwo.addAvp(Avp.PROXY_HOST, "two.mobicents.org", true);
    proxyInfoTwo.addAvp(Avp.PROXY_STATE, "STATE2", true);

    AvpSet proxyInfoThree = messageAvps.addGroupedAvp(Avp.PROXY_INFO);
    proxyInfoThree.addAvp(Avp.PROXY_HOST, "three.mobicents.org", true);
    proxyInfoThree.addAvp(Avp.PROXY_STATE, "STATE3", true);
  }

  @Test
  public void testAnswerHasProxyInfoOrdered() {
    MessageParser mp = new MessageParser();
    Message newMessage = mp.createEmptyMessage((IMessage) message);

    String[] hosts = {"one","two","three"};
    String[] states = {"STATE1","STATE2","STATE3"};

    for (int i = 0; i < message.getAvps().getAvps(Avp.PROXY_INFO).size(); i++) {
      Avp origAvp = null;
      Avp copyAvp = null;
      origAvp = message.getAvps().getAvps(Avp.PROXY_INFO).getAvpByIndex(i);
      copyAvp = newMessage.getAvps().getAvps(Avp.PROXY_INFO).getAvpByIndex(i);
      try {
        Assert.assertNotNull(origAvp.getRaw());
        Assert.assertNotNull(copyAvp.getRaw());

        Assert.assertNotSame(origAvp, copyAvp);

        Assert.assertArrayEquals(origAvp.getRaw(), copyAvp.getRaw());

        Assert.assertEquals(copyAvp.getGrouped().getAvp(Avp.PROXY_HOST).getUTF8String(), hosts[i] + ".mobicents.org");
        Assert.assertEquals(copyAvp.getGrouped().getAvp(Avp.PROXY_STATE).getUTF8String(), states[i]);
      }
      catch (AvpDataException e) {
        Assert.fail(e.getMessage());
      }
    }

  }
}
