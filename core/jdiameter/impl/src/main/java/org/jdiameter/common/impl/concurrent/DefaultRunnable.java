package org.jdiameter.common.impl.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;
import static org.jdiameter.common.api.statistic.IStatistic.Counters.BrokenTasks;
import static org.jdiameter.common.api.statistic.IStatistic.Counters.WorkingThread;
import org.jdiameter.common.api.statistic.IStatisticRecord;

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
