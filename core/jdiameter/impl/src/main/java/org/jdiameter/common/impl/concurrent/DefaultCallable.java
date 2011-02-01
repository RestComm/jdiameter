package org.jdiameter.common.impl.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.*;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

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
