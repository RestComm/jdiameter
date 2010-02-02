package org.jdiameter.common.impl.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
  private static final Logger log = LoggerFactory.getLogger(DefaultRejectedExecutionHandler.class);

  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    log.debug("Task rejected {}", r);
  }
}