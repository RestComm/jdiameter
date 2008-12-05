package org.jdiameter.api.cca.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * 
 * JCreditControlRequest.java
 *
 * <br>Super project:  mobicents
 * <br>3:46:49 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public interface JCreditControlRequest extends AppRequestEvent {
  
	public static final String _SHORT_NAME = "CCR";
	public static final String _LONG_NAME = "Credit-Control-Request";
	
	public static final int code = 272;
	
	boolean isRequestedActionAVPPresent();
	
	int getRequestedActionAVPValue();
	
	boolean isRequestTypeAVPPresent();
	
	int getRequestTypeAVPValue();
}
