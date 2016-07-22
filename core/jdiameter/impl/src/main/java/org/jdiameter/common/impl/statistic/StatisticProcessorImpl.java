 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.common.impl.statistic;

import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.StatisticTimer;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticProcessor;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Statistics Processor
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class StatisticProcessorImpl implements IStatisticProcessor {

  private static final Logger logger = LoggerFactory.getLogger(StatisticProcessorImpl.class);

  private ScheduledExecutorService executorService;
  private IConcurrentFactory concurrentFactory;
  private IStatisticManager statisticFactory;

  // statics for logger names
  private static final String STATS_ROOT_LOGGER_NAME = "jdiameter.statistic";
  private static final String STATS_LOGGER_PREFIX = "jdiameter.statistic.";

  // future for actions to update per second stats
  private Future<?> processorFuture;
  // future for logger runnable
  private Future<?> logFuture;

  // map of loggers, so we dont have to fetch from slf all the time
  private HashMap<String, Logger> loggers = new HashMap<String, Logger>();

  public StatisticProcessorImpl(Configuration config, IConcurrentFactory concurrentFactory, final IStatisticManager statisticFactory) {
    this.statisticFactory = statisticFactory;
    this.concurrentFactory = concurrentFactory;
  }

  @Override
  public void start() {
    if (!this.statisticFactory.isOn()) {
      return;
    }

    this.executorService = concurrentFactory.getScheduledExecutorService(StatisticTimer.name());

    // start processor
    this.processorFuture = this.executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          for (IStatisticRecord r : ((StatisticManagerImpl) statisticFactory).getPSStatisticRecord()) {
            StatisticRecord[] recs = r.getChilds();
            // magic of PS records, there are two children created...
            IStatisticRecord realRecord = (IStatisticRecord) recs[0];
            IStatisticRecord prevRecord = (IStatisticRecord) recs[1];
            r.setLongValue(realRecord.getValueAsLong() - prevRecord.getValueAsLong());
            prevRecord.setLongValue(realRecord.getValueAsLong());
          }
        }
        catch (Exception e) {
          logger.warn("Can not start persecond statistic", e);
        }
      }
    }, 0, 1, TimeUnit.SECONDS);

    // start logging actions
    this.logFuture = this.executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        boolean oneLine = false;
        for (IStatistic statistic : statisticFactory.getStatistic()) {
          if (statistic.isEnabled()) {
            for (StatisticRecord record : statistic.getRecords()) {
              oneLine = true;
              String loggerKey = statistic.getName() + "." + record.getName();
              Logger logger = null;
              if ((logger = loggers.get(loggerKey)) == null) {
                logger = LoggerFactory.getLogger(STATS_LOGGER_PREFIX + loggerKey);
                loggers.put(loggerKey, logger);
              }
              if (logger.isTraceEnabled()) {
                logger.trace(record.toString());
              }
            }
          }
        }
        if (oneLine) {
          Logger logger = null;
          if ((logger = loggers.get(STATS_ROOT_LOGGER_NAME)) == null) {
            logger = LoggerFactory.getLogger(STATS_ROOT_LOGGER_NAME);
            loggers.put(STATS_ROOT_LOGGER_NAME, logger);
          }
          if (logger.isTraceEnabled()) {
            logger.trace("=============================================== Marker ===============================================");
          }
        }
      }
    }, statisticFactory.getPause(), statisticFactory.getDelay(), TimeUnit.MILLISECONDS);
  }

  @Override
  public void stop() {
    if (!this.statisticFactory.isOn()) {
      return;
    }
    if (this.processorFuture != null) {
      this.processorFuture.cancel(false);
      this.processorFuture = null;
    }

    if (this.logFuture != null) {
      this.logFuture.cancel(false);
      this.logFuture = null;
    }
    this.concurrentFactory.shutdownNow(executorService);
  }

}
