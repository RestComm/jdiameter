/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.diameter.stack;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.BaseSession;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Mode;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.StackState;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

public class DiameterStackProxy implements Stack, IContainer {

  protected Stack realStack = null;

  public DiameterStackProxy(Stack realStack) {
    super();
    this.realStack = realStack;
  }

  public void destroy() {
    this.realStack.destroy();
  }

  public Logger getLogger() {
    return this.realStack.getLogger();
  }

  public MetaData getMetaData() {
    return this.realStack.getMetaData();
  }

  public SessionFactory getSessionFactory() throws IllegalDiameterStateException {
    return this.realStack.getSessionFactory();
  }

  public <T extends BaseSession> T getSession(String sessionId, Class<T> clazz) throws InternalException {
    return this.realStack.getSession(sessionId, clazz);
  }

  public SessionFactory init( Configuration config ) throws IllegalDiameterStateException, InternalException {
    return this.realStack.init( config );
  }

  public boolean isActive() {
    return this.realStack.isActive();
  }

  public void start() throws IllegalDiameterStateException, InternalException {
    this.realStack.start();    
  }

  public void start(Mode mode, long timeout, TimeUnit unit) throws IllegalDiameterStateException, InternalException {
    this.realStack.start( mode, timeout, unit );
  }

  public void stop( long timeout, TimeUnit unit ) throws IllegalDiameterStateException, InternalException {
    this.realStack.stop( timeout, unit );    
  }

  public boolean isWrapperFor( Class<?> iface ) throws InternalException {
    return this.realStack.isWrapperFor( iface );
  }

  public <T> T unwrap( Class<T> iface ) throws InternalException {
    return this.realStack.unwrap( iface );
  }

  public void addSessionListener(String sessionId, NetworkReqListener listener) {
    ((IContainer)realStack).addSessionListener(sessionId, listener);
  }

  public IConcurrentFactory getConcurrentFactory() {
    return ((IContainer)realStack).getConcurrentFactory();
  }

  public Configuration getConfiguration() {
    return ((IContainer)realStack).getConfiguration();
  }

  public ScheduledExecutorService getScheduledFacility() {
    return ((IContainer)realStack).getScheduledFacility();
  }

  public StackState getState() {
    return ((IContainer)realStack).getState();
  }

  public void removeSessionListener(String sessionId) {
    ((IContainer)realStack).removeSessionListener(sessionId);
  }

  public void sendMessage(IMessage session) throws RouteException, AvpDataException, IllegalDiameterStateException, IOException {
    ((IContainer)realStack).sendMessage(session);
  }

  public IAssembler getAssemblerFacility() {
    return ((IContainer)realStack).getAssemblerFacility();
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.Stack#getDictionary()
   */
  public Dictionary getDictionary() throws IllegalDiameterStateException {
    return realStack.getDictionary();
  }

}
