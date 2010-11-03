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
package org.jdiameter.common.api.app.ro;

import java.util.concurrent.ScheduledFuture;

import org.jdiameter.api.Request;
import org.jdiameter.api.ro.ServerRoSession;

/**
 * Diameter Credit Control Application Server Additional listener
 * Actions for FSM
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface IServerRoSessionContext {

  public void sessionSupervisionTimerExpired(ServerRoSession session);

  /**
   * This is called always when Tcc starts
   * @param session
   * @param future
   */
  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStarted(ServerRoSession session, ScheduledFuture future);

  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerReStarted(ServerRoSession session, ScheduledFuture future);

  @SuppressWarnings("unchecked")
  public void sessionSupervisionTimerStopped(ServerRoSession session, ScheduledFuture future);

  /**
   * Returns seconds value representing default validity time, App session uses 2x for Tcc timer
   * @return
   */
  public long getDefaultValidityTime();

  public void timeoutExpired(Request request);

}
