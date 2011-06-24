/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc. and individual contributors
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
import java.util.Collection;

import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
@RunWith(Parameterized.class)
public class IPFilterRuleFailTest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { "deny out udp from 192.168.0.0 to 192.168.1.1 established" },
        { "permit in 9999 from 192.168.0.0/24 to 192.168.1.1 frag foo" },
        { "permit in ip from 300.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established" },
        { "permit in ip from 257.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established" },
        { "deny out udp from 192.168.0.0 to 192.168.1.1 established" },
        { "permit in 9999 from 192.168.0.0/24 to 192.168.1.1 frag foo" },

        { "permit out 2 from 2001:0db8:Z122:0000:0000:8a2e:0370:7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:0db8:::1:8a2e:0370:7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:0db8:::7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:::7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001::: to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from ::: to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
    });
  }

  private String rule;

  public IPFilterRuleFailTest(String rule) {
    this.rule = rule;
  }

  @Test
  public void runTest() {
    try {
      IPFilterRule r = new IPFilterRule(rule);
      Assert.assertTrue("Rule should not be parsed properly: " + r.getRuleString(), false);
    } catch (IllegalArgumentException e) {

    }
  }
}
