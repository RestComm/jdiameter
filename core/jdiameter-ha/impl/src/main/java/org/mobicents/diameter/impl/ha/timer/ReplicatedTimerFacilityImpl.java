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

package org.mobicents.diameter.impl.ha.timer;

import java.io.Serializable;

import org.jdiameter.api.BaseSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.BaseSessionImpl;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.restcomm.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.restcomm.timers.FaultTolerantScheduler;
import org.restcomm.timers.TimerTask;
import org.restcomm.timers.TimerTaskData;
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
    MobicentsCluster cluster = ((ReplicatedSessionDatasource) this.sessionDataSource).getMobicentsCluster();
    this.ftScheduler = new FaultTolerantScheduler("DiameterTimer", 5, cluster, (byte) 12, null, this.taskFactory);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
   */
  @Override
  public void cancel(Serializable id) {
    logger.debug("Cancelling timer with id {}", id);
    this.ftScheduler.cancel(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.timer.ITimerFacility#schedule(java.lang.String, java.lang.String, long)
   */
  @Override
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

  private final class TimerTaskFactory implements org.restcomm.timers.TimerTaskFactory {

    @Override
    public TimerTask newTimerTask(TimerTaskData data) {
      return new DiameterTimerTask(data);
    }
  }

  private final class DiameterTimerTask extends TimerTask {

    DiameterTimerTask(TimerTaskData data) {
      super(data);
    }

    @Override
    public void runTask() {
      try {
        DiameterTimerTaskData data = (DiameterTimerTaskData) getData();
        BaseSession bSession = sessionDataSource.getSession(data.getSessionId());
        if (bSession == null) {
          // FIXME: error ?
          logger.error("Base Session is null for sessionId: {}", data.getSessionId());
          return;
        }
        else {
          try {
            if (!bSession.isAppSession()) {
              BaseSessionImpl impl = (BaseSessionImpl) bSession;
              impl.onTimer(data.getTimerName());
            }
            else {
              AppSessionImpl impl = (AppSessionImpl) bSession;
              impl.onTimer(data.getTimerName());
            }
          }
          catch (Exception e) {
            logger.error("Caught exception from session object!", e);
          }
        }
      }
      catch (Exception e) {
        logger.error("Failure executing timer task", e);
      }
    }
  }

}
