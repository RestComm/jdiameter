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

package org.jdiameter.common.impl.concurrent;

import static org.jdiameter.common.api.statistic.IStatistic.Groups.ScheduledExecService;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.BrokenTasks;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.CanceledTasks;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.RejectedTasks;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.WaitTimeTask;
import static org.jdiameter.common.api.statistic.IStatisticRecord.Counters.WorkingThread;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentEntityFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class CommonScheduledExecutorService extends ScheduledThreadPoolExecutor {

  private IStatistic statistic;
  private IConcurrentEntityFactory entityFactory;
  private IStatisticRecord execTimeSumm;
  private IStatisticRecord execTimeCount;
  private IStatisticRecord waitTimeSumm;
  private IStatisticRecord waitTimeCount;
  private IStatisticManager statisticFactory;

  CommonScheduledExecutorService(String name, Configuration config, final IConcurrentEntityFactory entityFactory, IStatisticManager statisticFactory) {
    super(config == null ? (Integer) Parameters.ConcurrentEntityPoolSize.defValue() : config.getIntValue(Parameters.ConcurrentEntityPoolSize.ordinal(),
        (Integer) Parameters.ConcurrentEntityPoolSize.defValue()));
    this.statisticFactory = statisticFactory;
    this.entityFactory = entityFactory;
    final IStatisticRecord rejectedCount = statisticFactory.newCounterRecord(RejectedTasks);
    execTimeSumm = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm");
    execTimeCount = statisticFactory.newCounterRecord("TimeCount", "TimeCount");
    waitTimeSumm = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm");
    waitTimeCount = statisticFactory.newCounterRecord("TimeCount", "TimeCount");
    //XXX: YYY: no need to remove? it lives as long stack does.
    statistic = statisticFactory.newStatistic(name, ScheduledExecService, rejectedCount);

    final IStatisticRecord execTimeCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.ExecTimeTask, new AbstractTask.AverageValueHolder(
        statistic, IStatisticRecord.Counters.ExecTimeTask), execTimeSumm, execTimeCount);

    final IStatisticRecord waitTimeCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.WaitTimeTask, new AbstractTask.AverageValueHolder(
        statistic, IStatisticRecord.Counters.WaitTimeTask), waitTimeSumm, waitTimeCount);

    statistic.appendCounter(statisticFactory.newCounterRecord(WorkingThread), statisticFactory.newCounterRecord(CanceledTasks),
        statisticFactory.newCounterRecord(BrokenTasks), execTimeCounter, waitTimeCounter, statisticFactory.newCounterRecord(WaitTimeTask));

    if (config == null) {
      this.setThreadFactory(entityFactory.newThreadFactory(name));
    } else {
      this.setThreadFactory(entityFactory.newThreadFactory(config.getStringValue(Parameters.ConcurrentEntityDescription.ordinal(), name)));
    }

    super.setRejectedExecutionHandler(entityFactory.newRejectedExecutionHandler(rejectedCount));
  }


  @Override
  public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
    return super.schedule(this.entityFactory.newDefaultRunnable(runnable, statistic, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount), delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return super.schedule(this.entityFactory.newDefaultCallable(callable, statistic, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount), delay, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
    return super.scheduleAtFixedRate(this.entityFactory.newDefaultRunnable(runnable, statistic, execTimeSumm, execTimeCount, waitTimeSumm, waitTimeCount),
        initialDelay, period, unit);
  }

  public IStatistic getStatistic() {
    return statistic;
  }

  @Override
  public void shutdown() {
    this.statisticFactory.removeStatistic(statistic);
    super.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    this.statisticFactory.removeStatistic(statistic);
    return super.shutdownNow();
  }

}
