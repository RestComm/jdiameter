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
