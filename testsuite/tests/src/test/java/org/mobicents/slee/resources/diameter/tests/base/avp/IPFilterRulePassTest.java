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
import java.util.Collection;

import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;

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
public class IPFilterRulePassTest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { 
        { "permit in ip from 192.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established" },
        { "permit out 2 from 192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss" },
        { "permit out 2 from !192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss" },
        { "permit out 2 from assigned 34 to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss ipoptions !rr,!ts" },
        { "deny in ip from !assigned to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss" },
        { "permit in 17 from 172.20.20.57 1031 to any" },
        { "permit in 17 from any to 172.20.20.57 1031-2000,300 " },
        { "permit in 17 from 172.20.20.57 1031-1300 to any 255,300-301" },
        { "permit out 17 from any to 172.20.20.54 6557" },
        { "permit in ip from 192.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established"},
        { "permit out 2 from 192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss" },
        { "permit out 2 from 192.1.0.0/24 to 192.1.1.1/0 2345"},
        { "permit out 2 from !192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss" },
        { "permit out 2 from 2001:0db8:85a3:0000:0000:8a2e:0370:7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:0db8:85a3:0000:0000:8a2e:0370:7334/32 to fe80:0:0:0:202:b3ff:fe1e:8329/24 2345" },
        { "permit out 2 from 2001:0db8:85a3::1:8a2e:0370:7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:0db8:85a3::0370:7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:0db8:85a3::7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:0db8::7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001::7334 to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from 2001:: to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from :: to fe80:0:0:0:202:b3ff:fe1e:8329 2345" },
        { "permit out 2 from any to 192.1.1.1/0 frag established setup tcpoptions mrss" },
        { "permit out 2 from assigned 34 to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss ipoptions !rr,!ts" },
        { "deny in ip from !assigned to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss" },
        { "deny in ip from !assigned to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss tcpflags fin,syn,rst,psh,ack,urg" },
        { "deny in ip from !assigned to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss icmptypes 0,3,4,5" },
    });
  }

  private String rule;

  public IPFilterRulePassTest(String rule) {
    this.rule = rule;
  }

  @Test
  public void runTest() throws IllegalArgumentException {
    new IPFilterRule(rule);
  }
}
