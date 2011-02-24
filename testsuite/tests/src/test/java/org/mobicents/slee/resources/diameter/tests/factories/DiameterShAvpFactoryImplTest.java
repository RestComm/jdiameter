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
package org.mobicents.slee.resources.diameter.tests.factories;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.DiameterShAvpFactoryImpl;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class DiameterShAvpFactoryImplTest {

  private DiameterShAvpFactoryImpl shAvpFactory = null;

  @Before
  public void setUp() {
    //FIXME: add more init for other tests later.
    this.shAvpFactory = new DiameterShAvpFactoryImpl(new DiameterAvpFactoryImpl());
  }

  @After
  public void tearDown() {
    this.shAvpFactory = null;
  }

  @Test
  public void testAvpFactoryUserDataValidation() {
    byte[] userData = null;

    // we must fail, since it's null
    assertFalse("Test result passed for null reference, it should have failed.", shAvpFactory.validateUserData(userData));

    // now it's just empty, should fail
    userData = new byte[256];

    assertFalse("Test result passed for empty data, it should have failed.", shAvpFactory.validateUserData(userData));

    userData = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<Sh-Data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<IWillFail></IWillFail>" +
        "</Sh-Data>").getBytes();

    assertFalse("Test result passed for data not following schema, it should have failed.", shAvpFactory.validateUserData(userData));

    userData=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<Sh-Data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<PublicIdentifiers>" +
            "<IMSPublicIdentity>sip:john.doe@hp.com</IMSPublicIdentity>" +
          "</PublicIdentifiers>" +
          "<Sh-IMS-Data>" +
            "<IMSUserState>1</IMSUserState>" +
          "</Sh-IMS-Data>"+
        "</Sh-Data>").getBytes();

    assertTrue("Test result failed for valid data, it should have passed.", shAvpFactory.validateUserData(userData));
  }
}
