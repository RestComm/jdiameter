package org.jdiameter.api.cca;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

/**
 * 
 * ClientCCASession.java
 *
 * <br>Super project:  mobicents
 * <br>3:47:35 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public interface ClientCCASession extends AppSession, StateMachine {

	public void sendCreditControlRequest(JCreditControlRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
	
	public void sendReAuthAnswer(ReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
