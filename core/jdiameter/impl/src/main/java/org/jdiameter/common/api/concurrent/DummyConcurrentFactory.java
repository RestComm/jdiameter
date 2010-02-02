package org.jdiameter.common.api.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DummyConcurrentFactory implements IConcurrentFactory {

  public Thread getThread(Runnable runnuble) {
    return new Thread(runnuble);
  }

  public Thread getThread(String namePrefix, Runnable runnuble) {
    return new Thread(runnuble, namePrefix);
  }

  public List<Thread> getThreads() {
    return new ArrayList<Thread>();
  }

  public ThreadGroup getThreadGroup() {
    return null;
  }

  public ScheduledExecutorService getScheduledExecutorService(String name) {
    return Executors.newScheduledThreadPool(4);
  }

  public Collection<ScheduledExecutorService> getScheduledExecutorServices() {
    return new ArrayList<ScheduledExecutorService>();
  }

  public void shutdownNow(ScheduledExecutorService service) {
  }

  public IStatistic getStatistic() {
    return null;
  }

  public IStatistic[] getStatistics() {
    return new IStatistic[0];
  }

  public void shutdownAllNow() {
  }
}
