/**
 * Start time:11:12:08 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.slee.resource.diameter.cxdx.handlers;

import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._ErrorAnswer;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._ExtensionDiameterMessage;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._LocationInfoAnswer;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._LocationInfoRequest;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._MultimediaAuthenticationAnswer;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._MultimediaAuthenticationRequest;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._PushProfileAnswer;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._PushProfileRequest;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._RegistrationTerminationRequest;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._ServerAssignmentAnswer;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._ServerAssignmentRequest;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._UserAuthorizationAnswer;
import static org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener._UserAuthorizationRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSessionListener;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;
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
 * Start time:11:12:08 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class CxDxSessionFactory implements IAppSessionFactory, ServerCxDxSessionListener,ClientCxDxSessionListener, StateChangeListener, ICxDxMessageFactory{

	private CxDxSessionCreationListener cxDxResourceAdaptor;
	private long messageTimeout;
	private SessionFactory sessionFactory;
	private static final Logger log = Logger.getLogger(CxDxSessionFactory.class);

	public CxDxSessionFactory(CxDxSessionCreationListener cxDxResourceAdaptor, long messageTimeout, SessionFactory sessionFactory) {
		super();
		this.cxDxResourceAdaptor = cxDxResourceAdaptor;
		this.messageTimeout = messageTimeout;
		this.sessionFactory = sessionFactory;
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.common.api.app.IAppSessionFactory#getNewSession(java.lang.String, java.lang.Class, org.jdiameter.api.ApplicationId, java.lang.Object[])
	 */
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

			this.cxDxResourceAdaptor.sessionCreated(clientSession);

			appSession = clientSession;
		}else if(appSessionClass == ServerCxDxSession.class)
		{
			CxDxServerSessionImpl serverSession = null;
			if (args != null && args.length > 0 && args[0] instanceof Request) {
				// This shouldnt happen but just in case
				Request request = (Request) args[0];
				serverSession = new CxDxServerSessionImpl(request.getSessionId(), this, sessionFactory, this);
			} else {
				serverSession = new CxDxServerSessionImpl(sessionId, this, sessionFactory, this);
			}

			serverSession.addStateChangeNotification(this);
			serverSession.getSessions().get(0).setRequestListener(serverSession);

			this.cxDxResourceAdaptor.sessionCreated(serverSession);

			appSession = serverSession;
		} else {
			throw new IllegalArgumentException("Wrong session class!![" + appSessionClass + "]. Supported[" + ServerCxDxSession.class + "," + ClientCxDxSession.class + "]");
		}
		return appSession;
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doLocationInformationRequest(org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JLocationInfoRequest, org.jdiameter.api.cxdx.events.JLocationInfoAnswer)
	 */
	public void doLocationInformationRequest(ServerCxDxSession session, JLocationInfoRequest request, JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_LocationInfoRequest , (Request)request.getMessage(), null);
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doMultimediaAuthRequest(org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JMultimediaAuthRequest, org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer)
	 */
	public void doMultimediaAuthRequest(ServerCxDxSession session, JMultimediaAuthRequest request, JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_MultimediaAuthenticationRequest , (Request)request.getMessage(), null);
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doOtherEvent(org.jdiameter.api.app.AppSession, org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
	 */
	public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		if (answer != null) {
			if(answer.getMessage().isError())
			{
				this.cxDxResourceAdaptor.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) answer.getMessage());
			}else
			{
				this.cxDxResourceAdaptor.fireEvent(appSession.getSessions().get(0).getSessionId(), _ExtensionDiameterMessage, null, (Answer) answer.getMessage());
			}
		} else {
			this.cxDxResourceAdaptor.fireEvent(appSession.getSessions().get(0).getSessionId(), _ExtensionDiameterMessage, (Request) request.getMessage(), null);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doPushProfileAnswer(org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JPushProfileRequest, org.jdiameter.api.cxdx.events.JPushProfileAnswer)
	 */
	public void doPushProfileAnswer(ServerCxDxSession session, JPushProfileRequest request, JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_PushProfileAnswer , null, (Answer) answer.getMessage());
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doRegistrationTerminationAnswer(org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest, org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
	 */
	public void doRegistrationTerminationAnswer(ServerCxDxSession session, JRegistrationTerminationRequest request, JRegistrationTerminationAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_PushProfileAnswer , null, (Answer) answer.getMessage());
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doServerAssignmentRequest(org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JServerAssignmentRequest, org.jdiameter.api.cxdx.events.JServerAssignmentAnswer)
	 */
	public void doServerAssignmentRequest(ServerCxDxSession session, JServerAssignmentRequest request, JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_ServerAssignmentRequest , (Request)request.getMessage(), null);
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ServerCxDxSessionListener#doUserAuthorizationRequest(org.jdiameter.api.cxdx.ServerCxDxSession, org.jdiameter.api.cxdx.events.JUserAuthorizationRequest, org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer)
	 */
	public void doUserAuthorizationRequest(ServerCxDxSession session, JUserAuthorizationRequest request, JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_UserAuthorizationRequest , (Request)request.getMessage(), null);
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doLocationInformationAnswer(org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JLocationInfoRequest, org.jdiameter.api.cxdx.events.JLocationInfoAnswer)
	 */
	public void doLocationInformationAnswer(ClientCxDxSession session, JLocationInfoRequest request, JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_LocationInfoAnswer , null, (Answer) answer.getMessage());
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doMultimediaAuthAnswer(org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JMultimediaAuthRequest, org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer)
	 */
	public void doMultimediaAuthAnswer(ClientCxDxSession session, JMultimediaAuthRequest request, JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_MultimediaAuthenticationAnswer , null, (Answer) answer.getMessage());
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doPushProfileRequest(org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JPushProfileRequest, org.jdiameter.api.cxdx.events.JPushProfileAnswer)
	 */
	public void doPushProfileRequest(ClientCxDxSession session, JPushProfileRequest request, JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_PushProfileRequest , (Request)request.getMessage(), null);
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doRegistrationTerminationRequest(org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest, org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
	 */
	public void doRegistrationTerminationRequest(ClientCxDxSession session, JRegistrationTerminationRequest request, JRegistrationTerminationAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_RegistrationTerminationRequest , (Request)request.getMessage(), null);
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doServerAssignmentAnswer(org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JServerAssignmentRequest, org.jdiameter.api.cxdx.events.JServerAssignmentAnswer)
	 */
	public void doServerAssignmentAnswer(ClientCxDxSession session, JServerAssignmentRequest request, JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_ServerAssignmentAnswer , null, (Answer) answer.getMessage());
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.cxdx.ClientCxDxSessionListener#doUserAuthorizationAnswer(org.jdiameter.api.cxdx.ClientCxDxSession, org.jdiameter.api.cxdx.events.JUserAuthorizationRequest, org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer)
	 */
	public void doUserAuthorizationAnswer(ClientCxDxSession session, JUserAuthorizationRequest request, JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		this.cxDxResourceAdaptor.fireEvent(session.getSessions().get(0).getSessionId(),_UserAuthorizationAnswer , null, (Answer) answer.getMessage());
		
	}

	/* (non-Javadoc)
	 * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum, java.lang.Enum)
	 */
	public void stateChanged(Enum oldState, Enum newState) {
		log.info("Diameter CxDx SessionFactory :: stateChanged :: oldState[" + oldState + "], newState[" + newState + "]");
		//FIXME: add code here.
		
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
