/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
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
package org.mobicents.slee.resources.diameter.tests.factories;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;

/**
 * Start time:11:09:44 2009-05-25<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class DiameterShAvpFactoryImplTest {

	
	private DiameterShAvpFactoryImpl shAvpFactory = null;
	/**
	 * 	
	 */
	public DiameterShAvpFactoryImplTest() {
		// TODO Auto-generated constructor stub
	}
	
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
	public void testAvpFactoryUserDataValidation()
	{
		boolean testResult = false;
		byte[] userData = null;
		
		//we must not throw exception
		testResult = this.shAvpFactory.validateUserData(userData);
		assertEquals("Test result passed for null reference!!",false,testResult);
		
		//empty, should fail
		userData = new byte[256];
		
		testResult = this.shAvpFactory.validateUserData(userData);
		assertEquals("Test result passed for empty data!!",false,testResult);

		
		 userData=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		 			"<Sh-Data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		 			"<IWillFail></IWillFail>" +
		 			"</Sh-Data>").getBytes();

		 testResult = this.shAvpFactory.validateUserData(userData);
		 assertEquals("Test result passed for data not following schema!!",false,testResult);
		 
	 
		
		 userData=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		 			"<Sh-Data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		 				"<PublicIdentifiers>" +
		 					"<IMSPublicIdentity>" +
		 						"sip:john.doe@hp.com" +
		 					"</IMSPublicIdentity>" +
		 				"</PublicIdentifiers>" +
		 				"<Sh-IMS-Data>" +
		 					"<IMSUserState>" +
		 					"1" +
		 					"</IMSUserState>" +
		 				"</Sh-IMS-Data>"+
		 			"</Sh-Data>").getBytes();
    	testResult = this.shAvpFactory.validateUserData(userData);
		assertEquals("Test result did not pass for valid data.!!",true,testResult);
	}

}
