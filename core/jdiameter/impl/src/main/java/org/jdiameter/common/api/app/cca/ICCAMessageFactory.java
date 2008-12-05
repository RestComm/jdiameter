package org.jdiameter.common.api.app.cca;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;

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
	public AppRequestEvent createReAuthRequest(Request req);

	public AppAnswerEvent createReAuthAnswer(Answer answer);

	//FIXME: make server session those those
	public AppRequestEvent createCreditControlRequest(Request req);

	public AppAnswerEvent createCreditControlAnswer(Answer answer);
	
	public long[] getApplicationIds();
	
}
