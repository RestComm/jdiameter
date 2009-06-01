package org.mobicents.slee.resource.diameter.base.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;
import static org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener.*;

public class AccountingSessionFactory implements IAppSessionFactory, ServerAccSessionListener, StateChangeListener, ClientAccSessionListener {

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

	protected HashMap<ApplicationId, BaseSessionCreationListener> ras;
	protected long messageTimeout = 5000;
	protected SessionFactory sessionFactory = null;
	protected final static Logger logger = Logger.getLogger(AccountingSessionFactory.class);

	public static AccountingSessionFactory INSTANCE = new AccountingSessionFactory();

	/*
	 * public AccountingSessionFactory(BaseSessionCreationListener ra, long
	 * messageTimeout, SessionFactory sessionFactory, ApplicationId appId) {
	 * super(); this.ras.put(appId, ra); this.messageTimeout = messageTimeout;
	 * this.sessionFactory = sessionFactory; }
	 */

	private AccountingSessionFactory() {
		this.ras = new HashMap<ApplicationId, BaseSessionCreationListener>();
	}

	public void registerListener(BaseSessionCreationListener ra, long messageTimeout, SessionFactory sessionFactory) {
		for (ApplicationId appId : ra.getSupportedApplications())
			this.ras.put(appId, ra);
		this.messageTimeout = messageTimeout;
		this.sessionFactory = sessionFactory;
	}

	public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
		try {
			if (aClass == ServerAccSession.class) {
				Request request = (Request) args[0];

				ServerAccSessionImpl session = new ServerAccSessionImpl(sessionFactory.getNewSession(request.getSessionId()), request, this, messageTimeout, true, new StateChangeListener[] { this });
				BaseSessionCreationListener ra = this.ras.get(applicationId) != null ? this.ras.get(applicationId) : this.ras.values().iterator().next();
				ra.sessionCreated(session);
				return session;
			} else if (aClass == ClientAccSession.class) {
				ClientAccSessionImpl session = sessionId == null ? new ClientAccSessionImpl(sessionFactory, this, applicationId) : new ClientAccSessionImpl(sessionFactory, sessionId, this,
						applicationId);
				session.addStateChangeNotification(this);
				BaseSessionCreationListener ra = this.ras.get(applicationId) != null ? this.ras.get(applicationId) : this.ras.values().iterator().next();
				ra.sessionCreated(session);
				return session;
			}
		} catch (Exception e) {
			logger.error("Failure to obtain new Accounting Session.", e);
		}

		return null;
	}

	public void doAccRequestEvent(ServerAccSession appSession, AccountRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AccountingSessionFactory :: doAccRequestEvent :: appSession[" + appSession + "], Request[" + request + "]");

		ApplicationId appId = null;
		Iterator<ApplicationId> appIdIt = request.getMessage().getApplicationIdAvps().iterator();
		while (appIdIt.hasNext() && appId == null) {
			appId = appIdIt.next();
			appId = appId.getAcctAppId() != ApplicationId.UNDEFINED_VALUE && this.ras.containsKey(appId) ? appId : null;
		}

		BaseSessionCreationListener ra = appId != null ? this.ras.get(appId) : this.ras.values().iterator().next();

		ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _AccountingRequest, (Request) request.getMessage(), null);
	}

	public void doAccAnswerEvent(ClientAccSession appSession, AccountRequest request, AccountAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("doAccAnswerEvent :: appSession[" + appSession + "], request[" + request + "], answer[" + answer + "]");

		ApplicationId appId = null;
		Iterator<ApplicationId> appIdIt = request.getMessage().getApplicationIdAvps().iterator();
		while (appIdIt.hasNext() && appId == null) {
			appId = appIdIt.next();
			appId = appId.getAcctAppId() != ApplicationId.UNDEFINED_VALUE && this.ras.containsKey(appId) ? appId : null;
		}
		BaseSessionCreationListener ra = appId != null ? this.ras.get(appId) : this.ras.values().iterator().next();
		if(answer.getMessage().isError())
		{
			ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) answer.getMessage());
		}else
		{
			ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _AccountingAnswer, null, (Answer) answer.getMessage());
		}
	}

	public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base AccountingSessionFactory :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");

		ApplicationId appId = null;
		Iterator<ApplicationId> appIdIt = request.getMessage().getApplicationIdAvps().iterator();
		while (appIdIt.hasNext() && appId == null) {
			appId = appIdIt.next();
			appId = appId.getAcctAppId() != ApplicationId.UNDEFINED_VALUE && this.ras.containsKey(appId) ? appId : null;
		}
		BaseSessionCreationListener ra = appId != null ? this.ras.get(appId) : this.ras.values().iterator().next();

		//FIXME: Alex validate :}
		if (answer != null) {
			if(answer.getMessage().isError())
			{
				ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ErrorAnswer, null, (Answer) answer.getMessage());
			}else
			{
				ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ExtensionDiameterMessage, null, (Answer) answer.getMessage());
			}
		} else {
			ra.fireEvent(appSession.getSessions().get(0).getSessionId(), _ExtensionDiameterMessage, (Request) request.getMessage(), null);
		}
	}

	public void stateChanged(Enum oldState, Enum newState) {
		logger.info("Diameter Base AccountingSessionFactory :: stateChanged :: oldState[" + oldState + "], newState[" + newState + "]");
		//FIXME: add code here.
		
	}
}
