package org.jdiameter.api.cca.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppAnswerEvent;

/**
 * 
 * JCreditControlAnswer.java
 *
 * <br>Super project:  mobicents
 * <br>3:45:53 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public interface JCreditControlAnswer extends AppAnswerEvent {
	
	public static final String _SHORT_NAME = "CCA";
	public static final String _LONG_NAME = "Credit-Control-Answer";
	
	public static final int code = 272;
	
	boolean isCreditControlFailureHandlingAVPPresent();
	
	int getCredidControlFailureHandlingAVPValue();
	
	boolean isDirectDebitingFailureHandlingAVPPresent();
	
	int getDirectDebitingFailureHandlingAVPValue();
	
	boolean isRequestTypeAVPPresent();
	
	int getRequestTypeAVPValue();
	
	Avp getValidityTimeAvp();

}
