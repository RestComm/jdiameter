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
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.*;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class DefaultCallable<L> extends AbstractTask<Callable<L>> implements Callable<L> {

  public DefaultCallable(Callable<L> task, IStatistic statistic, IStatisticRecord... statisticRecords) {
    super(task, statistic, statisticRecords);
  }

  public L call() throws Exception {
	  long time = 0;
	  if(statistic.isEnabled())
	  {
		  getCounter(WorkingThread).inc();
		  time = System.nanoTime();
	  }
    try {
      return parentTask.call();
    }
    catch (CancellationException e) {
    	if(statistic.isEnabled())
    		getCounter(CanceledTasks).inc();
      throw e;
    }
    catch (Exception e) {
    	if(statistic.isEnabled())
    		getCounter(BrokenTasks).inc();
      throw e;
    }
    finally {
    	if(statistic.isEnabled())
    	{
    		updateTimeStatistic(time, time - createdTime);
    		getCounter(WorkingThread).dec();
    	}
      
    }
  }
}
