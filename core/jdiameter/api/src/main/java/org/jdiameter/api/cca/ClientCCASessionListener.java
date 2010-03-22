package org.jdiameter.api.cca;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

/**
 * 
 * ClientCCASessionListener.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 3:47:53 PM Dec 2, 2008 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ClientCCASessionListener {

	void doCreditControlAnswer(ClientCCASession session,
			JCreditControlRequest request, JCreditControlAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException;

	void doReAuthRequest(ClientCCASession session, ReAuthRequest request)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException;

	/**
	 * Notifies this ClientCCASessionListener that the ClientCCASession has
	 * recived not CCA message, usually some extension.
	 * 
	 * @param session
	 *            parent application session (FSM)
	 * @param request
	 *            request object
	 * @param answer
	 *            answer object
	 * @throws InternalException
	 *             The InternalException signals that internal error is
	 *             occurred.
	 * @throws IllegalDiameterStateException
	 *             The IllegalStateException signals that session has incorrect
	 *             state (invalid).
	 * @throws RouteException
	 *             The NoRouteException signals that no route exist for a given
	 *             realm.
	 * @throws OverloadException
	 *             The OverloadException signals that destination host is
	 *             overloaded.
	 */
	void doOtherEvent(AppSession session, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException;

	/**
	 * Provides with default value of DDFH avp - this is used when avp is not
	 * present or send opoeration fails for some reason <br>
	 * DDFH is of type Enumarated - int32
	 * 
	 * @return
	 */

	int getDefaultDDFHValue();

	/**
	 * Provides with default value of CCFH avp - this is used when avp is not
	 * present or send operation fails for some reason <br>
	 * CCFH is of type Enumarated - int32
	 * 
	 * @return
	 */
	int getDefaultCCFHValue();

}
