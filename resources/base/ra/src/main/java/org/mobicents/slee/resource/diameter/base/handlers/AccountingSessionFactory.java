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
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ClientAccSessionListener;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.impl.app.acc.ClientAccSessionImpl;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.acc.ClientAccSessionState;
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;


public class AccountingSessionFactory implements IAppSessionFactory,
		ServerAccSessionListener, StateChangeListener, ClientAccSessionListener {

	private static final Map<Integer, String> events;
	private static HashSet<Integer> accEventCodes = new HashSet<Integer>();
	private static HashSet<Integer> authEventCodes = new HashSet<Integer>();

	static {
		Map<Integer, String> eventsTemp = new HashMap<Integer, String>();

		eventsTemp.put(AbortSessionAnswer.commandCode, "AbortSession");
		eventsTemp.put(AccountingAnswer.commandCode, "Accounting");
		eventsTemp.put(CapabilitiesExchangeAnswer.commandCode,
				"CapabilitiesExchange");
		eventsTemp.put(DeviceWatchdogAnswer.commandCode, "DeviceWatchdog");
		eventsTemp.put(DisconnectPeerAnswer.commandCode, "DisconnectPeer");
		eventsTemp.put(ReAuthAnswer.commandCode, "ReAuth");
		eventsTemp.put(SessionTerminationAnswer.commandCode,
				"SessionTermination");
		eventsTemp.put(ErrorAnswer.commandCode, "Error");

		// FIXME: baranowb - make sure its compilant with xml
		eventsTemp.put(ExtensionDiameterMessage.commandCode,
				"ExtensionDiameter");

		events = Collections.unmodifiableMap(eventsTemp);

		authEventCodes.add(AbortSessionAnswer.commandCode);
		authEventCodes.add(ReAuthAnswer.commandCode);
		authEventCodes.add(SessionTerminationAnswer.commandCode);

		accEventCodes.add(AccountingAnswer.commandCode);
	}

	protected BaseSessionCreationListener ra;
	protected long messageTimeout = 5000;
	protected SessionFactory sessionFactory = null;
	protected final static Logger logger = Logger
			.getLogger(AccountingSessionFactory.class);

	public AccountingSessionFactory(BaseSessionCreationListener ra,
			long messageTimeout, SessionFactory sessionFactory) {
		super();
		this.ra = ra;
		this.messageTimeout = messageTimeout;
		this.sessionFactory = sessionFactory;
	}

	public AppSession getNewSession(String sessionId,
			Class<? extends AppSession> aClass, ApplicationId applicationId,
			Object[] args) {
		try {
			if (aClass == ServerAccSession.class) {
				Request request = (Request) args[0];

				ServerAccSessionImpl session = new ServerAccSessionImpl(
						sessionFactory.getNewSession(request.getSessionId()),
						request, this, messageTimeout, true,
						new StateChangeListener[] { this });
				this.ra.sessionCreated(session);
				return session;
			} else {
				if (aClass == ClientAccSession.class) {

					ClientAccSessionImpl session = sessionId == null ? new ClientAccSessionImpl(
							sessionFactory, this, applicationId)
							: new ClientAccSessionImpl(sessionFactory,
									sessionId, this, applicationId);
					session.addStateChangeNotification(this);		
					this.ra.sessionCreated(session);
					return session;
				}
			}

		} catch (Exception e) {
			logger.error("Failure to obtain new Accounting Session.", e);
		}

		return null;
	}

	public void doAccRequestEvent(ServerAccSession appSession,
			AccountRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AccountingSessionFactory :: doAccRequestEvent :: appSession["
				+ appSession + "], Request[" + request + "]");

		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(),
				events.get(request.getCommandCode()) + "Request",
				(Request) request.getMessage(), null);
	}

	public void doAccAnswerEvent(ClientAccSession appSession,
			AccountRequest request, AccountAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		logger.info("doAccAnswerEvent :: appSession[" + appSession
				+ "], request[" + request + "], answer[" + answer + "]");

		this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(),
				events.get(answer.getCommandCode()) + "Answer", null,
				(Answer) answer.getMessage());
	}

	public void doOtherEvent(AppSession appSession, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AccountingSessionFactory :: doOtherEvent :: appSession["
				+ appSession + "], Request[" + request + "], Answer[" + answer
				+ "]");

		if (answer != null) {
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(),
					events.get(answer.getCommandCode()) + "Answer", null,
					(Answer) answer.getMessage());
		} else {
			this.ra.fireEvent(appSession.getSessions().get(0).getSessionId(),
					events.get(request.getCommandCode()) + "Request",
					(Request) request.getMessage(), null);
		}
	}

	public void stateChanged(Enum oldState, Enum newState) {
		logger.info("Diameter Base AccountingSessionFactory :: stateChanged :: oldState[" + oldState
				+ "], newState[" + newState + "]");
		
	}
}
