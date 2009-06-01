package org.mobicents.slee.resource.diameter.base.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;

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
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ClientAuthSessionListener;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.api.auth.ServerAuthSessionListener;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.client.impl.app.auth.ClientAuthSessionImpl;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.auth.IAuthMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.server.impl.app.auth.ServerAuthSessionImpl;
import static org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener.*;
public class AuthorizationSessionFactory implements IAppSessionFactory, IAuthMessageFactory, ServerAuthSessionListener, StateChangeListener, ClientAuthSessionListener {

	private long authAppId = 19301L;
	private static final Map<Integer, String> events;
	private static HashSet<Integer> accEventCodes = new HashSet<Integer>();
	private static HashSet<Integer> authEventCodes = new HashSet<Integer>();

	static {
		Map<Integer, String> eventsTemp = new HashMap<Integer, String>();

		eventsTemp.put(AbortSessionAnswer.commandCode, "AbortSession");
		eventsTemp.put(AccountingAnswer.commandCode, "Accounting");
		eventsTemp.put(CapabilitiesExchangeAnswer.commandCode, "CapabilitiesExchange");
		eventsTemp.put(DeviceWatchdogAnswer.commandCode, "DeviceWatchdog");
		eventsTemp.put(DisconnectPeerAnswer.commandCode, "DisconnectPeer");
		eventsTemp.put(ReAuthAnswer.commandCode, "ReAuth");
		eventsTemp.put(SessionTerminationAnswer.commandCode, "SessionTermination");
		eventsTemp.put(ErrorAnswer.commandCode, "Error");

		// FIXME: baranowb - make sure its compilant with xml
		eventsTemp.put(ExtensionDiameterMessage.commandCode, "ExtensionDiameter");

		events = Collections.unmodifiableMap(eventsTemp);

		authEventCodes.add(AbortSessionAnswer.commandCode);
		authEventCodes.add(ReAuthAnswer.commandCode);
		authEventCodes.add(SessionTerminationAnswer.commandCode);

		accEventCodes.add(AccountingAnswer.commandCode);
	}


	
	protected BaseSessionCreationListener ra;
	protected long messageTimeout = 5000;
	protected SessionFactory sessionFactory = null;
	protected final static Logger logger = Logger.getLogger(AccountingSessionFactory.class);

	private boolean stateless = true;

	public AuthorizationSessionFactory(BaseSessionCreationListener ra, long messageTimeout, SessionFactory sessionFactory) {
		super();
		this.ra = ra;
		this.messageTimeout = messageTimeout;
		this.sessionFactory = sessionFactory;
	}

	public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
		try {
			if (aClass == ServerAuthSession.class) {
				Request request = (Request) args[0];

				ServerAuthSessionImpl session = new ServerAuthSessionImpl(sessionFactory.getNewSession(request.getSessionId()), request, this, this, messageTimeout, stateless, this);
				this.ra.sessionCreated(session);

				return session;
			} else {
				if (aClass == ClientAuthSession.class) {
					ClientAuthSessionImpl session = sessionId == null ? new ClientAuthSessionImpl(stateless, this, sessionFactory, this) : new ClientAuthSessionImpl(stateless, sessionId, this,
							sessionFactory, this);
					session.addStateChangeNotification(this);
					this.ra.sessionCreated(session);
					return session;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return null;
	}

	public void stateChanged(Enum oldState, Enum newState) {
		logger.info("Diameter Base AuthorizationSessionFactory :: stateChanged :: oldState[" + oldState + "], newState[" + newState + "]");
	}

	public void doAbortSessionRequestEvent(ClientAuthSession appSession, AbortSessionRequest asr) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doAbortSessionRequestEvent :: appSession[" + appSession + "], ASR[" + asr + "]");

		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _AbortSessionRequest, (Request) asr.getMessage(), null);
	}

	public void doAbortSessionAnswerEvent(ServerAuthSession appSession, org.jdiameter.api.auth.events.AbortSessionAnswer asa) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doAbortSessionAnswerEvent :: appSession[" + appSession + "], ASA[" + asa + "]");

		if (asa.getMessage().isError())
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) asa.getMessage());
		else
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _AbortSessionAnswer, null, (Answer) asa.getMessage());
	}

	public void doSessionTerminationRequestEvent(ServerAuthSession appSession, SessionTermRequest str) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doSessionTerminationRequestEvent :: appSession[" + appSession + "], STA[" + str + "]");

		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _SessionTerminationRequest, (Request) str.getMessage(), null);
	}

	public void doSessionTerminationAnswerEvent(ClientAuthSession appSession, SessionTermAnswer sta) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doSessionTerminationAnswerEvent :: appSession[" + appSession + "], STA[" + sta + "]");
		if (sta.getMessage().isError())
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) sta.getMessage());
		else
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _SessionTerminationAnswer, null, (Answer) sta.getMessage());
	}

	public void doAuthRequestEvent(ServerAuthSession appSession, AppRequestEvent request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doAuthRequestEvent :: appSession[" + appSession + "], Request[" + request + "]");
		// FIXME???
		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), events.get(request.getCommandCode()) + "Request", (Request) request.getMessage(), null);
	}

	public void doAuthAnswerEvent(ClientAuthSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doAuthAnswerEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");
		// FIXME???
		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), events.get(answer.getCommandCode()) + "Answer", null, (Answer) answer.getMessage());
	}

	public void doReAuthRequestEvent(ClientAuthSession appSession, ReAuthRequest rar) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doReAuthRequestEvent :: appSession[" + appSession + "], RAR[" + rar + "]");

		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ReAuthRequest, (Request) rar.getMessage(), null);
	}

	public void doReAuthAnswerEvent(ServerAuthSession appSession, ReAuthRequest rar, org.jdiameter.api.auth.events.ReAuthAnswer raa) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doReAuthAnswerEvent :: appSession[" + appSession + "], RAR[" + rar + "], RAA[" + raa + "]");
		if (raa.getMessage().isError())
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) raa.getMessage());
		else
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ReAuthAnswer, null, (Answer) raa.getMessage());
	}

	public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AuthorizationSessionFactory :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");

		if (answer != null) {
			if (answer.getMessage().isError())
				this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) answer.getMessage());
			else
				this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ExtensionDiameterMessage, (Request) request.getMessage(), null);
		} else {
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ExtensionDiameterMessage, (Request) request.getMessage(), null);
		}
	}

	public AppAnswerEvent createAuthAnswer(Answer answer) {
		return new AppAnswerEventImpl(answer);
	}

	public AppRequestEvent createAuthRequest(Request request) {
		return new AppRequestEventImpl(request);
	}

	public ApplicationId getApplicationId() {
		return ApplicationId.createByAuthAppId(authAppId);
	}

	public int getAuthMessageCommandCode() {
		// FIXME: alexandre: what to use here?
		return 0;
	}

	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}

	public boolean getStateless() {
		return this.stateless;
	}
}
