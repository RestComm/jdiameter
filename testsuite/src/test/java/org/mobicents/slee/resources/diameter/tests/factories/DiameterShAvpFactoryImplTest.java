/**
 * Start time:11:09:44 2009-05-25<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resources.diameter.tests.factories;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.slee.resource.diameter.sh.client.DiameterShAvpFactoryImpl;

import static org.junit.Assert.*;


/**
 * Start time:11:09:44 2009-05-25<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
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
		this.shAvpFactory = new DiameterShAvpFactoryImpl(null,null);
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
