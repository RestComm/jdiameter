package org.jdiameter.common.impl.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class BaseThreadFactory implements ThreadFactory {

  public static final String ENTITY_NAME = "ThreadGroup";

  private ThreadGroup threadGroup;
  private String threadPoolName;
  private AtomicInteger count = new AtomicInteger(0);

  public BaseThreadFactory(String threadPoolName) {
    this.threadPoolName = threadPoolName;

    this.threadGroup = new ThreadGroup("jd " + threadPoolName + " group");
  }

  public Thread newThread(Runnable runnable) {
    return new Thread(threadGroup, runnable, threadPoolName + "-" + count.getAndIncrement());
  }

  public Thread newThread(String namePrefix, Runnable runnable) {
    return new Thread(threadGroup, runnable, namePrefix + "-" + count.getAndIncrement());
  }

  public ThreadGroup getThreadGroup() {
    return threadGroup;
  }

}
