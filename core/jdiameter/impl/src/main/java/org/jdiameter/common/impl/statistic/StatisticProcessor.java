package org.jdiameter.common.impl.statistic;

import org.jdiameter.api.Configuration;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.StatisticTimer;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.api.statistic.IStatisticProcessor;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticProcessor implements IStatisticProcessor {

  private static final Logger log = LoggerFactory.getLogger(StatisticProcessor.class);

  private ScheduledExecutorService executorService;
  private IConcurrentFactory concurrentFactory;
  private IStatisticFactory statisticFactory;
  private Configuration config;

  public StatisticProcessor(Configuration config, IConcurrentFactory concurrentFactory, final IStatisticFactory statisticFactory) {
    this.config = config;
    this.statisticFactory = statisticFactory;
    this.concurrentFactory = concurrentFactory;
  }

  public void start() {
    this.executorService = concurrentFactory.getScheduledExecutorService(StatisticTimer.name());
    new StatisticLogger((StatisticFactory) statisticFactory, executorService, config);

    executorService.scheduleAtFixedRate(new Runnable() {
      public void run() {
        try {
          for (IStatisticRecord r : ((StatisticFactory) statisticFactory).allPSStatisticRecord) {
            r.setLongValue(r.getChilds()[0].getValueAsLong() - r.getChilds()[1].getValueAsLong());
            ((IStatisticRecord) r.getChilds()[1]).setLongValue(r.getChilds()[0].getValueAsLong());
          }
        }
        catch (Exception e) {
          log.warn("Can not start persecond statistic", e);
        }
      }
    }, 0, 1, TimeUnit.SECONDS);
  }

  public void stop() {
    concurrentFactory.shutdownNow(executorService);
  }
}
