package org.jdiameter.common.impl.concurrent;

import org.jdiameter.common.api.concurrent.IConcurrentEntityFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

class EntityFactory implements IConcurrentEntityFactory {

  private IStatistic statistic;

  EntityFactory(IStatistic statistic) {
    this.statistic = statistic;
  }

  public ThreadFactory newThreadFactory(String threadPoolName) {
    return new BaseThreadFactory(threadPoolName);
  }

  public RejectedExecutionHandler newRejectedExecutionHandler() {
    return new DefaultRejectedExecutionHandler();
  }

  public <L> Callable<L> newDefaultCallable(Callable<L> runnable, IStatisticRecord... statisticRecords) {
    return new DefaultCallable<L>(runnable, statistic, statisticRecords);
  }

  public Runnable newDefaultRunnable(Runnable runnable, IStatisticRecord... statisticRecords) {
    return new DefaultRunnable(runnable, statistic, statisticRecords);
  }
}
