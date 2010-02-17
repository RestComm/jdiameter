package org.jdiameter.common.api.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public interface IConcurrentFactory {

  enum ScheduledExecServices {
    ProcessingMessageTimer,
    RedirectMessageTimer,
    DuplicationMessageTimer,
    PeerOverloadTimer,
    ConnectionTimer,
    StatisticTimer,
    ApplicationSession
  }

  // Thread
  Thread getThread(Runnable runnuble);

  Thread getThread(String namePrefix, Runnable runnuble);

  List<Thread> getThreads();

  ThreadGroup getThreadGroup();

  // ScheduledExecutorService
  ScheduledExecutorService getScheduledExecutorService(String name);

  Collection<ScheduledExecutorService> getScheduledExecutorServices();

  void shutdownNow(ScheduledExecutorService service);

  // Common
  IStatistic getStatistic();

  IStatistic[] getStatistics();

  void shutdownAllNow();
}
