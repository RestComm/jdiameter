package org.jdiameter.common.impl.statistic;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticLogger {

  private static final String STATS_ROOT_LOGGER_NAME = "jdiameter.statistic";
  private static final String STATS_LOGGER_PREFIX = "jdiameter.statistic.";
  
  public StatisticLogger(final StatisticFactory factory, ScheduledExecutorService concurrentFactory, Configuration config) {

    long pause = (Long) Parameters.StatisticLoggerPause.defValue();
    long delay = (Long) Parameters.StatisticLoggerDelay.defValue();
    Configuration[] loggerParams = config.getChildren(Parameters.StatisticLogger.ordinal());
    if (loggerParams != null && loggerParams.length > 0) {
      pause = loggerParams[0].getLongValue(Parameters.StatisticLoggerPause.ordinal(), pause);
      delay = loggerParams[0].getLongValue(Parameters.StatisticLoggerDelay.ordinal(), delay);
    }

    concurrentFactory.scheduleAtFixedRate(new Runnable() {
      
      HashMap<String, Logger> loggers = new HashMap<String, Logger>();

      public void run() {
        boolean oneLine = false;
        for (IStatistic statistic : factory.allStatistic) {
          if (statistic.isEnable()) {
            for (IStatisticRecord record : statistic.getRecords()) {
              oneLine = true;
              String loggerKey = statistic.getName() + "." + record.getName();
              Logger logger = null;
              if((logger = loggers.get(loggerKey)) == null) {
                logger = LoggerFactory.getLogger(STATS_LOGGER_PREFIX + loggerKey);
                loggers.put(loggerKey, logger);
              }
              if(logger.isTraceEnabled()) {
                logger.trace(record.toString());
              }
            }
          }
        }
        if (oneLine) {
          Logger logger = null;
          if((logger = loggers.get(STATS_ROOT_LOGGER_NAME)) == null) {
            logger = LoggerFactory.getLogger(STATS_ROOT_LOGGER_NAME);
            loggers.put(STATS_ROOT_LOGGER_NAME, logger);
          }
          if(logger.isTraceEnabled()) {
            logger.trace("=============================================== Marker ===============================================");
          }
        }
      }
    }, pause, delay, TimeUnit.MILLISECONDS);
  }

}
