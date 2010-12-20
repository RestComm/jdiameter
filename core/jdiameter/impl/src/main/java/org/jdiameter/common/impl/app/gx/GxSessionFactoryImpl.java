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
package org.jdiameter.common.impl.app.gx;

import java.util.concurrent.ScheduledFuture;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.ClientGxSessionListener;
import org.jdiameter.api.gx.ServerGxSession;
import org.jdiameter.api.gx.ServerGxSessionListener;
import org.jdiameter.api.gx.events.GxCreditControlAnswer;
import org.jdiameter.api.gx.events.GxCreditControlRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.gx.ClientGxSessionImpl;
import org.jdiameter.common.api.app.gx.IClientGxSessionContext;
import org.jdiameter.common.api.app.gx.IGxMessageFactory;
import org.jdiameter.common.api.app.gx.IGxSessionFactory;
import org.jdiameter.common.api.app.gx.IServerGxSessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.server.impl.app.gx.ServerGxSessionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Gx Session Factory implementation.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:carl-magnus.bjorkell@emblacom.com"> Carl-Magnus Björkell </a>
 */
public class GxSessionFactoryImpl implements IGxSessionFactory, ClientGxSessionListener, ServerGxSessionListener, StateChangeListener<AppSession>,
      IGxMessageFactory, IServerGxSessionContext, IClientGxSessionContext {

    // Message timeout value (in milliseconds)
    protected int defaultDirectDebitingFailureHandling = 0;
    protected int defaultCreditControlFailureHandling = 0;
    // its seconds
    protected long defaultValidityTime = 60;
    protected long defaultTxTimerValue = 30;
    // local not replicated listeners:
    protected ClientGxSessionListener clientSessionListener;
    protected ServerGxSessionListener serverSessionListener;
    protected StateChangeListener<AppSession> stateListener;
    protected IServerGxSessionContext serverContextListener;
    protected IClientGxSessionContext clientContextListener;
    protected IGxMessageFactory messageFactory;
    protected Logger logger = LoggerFactory.getLogger(GxSessionFactoryImpl.class);
    protected ISessionDatasource iss;
    protected ISessionFactory sessionFactory = null;

    public GxSessionFactoryImpl(SessionFactory sessionFactory) {
        super();

        this.sessionFactory = (ISessionFactory) sessionFactory;
        this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    }

    public GxSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultCreditControlFailureHandling,
                                long defaultValidityTime, long defaultTxTimerValue) {
        this(sessionFactory);

        this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
        this.defaultCreditControlFailureHandling = defaultCreditControlFailureHandling;
        this.defaultValidityTime = defaultValidityTime;
        this.defaultTxTimerValue = defaultTxTimerValue;
    }

    /**
     * @return the clientSessionListener
     */
    public ClientGxSessionListener getClientSessionListener() {
        if (clientSessionListener != null) {
            return clientSessionListener;
        } else {
            return this;
        }
    }

    /**
     * @param clientSessionListener
     *          the clientSessionListener to set
     */
    public void setClientSessionListener(final ClientGxSessionListener clientSessionListener) {
        this.clientSessionListener = clientSessionListener;
    }

    /**
     * @return the serverSessionListener
     */
    public ServerGxSessionListener getServerSessionListener() {
        if (serverSessionListener != null) {
            return serverSessionListener;
        } else {
            return this;
        }
    }

    /**
     * @param serverSessionListener
     *          the serverSessionListener to set
     */
    public void setServerSessionListener(ServerGxSessionListener serverSessionListener) {
        this.serverSessionListener = serverSessionListener;
    }

    /**
     * @return the serverContextListener
     */
    public IServerGxSessionContext getServerContextListener() {
        if (serverContextListener != null) {
            return serverContextListener;
        } else {
            return this;
        }
    }

    /**
     * @param serverContextListener
     *          the serverContextListener to set
     */
    public void setServerContextListener(IServerGxSessionContext serverContextListener) {
        this.serverContextListener = serverContextListener;
    }

    /**
     * @return the clientContextListener
     */
    public IClientGxSessionContext getClientContextListener() {
        if (clientContextListener != null) {
            return clientContextListener;
        } else {
            return this;
        }
    }

    /**
     * @return the messageFactory
     */
    public IGxMessageFactory getMessageFactory() {
        if (messageFactory != null) {
            return messageFactory;
        } else {
            return this;
        }
    }

    /**
     * @param messageFactory
     *          the messageFactory to set
     */
    public void setMessageFactory(final IGxMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * @param clientContextListener
     *          the clientContextListener to set
     */
    public void setClientContextListener(IClientGxSessionContext clientContextListener) {
        this.clientContextListener = clientContextListener;
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * @param sessionFactory
     *          the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = (ISessionFactory) sessionFactory;
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
     *          the stateListener to set
     */
    public void setStateListener(StateChangeListener<AppSession> stateListener) {
        this.stateListener = stateListener;
    }

    public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
        AppSession appSession = null;
        try {
            // FIXME:
            if (aClass == ClientGxSession.class) {
                ClientGxSessionImpl clientSession = null;
                if (args != null && args.length > 0 && args[0] instanceof Request) {
                    Request request = (Request) args[0];
                    clientSession = new ClientGxSessionImpl(request.getSessionId(), this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());
                } else {
                    clientSession = new ClientGxSessionImpl(sessionId, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());
                }
                // this goes first!
                iss.addSession(clientSession);
                // iss.setSessionListener(clientSession.getSessionId(),
                // (NetworkReqListener) appSession);
                clientSession.getSessions().get(0).setRequestListener(clientSession);
                // clientSession.addStateChangeNotification(this);

                // this.resourceAdaptor.sessionCreated(clientSession);

                appSession = clientSession;
            } else if (aClass == ServerGxSession.class) {
                ServerGxSessionImpl serverSession = null;

                if (args != null && args.length > 0 && args[0] instanceof Request) {
                    // This shouldnt happen but just in case
                    Request request = (Request) args[0];
                    serverSession = new ServerGxSessionImpl(request.getSessionId(), this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());
                } else {
                    serverSession = new ServerGxSessionImpl(sessionId, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());
                }
                iss.addSession(serverSession);
                // iss.setSessionListener(serverSession.getSessionId(),
                // (NetworkReqListener) appSession);
                serverSession.getSessions().get(0).setRequestListener(serverSession);
                // serverSession.addStateChangeNotification(this);

                // this.resourceAdaptor.sessionCreated(serverSession);

                appSession = serverSession;
            } else {
                throw new IllegalArgumentException("Wrong session class!![" + aClass + "]. Supported[" + ClientGxSession.class + "," + ServerGxSession.class + "]");
            }
        } catch (Exception e) {
            logger.error("Failure to obtain new Credit-Control Session.", e);
        }

        return appSession;
    }

    // default implementation of methods so there are no exception!
    // ------------------------------------------------
    // Message Handlers ---------------------------------------------------------
    public void doCreditControlRequest(ServerGxSession session, GxCreditControlRequest request) throws InternalException {
    }

    public void doCreditControlAnswer(ClientGxSession session, GxCreditControlRequest request, GxCreditControlAnswer answer) throws InternalException {
    }

    public void doReAuthRequest(ClientGxSession session, ReAuthRequest request) throws InternalException {
    }

    public void doReAuthAnswer(ServerGxSession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException {
    }

    public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
    }

    // Message Factory Methods --------------------------------------------------
    public GxCreditControlAnswer createCreditControlAnswer(Answer answer) {
        return new GxCreditControlAnswerImpl(answer);
    }

    public GxCreditControlRequest createCreditControlRequest(Request req) {
        return new GxCreditControlRequestImpl(req);
    }

    public ReAuthAnswer createReAuthAnswer(Answer answer) {
        return new ReAuthAnswerImpl(answer);
    }

    public ReAuthRequest createReAuthRequest(Request req) {
        return new ReAuthRequestImpl(req);
    }

    // Context Methods ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void stateChanged(Enum oldState, Enum newState) {
        logger.info("Diameter Ro SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
     */
    @SuppressWarnings("unchecked")
    public void stateChanged(AppSession source, Enum oldState, Enum newState) {
        logger.info("Diameter Ro SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
    }

    // FIXME: add ctx methods proxy calls!
    public void sessionSupervisionTimerExpired(ServerGxSession session) {
        // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
        session.release();
    }

    @SuppressWarnings("unchecked")
    public void sessionSupervisionTimerReStarted(ServerGxSession session, ScheduledFuture future) {
        // TODO Complete this method.
    }

    @SuppressWarnings("unchecked")
    public void sessionSupervisionTimerStarted(ServerGxSession session, ScheduledFuture future) {
        // TODO Complete this method.
    }

    @SuppressWarnings("unchecked")
    public void sessionSupervisionTimerStopped(ServerGxSession session, ScheduledFuture future) {
        // TODO Complete this method.
    }

    public void timeoutExpired(Request request) {
        // FIXME What should we do when there's a timeout?
    }

    public void denyAccessOnDeliverFailure(ClientGxSession clientGxSessionImpl, Message request) {
        // TODO Complete this method.
    }

    public void denyAccessOnFailureMessage(ClientGxSession clientGxSessionImpl) {
        // TODO Complete this method.
    }

    public void denyAccessOnTxExpire(ClientGxSession clientGxSessionImpl) {
        // this.resourceAdaptor.sessionDestroyed(clientGxSessionImpl.getSessions().get(0).getSessionId(),
        // clientGxSessionImpl);
        clientGxSessionImpl.release();
    }

    public int getDefaultCCFHValue() {
        return defaultCreditControlFailureHandling;
    }

    public int getDefaultDDFHValue() {
        return defaultDirectDebitingFailureHandling;
    }

    public long getDefaultTxTimerValue() {
        return defaultTxTimerValue;
    }

    public void grantAccessOnDeliverFailure(ClientGxSession clientGxSessionImpl, Message request) {
        // TODO Auto-generated method stub
    }

    public void grantAccessOnFailureMessage(ClientGxSession clientGxSessionImpl) {
        // TODO Auto-generated method stub
    }

    public void grantAccessOnTxExpire(ClientGxSession clientGxSessionImpl) {
        // TODO Auto-generated method stub
    }

    public void indicateServiceError(ClientGxSession clientGxSessionImpl) {
        // TODO Auto-generated method stub
    }

    public void txTimerExpired(ClientGxSession session) {
        // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
        session.release();
    }

    public long[] getApplicationIds() {
        // FIXME: What should we do here?
        return new long[]{4};
    }

    public long getDefaultValidityTime() {
        return this.defaultValidityTime;
    }
}
