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
package org.jdiameter.client.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.RawSession;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.StackState;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;

/**
 * Implementation for {@link ISessionFactory}
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SessionFactoryImpl implements ISessionFactory {

  private IContainer stack;

  @SuppressWarnings("rawtypes")
  private Map<Class, IAppSessionFactory> appFactories = new ConcurrentHashMap<Class, IAppSessionFactory>();
  private ISessionDatasource dataSource;

  public SessionFactoryImpl(IContainer stack) {
    this.stack = stack;
    this.dataSource = this.stack.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  public RawSession getNewRawSession() throws InternalException {
    if (stack.getState() == StackState.IDLE) {
      throw new InternalException("Illegal state of stack");
    }
    return new RawSessionImpl(stack);
  }

  public Session getNewSession() throws InternalException {
    if (stack.getState() == StackState.IDLE) {
      throw new InternalException("Illegal state of stack");
    }

    // FIXME: store this! Properly handle in ISessiondata
    SessionImpl session = new SessionImpl(stack); 
    this.dataSource.addSession(session);
    return session;
  }

  public Session getNewSession(String sessionId) throws InternalException {
    if (stack.getState() == StackState.IDLE) {
      throw new InternalException("Illegal state of stack");
    }
    SessionImpl session = new SessionImpl(stack);
    if (sessionId != null && sessionId.length() > 0) {
      session.sessionId = sessionId;
    }
    // FIXME: store this! Properly handle in ISessiondata
    this.dataSource.addSession(session);
    return session;
  }

  @SuppressWarnings("unchecked")
  public <T extends AppSession> T getNewAppSession(ApplicationId applicationId, Class<? extends AppSession> aClass) throws InternalException {
    return (T) getNewAppSession(null, applicationId, aClass, new Object[0]);
  }

  @SuppressWarnings("unchecked")
  public <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, Class<? extends AppSession> aClass) throws InternalException {
    return (T) getNewAppSession(sessionId, applicationId,aClass, new Object[0]);
  }

  @SuppressWarnings("unchecked")
  public <T extends AppSession> T getNewAppSession(String sessionId,  ApplicationId applicationId, Class<? extends AppSession> aClass, Object... args) throws InternalException {
    T session = null;
    if (stack.getState() == StackState.IDLE)
      throw new InternalException("Illegal state of stack");
    if (appFactories.containsKey(aClass)) {
      session = (T) ((IAppSessionFactory) appFactories.get(aClass)).getNewSession(sessionId, aClass, applicationId, args);
      // FIXME: add check if it exists already!
      //dataSource.addSession(session);
    }
    return session;
  }

  public void registerAppFacory(Class<? extends AppSession> sessionClass, IAppSessionFactory factory) {
    appFactories.put(sessionClass, factory);
  }

  public void unRegisterAppFacory(Class<? extends AppSession> sessionClass) {
    appFactories.remove(sessionClass);
  }

  /* (non-Javadoc)
   * @see org.jdiameter.client.api.ISessionFactory#getAppSessionFactory(java.lang.Class)
   */
  public IAppSessionFactory getAppSessionFactory(Class<? extends AppSession> sessionClass) {
    return appFactories.get(sessionClass);
  }

  public IContainer getContainer() {
    return stack;
  }

}
