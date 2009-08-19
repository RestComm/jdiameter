/**
 * Start time:14:23:48 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.common.impl.app.cxdx.test;

import java.util.logging.Logger;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSessionListener;
import org.jdiameter.client.impl.app.cxdx.CxDxClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.impl.app.cxdx.JLocationInfoAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JLocationInfoRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JMultimediaAuthAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JMultimediaAuthRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JPushProfileAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JPushProfileRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JRegistrationTerminationAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JRegistrationTerminationRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JUserAuthorizationAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JUserAuthorizationRequestImpl;
import org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl;

/**
 * Start time:14:23:48 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public abstract class SessionFactoryCXDX implements IAppSessionFactory,ClientCxDxSessionListener, StateChangeListener,ICxDxMessageFactory, ServerCxDxSessionListener{

	protected SessionFactory sessionFactory = null;
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	public SessionFactoryCXDX(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}
	
	
	public AppSession getNewSession(String sessionId, Class<? extends AppSession> appSessionClass, ApplicationId applicationId, Object[] args) {
		AppSession appSession = null;
		if(appSessionClass == ClientCxDxSession.class)
		{
			CxDxClientSessionImpl clientSession = null;
			if (args != null && args.length > 0 && args[0] instanceof Request) {
				Request request = (Request) args[0];
				clientSession = new CxDxClientSessionImpl(request.getSessionId(),this,this.sessionFactory,this);
			}else
			{
				clientSession = new CxDxClientSessionImpl(sessionId,this,this.sessionFactory,this);
			}
			clientSession.getSessions().get(0).setRequestListener(clientSession);
			clientSession.addStateChangeNotification(this);


			appSession = clientSession;
		}else if(appSessionClass == ServerCxDxSession.class)
		{
			CxDxServerSessionImpl serverSession = null;
			if (args != null && args.length > 0 && args[0] instanceof Request) {
				// This shouldnt happen but just in case
				Request request = (Request) args[0];
				serverSession = new org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl(request.getSessionId(), this, sessionFactory, this);
			} else {
				serverSession = new org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl(sessionId, this, sessionFactory, this);
			}

			serverSession.addStateChangeNotification(this);
			serverSession.getSessions().get(0).setRequestListener(serverSession);


			appSession = serverSession;
		} else {
			throw new IllegalArgumentException("Wrong session class!![" + appSessionClass + "]. Supported[" + ServerCxDxSession.class + "," + ClientCxDxSession.class + "]");
		}
		return appSession;
	}
	
	public void stateChanged(Enum oldState, Enum newState) {
		
			logger
					.info("Diameter CCA SessionFactory :: stateChanged :: oldState["+
					 oldState + "], newState[" + newState + "]");
		
		
		System.out.println("Diameter CCA SessionFactory :: stateChanged :: oldState["+
		 oldState + "], newState[" + newState + "]");
	}

	

	public long[] getApplicationIds() {
		//FIXME: ???
		return new long[]{4};
	}
	
	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createLocationInfoAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createLocationInfoAnswer(Answer answer) {
		return new JLocationInfoAnswerImpl(answer);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createLocationInfoRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createLocationInfoRequest(Request request) {
		return new JLocationInfoRequestImpl(request);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createMultimediaAuthAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createMultimediaAuthAnswer(Answer answer) {
		return new JMultimediaAuthAnswerImpl(answer);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createMultimediaAuthRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createMultimediaAuthRequest(Request request) {
		return new JMultimediaAuthRequestImpl(request);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createPushProfileAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createPushProfileAnswer(Answer answer) {
		return new JPushProfileAnswerImpl(answer);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createPushProfileRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createPushProfileRequest(Request request) {
		// TODO Auto-generated method stub
		return new JPushProfileRequestImpl(request);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createRegistrationTerminationAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createRegistrationTerminationAnswer(Answer answer) {
		return new JRegistrationTerminationAnswerImpl(answer);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createRegistrationTerminationRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createRegistrationTerminationRequest(Request request) {
		return new JRegistrationTerminationRequestImpl(request);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createServerAssignmentAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createServerAssignmentAnswer(Answer answer) {
		return new JServerAssignmentAnswerImpl(answer);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createServerAssignmentRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createServerAssignmentRequest(Request request) {
		return new JServerAssignmentRequestImpl(request);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createUserAuthorizationAnswer(org.jdiameter.api.Answer)
	 */
	public AppAnswerEvent createUserAuthorizationAnswer(Answer answer) {
		return new JUserAuthorizationAnswerImpl(answer);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#createUserAuthorizationRequest(org.jdiameter.api.Request)
	 */
	public AppRequestEvent createUserAuthorizationRequest(Request request) {
		return new JUserAuthorizationRequestImpl(request);
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#getApplicationId()
	 */
	public long getApplicationId() {
		//FIXME: ??
		return 16777216;
	}
}
