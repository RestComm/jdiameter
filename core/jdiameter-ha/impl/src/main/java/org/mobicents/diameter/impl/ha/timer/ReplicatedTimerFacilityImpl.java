/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.impl.ha.timer;

import java.io.Serializable;

import org.jdiameter.api.BaseSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.mobicents.cache.MobicentsCache;
import org.mobicents.cluster.DefaultMobicentsCluster;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.cluster.election.DefaultClusterElector;
import org.mobicents.diameter.impl.ha.data.ReplicatedDataSource;
import org.mobicents.timers.FaultTolerantScheduler;
import org.mobicents.timers.TimerTask;
import org.mobicents.timers.TimerTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replicated implementation of {@link ITimerFacility}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ReplicatedTimerFacilityImpl implements ITimerFacility {

  private static final Logger logger = LoggerFactory.getLogger(ReplicatedTimerFacilityImpl.class);

  private ISessionDatasource sessionDataSource;
  private TimerTaskFactory taskFactory;
  private FaultTolerantScheduler ftScheduler;

  public ReplicatedTimerFacilityImpl(IContainer container) {
    super();
    this.sessionDataSource = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.taskFactory = new TimerTaskFactory();
    ReplicatedDataSource rds = (ReplicatedDataSource) this.sessionDataSource;
    MobicentsCache mcCache = new MobicentsCache(rds.getJBossCache(), null);
    MobicentsCluster cluster = new DefaultMobicentsCluster(mcCache, null, new DefaultClusterElector());
    this.ftScheduler = new FaultTolerantScheduler("DiameterTimer", 5, cluster, (byte) 12, null, this.taskFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
   */
  public void cancel(Serializable id) {
    logger.debug("Cancelling timer with id {}", id);
    this.ftScheduler.cancel(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.timer.ITimerFacility#schedule(java.lang.String, java.lang.String, long)
   */
  public Serializable schedule(String sessionId, String timerName, long miliseconds) throws IllegalArgumentException {
    String id = sessionId + "/" + timerName;
    logger.debug("Scheduling timer with id {}", id);

    if (this.ftScheduler.getTimerTaskData(id) != null) {
      throw new IllegalArgumentException("Timer already running: " + id);
    }

    DiameterTimerTaskData data = new DiameterTimerTaskData(id, miliseconds, sessionId, timerName);
    TimerTask tt = this.taskFactory.newTimerTask(data);
    ftScheduler.schedule(tt);
    return id;
  }

  private final class TimerTaskFactory implements org.mobicents.timers.TimerTaskFactory {

    public TimerTask newTimerTask(TimerTaskData data) {
      return new DiameterTimerTask(data);
    }
  }

  private final class DiameterTimerTask extends TimerTask {

    public DiameterTimerTask(TimerTaskData data) {
      super(data);
    }

    public void runTask() {
      try {
        DiameterTimerTaskData data = (DiameterTimerTaskData) getData();
        BaseSession bSession = sessionDataSource.getSession(data.getSessionId());
        if (bSession == null || !bSession.isAppSession()) {
          // FIXME: error ?
          return;
        }
        else {
          AppSessionImpl impl = (AppSessionImpl) bSession;
          impl.onTimer(data.getTimerName());
        }
      }
      catch (Exception e) {
        logger.error("Failure executing timer task", e);
      }
    }
  }

}
