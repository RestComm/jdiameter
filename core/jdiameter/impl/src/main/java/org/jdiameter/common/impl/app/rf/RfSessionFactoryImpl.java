/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.common.impl.app.rf;

import java.util.concurrent.ScheduledFuture;

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
import org.jdiameter.api.rf.ClientRfSession;
import org.jdiameter.api.rf.ClientRfSessionListener;
import org.jdiameter.api.rf.ServerRfSession;
import org.jdiameter.api.rf.ServerRfSessionListener;
import org.jdiameter.api.rf.events.RfAccountingAnswer;
import org.jdiameter.api.rf.events.RfAccountingRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.rf.ClientRfSessionImpl;
import org.jdiameter.common.api.app.rf.IClientRfActionContext;
import org.jdiameter.common.api.app.rf.IRfSessionFactory;
import org.jdiameter.common.api.app.rf.IServerRfActionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.rf.ServerRfSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Rf Session Factory implementation
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfSessionFactoryImpl implements IRfSessionFactory, ServerRfSessionListener, ClientRfSessionListener, IClientRfActionContext, IServerRfActionContext,
		StateChangeListener<AppSession> {

	protected Logger logger = LoggerFactory.getLogger(RfSessionFactoryImpl.class);

	protected ServerRfSessionListener serverSessionListener;
	protected StateChangeListener<AppSession> stateListener;
	protected ClientRfSessionListener clientSessionListener;
	protected IClientRfActionContext clientContextListener;
	protected IServerRfActionContext serverContextListener;

	protected ISessionDatasource iss;
	protected ISessionFactory sessionFactory = null;
	protected long messageTimeout = 5000;
	protected ApplicationId applicationId;

	protected RfSessionFactoryImpl() {
	}

	public RfSessionFactoryImpl(SessionFactory sessionFactory) {
		super();

		this.sessionFactory = (ISessionFactory) sessionFactory;
		this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
	}

	// ACC Factory Methods
	// ------------------------------------------------------

	/**
	 * @return the serverSessionListener
	 */
	public ServerRfSessionListener getServerSessionListener() {
		if (this.serverSessionListener != null) {
			return serverSessionListener;
		} else {
			return this;
		}
	}

	/**
	 * @param serverSessionListener
	 *            the serverSessionListener to set
	 */
	public void setServerSessionListener(ServerRfSessionListener serverSessionListener) {
		this.serverSessionListener = serverSessionListener;
	}

	/**
	 * @return the stateListener
	 */
	public StateChangeListener<AppSession> getStateListener() {
		if (this.stateListener != null) {
			return stateListener;
		} else {
			return this;
		}
	}

	/**
	 * @param stateListener
	 *            the stateListener to set
	 */
	public void setStateListener(StateChangeListener<AppSession> stateListener) {
		this.stateListener = stateListener;
	}

	/**
	 * @return the clientSessionListener
	 */
	public ClientRfSessionListener getClientSessionListener() {
		if (this.clientSessionListener != null) {
			return clientSessionListener;
		} else {
			return this;
		}
	}

	/**
	 * @param clientSessionListener
	 *            the clientSessionListener to set
	 */
	public void setClientSessionListener(ClientRfSessionListener clientSessionListener) {
		this.clientSessionListener = clientSessionListener;
	}

	/**
	 * @return the clientContextListener
	 */
	public IClientRfActionContext getClientContextListener() {
		if (this.clientContextListener != null) {
			return clientContextListener;
		} else {
			return this;
		}
	}

	/**
	 * @param clientContextListener
	 *            the clientContextListener to set
	 */
	public void setClientContextListener(IClientRfActionContext clientContextListener) {
		this.clientContextListener = clientContextListener;
	}

	/**
	 * @return the serverContextListener
	 */
	public IServerRfActionContext getServerContextListener() {
		if (this.serverContextListener != null) {
			return serverContextListener;
		} else {
			return this;
		}
	}

	/**
	 * @param serverContextListener
	 *            the serverContextListener to set
	 */
	public void setServerContextListener(IServerRfActionContext serverContextListener) {
		this.serverContextListener = serverContextListener;
	}

	/**
	 * @return the messageTimeout
	 */
	public long getMessageTimeout() {
		return messageTimeout;
	}

	/**
	 * @param messageTimeout
	 *            the messageTimeout to set
	 */
	public void setMessageTimeout(long messageTimeout) {
		this.messageTimeout = messageTimeout;
	}

	/**
	 * @return the sessionFactory
	 */
	public ISessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(ISessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		if (this.iss == null) {
			this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.acc.IAccSessionFactory#getApplicationId()
	 */
	public ApplicationId getApplicationId() {
		return this.applicationId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.acc.IAccSessionFactory#setApplicationId(
	 * org.jdiameter.api.ApplicationId)
	 */
	public void setApplicationId(ApplicationId id) {
		this.applicationId = id;
	}

	// App Session Factory
	// ------------------------------------------------------

	public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
		try {
			if (aClass == ServerRfSession.class) {
				Request request = null;
				if (args != null && args.length > 0) {
					request = (Request) args[0];
				}
				// FIXME: determine how to get boolean flag!
				ServerRfSessionImpl session = new ServerRfSessionImpl(sessionId, sessionFactory, request, getServerSessionListener(), getServerContextListener(),
						getStateListener(), messageTimeout, true);
				iss.addSession(session);
				session.getSessions().get(0).setRequestListener(session);
				return session;
			} else if (aClass == ClientRfSession.class) {
				ClientRfSessionImpl session = new ClientRfSessionImpl(sessionId, sessionFactory, getClientSessionListener(), getClientContextListener(), getStateListener(),
						this.getApplicationId());

				iss.addSession(session);
				session.getSessions().get(0).setRequestListener(session);
				return session;
			}
		} catch (Exception e) {
			logger.error("Failure to obtain new Accounting Session.", e);
		}

		return null;
	}

	// State Change Listener
	// ----------------------------------------------------

	@SuppressWarnings("unchecked")
	public void stateChanged(Enum oldState, Enum newState) {
		logger.info("Diameter ACC SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object,
	 * java.lang.Enum, java.lang.Enum)
	 */
	@SuppressWarnings("unchecked")
	public void stateChanged(AppSession source, Enum oldState, Enum newState) {
		logger.info("Diameter Rf SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[] { source, oldState, newState });
	}

	// ///////////////////
	// Event listeners //
	// ///////////////////

	public void doRfAccountingRequestEvent(ServerRfSession appSession, RfAccountingRequest acr) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		logger.info("Diameter Base RfSessionFactory :: doAccRequestEvent :: appSession[" + appSession + "], Request[" + acr + "]");
	}

	public void doRfAccountingAnswerEvent(ClientRfSession appSession, RfAccountingRequest acr, RfAccountingAnswer aca) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		logger.info("doRfAnswerEvent :: appSession[" + appSession + "], Request[" + acr + "], Answer[" + aca + "]");
	}

	public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		logger.info("Diameter Base RfountingSessionFactory :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");
	}

	// Client context
	// -----------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.Rf.IClientRfActionContext#disconnectUserOrDev
	 * (org.jdiameter.api.Request)
	 */
	public void disconnectUserOrDev(ClientRfSession appSession, Request sessionTermRequest) throws InternalException {
		logger.info("disconnectUserOrDev :: appSession[" + appSession + "], Request[" + sessionTermRequest + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.Rf.IClientRfActionContext#failedSendRecord
	 * (org.jdiameter.api.Request)
	 */
	public boolean failedSendRecord(ClientRfSession appSession, Request rfRequest) throws InternalException {
		logger.info("failedSendRecord :: appSession[" + appSession + "], Request[" + rfRequest + "]");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdiameter.common.api.app.acc.IClientAccActionContext#
	 * interimIntervalElapses(org.jdiameter.api.Request)
	 */
	public void interimIntervalElapses(ClientRfSession appSession, Request interimRequest) throws InternalException {
		logger.info("interimIntervalElapses :: appSession[" + appSession + "], Request[" + interimRequest + "]");
	}

	// Server context
	// -----------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.common.api.app.Rf.IServerRfActionContext#
	 * sessionTimeoutElapses(org.jdiameter.api.Rf.ServerRfSession)
	 */
	public void sessionTimeoutElapses(ServerRfSession appSession) throws InternalException {
		logger.info("sessionTimeoutElapses :: appSession[" + appSession + "]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.Rf.IServerRfActionContext#sessionTimerStarted
	 * (org.jdiameter.api.Rf.ServerRfSession,
	 * java.util.concurrent.ScheduledFuture)
	 */
	@SuppressWarnings("unchecked")
	public void sessionTimerStarted(ServerRfSession appSession, ScheduledFuture timer) throws InternalException {
		logger.info("sessionTimerStarted :: appSession[" + appSession + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.common.api.app.Rf.IServerRfActionContext#srssionTimerCanceled
	 * (org.jdiameter.api.Rf.ServerRfSession,
	 * java.util.concurrent.ScheduledFuture)
	 */
	@SuppressWarnings("unchecked")
	public void sessionTimerCanceled(ServerRfSession appSession, ScheduledFuture timer) throws InternalException {
		logger.info("sessionTimerCanceled :: appSession[" + appSession + "]");
	}


}
