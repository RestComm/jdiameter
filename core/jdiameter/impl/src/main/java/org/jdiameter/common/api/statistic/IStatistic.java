/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.api.statistic;

import org.jdiameter.api.Statistic;

/**
 * This interface describe extends methods of base class
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IStatistic extends Statistic{

  enum Groups {
    Peer("Peer statistic"),
    PeerFSM("Peer FSM statistic"),
    Network("Network statistic"),
    Concurrent(" Concurrent factory statistics"),
    ScheduledExecService("ScheduledExecutorService statistic");

    private String description;

    Groups(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /**
   * Merge statistic
   *
   * @param rec external statistic
   */
  public void appendCounter(IStatisticRecord... rec);

  public IStatisticRecord getRecordByName(String name);

  public IStatisticRecord getRecordByName(IStatisticRecord.Counters name);

}
