package org.jdiameter.common.impl.concurrent;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentEntityFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import static org.jdiameter.common.api.statistic.IStatistic.Counters.*;
import static org.jdiameter.common.api.statistic.IStatistic.Groups.ScheduledExecService;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.*;

class CommonScheduledExecutorService extends ScheduledThreadPoolExecutor {

  private IStatistic statistic;
  private IConcurrentEntityFactory entityFactory;
  IStatisticRecord execTimeSumm;
  IStatisticRecord execTimeCount;
  IStatisticRecord waitTimeSumm;
  IStatisticRecord waitTimeCount;

  public CommonScheduledExecutorService(String name, Configuration config, final IConcurrentEntityFactory entityFactory, IStatisticFactory statisticFactory) {
    super(config == null ? (Integer) Parameters.ConcurrentEntityPoolSize.defValue() : 
      config.getIntValue(Parameters.ConcurrentEntityPoolSize.ordinal(), (Integer) Parameters.ConcurrentEntityPoolSize.defValue()));

    this.entityFactory = entityFactory;
    final IStatisticRecord rejectedCount = statisticFactory.newCounterRecord(RejectedTasks);
    execTimeSumm = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm", 0);
    execTimeCount = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm", 0);
    waitTimeSumm = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm", 0);
    waitTimeCount = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm", 0);

    statistic = statisticFactory.newStatistic(ScheduledExecService.name() + "." + name, ScheduledExecService.getDescription(), rejectedCount);
    
    final IStatisticRecord execTimeCounter = statisticFactory.newCounterRecord(IStatistic.Counters.ExecTimeTask, 
        new AbstractTask.AverajeValueHolder(statistic, IStatistic.Counters.ExecTimeTask), execTimeSumm, execTimeCount);

    final IStatisticRecord waitTimeCounter = statisticFactory.newCounterRecord(IStatistic.Counters.WaitTimeTask, 
        new AbstractTask.AverajeValueHolder(statistic, IStatistic.Counters.WaitTimeTask), waitTimeSumm, waitTimeCount);

    statistic.appendCounter(
        statisticFactory.newCounterRecord(WorkingThread),
        statisticFactory.newCounterRecord(CanceledTasks),
        statisticFactory.newCounterRecord(BrokenTasks),
        execTimeCounter,
        waitTimeCounter,
        statisticFactory.newCounterRecord(WaitTimeTask)
    );

    if (config == null) {
      this.setThreadFactory(entityFactory.newThreadFactory(name));
    }
    else {
      this.setThreadFactory(entityFactory.newThreadFactory(config.getStringValue(Parameters.ConcurrentEntityDescription.ordinal(), name)));
    }
    this.setRejectedExecutionHandler(new RejectedExecutionHandler() {
      public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
        rejectedCount.inc();
        entityFactory.newRejectedExecutionHandler().rejectedExecution(task, executor);
      }
    });
  }

  public IStatistic getStatistic() {
    return statistic;
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
    return super.schedule(entityFactory.newDefaultRunnable(runnable, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount), delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return super.schedule(entityFactory.newDefaultCallable(callable, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount), delay, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
    return super.scheduleAtFixedRate(entityFactory.newDefaultRunnable(runnable, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount), initialDelay, period, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
    return super.scheduleWithFixedDelay(entityFactory.newDefaultRunnable(runnable, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount), initialDelay, delay, unit);
  }

  @Override
  public void execute(Runnable runnable) {
    super.execute(entityFactory.newDefaultRunnable(runnable, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount));
  }
}
