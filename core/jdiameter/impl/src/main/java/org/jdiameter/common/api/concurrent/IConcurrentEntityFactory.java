package org.jdiameter.common.api.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;

public interface IConcurrentEntityFactory {

  ThreadFactory newThreadFactory(String threadPoolName);

  RejectedExecutionHandler newRejectedExecutionHandler(IStatisticRecord rejectedCount);

  //TODO: get rid of those.
  <L> Callable<L> newDefaultCallable(Callable<L> runnable,IStatistic statistic , IStatisticRecord... statisticRecords);

  Runnable newDefaultRunnable(Runnable runnable, IStatistic statistic, IStatisticRecord... statisticRecords);
  
}