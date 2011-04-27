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

package org.mobicents.diameter.impl.ha.timer;

import java.io.Serializable;

import org.mobicents.timers.PeriodicScheduleStrategy;
import org.mobicents.timers.TimerTaskData;

/**
 * Diameter timer task data holder.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
final class DiameterTimerTaskData extends TimerTaskData {

  private static final long serialVersionUID = 8774218122384404225L;

  // data we need to recreate timer task
  private String sessionId;
  private String timerName;

  public DiameterTimerTaskData(Serializable id, long delay, String sessionId, String timerName) {
    super(id, System.currentTimeMillis() + delay, -1, PeriodicScheduleStrategy.withFixedDelay);
    this.sessionId = sessionId;
    this.timerName = timerName;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getTimerName() {
    return timerName;
  }
}
