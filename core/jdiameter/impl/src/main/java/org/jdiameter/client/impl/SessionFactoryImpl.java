package org.jdiameter.client.impl;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.RawSession;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.StackState;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionFactoryImpl implements ISessionFactory {

    IContainer stack;
    Map<Class, IAppSessionFactory> appFactories = new ConcurrentHashMap<Class, IAppSessionFactory>();

    public SessionFactoryImpl(IContainer stack) {
        this.stack = stack;
    }

    public RawSession getNewRawSession() throws InternalException {
        if (stack.getState() == StackState.IDLE)
            throw new InternalException("Illegal state of stack");
        return new RawSessionImpl(stack);
    }

    public Session getNewSession() throws InternalException {
        if (stack.getState() == StackState.IDLE)
            throw new InternalException("Illegal state of stack");
        return new SessionImpl(stack);
    }

    public Session getNewSession(String sessionId) throws InternalException {
        if (stack.getState() == StackState.IDLE)
            throw new InternalException("Illegal state of stack");
        SessionImpl session = new SessionImpl(stack);
        if (sessionId != null && sessionId.length() > 0)
            session.sessionId = sessionId;
        return session;
    }

    public <T extends AppSession> T getNewAppSession(ApplicationId applicationId, Class<? extends AppSession> aClass) throws InternalException {
        return (T) getNewAppSession(null, applicationId, aClass, new Object[0]);
    }

    public <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, Class<? extends AppSession> aClass) throws InternalException {
        return (T) getNewAppSession(sessionId, applicationId,aClass, new Object[0]);
    }

    public <T extends AppSession> T getNewAppSession(String sessionId,  ApplicationId applicationId, Class<? extends AppSession> aClass, Object... args) throws InternalException {
        if (stack.getState() == StackState.IDLE)
            throw new InternalException("Illegal state of stack");
        if (appFactories.containsKey(aClass))
            return (T) ((IAppSessionFactory) appFactories.get(aClass)).getNewSession(sessionId, aClass, applicationId, args);
        return null;
    }

    public void registerAppFacory(Class<? extends AppSession> sessionClass, IAppSessionFactory factory) {
        appFactories.put(sessionClass, factory);
    }

    public void unRegisterAppFacory(Class<? extends AppSession> sessionClass) {
        appFactories.remove(sessionClass);
    }

	public IConcurrentFactory getConcurrentFactory() {
		if(stack != null)
		{
			return stack.getConcurrentFactory();
		}
		
		return null;
	}
}
