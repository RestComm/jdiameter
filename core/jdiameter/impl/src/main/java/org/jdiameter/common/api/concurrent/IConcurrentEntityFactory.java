package org.jdiameter.common.api.concurrent;

import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

public interface IConcurrentEntityFactory {

  ThreadFactory newThreadFactory(String threadPoolName);

  RejectedExecutionHandler newRejectedExecutionHandler();

  <L> Callable<L> newDefaultCallable(Callable<L> runnable, IStatisticRecord... statisticRecords);

  Runnable newDefaultRunnable(Runnable runnable, IStatisticRecord... statisticRecords);
}