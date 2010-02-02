package org.jdiameter.common.impl.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;
import static org.jdiameter.common.api.statistic.IStatistic.Counters.*;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

class DefaultCallable<L> extends AbstractTask<Callable<L>> implements Callable<L> {

  public DefaultCallable(Callable<L> task, IStatistic statistic, IStatisticRecord... statisticRecords) {
    super(task, statistic, statisticRecords);
  }

  public L call() throws Exception {
    getCounter(WorkingThread).inc();
    long time = System.nanoTime();
    try {
      return parentTask.call();
    }
    catch (CancellationException e) {
      getCounter(CanceledTasks).inc();
      throw e;
    }
    catch (Exception e) {
      getCounter(BrokenTasks).inc();
      throw e;
    }
    finally {
      updateTimeStatistic(time, time - createdTime);
      getCounter(WorkingThread).dec();
    }
  }
}
