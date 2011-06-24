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

package org.mobicents.slee.resources.diameter.tests.base.avp;

import java.util.Arrays;

import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;

import org.junit.Assert;
import org.junit.Test;

/**
 * Few more tests, to check if proper data ends up in proper place.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class IPFilterRuleTest {

  private final static String[] EMPTY_STRING_ARRAY = new String[0];
  private final static int[] EMPTY_INT_ARRAY = new int[0];

  @Test
  public void testGetters1() {
    IPFilterRule rule = new IPFilterRule("permit out 2 from assigned 34 to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss ipoptions !rr,!ts");
    Assert.assertEquals("Action does not match!", IPFilterRule.ACTION_PERMIT, rule.getAction());
    Assert.assertEquals("Direction does not match!", IPFilterRule.DIR_OUT, rule.getDirection());
    Assert.assertEquals("Protocol does not match!", 2, rule.getProtocol());
    Assert.assertEquals("Any does not match!", false, rule.isAnyProtocol());
    Assert.assertEquals("SrcAssign does not match!", true, rule.isSourceAssignedIps());
    Assert.assertEquals("SrcNoMatch does not match!", false, rule.isSourceNoMatch());
    Assert.assertEquals("DstAssign does not match!", false, rule.isDestAssignedIps());
    Assert.assertEquals("DstNoMatch does not match!", false, rule.isDestNoMatch());
    Assert.assertEquals("SrcBits does not match!", -1, rule.getSourceBits());
    Assert.assertEquals("DstBits does not match!", 0, rule.getDestBits());
    Assert.assertEquals("SrcAddr does not match", null, rule.getSourceIp());
    Assert.assertEquals("DstAddr does not match", "192.1.1.1", rule.getDestIp());

    // ports
    int[][] sourcePorts = rule.getSourcePorts();
    int[][] expectedSourcePorts = new int[][] { new int[] { 34, 34 } };
    Assert.assertNotNull("No source ports!", sourcePorts);
    Assert.assertEquals("Source ports len does not match!", expectedSourcePorts.length, sourcePorts.length);
    for (int index = 0; index < expectedSourcePorts.length; index++) {
      Assert.assertTrue("Source ports entry[" + index + "] does not match!", Arrays.equals(expectedSourcePorts[index], sourcePorts[index]));
    }

    int[][] destPorts = rule.getDestPorts();
    int[][] expectedDestPorts = new int[][] { new int[] { 6, 6 }, new int[] { 3, 3 } };
    Assert.assertNotNull("No dest ports!", destPorts);
    Assert.assertEquals("Dest ports len does not match!", expectedDestPorts.length, destPorts.length);
    for (int index = 0; index < expectedDestPorts.length; index++) {
      Assert.assertTrue("Dest ports entry[" + index + "] does not match!", Arrays.equals(expectedDestPorts[index], destPorts[index]));
    }

    // options
    Assert.assertEquals("Expected fragment", true, rule.isFragment());
    Assert.assertEquals("Expected established", true, rule.isEstablished());
    Assert.assertEquals("Expected setup", true, rule.isSetup());
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getIcmpTypes(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getNumericIcmpTypes(), EMPTY_INT_ARRAY));

    Assert.assertTrue("Wrong tcpflags option", Arrays.equals(rule.getTcpFlags(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong tcpoptions option", Arrays.equals(rule.getTcpOptions(), new String[] { "mrss" }));
    Assert.assertTrue("Wrong ipoptions option", Arrays.equals(rule.getIpOptions(), new String[] { "!rr", "!ts" }));
  }

  @Test
  public void testGetters2() {
    IPFilterRule rule = new IPFilterRule("deny in ip from !assigned to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss icmptypes 0,3,4,5");
    Assert.assertEquals("Action does not match!", IPFilterRule.ACTION_DENY, rule.getAction());
    Assert.assertEquals("Direction does not match!", IPFilterRule.DIR_IN, rule.getDirection());
    Assert.assertEquals("Protocol does not match!", 0, rule.getProtocol()); // 0 is ip?
    Assert.assertEquals("Any does not match!", true, rule.isAnyProtocol()); // this is a bit confusing...
    Assert.assertEquals("SrcAssign does not match!", true, rule.isSourceAssignedIps());
    Assert.assertEquals("SrcNoMatch does not match!", true, rule.isSourceNoMatch());
    Assert.assertEquals("DstAssign does not match!", false, rule.isDestAssignedIps());
    Assert.assertEquals("DstNoMatch does not match!", false, rule.isDestNoMatch());
    Assert.assertEquals("SrcBits does not match!", -1, rule.getSourceBits());
    Assert.assertEquals("DstBits does not match!", 0, rule.getDestBits());
    Assert.assertEquals("SrcAddr does not match", null, rule.getSourceIp());
    Assert.assertEquals("DstAddr does not match", "192.1.1.1", rule.getDestIp());

    // ports
    int[][] sourcePorts = rule.getSourcePorts();
    Assert.assertNull("No source ports!", sourcePorts);

    int[][] destPorts = rule.getDestPorts();
    int[][] expectedDestPorts = new int[][] { new int[] { 6, 6 }, new int[] { 3, 3 } };
    Assert.assertNotNull("No dest ports!", destPorts);
    Assert.assertEquals("Dest ports len does not match!", expectedDestPorts.length, destPorts.length);
    for (int index = 0; index < expectedDestPorts.length; index++) {
      Assert.assertTrue("Dest ports entry[" + index + "] does not match!", Arrays.equals(expectedDestPorts[index], destPorts[index]));
    }

    // options
    Assert.assertEquals("Expected fragment", true, rule.isFragment());
    Assert.assertEquals("Expected established", true, rule.isEstablished());
    Assert.assertEquals("Expected setup", true, rule.isSetup());
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getIcmpTypes(), new String[] { "0", "3", "4", "5" }));
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getNumericIcmpTypes(), EMPTY_INT_ARRAY));

    Assert.assertTrue("Wrong tcpflags option", Arrays.equals(rule.getTcpFlags(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong tcpoptions option", Arrays.equals(rule.getTcpOptions(), new String[] { "mrss" }));
    Assert.assertTrue("Wrong opoptions option", Arrays.equals(rule.getIpOptions(), EMPTY_STRING_ARRAY));
  }

  @Test
  public void testGetters3() {
    IPFilterRule rule = new IPFilterRule("permit out 2 from 2001:0db8:85a3:0000:0000:8a2e:0370:7334/32 to fe80:0:0:0:202:b3ff:fe1e:8329/24 2345");
    Assert.assertEquals("Action does not match!", IPFilterRule.ACTION_PERMIT, rule.getAction());
    Assert.assertEquals("Direction does not match!", IPFilterRule.DIR_OUT, rule.getDirection());
    Assert.assertEquals("Protocol does not match!", 2, rule.getProtocol()); // 0 is ip?
    Assert.assertEquals("Any does not match!", false, rule.isAnyProtocol()); // this is a bit confusing...
    Assert.assertEquals("SrcAssign does not match!", false, rule.isSourceAssignedIps());
    Assert.assertEquals("SrcNoMatch does not match!", false, rule.isSourceNoMatch());
    Assert.assertEquals("DstAssign does not match!", false, rule.isDestAssignedIps());
    Assert.assertEquals("DstNoMatch does not match!", false, rule.isDestNoMatch());
    Assert.assertEquals("SrcBits does not match!", 32, rule.getSourceBits());
    Assert.assertEquals("DstBits does not match!", 24, rule.getDestBits());
    Assert.assertEquals("SrcAddr does not match", "2001:0db8:85a3:0000:0000:8a2e:0370:7334", rule.getSourceIp());
    Assert.assertEquals("DstAddr does not match", "fe80:0:0:0:202:b3ff:fe1e:8329", rule.getDestIp());

    // ports
    int[][] sourcePorts = rule.getSourcePorts();
    Assert.assertNull("No source ports!", sourcePorts);

    int[][] destPorts = rule.getDestPorts();
    int[][] expectedDestPorts = new int[][] { new int[] { 2345, 2345 } };
    Assert.assertNotNull("No dest ports!", destPorts);
    Assert.assertEquals("Dest ports len does not match!", expectedDestPorts.length, destPorts.length);
    for (int index = 0; index < expectedDestPorts.length; index++) {
      Assert.assertTrue("Dest ports entry[" + index + "] does not match!", Arrays.equals(expectedDestPorts[index], destPorts[index]));
    }

    // options
    Assert.assertEquals("Expected fragment", false, rule.isFragment());
    Assert.assertEquals("Expected established", false, rule.isEstablished());
    Assert.assertEquals("Expected setup", false, rule.isSetup());
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getIcmpTypes(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getNumericIcmpTypes(), EMPTY_INT_ARRAY));

    Assert.assertTrue("Wrong tcpflags option", Arrays.equals(rule.getTcpFlags(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong tcpoptions option", Arrays.equals(rule.getTcpOptions(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong opoptions option", Arrays.equals(rule.getIpOptions(), EMPTY_STRING_ARRAY));
  }

  // here we test options, if proper flags are lit

  @Test
  public void testGetters4() {
    IPFilterRule rule = new IPFilterRule("permit out 2 from 2001:0db8:85a3:0000:0000:8a2e:0370:7334/32 to fe80:0:0:0:202:b3ff:fe1e:8329/24 2345 frag");
    // options
    Assert.assertEquals("Expected fragment", true, rule.isFragment());
    Assert.assertEquals("Expected established", false, rule.isEstablished());
    Assert.assertEquals("Expected setup", false, rule.isSetup());
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getIcmpTypes(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getNumericIcmpTypes(), EMPTY_INT_ARRAY));

    Assert.assertTrue("Wrong tcpflags option", Arrays.equals(rule.getTcpFlags(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong tcpoptions option", Arrays.equals(rule.getTcpOptions(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong opoptions option", Arrays.equals(rule.getIpOptions(), EMPTY_STRING_ARRAY));

    rule = new IPFilterRule("permit out 2 from 2001:0db8:85a3:0000:0000:8a2e:0370:7334/32 to fe80:0:0:0:202:b3ff:fe1e:8329/24 2345 setup");
    // options
    Assert.assertEquals("Expected fragment", false, rule.isFragment());
    Assert.assertEquals("Expected established", false, rule.isEstablished());
    Assert.assertEquals("Expected setup", true, rule.isSetup());
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getIcmpTypes(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getNumericIcmpTypes(), EMPTY_INT_ARRAY));

    Assert.assertTrue("Wrong tcpflags option", Arrays.equals(rule.getTcpFlags(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong tcpoptions option", Arrays.equals(rule.getTcpOptions(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong opoptions option", Arrays.equals(rule.getIpOptions(), EMPTY_STRING_ARRAY));

    rule = new IPFilterRule("permit out 2 from 2001:0db8:85a3:0000:0000:8a2e:0370:7334/32 to fe80:0:0:0:202:b3ff:fe1e:8329/24 2345 established");
    // options
    Assert.assertEquals("Expected fragment", false, rule.isFragment());
    Assert.assertEquals("Expected established", true, rule.isEstablished());
    Assert.assertEquals("Expected setup", false, rule.isSetup());
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getIcmpTypes(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong icmp option", Arrays.equals(rule.getNumericIcmpTypes(), EMPTY_INT_ARRAY));

    Assert.assertTrue("Wrong tcpflags option", Arrays.equals(rule.getTcpFlags(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong tcpoptions option", Arrays.equals(rule.getTcpOptions(), EMPTY_STRING_ARRAY));
    Assert.assertTrue("Wrong opoptions option", Arrays.equals(rule.getIpOptions(), EMPTY_STRING_ARRAY));
  }

}
