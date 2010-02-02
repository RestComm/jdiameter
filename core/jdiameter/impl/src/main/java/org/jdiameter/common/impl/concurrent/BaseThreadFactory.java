package org.jdiameter.common.impl.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class BaseThreadFactory implements ThreadFactory {

  public static final String ENTITY_NAME = "ThreadGroup";

  private ThreadGroup threadGroup;
  private String threadPoolName;
  private AtomicInteger count = new AtomicInteger(0);
  private int maxCount;

  public BaseThreadFactory(String threadPoolName) {
    this.threadPoolName = threadPoolName;
    this.maxCount = Integer.MAX_VALUE;
    this.threadGroup = new ThreadGroup("jd " + threadPoolName + " group");
  }

  public BaseThreadFactory(String threadPoolName, int maxCount) {
    this(threadPoolName);
    this.maxCount = maxCount;
  }

  public Thread newThread(Runnable runnable) {
    if (count.get() > maxCount) {
      throw new RuntimeException("Can not create thread (count=" + count.get() + ", max=" + maxCount + ")");
    }
    return new Thread(threadGroup, runnable, threadPoolName + "-" + count.getAndIncrement());
  }

  public Thread newThread(String namePrefix, Runnable runnable) {
    if (count.get() > maxCount) {
      throw new RuntimeException("Can not create thread (count=" + count.get() + ", max=" + maxCount + ")");
    }
    return new Thread(threadGroup, runnable, namePrefix + "-" + count.getAndIncrement());
  }

  public ThreadGroup getThreadGroup() {
    return threadGroup;
  }

}
