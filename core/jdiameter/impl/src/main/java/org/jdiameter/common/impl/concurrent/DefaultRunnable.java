/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.impl.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.BrokenTasks;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.WorkingThread;
import org.jdiameter.common.api.statistic.IStatisticRecord;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class DefaultRunnable extends AbstractTask<Runnable> implements Runnable {

  public DefaultRunnable(Runnable task, IStatistic statistic, IStatisticRecord... statisticRecords) {
    super(task, statistic, statisticRecords);
  }

  public void run() {
    if (getCounter(WorkingThread) != null) {
      getCounter(WorkingThread).inc();
    }
    long time = System.nanoTime();
    try {
      parentTask.run();
    }
    catch (RuntimeException e) {
      if (getCounter(BrokenTasks) != null) {
        getCounter(BrokenTasks).inc();
      }
      throw e;
    }
    finally {
      updateTimeStatistic(time, time - createdTime);
      if (getCounter(WorkingThread) != null) {
        getCounter(WorkingThread).dec();
      }
    }
  }
}
