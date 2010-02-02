package org.jdiameter.common.impl.statistic;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticLogger {

  public StatisticLogger(final StatisticFactory factory, ScheduledExecutorService concurrentFactory, Configuration config) {

    long pause = (Long) Parameters.StatisticLoggerPause.defValue();
    long delay = (Long) Parameters.StatisticLoggerDelay.defValue();
    Configuration[] loggerParams = config.getChildren(Parameters.StatisticLogger.ordinal());
    if (loggerParams != null && loggerParams.length > 0) {
      pause = loggerParams[0].getLongValue(Parameters.StatisticLoggerPause.ordinal(), pause);
      delay = loggerParams[0].getLongValue(Parameters.StatisticLoggerDelay.ordinal(), delay);
    }

    concurrentFactory.scheduleAtFixedRate(new Runnable() {

      public void run() {
        boolean oneLine = false;
        for (IStatistic statistic : factory.allStatistic) {
          if (statistic.isEnable()) {
            for (IStatisticRecord record : statistic.getRecords()) {
              oneLine = true;
              LoggerFactory.getLogger("jdiameter.statistic." + statistic.getName() + "." +
                  record.getName()).debug(record.toString());
            }
          }
        }
        if (oneLine) {
          LoggerFactory.getLogger("jdiameter.statistic").
          debug("=============================================== Marker ===============================================");
        }
      }
    }, pause, delay, TimeUnit.MILLISECONDS);
  }

}
