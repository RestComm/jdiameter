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

package org.jdiameter.common.impl.timer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.jdiameter.api.BaseSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.BaseSessionImpl;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local implementation of timer facility for {@link ITimerFacility}
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LocalTimerFacilityImpl implements ITimerFacility {

  private static final Logger logger = LoggerFactory.getLogger(LocalTimerFacilityImpl.class);

  private ScheduledThreadPoolExecutor executor;
  private ISessionDatasource sessionDataSource;

  // TimerTaskHandle pooling to minimize impact on Eden space and avoid too
  // much GC, consequently not loosing time during GC
  private final GenericObjectPool pool = new GenericObjectPool(new TimerTaskHandleFactory(), 100000, GenericObjectPool.WHEN_EXHAUSTED_GROW, 10, 20000);

  public LocalTimerFacilityImpl(IContainer container) {
    super();
    this.executor = (ScheduledThreadPoolExecutor) container.getConcurrentFactory().
        getScheduledExecutorService(IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
    this.sessionDataSource = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
   */
  @Override
  public void cancel(Serializable f) {
    if (f != null && f instanceof TimerTaskHandle) {
      TimerTaskHandle timerTaskHandle = (TimerTaskHandle) f;
      if (timerTaskHandle.future != null) {
        logger.debug("Cancelling timer with id [{}] and delay [{}]", timerTaskHandle.id, timerTaskHandle.future.getDelay(TimeUnit.MILLISECONDS));
        if (executor.remove((Runnable) timerTaskHandle.future)) {
          timerTaskHandle.future.cancel(false);
          returnTimerTaskHandle(timerTaskHandle);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.timer.ITimerFacility#schedule(java.lang.String, java.lang.String, long)
   */
  @Override
  public Serializable schedule(String sessionId, String timerName, long milliseconds) throws IllegalArgumentException {
    String id = sessionId + "/" + timerName;
    logger.debug("Scheduling timer with id [{}]", id);
    TimerTaskHandle ir = borrowTimerTaskHandle();
    ir.id = id;
    ir.sessionId = sessionId;
    ir.timerName = timerName;
    ir.future = this.executor.schedule(ir, milliseconds, TimeUnit.MILLISECONDS);
    return ir;
  }

  protected void returnTimerTaskHandle(TimerTaskHandle timerTaskHandle) {
    try {
      pool.returnObject(timerTaskHandle);
    }
    catch (Exception e) {
      logger.warn(e.getMessage());
    }
  }

  protected TimerTaskHandle borrowTimerTaskHandle() {
    try {
      TimerTaskHandle timerTaskHandle = (TimerTaskHandle) pool.borrowObject();
      return timerTaskHandle;
    }
    catch (Exception e) {
      logger.error("", e);
    }
    return null;
  }

  class TimerTaskHandleFactory extends BasePoolableObjectFactory {
    @Override
    public Object makeObject() throws Exception {
      return new TimerTaskHandle();
    }

    @Override
    public void passivateObject(Object obj) throws Exception {
      TimerTaskHandle timerTaskHandle = (TimerTaskHandle) obj;
      timerTaskHandle.id = null;
      timerTaskHandle.sessionId = null;
      timerTaskHandle.timerName = null;
      timerTaskHandle.future = null;
    }
  }

  private final class TimerTaskHandle implements Runnable, Externalizable {
    // its not really serializable;
    private String sessionId;
    private String timerName;
    private String id; //for debug, easier to check what's going on and what that timer does.
    private transient ScheduledFuture<?> future;

    @Override
    public void run() {
      try {
        BaseSession bSession = sessionDataSource.getSession(sessionId);
        if (bSession == null) {
          // FIXME: error ?
          logger.error("Base Session is null for sessionId: {}", sessionId);
          return;
        }
        else {
          try {
            if (!bSession.isAppSession()) {
              BaseSessionImpl impl = (BaseSessionImpl) bSession;
              impl.onTimer(timerName);
            }
            else {
              AppSessionImpl impl = (AppSessionImpl) bSession;
              impl.onTimer(timerName);
            }
          }
          catch (Exception e) {
            logger.error("Caught exception from session object!", e);
          }
        }
      }
      catch (Exception e) {
        logger.error("Failure executing timer task witb id: " + id, e);
      }
      finally {
        returnTimerTaskHandle(this);
      }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      throw new IOException("Failed to serialize local timer!");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      throw new IOException("Failed to deserialize local timer!");
    }
  }

}
