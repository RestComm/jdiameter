package org.jdiameter.common.api.app.cca;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

/**
 * 
 * ICCAMessageFactory.java
 *
 * <br>Super project:  mobicents
 * <br>4:22:14 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public interface ICCAMessageFactory {

  //FIXME: not sure why we need this, maybe to be able to execute some hacks?
	public ReAuthRequest createReAuthRequest(Request req);

	public ReAuthAnswer createReAuthAnswer(Answer answer);

	//FIXME: make server session those those
	public JCreditControlRequest createCreditControlRequest(Request req);

	public JCreditControlAnswer createCreditControlAnswer(Answer answer);
	
	public long[] getApplicationIds();
	
}
